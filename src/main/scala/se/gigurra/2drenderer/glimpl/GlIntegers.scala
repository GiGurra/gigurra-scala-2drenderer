package se.gigurra.renderer.glimpl

import scala.collection.mutable.HashMap
import javax.media.opengl.GL2ES2
import javax.media.opengl.GL2ES3
import javax.media.opengl.GL3ES3
import se.gigurra.util.Mutate.Mutable
import javax.media.opengl.GL

class GlIntegers(gl: GL3ES3) {

  private val cachedGlInts = new HashMap[Int, Int]

  val maxUniformFloatArrayLen = getGlIntCached(GL2ES3.GL_MAX_VERTEX_UNIFORM_COMPONENTS, 1024)
  val maxUniformVec4ArrayLen = getGlIntCached(GL2ES2.GL_MAX_VERTEX_UNIFORM_VECTORS, maxUniformFloatArrayLen)
  val maxUniformMat4ArrayLen = maxUniformVec4ArrayLen / 4

  val bindRangeAlignment = getGlIntCached(GL2ES3.GL_UNIFORM_BUFFER_OFFSET_ALIGNMENT)

  def getGlIntCached(name: Int, default: Int = -1): Int = cachedGlInts.getOrElseUpdate(name, GlIntegers.getInt(gl, name, default))

}

object GlIntegers {

  def getInt(gl: GL3ES3, name: Int, default: Int = -1): Int = getIntWith(gl, default, s"glGetInt(${name})")(gl.glGetIntegerv(name, _, 0))

  def getIntWith[A](gl: GL3ES3, default: Int = -1, descr: String = "")(call: Array[Int] => A): Int = {

    val callDescr = if (descr != null && descr.nonEmpty) descr else call.toString

    var err = gl.glGetError()
    if (err != GL.GL_NO_ERROR)
      throw new RuntimeException(s"cannot call $call - glError was set before (${err.toHexString})")
    val out = Array(-1).mutate(call)(0)
    err = gl.glGetError()
    if (err != GL.GL_NO_ERROR) {
      if (default != -1) {
        System.err.println(s"Warning: $callDescr failed with error 0x${err.toHexString}, defaulting to ${default}")
        default
      } else {
        throw new RuntimeException(s"$callDescr failed with error 0x${err.toHexString}")
      }
    } else {
      out
    }
  }

  def checkErr[A](gl: GL3ES3)(handler: Int => A): Boolean = {
    val err = gl.glGetError()
    val isNoErr = err == GL.GL_NO_ERROR
    if (!isNoErr)
      handler(err)
    isNoErr
  }

  def mkErrString(err: Int): String = s"GL error 0x${err.toHexString}"

  val warnHandler = (e: Int) => System.err.println(mkErrString(e))
  val throwHandler = (e: Int) => throw new RuntimeException(mkErrString(e))

  def warnIfGlErr(gl: GL3ES3) = checkErr(gl)(warnHandler)
  def throwIfGlErr(gl: GL3ES3) = checkErr(gl)(throwHandler)

  def flushErrs[A](gl: GL3ES3)(handler: Int => A) {
    while (!checkErr(gl)(handler)) {}
  }

  def flushErrsWarn(gl: GL3ES3) = flushErrs(gl)(warnHandler)
  def flushErrsThrow(gl: GL3ES3) = flushErrs(gl)(throwHandler)
  

}