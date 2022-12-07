package ZIO_nomicon.ExceptionsVersusDefects

sealed trait Cause[+E]

object Cause {
  final case class Die(t: Throwable) extends Cause[Nothing] // defect
  final case class Fail[+E](e: E)    extends Cause[E]       // error

  final case class Both[+E](left: Cause[E], right: Cause[E]) extends Cause[E]
  final case class Then[+E](left: Cause[E], right: Cause[E]) extends Cause[E]
}
