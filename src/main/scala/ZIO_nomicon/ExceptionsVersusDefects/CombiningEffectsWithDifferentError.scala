package ZIO_nomicon.ExceptionsVersusDefects

import zio.{ IO, ZIO }

object CombiningEffectsWithDifferentError {
  final case class ApiError(message: String) extends Exception(message)
  final case class DbError(message: String)  extends Exception(message)

  lazy val callApi: ZIO[Any, ApiError, String] /*IO[ApiError, String]*/ = ???
  lazy val queryDb: ZIO[Any, DbError, Int]                              = ???

  lazy val combine: ZIO[Any, Exception, (String, Int)] = callApi.zip(queryDb)

  final case class InsufficientPermission(
    user: String,
    operation: String
  )

  final case class FileIsLocked(file: String)

  def shareDocument(doc: String): IO[InsufficientPermission, Unit] = ???

  def moveDocument(
    doc: String,
    folder: String
  ): ZIO[Any, FileIsLocked, Unit] = ???

  lazy val resultAnyError: ZIO[Any, Any, Unit] = shareDocument("12341").zip(moveDocument("s324", "folder"))

  type DocumentError = Either[InsufficientPermission, FileIsLocked]

  lazy val result: IO[DocumentError, Unit] =
    shareDocument("12341")
      .mapError(Left(_))
      .zip(moveDocument("s324", "folder").mapError(Right(_)))
      .unit

}
