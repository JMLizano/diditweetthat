package diditweetthat

import java.time.Instant

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import com.danielasfregola.twitter4s.TwitterRestClient
import com.danielasfregola.twitter4s.entities.{RatedData, Tweet}

import scala.util.{Failure, Success, Try}


object Twitter {
  import scala.concurrent.duration._

  case class rawTweet(id: Long, id_str: String, text: String, created_at: Instant)

  val client = TwitterRestClient()

  // TODO: Consider implementing as an iterator
  def TimelineStream(user: String, max_id: Future[Option[Long]] = Future.successful { Some(Long.MaxValue - 1) }): LazyList[Future[Seq[rawTweet]]] = {
    val current_max_id = Await.result(max_id, Duration.Inf)
    current_max_id match {
      case Some(0) => LazyList.empty // No more tweets to retrieve
      case _ => {
        val TweetChunk = getTimeline(user, current_max_id)
        val restOfTweets = TimelineStream(
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

  def getAllTweetsInTimeline(user: String) : Future[Seq[rawTweet]] = {
    def getTimelineRecursive(user: String, max_id: Option[Long]): Future[Seq[rawTweet]]  = {
      getTimeline(user, max_id).map { tweets=>
        println(s"Got ${tweets.length} tweets")
        if (tweets.nonEmpty) {
          // There are still more tweets on the user timeline
          val current_max_id = Some(tweets.minBy(t => t.id).id - 1)
          try {
            val restOfTweets = Await.result(getTimelineRecursive(user, current_max_id), Duration.Inf)
            tweets ++ restOfTweets
          } catch {
            case e: java.util.concurrent.TimeoutException =>  {
              println(s"Timeout at $max_id")
              tweets
            }
          }
        } else {
          tweets
        }
      }
    }
    getTimelineRecursive(user, None)
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
