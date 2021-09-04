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

class Green(val vertx: Vertx) {
	def withModule[T <: Module](module:Class[T], options:Option[DeploymentOptions] = None):Green = {
		val opts = options.getOrElse(new DeploymentOptions())
		vertx.deployVerticle(module, opts)
		this
	}
}