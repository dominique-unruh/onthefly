package de.unruh.onthefly.unsorted

import de.unruh.onthefly.unsorted.CachedComputation.{Hash, Hashed, O}
import io.reactivex.rxjava3.core.Observable

import scala.collection.mutable

class CachedComputation[Input, Output](f: Input => Observable[Output]) {
  def seen = new mutable.HashSet[Hash]

  def compute(input: Hashed[Input]) : O[Output] = {
    // TODO: lookup in cache

    // Dummy code for debugging
    val hash = input.hash
    if (hash == 0L) {
      println("Unhashed input")
    } else {
      println(s"Hash: $hash, seen: ${seen.contains(hash)}")
      seen += hash
    }

    f(input.value)
    // TODO: store in cache
  }
}

object CachedComputation {
  type Hash = Long
  type O[T] = Observable[T]

  case class Hashed[T](value: T, hash: Hash)

/*  sealed trait Result[T] {
    def map[U](f: T=>U) : Result[U]
  }
  case class Final[T](result: Hashed[T]) extends Result[T]
  object Final {
    def apply[T : Hasher](value: T): Final[T] = Final(Hasher.hash(value))
  }
  case class Update[T](result: Hashed[T]) extends Result[T]*/
}

trait Hasher[-T] {
  def hash(value : T) : Hash
}

object Hasher {
  def hash[T : Hasher](value : T): Hashed[T] = Hashed(value, implicitly[Hasher[T]].hash(value))
  implicit object dummyHasher extends Hasher[Any] {
    override def hash(value: Any): Hash = 0L
  }
}
