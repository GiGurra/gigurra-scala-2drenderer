package se.gigurra.renderer.glimpl.drawstrategy.single

import javax.media.opengl.GL3ES3
import se.gigurra.renderer.Transform
import se.gigurra.renderer.glimpl.GlShaderProgram
import se.gigurra.renderer.glimpl.drawstrategy.DrawStrategy
import se.gigurra.renderer.glimpl.megavbo.MegaVboModelDescriptor
import se.gigurra.renderer.glimpl.uniforms.SimpleUniform
import se.gigurra.renderer.Color

class SingleDrawStrategy(_gl_init: GL3ES3, program: GlShaderProgram) extends DrawStrategy {

  val colorScaleUniform = new SimpleUniform(_gl_init, program, "uniform_ColorScale")
  val transformUniform = new SimpleUniform(_gl_init, program, "uniform_Transformation")

  override def draw(gl: GL3ES3, model: MegaVboModelDescriptor, colorScale: Color, transform: Transform) {
    colorScaleUniform.setVector(gl, colorScale.array)
    transformUniform.setMatrix(gl, transform.m)
    model.drawNow(gl)
  }

  override def startFrame(gl: GL3ES3) {}

  override def endFrame(gl: GL3ES3) {}

  override def dispose(gl: GL3ES3) {}
  
  override def flush(gl: GL3ES3) {}

}