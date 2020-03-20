package diditweetthat

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object main extends  App {

  val tweetsStream = Twitter.TimelineStream(args(0))
  tweetsStream.map { tweetChunk =>
    val tweets = Await.result(tweetChunk, Duration.Inf)
    tweets.map(tweet => (tweet.text, TweetSentiment.mainSentiment(tweet.text)))
  }.foreach(println)

}
