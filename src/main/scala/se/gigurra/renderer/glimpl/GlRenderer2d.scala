package se.gigurra.renderer.glimpl

import javax.media.opengl.GL3ES3
import se.gigurra.renderer.Model
import se.gigurra.renderer.Widget
import se.gigurra.renderer.glimpl.drawstrategy.multi.MultiDrawStrategy
import se.gigurra.renderer.glimpl.drawstrategy.single.SingleDrawStrategy
import se.gigurra.renderer.glimpl.megavbo.MegaVbo
import se.gigurra.renderer.glimpl.megavbo.MegaVboModelDescriptor
import se.gigurra.renderer.Color

class GlRenderer2d(_init_gl: GL3ES3) extends GlRenderer(_init_gl) {

  private val megaVbo = new MegaVbo(gl, getShaderProgram)
  private val drawStrategy =
    if (glslCfg.supportsMultiDraw(_init_gl))
      new MultiDrawStrategy(_init_gl, getShaderProgram)
    else
      new SingleDrawStrategy(_init_gl, getShaderProgram)

  private var _nModelsDrawn = 0
  private var _nModelDrawCalls = 0

  ///////////////////////////////////////////////////

  override final def draw(widget: Widget[_], isRoot: Boolean) {

    if (isRoot) {
      megaVbo.startFrame(gl)
      drawStrategy.startFrame(gl)
    }

    super.draw(widget, isRoot)

    if (isRoot) {
      drawStrategy.endFrame(gl)
      megaVbo.endFrame(gl)
      _nModelsDrawn = 0
      _nModelDrawCalls = 0
    }

  }

  override final def flush() {
    drawStrategy.flush(gl)
  }

  override final def draw(model: Model) {
    drawStrategy.draw(gl, megaVbo.getModel(gl, model), colorScale, transform)
    _nModelsDrawn += 1
  }

  override final def dispose() {
    drawStrategy.dispose(gl)
    megaVbo.dispose(gl)
    super.dispose()
  }

  override final def nVerticesQueued() = 0
  override final def nModelsDrawn() = _nModelsDrawn
  override final def nBatchDrawCalls() = drawStrategy.nBatchDraws

}