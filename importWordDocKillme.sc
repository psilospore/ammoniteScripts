import $ivy.`com.nrinaudo::kantan.csv:0.5.0`
import $ivy.`org.apache.poi:poi-ooxml:4.0.1`
import $ivy.`org.scalaz::scalaz-core:7.2.7`
import $ivy.`com.google.guava:guava:18.0`
import $ivy.`com.jsuereth::scala-arm:2.0`

import scalaz._
import Scalaz._
import resource._

import java.nio.file.{Paths, Files}
import org.apache.poi.xwpf.extractor._
import org.apache.poi.xwpf.usermodel.XWPFDocument
import scala.collection.JavaConverters._

import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.Document

val tfPath = Paths.get("/Users/sjafri/Downloads/True_False_Without_Rejoinder_Template.docx")

//TODO couldn't get scala-arm to work for some reason use managed if I can
val tfInputStream = Files.newInputStream(tfPath)
val tfDoc = new XWPFDocument(tfInputStream)

val tfText = tfDoc.getParagraphs.asScala.map(_.getText).mkString("\n")
val images = tfDoc.getAllPictures.asScala.map(p => {
  val bytes: Array[Byte] = p.getData
  val pictureType: Int = p.getPictureType //You can match this against statics in org.apache.poi.xwpf.usermodel.Document
})
//upload images now or something
println(tfText)
println(s"Found ${images.size} picture 📸")


tfInputStream.close()
tfDoc.close()