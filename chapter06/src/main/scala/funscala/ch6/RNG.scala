package funscala.ch6

import scala.annotation.tailrec

/** Book's example */
trait RNG {
  def nextInt: (Int, RNG)
}

//type Rand[+A] = RNG ⇒ (A, RNG)
type Rand[+A] = State[RNG,A]

object RNG {
  /** Book's example */
  def simple(seed: Long): RNG = new RNG {
    def nextInt = {
      val seed2 = (seed*0x5DEECE66DL + 0xBL) &
        ((1L << 48) - 1)
      ((seed2 >>> 16).asInstanceOf[Int],
        simple(seed2))
    }
  }

  /** Book's example */
  val int: Rand[Int] = State(_.nextInt)

  /** Book's example */
  def unit[A](a: A): Rand[A] = State(rng ⇒ (a, rng))

  /** [CHAP-6][EXERCISE-10] re-implement map, map2 in terms of flatMap */
  def map[A,B](s: Rand[A])(f: A ⇒ B): Rand[B] =  flatMap(s)(a ⇒ RNG.unit(f(a)))

  /** [CHAP-6][EXERCISE-07] implement map2 */
  /** [CHAP-6][EXERCISE-10] re-implement map, map2 in terms of flatMap */
  def map2[A,B,C](ra: Rand[A], rb: Rand[B])(f: (A,B) ⇒ C): Rand[C] = flatMap(ra)(a ⇒ {
    flatMap(rb)(b ⇒ RNG.unit(f(a, b)))  // this line's is same as map
  })



  /** [CHAP-6][EXERCISE-09] implement flatMap and re-implement positiveInt */
  def flatMap[A,B](f: Rand[A])(g: A ⇒ Rand[B]): Rand[B] = State(rng ⇒ {
    val (a, rng2) = f.run(rng)
    g(a).run(rng2)
  })

  /** [CHAP-6][EXERCISE-08] (hard) implement sequence of Rands */
  def sequence[A](fs: List[Rand[A]]): Rand[List[A]] = State(rng ⇒ {
    @tailrec
    def go(l: List[Rand[A]], acc: List[A], r: RNG): (List[A], RNG) = {
      l match {
        case Nil ⇒ (acc, r)
        case h :: t ⇒ {
          val (h2: A, r2: RNG) = h.run(r)
          go(t, h2 :: acc, r2)
        }
      }
    }

    val l2: (List[A], RNG) = go(fs, List[A](), rng)
    (l2._1.reverse, l2._2)
  })

  /** [CHAP-6][EXERCISE-01] implement random positiveInt */
  def positiveInt: Rand[Int] = State(rng ⇒ {
    val (next, rng2) = rng.nextInt
    if (next == Int.MinValue) positiveInt.run(rng2) else (next.abs, rng2)
  })
  def positiveInt_2: Rand[Int] = State(rng ⇒ {
    val (next, rng2) = rng.nextInt
    if (next == Int.MinValue) positiveInt.run(rng2) else (next.abs, rng2)
  })

  def positiveInt_3: Rand[Int] =
    flatMap(int)(a ⇒
      if (a != Int.MinValue) RNG.unit(a.abs) else positiveInt_3)

  /** [CHAP-6][EXERCISE-05] generate Int between 0 and n inclusive in terms of map */
  def positiveMax(n: Int): Rand[Int] = RNG.map(positiveInt_2)(a ⇒ (a / (Int.MaxValue / (n+1))))

  /** [CHAP-6][EXERCISE-02] implement random double */
  def double(rng: RNG): (Double, RNG) = {
    val (i1, rng2) = positiveInt.run(rng)
    (i1.toDouble * (1.0 / Int.MaxValue.toDouble), rng2)
  }

  /** [CHAP-6][EXERCISE-06] re-implement double in terms of map */
  def double_2: Rand[Double] = RNG.map(positiveInt_2)(i ⇒ i.toDouble * (1.0 / Int.MaxValue.toDouble))

  /** [CHAP-6][EXERCISE-03] implement random pairs (Int,Double), (Double,Int), triple (Double,Double,Double) */
  def intDouble(rng: RNG): ((Int, Double), RNG) = {
    val (i1, rng2) = positiveInt.run(rng)
    val (d2, rng3) = double(rng2)
    ((i1,d2), rng3)
  }

  /** [CHAP-6][EXERCISE-03] implement random pairs (Int,Double), (Double,Int), triple (Double,Double,Double) */
  def doubleInt(rng: RNG): ((Double, Int), RNG) = {
    val (d1, rng2) = double(rng)
    val (i2, rng3) = positiveInt.run(rng2)
    ((d1, i2), rng3)
  }

  /** [CHAP-6][EXERCISE-03] implement random pairs (Int,Double), (Double,Int), triple (Double,Double,Double) */
  def double3(rng: RNG): ((Double, Double, Double), RNG) = {
    val (d1, rng2) = double(rng)
    val (d2, rng3) = double(rng2)
    val (d3, rng4) = double(rng3)
    ((d1, d2, d3), rng4)
  }

  /** [CHAP-6][EXERCISE-04] implement positiveInt */
  def ints(count: Int)(rng: RNG): (List[Int], RNG) = {
    @tailrec
    def go(c: Int, acc: (List[Int], RNG), r: RNG): (List[Int], RNG) = {
      if (c <= 0) acc
      else {
        val (i, rng2) = positiveInt.run(r)
        go(c-1, (i :: acc._1, acc._2), rng2)
      }
    }
    val reversed = go(count, (List[Int](), rng), rng)
    (reversed._1.reverse, reversed._2)
  }


  /** [CHAP-6][EXERCISE-08] (hard) reimplement ints with sequence of Rands */
  def ints_2(count: Int): Rand[List[Int]] = {
    val l =  List.fill(count)(RNG.map2(RNG.unit(0), positiveInt_2)((_, b) ⇒ b)).toList
    RNG.sequence(l)
  }

}
