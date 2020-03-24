package diditweetthat

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import java.time.Instant

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.danielasfregola.twitter4s.TwitterRestClient
import com.danielasfregola.twitter4s.entities.{RatedData, Tweet}


object Twitter {
  import scala.concurrent.duration._

  case class rawTweet(id: Long, id_str: String, text: String, created_at: Instant)

  val client = TwitterRestClient()

  def TimelineStream(user: String, max_id: Option[Long] = None): Source[Seq[rawTweet], NotUsed] = {
    Source.unfoldAsync(max_id) { current_max_id =>
      val nextChunkFuture =  getTimeline(user, current_max_id)
      nextChunkFuture.map { chunk =>
        if (chunk.isEmpty) None
        else {
          // Subtract 1 to last id to avoid overlapping
          val next_max_id = Some(chunk.minBy(tweet => tweet.id).id - 1)
          Some(next_max_id, chunk)
        }
      }
    }
  }

  // TODO: Consider implementing as an iterator
  def TimelineStreamLazyList(
    user: String,
    max_id: Future[Option[Long]] = Future.successful { Some(Long.MaxValue - 1) }
  ): LazyList[Future[Seq[rawTweet]]] = {
    val current_max_id = Await.result(max_id, Duration.Inf)
   current_max_id match {
      case Some(0) => LazyList.empty // No more tweets to retrieve
      case current_max_id => {
        val TweetChunk = getTimeline(user, current_max_id)
        val restOfTweets = TimelineStreamLazyList(
          user,
          TweetChunk.map { tweets =>
            // Subtract 1 to last id to avoid overlapping
            if (tweets.nonEmpty) Some(tweets.minBy(tweet => tweet.id).id - 1)
            else Some(0)
          }
        )
        TweetChunk #:: restOfTweets
      }
    }
  }

  def getTimeline(user: String, max_id: Option[Long] = None): Future[Seq[rawTweet]] = {
    println(s"Querying for $max_id")
    client
      .userTimelineForUser(screen_name=user, max_id=max_id, exclude_replies=true, trim_user=true, include_rts=false)
      .map { ratedData: RatedData[Seq[Tweet]] =>
        ratedData.data.map(tweet => rawTweet(tweet.id, tweet.id_str, tweet.text, tweet.created_at))
      }
  }

  def deleteTweet(tweet_id: Long): Future[rawTweet] = {
    client
      .deleteTweet(id = tweet_id)
      .map { tweet =>
        rawTweet(tweet.id, tweet.id_str, tweet.text, tweet.created_at)
      }
  }

}
