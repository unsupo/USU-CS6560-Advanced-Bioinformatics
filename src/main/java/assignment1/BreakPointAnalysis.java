package assignment1;

import utilities.filesystem.FileOptions;
import utilities.plotting.Plot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static assignment1.ImprovedBreakpointReversalSort.dir;
import static assignment1.ImprovedBreakpointReversalSort.file;

/**
 * Created by jarndt on 7/5/16.
 */
public class BreakPointAnalysis {
    public static void main(String[] args) throws IOException {
        List<String> results = FileOptions.readFileIntoListString(file);
        List<Results> values = Results.convertList(results);
        Collections.sort(values,(a,b)->a.actual-b.actual);
        double[] x = new double[values.size()], y = new double[values.size()], t = new double[values.size()];;
        for(int i = 0; i<values.size(); i++){
            x[i] = values.get(i).actual;
            y[i] = values.get(i).result;
            t[i] = values.get(i).milliseconds;
        }

        new Plot(x,y)
                .setXLabel("Number of Random Flips")
                .setYLabel("Number of Flips by the Algorithm")
                .setPlotTitle("Relation Between Flips Done and flips Required")//.showPlot();
                .savePlot(dir+"flips_done_vs_flips_required.png");


        new Plot(x,t)
                .setXLabel("Number of Random Flips")
                .setYLabel("Time in milliseconds to find flips")
                .setPlotTitle("Relation Between Flips Done and Time to Solve")//.showPlot();
                .savePlot(dir+"flips_done_vs_time_to_solve.png");
    }
    private static class Results{
        String line;
        int result, actual, milliseconds;
        public Results(String v) {
            line = v;
            String[] s = v.split(",");
            result = Integer.parseInt(s[0]);
            actual = Integer.parseInt(s[1]);
            milliseconds = Integer.parseInt(s[2]);
        }
        public static List<Results> convertList(List<String> results){
            List<Results> r = new ArrayList<>();
            for(String s : results)
                r.add(new Results(s));
            return r;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Results results = (Results) o;

            if (result != results.result) return false;
            if (actual != results.actual) return false;
            if (milliseconds != results.milliseconds) return false;
            return line != null ? line.equals(results.line) : results.line == null;

        }

        @Override
        public int hashCode() {
            int result1 = line != null ? line.hashCode() : 0;
            result1 = 31 * result1 + result;
            result1 = 31 * result1 + actual;
            result1 = 31 * result1 + milliseconds;
            return result1;
        }
    }
}
