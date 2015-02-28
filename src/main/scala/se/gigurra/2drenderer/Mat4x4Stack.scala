package se.gigurra.renderer

import se.gigurra.renderer.util.CopyUtil

class Mat4x4Stack(val nLevels: Int = 32, val top: Mat4x4 = new Mat4x4) {
  private val stackData = new Array[Float](nLevels * 16)
  private var i = -1

  final def push() {
    i += 1
    CopyUtil.floats(top.array, 0, stackData, i * 16, 16)
    //CopyUtil.floats16(top.array, 0, stackData, i * 16)
  }

  final def pop() {
    CopyUtil.floats(stackData, i * 16, top.array, 0, 16)
    //CopyUtil.floats16(stackData, i * 16, top.array, 0)
    i -= 1
  }

}