package se.gigurra.renderer.glimpl.drawstrategy.multi

import javax.media.opengl.GL3ES3
import se.gigurra.renderer.Color
import se.gigurra.renderer.Transform
import se.gigurra.renderer.glimpl.GlIntegers
import se.gigurra.renderer.glimpl.GlShaderProgram
import se.gigurra.renderer.glimpl.drawstrategy.DrawStrategy
import se.gigurra.renderer.glimpl.megavbo.MegaVboModelDescriptor
import se.gigurra.renderer.glimpl.uniforms.SimpleUniform

class MultiDrawStrategy(_gl_init: GL3ES3, program: GlShaderProgram) extends DrawStrategy {

  val glInts = new GlIntegers(_gl_init)
  val transformUniform = new SimpleUniform(_gl_init, program, "uniform_Transformation")
  val colorScaleUniform = new SimpleUniform(_gl_init, program, "uniform_ColorScale")
  val batch = new SimpleDrawBatch(glInts.maxUniformMat4ArrayLen / 2)

  var _nBatchDraws = 0

  override final def draw(
      gl: GL3ES3, 
      model: MegaVboModelDescriptor, 
      colorScale: Color,
      transform: Transform) {
    if (!batch.canAdd(model))
      flush(gl)
    batch.add(model, transform, colorScale)
  }

  override final def startFrame(gl: GL3ES3) {}

  override final def flush(gl: GL3ES3) {
    if (batch.nonEmpty) {
      val gl3 = gl.getGL3
      batch.draw(gl3, colorScaleUniform, transformUniform)
      batch.reset()
      _nBatchDraws += 1
    }
  }

  override final def endFrame(gl: GL3ES3) {
    flush(gl)
    _nBatchDraws = 0
  }

  override final def nBatchDraws(): Int = {
    _nBatchDraws
  }

  override final def dispose(gl: GL3ES3) {
    //transformUbo.dispose(gl)
  }

}