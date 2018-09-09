package modules

import auth.filters.{CookieAuthFilter, CookieSettings}
import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment}

class UtilityModule extends Module {

  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = Seq(
    bind[CookieAuthFilter].toSelf.eagerly(),
    bind[CookieSettings].toSelf.eagerly()
  )
}
