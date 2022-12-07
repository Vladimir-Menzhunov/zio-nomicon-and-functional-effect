package ZIO_nomicon.Part1

import zio.{ Scope, ZIO, ZIOAppArgs, ZIOAppDefault }

import scala.io.StdIn

//object ErrorTypeSimpleProgram extends App {
//
//  lazy val readInt: ZIO[Any, NumberFormatException, Int] =
//    ZIO
//      .attempt(StdIn.readInt())
//      .foldZIO(_ => ZIO.fail(new NumberFormatException), a => ZIO.success(a))
//
//  lazy val readAndSumTwoInts: ZIO[Any, NumberFormatException, Int] =
//    for {
//      x <- readInt
//      y <- readInt
//    } yield x * y
//
//  for {
//    _ <- readAndSumTwoInts
//  } yield ()
//}
