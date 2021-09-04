package framework.module

import framework.http.Routing
import io.vertx.core.Promise
import io.vertx.core.http.HttpServerOptions
import io.vertx.ext.web.Router

trait HttpModule extends Module {

	def options:Option[HttpServerOptions]

	def routing(router:Routing):Unit

	override def start(wtf:Promise[Void]):Unit = {
		val router = Router.router(getVertx)
		routing(new Routing(router))

		val server = options match {
			case None => {
				val server = getVertx.createHttpServer()
				server.requestHandler(router)
				server.listen(8080)
  		}
			case Some(opts) => {
				val server = getVertx.createHttpServer(opts)
				server.requestHandler(router)
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
