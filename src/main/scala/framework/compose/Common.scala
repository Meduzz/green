package framework.compose

import scala.util.{Try, Success => Yes, Failure => No}

object Common {

	type Lambda[T,K] = T=>Result[K]

	trait Result[T] {
		def filter(cond:T=>Boolean):Result[T] = {
			this match {
				case Success(value) => {
					if (cond(value)) {
						this
					} else {
						Failure(Error(s"$value did not meet condition"))
					}
				}
				case f:Failure[T] => f
			}
		}

		def map[K](map:T=>K):Result[K] = {
			this match {
				case Success(value) => Success(map(value))
				case Failure(err) => Failure(err)
			}
		}

		def flatMap[K](map:T=>Result[K]):Result[K] = {
			this match {
				case Success(value) => map(value)
				case Failure(err) => Failure(err)
			}
		}

		def recover(func:Error=>T):Result[T] = {
			this match {
				case s:Success[T] => s
				case Failure(err) => Success(func(err))
			}
		}

		def foreach(func:T=>Unit):Unit = {
			this match {
				case Success(value) => func(value)
				case _ =>
			}
		}

		def getOrElse(other:T):T = {
			this match {
				case Success(value) => value
				case _ => other
			}
		}

		/**
			* A way to run stuff that is not pure, without affecting things.
			* @param func the dirty function.
			* @return returns a copy of this.
			*/
		def sideeffects(func:T=>Unit):Result[T] = {
			this match {
				case Success(value) => {
					func(value)
					Success(value)
				}
				case Failure(err) => Failure(err)
			}
		}
	}

	/**
		* Guard removes some re-occuring boilerplate from creating lambdas it also wraps func in Try().
		* @param func the lambda function
		* @param errorHandler an implicit error handler to deal with any exceptions.
		* @tparam T - the value in
		* @tparam K - the value out
		* @return returns a guarded function returning Result[K]
		*/
	def guard[T,K](func:T=>K)(implicit errorHandler:Throwable=>Error):Lambda[T,K] = {
		{ it =>
			Try(func(it)) match {
				case Yes(value) => Success(value)
				case No(err) => Failure(errorHandler(err))
			}
		}
	}

	/**
		* Http API helper, when using lambdas with http.Common.api function.
		* @param lambda the lambda, going from T=>View.
		* @param errorHandler the error handler, dealing with any errors from the lambda.
		* @tparam T - the T
		* @tparam View - the View
		* @return returns a function that http.Common.api can use.
		*/
	def lambda[T, View](lambda: Lambda[T,View])(implicit errorHandler:Error=>View):T=>View = {
		{ value =>
			lambda(value) match {
				case Success(view) => view
				case Failure(err) => errorHandler(err)
			}
		}
	}

	case class Success[T](value:T) extends Result[T]
	case class Failure[T](error:Error) extends Result[T]

	case class Error(message:String, code:Option[String] = None)
}
