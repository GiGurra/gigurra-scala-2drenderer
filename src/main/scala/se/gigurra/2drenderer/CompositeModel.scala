package se.gigurra.renderer

import scala.language.implicitConversions

import se.gigurra.util.ArrayUtil

class CompositeModel private (private val models: Map[PrimitiveType, Model] = Map()) extends Widget[CompositeModel] {
  private def this(model: Model) = this(Map(model.primType -> model))

  override def doDraw(renderer: Renderer, isRoot: Boolean) {
    models.values.foreach(renderer.draw)
  }

  def +(m: CompositeModel): CompositeModel = {
    val grouped = (models.values ++ m.models.values).groupBy(_.primType)
    new CompositeModel(grouped.map(x => (x._1, merge(x._2))))
  }

  def copy() = new CompositeModel(models)
  def copy(color: Color) = new CompositeModel(models.map(pair => (pair._1, Model(pair._2, color))))

  def +(m: Model): CompositeModel = this + new CompositeModel(m)

  def flatten(primType: PrimitiveType) = models.getOrElse(primType, Model.empty)
  def flattenTriangles() = flatten(PrimitiveType.TRIANGLES)
  def flattenTines() = flatten(PrimitiveType.LINES)

  private def merge(models: Iterable[Model]): Model = {
    new Model(models.head.primType, ArrayUtil.merge(models.map(_.vertices)), ArrayUtil.merge(models.map(_.colors)))
  }

}

object CompositeModel {
  implicit def toCompositeModel(m: Model) = new CompositeModel(m)
}
