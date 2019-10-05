interp.configureCompiler(_.settings.YpartialUnification.value = true)

import $ivy.`org.typelevel::cats-core:1.0.1`

import ammonite.ops._
import scala.util.matching.Regex
import scala.util.Try
import ujson._

//TODO can use part of this

//Create a new out directory with relative paths in meta.json

val metaPathWithRef: Regex = "(q?ref:[0-9a-fA-F]+:)(.*)".r
val isPath: Regex = "/(.*)".r
def newPath(relativeTo: Path): PartialFunction[String, String] = {
  case metaPathWithRef(ref, path) => s"$ref${Path(path).relativeTo(relativeTo)}"
  case isPath(path) => Path(path).relativeTo(relativeTo).toString()
  case notpath => notpath
}

def op(taskName: String): Unit = {
  val outDir: Path = pwd / 'out
  val newOutDir: Path = pwd / 'newOut

  val taskDir: Path = outDir / taskName
  val newTaskDir: Path = newOutDir / taskName

  rm(newOutDir)
  mkdir(newOutDir)
  cp(taskDir, newTaskDir)

  def rewriteMetaJsons(): Unit = {
    val subTasksToCopy: LsSeq = ls ! taskDir //TODO In actual script this would be a subtasks or there is none (by presense of meta.json) or maybe there actually can be both?
    subTasksToCopy.toList.foreach(rewriteMetaJson)
  }

  //Rewrite metaJson so if there are any absolute paths then convert them to relative paths
  def rewriteMetaJson(dir: Path): Unit = {
    val metaJson: Value = ujson.read(dir / "meta.json" toIO)
    metaJson.obj
      .get("value")
      .foreach {
        case Obj(obj) =>
          obj.foreach({ case (k, v) =>
            obj.update(k, newPath(dir)(v.str))
          })
        case Arr(arr) =>
          arr.map(v =>
            newPath(dir)(v.str)
          )
        case Str(str) =>
          metaJson.update("value", Str(newPath(dir)(str)))
      }

    ujson.write(metaJson, 4)
  }

  //  mkdir! cachedTaskDir
  rewriteMetaJsons()
}

op("bar")
op("foo")


/**
 *
 * Crap there's Path.relativeTo
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
 * @param abspath
 * @param from directory the path should be absolute from. Defaults to cwd.
 */
//def absToRelPath(abspath: Path, from: Path = pwd): RelPath = {
//  abspath.segments
//  from.segments
//}


