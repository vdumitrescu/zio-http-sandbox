package com.vdumitrescu

import zio.*
import zio.http.*
import Security.User

object SandboxApp:
  def apply(): HttpApp[Any] = Routes(
    Method.GET / "hello" / string("username")
      -> handler { (username: String, _: Request) =>
        Response.text(s"Hello $username!")
      },
    Method.GET / "hola"
      -> Security.authorized
      -> handler { (user: User, _: Request) =>
        Response.text(s"Sorry, ${user.username}, English only!")
      },
    Method.GET / "hola" / string("username")
      -> Security.authorized
      -> handler { (username: String, user: User, _: Request) =>
        Response.text(s"Hola $username from ${user.username}")
      },
  ).sandbox.toHttpApp
