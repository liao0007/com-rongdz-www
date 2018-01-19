package services

import javax.inject.Inject

import play.api.Configuration
import com.alipay.api._

/**
  * Created by liangliao on 18/4/16.
  */
class AlipayService @Inject()(val configuration: Configuration)
    extends DefaultAlipayClient(configuration.underlying.getString("alipay.gateway"),
                                configuration.underlying.getString("alipay.appId"),
                                configuration.underlying.getString("alipay.privateKey"),
                                AlipayConstants.FORMAT_JSON,
                                AlipayConstants.CHARSET_UTF8,
                                configuration.underlying.getString("alipay.alipayPublicKey"),
                                AlipayConstants.SIGN_TYPE_RSA2)
