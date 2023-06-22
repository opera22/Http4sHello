package io.github.opera22

import doobie._
import doobie.implicits._
import cats.effect.IO
import cats.effect.unsafe.IORuntime
import org.http4s.dsl._

import scala.concurrent.ExecutionContext
import java.sql._
import cats.effect.unsafe.implicits.global

import java.util.UUID

object DatabaseService {

  val dbUser = sys.env.getOrElse("db_user", throw new Exception("Missing DB User!"))
  val dbPassword = sys.env.getOrElse("db_password", throw new Exception("Missing DB Password!"))
  val dbHost = sys.env.getOrElse("db_host", throw new Exception("Missing DB Host!"))
  val dbName = sys.env.getOrElse("db_name", throw new Exception("Missing DB Name!"))

  val xa = Transactor.fromDriverManager[IO](
    "com.mysql.cj.jdbc.Driver",
    s"jdbc:mysql://$dbHost/$dbName?sslMode=VERIFY_IDENTITY",
    dbUser,
    dbPassword
  )

  case class User(id: Int, name: String, age: Int)

//  def find(n: String): ConnectionIO[Option[User]] =
//    sql"select name, age from users where name = $n".query[User].option

  def findUserByName(name: String): Option[User] = {
    sql"select id, name, age from users where name = $name".query[User].option.transact(xa).unsafeRunSync()
  }

  def insertUser(name: String, age: Int): User = {
    val retrievedUser = for {
      id <- sql"insert into users (name, age) values ($name, $age)".update.withUniqueGeneratedKeys[Int]("id")
      user <- sql"select * from users where id = $id".query[User].unique
    } yield user
    retrievedUser.transact(xa).unsafeRunSync()
  }

//    for {
//      id  <- sql"insert into person (name, age) values ($name, $age)".update.withGeneratedKeys[Int]("id")
//      p  <- sql"select id, name, age from users where id = $id".query[User].unique
//    } yield p
//    sql"insert into users (name, age) values ($name, $age)".update.run
//  println(findUserByName("John"))
//  val user = find("John").transact(xa).unsafeRunSync()
//  println(findUserByName("John"))
}
