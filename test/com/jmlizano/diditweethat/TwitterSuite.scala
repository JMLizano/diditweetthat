package diditweethat

import akka.actor.ActorSystem
import akka.stream.scaladsl.Sink

import scala.concurrent.{Await, Future}
import org.scalatest.funsuite.{AnyFunSuite, AsyncFunSuite}
import diditweetthat.Twitter
import diditweetthat.Twitter.rawTweet


class TwitterSuite extends AsyncFunSuite {

  test("getTimeline should return some tweets for the user") {
    val timeline: Future[Seq[rawTweet]] = Twitter.getTimeline("twitterapi")
    timeline.map { tweets =>
      assert(tweets.nonEmpty)
    }
  }

  test("TimelineStream should return all tweets for the user") {
    implicit val system = ActorSystem()

    val futureDate = "2999-01-01T00:00:00Z"
    val timeline = Twitter.TimelineStream("twitterapi")
    val firstTweet = timeline
      .map(_.minBy(_.created_at))
      .fold(futureDate)((min_date, tweet) => {
        val tweet_date = tweet.created_at.toString
        if(tweet_date < min_date) tweet_date else min_date
      }).runWith(Sink.last)
    // This is not the first tweet for twitterapi, but seems like the standard API
    // does not go further back in time
    firstTweet.map { value => assert( value == "2009-05-08T18:54:09Z") }

  }

}
