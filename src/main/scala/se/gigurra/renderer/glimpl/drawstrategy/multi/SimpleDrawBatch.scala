package se.gigurra.renderer.glimpl.drawstrategy.multi

import com.jogamp.common.nio.Buffers

import javax.media.opengl.GL3
import se.gigurra.renderer.Color
import se.gigurra.renderer.Transform
import se.gigurra.renderer.glimpl.megavbo.MegaVboModelDescriptor
import se.gigurra.renderer.glimpl.uniforms.SimpleUniform

class SimpleDrawBatch(val maxItems: Int) {

  private val items = new Array[MegaVboModelDescriptor](maxItems)
  private val vertexOffsets = Buffers.newDirectIntBuffer(maxItems)
  private val vertexCounts = Buffers.newDirectIntBuffer(maxItems)
  private val colorScales = Buffers.newDirectFloatBuffer(maxItems * 4)
  private val transforms = Buffers.newDirectFloatBuffer(maxItems * 16)

  private var nItems = 0
  private var primType = 0

  final def add(
      item: MegaVboModelDescriptor, 
      transform: Transform, 
      colorScale: Color) {
    
    items(nItems) = item

    vertexOffsets.put(item.vertexOffset)
    vertexCounts.put(item.nVertices)
    colorScales.put(colorScale.array)
    transforms.put(transform.array)

    nItems += 1
    primType = item.primType
  }

  final def draw(
      gl3: GL3,
      colorScaleUniform: SimpleUniform, 
      transformUniform: SimpleUniform) {
    vertexOffsets.rewind()
    vertexCounts.rewind()
    colorScales.rewind()
    transforms.rewind()
    colorScaleUniform.setVectors(gl3, colorScales, nItems)
    transformUniform.setMatrices(gl3, transforms, nItems)
    gl3.glMultiDrawArrays(primType, vertexOffsets, vertexCounts, nItems)
  }

  final def reset() {
    nItems = 0
  }

  final def canAdd(item: MegaVboModelDescriptor): Boolean = {
    nItems * primType == nItems * item.primType && nItems < maxItems
  }

  final def nonEmpty(): Boolean = {
    nItems != 0
  }

}