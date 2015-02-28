package se.gigurra.renderer.glimpl.vbos

import javax.media.opengl.GL3ES3
import se.gigurra.renderer.glimpl.GlShaderProgram

trait HasVertexAttrib { _: Vbo =>

  val componentsPerVertex: Int
  val shaderAttribName: String
  val program: GlShaderProgram
  val shaderAttribLocation = program.getAttribLocation(_gl_init, shaderAttribName)

  def enableAttrib(gl: GL3ES3) {
    bind(gl)
    gl.glVertexAttribPointer(shaderAttribLocation, componentsPerVertex, componentType, false, 0, 0)
    gl.glEnableVertexAttribArray(shaderAttribLocation)
  }

}