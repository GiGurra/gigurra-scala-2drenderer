package se.gigurra.renderer

case class Rect[@specialized T](
  val w: T,
  val h: T,
  val x: T = 0,
  val y: T = 0) {
}
