package apriori;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.Counters.Counter;
import org.apache.hadoop.mapreduce.Mapper;

public class FindKFrequentItemsetMapper extends Mapper<Object, Text, Text, IntWritable> {

	LinkedHashSet<String> remaining_unique_items = new LinkedHashSet<String>();
	LinkedList<String> combinations_of_unique_items = new LinkedList<String>();
	Integer k;

	@Override
	protected void setup(Mapper<Object, Text, Text, IntWritable>.Context context)
			throws IOException, InterruptedException {

		// Getting a configuration....
		Configuration configuration = context.getConfiguration();
		
		// Get the value of k
		k = Integer.parseInt(configuration.get("k"));
		
		// Creating a file system object of configuration......
		FileSystem filesystem = FileSystem.get(configuration);
		// Path of previous algorithm pass.....
		Path kfrequent_previous = new Path("/user/MahaaGURU/output"+((k-1 == 0) ? "" : k-1)+"/part-r-00000");
		
		// Open the file at output path and read it
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(filesystem.open(kfrequent_previous)));

		// The Frequent itemsets output file will be something like below
		// [A,B] ---> 4. So need to remove '[',']' and consider the string
		// before first '-' and add those items into remaining unique items
		for (String line; (line = bufferedReader.readLine()) != null;) {
			line = line.replaceAll("[\\[\\]]", "");
			String[] items = line.substring(0, line.indexOf('-')).trim().split(",");
			remaining_unique_items.addAll(Arrays.asList(items));
		}
		bufferedReader.close();

		// Checking if number of unique items is less than k
		if (remaining_unique_items.size() <= k) {
			Counter counter = (Counter) context.getCounter("AprioriGroup", "AprioriStopJob");
			counter.increment(1);
		}
		else {
			// Make combinations of unique items. I used apache math library.....
			List<String> list_of_remaining_unique_items = new ArrayList<>(remaining_unique_items);
			Iterator<int[]> k_sized_combinations_of_unique_items = CombinatoricsUtils
					.combinationsIterator(list_of_remaining_unique_items.size(), k);
			while (k_sized_combinations_of_unique_items.hasNext()) {
				int[] next_combination = k_sized_combinations_of_unique_items.next();
				StringBuilder combinationString = new StringBuilder();
				for (int i = 0; i < next_combination.length; i++) {
					combinationString.append(list_of_remaining_unique_items.get(next_combination[i])
							+ ((i < next_combination.length - 1) ? "," : ""));
				}
				combinations_of_unique_items.add(combinationString.toString());
			}
		}
	}

	@Override
	protected void map(Object key, Text value, Mapper<Object, Text, Text, IntWritable>.Context context)
			throws IOException, InterruptedException {

		String[] record_split_by_comma = value.toString().split(",");
		for (String unique_item : combinations_of_unique_items) {
			String[] unique_item_array = unique_item.split(",");

			Set<String> recordSet = new HashSet<>(Arrays.asList(record_split_by_comma));
			Set<String> uniqueItemSet = new HashSet<>(Arrays.asList(unique_item_array));

			// Check if unique_item_array is a subset of record_split_comma
			if (recordSet.containsAll(uniqueItemSet)) {
				Text item = new Text("[" + unique_item + "] ---> ");
				context.write(item, new IntWritable(1));
			}

		}
	}

}
