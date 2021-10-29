package framework.requirement

import framework.http.Routing
import framework.module.RestModule
import io.vertx.core.http.{HttpServerOptions, HttpServerRequest}
import io.vertx.core.{Handler, Promise}
import io.vertx.ext.web.Router

trait HttpBase extends Requirement {
  def options:Option[HttpServerOptions]

  def defaultHandler():Handler[HttpServerRequest]

  override def start(startPromise: Promise[Void]): Unit = {
    val handler = defaultHandler()
    val server = options match {
      case None => {
        val server = getVertx().createHttpServer()
        server.requestHandler(handler)
        server.listen(8080)
      }
      case Some(opts) => {
        val server = getVertx().createHttpServer(opts)
        server.requestHandler(handler)
        server.listen()
      }
    }

    server.onComplete(result => {
      if (result.failed()) {
        result.cause().printStackTrace()
        startPromise.fail(result.cause())
        getVertx().close()
      } else {
        startPromise.complete()
        println(s"${this.getClass.getSimpleName} started on port: ${result.result().actualPort()}.")
      }
    })
  }

  protected def routed(modules:RestModule*):Handler[HttpServerRequest] = {
    val router = Router.router(getVertx)
    val routing = new Routing(router)
    modules.foreach(_.routing(routing))
    router
  }
}