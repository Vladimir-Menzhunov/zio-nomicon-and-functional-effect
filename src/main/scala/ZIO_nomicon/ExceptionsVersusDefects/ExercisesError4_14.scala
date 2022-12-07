package ZIO_nomicon.ExceptionsVersusDefects

import zio.{ Cause, Console, IO, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault }

import java.io.IOException

object ExercisesError4_14 extends ZIOAppDefault {

  // 1 Ex
  def failWithMessage(string: String): UIO[Nothing] =
    ZIO.succeed(throw new Error(string))

  def fixFailWithMessage(string: String): UIO[Unit] =
    failWithMessage("message").catchAllDefect(_ => ZIO.succeed())

  // 2 Ex
  def recoverFromSomeDefects[R, E, A](zio: ZIO[R, E, A])(
    f: Throwable => Option[A]
  ): ZIO[R, E, A] = {
    val isSuitable: Throwable => Boolean = (tr: Throwable) => if (tr.getMessage == "bugMessage") true else false
    zio.foldCauseZIO(
      failure =>
        (for {
          defect <- failure.defects.find(isSuitable)
          a      <- f(defect)
        } yield a).fold(zio)(ZIO.succeed(_)),
      a => ZIO.succeed(a)
    )
  }

  // 3 Ex
  def logFailures[R, E, A](zio: ZIO[R, E, A]): ZIO[R, E, A] =
    zio.foldCauseZIO(
      //failure => Console.printLine(s"Logging error or defect: ${failure.prettyPrint}").orDie.flatMap(_ => zio),
      failure => Console.printLine(s"Logging error or defect: ${failure.prettyPrint}").orDie.flatMap(_ => zio),
      success => ZIO.succeed(success)
    )

  // 4 Ex
  def onAnyFailure[R, E, A](zio: ZIO[R, E, A], handler: ZIO[R, E, Any]): ZIO[R, E, A] =
    zio.foldCauseZIO(
      _ => handler *> zio,
      _ => zio
    )

  // 5 Ex
  def ioException[R, A](zio: ZIO[R, Throwable, A]): ZIO[R, java.io.IOException, A] =
    zio.refineOrDie {
      case ex: IOException => ex
    }

  // 6 Ex
  val parseNumber: ZIO[Any, Throwable, Int]             = ZIO.attempt("foo".toInt)
  val parseNumberRefine: IO[NumberFormatException, Int] = parseNumber.refineToOrDie[NumberFormatException]

  // 7 Ex
  def left[R, E, A, B](
    zio: ZIO[R, E, Either[A, B]]
  ): ZIO[R, Either[E, B], A] =
    zio.foldZIO(
      failure => ZIO.fail(Left(failure)),
      success =>
        success match {
          case Left(value)  => ZIO.succeed(value)
          case Right(value) => ZIO.fail(Right(value))
        }
    )

  def unleft[R, E, A, B](
    zio: ZIO[R, Either[E, B], A]
  ): ZIO[R, E, Either[A, B]] =
    zio.foldZIO(
      failure => failure.fold(ZIO.fail(_), value => ZIO.succeed(Right(value))),
      success => ZIO.succeed(Left(success))
    )

  // 8 Ex
  def right[R, E, A, B](
    zio: ZIO[R, E, Either[A, B]]
  ): ZIO[R, Either[E, A], B] =
    zio.foldZIO(
      failure => ZIO.fail(Left(failure)),
      success =>
        success match {
          case Left(value)  => ZIO.fail(Right(value))
          case Right(value) => ZIO.succeed(value)
        }
    )

  def unright[R, E, A, B](
    zio: ZIO[R, Either[E, A], B]
  ): ZIO[R, E, Either[A, B]] =
    zio.foldZIO(
      failure => failure.fold(ZIO.fail(_), value => ZIO.succeed(Left(value))),
      success => ZIO.succeed(Right(success))
    )

  // 9 Ex
  def catchAllCause[R, E1, E2, A](
    zio: ZIO[R, E1, A],
    handler: Cause[E1] => ZIO[R, E2, A]
  ): ZIO[R, E2, A] = zio.sandbox.catchAll(handler)

  // 10 Ex
  def catchAllCause2[R, E1, E2, A](
    zio: ZIO[R, E1, A],
    handler: Cause[E1] => ZIO[R, E2, A]
  ): ZIO[R, E2, A] =
    zio.foldCauseZIO(
      failure => handler(failure),
      success => ZIO.succeed(success)
    )

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    catchAllCause2(ZIO.fail(new IOException), (_: Cause[Any]) => ZIO.succeed(println("All Ok")))

}
