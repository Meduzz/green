package framework.http

import framework.encoding.Codec
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.RoutingContext

object Common {
	type VertxHandler = RoutingContext => Unit

	trait View {
		def code:Int
		def contentType:String
		def render():String
	}

	trait Template {
		def render():String
	}

	def handler(func:RoutingContext=>Unit):VertxHandler = func

	// TODO introduce form[T](T=>View)?
	def api[T](func:T=>View)(implicit codec:Codec, manifest: Manifest[T]):VertxHandler = handler(ctx => {
		val body = ctx.getBodyAsString
		val entity = codec.decode[T](body)
		val view = func(entity)

		renderView(view, ctx.response())
	})

	def apiWithContext[T](func:RoutingContext => T => View)(implicit codec:Codec, manifest: Manifest[T]):VertxHandler = handler(ctx => {
		val next = func(ctx)

		val body = ctx.getBodyAsString
		val entity = codec.decode[T](body)
		val view = next(entity)

		renderView(view, ctx.response())
	})

	def render(view:View):VertxHandler = handler(ctx => {
		renderView(view, ctx.response())
	})

	def renderWithContext(func:RoutingContext => View):VertxHandler = handler(ctx => {
		val view = func(ctx)

		renderView(view, ctx.response())
	})

	protected def renderView(view:View, ctx:HttpServerResponse):Unit = {
		ctx.setStatusCode(view.code)
		ctx.putHeader("Content-Type", view.contentType)
		ctx.end(view.render())
	}
}
