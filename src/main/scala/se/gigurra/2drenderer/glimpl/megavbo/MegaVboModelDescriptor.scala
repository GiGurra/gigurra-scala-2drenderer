package se.gigurra.renderer.glimpl.megavbo

import javax.media.opengl.GL3ES3
import se.gigurra.renderer.Model
import javax.media.opengl.GL
import se.gigurra.renderer.PrimitiveType

class MegaVboModelDescriptor(val model: Model, val vertexOffset: Int) {

  val primType = MegaVboModelDescriptor.getGlprimType(model.primType)
  val nVertices = model.vertexCount
  val floatOffset = vertexOffset * 4
  val nBytes = nVertices * 4

  // private var _nDrawsThisFrame = 0

  def drawNow(gl: GL3ES3) {
    gl.glDrawArrays(primType, vertexOffset, nVertices)
  }

  // def resetDrawCounter() { _nDrawsThisFrame = 0 }
  //  def isDrawnThisFrame() = _nDrawsThisFrame != 0

}

object MegaVboModelDescriptor {

  def getGlprimType(primType: PrimitiveType): Int = {
    primType match {
      case PrimitiveType.TRIANGLES => GL.GL_TRIANGLES
      case PrimitiveType.LINES     => GL.GL_LINES
    }
  }
}