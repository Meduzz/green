package example

import framework.Green
import framework.encoding.{Codec, JsonCodec}
import framework.http.Routing
import framework.http.Views.{Html, Json, Redirect}
import framework.module.RestModule
import framework.http.Common.{View, api, render, renderWithContext}
import framework.lambda.Common.{Error, Lambda, Result, guard, lambda}
import framework.http.Templates.{StringFormatTemplate, StringTemplate}
import framework.requirement.HttpBase
import io.vertx.core.Handler
import io.vertx.core.http.{HttpServerOptions, HttpServerRequest}
import io.vertx.ext.web.RoutingContext
import org.json4s.DefaultFormats

object ExampleApp extends App {
	class ExampleModule extends RestModule {
		implicit val lambdaErrorHandler:Throwable=>Error = { err => Error(err.getMessage) }
		implicit val httpErrorHandler:Error=>View = { err => Html(StringFormatTemplate("<h1>%s</h1>\n<p>%s</p>", err.code.getOrElse("Error"), err.message)) }

		override def routing(router:Routing):Unit = {
			router.get("/", render(Html(StringTemplate("<h1>Hello world!</h1>"))))
			router.get("/redirect", render(Redirect("/", 303)))
			router.get("/greet/:world", renderWithContext(lambda(greetFlow)))
		}

		private val fromPathParam:RoutingContext => String = ctx => ctx.pathParam("world")
		private val merge:String=>String = in => s"Hello $in!"
		private val asView = guard[String, View](in => Html(StringTemplate(s"<h1>$in</h1>")))
		private val greetFlow:Lambda[RoutingContext, View] = Result.from(_)
			.map(fromPathParam)
			.map(merge)
			.flatMap(asView)
	}

	class ApiModule extends RestModule {
		implicit val codec:Codec = JsonCodec(DefaultFormats.strict)

		override def routing(router:Routing):Unit = {
			router.post("/api/", api[Greet](it => {
				Json(Greeting(s"Hello ${it.name}!"))
			}))
		}
	}

	class HttpServer extends HttpBase {
		val options:Option[HttpServerOptions] = None

		override def defaultHandler(): Handler[HttpServerRequest] = {
			routed(None, new ExampleModule, new ApiModule)
		}
	}

	Green.require(classOf[HttpServer])

	case class Greet(name:String)
	case class Greeting(text:String)
}
