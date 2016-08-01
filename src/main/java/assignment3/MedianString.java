package assignment3;

import java.util.HashMap;

/**
 * Created by jarndt on 7/18/16.
 */
public class MedianString {
    public static void main(String[] args) {
        int motifLength = 5;
        DNASequence dna = new DNASequence(4);
        String[] dnaSequences = dna.getMultipleDNASequences(30,motifLength,3,20);

        System.out.println(dna.getDnaSequenceList());
        System.out.println(dna.getMotifSequence());
        System.out.println(dna.getMotifInsertLocations());
        System.out.println(MedianString(dnaSequences,motifLength));
    }

    static String[] DNA = new String[]{};
    static int globalBestScore = Integer.MAX_VALUE;
    static String bestMedianString = "";

    /**
     *
     *
     * @param dna is a string array of dna sequences
     * @return string of best median string found.
     */
    public static String MedianString(String[] dna, int...motifLength){
        init();
        if(DNA == null)
            throw new IllegalArgumentException("DNA can't be null");
        DNA = dna;
        int L = dna[0].length();
        if(motifLength != null && motifLength.length == 1)
            L = motifLength[0];
        _MedianString(L,"");
        return bestMedianString;
    }

    private static void init(){
            DNA = new String[]{};
            globalBestScore = Integer.MAX_VALUE;
        bestMedianString = "";
    }

    /**
     *
     * @param length length is the number of characters in the remaining median string initialized to L
     * @param ms ms is the median string, initially ""
     */
    private static void _MedianString(int length, String ms){// length is the number of characters in the remaining
        // median string initialized to L, ms is the median string, initially ""
        if(length<0) return;
        if(length==0){// Have the median string of the correct length
            int score = totalDistance(ms, DNA); //DNA is a global tXn array of DNA sequences
            if(score<globalBestScore){  // update the best global solution and score
                globalBestScore = score;
                bestMedianString = ms;
                return;
            }
        }
        // apply the bound once the ms string is at least 4 characters long (you can experiment with this)
        if(ms.length() >= 4 && totalDistance(ms,DNA) > globalBestScore)
            return;
        // otherwise, keep trying new combinations
        for(String base : DNASequence.OPTIONS)
            _MedianString(length-1,ms+base); //note the plus adds a character to ms and returns a new string 

    }

    private static int totalDistance(String ms, String[] dnaSequences) {
        //minimum possible total hamming distance
        int totalHammDistance = 0;
        //loop over all dna strings in the dnaSequences array
        for(String dna : dnaSequences){
            int minHammDistance = Integer.MAX_VALUE;
            //doing a scrolling window loop to get the smallest possible hamming distance of this sequence
            for(int i = 0; i<dna.length()-ms.length()+1; i++){
                int distance = hamDistance(ms,dna.substring(i,ms.length()+i));
                if(minHammDistance > distance)
                    minHammDistance = distance;
            }
            //add all sequences' minimum hamming distances together for the final score.
            totalHammDistance+=minHammDistance;
        }

        return totalHammDistance;
    }

    private static int hamDistance(String ms, String dna) {
        //determine which string is longer and which is shorter
        int d = ms.length(), l = dna.length(), distance;
        if(ms.length() > dna.length()){
            d = dna.length();
            l = ms.length();
        }
        //the starting distance is the difference in length
        distance = l - d;
        //then add to the distance, one for each difference
        for(int i = 0; i<d; i++)
            if(ms.charAt(i) != dna.charAt(i))
                distance++;
        return distance;
    }


    public static String medianString(int motifLength, String DNA){
        HashMap<String,Integer> motifs = new HashMap<>();
        for(int i = 0; i<DNA.length()-motifLength; i++){
            String key = DNA.substring(i,i+motifLength);
            if(motifs.containsKey(key))
                motifs.replace(key,motifs.get(key)+1);
            else
                motifs.put(key,1);
        }

        String maxKey = "";
        Integer max = Integer.MIN_VALUE;
        for(String s : motifs.keySet()) {
            Integer m = motifs.get(s);
            if(max < m){
                max = m;
                maxKey = s;
            }
        }
        return maxKey+","+max;
    }

}


/*
globalBestScore = +infinity
Def MedianString(length, ms): // length is the number of characters in the remaining median string initialized to L, ms is the median string, initially ""
    if length==0:   // Have the median string of the correct length
       score = totalDistance(ms, DNA) //DNA is a global tXn array of DNA sequences
       if score < globalBestScore :  // update the best global solution and score
            globalBestScore = score 
            bestMedianString = ms
        return
    // apply the bound once the ms string is at least 4 characters long (you can experiment with this)
    if   len(ms) >= 4 and (totalDistance(ms,DNA) > globalBestScore ): 
        return
    // otherwise, keep trying new combinations
    for base in {A,G,C,T}
        MedianString(length-1,ms+base)  //note the plus adds a character to ms and returns a new string 
 */
