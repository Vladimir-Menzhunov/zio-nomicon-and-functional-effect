package ZIO_nomicon

import ZIO_nomicon.ExceptionsVersusDefects.Cause

import scala.annotation.tailrec
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success, Try }

final case class ZIO[-R, +E, +A](run: R => Either[E, A]) { self =>
  def map[B](f: A => B): ZIO[R, E, B] =
    ZIO(r => self.run(r).map(f))

  def flatMap[R1 <: R, E1 >: E, B](f: A => ZIO[R1, E1, B]): ZIO[R1, E1, B] =
    ZIO(r => self.run(r).fold(e => ZIO.fail(e), f).run(r))

  def foldZIO[R1 <: R, E1, B](
    failure: E => ZIO[R1, E1, B],
    success: A => ZIO[R1, E1, B]
  ): ZIO[R1, E1, B] =
    ZIO(r => self.run(r).fold(failure, success).run(r))

//  ZIO[-R, +E, +A](run: R => Either[Cause[E], A])
//  def foldCauseZIO[R1 <: R, E1, B](
//    failure: Cause[E] => ZIO[R1, E, B],
//    success: A => ZIO[R1, E, B]
//  ) =
//    ZIO(r => self.run(r).fold(failure, success).run(r))

  def fold[B](
    failure: E => B,
    success: A => B
  ): ZIO[R, Nothing, B] =
    ZIO(r => Right(self.run(r).fold(failure, success)))

  def provide(r: R): ZIO[Any, E, A] =
    ZIO(_ => self.run(r))

//  def zipWith[R1 <: R, E1 >: E, B, C](that: ZIO[R1, E1, B])(f: (A, B) => C): ZIO[R1, E, C] =
//    self.flatMap(a => that.fold(e => ZIO.fail(e), b => f(a, b)))

//  ???
  def orDie(implicit ev: E <:< Throwable): ZIO[R, Nothing, A] =
    self.fold(throw _, value => value)
}

object ZIO {
  type IO[+E, +A]   = ZIO[Any, E, A]
  type Task[+A]     = ZIO[Any, Throwable, A]
  type UIO[+A]      = ZIO[Any, Nothing, A]
  type URIO[-R, +A] = ZIO[R, Nothing, A]
  type RIO[-R, +A]  = ZIO[R, Throwable, A]

  def environment[R]: ZIO[R, Nothing, R] = ZIO(r => Right(r))
  def attempt[A](a: => A): ZIO[Any, Throwable, A] =
    ZIO(r =>
      try Right(a)
      catch { case ex: Throwable => Left(ex) }
    )
  def fail[E](e: => E): ZIO[Any, E, Nothing]    = ZIO(_ => Left(e))
  def success[A](a: => A): ZIO[Any, Nothing, A] = ZIO(_ => Right(a))

  def fromEither[E, A](eea: => Either[E, A]): IO[E, A] =
    eea match {
      case Left(e)      => ZIO.fail(e)
      case Right(value) => ZIO.success(value)
    }

  def fromTry[A](a: => Try[A]): Task[A] = a match {
    case Failure(exception) => ZIO(_ => Left(exception))
    case Success(value)     => ZIO(_ => Right(value))
  }

  def fromOption[A](op: => Option[A]): IO[None.type, A] =
    op match {
      case Some(value) => ZIO.success(value)
      case None        => ZIO(_ => Left(None))
    }

  @tailrec
  def async[R, E, A](cb: (ZIO[R, E, A] => Unit) => Any): ZIO[R, E, A] = async(cb)

  @tailrec
  def fromFuture[A](make: ExecutionContext => Future[A]): Task[A] = fromFuture(make)

  def zipWith[R, E, A, B, C](
    self: ZIO[R, E, A],
    that: ZIO[R, E, B]
  )(f: (A, B) => C): ZIO[R, E, C] =
    for {
      a <- self
      b <- that
    } yield f(a, b)

  def collectAll[R, E, A](in: Iterable[ZIO[R, E, A]]): ZIO[R, E, List[A]] =
    in.foldLeft(ZIO[R, E, List[A]](_ => Right(List[A]())))((acc, value) =>
      acc.flatMap(list => value.map(a => a :: list))
    )

  def foreach[R, E, A, B](in: Iterable[A])(f: A => ZIO[R, E, B]): ZIO[R, E, List[B]] =
    ZIO.collectAll(in.map(f))

  def orElse[R, E1, E2, A](
    self: ZIO[R, E1, A],
    that: ZIO[R, E2, A]
  ): ZIO[R, E2, A] =
    ZIO(r =>
      self.run(r) match {
        case Left(_)      => that.run(r)
        case Right(value) => Right(value)
      }
    )

}
