package com.github.jogoes.imagetools.util

import java.io.ByteArrayInputStream

import org.scalatest.{Matchers, TryValues, WordSpec}

class StreamUtilsSpec extends WordSpec with Matchers with TryValues {

  import StreamUtils._

  "StreamUtils.toByteArray" should {
    "return empty array for empty stream" in {
      val sourceData = Array[Byte]()

      val data = toByteArray(new ByteArrayInputStream(sourceData))
      data.success.value shouldBe sourceData
    }

    "read whole array" in {
      val sourceData = Array[Byte](1, 2, 3, 4, 5, 6)

      val data = toByteArray(new ByteArrayInputStream(sourceData))
      data.success.value shouldBe sourceData
    }
  }
}
