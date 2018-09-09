package services

import com.alipay.api._
import javax.inject.Inject
import play.api.Configuration

/**
  * Created by liangliao on 18/4/16.
  */
class AlipayService @Inject()(val configuration: Configuration)
  extends DefaultAlipayClient(configuration.get[String]("alipay.gateway"),
    configuration.get[String]("alipay.appId"),
    configuration.get[String]("alipay.privateKey"),
    AlipayConstants.FORMAT_JSON,
    AlipayConstants.CHARSET_UTF8,
    configuration.get[String]("alipay.alipayPublicKey"),
    AlipayConstants.SIGN_TYPE_RSA2)
