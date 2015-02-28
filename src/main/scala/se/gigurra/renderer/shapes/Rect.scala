package se.gigurra.renderer.shapes

import se.gigurra.renderer.Model
import se.gigurra.renderer.Vertex
import se.gigurra.renderer.Vertices
import se.gigurra.renderer.Color

object Rect {

  private def fillImpl(width: Float, height: Float, left: Float, down: Float, color: Color): Model = {
    val right = left + width
    val up = down + height
    Model.triangles(
      Vertices(
        Vertex(right, up), Vertex(left, up),
        Vertex(left, down), Vertex(left, down),
        Vertex(right, down), Vertex(right, up)),
      color)
  }

  private def lineImpl(width: Float, height: Float, left: Float, down: Float, color: Color): Model = {
    val right = left + width
    val up = down + height
    Model.lines(
      Vertices(
        Vertex(left, down), Vertex(right, down),
        Vertex(right, down), Vertex(right, up),
        Vertex(right, up), Vertex(left, up),
        Vertex(left, up), Vertex(left, down)),
      color)
  }

  def fillCentered(width: Float, height: Float, color: Color) = fillImpl(width, height, -width / 2, -height / 2, color)
  def lineCentered(width: Float, height: Float, color: Color) = lineImpl(width, height, -width / 2, -height / 2, color)

  def fillUp(width: Float, height: Float, color: Color) = fillImpl(width, height, -width / 2, 0, color)
  def lineUp(width: Float, height: Float, color: Color) = lineImpl(width, height, -width / 2, 0, color)

}
