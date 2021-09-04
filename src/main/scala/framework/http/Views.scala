package framework.http

import framework.http.Common.View

object Views {
	case class Html(body:String, code:Int = 200, contentType:String = "text/html") extends View
	case class Json(body:String, code:Int = 200, contentType:String = "application/json") extends View
}
