package framework.module

import framework.http.Routing

trait RestModule extends Module {
	def routing(router:Routing):Unit
}
