name := "spark-xls"

version := "0.1"

organization := "com.skapane"

scalaVersion := "2.11.8"

//crossScalaVersions := Seq("2.10.5", "2.11.8")

EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource
EclipseKeys.projectFlavor := EclipseProjectFlavor.Scala
EclipseKeys.withSource := true

spName := "skapane/spark-xls" 

sparkVersion := "2.0.0"

sparkComponents ++= Seq("core", "sql")

publishMavenStyle := true

spAppendScalaVersion := false

spIncludeMaven := true


libraryDependencies ++= {
    val poiVersion = "3.14"
    Seq(
        "org.apache.poi" % "poi" % poiVersion,
        "org.apache.poi" % "poi-ooxml" % poiVersion
    )
}



libraryDependencies += "junit" % "junit" % "4.12" % "test"

libraryDependencies ++= {
    val sparkVersion = "2.0.0"
    Seq(
        "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
        "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
        "org.apache.spark" %% "spark-mllib" % sparkVersion % "provided"
    )
}




