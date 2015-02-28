package se.gigurra.renderer.glimpl.megavbo

import javax.media.opengl.GL3ES3
import se.gigurra.renderer.Model
import se.gigurra.renderer.glimpl.GlShaderProgram
import se.gigurra.renderer.glimpl.vbos.FloatAttribVbo

class MegaVboBufferSet(
  _gl_init: GL3ES3,
  val program: GlShaderProgram,
  val nVerticesMax: Int) {

  val vertices = new FloatAttribVbo(_gl_init, program, nVerticesMax, "attribute_Position")
  val colors = new FloatAttribVbo(_gl_init, program, nVerticesMax, "attribute_Color")

  private var _nVertices = 0

  def startFrame(gl: GL3ES3) {
    vertices.enableAttrib(gl)
    colors.enableAttrib(gl)
  }

  // Uploading a new model
  def add(gl: GL3ES3, model: Model): MegaVboModelDescriptor = {

    val out = new MegaVboModelDescriptor(model, nVertices)

    vertices.uploadFloats(gl, out.floatOffset, model.vertices, true)
    colors.uploadFloats(gl, out.floatOffset, model.colors, true)

    _nVertices += model.vertexCount

    out
  }

  // If cpu data changed, update gpu
  def update(model: Model, onGpu: MegaVboModelDescriptor) {
    // If vertices changed, then size may have changed, must check lots of things here..
    // If just color changed, we know there is no size change, so things become easier..
    ???
  }

  // Called after/before a frame is drawn, in a double buffered
  // scenario to compact memory
  def add(previousSet: MegaVboBufferSet) {
    ???
  }

  def nVertices(): Int = {
    _nVertices
  }

  def reset() {
    _nVertices = 0
  }

  def dispose(gl: GL3ES3) {
    vertices.dispose(gl)
    colors.dispose(gl)
  }

}