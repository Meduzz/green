package framework.http

import framework.encoding.Codec
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.RoutingContext

object Common {
	type VertxHandler = RoutingContext => Unit

	trait View {
		def code:Int
	}

	trait ViewWithRedirect extends View {
		def to:String
	}

	trait ViewWithRender extends View {
		def contentType:String
		def render():String
	}

	trait Template {
		def render():String
	}

	def handler(func:RoutingContext=>Unit):VertxHandler = func

	def json[T](ctx:RoutingContext)(implicit codec:Codec, manifest: Manifest[T]):T = {
		val body = ctx.getBodyAsString
		codec.decode[T](body)
	}

	def api[T](func:T=>View)(implicit codec:Codec, manifest: Manifest[T]):VertxHandler = handler(ctx => {
		val entity = json(ctx)
		val view = func(entity)

		renderView(view, ctx)
	})

	def apiWithContext[T](func:RoutingContext => T => View)(implicit codec:Codec, manifest: Manifest[T]):VertxHandler = handler(ctx => {
		val next = func(ctx)

		val entity = json(ctx)
		val view = next(entity)

		renderView(view, ctx)
	})

	def render(view:View):VertxHandler = handler(ctx => {
		renderView(view, ctx)
	})

	def renderWithContext(func:RoutingContext => View):VertxHandler = handler(ctx => {
		val view = func(ctx)

		renderView(view, ctx)
	})

	protected def renderView(view:View, ctx:RoutingContext):Unit = {
		ctx.response().setStatusCode(view.code)

		view match {
			case render:ViewWithRender => {
				ctx.response().putHeader("Content-Type", render.contentType)
				ctx.response().end(render.render())
			}
			case redirect:ViewWithRedirect => {
				ctx.redirect(redirect.to)
			}
		}
	}
}
