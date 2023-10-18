# An issue with zio-http

Per dev example, I created a HandlerAspect for user authentication/authorization.

```scala 3
object Security:
  final case class User(username: String)

  def authorized: HandlerAspect[Any, User] =
    HandlerAspect.customAuthProviding[User](
      _.headers.get(Header.Authorization) flatMap {
        case Header.Authorization.Basic(username, password) if username.reverse == password => Some(User(username))
        case _ => None
      }
    )
```

In my application, I have 3 types of routes:
- public routes (no auth needed): these work as documented;
- secured routes without path parameters: these work like in the example provided;
- secured routes with path parameters: this I can't get working. The path parameter cannot be accessed.

```scala 3
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
```

### Update

Many thanks to Nabil, I now have the solution.

To read the path parameters, the middleware must be used before the handler.
Then, the handler get access to the path parameter, as well as the Context and the Request.

```scala 3
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
```

This is how the API can be used:

No authorization:
```shell
➜  ~ curl http://localhost:8080/hello/Joe ; echo
Hello Joe!
```

With authorization, no path parameter:
```shell
➜  ~ curl -u Joe:eoJ http://localhost:8080/hola ; echo
Sorry, Joe, English only!
```

With authorization and path parameter:
```shell
➜  ~ curl -u Joe:eoJ http://localhost:8080/hola/Jim ; echo
Hola Jim from Joe
```
