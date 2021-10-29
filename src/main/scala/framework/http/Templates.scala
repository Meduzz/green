package framework.http

import framework.http.Common.Template

object Templates {
  /**
   * Template implementation that only returns the provided string.
   * @param body - the provided string
   */
  case class StringTemplate(body:String) extends Template {
    override def render(): String = body
  }

  /**
   * Template implementation that executes a string.format on render with the provided data.
   * @param body - the string template
   * @param params - the provided data
   */
  case class StringFormatTemplate(body:String, params:Any*) extends Template {
    override def render(): String = body.format(params)
  }
}
