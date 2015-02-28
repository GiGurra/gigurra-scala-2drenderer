package se.gigurra.renderer.util

import scalaxy.streams.optimize

object CopyUtil {

  final def floats(src: Array[Float], srcOffs: Int, trg: Array[Float], trgOffs: Int, n: Int) {
    System.arraycopy(src, srcOffs, trg, trgOffs, n)
  }

  final def floats(src: Array[Float], trg: Array[Float]) {
    System.arraycopy(src, 0, trg, 0, src.length)
  }
  
}