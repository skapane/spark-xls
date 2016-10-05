package xls

import org.junit.Test
import org.apache.spark.sql.SparkSession
import org.junit.Assert
import scala.collection.JavaConversions._

class XlsRelationTest {

  val spark = SparkSession
    .builder()
    .master("local")
    .appName("test")
    .getOrCreate()

  def load(file: String) = {
    val s = s"src/test/resources/${file}.xlsx"
    println(s"loading: $s")

    val df = spark.read.format("xls")
      .load(s)

    df.printSchema()
    df
  }

  @Test
  def empty() = {

    val df = load("empty")

    Assert.assertEquals(df.columns.length, 0)

    Assert.assertEquals(df.count(), 0)

  }

  @Test
  def header() = {

    val df = load("header")

    Assert.assertArrayEquals(df.columns.toArray[Object], Array("col1", "col2").toArray[Object])

    Assert.assertEquals(df.count(), 0)

  }

  @Test
  def withNull() = {

    val df = load("withNull")

    val rows = df.collect()

    Assert.assertTrue(rows(0).isNullAt(1))
    Assert.assertTrue(rows(1).isNullAt(0))

  }

}