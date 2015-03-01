package se.gigurra.renderer.glimpl

import javax.media.opengl.GL3ES3

class GlSlConfiguration(
  val glSlVersion: String, // e.g. 330
  val extensions: Set[String] // e.g. GL_ARB_shader_draw_parameters
  ) {

  def filter(gl: GL3ES3, code: String): String = {
    val builder = new StringBuilder(code.length + 100)
    builder.append(s"#version $glSlVersion \n")
    val (enabled, notEnabled) = (extensions ++ GlExtensions.ALL_KNOWN).partition(isExtensionActive(gl, _))
    for (e <- enabled) {
      builder.append(s"#extension $e : require \n")
      builder.append(s"#define ${e}_enabled 1\n")
    }
    builder.append(rewriteForUnavailableExtensions(notEnabled, code))
    rewriteMaxConstants(gl, builder.toString())
  }

  def isExtensionActive(gl: GL3ES3, extension: String): Boolean = {
    extensions.contains(extension) && gl.isExtensionAvailable(extension)
  }

  def rewriteForUnavailableExtensions(notEnabled: Set[String], unmodifiedCode: String): String = {
    var code = unmodifiedCode
    val rewriteFunc = rewriteForUnavailableExtension()
    // Can we rewrite the glsl code to run anyway?
    for (e <- notEnabled) {
      if (rewriteFunc.isDefinedAt(e)) {
        code = rewriteFunc.apply(e)(code)
      } else {
        throw new RuntimeException(s"GL extension '$e' is not not enabled, and don't know how to rewrite your GLSL code to compensate :/")
      }
    }
    code
  }

  def supportsMultiDraw(gl: GL3ES3): Boolean = {
    gl.isGL3() && isExtensionActive(gl, GlExtensions.GL_ARB_shader_draw_parameters)
  }

  def rewriteMaxConstants(gl: GL3ES3, unmodifiedCode: String): String = {
    val constants = new GlIntegers(gl)
    val mappings = Map(
      "<max_uniform_mat4_array_len>" -> constants.maxUniformMat4ArrayLen,
      "<max_uniform_vec4_array_len>" -> constants.maxUniformVec4ArrayLen,
      "<max_uniform_float_array_len>" -> constants.maxUniformFloatArrayLen)
    var code = unmodifiedCode
    for ((k, v) <- mappings) {
      if (code.contains(k)) {
        code = code.replaceAllLiterally(k, v.toString)
      }
    }
    code
  }

  def rewriteForUnavailableExtension(): PartialFunction[String, String => String] = {
    case GlExtensions.GL_ARB_shader_draw_parameters => { code =>
      if (code.contains("gl_DrawIDARB")) {
        System.err.println(s"Warning, gl extension ${GlExtensions.GL_ARB_shader_draw_parameters} not enabled, but constant gl_DrawID used")
        System.err.println(" --> Rewriting source code, replacing all 'gl_DrawIDARB's with '0's")
        code.replaceAllLiterally("gl_DrawIDARB", "0")
      } else {
        code
      }
    }
    case "DummyTestExtension" => { code =>
      println("Hello, you used the GlSlConfiguration.rewriteForUnavailableExtension() test function :)")
      code
    }
  }

}

object GlSlConfiguration {
  def v330(extensions: String*): GlSlConfiguration = {
    println(s"Loading GL 3.3 configuration with extensions: ${extensions.mkString(", ")}")
    new GlSlConfiguration("330", extensions.toSet)
  }
}

object GlExtensions {
  val GL_ARB_shader_draw_parameters = "GL_ARB_shader_draw_parameters"
  val ALL_KNOWN = Set(GL_ARB_shader_draw_parameters)
}