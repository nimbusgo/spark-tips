import java.io.NotSerializableException

import com.holdenkarau.spark.testing.SharedSparkContext
import org.apache.spark.SparkException
import org.scalatest.FunSuite
import org.scalatest.Matchers

import scala.util.Random

/**
  * Created by ngoehausen on 9/1/16.
  */
class Examples extends FunSuite with SharedSparkContext with Matchers  {

  test("bad"){

    val numbers = sc.parallelize(1 to 20)

    val pairs = sc.parallelize(Random.alphanumeric.take(20)).zipWithIndex

    val result = numbers.map(number => pairs.filter{case (c, num) => num == number}.first())

    an [SparkException] should be thrownBy {
      result.count()
    }
  }

  test("timing"){

    val singlePart = sc.parallelize(1 to 20, 1)

    val mappedResult = singlePart.map(x => System.nanoTime()).distinct()
    assert(mappedResult.count() == 20)

    val singleResult = singlePart.mapPartitions(nums => {
      val t = System.nanoTime()
      nums.map(x => t)
    })

    assert(singleResult.distinct().count() == 1)

    val multiPart = sc.parallelize(1 to 20, 5)

    val multiResult = multiPart.mapPartitions(nums => {
      val t = System.nanoTime()
      nums.map(x => t)
    })
    assert(multiResult.distinct().count() == 5)

    val t = System.nanoTime()
    val result = multiPart.map(x => t).distinct()
    assert(result.count() == 1)
  }

  test("classes"){

    class Basic() {
      val nums = sc.parallelize(1 to 10)
      val doubled = nums.map(x => x * 2)
    }

    val basic = new Basic()
    assert(basic.doubled.sum > 0)

    class DriverParam(maxNum : Int){
      val nums = sc.parallelize(1 to maxNum)
      val doubled = nums.map(x => x * 2)
    }

    val driverParam = new DriverParam(20)
    assert(driverParam.doubled.sum > 0)

    class GonnaBreak(mult: Int){
      val nums = sc.parallelize(1 to 10)
      val multiplied = nums.map(x => x * mult)
    }
    an [SparkException] should be thrownBy {
      val gonnaBreak = new GonnaBreak(3)
      gonnaBreak.multiplied.sum > 0
    }

    class SerializableFix(mult: Int) extends Serializable {
      val nums = sc.parallelize(1 to 10)
      val multiplied = nums.map(x => x * mult)
    }

    val serializableFix = new SerializableFix(3)
    assert(serializableFix.multiplied.sum > 0)

    class Captured(mult: Int) {
      val nums = sc.parallelize(1 to 10)
      val multiplied = {
        val capturedMult = mult
        nums.map(x => x * capturedMult)
      }
    }

    val captured = new Captured(3)
    assert(captured.multiplied.sum > 0)


  }

}
