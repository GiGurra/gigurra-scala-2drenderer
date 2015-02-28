package se.gigurra.renderer

import scalaxy.streams.optimize

object Color {

  val RED = Color(1.0f, 0.0f, 0.0f, 1.0f)
  val GREEN = Color(0.0f, 1.0f, 0.0f, 1.0f)
  val BLUE = Color(0.0f, 0.0f, 1.0f, 1.0f)
  val BLACK = Color(0.0f, 0.0f, 0.0f, 1.0f)
  val WHITE = Color(1.0f, 1.0f, 1.0f, 1.0f)

  def apply(hex: String): Color = {
    def h2f(s: Int, e: Int) = Integer.valueOf(hex.substring(s, e), 16).toFloat / 256.0f
    if (hex.length == 7) {
      apply(h2f(1, 3), h2f(3, 5), h2f(5, 7), h2f(7, 9))
    } else if (hex.length == 5) {
      apply(h2f(1, 3), h2f(3, 5), h2f(5, 7), 1.0f)
    } else {
      throw new RuntimeException(s"Bad color code: $hex, (should be e.g. #FFFFFF or #FFFFFFFF)")
    }
  }

  implicit class RichColor(val c: Color) extends AnyVal {
    def butWith(r: Float = c.r, g: Float = c.g, b: Float = c.b, a: Float = c.a) = Color(r, g, b, a)
    def toArray(): Array[Float] = Array(c.r, c.g, c.b, c.a)
    def scaled(sr: Float = 1.0f, sg: Float = 1.0f, sb: Float = 1.0f, sa: Float = 1.0f) = Color(sr * c.r, sg * c.g, sg * c.b, sa * c.a)
    def scaledrgb(srgb: Float = 1.0f, sa: Float = 1.0f) = scaled(srgb, srgb, srgb, sa)
  }

  def mkColorScale(sr: Float, sg: Float, sb: Float): Color = Color(sr, sg, sb, 1.0f)
  def mkColorScale(s: Float): Color = mkColorScale(s, s, s)

}

case class Color(r: Float, g: Float, b: Float, a: Float) {

  val array = Array(r, g, b, a)

  def mkArray(nVertices: Int) = {
    val out = new Array[Float](nVertices * 4)
    optimize(for (i <- 0 until nVertices) {
      out(i * 4 + 0) = r
      out(i * 4 + 1) = g
      out(i * 4 + 2) = b
      out(i * 4 + 3) = a
    })
    out
  }

  def *(c: Color): Color = {
    Color(
      r * c.r,
      g * c.g,
      b * c.b,
      a * c.a)
  }

}
