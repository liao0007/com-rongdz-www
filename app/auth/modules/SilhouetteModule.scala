package auth.modules

import auth.providers.WechatProvider
import auth.services.authorizations.{Has, HasImpl}
import auth.services.{PasswordAuthInfoService, UserIdentityService}
import auth.{CustomSecuredErrorHandler, CustomUnsecuredErrorHandler, JWTEnv}
import com.google.inject.name.Names
import com.google.inject.{AbstractModule, Provides, TypeLiteral}
import com.mohiva.play.silhouette.api.actions.{SecuredErrorHandler, UnsecuredErrorHandler}
import com.mohiva.play.silhouette.api.crypto.{AuthenticatorEncoder, Base64AuthenticatorEncoder}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.util._
import com.mohiva.play.silhouette.api.{Environment, EventBus, Silhouette, SilhouetteProvider}
import com.mohiva.play.silhouette.impl.authenticators.{JWTAuthenticator, JWTAuthenticatorService, JWTAuthenticatorSettings}
import com.mohiva.play.silhouette.impl.providers.oauth2.state.DummyStateProvider
import com.mohiva.play.silhouette.impl.providers.{CredentialsProvider, OAuth2Settings, OAuth2StateProvider}
import com.mohiva.play.silhouette.impl.util.SecureRandomIDGenerator
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import play.api.Configuration
import play.api.libs.ws.WSClient
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import services.WxMpService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class SilhouetteModule extends AbstractModule {
  override def configure: Unit = {
    bind(classOf[Clock]).toInstance(Clock())
    bind(classOf[EventBus]).toInstance(EventBus())

    bind(classOf[AuthenticatorEncoder]).to(classOf[Base64AuthenticatorEncoder]).asEagerSingleton()
    bind(classOf[IDGenerator]).to(classOf[SecureRandomIDGenerator]).asEagerSingleton()
    bind(classOf[OAuth2StateProvider]).to(classOf[DummyStateProvider]).asEagerSingleton()

    bind(new TypeLiteral[DelegableAuthInfoDAO[PasswordInfo]] {}).to(classOf[PasswordAuthInfoService]).asEagerSingleton()
    bind(classOf[PasswordHasher]).to(classOf[BCryptPasswordHasher]).asEagerSingleton()
    bind(new TypeLiteral[Silhouette[JWTEnv]] {}).to(new TypeLiteral[SilhouetteProvider[JWTEnv]] {}).asEagerSingleton()

    bind(classOf[SecuredErrorHandler]).to(classOf[CustomSecuredErrorHandler]).asEagerSingleton()
    bind(classOf[UnsecuredErrorHandler]).to(classOf[CustomUnsecuredErrorHandler]).asEagerSingleton()

    bind(classOf[Has]).to(classOf[HasImpl]).asEagerSingleton()
  }

  @Provides
  def provideEnvironment(
      identityService: UserIdentityService,
      authenticatorService: AuthenticatorService[JWTAuthenticator],
      eventBus: EventBus
  ): Environment[JWTEnv] = Environment[JWTEnv](identityService, authenticatorService, Seq(), eventBus)

  @Provides
  def provideAuthenticatorService(
      configuration: Configuration,
      idGenerator: IDGenerator,
      base64AuthenticatorEncoder: AuthenticatorEncoder,
      clock: Clock
  ): AuthenticatorService[JWTAuthenticator] = {
    val cfg          = configuration.underlying
    val sharedSecret = cfg.getString("silhouette.authenticator.jwt.sharedSecret")
    val issuer       = cfg.getString("silhouette.authenticator.jwt.issuerClaim")
    val expiry       = cfg.getDuration("silhouette.authenticator.jwt.authenticatorExpiry")
    val fieldName    = cfg.getString("silhouette.authenticator.jwt.fieldName")

    // we do not encrypt subject, as we do not transmit sensitive data AND it'd have to be decryptable across services
    val jwtSettings = JWTAuthenticatorSettings(
      fieldName = fieldName,
      issuerClaim = issuer,
      authenticatorExpiry = Duration.fromNanos(expiry.toNanos),
      sharedSecret = sharedSecret
    )

    // Repository is set to `None`, meaning we utilize stateless JWT tokens - but we can't invalidate them
    // which can complicate logouts
    new JWTAuthenticatorService(settings = jwtSettings,
                                repository = None,
                                authenticatorEncoder = base64AuthenticatorEncoder,
                                idGenerator = idGenerator,
                                clock = clock)
  }

  @Provides
  def provideCredentialsProvider(
      authInfoRepository: AuthInfoRepository,
      passwordHasher: PasswordHasher
  ): CredentialsProvider = new CredentialsProvider(authInfoRepository, PasswordHasherRegistry(passwordHasher))

  @Provides
  def provideAuthInfoRepository(passwordInfoDao: DelegableAuthInfoDAO[PasswordInfo]): AuthInfoRepository =
    new DelegableAuthInfoRepository(passwordInfoDao)

  @Provides
  def provideBCryptPasswordHasher: BCryptPasswordHasher = new BCryptPasswordHasher

  @Provides
  def provideSecureRandomIDGenerator: SecureRandomIDGenerator = new SecureRandomIDGenerator

  //  social auth
  @Provides
  def provideHTTPLayer(client: WSClient): HTTPLayer = new PlayHTTPLayer(client)

  @Provides
  def provideWechatProvider(
      configuration: Configuration,
      httpLayer: HTTPLayer,
      stateProvider: OAuth2StateProvider,
      wxMpService: WxMpService
  ): WechatProvider = {
    val setting = configuration.underlying.as[OAuth2Settings]("silhouette.wechat")
    new WechatProvider(httpLayer, stateProvider, wxMpService, setting)
  }

}
