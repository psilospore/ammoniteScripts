interp.configureCompiler(_.settings.YpartialUnification.value = true)

import $ivy.`org.typelevel::cats-core:1.0.1`

import ammonite.ops._
import scala.util.matching.Regex
import scala.util.Try
import ujson._

//TODO can use part of this

//Create a new out directory with relative paths in meta.json

val metaPathWithRef: Regex = "(q?ref:[0-9a-fA-F]+:)(.*)".r
val maybePathRegex: Regex = "(/.*)".r //TODO better approach?

//TODO could do something better. Coursier references won't work because it's located at ~/

def convertIfPath(relativeTo: Path): Value => Option[String] = {
  case Str(metaPathWithRef(ref, path)) => Some(s"$ref${Path(path).relativeTo(relativeTo)}")
  case Str(maybePathRegex(maybePath)) =>
    Try {
      Path(maybePath).relativeTo(relativeTo).toString()
    } toOption
  case _ => None
}
val outDir: Path = pwd / 'out
val newOutDir: Path = pwd / 'newOut

def op(taskName: String): Unit = {

  val taskDir: Path = outDir / taskName
  val newTaskDir: Path = newOutDir / taskName
  cp(taskDir, newTaskDir)

  def rewriteMetaJsons(): Unit = {
    val metaJsons =  if (ammonite.ops.exists(taskDir/"meta.json")) {
      List(taskDir)
    } else {
      ls ! taskDir toList
    }
    metaJsons.foreach(rewriteMetaJson)
  }

  //Rewrite metaJson so if there are any absolute paths then convert them to relative paths
  def rewriteMetaJson(baseDir: Path): Unit = {
    val metaDir = baseDir / "meta.json"
    val metaJson = ujson.read(metaDir toIO)

    metaJson.obj.get("value").foreach {
      case Obj(obj) =>
        obj.foreach({ case (k, v) =>
          convertIfPath(baseDir)(v).foreach(s => {println(s); obj.update(k, s)})
        })
      case Arr(arr) =>
        metaJson.obj.put("value",
          Arr(
            arr.map(v =>
              convertIfPath(baseDir)(v).map({ s => Str(s) }).getOrElse(v)
            )
          )
        )
      case Str(str) =>
        convertIfPath(baseDir)(str).foreach(s =>
          metaJson.obj.put("value", Str(s))
        )
      case _ => ()
    }
    println(s"Writing $metaJson")
    ammonite.ops.write.over(newOutDir / "meta.json", ujson.write(metaJson, 4)) //TODO actually change this later to new out
  }

  //  mkdir! cachedTaskDir
  rewriteMetaJsons()
}

rm(newOutDir)
mkdir(newOutDir)
op("foo")
op("bar")


/**
 *
 * Crap there's Path.relativeTo don't need this
 * Find common ancestor
 * abspath = /Users/sjafri/Developer/mill/scratch/out/bar/compile/dest/classes/HelloWorld.class //or without file
 * path = /Users/sjafri/Developer/mill/scratch/out/bar/compile/
 *
 * replace common ancestor to ./
 *
 * to ./dest/classes/HelloWorld.class
 *
 * abspath = /Users/sjafri/Developer/mill/scratch/bar/src/Main.scala
 * path = /Users/sjafri/Developer/mill/scratch/out/bar/compile/
 *
 * /Users/sjafri/Developer/mill/scratch replace common ancestor to ./
 * to ../../../bar/src/Main.scala
 *
 */
//def absToRelPath(abspath: Path, from: Path = pwd): RelPath = {
//  abspath.segments
//  from.segments
//}


