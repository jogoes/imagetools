package com.github.jogoes.imagetools.jpeg

import java.io.{IOException, InputStream}

import com.github.jogoes.imagetools.jpeg.Jpeg.Segment.{Soi, Sos}
import com.github.jogoes.imagetools.jpeg.Jpeg.{Marker, Segment}
import com.github.jogoes.imagetools.util.StreamUtils

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

trait JpegReader {
  def read(in: InputStream): Try[Jpeg]
}

object JpegReader {
  def apply() : JpegReader = new JpegInMemoryReader
}

class JpegInMemoryReader extends JpegReader {

  def read(is: InputStream): Try[Jpeg] = {
    for {
      bytes <- StreamUtils.toByteArray(is)
      jpeg  <- read(bytes)
    } yield jpeg
  }

  def read(bytes: Array[Byte]): Try[Jpeg] =
    readSegments(bytes).map(segments => Jpeg(bytes, segments))

  import Jpeg.Marker._

  private def validate(bytes: Array[Byte]): Try[Unit] = {
    if (bytes.length < 4) {
      Failure(new IOException("Invalid JPEG format: not enough data."))
    } else if (Marker(bytes(0), bytes(1)) != SOI) {
      Failure(new IOException("Invalid JPEG format: missing SOI tag."))
    } else {
      Success(())
    }
  }

  private def readSegments(bytes: Array[Byte]): Try[Vector[Segment]] = {
    @tailrec
    def readNextSegment(bytes: Array[Byte],
                        offset: Int,
                        segments: Vector[Segment]): Try[Vector[Segment]] = {
      if (offset >= bytes.length) {
        Failure(new IOException("Invalid JPEG format: unexpected end of data."))
      } else {
        val header: Marker = Marker(bytes(offset), bytes(offset + 1))
        header match {
          case SOS => Success(segments :+ Sos(bytes, offset, bytes.length - offset))
          case Marker(t1, t2) if t1 == 0xff.toByte =>
            val length   = (bytes(offset + 2) & 0xff) * 256 + (bytes(offset + 3) & 0xff)
            val endPoint = offset + length + 2
            readNextSegment(bytes,
                            endPoint,
                            segments :+ Segment(Marker(t2), bytes, offset, length + 2))
          case Marker(t1, t2) =>
            Failure(new IOException(s"Invalid JPEG format: unexpected markers ($t1, $t2) found."))
        }
      }
    }

    for {
      _        <- validate(bytes)
      segments <- readNextSegment(bytes, 2, Vector(Soi(bytes, 0, 2)))
    } yield segments
  }
}
