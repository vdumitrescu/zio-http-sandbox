package com.vdumitrescu

import zio.*
import zio.http.*
import Security.User

object SandboxApp:
  def apply(): HttpApp[Any] = Routes(
    Method.GET / "hello" / string("username") -> handler { (username: String, _: Request) =>
      Response.text(s"Hello $username!")
    },
    Method.GET / "hola" -> handler { (user: User, _: Request) =>
      Response.text(s"Sorry, ${user.username}, English only!")
    } @@ Security.authorized,
    Method.GET / "hola" / string("username") -> handler { (user: User, _: Request) =>
      val username = ??? // TODO: how to obtain the value of the path parameter?
      Response.text(s"Hola $username from ${user.username}")
    } @@ Security.authorized
  ).sandbox.toHttpApp
