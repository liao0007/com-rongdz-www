

/**
  * Created by liangliao on 12/12/16.
  */
package object controllers {

  val staticPrefix = "https://static.rongdz.com"
  object customRoutes {
    val UploadedAssets = new ReverseUploadedAssets(staticPrefix)
//    val Assets = new ReverseAssets(staticPrefix)
    val Assets = new ReverseAssets(RoutesPrefix.byNamePrefix())

    object javascript {
      val UploadedAssets = new ReverseUploadedAssets(staticPrefix)
//      val Assets = new ReverseAssets(staticPrefix)
      val Assets = new ReverseAssets(RoutesPrefix.byNamePrefix())
    }
  }
}
