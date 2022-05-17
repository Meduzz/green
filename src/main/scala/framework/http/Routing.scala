package framework.http

import framework.http.Common.VertxHandler
import io.vertx.ext.web.handler.{BodyHandler, StaticHandler}
import io.vertx.ext.web.{Route, Router}

import scala.collection.Seq
import scala.util.{Failure, Success, Try}

class Routing(val router:Router) {

	def get(path:String, handlers:VertxHandler*):Unit = {
		val route = router.get(path)
		setupRoute(route, handlers)
	}

	def post(path:String, handlers:VertxHandler*):Unit = {
		val route = router.post(path)

		setupRoute(route, handlers)
	}

	def put(path:String, handlers:VertxHandler*):Unit = {
		val route = router.put(path)

		setupRoute(route, handlers)
	}

	def delete(path:String, handlers:VertxHandler*):Unit = {
		val route = router.delete(path)

		setupRoute(route, handlers)
	}

	def patch(path:String, handlers:VertxHandler*):Unit = {
		val route = router.patch(path)

		setupRoute(route, handlers)
	}

	def options(path:String, handlers:VertxHandler*):Unit = {
		val route = router.options(path)

		setupRoute(route, handlers)
	}

	def static(path:String, dir:String):Unit = {
		val handler = StaticHandler.create(dir)

		router.route(path).handler(handler.handle)
	}

	private def setupRoute(route:Route, handlers:Seq[VertxHandler]):Unit = {
		handlers.foreach(it => {
			route.blockingHandler(ctx => {
				Try(it(ctx)) match {
					case s:Success[Unit] => {
						if (!ctx.response().ended()) {
							ctx.next()
						}
					}
					case Failure(e) => {
						ctx.fail(500, e) // TODO error handler.
					}
				}
			})
		})
	}
}
