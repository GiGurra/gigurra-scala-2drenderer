package se.gigurra.renderer

import scala.language.implicitConversions

object Renderer {
  implicit def toTransform(renderer: Renderer): Transform = renderer.getTransform
  implicit def toTransformStack(renderer: Renderer): TransformStack = renderer.getTransformStack
  implicit def toTextRenderer(renderer: Renderer): TextRenderer = renderer.getTextRenderer
}

trait Renderer {

  def draw(widget: Widget[_], isRoot: Boolean)
  def draw(widget: Widget[_]) { draw(widget, false) }
  def draw(model: Model)
  def flush() // flushes all drawing, useful before making state changes

  def setColorScale(c: Color)
  def colorScale(): Color

  // State changes requiring flush
  def setLineWidth(w: Float)

  def dispose()
  def getTransform(): Transform = getTransformStack().top
  def getTransformStack(): TransformStack
  def getTextRenderer(): TextRenderer

  def setViewPort(x: Int, y: Int, w: Int, h: Int)

  def surfaceWidth(): Int
  def surfaceHeight(): Int
  def surfaceSize() = Rect(surfaceWidth, surfaceHeight)
  def setViewPort(r: Rect[Int]) { setViewPort(r.x, r.y, r.w, r.h) }
  def setViewPortToFull() { setViewPort(surfaceSize) }

  // An implementation is free to implement these optionally
  def nVerticesQueued(): Int = ???
  def nModelsDrawn(): Int = ???
  def nBatchDrawCalls(): Int = ???
  def getFps(): Float = ???

  def multColorScale(c: Color) {
    pushPopColorScale {
      setColorScale(colorScale() * c)
    }
  }
  
  def draw(model: Model, colorScale: Color) {
    useColorScale(colorScale)(draw(model))
  }
  
  def draw(model: Widget[_], colorScale: Color) {
    useColorScale(colorScale)(draw(model))
  }
  
  def pushPopColorScale[A](f: => A) {
    val colBefore = colorScale
    f
    setColorScale(colBefore)
  }

  def useMultTransform[A](t: Transform)(f: => A) {
    this.pushPopTransform {
      this.mult(t)
      f
    }
  }

  def useTransform[A](t: Transform)(f: => A) {
    this.pushPopTransform {
      this.load(t)
      f
    }
  }

  def useColorScale[A](c: Color)(f: => A) {
    pushPopColorScale {
      setColorScale(c)
      f
    }
  }

  def useMultColorScale[A](c: Color)(f: => A) {
    pushPopColorScale {
      multColorScale(c)
      f
    }
  }

}
