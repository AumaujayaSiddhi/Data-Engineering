package apriori;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class AprioriReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

	Integer support_count;
	
	@Override
	protected void setup(Reducer<Text, IntWritable, Text, IntWritable>.Context context)
			throws IOException, InterruptedException {
		support_count = Integer.parseInt(context.getConfiguration().get("support_count"));
		super.setup(context);
	}
	
	@Override
	public void reduce(Text key, Iterable<IntWritable> values, Context context)
			throws IOException, InterruptedException {
		int sum = 0;
		
		for (IntWritable value : values) {
			sum += value.get();
		}

		if (sum >= support_count) {
			context.write(key, new IntWritable(sum));
		}
	}

}
