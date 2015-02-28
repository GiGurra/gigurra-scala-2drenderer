package se.gigurra.renderer

import scalaxy.streams.optimize

class TextModel(val text: String, vertices: Array[Float], colors: Array[Float], font: Font)
  extends Model(PrimitiveType.TRIANGLES, vertices, colors) {

  val width = (text map font.get).foldLeft(0.0f)(_ + _.advanceWidth)

  override def toString(): String = s"TextModel: $text"
}

object TextModel {
  def apply(text: String, vertices: Array[Float], colors: Array[Float], font: Font) = new TextModel(text, vertices, colors, font)
  def apply(text: String, font: Font): TextModel = {

    val glyphs = text map font.get

    val vertices =
      if (glyphs.length == 1) {
        val g = glyphs.head
        g.vertices
      } else {

        val vertices = new Array[Float](glyphs.foldLeft(0)(_ + _.vertices.length))
        var width = 0.0f
        var writeOffs = 0
        for (g <- glyphs) {
          optimize(for (i <- 0 until g.vertices.length by 4) {
            vertices(writeOffs + i + 0) = g.vertices(i + 0) + width
            vertices(writeOffs + i + 1) = g.vertices(i + 1)
            vertices(writeOffs + i + 2) = g.vertices(i + 2)
            vertices(writeOffs + i + 3) = g.vertices(i + 3)
          })
          writeOffs += g.vertices.length
          width += g.advanceWidth
        }

        vertices
      }

    val colors = vertices.clone
    optimize(for (i <- 0 until vertices.length by 4) {
      colors(i + 0) = font.color.r
      colors(i + 1) = font.color.g
      colors(i + 2) = font.color.b
      colors(i + 3) = font.color.a
    })

    new TextModel(text, vertices, colors, font)
  }
}
