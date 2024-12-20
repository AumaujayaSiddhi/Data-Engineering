package apriori;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class AprioriDriver {

	public static void main(String[] args) throws Exception {
		Configuration configuration = new Configuration();
		configuration.set("k", args[0]);
		configuration.set("fs.defaultFS", "hdfs://localhost:9000");

		Job job = Job.getInstance(configuration, "Apriori Algorithm......");

		job.setJarByClass(AprioriDriver.class);
		if (Integer.parseInt(args[0]) > 1) {
			job.setMapperClass(FindKFrequentItemsetMapper.class);
		} else {
			job.setMapperClass(FindOneFrequentItemsetMapper.class);
		}

		job.setReducerClass(AprioriReducer.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		FileInputFormat.addInputPath(job, new Path(args[1]));
		FileOutputFormat.setOutputPath(job, new Path(args[2]));

		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}
