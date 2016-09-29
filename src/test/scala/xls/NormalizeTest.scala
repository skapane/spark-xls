package xls

import org.junit.Test
import org.junit.Assert
import com.skapane.spark.xls.XlsRelation

class NormalizeTest {

  //  ,;{}()\n\t=]".r

  @Test
  def test() = {
    val tests = Map(
      "foo" -> "foo",
      "fo o" -> "fo_o")

    tests.foreach { e =>
      Assert.assertEquals(XlsRelation.normalize(e._1), e._2)
    }

  }

}