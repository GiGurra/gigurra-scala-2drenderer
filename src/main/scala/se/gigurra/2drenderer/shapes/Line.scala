package se.gigurra.renderer.shapes

import se.gigurra.renderer.Model
import se.gigurra.renderer.Vertex
import se.gigurra.renderer.Color
import se.gigurra.renderer.Vertices

object Line {

  val length = 1.0f
  val halfLength = 0.5f * length

  def vertCentered(color: Color) = Model.lines(Vertices(Vertex(0.0f, -halfLength), Vertex(0.0f, halfLength)), color.mkArray(2))
  def horCentered(color: Color) = Model.lines(Vertices(Vertex(-halfLength, 0.0f), Vertex(halfLength, 0.0f)), color.mkArray(2))

  def vertUp(color: Color) = Model.lines(Vertices(Vertex(0.0f, 0.0f), Vertex(0.0f, length)), color.mkArray(2))
  def horRight(color: Color) = Model.lines(Vertices(Vertex(0.0f, 0.0f), Vertex(length, 0.0f)), color.mkArray(2))
}
