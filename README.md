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