package se.gigurra.renderer.shapes

import scala.collection.mutable.ArrayBuffer
import scalaxy.streams.optimize
import se.gigurra.renderer.Model
import se.gigurra.renderer.Vertex
import se.gigurra.renderer.Color
import se.gigurra.renderer.Vertices

object Arc {

  val radius = 0.5f

  sealed abstract class Type
  object Type {
    case object OPEN extends Type
    case object CLOSED extends Type
    case object CONE extends Type
  }

  private def mkArcVertices(angDeg: Float, nSegments: Int): Seq[Vertex] = {
    val ang0Rads = (90.0f - angDeg * 0.5f).toRadians.toFloat
    val dAngRads = angDeg.toRadians / nSegments
    optimize(for (i <- 0 to nSegments) yield {
      val angRads = ang0Rads + dAngRads * i
      Vertex(math.cos(angRads).toFloat * radius, math.sin(angRads).toFloat * radius)
    })
  }

  def lines(angDeg: Float, nSegments: Int, typ: Arc.Type, color: Color): Model = {
    val arcVertices = mkArcVertices(angDeg, nSegments)

    val vertices = new ArrayBuffer[Vertex](arcVertices.size * 2 + 12)
    optimize(for (i <- 0 until nSegments) {
      vertices += arcVertices(i + 0)
      vertices += arcVertices(i + 1)
    })

    typ match {
      case Type.OPEN =>
      case Type.CLOSED =>
        vertices += arcVertices.head
        vertices += arcVertices.last
      case Type.CONE =>
        vertices += arcVertices.head
        vertices += Vertex(0, 0)
        vertices += arcVertices.last
        vertices += Vertex(0, 0)
    }

    Model.lines(Vertices(vertices), color.mkArray(vertices.size))
  }

  def lines(angDeg: Float, nSegments: Int, color: Color): Model = lines(angDeg, nSegments, Arc.Type.OPEN, color)

  def fill(angDeg: Float, nSegments: Int, typ: Arc.Type, color: Color): Model = {
    val arcVertices = mkArcVertices(angDeg, nSegments)

    val centerPt = typ match {
      case Type.OPEN   => throw new RuntimeException(s"Invalid arc type: $typ")
      case Type.CLOSED => arcVertices.head
      case Type.CONE   => Vertex(0, 0)
    }

    val vertices = new ArrayBuffer[Vertex](nSegments * 3 + 12)
    optimize(for (i <- 0 until nSegments) {
      vertices += centerPt
      vertices += arcVertices(i + 0)
      vertices += arcVertices(i + 1)
    })

    Model.triangles(Vertices(vertices), color.mkArray(vertices.size))
  }

  def fill(angDeg: Float, nSegments: Int, color: Color): Model = {
    fill(angDeg, nSegments, Type.CONE, color)
  }
}