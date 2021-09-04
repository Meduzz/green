package framework.encoding

trait Codec {
	/**
		* Encode anything to a string
		* @param any - the thing to encode
		* @return returns the encoded result.
		*/
	def encode(any:AnyRef):String

	/**
		* Decode a string into something
		* @param data - the string to decode
		* @tparam T - the something definition.
		* @return returns an instance of something.
		*/
	def decode[T](data:String)(implicit manifest: Manifest[T]):T
}
