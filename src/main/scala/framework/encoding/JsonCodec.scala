package framework.encoding

import org.json4s._
import org.json4s.native.Serialization.{read, write}

object JsonCodec {

	class JsonCodec(implicit formats:Formats) extends Codec {
		/**
			* Encode anything to a string
			*
			* @param any - the thing to encode
			* @return returns the encoded result.
			*/
		override def encode(any:AnyRef):String = write(any)

		/**
			* Decode a string into something
			*
			* @param data - the string to decode
			* @tparam T - the something definition.
			* @return returns an instance of something.
			*/
		override def decode[T](data:String)(implicit manifest: Manifest[T]):T = read[T](data)
	}

	def apply(implicit formats:Formats):JsonCodec = new JsonCodec()
}
