package com.resilient.providers

import com.typesafe.config.Config

trait ConfigProvider {
  val config: Config
}