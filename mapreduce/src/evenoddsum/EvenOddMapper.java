package evenoddsum;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class EvenOddMapper extends Mapper<Object, Text, Text, IntWritable> {

    private final static Text ODD = new Text("ODD");
    private final static Text EVEN = new Text("EVEN");

    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        
        String[] data = value.toString().split(" ");

        for (String num : data) {
            int number = Integer.parseInt(num.trim()); // Parse number

            if (number % 2 == 1) {
                context.write(ODD, new IntWritable(number));
            } else {
                context.write(EVEN, new IntWritable(number));
            }
        }
    }
}
