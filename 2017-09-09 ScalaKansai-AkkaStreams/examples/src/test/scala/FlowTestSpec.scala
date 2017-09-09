import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, Source}
import akka.stream.testkit.scaladsl.TestSink
import org.scalatest.FunSpec

class FlowTestSpec extends FunSpec {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  describe("flow basic test") {
    it("use probe") {
      val primeFilterFlow: Flow[Int, Int, NotUsed] = Flow[Int].filter {
        case i if i <= 1 => false
        case i if i.==(2)  => true
        case i => !(2 until i).exists(x => i % x == 0)
      }

      val testSrc = Source(1 to 20)
      val probe = testSrc
        .via(primeFilterFlow)
        .toMat(TestSink.probe[Int])(Keep.right)
        .run()

      probe.request(8)
      probe.expectNext(2, 3, 5, 7, 11, 13, 17, 19)
    }
  }
}
