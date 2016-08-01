package assignment2;

import utilities.filesystem.FileOptions;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Set;


/**
 * Created by jarndt on 7/7/16.
 */
public class PartialDigestAnalysis {
    public static void main(String[] args) throws IOException {
        String file = System.getProperty("user.dir")+"/src/main/resources/assignment2/timings.txt";
        Random r = new Random();
        for(int i = 0; i<1000; i++){
//            int max = r.nextInt(1000)+10;
            int max = 1000;
            int count = r.nextInt(150)+2;
            if(max<count) {
                int temp = count;
                count = max;
                max = count+10;
            }
            long time = timeIt(max, count);
            String content = max+","+count+","+time;
            FileOptions.writeToFileAppend(file,content);
        }
    }

    public static long timeIt(int max, int count){
        List<Integer> cuts = PartialDigestProblem.getRandomCuts(max, count);
        List<Integer> allCuts = PartialDigestProblem.getFragments(cuts);
        long start = System.nanoTime();
        Set<List<Integer>> foundCuts = PartialDigestProblem.partialDigest(allCuts);
        long end = System.nanoTime();
        return end-start;
    }
}
