package de.unruh.onthefly.unsorted

import java.awt._
import java.awt.image.BufferedImage
import java.util.Objects
import java.util.concurrent.TimeUnit

import de.unruh.onthefly.inlays.{Drawable, ImageDrawable, Inlay, ObservableInlay, TextDrawable}
import de.unruh.onthefly.unsorted.Latex2Image
import io.reactivex.rxjava3.core.{Observable, Single, SingleEmitter, SingleOnSubscribe}
import io.reactivex.rxjava3.internal.operators.observable.ObservableIntervalRange.IntervalRangeObserver

import scala.concurrent.{ExecutionContextExecutor, Future}

class FakeComputationInlay(val variable: String, val expr: Observable[Expression])
  extends ObservableInlay(FakeComputationInlay.computation(expr))

object FakeComputationInlay {
  import scala.jdk.FutureConverters._
  implicit private val ec: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global

  def computation(expr: Observable[Expression]): Observable[Drawable] = {
//    val drawable : Observable[Drawable] =
      for (e <- expr;
           latex = e.toLatex;
           image = Latex2Image.render(latex))
        yield ImageDrawable(image)
/*

    Observable.create[Drawable](emitter => drawable.onComplete { value =>
      emitter.onNext(value.get)
    })
*/
/*
    Observable.intervalRange(0, 2, 1, 1, TimeUnit.SECONDS)
      .map { i => if (i==0)
        val latex = expr.toLatex
        val image = Latex2Image.render(latex)
        TextDrawable("Rendering " + latex)
      else
        ImageDrawable(image)
      }
*/
  }
}
