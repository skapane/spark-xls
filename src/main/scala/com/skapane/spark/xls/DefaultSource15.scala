package com.skapane.spark.xls

import org.apache.spark.sql.sources.DataSourceRegister

/* Extension of DefaultSource (which is Spark 1.3 and 1.4 compatible) for Spark 1.5 compatibility.
 * Since the class is loaded through META-INF/services we can decouple the two to have
 * Spark 1.5 byte-code loaded lazily.
 *
 * This trick is adapted from spark elasticsearch-hadoop data source:
 * <https://github.com/elastic/elasticsearch-hadoop>
 */
class DefaultSource15 extends DefaultSource with DataSourceRegister {

  /**
   * Short alias for spark-xls data source.
   */
  override def shortName(): String = "xls"
}