package framework

import framework.module.Module
import io.vertx.core.{DeploymentOptions, Vertx, VertxOptions}

object Green {
	def withModule[T <: Module](module:Class[T], options:Option[DeploymentOptions] = None):Green = {
		val vertx = Vertx.vertx()
		val instance = new Green(vertx)
		instance.withModule(module, options)
	}

	def withSettings(options:VertxOptions):Green = {
		val vertx = Vertx.vertx(options)
		new Green(vertx)
	}
}

/*
	TODO
	Starting multiple modules that start a webserver that binds to the same port
	makes the requests bounce between the started server, generating 404 whenever
	hitting a server that did not define the path...

	Huge disappointment!
 */
class Green(val vertx: Vertx) {
	def withModule[T <: Module](module:Class[T], options:Option[DeploymentOptions] = None):Green = {
		val opts = options.getOrElse(new DeploymentOptions())
		vertx.deployVerticle(module, opts)
		this
	}
}