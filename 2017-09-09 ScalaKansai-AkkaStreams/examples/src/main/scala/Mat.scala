import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source}

import scala.concurrent.Future

object Mat {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val mat = ActorMaterializer()
    implicit val ctx = system.dispatcher

    val src: Source[Int, NotUsed] = Source(1 to 20)
    val primeFilterFlow: Flow[Int, Int, NotUsed] = Flow[Int].filter {
      case i if i <= 1 => false
      case i if i == 2  => true
      case i => !(2 until i).exists(x => i % x == 0)
    }
    val collectIntSink: Sink[Int, Future[Set[Int]]] =
      Sink.fold(Set[Int]()){ case (a, b) => a + b }

    val runnableGraph: RunnableGraph[Future[Set[Int]]] =
      (src via primeFilterFlow toMat collectIntSink)(Keep.right)

    val future: Future[Set[Int]] = runnableGraph.run()
    future.onComplete { x =>
        println(x)
        system.terminate()
    }
  }
}
