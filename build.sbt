name := "spark-tips"

version := "0.1"

val SPARK_VERSION = "2.0.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % SPARK_VERSION,
  "org.apache.spark" %% "spark-sql" % SPARK_VERSION,
  "org.apache.spark" %% "spark-mllib" % SPARK_VERSION,
  "com.holdenkarau" %% "spark-testing-base" % "2.0.0_0.4.4" % "test",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test" withSources()
)

parallelExecution in Test := false
