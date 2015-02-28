package se.gigurra.renderer.glimpl

import com.jogamp.opengl.util.Animator

import javax.media.opengl.GLAutoDrawable

protected object GlRenderThread {
  private var animator: Animator = null
  private var n = 0

  def register(surface: GLAutoDrawable) = synchronized {
    if (animator == null) {
      animator = new Animator
      animator.add(surface)
      animator.start()
    } else {
      animator.add(surface)
    }
    n += 1
  }

  def unregister(surface: GLAutoDrawable) = synchronized {
    animator.remove(surface)
    n -= 1
    if (n == 0) {
      animator.stop()
      animator = null
    }
  }

  def getFps(): Float = {
    if (animator != null) {
      if (animator.getUpdateFPSFrames == 0)
        animator.setUpdateFPSFrames(100, null)
      animator.getLastFPS
    } else {
      0.0f
    }
  }

}
