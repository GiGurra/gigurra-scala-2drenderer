package se.gigurra.renderer

import java.awt.font.FontRenderContext
import java.awt.geom.PathIterator

import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.mutable.HashMap

import org.poly2tri.triangulation.TriangulationPoint
import org.poly2tri.triangulation.delaunay.sweep.DTSweep
import org.poly2tri.triangulation.delaunay.sweep.DTSweepContext
import org.poly2tri.triangulation.point.TPoint
import org.poly2tri.triangulation.sets.PointSet

import scalaxy.streams.optimize

object Font {
  object Awt {
    def monoSpace(color: Color) = new AwtFont(color, new java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.PLAIN, 10))
  }
}

trait Font {
  def get(c: Char): TriangulatedGlyph
  def color(): Color
  var loadedContents: AnyRef = null
}

class AwtFont(color: Color, font: java.awt.Font) extends Font {

  private val awtCache = new HashMap[Char, TriangulatedGlyph]
  private val awtFrc = new FontRenderContext(null, true, true)
  private val context = new DTSweepContext
  private val pts = new java.util.ArrayList[TPoint].asInstanceOf[java.util.List[TriangulationPoint]]
  private val scale = 1.0f / font.getSize.toFloat

  private def interpolateOutlinePts(shape: java.awt.Shape) {
    pts.clear()

    val curveInterpDist = font.getSize * 0.0175 // cannot be too high or shapes are edgy (0.0175 seems to be max)
    val straightInterpDist = curveInterpDist * 2 // cannot be too high or delaunay triangulation fails (2*curveInterpDist seems to be max)
    val p = Array(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    val iterator = shape.getPathIterator(null, curveInterpDist)
    var xLast = -1e34
    var yLast = -1e34
    while (!iterator.isDone) {
      val typ = iterator.currentSegment(p)
      val px = p(0)
      val py = p(1)
      typ match {
        case PathIterator.SEG_MOVETO =>
        case PathIterator.SEG_LINETO =>
          if (px != xLast || py != yLast) {
            val len = math.hypot(px - xLast, py - yLast)
            val nSteps = (len / straightInterpDist).ceil.toInt
            val dx = (px - xLast) / nSteps.toDouble
            val dy = (py - yLast) / nSteps.toDouble
            optimize(for (i <- 0 to nSteps) {
              pts.add(new TPoint(xLast + dx * i.toDouble, yLast + dy * i.toDouble))
            })
          }
        case _ =>
      }
      xLast = px
      yLast = py
      iterator.next()
    }
  }

  private def makeTriangles(shape: java.awt.Shape): Array[Float] = {
    if (pts.nonEmpty) {

      context.clear()
      val ps = new PointSet(pts)
      context.prepareTriangulation(ps)
      DTSweep.triangulate(context)

      // Remove triangles outside the shape
      val validTriangles = ps.getTriangles.filter { t =>
        shape.contains(
          (t.points(0).getX + t.points(1).getX + t.points(2).getX) * 0.33333333333333,
          (t.points(0).getY + t.points(1).getY + t.points(2).getY) * 0.33333333333333)
      }

      // Now store vertices in a float array
      val out = new Array[Float](validTriangles.size * 12)
      var i = 0
      for (t <- validTriangles) {
        out(i + 0) = t.points(2).getX.toFloat * scale
        out(i + 1) = -t.points(2).getY.toFloat * scale
        out(i + 2) = 0.0f
        out(i + 3) = 1.0f

        out(i + 4) = t.points(1).getX.toFloat * scale
        out(i + 5) = -t.points(1).getY.toFloat * scale
        out(i + 6) = 0.0f
        out(i + 7) = 1.0f

        out(i + 8) = t.points(0).getX.toFloat * scale
        out(i + 9) = -t.points(0).getY.toFloat * scale
        out(i + 10) = 0.0f
        out(i + 11) = 1.0f
        i += 12
      }
      out
    } else {
      Array()
    }
  }

  override def get(c: Char): TriangulatedGlyph = synchronized {
    awtCache.getOrElseUpdate(c, {
      val glyphs = font.createGlyphVector(awtFrc, Array(c))
      val shape = glyphs.getOutline
      interpolateOutlinePts(shape)
      val triangles = makeTriangles(shape)
      TriangulatedGlyph(glyphs.getLogicalBounds.getMaxX.toFloat * scale, triangles)
    })
  }

  override def color(): Color = {
    color
  }

}

case class TriangulatedGlyph(advanceWidth: Float, vertices: Array[Float])
