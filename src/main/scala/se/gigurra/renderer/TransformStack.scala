package se.gigurra.renderer

import scala.language.implicitConversions

object TransformStack {
  implicit def toTransform(stack: TransformStack): Transform = stack.top
}

class TransformStack(m: Mat4x4 = new Mat4x4) {
  val top = new Transform(m)
  private var modCountBeforePush = top.modCount
  private val stack = new Mat4x4Stack(top = m)

  final def pushPopTransform[A](f: => A): TransformStack = {
    push()
    f
    pop()
    this
  }

  final def push() {
    modCountBeforePush = top.modCount
    stack.push()
  }

  final def pop() {
    stack.pop()
    if (modCountBeforePush != top.modCount) top.mod()
  }

}