package se.gigurra.renderer.glimpl.vbos

import java.nio.FloatBuffer

import javax.media.opengl.GL
import javax.media.opengl.GL3ES3
import se.gigurra.renderer.glimpl.GlShaderProgram

class FloatAttribVbo(
    _gl_init: GL3ES3, 
    val program: GlShaderProgram,
    val vertexCapacity: Int, 
    val shaderAttribName: String)
  extends Vbo(_gl_init, GL.GL_ARRAY_BUFFER, vertexCapacity * 4 * 4, GL.GL_DYNAMIC_DRAW, GL.GL_FLOAT)
  with HasVertexAttrib {

  val componentsPerVertex = 4

  def uploadFloats(gl: GL3ES3, tgtFloatOffset: Int, nFloats: Int, data: FloatBuffer, doBind: Boolean) {
    uploadBytes(gl, tgtFloatOffset * 4, nFloats * 4, data, doBind)
  }

  def uploadFloats(gl: GL3ES3, tgtFloatOffset: Int, data: Array[Float], doBind: Boolean) {
    uploadFloats(gl, tgtFloatOffset, data.length, FloatBuffer.wrap(data), doBind)
  }

}