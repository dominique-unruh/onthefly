package de.unruh.onthefly

import io.reactivex.rxjava3.core.{Flowable, FlowableSubscriber, Observable, Observer}
import System.out.println
import java.util

import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.processors.{BehaviorProcessor, MulticastProcessor, PublishProcessor}
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.{BehaviorSubject, PublishSubject, Subject}
import io.reactivex.rxjava3.subscribers.DefaultSubscriber
import org.reactivestreams.{Subscriber, Subscription}

import scala.collection.mutable.ListBuffer

object Scratch {
  def sleep(secs: Int, what: String = "<unnamed>"): Unit =
    Thread.sleep(secs*1000L)
//  catch {
//    case e : InterruptedException =>
//      println(s"Sleep interruped ($secs secs, $what)")
//  }


  def slow(i:Int): Int = {
    try {
      println(s"slow($i)")
      sleep(10, s"slow($i)")
      println(s"slow($i) done")
      i + 1
    } finally {
      println(s"slow($i) finally")
    }
  }

  def main(args: Array[String]): Unit = {
    println("Starting")

    val flowable0 = Observable.just(1)
      .observeOn(Schedulers.computation())
      .map(slow)
      .map(slow)
      .observeOn(Schedulers.computation())
    val processor = new TestSubject
    val flowable = processor.observeOn(Schedulers.computation())
    flowable0.subscribe(processor)

    println("Created flowable")

    sleep(3)

    println("Subscribing")
    val disposable1 = flowable.subscribe((result:Int) => println(s"Result 1: $result"),
      (t:Throwable) => println(s"Error 1: $t"))
    val disposable2 = flowable.subscribe((result:Int) => println(s"Result 2: $result"),
      (t:Throwable) => println(s"Error 2: $t"))
    println("Subscribed")

    sleep(3)

    println("Disposing")
    disposable1.dispose()
    disposable2.dispose()
    println("Disposed")

    sleep(50)

    print("Done")
  }
}

class TestSubject extends Subject[Int] {
  type O = Observer[_ >: Int]

  override def hasObservers: Boolean = !observers.isEmpty

  override def hasThrowable: Boolean = ???

  override def hasComplete: Boolean = ???

  override def getThrowable: Throwable = ???

  var disposable : Disposable = _
  override def onSubscribe(d: Disposable): Unit = {
    println("onSubscribe")
    disposable = d
  }

  override def onNext(t: Int): Unit =
    for (o <- observers)
      o.onNext(t)

  override def onError(e: Throwable): Unit =
    for (o <- observers)
      o.onError(e)

  override def onComplete(): Unit =
    for (o <- observers)
      o.onComplete()

  val observers = new ListBuffer[O]

  def remove(observer: Observer[_ >: Int]): Unit = observers -= observer

  override def subscribeActual(observer: Observer[_ >: Int]): Unit = {
    println("SubscribeActual")
    observer.onSubscribe(new Disposable {
      override def dispose(): Unit = {
        remove(observer)
        if (observers.isEmpty && disposable != null)
          disposable.dispose()
      }
      override def isDisposed: Boolean = ???
    })
    observers += observer
  }
}

class TestProcessor extends Flowable[Int] with FlowableSubscriber[Int] {
  type S = Subscriber[_ >: Int]
  val subscribers = new ListBuffer[S]()

  override def subscribeActual(subscriber: S): Unit = {
    println("subscribeActual")
    subscribers += subscriber
    subscriber.onSubscribe(new Subscription {
      override def request(n: Long): Unit =
        source.request(n)
      override def cancel(): Unit = remove(subscriber)
    })
  }

  var source : Subscription = _

  def remove(subscriber: S): Unit = {
    subscribers -= subscriber
    if (subscribers.isEmpty && source != null)
      source.request(0)
  }

  override def onSubscribe(s: Subscription): Unit = {
    println("onSubscribe")
    source = s
  }

  override def onNext(t: Int): Unit =
    for (s <- subscribers)
      s.onNext(t)

  override def onError(t: Throwable): Unit =
  for (s <- subscribers)
    s.onError(t)

  override def onComplete(): Unit =
    for (s <- subscribers)
      s.onComplete()
}