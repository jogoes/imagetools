package com.github.jogoes.imagetools.util

import java.io.{ByteArrayOutputStream, InputStream}

import scala.annotation.tailrec
import scala.util.Try

object StreamUtils {

  def toByteArray(is: InputStream, bufferSize: Int = 16 * 1024): Try[Array[Byte]] = Try {
    val buffer = new Array[Byte](bufferSize)
    val bos    = new ByteArrayOutputStream()

    @tailrec
    def readNext(): Unit = {
      val n = is.read(buffer)
      if (n != -1) {
        bos.write(buffer, 0, n)
        readNext()
      }
    }

    readNext()
    bos.toByteArray
  }
}
