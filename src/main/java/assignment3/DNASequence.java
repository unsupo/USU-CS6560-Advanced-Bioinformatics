package assignment3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by jarndt on 7/29/16.
 */
public class DNASequence {
    public static String[] OPTIONS = {"A","G","C","T"};
    public static int MUTATION_CHANCE = 100;
    private static Random ran = new Random();

    /**
     * Designed to create DNASequences
     * the method getDNASequence should be called next
     * that method will store it's results in<br/>
     * <b>sequence</b> - using the getDNASequence method<br/>
     * <b>motif</b> - using the getMotifSequence method<br/>
     * <b>motif insert locations</b> - using the getMotifInsertLocations method
     *
     * @param options default parameter.  if no paramater is pass or null or empty String array is passed <br/>
     *                then default <b>OPTIONS = {"A","G","C","T"}</b> will be applied
     */
    public DNASequence(String...options) {
        init(options);
        dnaSequenceList = new ArrayList<>();
        motifInsertLocations = new ArrayList<>();
    }

    /**
     * Designed to create DNASequences with mutations
     * the method getDNASequence should be called next
     * that method will store it's results in<br/>
     * <b>sequence</b> - using the getDNASequence method<br/>
     * <b>motif</b> - using the getMotifSequence method<br/>
     * <b>motif insert locations</b> - using the getMotifInsertLocations method
     *
     * @param options default parameter.  if no paramater is pass or null or empty String array is passed <br/>
     *                then default <b>OPTIONS = {"A","G","C","T"}</b> will be applied
     */
    public DNASequence(int numMutations, String...options) {
        init(options);
        dnaSequenceList = new ArrayList<>();
        motifInsertLocations = new ArrayList<>();
        this.numMutations = numMutations;
    }

    private static void init(String...options){
        if(options != null && options.length != 0)
            OPTIONS = options;
    }

    private int numMutations = 0;
    private String motifSequence;
    private List<List<Integer>> motifInsertLocations;
    private String[] DNASequences;
    private List<String> dnaSequenceList;


    public int getNumMutations() {
        return numMutations;
    }

    public void setNumMutations(int numMutations) {
        this.numMutations = numMutations;
    }

    public List<String> getDnaSequenceList() {
        return dnaSequenceList;
    }

    public String getMotifSequence() {
        return motifSequence;
    }

    public List<List<Integer>> getMotifInsertLocations() {
        return motifInsertLocations;
    }

    public String[] getDNASequences() {
        this.DNASequences = dnaSequenceList.toArray(new String[dnaSequenceList.size()]);
        return DNASequences;
    }

    /**
     *     generate random sequences of AGCT's of length
     *     @param length is the length of the random sequence
     *     @param options the String characters to use in making the random sequence<br/>
     *                     default is <b>OPTIONS = {"A","G","C","T"}</b>
     */
    public static String generateRandomSequence(int length, String...options){
        init(options);
        String results = "";
        while(results.length() < length)
            results+=OPTIONS[ran.nextInt(OPTIONS.length)];
        return results;
    }

    /**
     * This method creates multiple random dna sequence and then creates a random motif and inserts that motif into random
     * locations in each dna sequence<br/>
     * <b>sequence</b> - using the getSequence method<br/>
     * <b>motif</b> - using the getMotifSequence method<br/>
     * <b>motif insert locations</b> - using the getMotifInsertLocations method
     *
     * @param   dnaLength           the final length of the resulting dna string after the inserted sequence
     * @param   motifLength         length of the inserted sequence
     * @param   numRandMotifInserts number of inserted sequences
     * @param   numDNASequences     number of DNA Sequences
     * @return  final dna sequence
     */
    public String[] getMultipleDNASequences(int dnaLength, int motifLength, int numRandMotifInserts, int numDNASequences){
        String motif = generateRandomSequence(motifLength);
        for(int i = 0; i< numDNASequences; i++)
            getDNASequence(dnaLength, motif,numRandMotifInserts);
        return getDNASequences();
    }


    /**
     * create a median string of length L (say 12) and insert it into a random position in each sequence
     * This method creates a random dna sequence and then creates a random motif and inserts that motif into random
     * locations in the dna sequence<br/>
     * <b>sequence</b> - using the getSequence method<br/>
     * <b>motif</b> - using the getMotifSequence method<br/>
     * <b>motif insert locations</b> - using the getMotifInsertLocations method
     *
     * @param   dnaLength           the final length of the resulting dna string after the inserted sequence
     * @param   motifLength         length of the inserted sequence
     * @param   numRandMotifInserts number of inserted sequences
     * @return  final dna sequence
     */
    public String getDNASequence(int dnaLength, int motifLength, int numRandMotifInserts){
        return getDNASequence(dnaLength, generateRandomSequence(motifLength),numRandMotifInserts);
    }

    /**
     * This method creates multiple random dna sequence and then creates a random motif and inserts that motif into random
     * locations in each dna sequence<br/>
     * <b>sequence</b> - using the getSequence method<br/>
     * <b>motif</b> - using the getMotifSequence method<br/>
     * <b>motif insert locations</b> - using the getMotifInsertLocations method
     *
     * @param   dnaLength           the final length of the resulting dna string after the inserted sequence
     * @param   motifString         length of the inserted sequence
     * @param   numRandMotifInserts number of inserted sequences
     * @param   numDNASequences     number of DNA Sequences
     * @return  final dna sequence string array
     */
    public String[] getMultipleDNASequences(int dnaLength, String motifString, int numRandMotifInserts, int numDNASequences){
        for(int i = 0; i< numDNASequences; i++)
            getDNASequence(dnaLength, motifString,numRandMotifInserts);
        return getDNASequences();
    }
    /**
     * create a median string of length L (say 12) and insert it into a random position in each sequence
     * This method creates a random dna sequence and inserts a given motif sequence into random locations
     * in the dna sequence<br/>
     * <b>sequence</b> - using the getSequence method<br/>
     * <b>motif</b> - using the getMotifSequence method<br/>
     * <b>motif insert locations</b> - using the getMotifInsertLocations method
     *
     * @param dnaLength             the final length of the resulting dna string after the inserted sequence
     * @param motifString           the motif sequence to be inserted into the dna sequence
     * @param numRandMotifInserts    number of inserted sequences
     * @return  final dna sequence
     */
    public String getDNASequence(int dnaLength, String motifString, int numRandMotifInserts){
        List<String> results = new ArrayList<>();
        final int length = dnaLength-motifString.length()*numRandMotifInserts;
        if(length<1)
            throw new IllegalArgumentException("DNA length given: "+dnaLength+" can't be less than or equal\n" +
                    "motif length: "+motifString.length()+" times by number of motif inserts: "+numRandMotifInserts+" m*n = "+motifString.length()*numRandMotifInserts);
        String medianString = generateRandomSequence(length);

        List<Integer> randNums = IntStream.range(0,numRandMotifInserts).map(a->ran.nextInt(length)).boxed().collect(Collectors.toList());
        Collections.sort(randNums);
        for(int i = 0; i<randNums.size(); i++){
            int m = randNums.get(i);
            String start = medianString.substring(0,m), end = medianString.substring(m);
            medianString = start+"0"+end;
            randNums.set(i,motifString.length()*i+m - i);
        }
        medianString = medianString.replaceAll("0",motifString);

        medianString = mutate(medianString, motifString.length(), randNums);

        this.motifSequence = motifString;
        this.motifInsertLocations.add(randNums);
        this.dnaSequenceList.add(medianString);

        return medianString;
    }

    private String mutate(String medianString, int motifLength, List<Integer> motifLocations) {
        while(numMutations-- > 0){
            for (Integer i : motifLocations){
                boolean shouldMutateHere = ran.nextInt(100) < MUTATION_CHANCE;
                if(shouldMutateHere){
                    int mutationLocation = ran.nextInt(motifLength);
                    medianString = medianString.substring(0,i+mutationLocation)+
                                        getNextOption(medianString.charAt(i+mutationLocation)+"") +
                                    medianString.substring(i+mutationLocation+1);
                }
            }
        }
        return medianString;
    }

    private String getNextOption(String...s) {
        String exclude = "";
        if(s!=null && s.length ==1)
            exclude = s[0];
        String v = OPTIONS[ran.nextInt(OPTIONS.length)];
        while(OPTIONS.length > 1 && v.equals(exclude))
            v = OPTIONS[ran.nextInt(OPTIONS.length)];
        return v;
    }
}
