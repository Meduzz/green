package framework

import framework.requirement.Requirement
import io.vertx.core.{DeploymentOptions, Vertx, VertxOptions}

object Green {
	def require[T <: Requirement](module:Class[T], options:Option[DeploymentOptions] = None):Green = {
		val vertx = Vertx.vertx()
		val instance = new Green(vertx)
		instance.require(module, options)
	}

	def withSettings(options:VertxOptions):Green = {
		val vertx = Vertx.vertx(options)
		new Green(vertx)
	}
}

class Green(val vertx: Vertx) {
	def require[T <: Requirement](module:Class[T], options:Option[DeploymentOptions] = None):Green = {
		val opts = options.getOrElse(new DeploymentOptions())
		vertx.deployVerticle(module, opts)
		this
	}
}