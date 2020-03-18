package diditweetthat

import java.time.Instant

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import com.danielasfregola.twitter4s.TwitterRestClient
import com.danielasfregola.twitter4s.entities.{RatedData, Tweet}


object Twitter {

  case class simpleTweet(id: Long, id_str: String, text: String, created_at: Instant)

  val client = TwitterRestClient()

  def getAllTweetsinTimeline(user: String) : Future[Seq[simpleTweet]] =
    getTimeline(user, max_id = Long.MaxValue)

  def getTimeline(user: String, max_id: Long): Future[Seq[simpleTweet]] = {
    client
      .userTimelineForUser(screen_name=user, max_id = Some(max_id), exclude_replies=true, count=3200, trim_user=true, include_rts=false)
      .map { ratedData: RatedData[Seq[Tweet]] =>
        ratedData.data.map(tweet => simpleTweet(tweet.id, tweet.id_str, tweet.text, tweet.created_at))
      }
  }

  def deleteTweet(tweet_id: Long): Future[simpleTweet] = {
    client
      .deleteTweet(id = tweet_id)
      .map { tweet =>
        simpleTweet(tweet.id, tweet.id_str, tweet.text, tweet.created_at)
      }
  }

}
