package se.gigurra.renderer.glimpl.drawstrategy

import javax.media.opengl.GL3ES3
import se.gigurra.renderer.Color
import se.gigurra.renderer.Transform
import se.gigurra.renderer.glimpl.megavbo.MegaVboModelDescriptor

trait DrawStrategy {
  def draw(gl: GL3ES3, model: MegaVboModelDescriptor, colorScale: Color, transform: Transform)
  def flush(gl: GL3ES3)
  def startFrame(gl: GL3ES3)
  def endFrame(gl: GL3ES3)
  def dispose(gl: GL3ES3)
  def nBatchDraws(): Int = 0
}