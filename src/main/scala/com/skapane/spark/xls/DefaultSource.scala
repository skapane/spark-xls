package com.skapane.spark.xls

import org.apache.spark.sql.sources.RelationProvider
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.sources.BaseRelation

class DefaultSource extends RelationProvider {

  override def createRelation(
    sqlContext: SQLContext,
    parameters: Map[String, String]): BaseRelation = {

    val path = parameters("path")

    val normalizeNames = parameters.get("normalizeNames") match {
      case Some(e) => e.toBoolean
      case _ => true
    }

    XlsRelation(
      path,
      parameters.get("sheet"),
      normalizeNames)(sqlContext)

  }
}   

 