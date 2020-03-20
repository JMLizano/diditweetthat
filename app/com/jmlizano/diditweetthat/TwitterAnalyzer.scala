package com.jmlizano.diditweetthat


import java.time.Instant

import diditweetthat.Twitter.simpleTweet

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import play.api.libs.json.{Format, Json}
import edu.stanford.nlp.pipeline.CoreNLPProtos
import diditweetthat.{TweetSentiment, Twitter}


object TwitterAnalyzer {

  case class badTweet(id: String, text: String, created_at: Instant, sentiment: Int)
  case class badTweetCollection(tweets: Seq[badTweet])

  object Formatters {
    // Necessary to render the JSON response in the controller
    implicit val badTweetFormatter: Format[badTweet] = Json.format[badTweet]
    implicit val badTweetCollectionFormatter: Format[badTweetCollection] = Json.format[badTweetCollection]
  }

  def getBadTweets(user: String, max_id: Option[Long] = None): badTweetCollection = {
    val tweetsStream = Twitter.TimelineStream(user,  Future.successful(max_id))
    val filteredTweets = tweetsStream.map(tweetChunk => Await.result(tweetChunk, Duration.Inf))
      .fold(Seq.empty) { (tweetChunk: Seq[simpleTweet], nextTweetChunk: Seq[simpleTweet]) =>
        tweetChunk ++ nextTweetChunk
      }
      .map(tweet => badTweet(tweet.id_str, tweet.text, tweet.created_at, TweetSentiment.mainSentiment(tweet.text).getNumber))
      .take(10)
    badTweetCollection(filteredTweets)
  }

}
