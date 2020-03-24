package com.jmlizano.diditweetthat.controllers

import com.jmlizano.diditweetthat.TwitterAnalyzer
import com.jmlizano.diditweetthat.TwitterAnalyzer.writers._
import com.jmlizano.diditweetthat.TwitterAnalyzer.{processedTweet, processedTweetCollection}

import scala.concurrent.ExecutionContext
import javax.inject._
import play.api.data.Form
import play.api.mvc.{MessagesRequest, _}
import play.api.Logger
import play.api.libs.json.Json


case class UserForm(user: String)

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(
    val controllerComponents: ControllerComponents,
    messagesAction: MessagesActionBuilder
  )(implicit ec: ExecutionContext)
  extends BaseController {

  private val logger = Logger(getClass)

  private val form: Form[UserForm] = {
    import play.api.data.Forms._

    Form(
      mapping(
        "user" -> nonEmptyText
      )(UserForm.apply)(UserForm.unapply)
    )
  }

  def index() = messagesAction { implicit request: MessagesRequest[AnyContent] =>
    Ok(com.jmlizano.diditweetthat.views.html.home(form))
  }

  def scan() = Action {
    Ok(com.jmlizano.diditweetthat.views.html.scan())
  }

  def doScan(user: String, test: Boolean = false) = messagesAction {

        implicit request: MessagesRequest[AnyContent] =>
          logger.trace("process: ")
          val max_id = test match {
            case false => None
            case true => Some(992357979549782015L)
          }
          val tweets = TwitterAnalyzer.getProcessedTweets(user, max_id).map { Json.toJson(_) }
          Ok.chunked(tweets)
  }

}
