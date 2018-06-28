package com.github.jogoes.imagetools.jpeg

import com.github.jogoes.imagetools.jpeg.Jpeg.Segment

object Jpeg {

  case class Marker(t1: Byte, t2: Byte) {
    override def toString: String = s"(0x${(t1 & 0xff).toHexString}, 0x${(t2 & 0xff).toHexString})"
  }

  object Marker {
    def apply(b: Byte): Marker = Marker(0xff.toByte, b)

    val SOI: Marker = Marker(0xd8.toByte)

    val DHT: Marker = Marker(0xc4.toByte)
    val JPG: Marker = Marker(0xc8.toByte)

    val DAC: Marker = Marker(0xcc.toByte)
    val DQT: Marker = Marker(0xdb.toByte)
    val DRI: Marker = Marker(0xdd.toByte)

    val COM: Marker = Marker(0xfe.toByte)
    val SOS: Marker = Marker(0xda.toByte)
    val EOI: Marker = Marker(0xd9.toByte)

    def app(n: Byte): Marker = Marker((0xe0 + n).toByte)
    def sof(n: Byte): Marker = Marker((0xc0 + n).toByte)
  }

  sealed trait Segment {
    def data: Array[Byte]
    def offset: Int
    def length: Int
  }

  object Segment {
    import Marker._

    final case class Soi(data: Array[Byte], offset: Int, length: Int) extends Segment

    final case class App(n: Byte, data: Array[Byte], offset: Int, length: Int) extends Segment

    final case class Dht(data: Array[Byte], offset: Int, length: Int) extends Segment

    final case class Jpg(data: Array[Byte], offset: Int, length: Int) extends Segment

    final case class Dac(data: Array[Byte], offset: Int, length: Int) extends Segment

    final case class Dqt(data: Array[Byte], offset: Int, length: Int) extends Segment

    final case class Dri(data: Array[Byte], offset: Int, length: Int) extends Segment

    final case class Sof(n: Byte, data: Array[Byte], offset: Int, length: Int) extends Segment

    final case class Com(data: Array[Byte], offset: Int, length: Int) extends Segment

    final case class Sos(data: Array[Byte], offset: Int, length: Int) extends Segment

    final case class Eoi(data: Array[Byte], offset: Int, length: Int) extends Segment

    final case class Other(marker: Marker, data: Array[Byte], offset: Int, length: Int)
        extends Segment

    val FF: Byte = 0xff.toByte

    def apply(marker: Marker, data: Array[Byte], offset: Int, length: Int): Segment = marker match {
      case SOI => Soi(data, offset, length)
      case DHT => Dht(data, offset, length)
      case JPG => Jpg(data, offset, length)
      case DAC => Dac(data, offset, length)
      case DQT => Dqt(data, offset, length)
      case DRI => Dri(data, offset, length)
      case COM => Com(data, offset, length)
      case SOS => Sos(data, offset, length)
      case Marker(FF, t2) if t2 >= 0xe0.toByte && t2 <= 0xef.toByte =>
        App((t2 & 0xf).toByte, data, offset, length)
      case Marker(FF, t2)
          if t2 >= 0xc0.toByte && t2 <= 0xcf.toByte && t2 != 0xc4.toByte && t2 != 0xc8.toByte =>
        Sof((t2 & 0xf).toByte, data, offset, length)
      case m => Other(m, data, offset, length)
    }
  }
}

case class Jpeg(data: Array[Byte], segments: Seq[Segment])
