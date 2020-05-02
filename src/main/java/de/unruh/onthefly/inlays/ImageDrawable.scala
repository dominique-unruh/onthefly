package de.unruh.onthefly.inlays

import java.awt.{Dimension, Graphics2D}
import java.awt.image.BufferedImage

case class ImageDrawable(image: BufferedImage) extends Drawable {
  override val dimension: Dimension =
    new Dimension(image.getWidth(), image.getHeight())

  override def paint(g: Graphics2D): Unit =
    g.drawImage(image, 0, 0, null)
}
