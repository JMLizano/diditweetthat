package diditweethat

import scala.concurrent.{Await, Future}
import org.scalatest.funsuite.{AnyFunSuite, AsyncFunSuite}
import diditweetthat.Twitter
import diditweetthat.Twitter.simpleTweet


class TwitterSuite extends AsyncFunSuite {

  test("getTimeline should return some tweets for the user") {
    val timeline: Future[Seq[simpleTweet]] = Twitter.getTimeline("twitterapi")
    timeline.map { tweets =>
      assert(tweets.nonEmpty)
    }
  }

  test("TimelineStream should return all tweets for the user") {
    import scala.concurrent.duration._

    val timeline: LazyList[Future[Seq[simpleTweet]]] = Twitter.TimelineStream("twitterapi")
    val firstTweet = timeline
      .map(tweetChunk => Await.result(tweetChunk, Duration.Inf))
      .fold(Seq.empty) { (tweetChunk: Seq[simpleTweet], nextTweetChunk: Seq[simpleTweet]) =>
        tweetChunk ++ nextTweetChunk
      }.minBy(tweet => tweet.created_at)
    // This is not the first tweet for twitterapi, but seems like the standard API
    // does not go further back in time
    assert(firstTweet.created_at.toString == "2009-05-08T18:54:09Z")
  }

}
