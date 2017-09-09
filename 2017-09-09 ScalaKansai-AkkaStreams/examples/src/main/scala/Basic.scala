import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

object Basic {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val mat = ActorMaterializer()

    val src = Source(1 to 5)
    val doubleFlow = Flow[Int].map(x => x * 2)
    val printSink = Sink.foreach(println)

    val runnableGraph = src via doubleFlow to printSink

    runnableGraph.run()

    Thread.sleep(100)
    system.terminate()
  }
}
