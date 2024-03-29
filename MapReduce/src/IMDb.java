/*                   
  Sample input: tt0108778;tvSeries;Friends;1994;8.9;Comedy,Romance
  Sample output: [2011-2020],Comedy;Romance	294
*/

//Java Basic Imports
import java.io.IOException;
import java.util.Arrays;
import javax.naming.Context;
// Hadoop Related Imports
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

// Main Class 
public class IMDb {
  /*
   * Mapper class
   * expected input data types : Key Object, Value Text
   * expected output data types: Key Text, Value IntWriteable
   */
  public static class IMDbMapper extends Mapper<Object, Text, Text, IntWritable> {
    // Declaring class Variables
    private final static IntWritable one = new IntWritable(1);
    private Text genreAndPeriod = new Text();

    // Function to check the genre of the movie
    public static String CheckGenre(String[] genres) {
      if (Arrays.asList(genres).contains("Action") && Arrays.asList(genres).contains("Thriller")) {
        return "Action;Thriller";
      } else if (Arrays.asList(genres).contains("Adventure") && Arrays.asList(genres).contains("Drama")) {
        return "Adventure;Drama";
      } else if (Arrays.asList(genres).contains("Romance") && Arrays.asList(genres).contains("Comedy")) {
        return "Comedy;Romance";
      } else {
        return null;
      }
    }

    // Function to check the Year of the movie
    public static String CheckPeriod(Integer year) {
      if (year >= 1991 && year <= 2000) {
        return "[1991-2000]";
      } else if (year >= 2001 && year <= 2010) {
        return "[2001-2010]";
      } else if (year >= 2011 && year <= 2020) {
        return "[2011-2020]";
      } else {
        return null;
      }
    }

    // Map function
    // the line from the txt file is in the value variable
    public void map(Object key, Text value, Context context)
        throws IOException, InterruptedException {
      String[] genresList;
      String period;
      String genre;
      // Spilt the string into array for easy accessability
      String[] strToArr = value.toString().split(";");
      // check if the entry is a movie with high rating
      if (strToArr[1].equals("movie") && Double.parseDouble(strToArr[4]) >= 7.5) {
        genresList = strToArr[5].split(",");
        // get the year period
        try {
          period = CheckPeriod(Integer.parseInt(strToArr[3]));
        } catch (NumberFormatException e) {
          return;
        }
        // get the genre type
        genre = CheckGenre(genresList);
        // set the key in the desired format and pass to the reducer
        if (period != null && genre != null) {
          genreAndPeriod.set(period + "," + genre);
          context.write(genreAndPeriod, one);
        }
      }
    }
  }

  /*
   * Reducer class
   * expected input data types : Key Text, Value IntWriteable
   * expected output data types: Key Text, Value IntWriteable
   */
  public static class IMDbReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
    // Class variables
    private IntWritable result = new IntWritable();

    // reducer function
    public void reduce(Text key, Iterable<IntWritable> values, Context context)
        throws IOException, InterruptedException {
      int sum = 0;
      // iterrate and sum all counts
      for (IntWritable val : values) {
        sum += val.get();
      }
      // need to output IntWriteable so change the datatype
      result.set(sum);
      // write the output
      context.write(key, result);
    }
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration(); // create a configuration object
    conf.set("mapred.textoutputformat.separator", ","); // change default the key value seperator from " " to ","
    Job job = Job.getInstance(conf, "Movie count for specific genre and decade"); // create a MapReduce Job
    job.setJarByClass(IMDb.class); // declare main class
    job.setMapperClass(IMDbMapper.class); // declare mapper class
    job.setReducerClass(IMDbReducer.class); // declare reducer class
    job.setOutputKeyClass(Text.class); // declare the output key data type
    job.setOutputValueClass(IntWritable.class); // declare the output value data type
    FileInputFormat.addInputPath(job, new Path(args[0])); // declare the input path
    FileOutputFormat.setOutputPath(job, new Path(args[1])); // declare the output path
    System.exit(job.waitForCompletion(true) ? 0 : 1); // wait for the job to compelet and then exit
  }

}
