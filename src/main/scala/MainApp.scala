package com.vdumitrescu

import zio.*
import zio.http.*

object MainApp extends ZIOAppDefault:
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    Server
      .serve(SandboxApp())
      .provide(
        Server.live,
        ZLayer.succeed(Server.Config.default.binding("localhost", 8080))
      )