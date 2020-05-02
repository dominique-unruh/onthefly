package de.unruh.onthefly.inlays

import java.awt.{Dimension, Graphics2D}

// TODO: should have dynamic width
case class TextDrawable(text: String) extends Drawable {
  override val dimension: Dimension = new Dimension(1000, 50);

  override def paint(g: Graphics2D): Unit =
    g.drawString(text, 20, 30)
}
