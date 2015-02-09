package mesosphere.marathon.api.v2

import javax.ws.rs.{ Path, GET, Consumes, Produces }
import javax.ws.rs.core.{ MediaType, Response }
import com.codahale.metrics.annotation.Timed
import javax.inject.Inject
import mesosphere.marathon.MarathonConf
import mesosphere.marathon.api.RestResource
import mesosphere.marathon.api.v2.json.Formats
import mesosphere.marathon.tasks.TaskQueue
import play.api.libs.json.Json

@Path("v2/queue")
@Consumes(Array(MediaType.APPLICATION_JSON))
class QueueResource @Inject() (
    taskQueue: TaskQueue,
    val config: MarathonConf) extends RestResource {

  @GET
  @Timed
  @Produces(Array(MediaType.APPLICATION_JSON))
  def index(): Response = {
    import Formats._

    val queuedWithDealy = taskQueue.listWithDelay.map {
      case (task, delay) =>
        Json.obj(
          "app" -> task.app,
          "count" -> task.count.get(),
          "delay" -> Json.obj(
            "overdue" -> delay.isOverdue()
          )
        )
    }

    ok(Json.obj("queue" -> queuedWithDealy))
  }
}
