package controllers.rest

import java.time.Duration

import auth._
import auth.services.{UserIdentityService, UserPermissionService}
import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, Controller, Result}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserController @Inject()(
    silhouette: Silhouette[JWTEnv],
    configuration: Configuration,
    userPermissionService: UserPermissionService,
    userIdentityService: UserIdentityService
) extends Controller
    with ResponseSupport {

  /**
    * Lists all users
    */
  //  def listAll: Action[AnyContent] = Action.async { implicit req =>
  //    //silhouette.SecuredAction.async { implicit req =>
  //    //silhouette.SecuredAction(permissions.require(AccessAdmin)).async { implicit req =>
  //    userService.list().map { res =>
  //      Ok(Json.toJson(res))
  //    }
  //  }
  //
  def get = silhouette.SecuredAction { implicit request =>
    Ok(Json.toJson(request.identity))
  }

  def update: Action[JsValue] = silhouette.SecuredAction.async(parse.json) { implicit request =>
    request.body
      .validate[UpdateUserRequest]
      .map { updateUser =>
        userIdentityService.retrieve(request.identity.id) flatMap {
          case Some(user) => userIdentityService.update(user) map (user => Ok(Json.toJson(user)))
          case None       => Future.successful(Unauthorized(Json.toJson(Bad("invalid.credentials"))))
        }
      }
      .recoverTotal(badRequestWithMessage)
  }

  //
  //  /**
  //   * Gets user by login info
  //   */
  //  def getByLoginInfo(providerID: String, providerKey: String) = Action.async {
  //    userService.retrieve(LoginInfo(providerID, providerKey)) map {
  //      case Some(user) => Ok(Json.toJson(user))
  //      case None => NotFound
  //    }
  //  }
  //
  //  /**
  //   * Registers users in auth.direct.service and issues token in backend
  //   */
  //  def signUpRequestRegistration: Action[JsValue] = Action.async(parse.json) { implicit request =>
  //    request.body
  //      .validate[SignUp]
  //      .map { signUp =>
  //        authService.createUserByCredentials(signUp, newAccountTokenValidFor.toHours) map {
  //          case Left(UserAlreadyExists) =>
  //            Conflict(Json.toJson(Bad("user.exists")))
  //          case Right(created) =>
  //            Created(Json.toJson(Json.obj("token" -> created.token)))
  //              .withHeaders(LOCATION → controllers.auth.api.auth.routes.UsersController
  //                .get(created.crmUserId)
  //                .url)
  //        }
  //      }
  //  }
}
