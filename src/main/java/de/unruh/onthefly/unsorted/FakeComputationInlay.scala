package de.unruh.onthefly.unsorted

import java.awt._
import java.util.Objects
import java.util.concurrent.TimeUnit

import de.unruh.onthefly.inlays.{Drawable, ImageDrawable, Inlay, ObservableInlay, TextDrawable}
import de.unruh.onthefly.unsorted.Latex2Image
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.internal.operators.observable.ObservableIntervalRange.IntervalRangeObserver

class FakeComputationInlay(val variable: String, val expr: Expression)
  extends ObservableInlay(FakeComputationInlay.computation(expr))

object FakeComputationInlay {
  def computation(expr: Expression): Observable[Drawable] = {
    val latex = expr.toLatex
    val image = Latex2Image.render(latex)
    Observable.intervalRange(0, 2, 1, 1, TimeUnit.SECONDS)
      .map { i => if (i==0)
        TextDrawable("Rendering " + latex)
      else
        ImageDrawable(image)
      }
  }
}
