package edu.gatech.cse6242;
import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

public class Q1 {

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "Q1");

    /* TODO: Needs to be implemented */
    job.setJarByClass(Q1.class);
    job.setMapperClass(TokenizerMapper.class);
    job.setCombinerClass(IntSumReducer.class);
    job.setReducerClass(IntSumReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);

    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
  
  public static class TokenizerMapper
  extends Mapper<Object, Text, Text, IntWritable>{
      
      private IntWritable weight = new IntWritable(1);
      private Text word = new Text();
      
      public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
          String[] columns = value.toString().split("\\t");
          if(columns.length ==3) {
              String node = columns[1];
              String nodeWeight = columns[2];
              weight= new IntWritable(Integer.parseInt(nodeWeight));
              
              word.set(node);
              context.write(word, weight);
          }
      }
  }
  
  public static class IntSumReducer
  extends Reducer<Text,IntWritable,Text,IntWritable> {
      private IntWritable result = new IntWritable();
      
      public void reduce(Text key, Iterable<IntWritable> values,
                         Context context
                         ) throws IOException, InterruptedException {
          int smallest = Integer.MAX_VALUE;
          for (IntWritable val : values) {
              if( val.get()<smallest) {
            	  smallest = val.get();
              }
          }
          result.set(smallest);
          context.write(key, result);
      }
  }
}
