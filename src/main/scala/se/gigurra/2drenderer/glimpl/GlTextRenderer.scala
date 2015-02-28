package se.gigurra.renderer.glimpl

import scalaxy.streams.optimize
import se.gigurra.renderer.Font
import se.gigurra.renderer.TextModel
import se.gigurra.renderer.TextRenderer
import se.gigurra.renderer.TransformStack

class GlTextRenderer(renderer: GlRenderer, ts: TransformStack) extends TextRenderer {

  val t = ts.top
  // private val charCaches = new java.util.IdentityHashMap[Font, Array[TextModel]]

  private def getTextCharacterModel(c: Char, font: Font): TextModel = {
    val charCache = getCharCache(font)
    val i = c.asInstanceOf[Int]
    val cached = charCache(i)
    if (cached != null) {
      cached
    } else {
      val newItem = TextModel(c.toString, font)
      charCache(i) = newItem
      newItem
    }
  }

  private def getCharCache(font: Font): Array[TextModel] = {

    val cached = font.loadedContents.asInstanceOf[Array[TextModel]]
    if (cached != null) {
      cached
    } else {
      val out = new Array[TextModel](1024)
      font.loadedContents = out
      out
    }

  }

  override final def print(text: String, font: Font) {
    optimize(for (i <- 0 until text.length) {
      val c = text.charAt(i)
      val model = getTextCharacterModel(c, font)
      renderer.draw(model)
      t.translateX(model.width)
    })
  }

  override final def printLn(text: String, font: Font) {
    ts.push()
    print(text, font)
    ts.pop()
    t.translateY(-1)
  }

  override final def getTextWidth(text: String, font: Font): Float = {
    text.foldLeft(0.0f)((acc, c) => acc + getTextCharacterModel(c, font).width)
  }

}