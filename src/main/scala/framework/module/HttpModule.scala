package framework.module

import io.vertx.core.{Handler, Promise, Vertx}
import io.vertx.core.http.{HttpServerOptions, HttpServerRequest}

trait HttpModule extends Module {
  def options:Option[HttpServerOptions]

  def defaultHandler:Handler[HttpServerRequest]

  override def start(wtf:Promise[Void]):Unit = {
    startServer(wtf, defaultHandler)
  }

  protected def startServer(wtf:Promise[Void], handler:Handler[HttpServerRequest]):Unit = {
    val server = options match {
      case None => {
        val server = getVertx.createHttpServer()
        server.requestHandler(handler)
        server.listen(8080)
      }
      case Some(opts) => {
        val server = getVertx.createHttpServer(opts)
        server.requestHandler(handler)
        server.listen()
      }
    }

    server.onComplete(result => {
      if (result.failed()) {
        wtf.fail(result.cause())
        result.cause().printStackTrace()
        getVertx.close()
      } else {
        wtf.complete()
        println(s"${this.getClass.getSimpleName} started on port: ${result.result().actualPort()}.")
      }
    })
  }
}
