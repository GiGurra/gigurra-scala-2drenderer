package se.gigurra.renderer

class Model(val primType: PrimitiveType, val vertices: Array[Float], val colors: Array[Float]) {
  def this(primType: PrimitiveType, vertices: Array[Float], color: Color) = this(primType, vertices, color.mkArray(vertices.length / 4))

  val vertexCount = vertices.length / 4

  def transform(t: Transform): Model = {
    val newVertices = vertices.clone
    Mat4x4.multMatrixVector(t.array, newVertices)
    Model(this, newVertices)
  }
  def transform(f: Transform => Transform): Model = transform(f(Transform()))

}

object Model {
  def apply(oldModel: Model, newColor: Color): Model = apply(oldModel.primType, oldModel.vertices, newColor.mkArray(oldModel.vertexCount))
  def apply(oldModel: Model, newVertices: Array[Float]): Model = apply(oldModel.primType, newVertices, oldModel.colors)
  def apply(primType: PrimitiveType, vertices: Array[Float], colors: Array[Float]) = new Model(primType, vertices, colors)
  def lines(vertices: Array[Float], colors: Array[Float]) = new Model(PrimitiveType.LINES, vertices, colors)
  def triangles(vertices: Array[Float], colors: Array[Float]) = new Model(PrimitiveType.TRIANGLES, vertices, colors)
  def lines(vertices: Array[Float], color: Color) = new Model(PrimitiveType.LINES, vertices, color)
  def triangles(vertices: Array[Float], color: Color) = new Model(PrimitiveType.TRIANGLES, vertices, color)
  def empty() = new Model(PrimitiveType.TRIANGLES, Array[Float](), Array[Float]())
}
