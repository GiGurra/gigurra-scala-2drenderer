package se.gigurra.renderer

sealed abstract class PrimitiveType

object PrimitiveType {
  case object TRIANGLES extends PrimitiveType
  case object LINES extends PrimitiveType
}
