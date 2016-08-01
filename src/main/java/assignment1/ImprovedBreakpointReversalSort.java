package assignment1;

import utilities.filesystem.FileOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by jarndt on 7/5/16.
 */
public class ImprovedBreakpointReversalSort {
    public static void main(String[] args) throws IOException {
////        char[] v = new char['z'-'a'+1];
////        for (int i = 'a',j=0; i<='z'; i++,j++)
////            v[j] = (char)i;
//        int[] v = linspace(1,10);
////        int[] r = nRandomReversals(v,100);
//        int[] r = {1,6,7,3,8,2,4,5,9};
//
//        print(v);
//        print(r);
//
//        ImprovedBreakpointReversalSort bp = new ImprovedBreakpointReversalSort(r);
//        print(bp.sort().array);
////        for (int[] vv : bp.bestFlips)
////            print(vv);
//
//
////        print(getRandomSequentialNumbers(0,10,5));
////        print(flip(v,2,10));


        System.out.println(file);
        int[] v = linspace(1,size);
        for(int i = 0; i<100000; i++) {
            int r = new Random().nextInt(size);
            int[] rr = nRandomReversals(v, r);
            long start = System.currentTimeMillis();
            improvedBreakpointReversalSort(rr);
            long end = System.currentTimeMillis();
            int cr = count;
            count = 0;
            FileOptions.writeToFileAppend(file,r+","+cr+","+(end-start));
        }


//        int[] v = linspace(1,1000),r = nRandomReversals(v,720);
//        print(v);
//        print(r);
//        improvedBreakpointReversalSort(r);
//        print(r);
//        System.out.println(count);
    }
    public static int size = 1000;
    public static String dir = System.getProperty("user.dir")+"/src/main/resources/assignment1/", file = dir+"sort_real_vs_found_"+size+".txt";

    static int count = 0;
    public static void improvedBreakpointReversalSort(int[] v){
        while(hasBreakpoints(v)){
            ArrayList<List<int[]>> vv = getStrips(v);
            List<int[]> increasing = vv.get(0), decreasing = vv.get(1);
            int[] reversal;
            ArrayList<int[]> everything = new ArrayList<int[]>(increasing);
            everything.addAll(decreasing);
            if(decreasing.size()>0)
                reversal = pickReversal(v,everything);
            else
                reversal =increasing.get(0);
            v = doReversal(v,reversal);
            count++;
        }
    }

    private static int[] doReversal(int[] v, int[] reversal) {
        return flip(v,reversal[0],reversal[1]-1);
    }

    private static int[] pickReversal(int[] v, ArrayList<int[]> everything) {
        int[][] reversal = {{-1},null};
        int[] left = new int[everything.size()], right = new int[everything.size()];
        for(int i = 0; i<everything.size(); i++){
            int[] vv = everything.get(i);
            left[i] = vv[0];
            right[i] = vv[1];
        }
        for(int i : left)
            for(int j : right){
                if(i>=j-1) continue;
                int breakpointsRemoved = 0;
                if(Math.abs(v[j-1]-v[i-1])==1)
                    breakpointsRemoved+=1;
                if(Math.abs(v[j]-v[i])==1)
                    breakpointsRemoved+=1;
                if(breakpointsRemoved > reversal[0][0])
                    reversal = new int[][]{{breakpointsRemoved}, {i,j}};
            }
        return reversal[1];
    }

    private static ArrayList<List<int[]>> getStrips(int[] v) {
        List<int[]> increasing = new ArrayList<>(), decreasing = new ArrayList<>();
        int[] deltas = new int[v.length-1];
        for(int i = 0; i<v.length-1; i++)
            deltas[i] = v[i+1]-v[i];
        int start = 0;
        for(int i = 0; i<deltas.length; i++) {
            if (Math.abs(deltas[i]) == 1 && deltas[i] == deltas[start])
                continue;
            if(start > 0)
                if(deltas[start]==1)
                    increasing.add(new int[]{start,i+1});
                else
                    decreasing.add(new int[]{start,i+1});
            start = i+1;
        }
        ArrayList<List<int[]>> values = new ArrayList<>();
        values.add(increasing);
        values.add(decreasing);
        return values;
    }

    private static boolean hasBreakpoints(int[] v) {
        for(int i = 1; i<v.length; i++)
            if(v[i]!=v[i-1]+1)
                return true;
        return false;
    }




    private int[] array;
    public List<int[]> bestFlips = new ArrayList<int[]>();
    public ImprovedBreakpointReversalSort(int[] array){
        this.array = array;
    }
    public ImprovedBreakpointReversalSort sort(){
        int[] minMax = getMinMax(array);  //O(n)
        int[] nArray = new int[array.length+2];
        nArray[0] = minMax[0]-1; nArray[nArray.length-1] = minMax[1]+1;
        for (int i = 0; i<array.length; i++)
            nArray[i+1]=array[i];   //2 O(n) at this point
        //loop over each breakpoint and flip 0 to bp[0], bp[0] to bp[1] and so on
        // after each flip get breakpoint count
        // choose the minimum breakpoint count and perform that flip
        // repeat
        int[] breakPoints = getBreakPoints(nArray); //4 O(n)
        while(breakPoints.length > 2){
            int[] bestFlip = getBestFlip(nArray); //6 O(n)
            bestFlips.add(bestFlip);
            nArray = flip(nArray,bestFlip[0],bestFlip[1]); //6 O(n)
//            print(nArray);
            breakPoints = getBreakPoints(nArray); //7 O(n)
        }
        for(int i = 0; i<array.length; i++) //8 O(n)
            array[i] = nArray[i+1];

        return this;
    }

    public static int[] getBestFlip(int[] nArray){
        int[] breakPoints = getBreakPoints(nArray); //4 O(n)
        int[] flipHere = new int[]{0,breakPoints[0]};
        int min = breakPoints.length, maxGap = 0, pos1 = 0, pos2 = 0;
        boolean didFlip = false;
        for(int i = 2; i<breakPoints.length; i++){
            int[] temp = nArray.clone();
            int flipPointEnd = breakPoints[i]-1, flipPointStart = breakPoints[i-1];
            if(flipPointEnd-flipPointStart<=0) continue;
            temp = flip(temp, flipPointStart, flipPointEnd);
            int bpCount = getBreakPoints(temp).length;
            if(min > bpCount){
                min = bpCount;
                flipHere = new int[]{flipPointStart,flipPointEnd};
                didFlip = true;
            }
        }
        return didFlip?flipHere:getRandomSequentialNumbers(1,nArray.length-1,2);
    }

    public static int[] getBreakPoints(int[] v){
        List<Integer> bp = new ArrayList<Integer>();
        bp.add(v[0]);
        int start = v[0];
        boolean isIncreasing = true, isBetween = false;
        for(int i = 1; i<v.length; i++){
            if(!isBetween){
                if(v[i]-start==1){
                    isIncreasing = true;
                    isBetween = true;
                }else if(start - v[i]==1){
                    isBetween = true;
                    isIncreasing = false;
                }else
                    bp.add(i);
            }else if(isIncreasing && v[i]-start!=1){
                bp.add(i);
                isBetween = false;
            }else if(!isIncreasing && start-v[i]!=1){
                bp.add(i);
                isBetween = false;
            }
            start = v[i];
        }
        bp.add(v[v.length-1]);
        int[] values = new int[bp.size()];
        for(int i = 0; i<bp.size(); i++)
            values[i] = bp.get(i);
        return values;
    }

    public static int[] getMinMax(int[] v){
        int max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
        for(int i = 0; i<v.length; i++){
            if(v[i] > max) max = v[i];
            if(v[i] < min) min = v[i];
        }
        return new int[]{min,max};
    }

    public static int[] linspace(int min, int max){
        if(min >= max) throw new IllegalArgumentException("Min: "+min+" must be less than Max: "+max);
        int[] v = new int[max-min];
        for(int i = min,j=0; i<max; i++,j++)
            v[j]=i;
        return v;
    }

    public static <T> void print(T[] v){
        for (int i = 0; i<v.length; i++)
            System.out.print(v[i]+(i<v.length-1?",":""));
        System.out.println();
    }public static void print(char[] v){
        for (int i = 0; i<v.length; i++)
            System.out.print(v[i]+(i<v.length-1?",":""));
        System.out.println();
    }public static void print(int[] v){
        for (int i = 0; i<v.length; i++)
            System.out.print(v[i]+(i<v.length-1?",":""));
        System.out.println();
    }

    public static char[] nRandomReversals(char[] v, int nReversals){
        for(int i = 0; i<nReversals; i++) {
            int[] rand = getRandomSequentialNumbers(0,v.length-1,2);
            v = flip(v, rand[0], rand[1]);
        }
        return v;
    }public static int[] nRandomReversals(int[] v, int nReversals){
        int[] temp = v.clone();
        for(int i = 0; i<nReversals; i++) {
            int[] rand = getRandomSequentialNumbers(0,v.length-1,2);
            temp = flip(temp, rand[0], rand[1]);
        }
        return temp;
    }

    public static int[] getRandomSequentialNumbers(int min, int max, int count){
        if(count >= max-min) throw new IllegalArgumentException("Count: "+count+" can't be bigger or same size as max-min:"+(max-min));
        Random r = new Random();
        int[] ints = new int[count];
        for(int i = 0; i<count; i++) {
            int v = r.nextInt(max) + min;
            while(contains(ints,v))
                v = r.nextInt(max) + min;
            ints[i] = v;
        }
        Arrays.sort(ints);
        return ints;
    }

    public static <T> boolean contains(final T[] array, final T v) {
        if (v == null) {
            for (final T e : array)
                if (e == null)
                    return true;
        } else {
            for (final T e : array)
                if (e == v || v.equals(e))
                    return true;
        }

        return false;
    }  public static boolean contains(final int[] array, final int v) {
        for (final int e : array)
                if (e == v)
                    return true;

        return false;
    }

    public static char[] flip(char[] v, int start, int end){
        if(start<0 || end >= v.length) throw new IllegalArgumentException("Start and end must be between [0,"+(v.length-1)+")");
        for (int i = start; i<end/2; i++){
            char temp = v[i];
            v[i] = v[end-i];
            v[end-i] = temp;
        }
        return v;
    }public static int[] flip(int[] v, int start, int end){
        if(start<0 || end >= v.length) throw new IllegalArgumentException("Start and end must be between [0,"+(v.length-1)+")");
        int nEnd = end-start;
        for (int i = start; i<start+(nEnd%2==0?nEnd/2:nEnd/2+1); i++){
            int temp = v[i];
            v[i] = v[end];
            v[end--] = temp;
        }
        return v;
    }


}





































