package de.unruh.onthefly.inlays

import java.awt.{Dimension, Graphics2D}

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Consumer

class ObservableInlay(observable: Observable[Drawable], initial: Drawable = LoadingDrawable) extends Inlay {
  var current: Drawable = initial
  observable.subscribe({ drawable => current = drawable; changed() } : Consumer[Drawable])

  override def getInlayDimension(width: Int): Dimension = current.dimension

  override def paintInlay(g: Graphics2D, width: Int, height: Int): Unit =
    current.paint(g)
}

object ObservableInlay {
  def apply(drawable : Drawable): ObservableInlay =
    new ObservableInlay(Observable.empty[Drawable], drawable)
  def apply(text : String): ObservableInlay =
    ObservableInlay(TextDrawable(text))
}