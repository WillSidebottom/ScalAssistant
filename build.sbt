lazy val commonSettings = Seq(
  version := "1.0",
  scalaVersion := "2.11.8"
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    name := "scalAssistant",
    resolvers += typesafe,
    scalacOptions ++= Seq(
      optionOne,
      optionTwo
    ),
    libraryDependencies ++= Seq(
      akka,
      scalatest,
      scalactic,
      json4sJackson,
      twitter
    )
  )

//RESOLVERS GO HERE
val typesafe = "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

//SCALA OPTIONS GO HERE
val optionOne = "-deprecation"
val optionTwo = "-feature"

//DEPENDENCIES GO HERE
val akka	         = "com.typesafe.akka"	 %% "akka-actor"		  % "2.4.11"
val scalatest		   = "org.scalatest"		   %% "scalatest"		    % "3.0.0"	% "test"
val scalactic		   = "org.scalactic"		   %% "scalactic"		    % "3.0.0"
val json4sJackson  = "org.json4s"			     %% "json4s-jackson"	% "3.4.1"
val twitter        = "org.twitter4j"       % "twitter4j-core"   % "4.0.5"