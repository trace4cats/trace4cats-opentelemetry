import sbt._

object Dependencies {
  object Versions {
    val scala212 = "2.12.14"
    val scala213 = "2.13.6"

    val trace4cats = "0.12.0-RC1"

    val cats = "2.6.1"
    val catsEffect = "3.1.1"

    val catsTestkitScalatest = "2.1.5"
    val disciplineScalatest = "2.1.5"
    val discipline = "1.1.5"
    val scalaCheck = "1.15.4"
    val scalaCheckShapeless = "1.3.0"
    val scalaTest = "3.2.9"
  }

  lazy val catsLaws = "org.typelevel"             %% "cats-laws"              % Versions.cats
  lazy val catsEffectLaws = "org.typelevel"       %% "cats-effect-laws"       % Versions.catsEffect
  lazy val catsEffectTestkit = "org.typelevel"    %% "cats-effect-testkit"    % Versions.catsEffect
  lazy val catsTestkitScalatest = "org.typelevel" %% "cats-testkit-scalatest" % Versions.catsTestkitScalatest
  lazy val disciplineScalatest = "org.typelevel"  %% "discipline-scalatest"   % Versions.disciplineScalatest
  lazy val disciplineCore = "org.typelevel"       %% "discipline-core"        % Versions.discipline
  lazy val scalacheck = "org.scalacheck"          %% "scalacheck"             % Versions.scalaCheck
  lazy val scalacheckShapeless =
    "com.github.alexarchambault"       %% "scalacheck-shapeless_1.15" % Versions.scalaCheckShapeless
  lazy val scalaTest = "org.scalatest" %% "scalatest"                 % Versions.scalaTest

  lazy val test =
    Seq(
      catsLaws,
      catsEffectLaws,
      catsEffectTestkit,
      catsTestkitScalatest,
      disciplineScalatest,
      disciplineCore,
      scalacheck,
      scalacheckShapeless,
      scalaTest
    )
}
