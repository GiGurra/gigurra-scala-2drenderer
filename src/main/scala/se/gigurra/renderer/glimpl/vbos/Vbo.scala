package se.gigurra.renderer.glimpl.vbos

import java.nio.Buffer

import javax.media.opengl.GL3ES3
import se.gigurra.util.Mutate.Mutable

class Vbo(
  val _gl_init: GL3ES3,
  val target: Int, //  e.g. GL.GL_ARRAY_BUFFER
  val byteCapacity: Int, // The size in bytes of the entire vbo
  val usage: Int, //  e.g. GL.GL_DYNAMIC_DRAW
  val componentType: Int // e.g. GL.GL_FLOAT
  ) {

  val id = Array(0).mutate { _gl_init.glGenBuffers(1, _, 0) }(0)
  _gl_init.glBindBuffer(target, id)
  _gl_init.glBufferData(target, byteCapacity, null, usage)

  ///////////////////////////////////////////////////////////////

  def bind(gl: GL3ES3) {
    gl.glBindBuffer(target, id)
  }

  def uploadBytes(gl: GL3ES3, tgtOffs: Int, nBytes: Int, data: Buffer, doBind: Boolean) {
    if (tgtOffs + nBytes > byteCapacity)
      throw new RuntimeException("Trying to upload data past size of Vbo!")
    if (doBind)
      bind(gl)
    gl.glBufferSubData(target, tgtOffs, nBytes, data)
  }

  def dispose(gl: GL3ES3) {
    gl.glDeleteBuffers(1, Array(id), 0)
  }

}