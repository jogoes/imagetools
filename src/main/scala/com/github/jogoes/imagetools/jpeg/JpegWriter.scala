package com.github.jogoes.imagetools.jpeg

import java.io.OutputStream

import scala.util.Try

object JpegWriter {
  def apply() : JpegWriter = new JpegWriter
}

class JpegWriter {
  def write(jpeg: Jpeg, os: OutputStream): Try[Unit] = Try {
    jpeg.segments.foreach(segment => os.write(segment.data, segment.offset, segment.length))
  }
}
