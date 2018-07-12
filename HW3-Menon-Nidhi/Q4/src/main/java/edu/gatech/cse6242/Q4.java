package edu.gatech.cse6242;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import java.io.IOException;

public class Q4
{

  public static void main(String[] args) throws Exception
  {
    Configuration conf = new Configuration();
    Job job1 = Job.getInstance(conf, "Q4-part1");

    /* TODO: Needs to be implemented */
    job1.setJarByClass(Q4.class);
    job1.setMapperClass(TokenizerMapper.class);
    job1.setCombinerClass(IntSumReducer.class);
    job1.setReducerClass(IntSumReducer.class);
    job1.setOutputKeyClass(Text.class);
    job1.setOutputValueClass(IntWritable.class);
    FileInputFormat.addInputPath(job1, new Path(args[0]));
    FileOutputFormat.setOutputPath(job1, new Path("intermediate"));

    job1.waitForCompletion(true);
    Job job2 = Job.getInstance(conf, "Q4-part2");
    job2.setJarByClass(Q4.class);
    job2.setMapperClass(DegreeMapper.class);
    job2.setCombinerClass(IntSumReducer.class);
    job2.setReducerClass(IntSumReducer.class);
    job2.setOutputKeyClass(Text.class);
    job2.setOutputValueClass(IntWritable.class);
    FileInputFormat.addInputPath(job2, new Path("intermediate"));
    FileOutputFormat.setOutputPath(job2, new Path(args[1]));
    System.exit(job2.waitForCompletion(true) ? 0:1);
  }
  
  public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable>
  {
      private IntWritable add_weight = new IntWritable(1);
      private IntWritable sub_weight = new IntWritable(-1);
      private Text data = new Text();
      
      public void map(Object key, Text value, Context context) throws IOException, InterruptedException
      {
          String [] rows = value.toString().split("\t");
          if(rows.length == 2)
          {
              data.set(rows[0]);
              context.write(data, add_weight);
              data.set(rows[1]);
              context.write(data, sub_weight);
          }
      }
  }
  
  public static class DegreeMapper extends Mapper<Object, Text, Text, IntWritable>
  {
      private IntWritable degree = new IntWritable(1);
      private Text frequency = new Text();
      
      public void map(Object key, Text value, Context context) throws IOException, InterruptedException
      {
          String [] rows = value.toString().split("\\t");
          if(rows.length == 2)
          {
        	  frequency.set(rows[1]);
              context.write(frequency, degree);
          }
      }
  }
  
  public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable>
  {
      private IntWritable answer = new IntWritable();
      
      public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException
      {
          int sum = 0;
          for(IntWritable value : values)
          {
              sum = sum + value.get();
          }
          answer.set(sum);
          context.write(key, answer);
      }
  }
}
