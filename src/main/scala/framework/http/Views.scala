package framework.http

import framework.encoding.Codec
import framework.http.Common.{Template, ViewWithRedirect, ViewWithRender}

object Views {
	case class Html(body:Template, code:Int = 200, contentType:String = "text/html") extends ViewWithRender {
		override def render(): String = body.render()
	}
	case class Json(body:AnyRef, code:Int = 200, contentType:String = "application/json")(implicit codec: Codec) extends ViewWithRender {
		override def render(): String = codec.encode(body)
	}
	case class Redirect(to:String, code:Int = 302) extends ViewWithRedirect
}
