import scala.io.Source
import scala.sys.process._

object CheckBuildBazel {
  def main(args: Array[String]): Unit = {
    val buildFiles = getModifiedBuildFiles()

    buildFiles.foreach { file =>
      val content = readFile(file)
      val depsChanged = checkDepsChanged(content)

      if (depsChanged && !(checkSpecsChanged(content) || checkFeatureToggleChanged(content))) {
        warnOnPullRequest(file)
      }
    }
  }

  def getModifiedBuildFiles(): List[String] = {
    val diffOutput = "git diff --name-only HEAD~1 HEAD".!!.trim
    diffOutput.split("\n").filter(_.endsWith("BUILD.bazel")).toList
  }

  def readFile(path: String): String = {
    Source.fromFile(path).getLines().mkString("\n")
  }

  def checkDepsChanged(content: String): Boolean = {
    val depsRegex = """deps\s*=\s*\[(.*?)\]""".r
    depsRegex.findFirstMatchIn(content).exists(_.toString.contains("+"))
  }

  def checkSpecsChanged(content: String): Boolean = {
    val specsRegex = """specs\s*=\s*\[(.*?)\]""".r
    specsRegex.findFirstMatchIn(content).exists(_.toString.contains("+"))
  }

  def checkFeatureToggleChanged(content: String): Boolean = {
    val featureToggleRegex = """feature_toggle\s*=\s*\[(.*?)\]""".r
    featureToggleRegex.findFirstMatchIn(content).exists(_.toString.contains("+"))
  }

  def warnOnPullRequest(file: String): Unit = {
    println(s"::warning file=$file::Changes detected in deps but not in specs or feature_toggle.")
  }
}
