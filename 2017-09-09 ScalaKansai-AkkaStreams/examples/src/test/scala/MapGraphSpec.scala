import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, Source}
import akka.stream.testkit.scaladsl.TestSink
import org.scalatest.FunSpec

class MapGraphSpec extends FunSpec {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  describe("flow basic test") {
    it("use probe") {
      val doubleFn = (in: Int) => in * 2
      val doubleFlow: Flow[Int, Int, NotUsed] = Flow.fromGraph(MapGraph(doubleFn))

      val testSrc = Source(1 to 8)
      val probe = testSrc
        .via(doubleFlow)
        .toMat(TestSink.probe[Int])(Keep.right)
        .run()

      probe.request(8)
      probe.expectNext(2, 4, 6, 8, 10, 12, 14, 16)
    }
  }
}
