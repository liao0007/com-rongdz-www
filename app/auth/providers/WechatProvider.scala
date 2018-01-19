/**
  * Original work: SecureSocial (https://github.com/jaliss/securesocial)
  * Copyright 2013 Jorge Aliss (jaliss at gmail dot com) - twitter: @jaliss
  *
  * Derivative work: Silhouette (https://github.com/mohiva/play-silhouette)
  * Modifications Copyright 2015 Mohiva Organisation (license at mohiva dot com)
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
package auth.providers

import java.net.URLEncoder.encode

import com.mohiva.play.silhouette.api.{AuthInfo, Logger, LoginInfo}
import com.mohiva.play.silhouette.api.util.{ExtractableRequest, HTTPLayer}
import com.mohiva.play.silhouette.impl.providers._
import auth.providers.WechatProvider._
import com.mohiva.play.silhouette.api.exceptions.ConfigurationException
import com.mohiva.play.silhouette.impl.exceptions.{AccessDeniedException, UnexpectedResponseException}
import com.mohiva.play.silhouette.impl.providers.OAuth2Provider.{AuthorizationError, AuthorizationURLUndefined}
import me.chanjar.weixin.mp.bean.result.{WxMpOAuth2AccessToken, WxMpUser}
import play.api.mvc.{RequestHeader, Result, Results}
import services.WxMpService

import scala.concurrent.Future

case class WechatOAuth2Info(wxMpOAuth2AccessToken: WxMpOAuth2AccessToken) extends AuthInfo
case class WechatProfile(loginInfo: LoginInfo,
                         openId: String,
                         nickname: String,
                         remark: Option[String] = None,
                         country: Option[String] = None,
                         province: Option[String] = None,
                         city: Option[String] = None,
                         gender: Option[String] = None,
                         avatarUrl: Option[String] = None,
                         unionId: Option[String] = None)
    extends SocialProfile

trait WechatProfileBuilder extends SocialProfileBuilder { self: WechatProvider =>
  type Content = WxMpUser
  type Profile = WechatProfile
  val urls = Map("api" -> settings.apiURL.getOrElse(API))

  protected def buildProfile(authInfo: WechatOAuth2Info): Future[WechatProfile] = {
    val wxMpUser = wxMpService.oauth2getUserInfo(authInfo.wxMpOAuth2AccessToken, "zh_CN")
    profileParser.parse(wxMpUser, authInfo)
  }

  val profileParser = new WechatProfileParser
}

class WechatProfileParser extends SocialProfileParser[WxMpUser, WechatProfile, WechatOAuth2Info] {
  override def parse(content: WxMpUser, authInfo: WechatOAuth2Info): Future[WechatProfile] = Future.successful {
    WechatProfile(
      LoginInfo(ID, content.getOpenId),
      content.getOpenId,
      content.getNickname,
      Option(content.getRemark),
      Option(content.getCountry),
      Option(content.getProvince),
      Option(content.getCity),
      Option(content.getSex),
      Option(content.getHeadImgUrl),
      Option(content.getUnionId)
    )
  }
}

class WechatProvider(
    protected val httpLayer: HTTPLayer,
    protected val stateProvider: OAuth2StateProvider,
    protected val wxMpService: WxMpService,
    val settings: OAuth2Settings
) extends SocialProvider
    with OAuth2Constants
    with WechatProfileBuilder
    with Logger {

  type Self     = WechatProvider
  type A        = WechatOAuth2Info
  type Settings = OAuth2Settings

  // override oauth2constant
  override val ClientID     = "appid"
  override val ClientSecret = "secret"

  val id = ID

  def withSettings(f: (OAuth2Settings) => OAuth2Settings) = new WechatProvider(httpLayer, stateProvider, wxMpService, f(settings))

  def authenticate[B]()(implicit request: ExtractableRequest[B]): Future[Either[Result, WechatOAuth2Info]] = {
    request.extractString(Error).map {
      case e @ AccessDenied => new AccessDeniedException(AuthorizationError.format(id, e))
      case e                => new UnexpectedResponseException(AuthorizationError.format(id, e))
    } match {
      case Some(throwable) => Future.failed(throwable)
      case None =>
        request.extractString(Code) match {
          // We're being redirected back from the authorization server with the access code
          case Some(code) =>
            stateProvider.validate.flatMap { state =>
              getAccessToken(code).map(oauth2Info => Right(oauth2Info))
            }
          // There's no code in the request, this is the first step in the OAuth flow
          case None =>
            stateProvider.build.map { state =>
              val serializedState = stateProvider.serialize(state)
              val stateParam      = if (serializedState.isEmpty) List() else List(State -> serializedState)
              val params = settings.scope.foldLeft(List((ClientID, settings.clientID),
                                                        (RedirectURI, resolveCallbackURL(settings.redirectURL)),
                                                        (ResponseType, Code)) ++ stateParam ++ settings.authorizationParams.toList) {
                case (p, s) => (Scope, s) :: p
              }
              val encodedParams = params.map { p =>
                encode(p._1, "UTF-8") + "=" + encode(p._2, "UTF-8")
              }
              val url = settings.authorizationURL.getOrElse {
                throw new ConfigurationException(AuthorizationURLUndefined.format(id))
              } + encodedParams.mkString("?", "&", "")
              val redirect = stateProvider.publish(Results.Redirect(url), state)
              logger.debug("[Silhouette][%s] Use authorization URL: %s".format(id, settings.authorizationURL))
              logger.debug("[Silhouette][%s] Redirecting to: %s".format(id, url))
              Left(redirect)
            }
        }
    }
  }

  protected def getAccessToken(code: String)(implicit request: RequestHeader): Future[WechatOAuth2Info] = Future.successful {
    val wxMpOAuth2AccessToken: WxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code)
    WechatOAuth2Info(wxMpOAuth2AccessToken)
  }

}

/**
  * The companion object.
  */
object WechatProvider {

  /**
    * The error messages.
    */
  val SpecifiedProfileError = "[Silhouette][%s] Error retrieving profile information. Error message: %s, code: %s"

  /**
    * The Wechat constants.
    */
  val ID  = "wechat"
  val API = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN"
}
