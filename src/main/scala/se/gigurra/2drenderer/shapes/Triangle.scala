package se.gigurra.renderer.shapes

import se.gigurra.renderer.Model
import se.gigurra.renderer.Vertex
import se.gigurra.renderer.Color
import se.gigurra.renderer.Vertices

object Triangle {

  val side = 1.0f
  val width = side
  val halfWidth = width * 0.5f
  val height = math.sqrt(width - halfWidth * halfWidth).toFloat
  val halfHeight = height * halfWidth

  private def vertImplTriangles(offsX: Float, offsY: Float, color: Color): Model = {
    Model.triangles(
      Vertices(
        Vertex(-halfWidth + offsX, -halfHeight + offsY),
        Vertex(halfWidth + offsX, -halfHeight + offsY),
        Vertex(0.0f + offsX, halfHeight + offsY)),
      color)
  }

  private def vertImplLines(offsX: Float, offsY: Float, color: Color): Model = {
    Model.lines(
      Vertices(
        Vertex(-halfWidth + offsX, -halfHeight + offsY), Vertex(halfWidth + offsX, -halfHeight + offsY),
        Vertex(halfWidth + offsX, -halfHeight + offsY), Vertex(0.0f + offsX, halfHeight + offsY),
        Vertex(0.0f + offsX, halfHeight + offsY), Vertex(-halfWidth + offsX, -halfHeight + offsY)),
      color)
  }

  private def hatImplLines(offsX: Float, offsY: Float, color: Color): Model = {
    Model.lines(
      Vertices(
        Vertex(-halfWidth + offsX, -halfHeight + offsY), Vertex(0.0f + offsX, halfHeight + offsY),
        Vertex(halfWidth + offsX, -halfHeight + offsY), Vertex(0.0f + offsX, halfHeight + offsY)),
      color)
  }

  def fillCentered(color: Color) = vertImplTriangles(0, 0, color)
  def lineCentered(color: Color) = vertImplLines(0, 0, color)

  def fillUp(color: Color) = vertImplTriangles(0, halfHeight, color)
  def lineUp(color: Color) = vertImplLines(0, halfHeight, color)

  def hatCentered(color: Color) = hatImplLines(0, 0.0f, color)
  def hatUp(color: Color) = hatImplLines(0, halfHeight, color)
}