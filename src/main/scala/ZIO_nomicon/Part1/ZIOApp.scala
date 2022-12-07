package ZIO_nomicon.Part1

import ZIO_nomicon.ZIO

import java.io.IOException
import scala.io.StdIn

object ZIOApp extends App {
  val zioEffect = ZIO.attempt(throw new IOException())

  val effect = for {
    name <- zioEffect.orDie
  } yield name

  effect.run()
}

/*

  val self = ZIO.fail(new Throwable())
  val that = ZIO.success(println(10))

  ZIO.orElse(self, that).run()
/////
  val listEffect = List(10, 11, 12)

  val foreach = ZIO.foreach(listEffect)(a => ZIO.success(println(a)))

  foreach.run()
///////
    val listEffect = List(
    ZIO.success(println(10)),
    ZIO.success(println(11)),
    ZIO.success(println(12)),
    ZIO.success(println(13)),
    ZIO.success(println(14))
  )

  ZIO.collectAll(listEffect).run()

  /////

  val x = ZIO.zipWith(ZIO.success(5), ZIO.success(6))((a, b) => println(a + b))

  val y = for {
    _ <- x
    _ <- x
  } yield ()

  y.run()

 */
