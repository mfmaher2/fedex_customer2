package datastax.com.cam_analytics

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper

object JsonHelper {
  private final val _mapper = new ObjectMapper() with ScalaObjectMapper
  _mapper.registerModule(DefaultScalaModule)

  def fromJSON[T](jsonInput: String)(implicit m: Manifest[T]): T = {
      _mapper.readValue[T](jsonInput)
  }
}
