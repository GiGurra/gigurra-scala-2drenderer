lazy val gigurra_util_project = RootProject(file("ext/gigurra-scala-util"))
 
lazy val root = (project in file(".")).
  settings(
    name := "gigurra-scala-2drenderer",
    organization := "se.gigurra",
    version := "SNAPSHOT",
    scalaVersion := "2.11.5",

    parallelExecution in Test := false,
    EclipseKeys.withSource := true,
    EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource,

    libraryDependencies ++= Seq(
      "com.novocode" % "junit-interface" % "0.11" % "test",
      "org.slf4j" % "slf4j-simple" % "1.7.10",
      "com.nativelibs4java" %% "scalaxy-streams" % "0.3.4" % "provided",
      "org.jogamp.gluegen" % "gluegen-rt-main" % "2.2.4",
      "org.jogamp.jogl" % "jogl-all-main" % "2.2.4"
    )
  )
  .dependsOn(gigurra_util_project)


/////////////////////////////////////////////
//////// JOGAMP JARS temporary solution

/**
 * Use the stuff below in your application
 *

    val jogl_merge_strategy = new sbtassembly.MergeStrategy {
        val name = "jogl_rename"
        def apply(tempDir: File, path: String, files: Seq[File]) = 
            Right(files flatMap { file =>
                val (jar, _, _, isJar) = sbtassembly.AssemblyUtils.sourceOfFileForMerge(tempDir, file)
                if (isJar) Seq(file -> s"natives/${jar.getPath.split("-natives-")(1).split(".jar")(0)}/$path")
                else Seq(file -> path)
            })
    }

    assemblyMergeStrategy in assembly := {
      case x if (x.endsWith(".so") || x.endsWith(".dll") || x.endsWith(".jnilib")) =>
        jogl_merge_strategy
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    }

 */

