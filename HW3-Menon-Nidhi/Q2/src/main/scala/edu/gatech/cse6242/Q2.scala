package edu.gatech.cse6242

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.functions._

object Q2 {

	def main(args: Array[String]) {
    	val sc = new SparkContext(new SparkConf().setAppName("Q2"))
		val sqlContext = new SQLContext(sc)
		import sqlContext.implicits._

    	// read the file
    	val file = sc.textFile("hdfs://localhost:8020" + args(0))
		/* TODO: Needs to be implemented */
		
		val out = file.map(_.split("\t")).map(p => (p(0).toInt, p(2).toInt)).toDF("src","outweight")
        val in = file.map(_.split("\t")).map(p => (p(1).toInt, p(2).toInt)).toDF("tgt","inweight")

        val outgoing = out.filter("outweight >= 5").groupBy("src").agg(sum(out("outweight"))).withColumnRenamed("sum(outweight)", "outweight")
        val incoming = in.filter("inweight >= 5").groupBy("tgt").agg(sum(in("inweight"))).withColumnRenamed("sum(inweight)", "inweight")

        val res = incoming.as('a).join(outgoing.as('b), $"a.tgt" === $"b.src","full_outer").select(incoming("tgt"),(coalesce('inweight, lit(0)) - coalesce('outweight, lit(0)))).na.fill(0)
        
    	// store output on given HDFS path.
    	// YOU NEED TO CHANGE THIS
    	//file.saveAsTextFile("hdfs://localhost:8020" + args(1))
    	res.map(x => x.mkString("\t")).saveAsTextFile("hdfs://localhost:8020" + args(1))
  	}
}
