package se.gigurra.renderer

case class Vertex(x: Float, y: Float, z: Float = 0, w: Float = 1) {
  def toArray() = Array(x, y, z, w)
}

object Vertex {
  def apply(src: Array[Float]) = new Vertex(src(0), src(1), if (src.length >= 3) src(2) else 0.0f)
}

object Vertices {
  def apply(vs: Vertex*) = vs.toArray.flatMap(_.toArray)
  def apply(vs: Iterable[Vertex]) = vs.toArray.flatMap(_.toArray)
}

object Colors {
  def apply(vs: Color*) = vs.toArray.flatMap(_.toArray)
  def apply(vs: Iterable[Color]) = vs.toArray.flatMap(_.toArray)
}

