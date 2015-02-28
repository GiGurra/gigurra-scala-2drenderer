package se.gigurra.renderer

import java.nio.FloatBuffer

import se.gigurra.util.Mutate.Mutable

object Transform {
  def apply(): Transform = new Transform
}

class Transform(val m: Mat4x4 = new Mat4x4) {

  private var _modCount = 0

  final def copy() = new Transform(m.copy)

  /////////////////////////// TRANSFORMATIONS ///////////////////////////

  final def loadIdentity(): Transform = { m.loadIDentity(); mod() }

  final def rot(ang_deg: Float, x: Float, y: Float, z: Float): Transform = { m.rotate(ang_deg, x, y, z); mod() }
  final def rotX(ang_deg: Float): Transform = rot(ang_deg, 1.0f, 0.0f, 0.0f)
  final def rotY(ang_deg: Float): Transform = rot(ang_deg, 0.0f, 1.0f, 0.0f)
  final def rotZ(ang_deg: Float): Transform = rot(ang_deg, 0.0f, 0.0f, 1.0f)
  final def rot2d(ang_deg: Float): Transform = rotZ(ang_deg)

  final def scale(sx: Float, sy: Float, sz: Float): Transform = { m.scale(sx, sy, sz); mod() }
  final def scaleX(sx: Float): Transform = scale(sx, 1.0f, 1.0f)
  final def scaleY(sy: Float): Transform = scale(1.0f, sy, 1.0f)
  final def scaleZ(sz: Float): Transform = scale(1.0f, 1.0f, sz)
  final def scaleXY(sxy: Float): Transform = scale(sxy, sxy, 1.0f)
  final def scaleXZ(sxz: Float): Transform = scale(sxz, 1.0f, sxz)
  final def scaleYZ(syz: Float): Transform = scale(1.0f, syz, syz)
  final def scale2d(sxy: Float): Transform = scaleXY(sxy)

  final def translate(dx: Float, dy: Float, dz: Float): Transform = { m.translate(dx, dy, dz); mod() }
  final def translate2d(dx: Float, dy: Float): Transform = translate(dx, dy, 0.0f)
  final def translateX(dx: Float): Transform = translate(dx, 0.0f, 0.0f)
  final def translateY(dy: Float): Transform = translate(0.0f, dy, 0.0f)
  final def translateZ(dz: Float): Transform = translate(0.0f, 0.0f, dz)

  final def mult(t: Transform): Transform = { m.mult(t.m.array); mod() }
  final def load(t: Transform): Transform = { m.set(t.m.array); mod() }

  final def buffer(): FloatBuffer = m.buffer
  val array = m.array

  /////////////////////////// PROJECTIONS ///////////////////////////

  final def ortho(left: Float, right: Float, bottom: Float, top: Float, zNear: Float = 0, zFar: Float = 1): Transform = {
    m.ortho(left, right, bottom, top, zNear, zFar)
    mod()
  }

  final def orthoMinDist(minDist: Float, surface: Rect[Int], zNear: Float = 0, zFar: Float = 1): Transform = {
    val aspect = surface.w.toFloat / surface.h.toFloat
    val right = if (aspect > 1) minDist * aspect else minDist
    val top = if (aspect < 1) minDist / aspect else minDist
    val left = -right
    val bottom = -top
    ortho(left, right, bottom, top, zNear, zFar)
  }

  final def orthoYDist(yDist: Float, surface: Rect[Int], zNear: Float = 0, zFar: Float = 1): Transform = {
    val aspect = surface.w.toFloat / surface.h.toFloat
    val right = aspect * yDist
    val top = yDist
    val left = -right
    val bottom = -top
    ortho(left, right, bottom, top, zNear, zFar)
  }

  final def orthoXDist(xDist: Float, surface: Rect[Int], zNear: Float = 0, zFar: Float = 1): Transform = {
    val aspect = surface.w.toFloat / surface.h.toFloat
    val right = xDist
    val top = xDist / aspect
    val left = -right
    val bottom = -top
    ortho(left, right, bottom, top, zNear, zFar)
  }

  final def perspective(fovy_deg: Float, aspect: Float, zNear: Float, zFar: Float): Transform = {
    m.perspective(fovy_deg, aspect, zNear, zFar)
    mod()
  }
  final def perspective(fovy_deg: Float, surface: Rect[Int], zNear: Float, zFar: Float): Transform = {
    perspective(fovy_deg, surface.w.toFloat / surface.h.toFloat, zNear, zFar)
  }

  final def mod(): Transform = {
    _modCount += 1
    this
  }

  final def modCount(): Int = {
    _modCount
  }

}