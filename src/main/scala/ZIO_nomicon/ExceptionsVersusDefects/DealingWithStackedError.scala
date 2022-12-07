package ZIO_nomicon.ExceptionsVersusDefects

import zio.{ IO, Scope, ZIO, ZIOAppArgs, ZIOAppDefault }

object DealingWithStackedError extends ZIOAppDefault {
//  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
//    // two case
//    val user = for {
//      user <- lookupProfiles2("userId2")
//    } yield user
//
//    user.map(Console.println)
//  }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
    // One case
    val user = for {
      user <- lookupProfiles("userId2")
    } yield user

    user.map(Console.println)
  }

  case class DatabaseError() extends Exception
  case class UserProfiles()

  def lookupProfiles(userId: String): IO[DatabaseError, Option[UserProfiles]] =
    userId match {
      case "userId"  => ZIO.succeed(Some(UserProfiles()))
      case "userId2" => ZIO.succeed(None)
      case _         => ZIO.fail(DatabaseError())
    }

  def lookupProfiles2(userId: String): IO[Option[DatabaseError], UserProfiles] =
    lookupProfiles(userId).foldZIO(
      error => ZIO.fail(Some(error)),
      success =>
        success match {
          case Some(value) => ZIO.succeed(value)
          case None        => ZIO.fail(None)
        }
    )

  def lookupProfiles3(userId: String): IO[Option[DatabaseError], UserProfiles] =
    lookupProfiles(userId).some

  // two case and first case is equal
}
