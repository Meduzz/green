package example

import example.ExampleApp.{ApiModule, Greet, Greeting}
import framework.Green
import framework.lambda.Common.{Error, Lambda, guard, lambda}
import framework.encoding.{Codec, JsonCodec}
import framework.http.Common.{View, api}
import framework.http.Routing
import framework.http.Views.Json
import framework.module.RestModule
import framework.requirement.HttpBase
import io.vertx.core.{DeploymentOptions, Handler}
import io.vertx.core.http.{HttpServerOptions, HttpServerRequest}
import org.json4s.DefaultFormats

object ComposeExample extends App {

	class ComposingModule extends RestModule {
		implicit val codec:Codec = JsonCodec(DefaultFormats)
		implicit val lambdaErrorHandler:Throwable=>Error = { err => Error(err.getMessage) }
		implicit val httpErrorHandler:Error=>View = { err =>
			val json = codec.encode(err)
			Json(json, code = 500)
		}

		override def routing(router:Routing):Unit = {
			router.post("/greet", api[Greet](lambda(greetingFlow)))
		}

		val createGreet:Lambda[Greet, String] = guard(it => s"Hello ${it.name}!")
		val createGreeting:Lambda[String, Greeting] = guard(it => Greeting(it))
		val createGreetingView:Lambda[Greeting, View] = guard(it => {
			Json(it)
		})
		val greetingFlow:Lambda[Greet, View] = (greet:Greet) => createGreet(greet)
			.flatMap(createGreeting)
			.sideeffects(it => println(it.text))
			.filter(it => it.text.length > 7)
			.flatMap(createGreetingView)
	}

	class MyServer extends HttpBase {
		override def options:Option[HttpServerOptions] = None

		override def defaultHandler(): Handler[HttpServerRequest] = {
			routed(new ComposingModule)
		}
	}

	Green.require(classOf[MyServer], Some(new DeploymentOptions().setInstances(2)))
}
