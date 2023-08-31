package funscala.datastructures

/** Book's example */
sealed trait List[+A]
/** Data constructor of List */
case object Nil extends List[Nothing]
/** Data constructor of List */
case class Cons[+A](head: A, tail: List[A]) extends List[A]

object List {

  /** Book's example */
  def sum(ints: List[Int]): Int = ints match {
    case Nil ⇒ 0
    case Cons(x, xs) ⇒ x + sum(xs)
  }

  /** Book's example */
  def product(ds: List[Double]): Double = ds match {
    case Nil ⇒ 1.0
    case Cons(0.0, _) ⇒ 0.0
    case Cons(x, xs) ⇒ x * product(xs)
  }

  /** Book's example */
  def apply[A](as: A*): List[A] =
    if (as.isEmpty) Nil
    else Cons(as.head, apply(as.tail: _*))


  /** [CHAP-3][EXERCISE-01] pattern matching result */
  val x = List(1, 2, 3, 4, 5) match {
    case Cons(x, Cons(2, Cons(4, _))) ⇒ x
    case Nil ⇒ 42
    case Cons(x, Cons(y, Cons(3, Cons(4, _)))) ⇒ x + y
    case Cons(h, t) ⇒ h + sum(t)
    case _ ⇒ 101
  }

}
