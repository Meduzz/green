package framework.module

import framework.http.Routing
import io.vertx.core.{Handler, Promise}
import io.vertx.core.http.{HttpServerOptions, HttpServerRequest}
import io.vertx.ext.web.Router

trait RestModule extends HttpModule {

	def routing(router:Routing):Unit

	override def defaultHandler: Handler[HttpServerRequest] = null

	override def start(wtf:Promise[Void]):Unit = {
		val router = Router.router(getVertx)
		routing(new Routing(router))
		startServer(wtf, router)
	}
}
