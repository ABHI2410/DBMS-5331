import java.io.IOException;
import java.util.Arrays;
import javax.naming.Context;

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

//                0           1       2      3   4    5     
//Sample input: tt0108778;tvSeries;Friends;1994;8.9;Comedy,Romance
public class IMDb {
  public static class IMDbMapper extends Mapper<Object, Text, Text, IntWritable> {
    // private final static IntWritable one = new IntWritable(1);
    private Text genreAndPeriod = new Text();

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

    public void map(Object key, Text value, Context context)
        throws IOException, InterruptedException {
      String[] genresList;
      String period;
      String genre;
      String[] strToArr = value.toString().split(";");

      if (strToArr[1].equals("movie") && Double.parseDouble(strToArr[4]) >= 7.5) {
        genresList = strToArr[5].split(",");
        if (strToArr[3].equals("\\N")) {
          return;
        } else {
          period = CheckPeriod(Integer.parseInt(strToArr[3]));
        }
        genre = CheckGenre(genresList);
        if (period != null && genre != null) {
          genreAndPeriod.set(period + "," + genre);
          context.write(genreAndPeriod, new IntWritable(1));
        }
      }
    }
  }

  public static class IMDbReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
    private IntWritable result = new IntWritable();

    public void reduce(Text key, Iterable<IntWritable> values, Context context)
        throws IOException, InterruptedException {
      int sum = 0;
      for (IntWritable val : values) {
        sum += val.get();
      }
      result.set(sum);
      context.write(key, result);
    }
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "word count");
    job.setJarByClass(IMDb.class);
    job.setMapperClass(IMDbMapper.class);
    job.setCombinerClass(IMDbReducer.class);
    job.setReducerClass(IMDbReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }

}
