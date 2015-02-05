package com.teracode.beacons.conf

import com.typesafe.config.ConfigFactory

object AppConfig {
  private val config = ConfigFactory.load()

  object HttpConfig {
    private val httpConfig = config.getConfig("http")
    lazy val host = httpConfig.getString("host")
    lazy val port = httpConfig.getInt("port")
  }
}