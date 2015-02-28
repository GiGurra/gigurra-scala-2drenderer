import org.junit.Test
import scalaxy.streams.optimize
import se.gigurra.renderer.Mat4x4
import se.gigurra.util.MeasureTime
import se.gigurra.renderer.Mat44
import javamat.J44

object Demo2 {

  @Test
  def test() {

    val a = new Array[Float](16)
    val b = new Array[Float](16)
    val r = new Array[Float](16)
    
    val a44 = new Mat44
    val b44 = new Mat44
    val r44 = new Mat44

    val t0 = System.nanoTime() / 1e9
    val n = 100000000

    println("Warming up")

    def time() = System.nanoTime() / 1e9
    
//    val j44m5 = J44.method544(a, b, r)

    while (true) {

      val t0 = time
      var i = 0
      while (i < n) {
        //Mat4x4.multMatrix(a, b)
        Mat44.mult(a44, b44, r44)
        //J44.method544(a, b, r)
        //J44.method544(a, b, r)
        i += 1
      }
      val t1 = time
      val dt = t1 - t0
      val matsPerSec = (n.toDouble / dt / 1e6).toLong
      println(s"100 M mats in $dt seconds")
    }
  }

  def main(args: Array[String]) {
    test()

  }
}