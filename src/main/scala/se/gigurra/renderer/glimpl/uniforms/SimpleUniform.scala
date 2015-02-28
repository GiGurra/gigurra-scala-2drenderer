package se.gigurra.renderer.glimpl.uniforms

import java.nio.FloatBuffer

import javax.media.opengl.GL3ES3
import se.gigurra.renderer.Mat4x4
import se.gigurra.renderer.glimpl.GlShaderProgram

class SimpleUniform(_gl_init: GL3ES3, program: GlShaderProgram, name: String) {

  val location = program.getUniformLocation(_gl_init, name)

  def setInt(gl: GL3ES3, value: Int) {
    gl.glUniform1i(location, value)
  }

  def setFloat(gl: GL3ES3, value: Float) {
    gl.glUniform1f(location, value)
  }

  def setMatrix(gl: GL3ES3, m: Mat4x4) {
    setMatrix(gl, m.buffer)
  }

  def setMatrix(gl: GL3ES3, m: FloatBuffer) {
    gl.glUniformMatrix4fv(location, 1, false, m)
  }

  def setMatrix(gl: GL3ES3, m: Array[Float], nMatrixOffs: Int) {
    gl.glUniformMatrix4fv(location, 1, false, m, nMatrixOffs * 16)
  }

  def setMatrices(gl: GL3ES3, matrices: FloatBuffer, nMatrices: Int) {
    gl.glUniformMatrix4fv(location, nMatrices, false, matrices)
  }

  def setMatrices(gl: GL3ES3, matrices: Array[Float], nMatrixOffs: Int, nMatrices: Int) {
    gl.glUniformMatrix4fv(location, nMatrices, false, matrices, nMatrixOffs * 16)
  }

  def setVector(gl: GL3ES3, vector: Array[Float]) {
    gl.glUniform4fv(location, 1, vector, 0)
  }
  
  def setVectors(gl: GL3ES3, vectors: FloatBuffer, nVectors: Int) {
    gl.glUniform4fv(location, nVectors, vectors)
  }

}
