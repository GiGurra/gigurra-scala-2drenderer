package se.gigurra.renderer.shapes

import se.gigurra.renderer.Model
import se.gigurra.renderer.Color

object Circle {

  def fill(nSegments: Int, color: Color): Model = Arc.fill(360, nSegments, color)
  def lines(nSegments: Int, color: Color): Model = Arc.lines(360, nSegments, color)
}