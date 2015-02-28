package se.gigurra.renderer.glimpl.vbos

import java.nio.FloatBuffer
import javax.media.opengl.GL
import javax.media.opengl.GL2ES3
import javax.media.opengl.GL3ES3
import se.gigurra.renderer.glimpl.GlIntegers
import se.gigurra.renderer.glimpl.GlShaderProgram

class MatrixUniformBuffer(
    _gl_init: GL3ES3,
    val program: GlShaderProgram,
    val binRangeAlignBytes: Int, 
    val nMaxMatrices: Int)
  extends Vbo(_gl_init, GL2ES3.GL_UNIFORM_BUFFER, 64 * nMaxMatrices, GL.GL_DYNAMIC_DRAW, GL.GL_FLOAT) {

  val (blockIndex, blockBinding) = program.bindUniformBlockToBuffer(_gl_init, "uniform_TransformationBlock", id)

  def uploadMatrices(gl: GL3ES3, nMatrices: Int, data: Array[Float], doBind: Boolean) {
    uploadBytes(gl, 0, 64 * nMatrices, FloatBuffer.wrap(data), doBind)
  }

  def uploadMatrices(gl: GL3ES3, nMatrices: Int, data: Array[Float], floatOffs: Int, doBind: Boolean) {
    uploadBytes(gl, 0, 64 * nMatrices, FloatBuffer.wrap(data, floatOffs, nMatrices * 16), doBind)
  }

  def uploadMatrices(gl: GL3ES3, nMatrices: Int, data: FloatBuffer, doBind: Boolean) {
    uploadBytes(gl, 0, 64 * nMatrices, data, doBind)
  }

  def bindFromMatrixNumber(gl: GL3ES3, i: Int, n: Int) {
    gl.glBindBufferRange(GL2ES3.GL_UNIFORM_BUFFER, blockIndex, id, i * 64, 64 * n)
  }

}