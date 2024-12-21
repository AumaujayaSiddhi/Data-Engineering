package apriori;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class FindOneFrequentItemsetMapper extends Mapper<Object, Text, Text, IntWritable> {

	@Override
	protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {

		StringTokenizer strTokens = new StringTokenizer(value.toString(), ",");

		while (strTokens.hasMoreTokens()) {
			Text item = new Text("[" + strTokens.nextToken() + "] ---> ");
			context.write(item, new IntWritable(1));
		}
	}
}
