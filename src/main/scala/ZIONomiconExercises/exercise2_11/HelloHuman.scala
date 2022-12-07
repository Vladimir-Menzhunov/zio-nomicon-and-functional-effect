package ZIONomiconExercises.exercise2_11

import zio.{ Scope, ZIO, ZIOAppArgs, ZIOAppDefault }

object HelloHuman extends ZIOAppDefault {
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    for {
      _    <- zio.Console.printLine("Hello, how are you?").orDie
      name <- zio.Console.readLine
      _    <- zio.Console.printLine(s"Hello, $name")
    } yield ()
}
