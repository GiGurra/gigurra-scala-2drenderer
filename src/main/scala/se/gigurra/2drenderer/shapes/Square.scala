package se.gigurra.renderer.shapes

import se.gigurra.renderer.Color

object Square {

  val side = 1.0f
  val halfSide = 0.5f * side
  val height = side
  val width = side

  def fillCentered(color: Color) = Rect.fillCentered(width, height, color)
  def lineCentered(color: Color) = Rect.lineCentered(width, height, color)

  def fillUp(color: Color) = Rect.fillUp(width, height, color)
  def lineUp(color: Color) = Rect.fillUp(width, height, color)

}
