package example

import example.ExampleApp.{Greet, Greeting}
import framework.Green
import framework.compose.Common.{Error, Lambda, guard, lambda}
import framework.encoding.{Codec, JsonCodec}
import framework.http.Common.{View, api}
import framework.http.Routing
import framework.http.Views.Json
import framework.module.HttpModule
import io.vertx.core.DeploymentOptions
import io.vertx.core.http.HttpServerOptions
import org.json4s.DefaultFormats

object ComposeExample extends App {

	class ComposingModule extends HttpModule {
		implicit val codec:Codec = JsonCodec(DefaultFormats)
		implicit val lambdaErrorHandler:Throwable=>Error = { err => Error(err.getMessage) }
		implicit val httpErrorHandler:Error=>View = { err =>
			val json = codec.encode(err)
			Json(json, code = 500)
		}

		override def options:Option[HttpServerOptions] = None

		override def routing(router:Routing):Unit = {
			router.post("/greet", api[Greet](lambda(greetingFlow)))
		}

		val createGreet:Lambda[Greet, String] = guard(it => s"Hello ${it.name}!")
		val createGreeting:Lambda[String, Greeting] = guard(it => Greeting(it))
		val createGreetingView:Lambda[Greeting, View] = guard(it => {
			val json = codec.encode(it)
			Json(json)
		})
		val greetingFlow:Lambda[Greet, View] = (greet:Greet) => createGreet(greet)
			.flatMap(createGreeting)
			.sideeffects(it => println(it.text))
			.filter(it => it.text.length > 7)
			.flatMap(createGreetingView)
	}

	Green.withModule(classOf[ComposingModule], Some(new DeploymentOptions().setInstances(2)))
}
