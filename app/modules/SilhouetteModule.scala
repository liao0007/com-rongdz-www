package modules

import auth.authorizations.{Has, HasImpl}
import auth.environments.JWTEnv
import auth.services.{PasswordAuthInfoService, UserIdentityService}
import com.google.inject.{AbstractModule, Provides, TypeLiteral}
import com.mohiva.play.silhouette.api.crypto.{AuthenticatorEncoder, Base64AuthenticatorEncoder}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.util._
import com.mohiva.play.silhouette.api.{Environment, EventBus, Silhouette, SilhouetteProvider}
import com.mohiva.play.silhouette.impl.authenticators.{JWTAuthenticator, JWTAuthenticatorService, JWTAuthenticatorSettings}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.impl.util.SecureRandomIDGenerator
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import play.api.Configuration
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration

class SilhouetteModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[Clock]).toInstance(Clock())
    bind(classOf[EventBus]).toInstance(EventBus())

    bind(classOf[AuthenticatorEncoder]).to(classOf[Base64AuthenticatorEncoder]).asEagerSingleton()
    bind(classOf[IDGenerator]).to(classOf[SecureRandomIDGenerator]).asEagerSingleton()
    //    bind(classOf[OAuth2StateProvider]).to(classOf[DummyStateProvider]).asEagerSingleton()

    bind(new TypeLiteral[DelegableAuthInfoDAO[PasswordInfo]] {}).to(new TypeLiteral[PasswordAuthInfoService] {}).asEagerSingleton()
    bind(classOf[PasswordHasher]).to(classOf[BCryptPasswordHasher]).asEagerSingleton()
    bind(new TypeLiteral[Silhouette[JWTEnv]] {}).to(new TypeLiteral[SilhouetteProvider[JWTEnv]] {}).asEagerSingleton()

    bind(classOf[com.mohiva.play.silhouette.api.actions.SecuredErrorHandler]).to(classOf[auth.errorHandlers.SecuredErrorHandler]).asEagerSingleton()
    bind(classOf[com.mohiva.play.silhouette.api.actions.UnsecuredErrorHandler]).to(classOf[auth.errorHandlers.UnsecuredErrorHandler]).asEagerSingleton()

    bind(classOf[Has]).to(classOf[HasImpl]).asEagerSingleton()
  }

  @Provides
  def provideEnvironment(
                          identityService: UserIdentityService,
                          authenticatorService: AuthenticatorService[JWTAuthenticator],
                          eventBus: EventBus
                        )(implicit ec: ExecutionContext): Environment[JWTEnv] = Environment[JWTEnv](identityService, authenticatorService, Seq(), eventBus)

  @Provides
  def provideAuthenticatorService(
                                   configuration: Configuration,
                                   idGenerator: IDGenerator,
                                   base64AuthenticatorEncoder: AuthenticatorEncoder,
                                   clock: Clock
                                 )(implicit ec: ExecutionContext): AuthenticatorService[JWTAuthenticator] = {
    val cfg = configuration.underlying
    val sharedSecret = cfg.getString("silhouette.authenticator.jwt.sharedSecret")
    val issuer = cfg.getString("silhouette.authenticator.jwt.issuerClaim")
    val expiry = cfg.getDuration("silhouette.authenticator.jwt.authenticatorExpiry")
    val fieldName = cfg.getString("silhouette.authenticator.jwt.fieldName")

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
                                )(implicit ec: ExecutionContext): CredentialsProvider = new CredentialsProvider(authInfoRepository, PasswordHasherRegistry(passwordHasher))

  @Provides
  def provideAuthInfoRepository(passwordInfoDao: DelegableAuthInfoDAO[PasswordInfo])(implicit ec: ExecutionContext): AuthInfoRepository =
    new DelegableAuthInfoRepository(passwordInfoDao)

  @Provides
  def provideBCryptPasswordHasher: BCryptPasswordHasher = new BCryptPasswordHasher

  @Provides
  def provideSecureRandomIDGenerator(implicit ec: ExecutionContext): SecureRandomIDGenerator = new SecureRandomIDGenerator

  //  social auth
  @Provides
  def provideHTTPLayer(client: WSClient)(implicit ec: ExecutionContext): HTTPLayer = new PlayHTTPLayer(client)

}
