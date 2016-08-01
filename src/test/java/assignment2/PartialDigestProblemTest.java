package assignment2;

import org.testng.annotations.Test;

import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Created by jarndt on 7/7/16.
 */
public class PartialDigestProblemTest {
    @Test
    public void atestPartialDigestSizeTen(){
        testCuts(10,10);
    }
    @Test
    public void btestPartialDigestSizeFifty(){
        testCuts(1000,50);
    }
    @Test
    public void ctestPartialDigestSizeOneHundred(){
        testCuts(1000,100);
    }
    @Test
    public void dtestOverThousandsOfRandomCuts(){
        int maxNum = 1000, maxSize = 10;
        Random r = new Random();
        for(int i = 0; i<100; i++) {
            int max = r.nextInt(maxNum)+10,
                    count = r.nextInt(maxSize)+10;
            testCuts(max, count);
        }
    }

    public void testCuts(int max, int count){
        List<Integer> cuts = PartialDigestProblem.getRandomCuts(max, count);
        List<Integer> allCuts = PartialDigestProblem.getFragments(cuts);
        Set<List<Integer>> foundCuts = PartialDigestProblem.partialDigest(allCuts);
        boolean res = foundCuts.contains(cuts);
        if(!res) {
            System.out.println("ACTUAL CUTS: "+cuts);
            System.out.println("\nPREDICTED CUTS: ");
            foundCuts.forEach(System.out::println);
            assertEquals(true, res);
        }
    }
}
