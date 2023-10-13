package com.vdumitrescu

import zio.http.*

object Security:
  final case class User(username: String)

  def authorized: HandlerAspect[Any, User] =
    HandlerAspect.customAuthProviding[User](
      _.headers.get(Header.Authorization) flatMap {
        case Header.Authorization.Basic(username, password) if username.reverse == password => Some(User(username))
        case _ => None
      }
    )