package zio_nomicon

import zio.test.Assertion._
import zio.test.TestAspect.{ fibers, nonFlaky }
import zio.test.{ TestConfig, _ }
import zio.{ Clock, ExitCode, Random, Scope, Tag, ZEnvironment, ZLayer }

object ExampleTest extends ZIOSpecDefault {

  case class User(name: String, age: Int)

  val genName: Gen[Random with Sized, String] = Gen.asciiString
  val getAge: Gen[Random, Int]                = Gen.int(10, 120)

  val genUser: Gen[Random with Sized, User] =
    for {
      name <- genName
      age  <- getAge
    } yield User(name, age)

  override def spec: Spec[TestEnvironment with Scope, Nothing] =
    suite("ExampleTest")(
      test("user test") {
        check(genUser, genUser, genUser)((user1, user2, user3) =>
          assert(user1)(equalTo(user1)) &&
            assert(user2)(equalTo(user2)) &&
            assert(user3)(equalTo(user3))
        )
      }.provide(TestRandom.deterministic, TestConfig.default, Sized.default) @@ nonFlaky
    )
}
