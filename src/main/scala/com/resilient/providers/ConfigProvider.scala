package com.resilient.providers

import com.typesafe.config.{Config, ConfigFactory}

trait ConfigProvider {
  val config: Config
}

trait TypesafeConfig extends ConfigProvider {
  lazy val config: Config = ConfigFactory.load()
}