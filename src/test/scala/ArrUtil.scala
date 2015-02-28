import org.junit.Test

import se.gigurra.util.ArrayUtil

class ArrUtil {
  @Test
  def test() {
    def mkFloatArray(n: Int) = (0 until n).map(_ => math.random.toFloat).toArray
    val a1 = mkFloatArray(4)
    val a2 = mkFloatArray(4)
    val a3 = mkFloatArray(4)
    val arrayList = Array(a1, a2, a3)

    val fmRes = arrayList.flatten.toSeq
    val meRes = ArrayUtil.merge(arrayList).toSeq 
    assert(fmRes == meRes)
  }
}