package assignment3;

import utilities.filesystem.FileOptions;
import utilities.plotting.Plot;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static utilities.Utility.RESOURCE_DIRECTORY;

/**
 * Created by jarndt on 8/1/16.
 */
public class MedianStringAnalysis {
    /*
    How does the system perform w.r.t run time? Start with small examples and build up to larger examples. We expect the system to be linear in n, but near exponential in L.
    The effectiveness of the bound will dictate how the runtime grows as a function of L.
     */
    public static void main(String[] args) throws IOException {
//        createPlots(true);
        runTimingStudy();
//        analyseRealData(RESOURCE_DIRECTORY+"assignment3/promotorRegionsSample.txt");
    }

    public final static String fileName = RESOURCE_DIRECTORY+"assignment3/timing_study.txt";

    /**
     * promotorRegionsSample.txt
     * In the file lines that look like: >hg38_knownGene_uc057aup.1 give the name of the gene.
     * The following lines contain 1000 DNA bases (a g c t) that make up the sequence immediately before the start of the gene.
     * It is in this region that biologists are interested in transcription factor binding sites (which computer scientists find as median strings).
     * Run your code on this real data to see what it finds. set L to around 12.
     *
     * This will write it's results to a file located at RESOURCE_DIRECTORY/promotorRegionsAnalysis.txt
     *
     * @param filePath the path and filename to the real data (promotorRegionsSample.txt)
     * @throws IOException
     */
    private static void analyseRealData(String filePath) throws IOException {
        String[] promotorRegions = FileOptions.readFileIntoString(filePath).split(">\\w+\\.\\w+[\\s|\\t|\\n]+");

        int L = 12;
        for(String regions : promotorRegions){
            String foundMotif = MedianString.MedianString(regions.trim().toUpperCase().split("\n"),L);
            FileOptions.writeToFileAppend(RESOURCE_DIRECTORY+"assignment3/promotorRegionsAnalysis.txt",foundMotif);
        }
    }

    private static class Timings{
        int count, timeMillis;
        public Timings(String line){
            String[] s = line.split(",");
            count = Integer.parseInt(s[0]);
            timeMillis = Integer.parseInt(s[1]);
        }
    }

    public static void createPlots(boolean display) throws IOException {
        List<String> f = FileOptions.readFileIntoListString(fileName);
        List<Timings> timings = f.stream().map(a->new Timings(a)).collect(Collectors.toList());
        Collections.sort(timings,(a,b)->a.count-b.count);

        double[] x = new double[timings.size()], y = new double[timings.size()];
        for (int i = 0; i<x.length; i++){
            Timings t = timings.get(i);
            x[i] = t.count;
            y[i] = t.timeMillis;
        }

        Plot p = new Plot(x,y)
                .setPlotTitle("Runtime of Median String Problem")
                .setXLabel("L or length of median string")
                .setYLabel("Time in milliseconds");
//                .setType(Plot.JUST_POINTS);
        if(display)
            p.showPlot();
        else
            p.savePlot(RESOURCE_DIRECTORY+"assignment3/timings.png");
    }


    public static void runTimingStudy() throws IOException {
        int mutations = 0, dnaLength = 500, numMotifInserts = 5, numSequences = dnaLength;
        for(int i = 4; i<100; i++) {
            long start = System.currentTimeMillis();
            boolean b = testVariousParameters(mutations,dnaLength,i,numMotifInserts,numSequences);
            long end = System.currentTimeMillis();
            if(b)
                FileOptions.writeToFileAppend(fileName,i+","+(end-start));
            else i--;
        }
    }

    synchronized public static boolean testVariousParameters(int mutations, int dnaLength, int motifLength, int numMotifInserts, int numSequences){
        DNASequence dna = new DNASequence(mutations);
        String[] dnaSequences = dna.getMultipleDNASequences(dnaLength,motifLength,numMotifInserts,numSequences);

        String motif = dna.getMotifSequence();
        String gatheredMotif = MedianString.MedianString(dnaSequences,motifLength);
        if(!gatheredMotif.equals(motif)) {
            System.err.println(gatheredMotif + " != " + motif);
            return false;
        }
        return true;
    }
}
