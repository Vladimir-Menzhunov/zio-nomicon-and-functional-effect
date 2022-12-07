package ZIO_nomicon.Part1

import zio.{ Scope, Task, ZIO, ZIOAppArgs, ZIOAppDefault }

import scala.io.StdIn

object ZioEnvironment extends ZIOAppDefault {

  def readLine: Task[String]              = ZIO.attempt(StdIn.readLine())
  def printLine(line: String): Task[Unit] = ZIO.attempt(println(line))

  def readLineFirstName: Task[String]  = ZIO.attempt(StdIn.readLine("first: "))
  def readLineSecondName: Task[String] = ZIO.attempt(StdIn.readLine("second: "))
  def zipWithEffect: ZIO[Any, Throwable, String] =
    readLineFirstName.zipWith(readLineSecondName)((first, second) => s"$first, $second")

  def collectAllEffect: ZIO[Any, Throwable, List[Unit]] = {
    val list = List(
      ZIO.attempt(println("Hello")),
      ZIO.attempt(println("my")),
      ZIO.attempt(println("World"))
    )
    ZIO.collectAll(list)
  }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = collectAllEffect

}
