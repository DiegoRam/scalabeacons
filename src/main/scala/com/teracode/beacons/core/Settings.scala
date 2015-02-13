package com.teracode.beacons.core

import com.typesafe.config.{Config, ConfigFactory}

case class HttpSettings(
  host: String,
  port: Int
)

class Settings(config: Config) {
  val http = HttpSettings(
    config.getString("http.host"),
    config.getInt("http.port")
  )
}

object Settings {
  def apply() = {
    new Settings(ConfigFactory.load())
  }

  def apply(config: Config) = {
    new Settings(config)
  }
}