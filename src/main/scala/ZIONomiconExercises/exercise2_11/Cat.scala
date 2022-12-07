package ZIONomiconExercises.exercise2_11

import ZIONomiconExercises.exercise2_11.ZIOExercises2_11.readFileZio
import zio.Console.readLine
import zio.{ Scope, ZIO, ZIOAppArgs, ZIOAppDefault }

object Cat extends ZIOAppDefault {
  def method(commandLineArguments: List[String]): ZIO[Any, Throwable, List[String]] =
    ZIO.foreach(commandLineArguments)(filename => readFileZio(filename))

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    for {
      str  <- readLine
      list <- method(str.split(",").toList)
      _    <- ZIO.foreach(list)(el => ZIO.succeed(println(el)))
    } yield ()
}
