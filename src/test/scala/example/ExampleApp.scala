package example

import framework.Green
import framework.encoding.{Codec, JsonCodec}
import framework.http.Routing
import framework.http.Views.{Html, Json}
import framework.module.RestModule
import framework.http.Common.{api, render}
import framework.http.Templates.StringTemplate
import framework.requirement.HttpBase
import io.vertx.core.Handler
import io.vertx.core.http.{HttpServerOptions, HttpServerRequest}
import org.json4s.DefaultFormats

object ExampleApp extends App {
	class ExampleModule extends RestModule {
		override def routing(router:Routing):Unit = {
			router.get("/", render(Html(StringTemplate("<h1>Hello world!</h1>"))))
		}
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
			routed(new ExampleModule, new ApiModule)
		}
	}

	Green.require(classOf[HttpServer])

	case class Greet(name:String)
	case class Greeting(text:String)
}
