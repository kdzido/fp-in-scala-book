package funscala.datastructures

/** Book's example */
sealed trait Tree[+A]
case class Leaf[A](value: A) extends Tree[A]
case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]

object Tree {

  /** [CHAP-3][EXERCISE-25] implement size on Tree */
  def size[A](t: Tree[A]): Int = t match {
    case Leaf(_) ⇒ 1
    case Branch(t1, t2) ⇒ 1 + size(t1) + size(t2)
  }

  /** [CHAP-3][EXERCISE-26] implement maximum on Tree.
   * NOTE non-tail recursive, for balanced trees shall not be much of issue due to O(log n) */
  def maximum(t: Tree[Int]): Int = t match {
    case Leaf(x) ⇒ x
    case Branch(t1, t2) ⇒ maximum(t1) max maximum(t2)
  }

}