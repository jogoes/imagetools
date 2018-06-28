package com.github.jogoes.imagetools.jpeg

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, FileOutputStream}

import com.github.jogoes.imagetools.UnitSpec
import com.github.jogoes.imagetools.jpeg.Jpeg.Segment.Com
import com.github.jogoes.imagetools.util.StreamUtils._

class JpegWriterSpec extends UnitSpec {

  trait WithImageData {
    val is     = getClass.getResource("/jpeg/valid.jpg").openStream()
    val data   = toByteArray(is).success.value
    val reader = new JpegInMemoryReader()
    val jpeg   = reader.read(new ByteArrayInputStream(data)).success.value
  }

  "JpegWriter" should {
    "write image unmodified" in new WithImageData {

      val bos    = new ByteArrayOutputStream()
      val writer = JpegWriter()
      writer.write(jpeg, bos)

      bos.toByteArray shouldBe data
    }

    "write image correctly with removed comment segment" in new WithImageData {

      val filteredSegments = jpeg.segments.filterNot(s => s.isInstanceOf[Com])

      val targetJpeg = jpeg.copy(segments = filteredSegments)

      val bos    = new ByteArrayOutputStream()
      val writer = JpegWriter()
      writer.write(targetJpeg, bos)

      val writtenJpeg = reader.read(new ByteArrayInputStream(bos.toByteArray)).success.value

      writtenJpeg.segments.length shouldBe 8
      writtenJpeg.segments.exists(_.isInstanceOf[Com]) shouldBe false
    }
  }
}
