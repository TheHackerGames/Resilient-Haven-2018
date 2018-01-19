package com.resilient

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.resilient.providers.ConfigProvider
import com.typesafe.config.{Config, ConfigFactory}

object Application extends App with Service with ConfigProvider {

  lazy implicit val system: ActorSystem = ActorSystem()
  lazy implicit val materializer: ActorMaterializer = ActorMaterializer()
  lazy val config: Config = ConfigFactory.load()

  Http().bindAndHandle(heroHaven, config.getString("http.interface"), config.getInt("http.port"))
}