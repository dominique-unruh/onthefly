package de.unruh.onthefly.inlays

import java.awt.{Dimension, Graphics2D}

trait Drawable {
  val dimension : Dimension
  def paint(g: Graphics2D) : Unit
}
