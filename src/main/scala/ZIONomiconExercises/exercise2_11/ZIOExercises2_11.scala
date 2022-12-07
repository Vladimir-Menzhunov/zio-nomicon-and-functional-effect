package ZIONomiconExercises.exercise2_11

import zio.{ IO, Scope, Task, ZIO, ZIOAppArgs, ZIOAppDefault }

import java.io.IOException
import scala.concurrent.{ ExecutionContext, Future }

object ZIOExercises2_11 extends ZIOAppDefault {
  override def run: zio.ZIO[Any with ZIOAppArgs with Scope, Any, Any] = ???

  //doWhile(zio.Console.readLine)(_ == "str")
  def doWhile[R, E, A](
    body: ZIO[R, E, A]
  )(condition: A => Boolean): ZIO[R, E, A] =
    for {
      a <- body
      _ <- if (condition(a)) ZIO.unit else doWhile(body)(condition)
    } yield a

  def readUntil(
    acceptInput: String => Boolean
  ): IO[IOException, String] =
    for {
      str    <- zio.Console.readLine
      result <- if (acceptInput(str)) ZIO.succeed(str) else readUntil(acceptInput)
    } yield result

  case class Query()
  case class Result(result: String)
  def doQuery(query: Query)(implicit ec: ExecutionContext): Future[Result] = Future(Result("done"))

  def doQueryZio(query: Query): Task[Result] = ZIO.fromFuture(implicit ex => doQuery(query))

  //saveUserRecordZio("user")
  def save(user: String): Either[Throwable, Unit] =
    if (user == "user") Right(println(s"Saved: $user"))
    else Left(new Throwable)

  def saveUserRecord(user: String, onSuccess: () => Unit, onFailure: Throwable => Unit): Unit =
    save(user).fold(onFailure, _ => onSuccess())

  def saveUserRecordZio(user: String): Task[Unit] =
    ZIO.async(callback => saveUserRecord(user, () => callback(ZIO.unit), th => callback(ZIO.fail(th))))
  /*
    {
    val cacheValue = getCacheValue("key", str => println(str), _ => println("None"))
    getCacheValueZio("key").map(println)
  }
   */
  val cacheMap: Map[String, String] = Map[String, String]("key" -> "Hello")

  def getCacheValue(
    key: String,
    onSuccess: String => Unit,
    onFailure: Throwable => Unit
  ): Unit = {

    val value = cacheMap.get(key) match {
      case Some(value) => Right(value)
      case None        => Left(new Throwable)
    }

    value.fold(onFailure, onSuccess)
  }

  def getCacheValueZio(key: String): Task[String] =
    ZIO.async(callback => getCacheValue(key, str => callback(ZIO.succeed(str)), th => ZIO.fail(th)))

  def currentTime(): Long = java.lang.System.currentTimeMillis()

  lazy val currentTimeZIO: ZIO[Any, Nothing, Long] = ZIO.succeed(currentTime())

  def restToZIO[A](list: List[A]): ZIO[Any, None.type, A] =
    list.headOption match {
      case Some(value) => ZIO.succeed(value)
      case None        => ZIO.fail(None)
    }
  //restToZIO(List(1, 2, 3)).map(x => println(x))
  //restToZIO(List.empty)

  def eitherToZIO[E, A](either: Either[E, A]): ZIO[Any, E, A] =
    either match {
      case Left(ex)     => ZIO.fail(ex)
      case Right(value) => ZIO.succeed(value)
    }

  //eitherToZIO(Left(new Throwable()))
  //eitherToZIO(Right(println(5)))

  val random: Task[Int]                   = ZIO.attempt(scala.util.Random.nextInt(3) + 1)
  val readLine: Task[String]              = ZIO.attempt(scala.io.StdIn.readLine())
  def printLine(line: String): Task[Unit] = ZIO.attempt(println(line))

  val guessedNum: Task[Unit] =
    for {
      num   <- random
      _     <- printLine("Guess a number from 1 to 3")
      guess <- readLine
      _ <- if (guess == num.toString) printLine("You guessed right!")
          else printLine(s"You guessed wrong, the number was $num!")
    } yield ()

  val helloEffect: Task[Unit] =
    for {
      _    <- printLine("What is your name?")
      name <- readLine
      _    <- printLine(s"Hello $name")
    } yield helloEffect

  def readFileZio(file: String): Task[String] =
    for {
      file <- ZIO.attempt(scala.io.Source.fromFile(file))
      str  <- ZIO.attempt(file.getLines().mkString)
      _    <- ZIO.attempt(file.close())
    } yield str
  // readFileZio("src/resources/hello.txt")

  def writeFile(file: String, text: String): Unit = {
    import java.io._
    val pw = new PrintWriter(new File(file))

    try pw.write(text)
    finally pw.close()
  }

  def writeFileZio(file: String, text: String): Task[Unit] = ZIO.attempt(writeFile(file, text))
  //writeFileZio("src/resources/hello.txt", "World")

  def copyFileZio(source: String, dest: String): Task[Unit] =
    for {
      contents <- readFileZio(source)
      _        <- writeFileZio(dest, contents)
    } yield ()

  //copyFileZio("src/resources/hello.txt", "src/resources/helloCopy.txt")

}
