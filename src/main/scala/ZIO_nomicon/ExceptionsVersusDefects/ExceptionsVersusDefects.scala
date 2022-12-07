package ZIO_nomicon.ExceptionsVersusDefects

import zio.{ IO, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault }

import java.io.IOException
import scala.io.StdIn

object ExceptionsVersusDefects extends ZIOAppDefault {
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = ???

  val xDefect: UIO[String]            = ZIO.succeed(StdIn.readLine()) // With defect IOException
  val xError: IO[IOException, String] = zio.Console.readLine

}
