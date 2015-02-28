package se.gigurra.renderer.util

import java.nio.charset.Charset

import javax.media.opengl.GL
import javax.media.opengl.GL2ES2
import javax.media.opengl.GL3ES3
import se.gigurra.renderer.glimpl.GlIntegers
import se.gigurra.renderer.glimpl.GlShaderProgram
import se.gigurra.renderer.glimpl.GlSlConfiguration
import se.gigurra.util.Resource

object ShaderUtil {

  def buildShader(gl: GL3ES3, shaderType: Int, sourceCode: String, glslCfg: GlSlConfiguration): Int = {
    createAndCompileShader(gl, shaderType, glslCfg.filter(gl, sourceCode))
  }

  def buildVertexShader(gl: GL3ES3, sourceCode: String, glslCfg: GlSlConfiguration): Int = {
    buildShader(gl, GL2ES2.GL_VERTEX_SHADER, sourceCode, glslCfg)
  }

  def buildVertexShaderFromFile(gl: GL3ES3, filePath: String, glslCfg: GlSlConfiguration): Int = {
    buildVertexShader(gl, Resource.file2String(filePath), glslCfg)
  }

  def buildFragmentShader(gl: GL3ES3, sourceCode: String, glslCfg: GlSlConfiguration): Int = {
    buildShader(gl, GL2ES2.GL_FRAGMENT_SHADER, sourceCode, glslCfg)
  }

  def buildFragmentShaderFromFile(gl: GL3ES3, filePath: String, glslCfg: GlSlConfiguration): Int = {
    buildFragmentShader(gl, Resource.file2String(filePath), glslCfg)
  }

  def buildProgram(gl: GL3ES3, shaders: Int*): GlShaderProgram = {

    var err = gl.glGetError() // flush previous errors ..
    if (err != GL.GL_NO_ERROR)
      throw new RuntimeException("buildProgram: Pre GL Error: 0x" + Integer.toHexString(err))

    val program = gl.glCreateProgram()
    for (shader <- shaders) {
      gl.glAttachShader(program, shader)
      err = gl.glGetError() // flush previous errors ..
      if (err != GL.GL_NO_ERROR) {
        gl.glDeleteProgram(program)
        throw new RuntimeException("buildProgram:glAttachShader: Pre GL Error: 0x" + Integer.toHexString(err))
      }
    }

    gl.glLinkProgram(program)
    err = gl.glGetError() // flush previous errors ..
    if (err != GL.GL_NO_ERROR) {
      gl.glDeleteProgram(program)
      throw new RuntimeException("buildProgram:glLinkProgram: Pre GL Error: 0x" + Integer.toHexString(err))
    }

    GlShaderProgram(program, shaders)
  }

  def buildProgramFromFiles(
    gl: GL3ES3,
    vertexShaderFilePath: String,
    fragmentShaderFilePath: String,
    glslCfg: GlSlConfiguration): GlShaderProgram = {
    val vertexShader = buildVertexShaderFromFile(gl, vertexShaderFilePath, glslCfg)
    val fragmentShader = buildFragmentShaderFromFile(gl, fragmentShaderFilePath, glslCfg)
    buildProgram(gl, vertexShader, fragmentShader)
  }

  def createAndCompileShader(gl: GL3ES3, shaderType: Int, source: String): Int = {

    var err = gl.glGetError() // flush previous errors ..
    if (err != GL.GL_NO_ERROR)
      throw new RuntimeException("createAndCompileShader:glGetError: Pre GL Error: 0x" + Integer.toHexString(err))

    val shader = gl.glCreateShader(shaderType)
    err = gl.glGetError()
    if (err != GL.GL_NO_ERROR)
      throw new RuntimeException("createAndCompileShader:glCreateShader: GL Error: 0x" + Integer.toHexString(err))

    gl.glShaderSource(shader, 1, Array(source), Array(source.length), 0)
    err = gl.glGetError()
    if (err != GL.GL_NO_ERROR) {
      gl.glDeleteShader(shader)
      throw new RuntimeException("createAndCompileShader:glShaderSource: GL Error: 0x" + Integer.toHexString(err))
    }

    gl.glCompileShader(shader)
    err = gl.glGetError()
    if (err != GL.GL_NO_ERROR) {
      gl.glDeleteShader(shader)
      throw new RuntimeException("createAndCompileShader::glCompileShader: GL Error: 0x" + Integer.toHexString(err))
    }

    val compileResult = GlIntegers.getIntWith(gl)(gl.glGetShaderiv(shader, GL2ES2.GL_COMPILE_STATUS, _, 0))
    if (compileResult != GL.GL_TRUE) {

      val maxLogLen = GlIntegers.getIntWith(gl)(gl.glGetShaderiv(shader, GL2ES2.GL_INFO_LOG_LENGTH, _, 0))
      val logBytes = new Array[Byte](maxLogLen)

      val lens = Array(maxLogLen)
      gl.glGetShaderInfoLog(shader, maxLogLen, lens, 0, logBytes, 0)
      val logString = new String(logBytes, 0, lens(0), Charset.defaultCharset())

      gl.glDeleteShader(shader)
      throw new RuntimeException("createAndCompileShader::Compilation failed: \n" + logString)
    }

    shader
  }

}
