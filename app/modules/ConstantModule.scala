package modules

import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment, Mode}

/**
  * Created by liangliao on 25/7/17.
  */

class ConstantModule extends Module {

  final val UploadFolder = "uploaded/"

  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = Seq(
    bind[String].qualifiedWith("uploadFolder").toInstance(UploadFolder),
    environment.mode match {
      case Mode.Prod =>
        bind[String].qualifiedWith("uploadedResource").toInstance("https://static.youbohudong.com/" + UploadFolder)
      case _ =>
        bind[String].qualifiedWith("uploadedResource").toInstance("http://localhost:9000/" + UploadFolder)
    }
  )
}
