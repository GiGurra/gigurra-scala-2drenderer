package se.gigurra.renderer

import java.nio.FloatBuffer
import net.jodk.lang.FastMath
import net.jodk.lang.FastMath.SinCos
import scalaxy.streams.optimize
import scalaxy.streams.optimize
import se.gigurra.renderer.util.CopyUtil

class Mat4x4(loadIdentity: Boolean = true) {
  private val temp = new Array[Float](16)
  private val sinCosHolder = new SinCos

  val array = new Array[Float](16)
  val buffer = FloatBuffer.wrap(array)

  if (loadIdentity) loadIDentity()

  ////////////////////////////////////////////////////////////////////

  final def loadIDentity() { Mat4x4.makeIdentity(array) }

  final def set(m: Mat4x4) { set(m.array) }
  final def set(values: Array[Float]) { System.arraycopy(values, 0, array, 0, 16) }
  final def set(values: Array[Float], offs: Int) { System.arraycopy(values, offs, array, 0, 16) }
  final def set(fb: FloatBuffer) { set(fb.array, fb.position) }

  final def mult(m: Array[Float]) { Mat4x4.multMatrix(array, m) }
 // final def mult(m: Array[Float], offs: Int) { Mat4x4.multMatrix(array, m, offs) }

  final def translate(x: Float, y: Float, z: Float) { mult(Mat4x4.makeTranslation(temp, x, y, z)) }
  final def scale(x: Float, y: Float, z: Float) { mult(Mat4x4.makeScale(temp, x, y, z)) }
  final def rotate(ang_deg: Float, x: Float, y: Float, z: Float) { mult(Mat4x4.makeRotation(sinCosHolder, temp, ang_deg * Mat4x4.toRadsFast, x, y, z)) }

  final def ortho(left: Float, right: Float, bottom: Float, top: Float, zNear: Float, zFar: Float) { mult(Mat4x4.makeOrtho(temp, left, right, bottom, top, zNear, zFar)) }
  final def perspective(fovy_deg: Float, aspect: Float, zNear: Float, zFar: Float) { mult(Mat4x4.makePerspective(temp, fovy_deg * Mat4x4.toRadsFast, aspect, zNear, zFar)) }

  final def copy(): Mat4x4 = {
    val out = new Mat4x4(false)
    CopyUtil.floats(array, out.array)
    out
  }
  
}

object Mat4x4 {

  val PI = 3.14159265358979323846f
  val toRadsFast = PI / 180.0f

  /**
   * This turns out to be quite the performance hog...
   * Unfortunately I haven't been able to find something a lot faster... wonder if sse kicks in here :/
   *
   * Tried three different unrolled approaches, but the Jog-amp one seems to be the fastest
   */
  final def multMatrix(a: Array[Float], b: Array[Float]) {

    // The fastest (so far)
    val b00 = b(0 + 0 * 4)
    val b10 = b(1 + 0 * 4)
    val b20 = b(2 + 0 * 4)
    val b30 = b(3 + 0 * 4)
    val b01 = b(0 + 1 * 4)
    val b11 = b(1 + 1 * 4)
    val b21 = b(2 + 1 * 4)
    val b31 = b(3 + 1 * 4)
    val b02 = b(0 + 2 * 4)
    val b12 = b(1 + 2 * 4)
    val b22 = b(2 + 2 * 4)
    val b32 = b(3 + 2 * 4)
    val b03 = b(0 + 3 * 4)
    val b13 = b(1 + 3 * 4)
    val b23 = b(2 + 3 * 4)
    val b33 = b(3 + 3 * 4)

    var ai0 = a(0 * 4) // row-0 of a
    var ai1 = a(1 * 4)
    var ai2 = a(2 * 4)
    var ai3 = a(3 * 4)
    a(0 * 4) = ai0 * b00 + ai1 * b10 + ai2 * b20 + ai3 * b30
    a(1 * 4) = ai0 * b01 + ai1 * b11 + ai2 * b21 + ai3 * b31
    a(2 * 4) = ai0 * b02 + ai1 * b12 + ai2 * b22 + ai3 * b32
    a(3 * 4) = ai0 * b03 + ai1 * b13 + ai2 * b23 + ai3 * b33

    ai0 = a(1 + 0 * 4) // row-1 of a
    ai1 = a(1 + 1 * 4)
    ai2 = a(1 + 2 * 4)
    ai3 = a(1 + 3 * 4)
    a(1 + 0 * 4) = ai0 * b00 + ai1 * b10 + ai2 * b20 + ai3 * b30
    a(1 + 1 * 4) = ai0 * b01 + ai1 * b11 + ai2 * b21 + ai3 * b31
    a(1 + 2 * 4) = ai0 * b02 + ai1 * b12 + ai2 * b22 + ai3 * b32
    a(1 + 3 * 4) = ai0 * b03 + ai1 * b13 + ai2 * b23 + ai3 * b33

    ai0 = a(2 + 0 * 4) // row-2 of a
    ai1 = a(2 + 1 * 4)
    ai2 = a(2 + 2 * 4)
    ai3 = a(2 + 3 * 4)
    a(2 + 0 * 4) = ai0 * b00 + ai1 * b10 + ai2 * b20 + ai3 * b30
    a(2 + 1 * 4) = ai0 * b01 + ai1 * b11 + ai2 * b21 + ai3 * b31
    a(2 + 2 * 4) = ai0 * b02 + ai1 * b12 + ai2 * b22 + ai3 * b32
    a(2 + 3 * 4) = ai0 * b03 + ai1 * b13 + ai2 * b23 + ai3 * b33

    ai0 = a(3 + 0 * 4) // row-3 of a
    ai1 = a(3 + 1 * 4)
    ai2 = a(3 + 2 * 4)
    ai3 = a(3 + 3 * 4)
    a(3 + 0 * 4) = ai0 * b00 + ai1 * b10 + ai2 * b20 + ai3 * b30
    a(3 + 1 * 4) = ai0 * b01 + ai1 * b11 + ai2 * b21 + ai3 * b31
    a(3 + 2 * 4) = ai0 * b02 + ai1 * b12 + ai2 * b22 + ai3 * b32
    a(3 + 3 * 4) = ai0 * b03 + ai1 * b13 + ai2 * b23 + ai3 * b33

  }

  final def makeIdentity(m: Array[Float]) {
    m(0 + 4 * 0) = 1f
    m(1 + 4 * 0) = 0f
    m(2 + 4 * 0) = 0f
    m(3 + 4 * 0) = 0f
    m(0 + 4 * 1) = 0f
    m(1 + 4 * 1) = 1f
    m(2 + 4 * 1) = 0f
    m(3 + 4 * 1) = 0f
    m(0 + 4 * 2) = 0f
    m(1 + 4 * 2) = 0f
    m(2 + 4 * 2) = 1f
    m(3 + 4 * 2) = 0f
    m(0 + 4 * 3) = 0f
    m(1 + 4 * 3) = 0f
    m(2 + 4 * 3) = 0f
    m(3 + 4 * 3) = 1f
  }

  final def makeTranslation(m: Array[Float], tx: Float, ty: Float, tz: Float): Array[Float] = {
    m(0 + 4 * 0) = 1f
    m(1 + 4 * 0) = 0f
    m(2 + 4 * 0) = 0f
    m(3 + 4 * 0) = 0f
    m(0 + 4 * 1) = 0f
    m(1 + 4 * 1) = 1f
    m(2 + 4 * 1) = 0f
    m(3 + 4 * 1) = 0f
    m(0 + 4 * 2) = 0f
    m(1 + 4 * 2) = 0f
    m(2 + 4 * 2) = 1f
    m(3 + 4 * 2) = 0f
    m(0 + 4 * 3) = tx
    m(1 + 4 * 3) = ty
    m(2 + 4 * 3) = tz
    m(3 + 4 * 3) = 1f
    m
  }

  final def makeScale(m: Array[Float], sx: Float, sy: Float, sz: Float): Array[Float] = {
    m(0 + 4 * 0) = sx
    m(1 + 4 * 0) = 0f
    m(2 + 4 * 0) = 0f
    m(3 + 4 * 0) = 0f
    m(0 + 4 * 1) = 0f
    m(1 + 4 * 1) = sy
    m(2 + 4 * 1) = 0f
    m(3 + 4 * 1) = 0f
    m(0 + 4 * 2) = 0f
    m(1 + 4 * 2) = 0f
    m(2 + 4 * 2) = sz
    m(3 + 4 * 2) = 0f
    m(0 + 4 * 3) = 0f
    m(1 + 4 * 3) = 0f
    m(2 + 4 * 3) = 0f
    m(3 + 4 * 3) = 1f
    m
  }

  final def makeRotation(sc: SinCos, m: Array[Float], angrad: Float, x: Float, y: Float, z: Float): Array[Float] = {
    FastMath.sinAndCosDcsRemote(angrad, sc)
    val ic = 1.0f - sc.c
    val xy = x * y
    val xz = x * z
    val xs = x * sc.s
    val ys = y * sc.s
    val yz = y * z
    val zs = z * sc.s
    m(0 + 0 * 4) = x * x * ic + sc.c
    m(1 + 0 * 4) = xy * ic + zs
    m(2 + 0 * 4) = xz * ic - ys
    m(3 + 0 * 4) = 0
    m(0 + 1 * 4) = xy * ic - zs
    m(1 + 1 * 4) = y * y * ic + sc.c
    m(2 + 1 * 4) = yz * ic + xs
    m(3 + 1 * 4) = 0
    m(0 + 2 * 4) = xz * ic + ys
    m(1 + 2 * 4) = yz * ic - xs
    m(2 + 2 * 4) = z * z * ic + sc.c
    m(3 + 2 * 4) = 0
    m(0 + 3 * 4) = 0f
    m(1 + 3 * 4) = 0f
    m(2 + 3 * 4) = 0f
    m(3 + 3 * 4) = 1f
    m
  }

  final def makeOrtho(m: Array[Float], left: Float, right: Float, bottom: Float, top: Float, zNear: Float, zFar: Float): Array[Float] = {
    makeIdentity(m)
    val dx = right - left
    val dy = top - bottom
    val dz = zFar - zNear
    val tx = -1.0f * (right + left) / dx
    val ty = -1.0f * (top + bottom) / dy
    val tz = -1.0f * (zFar + zNear) / dz

    m(0 + 4 * 0) = 2.0f / dx

    m(1 + 4 * 1) = 2.0f / dy

    m(2 + 4 * 2) = -2.0f / dz

    m(0 + 4 * 3) = tx
    m(1 + 4 * 3) = ty
    m(2 + 4 * 3) = tz
    m(3 + 4 * 3) = 1f

    m
  }

  final def makeFrustum(m: Array[Float], left: Float, right: Float, bottom: Float, top: Float, zNear: Float, zFar: Float): Array[Float] = {
    if (zNear <= 0.0f || zFar < 0.0f) {
      throw new RuntimeException("GL_INVALID_VALUE: zNear and zFar must be positive, and zNear>0")
    }
    if (left == right || top == bottom) {
      throw new RuntimeException("GL_INVALID_VALUE: top,bottom and left,right must not be equal")
    }
    makeIdentity(m)
    val zNear2 = 2.0f * zNear
    val dx = right - left
    val dy = top - bottom
    val dz = zFar - zNear
    val A = (right + left) / dx
    val B = (top + bottom) / dy
    val C = -1.0f * (zFar + zNear) / dz
    val D = -2.0f * (zFar * zNear) / dz

    m(0 + 4 * 0) = zNear2 / dx
    m(1 + 4 * 1) = zNear2 / dy
    m(0 + 4 * 2) = A
    m(1 + 4 * 2) = B
    m(2 + 4 * 2) = C
    m(3 + 4 * 2) = -1.0f
    m(2 + 4 * 3) = D
    m(3 + 4 * 3) = 0f
    m
  }

  final def makePerspective(m: Array[Float], fovy_rad: Float, aspect: Float, zNear: Float, zFar: Float): Array[Float] = {
    val top = math.tan(fovy_rad / 2f).toFloat * zNear // use tangent of half-fov !
    val bottom = -1.0f * top
    val left = aspect * bottom
    val right = aspect * top
    makeFrustum(m, left, right, bottom, top, zNear, zFar)
  }

  final def makeLookAt(
    m: Array[Float],
    eye: Array[Float], eye_offset: Int,
    center: Array[Float], center_offset: Int,
    up: Array[Float], up_offset: Int, mat4Tmp: Array[Float]): Array[Float] = {

    val forward_off = 0
    val side_off = 3
    val up2_off = 6

    // forward!
    mat4Tmp(0) = center(0 + center_offset) - eye(0 + eye_offset)
    mat4Tmp(1) = center(1 + center_offset) - eye(1 + eye_offset)
    mat4Tmp(2) = center(2 + center_offset) - eye(2 + eye_offset)

    normalizeVec3(mat4Tmp) // normalize forward

    /* Side = forward x up */
    crossVec3(mat4Tmp, side_off, mat4Tmp, forward_off, up, up_offset)
    normalizeVec3(mat4Tmp, side_off) // normalize side

    /* Recompute up as: up = side x forward */
    crossVec3(mat4Tmp, up2_off, mat4Tmp, side_off, mat4Tmp, forward_off)

    m(+0 * 4 + 0) = mat4Tmp(0 + side_off) // side
    m(+0 * 4 + 1) = mat4Tmp(0 + up2_off) // up2
    m(+0 * 4 + 2) = -mat4Tmp(0) // forward
    m(+0 * 4 + 3) = 0

    m(+1 * 4 + 0) = mat4Tmp(1 + side_off) // side
    m(+1 * 4 + 1) = mat4Tmp(1 + up2_off) // up2
    m(+1 * 4 + 2) = -mat4Tmp(1) // forward
    m(+1 * 4 + 3) = 0

    m(+2 * 4 + 0) = mat4Tmp(2 + side_off) // side
    m(+2 * 4 + 1) = mat4Tmp(2 + up2_off) // up2
    m(+2 * 4 + 2) = -mat4Tmp(2) // forward
    m(+2 * 4 + 3) = 0

    m(+3 * 4 + 0) = 0
    m(+3 * 4 + 1) = 0
    m(+3 * 4 + 2) = 0
    m(+3 * 4 + 3) = 1

    makeTranslation(mat4Tmp, -eye(0 + eye_offset), -eye(1 + eye_offset), -eye(2 + eye_offset))
    multMatrix(m, mat4Tmp)

    m
  }

  final def normalizeVec3(result: Array[Float], v: Array[Float]): Array[Float] = {
    val lengthSq = v(0) * v(0) + v(1) * v(1) + v(2) * v(2)
    val invSqr = 1f / FastMath.sqrt(lengthSq).toFloat
    result(0) = v(0) * invSqr
    result(1) = v(1) * invSqr
    result(2) = v(2) * invSqr
    result
  }

  final def normalizeVec3(v: Array[Float]): Array[Float] = {
    val lengthSq = v(0) * v(0) + v(1) * v(1) + v(2) * v(2)
    val invSqr = 1f / FastMath.sqrt(lengthSq).toFloat
    v(0) = v(0) * invSqr
    v(1) = v(1) * invSqr
    v(2) = v(2) * invSqr
    v
  }

  final def normalizeVec3(v: Array[Float], offs: Int): Array[Float] = {
    val lengthSq = v(0 + offs) * v(0 + offs) + v(1 + offs) * v(1 + offs) + v(2 + offs) * v(2 + offs)
    val invSqr = 1f / FastMath.sqrt(lengthSq).toFloat
    v(0 + offs) = v(0 + offs) * invSqr
    v(1 + offs) = v(1 + offs) * invSqr
    v(2 + offs) = v(2 + offs) * invSqr
    v
  }

  final def crossVec3(result: Array[Float], v1: Array[Float], v2: Array[Float]): Array[Float] = {
    result(0) = v1(1) * v2(2) - v1(2) * v2(1)
    result(1) = v1(2) * v2(0) - v1(0) * v2(2)
    result(2) = v1(0) * v2(1) - v1(1) * v2(0)
    result
  }

  final def crossVec3(r: Array[Float], r_offset: Int, v1: Array[Float], v1_offset: Int, v2: Array[Float], v2_offset: Int): Array[Float] = {
    r(0 + r_offset) = v1(1 + v1_offset) * v2(2 + v2_offset) - v1(2 + v1_offset) * v2(1 + v2_offset)
    r(1 + r_offset) = v1(2 + v1_offset) * v2(0 + v2_offset) - v1(0 + v1_offset) * v2(2 + v2_offset)
    r(2 + r_offset) = v1(0 + v1_offset) * v2(1 + v2_offset) - v1(1 + v1_offset) * v2(0 + v2_offset)
    r
  }

  final def multMatrixVector(m: Array[Float], values: Array[Float], floatOffs: Int, nFloats: Int) {

    var r0 = 0.0f
    var r1 = 0.0f
    var r2 = 0.0f

    optimize(for (modelVertOffs <- floatOffs until (floatOffs + nFloats) by 4) {

      r0 =
        values(0 + modelVertOffs) * m(0 * 4) +
          values(1 + modelVertOffs) * m(1 * 4) +
          values(2 + modelVertOffs) * m(2 * 4) +
          values(3 + modelVertOffs) * m(3 * 4)

      r1 =
        values(0 + modelVertOffs) * m(0 * 4 + 1) +
          values(1 + modelVertOffs) * m(1 * 4 + 1) +
          values(2 + modelVertOffs) * m(2 * 4 + 1) +
          values(3 + modelVertOffs) * m(3 * 4 + 1)

      r2 =
        values(0 + modelVertOffs) * m(0 * 4 + 2) +
          values(1 + modelVertOffs) * m(1 * 4 + 2) +
          values(2 + modelVertOffs) * m(2 * 4 + 2) +
          values(3 + modelVertOffs) * m(3 * 4 + 2)

      values(0 + modelVertOffs) = r0
      values(1 + modelVertOffs) = r1
      values(2 + modelVertOffs) = r2
      values(3 + modelVertOffs) =
        values(0 + modelVertOffs) * m(0 * 4 + 3) +
          values(1 + modelVertOffs) * m(1 * 4 + 3) +
          values(2 + modelVertOffs) * m(2 * 4 + 3) +
          values(3 + modelVertOffs) * m(3 * 4 + 3)

    })
  }

  final def multMatrixVector(m: Array[Float], values: Array[Float]) {
    multMatrixVector(m, values, 0, values.length)
  }

} 

