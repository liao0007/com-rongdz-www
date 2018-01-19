package services

import javax.inject.Inject

import com.typesafe.config.Config
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl
import play.api.Configuration

/**
  * Created by liangliao on 18/4/16.
  */
class WxMpService @Inject()(val configuration: Configuration) extends WxMpServiceImpl {

  val wxConfig: Config = configuration.underlying.getConfig("wechat")

  private val config = new WxMpInMemoryConfigStorage {
    appId = configuration.underlying.getString("silhouette.wechat.clientID")
    secret = configuration.underlying.getString("silhouette.wechat.clientSecret")

    token = wxConfig.getString("token")
    aesKey = wxConfig.getString("aesKey")

    partnerId = wxConfig.getString("mchId")
    partnerKey = wxConfig.getString("mchKey")
  }

  setWxMpConfigStorage(config)
}