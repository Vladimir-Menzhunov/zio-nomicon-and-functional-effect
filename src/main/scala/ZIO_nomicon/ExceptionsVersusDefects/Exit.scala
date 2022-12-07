package ZIO_nomicon.ExceptionsVersusDefects

sealed trait Exit[+E, +A]

object Exit {
  final case class Success[+A](value: A)        extends Exit[Nothing, A]
  final case class Failure[+E](cause: Cause[E]) extends Exit[E, Nothing]
}
