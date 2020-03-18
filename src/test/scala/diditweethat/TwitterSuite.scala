package diditweethat

import scala.concurrent.Future

import org.scalatest.funsuite.AsyncFunSuite

import diditweetthat.Twitter
import diditweetthat.Twitter.simpleTweet


class TwitterSuite extends AsyncFunSuite {

  test("getTimeline should return tweets for the user") {
    val timeline: Future[Seq[simpleTweet]] = Twitter.getTimeline("twitterapi")
    timeline.map { tweets =>
      println(tweets.minBy(tweet => tweet.created_at))
      assert(tweets.length > 0)
    }
  }

}
