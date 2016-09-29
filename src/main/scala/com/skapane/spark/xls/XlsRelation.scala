package com.skapane.spark.xls

import collection.JavaConverters._
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.sources.BaseRelation
import org.apache.spark.sql.sources.TableScan
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StructField
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.FileInputStream
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.Row
import org.apache.spark.sql.sources.RelationProvider
import org.apache.poi.ss.usermodel.{ Row => XRow }
import org.apache.poi.ss.usermodel.{ Row => XRow }
import scala.util.matching.Regex

/**
 *
 * @param path path of the xls file
 * @param normalizeNames normalize column names: replace invalid chars
 *
 */
case class XlsRelation(
  path: String,
  normalizeNames: Boolean = true)(@transient val sqlContext: SQLContext)
    extends BaseRelation with TableScan {

  /**
   * A header
   *
   * @param name name of the header
   * @param col column number
   */
  case class Header(name: String, col: Int)

  // normalizer of column names
  val normalizer: (String) => String = {
    normalizeNames match {
      case true  => XlsRelation.normalize
      case false => identity[String] //{ s => s }
    }
  }

  // get first PortableDataStream
  val input = sqlContext
    .sparkContext
    .binaryFiles(path)
    .map(_._2)
    .first()

  // open workbook
  val wb = WorkbookFactory.create(input.open())

  // get cell evaluator
  val evaluator = wb.getCreationHelper().createFormulaEvaluator()

  // get headers
  val headers = extractHeader()

  // extract header from first row
  def extractHeader(): Seq[Header] = {
    val it = wb.getSheetAt(0).rowIterator()
    if (it.hasNext()) {
      it.next().cellIterator().asScala.map { x =>
        Header(
          normalizer.apply(evaluator.evaluate(x).formatAsString()),
          x.getAddress.getColumn)
      }.toSeq
    } else {
      Seq.empty[Header]
    }
  }

  override def schema = {
    // 
    StructType(headers.map { x =>
      StructField(x.name, StringType, nullable = true)
    })
  }

  override def buildScan() = {

    def toRow(r: XRow, idx: Int): Row = {

      val values = headers.map { h =>
        val c = r.getCell(h.col)
        if (c != null) {
          val r = evaluator.evaluate(c)
          if (r != null) {
            r.formatAsString()
          } else {
            null
          }
        } else {
          null
        }
      }

      Row.fromSeq(values)

    }

    sqlContext.sparkContext.parallelize(
      wb.getSheetAt(0)
        .rowIterator()
        .asScala
        .drop(1) // remove header
        .zipWithIndex
        .map(x => toRow(x._1, x._2)).toSeq)
  }
}

object XlsRelation {

  val invalid = "[ ,;{}()\n\t=\"]".r

  def normalize(s: String) = invalid.replaceAllIn(s, "_").stripPrefix("_").stripSuffix("_")

  //.replaceAll(" ,;{}()\n\t=", "_")
}
