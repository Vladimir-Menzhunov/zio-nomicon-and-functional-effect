package ZIONomiconExercises.exercise2_11

import zio.{ Scope, ZIO, ZIOAppArgs, ZIOAppDefault }

object NumberGuessing extends ZIOAppDefault {
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    for {
      num   <- zio.Random.nextIntBetween(1, 3)
      _     <- zio.Console.printLine("Guess number from 1 to 3")
      guess <- zio.Console.readLine
      _     <- if (num.toString == guess) zio.Console.printLine("Yes") else zio.Console.printLine(s"No, was $num")
    } yield ()
}
