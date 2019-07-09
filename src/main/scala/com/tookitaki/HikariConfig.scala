package com.tookitaki

import com.zaxxer.hikari.HikariConfig

object HikariConfig {
  def apply(jdbcUrl: String,
            username: String,
            password: String,
            poolSize: Int): HikariConfig = {
    val config = new HikariConfig()
    config.setJdbcUrl(jdbcUrl)
    config.setUsername(username)
    config.setPassword(password)
    config.setMaximumPoolSize(poolSize)

    config
  }

}
