package se.gigurra.renderer

import scala.util.Failure
import scala.util.Success
import scala.util.Try

import com.jogamp.newt.opengl.GLWindow

import javax.media.opengl.GLCapabilities
import javax.media.opengl.GLProfile
import net.jodk.lang.FastMath
import scalaxy.streams.optimize
import se.gigurra.renderer.Color.RichColor
import se.gigurra.renderer.CompositeModel.toCompositeModel
import se.gigurra.renderer.Renderer.toTextRenderer
import se.gigurra.renderer.Renderer.toTransform
import se.gigurra.renderer.Renderer.toTransformStack
import se.gigurra.renderer.glimpl.GlRenderer.Attachable
import se.gigurra.renderer.glimpl.GlRenderer2d
import se.gigurra.renderer.shapes.Arc
import se.gigurra.renderer.shapes.Circle
import se.gigurra.renderer.shapes.Line
import se.gigurra.renderer.shapes.Square
import se.gigurra.renderer.shapes.Triangle
import se.gigurra.util.Mutate.Mutable

class Demo extends Widget[Demo] {

  val whiteFont = Font.Awt.monoSpace(Color.WHITE)
  val redFont = Font.Awt.monoSpace(Color.RED)
  val greenFont = Font.Awt.monoSpace(Color.GREEN)
  val blueFont = Font.Awt.monoSpace(Color.BLUE)

  val staticText = TextModel("static text", greenFont)

  val model1 = Model.triangles(
    Vertices(Vertex(0.0f, 1.0f), Vertex(-1.0f, -1.0f), Vertex(1.0f, -1.0f)),
    Colors(Color.WHITE.butWith(a = 0.75f), Color.RED.butWith(a = 0.85f), Color.BLACK.butWith(a = 0.95f)))

  val model2 = Model.triangles(
    Vertices(Vertex(0.0f, 2.0f), Vertex(-1.0f, -2.0f), Vertex(1.0f, -1.0f)),
    Colors(Color.RED.butWith(a = 0.45f), Color.GREEN.butWith(a = 0.55f), Color.BLUE.butWith(a = 0.65f)))

  val model3 = (model1 + model2).transform { _.rot2d(45) } + model2

  val triLine1 = Triangle.lineCentered(Color.WHITE)
  val triLine2 = triLine1.transform(_.rot2d(180))
  val triFill1 = Triangle.fillCentered(Color.RED)
  val triFill2 = triFill1.transform(_.rot2d(180))

  val wierdShape = triLine1 + triFill2

  val leftSquare = Square.fillCentered(Color.WHITE)
  val rightSquare = Square.lineCentered(Color.WHITE)

  val whitePlus = Line.vertCentered(Color.WHITE) + Line.horCentered(Color.WHITE)
  val blackPlus = whitePlus.copy(Color.BLACK)

  val leftCircle = Circle.fill(100, Color.WHITE)
  val rightCircle = Circle.lines(100, Color.WHITE).transform { _.scale2d(1) }

  val lineArcOpen = Arc.lines(90, 5, Arc.Type.OPEN, Color.WHITE)
  val lineArcClosed = Arc.lines(90, 5, Arc.Type.CLOSED, Color.WHITE)
  val lineArcCone = Arc.lines(90, 5, Arc.Type.CONE, Color.WHITE)

  val fillArcClosed = Arc.fill(90, 5, Arc.Type.CLOSED, Color.BLUE.butWith(a = 0.35f))
  val fillArcCone = Arc.fill(90, 5, Arc.Type.CONE, Color.BLUE.butWith(a = 0.35f))

  override def doDraw(renderer: Renderer, isRoot: Boolean) {

    renderer.setViewPortToFull()
    renderer.setLineWidth(2.0f)

    renderer.loadIdentity().orthoMinDist(1, renderer.surfaceSize)

    optimize(for (i <- 0 until 1) {

      val tMillis = System.currentTimeMillis

      renderer pushPopTransform {

        renderer.rot2d(30f * FastMath.sin(tMillis * 0.005).toFloat)
        renderer.draw(model1)

        renderer.rot2d(30f * FastMath.sin(tMillis * 0.005).toFloat)
        renderer.draw(model1)

        renderer.rot2d(60f * FastMath.sin(tMillis * 0.005).toFloat)
        renderer.draw(model2)

        renderer.rot2d(120f * FastMath.sin(tMillis * 0.005).toFloat).scale2d(0.1f)
        renderer.useColorScale(Color.BLUE) {
          renderer.draw(model3)
        }
        renderer.draw(staticText)

      }

      renderer pushPopTransform {
        renderer.scale2d(0.10f)
        renderer pushPopTransform {
          renderer.translateX(-0.5f * renderer.getTextWidth("--- dynamic text ---", whiteFont))
          renderer.printLn("--- dynamic text ---", whiteFont)
          renderer.printLn(s"time: ${tMillis.toString}", whiteFont)
          renderer.printLn(s" fps: ${renderer.getFps}", blueFont)
          renderer.printLn("renderer.printLn(\"..\", blueFont)", blueFont)
        }
      }

      renderer pushPopTransform {
        renderer.translate2d(-0.75f, -0.75f).scale2d(0.2f)
        renderer.draw(triLine1)
        renderer.draw(triLine2)
      }
      renderer pushPopTransform {
        renderer.translate2d(0.75f, -0.75f).scale2d(0.2f)
        renderer.draw(triFill1)
        renderer.draw(triFill2)
      }
      renderer pushPopTransform {
        renderer.translate2d(0.0f, 0.75f).scale2d(0.2f)
        renderer.draw(wierdShape)
      }

      renderer pushPopTransform {
        renderer.translate2d(-0.75f, 0.0f).scale2d(0.2f)
        renderer.draw(leftSquare)
        renderer.draw(blackPlus)
      }
      renderer pushPopTransform {
        renderer.translate2d(0.75f, 0.0f).scale2d(0.2f)
        renderer.draw(rightSquare)
        renderer.draw(whitePlus)
      }

      renderer pushPopTransform {
        renderer.translate2d(-0.75f, 0.75f).scale2d(0.2f)
        renderer.draw(leftCircle)
      }
      renderer pushPopTransform {
        renderer.translate2d(0.75f, 0.75f).scale2d(0.2f)
        renderer.draw(rightCircle)
      }

      renderer pushPopTransform {
        renderer.translate2d(-0.5f, 0.5f).scale2d(0.2f)
        renderer.draw(lineArcOpen)
      }
      renderer pushPopTransform {
        renderer.translate2d(0, 0.5f).scale2d(0.2f)
        renderer.draw(lineArcClosed)
      }
      renderer pushPopTransform {
        renderer.translate2d(0.5f, 0.5f).scale2d(0.2f)
        renderer.draw(lineArcCone)
      }

      renderer pushPopTransform {
        renderer.translate2d(-0.25f, 0.15f).scale2d(0.2f)
        renderer.draw(fillArcClosed)
      }

      renderer pushPopTransform {
        renderer.translate2d(0.25f, 0.15f).scale2d(0.2f)
        renderer.draw(fillArcCone)
      }

    })

    println("nModelsDrawn: " + renderer.nModelsDrawn)
    println("nBatchDrawCalls: " + renderer.nBatchDrawCalls)
    println("fps: " + renderer.getFps())

  }

}

object Demo {

  def main(args: Array[String]) {

    val glProfile =
      Try(GLProfile.get(GLProfile.GL3)) match {
        case Success(profile) => profile
        case Failure(err) =>
          Try(GLProfile.get(GLProfile.GLES3)) match {
            case Success(profile) => profile
            case Failure(err)     => throw err
          }
      }

    val caps = new GLCapabilities(glProfile).mutate { caps =>
      caps.setNumSamples(4)
      caps.setSampleBuffers(true)
    }

    val glWindow = GLWindow.create(caps).mutate { glWindow =>
      glWindow.setTitle("Raw GL3ES3 Demo")
      glWindow.setSize(1280, 720)
      glWindow.setUndecorated(false)
      glWindow.setPointerVisible(true)
      glWindow.setVisible(true)
    }

    glWindow.attach(new Demo, new GlRenderer2d(_))

  }
}