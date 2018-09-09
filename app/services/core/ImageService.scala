package services.core

import com.github.aselab.activerecord.ActiveRecord.Relation1
import com.github.aselab.activerecord.dsl._
import com.google.inject.Inject
import models.ModelFilter
import models.core.{Image, ImageFilter}
import org.apache.commons.codec.digest.DigestUtils
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import play.api.Environment
import play.api.libs.Files.TemporaryFile
import services.CrudService

/**
  *  Created by liangliao on 7/28/16.
  */
class ImageService @Inject()(environment: Environment) extends CrudService[Image] {

  def delete(id: Long): Option[Boolean] = Image.find(id) map delete
  override def delete(image: Image): Boolean = {
    environment.getFile(image.url).delete()
    image.delete()
  }

  def upload(temporaryFile: TemporaryFile, contentType: String): Boolean =
    contentType.toLowerCase.split("/").last match {
      case extension if Seq("png", "jpg", "jpeg", "gif").contains(extension) =>
        val now       = LocalDateTime.now()
        val formatter = DateTimeFormat.forPattern("yyyy/MM/dd")

        val relativePath = "uploaded/" + formatter.print(now) + "/"
        val relativeUrl  = formatter.print(now) + "/"
        val fileName     = DigestUtils.md5Hex(temporaryFile.file.getName) + "." + extension

        //create dir
        scala.reflect.io.File(environment.getFile(relativePath)).createDirectory()
        //move file
        temporaryFile.moveTo(environment.getFile(relativePath + fileName))

        //create image record
        val image = Image(url = relativeUrl + fileName)
        image.save()
      case _ => throw new Exception("File Type not Supported")
    }

  override def processFilter(searchBase: Relation1[Image, Image], filter: ModelFilter[Image]): Relation1[Image, Image] = {
    val ImageFilter(idOpt, urlOpt) = filter

    searchBase.where(
      record =>
        record.id === idOpt.?
          and (record.url like urlOpt.map(value => s"%$value%").?))
  }
}
