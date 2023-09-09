package funscala.ch5

import org.scalatest.flatspec.AnyFlatSpec

class StreamTest extends AnyFlatSpec {

  "Empty Stream" should "not have any elements" in {
    val emptyStream = Stream.empty[Int]
    assert(emptyStream.isEmpty == true)
    assert(emptyStream.uncons.isDefined == false)
  }

  "Stream created with cons" should "have elements" in {
    val stream = Stream.cons(1, Stream.cons(2, Stream.empty))
    assert(stream.uncons.get._1 == 1)
    assert(stream.uncons.get._2.uncons.get._1 == 2)
    assert(stream.uncons.get._2.uncons.get._2.isEmpty == true)
    assert(stream.toList == List(1,2))
  }

  "Stream created with apply" should "have elements" in {
    val stream = Stream(1,2)
    assert(stream.uncons.get._1 == 1)
    assert(stream.uncons.get._2.uncons.get._1 == 2)
    assert(stream.uncons.get._2.uncons.get._2.isEmpty == true)
    assert(stream.toList == List(1,2))
  }

  // [CHAP-5][EXERCISE-01] implement toList on Stream
  "Stream" should "be converted into List" in {
    assert(Stream.empty[Int].toList == List[Int]())
    assert(Stream(1).toList == List(1))
    assert(Stream(1, 2, 3).toList == List(1, 2, 3))
  }

}