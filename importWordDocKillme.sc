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
import scala.collection.JavaConverters._

import org.apache.poi.xwpf.usermodel._

val tfPath = Paths.get("/Users/sjafri/Downloads/Question format.docx")

val tfInputStream = Files.newInputStream(tfPath)
val tfDoc = new XWPFDocument(tfInputStream)

val tfText = tfDoc.getParagraphs.asScala.map(_.getText).mkString("\n")
val images = tfDoc.getAllPictures.asScala.map(p => {
  val bytes: Array[Byte] = p.getData
  //Match this against constants in org.apache.poi.xwpf.usermodel.Document
  val pictureType: Int = p.getPictureType
})
//upload images now or something
println(tfText)
println(s"Found ${images.size} picture 📸")


val tables = tfDoc.getTables
tables.forEach(table => {
  val rows = table.getRows
})


//tfInputStream.close()
//tfDoc.close()