package se.gigurra.renderer.glimpl.megavbo

import javax.media.opengl.GL3ES3
import se.gigurra.renderer.Model
import se.gigurra.renderer.glimpl.GlShaderProgram
import se.gigurra.renderer.glimpl.GlSlConfiguration
import java.util.IdentityHashMap

class MegaVbo(_gl_init: GL3ES3, val program: GlShaderProgram, val nVerticesMax: Int = 5000000)(implicit cfg: GlSlConfiguration) {

  private val attributeBuffers = new MegaVboBufferSet(_gl_init, program, nVerticesMax)
  private val cache = new IdentityHashMap[Model, MegaVboModelDescriptor]

  final def startFrame(gl: GL3ES3) {
    frontSet().startFrame(gl)
  }

  final def endFrame(gl: GL3ES3) {

    // 1. Copy all subParts that have been drawn this frame to the other buffer
    // ---- Ofc also update their states

    // 2. Remove unused descriptors

    // 3. Reset all draw counters on descriptors
    /*    for ((k, v) <- loadedModels) {
      v.resetDrawCounter()
    }*/

    // 4. swap buffer indices
  }

  final def getModel(gl: GL3ES3, model: Model): MegaVboModelDescriptor = {

    val cached = cache.get(model)
    if (cached != null) {
      cached.asInstanceOf[MegaVboModelDescriptor]
    } else {
      val out = attributeBuffers.add(gl, model)
      println(s"Added model $model at vertex offset ${out.vertexOffset} with ${model.vertexCount} vertices")
      cache.put(model, out)
      out
    }
  }

  final def dispose(gl: GL3ES3) {
    frontSet().dispose(gl)
    //backSet().dispose()
  }

  private def frontSet(): MegaVboBufferSet = {
    attributeBuffers
  }

}