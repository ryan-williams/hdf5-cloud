spark
spark.version := "2.2.1"

dep(
  "org.lasersonlab.thredds" ^ "cdm" ^ "5.0.0",
  "com.novocode" ^ "junit-interface" ^ "0.11" tests,
  "org.lasersonlab" ^ "google-cloud-nio" ^ "0.55.2-alpha" snapshot,

  hammerlab.bytes % "1.2.0",
  hammerlab.channel % "1.5.0",
  hammerlab.cli.base % "1.0.1" snapshot,
  hammerlab.io % "5.1.1" snapshot,
  hammerlab.lib("types") % "1.3.0" snapshot,
  paths % "1.5.0",

  spark.mllib,
  spark.sql,

  "org.lasersonlab" ^ "s3fs" ^ "2.2.3"
)
