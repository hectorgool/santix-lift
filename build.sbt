name := "SANTIX"

version := "0.0.4"

organization := "net.liftweb"

scalaVersion := "2.11.2"

resolvers ++= Seq("snapshots"     at "https://oss.sonatype.org/content/repositories/snapshots",
                  "releases"      at "https://oss.sonatype.org/content/repositories/releases"
                )

seq(webSettings :_*)

unmanagedResourceDirectories in Test <+= (baseDirectory) { _ / "src/main/webapp" }

scalacOptions ++= Seq("-deprecation", "-unchecked")

port in container.Configuration := 9000

libraryDependencies ++= {
  val liftVersion = "2.6"
  Seq(
    "net.liftweb"             %% "lift-webkit"             % liftVersion           % "compile",
    "net.liftmodules"         %% "lift-jquery-module_2.6"  % "2.8",
    "org.eclipse.jetty"        % "jetty-webapp"            % "8.1.7.v20120910"     % "container,test",
    "org.eclipse.jetty"        % "jetty-plus"              % "8.1.7.v20120910"     % "container,test", // For Jetty Config
    "org.eclipse.jetty.orbit"  % "javax.servlet"           % "3.0.0.v201112011016" % "container,test" artifacts Artifact("javax.servlet", "jar", "jar"),
    "ch.qos.logback"           % "logback-classic"         % "1.0.6",
    "org.specs2"              %% "specs2"                  % "2.3.12"              % "test",
    "net.liftmodules"         %% "mongoauth_2.6"           % "0.6-SNAPSHOT",
    "com.foursquare"          %% "rogue-field"             % "2.5.0" intransitive(),
    "com.foursquare"          %% "rogue-core"              % "2.5.1" intransitive(),
    "com.foursquare"          %% "rogue-lift"              % "2.5.1" intransitive(),
    "com.foursquare"          %% "rogue-index"             % "2.5.1" intransitive(),
    "net.liftweb"             %% "lift-mongodb-record"     % "2.6",
    "net.tanesha.recaptcha4j"  % "recaptcha4j"             % "0.0.7",
    "com.twitter"              % "finagle-core_2.11"       % "6.24.0",
    "com.twitter"              % "finagle-stream_2.11"     % "6.24.0",
    "com.twitter"              % "finagle-http_2.11"       % "6.24.0",
    "org.twitter4j"            % "twitter4j-core"          % "4.0.2",
    "de.weltraumschaf"         % "speakingurl"             % "1.0.0"
  )
}

