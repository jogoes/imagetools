package com.github.jogoes.imagetools.jpeg

import java.io.{ByteArrayInputStream, InputStream}

import com.github.jogoes.imagetools.UnitSpec
import com.github.jogoes.imagetools.jpeg.Jpeg.Segment.Soi
import Jpeg.Segment._

class JpegInMemoryReaderSpec extends UnitSpec {

  trait WithReader {
    val reader = new JpegInMemoryReader
  }

  "JpegInMemoryReader" should {

    "successfully read segments from valid JPEG" in new WithReader {

      val is: InputStream = getClass.getResource("/jpeg/valid.jpg").openStream()

      val jpeg = reader.read(is)

      val segments = jpeg.success.value.segments
      segments.length shouldBe 9

      segments(0) shouldBe a[Soi]
      segments(1) shouldBe a[App]
      segments(2) shouldBe a[Com]
      segments(3) shouldBe a[Dqt]
      segments(4) shouldBe a[Dqt]
      segments(5) shouldBe a[Sof]
      segments(6) shouldBe a[Dht]
      segments(7) shouldBe a[Dht]
      segments(8) shouldBe a[Sos]
    }

    "return error in case of invalid JPEG" in new WithReader {
      val data = Array[Byte](1, 2, 3)

      val jpeg = reader.read(new ByteArrayInputStream(data))

      jpeg.isFailure shouldBe true
    }

    "return error in case of partially valid JPEG" in new WithReader {
      val data = Array(0xff, 0xd8, 0xff, 0xe0, 0x00, 0x10).map(_.toByte)

      val jpeg = reader.read(new ByteArrayInputStream(data))

      jpeg.isFailure shouldBe true
    }
  }
}
