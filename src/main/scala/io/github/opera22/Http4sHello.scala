package io.github.opera22


import cats._
import cats.effect._
import cats.implicits._
//import cats.syntax.functor._
//import cats.syntax.flatMap._
import org.http4s.circe._
import org.http4s._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.dsl._
import org.http4s.dsl.impl._
import org.http4s.headers._
import org.http4s.implicits._
import org.http4s.server._
import org.http4s.server.blaze.BlazeServerBuilder

object Http4sHello extends IOApp {

//  object UserQueryParamMatcher extends QueryParamDecoderMatcher[String]("")
//  DatabaseService.findUserById(1)

//  implicit val userDecoder = jsonOf[IO,DatabaseService.User]


  def helloRoutes[F[_] : Monad]: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    HttpRoutes.of[F] {
      case GET -> Root / "hello" => Ok(Map("hi" -> "hello world!").asJson)
    }
  }

  def userRoutes[F[_] : Concurrent]: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._
    implicit val userDecoder: EntityDecoder[F, DatabaseService.User] = jsonOf[F, DatabaseService.User]
    implicit val userEncoder: EntityEncoder[F, DatabaseService.User] = jsonEncoderOf[F, DatabaseService.User]

    HttpRoutes.of[F] {
      case GET -> Root / "users" / name => DatabaseService.findUserByName(name) match {
        case Some(user) => Ok(user.asJson)
        case _ => NotFound(s"No user with name $name found in the database.")
      }
      case req @ POST -> Root / "users" =>
        for {
          // Decode a User request
          user <- req.as[DatabaseService.User]
          // Insert into database
          _ = DatabaseService.insertUser(user.name, user.age)
          // Encode a hello response
          resp <- Ok("Inserted!".asJson)
        } yield resp

    }
  }

//  def allRoutes[F[_] : Monad]: HttpRoutes[F] =
//    helloRoutes[F] <+> userRoutes[F]
//
//  def allRoutesComplete[F[_] : Monad]: HttpApp[F] =
//    allRoutes[F].orNotFound

  override def run(args: List[String]): IO[ExitCode] = {

    val apis = Router(
      "/api" -> helloRoutes[IO],
      "/api/what" -> userRoutes[IO]
    ).orNotFound

    BlazeServerBuilder[IO](runtime.compute)
      .bindHttp(8080, "localhost")
      .withHttpApp(apis)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)

  }

}
