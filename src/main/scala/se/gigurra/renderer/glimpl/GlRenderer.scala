package se.gigurra.renderer.glimpl

import scala.collection.mutable.ArrayBuffer
import javax.media.opengl.GL
import javax.media.opengl.GL3ES3
import javax.media.opengl.GLAutoDrawable
import javax.media.opengl.GLDrawable
import javax.media.opengl.GLEventListener
import se.gigurra.renderer.Renderer
import se.gigurra.renderer.TransformStack
import se.gigurra.renderer.Widget
import se.gigurra.renderer.util.ShaderUtil
import se.gigurra.renderer.Color

abstract class GlRenderer(_init_gl: GL3ES3) extends Renderer {

  private var _gl: GL3ES3 = _init_gl
  private var _drawable: GLDrawable = _init_gl.getContext.getGLDrawable
  private var _colorScale = Color.WHITE

  implicit val glslCfg = GlSlConfiguration.v330(GlExtensions.GL_ARB_shader_draw_parameters)
  // implicit val glslCfg = GlSlConfiguration.v330()
  protected val shaderProgram = ShaderUtil.buildProgramFromFiles(gl, "shaders/vertexShader.c", "shaders/fragmentShader.c", glslCfg).use(gl)
  protected val transformStack = new TransformStack
  protected val transform = transformStack.top
  protected val textRenderer = new GlTextRenderer(this, transformStack)

  ///////////////////////////////////////////////////

  override def draw(widget: Widget[_], isRoot: Boolean) {

    if (isRoot) {
      getCaps foreach (enableDisable _).tupled
      (setBlendFunc _).tupled(getBlendFunc)
      clearColor(getClearColor)
      clearBits(getClearBits)
      GlIntegers.flushErrsThrow(gl)
    }

    widget.draw(this, isRoot)

  }

  override def setColorScale(c: Color) { _colorScale = c }
  override def colorScale() = _colorScale

  override def dispose() {
    val allPrograms = Seq(shaderProgram.program)
    val allShaders = shaderProgram.shaders.distinct // If adding more shaders, be sure to only delete once
    allPrograms foreach gl.glDeleteProgram
    allShaders foreach gl.glDeleteShader
  }

  override def getFps(): Float = GlRenderThread.getFps
  override def surfaceWidth() = drawable.getSurfaceWidth
  override def surfaceHeight() = drawable.getSurfaceHeight
  override def getTransformStack() = transformStack
  override def getTextRenderer() = textRenderer

  override def setViewPort(x: Int, y: Int, w: Int, h: Int) { gl.glViewport(0, 0, w, h) }

  var lastLineWidth = 1.0f
  override def setLineWidth(w: Float) {
    flush()
    if (w != lastLineWidth) {
      lastLineWidth = w
      gl.glLineWidth(w)
      GlIntegers.checkErr(gl) { _ =>
        System.err.println(s"Deprecation Warning: glLineWidth($w) not supported on this platform!")
      }
    }
  }

  protected def getShaderProgram() = shaderProgram

  protected def getClearBits(): Int = GlRenderer.defaultClearBits
  protected def getClearColor(): (Float, Float, Float, Float) = GlRenderer.defaultClearColor
  protected def getBlendFunc(): (Int, Int) = GlRenderer.defaultBlendFunc
  protected def getCaps(): Seq[(Int, Boolean)] = GlRenderer.defaulCaps

  protected def enableDisable(cap: Int, state: Boolean) = if (state) gl.glEnable(cap) else gl.glDisable(cap)
  protected def setBlendFunc(sfactor: Int, dFactor: Int) = (gl.glBlendFunc _).tupled(GlRenderer.defaultBlendFunc)
  protected def clearColor(c: (Float, Float, Float, Float)) = (gl.glClearColor _).tupled(c)
  protected def clearBits(bits: Int) = gl.glClear(bits)

  def gl() = _gl
  def drawable() = _drawable

}

object GlRenderer {

  val defaultClearBits = GL.GL_STENCIL_BUFFER_BIT | GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT
  val defaultClearColor = (0.0f, 0.0f, 0.0f, 0.0f)
  val defaultBlendFunc = (GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA)
  val defaulCaps: Seq[(Int, Boolean)] = {
    val out = new ArrayBuffer[(Int, Boolean)]
    out += ((GL.GL_DEPTH_TEST, false))
    out += ((GL.GL_CULL_FACE, false))
    out += ((GL.GL_BLEND, true))
    out
  }

  implicit class Attachable[T <: GLAutoDrawable](drawable: T) {
    def attach(widget: Widget[_], ctor: GL3ES3 => GlRenderer): T = {
      var renderer: GlRenderer = null
      drawable.addGLEventListener(new GLEventListener {
        override def init(surface: GLAutoDrawable) {
          renderer = ctor(surface.getGL.getGL3ES3)
          renderer._drawable = surface
          renderer._gl = surface.getGL.getGL3ES3
        }
        override def reshape(surface: GLAutoDrawable, x: Int, y: Int, w: Int, h: Int) {}
        override def display(surface: GLAutoDrawable) {
          renderer._drawable = surface
          renderer._gl = surface.getGL.getGL3ES3
          renderer.draw(widget, true)
        }
        override def dispose(surface: GLAutoDrawable) {
          GlRenderThread.unregister(surface)
          renderer.dispose()
        }
      })
      GlRenderThread.register(drawable)
      drawable
    }
  }
}