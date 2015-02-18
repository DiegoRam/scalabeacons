package com.teracode.beacons

import akka.io.IO
import com.teracode.beacons.core._
import spray.can.Http

object WebApp extends BootedCore with SettingsCore with ElasticSearchCore with ApiCore with App {
  IO(Http).tell(Http.Bind(serviceActor, interface = settings.http.host, port = settings.http.port), ioListener)
}
