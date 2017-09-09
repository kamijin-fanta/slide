import akka.stream._
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}

case class MapGraph[In, Out](val fn: In => Out) extends GraphStage[FlowShape[In, Out]] {
  private val in = Inlet[In]("in")
  private val out = Outlet[Out]("out")

  override val shape = FlowShape(in, out)

  override def createLogic(attr: Attributes) = new GraphStageLogic(shape) {
    setHandler(in, new InHandler {
      override def onPush(): Unit = push(out, fn(grab(in)))
    })
    setHandler(out, new OutHandler {
      override def onPull(): Unit = pull(in)
    })
  }
}
