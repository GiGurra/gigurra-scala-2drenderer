import java.util.Arrays

import org.junit.Test
import org.poly2tri.triangulation.delaunay.sweep.DTSweep
import org.poly2tri.triangulation.delaunay.sweep.DTSweepContext
import org.poly2tri.triangulation.point.TPoint
import org.poly2tri.triangulation.sets.PointSet

import com.jogamp.graph.curve.OutlineShape
import com.jogamp.graph.curve.OutlineShape.VerticesState
import com.jogamp.graph.font.FontFactory
import com.jogamp.graph.font.FontSet
import com.jogamp.graph.geom.Triangle

class Fonts {

  @Test
  def jogampFont() {

    val fontFactory = FontFactory.get(FontFactory.JAVA)
    val font = fontFactory.get(FontSet.FAMILY_MONOSPACED, FontSet.STYLE_NONE)

    val glyph = font.getGlyph('Å')
    val outlineShape: OutlineShape = glyph.getShape()

    val triangles = outlineShape.getTriangles(VerticesState.QUADRATIC_NURBS)
    val t0: Triangle = triangles.get(0)

    font.getAdvanceWidth(glyph.getID, 1)
    assert(triangles.size >= 5)
  }

  @Test
  def testTriangulate() {
    val context = new DTSweepContext
    val p1 = new TPoint(0, 0, 0)
    val p2 = new TPoint(0, 1, 0)
    val p3 = new TPoint(1, 1, 0)
    val p4 = new TPoint(1, 0, 0)

    val ps = new PointSet(Arrays.asList(p1, p2, p3, p4))
    context.prepareTriangulation(ps);
    DTSweep.triangulate(context);

    assert(ps.getTriangles.size == 2)

  }
}