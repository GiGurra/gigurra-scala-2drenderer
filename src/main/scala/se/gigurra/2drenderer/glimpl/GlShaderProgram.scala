package se.gigurra.renderer.glimpl

import java.util.concurrent.atomic.AtomicInteger

import javax.media.opengl.GL2ES3
import javax.media.opengl.GL3ES3

case class GlShaderProgram(val program: Int, val shaders: Seq[Int]) {

  def use(gl: GL3ES3): GlShaderProgram = {
    gl.glUseProgram(program)
    this
  }

  def getUniformBlockIndex(gl: GL3ES3, name: String): Int = {
    val out = gl.glGetUniformBlockIndex(program, name)
    if (out < 0)
      throw new RuntimeException(s"Could not find block index of UBO '$name'' (errCode: ${gl.glGetError()})")
    out
  }

  def getUniformBlockBinding(gl: GL3ES3, blockIndex: Int): Int = {
    val out = nBlockBindings.getAndIncrement()
    gl.glUniformBlockBinding(program, blockIndex, out)
    out
  }

  def bindUniformBlockToBuffer(gl: GL3ES3, binding: Int, bufferId: Int) {
    gl.glBindBufferBase(GL2ES3.GL_UNIFORM_BUFFER, binding, bufferId)
  }

  def bindUniformBlockToBuffer(gl: GL3ES3, blockName: String, bufferId: Int): (Int, Int) = {
    val blockIndex = getUniformBlockIndex(gl, blockName)
    val blockBinding = getUniformBlockBinding(gl, blockIndex)
    bindUniformBlockToBuffer(gl, blockBinding, bufferId)
    (blockIndex, blockBinding)
  }

  def getAttribLocation(gl: GL3ES3, name: String): Int = {
    val out = gl.glGetAttribLocation(program, name)
    if (out < 0)
      throw new RuntimeException(s"Could not find location of attribute '$name'' (errCode: ${gl.glGetError()})")
    out
  }
  
  def getUniformLocation(gl: GL3ES3, name: String): Int = {
    val out = gl.glGetUniformLocation(program, name)
    if (out < 0)
      throw new RuntimeException(s"Could not find location of uniform '$name'' (errCode: ${gl.glGetError()})")
    out
  }

  private val nBlockBindings = new AtomicInteger(0)
}
