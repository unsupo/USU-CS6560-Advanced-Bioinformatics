package assignment3;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Created by jarndt on 7/21/16.
 */
public class MedianStringTest {
    /*
    Test the code on some synthetic data that is created to include known median strings.

    A simple test would be to generate random sequences of AGCT's of length say 200 characters,
    then create a median string of length L (say 12) and insert it into a random position in each sequence.

    Next, try the algorithm when the median string is mutated by one character when inserted into each DNA sequence.

    How does the method perform?
    What about two mutations?
    Try more until the system fails to find the median string you inserted.
     */
    @Test
    public static void test1ZeroMutationsSmallSample() {
        testVariousParameters(0,30,5,3,30);
    }
    @Test
    public static void test2OneMutationsSmallSample() {
        testVariousParameters(1,30,5,3,30);
    }
    @Test
    public static void test3TwoMutationsSmallSample() {
        testVariousParameters(2,30,5,3,30);
    }
    @Test
    public static void test4ZeroMutationsLargeSample() {
        testVariousParameters(0,200,12,5,30);
    }
    @Test
    public static void test5OneMutationsLargeSample() {
        testVariousParameters(1,200,12,5,30);
    }
    @Test
    public static void test6TwoMutationsLargeSample() {
        testVariousParameters(2,200,12,5,30);
    }

    @Test(enabled = false)
    public static void test7MutateUntilFailure(){
        int mutations = 3;
        while(true) {
            try {
                testVariousParameters(mutations++,300,5,1,30);
            } catch (AssertionError e) {
                break;
            }
        }
        System.out.println("Took "+mutations+" to fail the system");
    }


    synchronized public static void testVariousParameters(int mutations, int dnaLength, int motifLength, int numMotifInserts, int numSequences){
        DNASequence dna = new DNASequence(mutations);
        String[] dnaSequences = dna.getMultipleDNASequences(dnaLength,motifLength,numMotifInserts,numSequences);

        String motif = dna.getMotifSequence();
        String gatheredMotif = MedianString.MedianString(dnaSequences,motifLength);
        assertEquals(motif,gatheredMotif);
    }
}
