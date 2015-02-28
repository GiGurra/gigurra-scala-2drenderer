package se.gigurra.renderer

trait Widget[T <: Widget[T]] { self: T =>

  private var _transform: Transform = null

  final def draw(renderer: Renderer, isRoot: Boolean) {
    if (_transform != null) {
      renderer.push()
      renderer.mult(_transform)
      doDraw(renderer, isRoot)
      renderer.pop()
    } else {
      doDraw(renderer, isRoot)
    }
  }

  def transform[AnyReturnType](f: Transform => AnyReturnType): T = {
    if (_transform == null)
      _transform = new Transform
    f(_transform)
    this
  }

  def doDraw(renderer: Renderer, isRoot: Boolean) {}

}