package auth.modules

import auth.filters.{CookieAuthFilter, CookieSettings}
import com.google.inject.AbstractModule

class UtilityModule extends AbstractModule {
  def configure(): Unit = {
    bind(classOf[CookieAuthFilter]).asEagerSingleton()
    bind(classOf[CookieSettings]).asEagerSingleton()
  }
}
