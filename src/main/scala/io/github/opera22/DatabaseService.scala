package io.github.opera22

import doobie._
import doobie.implicits._
import cats.effect.IO
import scala.concurrent.ExecutionContext
import java.sql._

import cats.effect.unsafe.implicits.global

object DatabaseService extends App {

  val xa = Transactor.fromDriverManager[IO](
    "com.mysql.jdbc.Driver",
//    "jdbc:mysql://aws.connect.psdb.cloud/testdb?verifyServerCertificate=true&useSSL=true&requireSSL=true&trustCertificateKeyStoreUrl=file:/Users/drewburns/Downloads/cacert.pem&trustStorePassword=changeit",
    "jdbc:mysql://aws.connect.psdb.cloud/testdb?sslMode=VERIFY_IDENTITY",
//    "jdbc:mysql://aws.connect.psdb.cloud/testdb?verifyServerCertificate=true&useSSL=true&requireSSL=true",
    "",
    "",

  )

  case class User(name: String, age: Int)

  def find(n: String): ConnectionIO[Option[User]] =
    sql"select name, age from users where name = $n".query[User].option

  val user = find("John").transact(xa).unsafeRunSync()
  println(user)
}
