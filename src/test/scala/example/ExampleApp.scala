package example

import framework.Green
import framework.encoding.{Codec, JsonCodec}
import framework.http.Routing
import framework.http.Views.{Html, Json}
import framework.module.HttpModule
import framework.http.Common.{api, render}
import io.vertx.core.http.HttpServerOptions
import org.json4s.DefaultFormats

object ExampleApp extends App {
	class ExampleModule extends HttpModule {
		val options:Option[HttpServerOptions] = None

		override def routing(router:Routing):Unit = {
			router.get("/", render(Html("<h1>Hello world!</h1>")))
		}
	}

	class ApiModule extends HttpModule {
		implicit val codec:Codec = JsonCodec(DefaultFormats.strict)

		val options:Option[HttpServerOptions] = None

		override def routing(router:Routing):Unit = {
			router.post("/api/", api[Greet](it => {
				val json = codec.encode(Greeting(s"Hello ${it.name}!"))
				Json(json)
			}))
		}
	}

	Green.withModule(classOf[ExampleModule])
  	.withModule(classOf[ApiModule])

	case class Greet(name:String)
	case class Greeting(text:String)
}
