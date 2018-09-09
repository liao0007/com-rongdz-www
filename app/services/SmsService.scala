package services

import java.net.URLEncoder

import javax.inject.Inject
import com.typesafe.config.Config
import daos.default.core.Sms.{SmsDirection, SmsState}
import models.core.Sms
import play.api.Configuration
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source

/**
  * Created by liangliao on 19/4/16.
  */
class SmsService @Inject()(ws: WSClient, configuration: Configuration) {

  val smsConfig: Config = configuration.underlying.getConfig("sms")

  val userId: String   = smsConfig.getString("userId")
  val password: String = smsConfig.getString("password")
  val gateway: String  = smsConfig.getString("gateway")
  val stamp: String    = smsConfig.getString("stamp")

  def send(callNumber: String, content: String): Future[Boolean] = {
    val sms = Sms(callNumber = callNumber, content = content, direction = SmsDirection.Down.toString)
    sms.save

    val queryString = gateway + "?" + Seq(
        s"un=$userId",
        "pw=" + password,
        "msg=" + URLEncoder.encode(content + stamp, "UTF-8"),
        s"phone=$callNumber",
        s"rd=1"
      ).mkString("&")

    Future {
      Source.fromURL(queryString).mkString
    } map { resultString =>
      val result = resultString.split("\n").toSeq
      val state  = result.head.split(",").toSeq

      sms.state = Some(state.last)
      if (sms.state.contains(SmsState.Code0.toString)) {
        sms.mid = Some(result.last)
      }
      sms.save
    }
  }

}
