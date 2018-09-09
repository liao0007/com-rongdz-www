package modules

import com.github.binarywang.wxpay.config.WxPayConfig
import com.github.binarywang.wxpay.service.WxPayService
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl
import com.typesafe.config.Config
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl
import me.chanjar.weixin.mp.api.{WxMpInMemoryConfigStorage, WxMpService}
import me.chanjar.weixin.open.api.WxOpenService
import me.chanjar.weixin.open.api.impl.{WxOpenInMemoryConfigStorage, WxOpenServiceImpl}
import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment}

/**
  * Created by liangliao on 25/7/17.
  */

class WechatModule extends Module {

  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    implicit val configurationImpl: Configuration = configuration
    Seq(
      /* mp */
      bind[WxMpService].qualifiedWith("ub").toInstance(getWechatMpServiceImpl("keys.wechat.ub")),
      bind[WxMpService].qualifiedWith("kfc").toInstance(getWechatMpServiceImpl("keys.wechat.kfc")),

      /* pay */
      bind[WxPayService].qualifiedWith("ub").toInstance(getWechatPayServiceImpl("keys.wechat.ub")),

      /* open */
      bind[WxOpenService].qualifiedWith("ub").toInstance(getWechatOpenServiceImpl("keys.wechat.ub-open"))
    )
  }

  private def getWechatMpServiceImpl(configurationKeyPath: String)(implicit configuration: Configuration): WxMpService = {
    val wxConfig: Config = configuration.underlying.getConfig(configurationKeyPath)
    val wxMpServiceImpl = new WxMpServiceImpl()
    wxMpServiceImpl.setWxMpConfigStorage(new WxMpInMemoryConfigStorage {
      appId = wxConfig.getString("appId")
      secret = wxConfig.getString("appSecret")
      token = wxConfig.getString("token")
      aesKey = wxConfig.getString("aesKey")
    })
    wxMpServiceImpl
  }

  private def getWechatPayServiceImpl(configurationKeyPath: String)(implicit configuration: Configuration): WxPayService = {
    val wxConfig: Config = configuration.underlying.getConfig(configurationKeyPath)
    val wxPayConfig = new WxPayConfig()
    wxPayConfig.setAppId(wxConfig.getString("appId"))
    wxPayConfig.setMchId(wxConfig.getString("mcId"))
    wxPayConfig.setMchKey(wxConfig.getString("mcKey"))
    val wxPayServiceImpl = new WxPayServiceImpl
    wxPayServiceImpl.setConfig(wxPayConfig)
    wxPayServiceImpl
  }

  private def getWechatOpenServiceImpl(configurationKeyPath: String)(implicit configuration: Configuration): WxOpenService = {
    val wxConfig: Config = configuration.underlying.getConfig(configurationKeyPath)
    val wxOpenInMemoryConfigStorage = new WxOpenInMemoryConfigStorage
    wxOpenInMemoryConfigStorage.setComponentAppId(wxConfig.getString("appId"))
    wxOpenInMemoryConfigStorage.setComponentAppSecret(wxConfig.getString("appSecret"))
    wxOpenInMemoryConfigStorage.setComponentToken(wxConfig.getString("token"))
    wxOpenInMemoryConfigStorage.setComponentAesKey(wxConfig.getString("aesKey"))
    val wxOpenServiceImpl = new WxOpenServiceImpl
    wxOpenServiceImpl.setWxOpenConfigStorage(wxOpenInMemoryConfigStorage)
    wxOpenServiceImpl
  }

}
