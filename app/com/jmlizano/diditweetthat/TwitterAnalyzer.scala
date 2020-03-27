package com.jmlizano.diditweetthat

import scala.util.matching.Regex
import java.time.Instant

import akka.NotUsed
import akka.stream.scaladsl.Source
import play.api.libs.json.{Json, Writes}
import diditweetthat.{TweetSentiment, Twitter}
import diditweetthat.Twitter.rawTweet


trait hashtagRemover {
  lazy val hashtagPattern: Regex = "#([^ ]*)".r

  def removeHashtags(text: String): String = {
    hashtagPattern.replaceAllIn(text, "")
  }
}

trait urlRemover {
  lazy val urlPattern: Regex = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)".r

  def removeUrls(text: String): String = {
    urlPattern.replaceAllIn(text, "")
  }
}

trait badWordsFilter {
  import scala.io.Source

  lazy val badWords: List[String] =  {
    val bufferedSource = Source.fromResource("bad-words.txt")
    val lines = bufferedSource.getLines.toList
    bufferedSource.close
    lines
  }

  def containsBadWords(text: String): Boolean =  badWords.exists(text.contains)

}

object TwitterAnalyzer extends urlRemover with hashtagRemover with badWordsFilter {

  case class processedTweet(id: String, text: String, created_at: Instant, sentiment: Int, cleaned_text: String)
  case class processedTweetCollection(tweets: Seq[processedTweet]) // Necessary since Seq has no apply function (required for JSON encoding)

  object writers {
    // Necessary to render the JSON response in the controller
    // Json.writes instead of Json.format
    implicit val processedTweetFormatter = Json.writes[processedTweet]
    implicit val processedTweetCollectionWrites: Writes[processedTweetCollection] = Json.writes[processedTweetCollection]
  }

  def processRawTweet(tweet: rawTweet): processedTweet = {
    val rawText = tweet.text
    val cleanedText = removeHashtags(removeUrls(rawText))
    val sentiment = TweetSentiment.mainSentiment(cleanedText).getNumber
    processedTweet(tweet.id_str, rawText, tweet.created_at, sentiment, cleanedText)
  }

  def getProcessedTweets(user: String, max_id: Option[Long] = None): Source[processedTweetCollection, NotUsed]= {
    val tweetsStream = Twitter.TimelineStream(user, max_id)
    tweetsStream.map { tweetChunk =>
      val badTweets =  tweetChunk
          .withFilter(tweet => containsBadWords(tweet.text))
          .map(processRawTweet)
      processedTweetCollection(badTweets)
    }
  }
}
