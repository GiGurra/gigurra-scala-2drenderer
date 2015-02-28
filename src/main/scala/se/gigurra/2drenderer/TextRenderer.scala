package se.gigurra.renderer

trait TextRenderer {
  def print(text: String, font: Font)
  def printLn(text: String, font: Font)
  def getTextWidth(text: String, font: Font): Float
}