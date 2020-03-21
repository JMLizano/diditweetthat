package com.jmlizano.diditweetthat

import scala.util.matching.Regex
import java.time.Instant

import diditweetthat.Twitter.rawTweet

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import play.api.libs.json.{Format, Json}
import edu.stanford.nlp.pipeline.CoreNLPProtos
import diditweetthat.{TweetSentiment, Twitter}


trait hashtagRemover {
  val hashtagPattern: Regex = "#([^ ]*)".r

  def removeHashtags(text: String): String = {
    hashtagPattern.replaceAllIn(text, "")
  }
}

trait urlRemover {
  val urlPattern: Regex = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)".r

  def removeUrls(text: String): String = {
    urlPattern.replaceAllIn(text, "")
  }
}

object TwitterAnalyzer extends urlRemover with hashtagRemover {


  case class processedTweet(id: String, text: String, created_at: Instant, sentiment: Int, cleaned_text: String)
  case class processedTweetCollection(tweets: Seq[processedTweet])

  object Formatters {
    // Necessary to render the JSON response in the controller
    implicit val processedTweetFormatter: Format[processedTweet] = Json.format[processedTweet]
    implicit val processedTweetCollectionFormatter: Format[processedTweetCollection] = Json.format[processedTweetCollection]
  }

  def getprocessedTweets(user: String, max_id: Option[Long] = None): processedTweetCollection = {
    val tweetsStream = Twitter.TimelineStream(user,  Future.successful(max_id))
    val filteredTweets = tweetsStream
      .map(tweetChunk => Await.result(tweetChunk, Duration.Inf))
      .flatMap(tweetChunk => tweetChunk.map {
        case rawTweet(id, id_str, text, created_at) => {
          val cleanedText = removeHashtags(removeUrls(text))
          val sentiment = TweetSentiment.mainSentiment(cleanedText).getNumber
          processedTweet(id_str, text, created_at, sentiment, cleanedText)
        }
      })
    processedTweetCollection(filteredTweets)
  }

}
