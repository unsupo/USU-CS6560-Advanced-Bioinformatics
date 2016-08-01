package assignment2;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.max;


/**
 * Created by jarndt on 7/7/16.
 */


/*
PARTIALDIGEST(L)
    width ← Maximum element in L
    DELETE(width, L)
    X←{0,width}
    PLACE(L,X)

PLACE(L,X)
    if L isempty
        output X
        return
    y ← Maximum element in L
    if Δ(y,X)⊆ L
        Add y to X and remove lengths Δ(y, X) from L
        PLACE(L,X)
        Remove y from X and add lengths Δ(y, X) to L
    if Δ(width−y,X)⊆ L
        Add width − y to X and remove lengths Δ(width − y, X) from L
        PLACE(L,X)
        Remove width − y from X and add lengths Δ(width − y, X) to L
    return
 */

public class PartialDigestProblem {
    public static void main(String[] args) {
//        int[] originalCuts = {0,100,302,539,899,1211,1400};
        List<Integer> originalCuts = Arrays.asList(0, 1, 2, 3, 5, 6, 7, 8, 9, 11);
        List<Integer> fragments = getFragments(originalCuts);
        System.out.println("ORIGINAL CUTS: ");
        System.out.println(originalCuts);
        System.out.println();
        System.out.println("ALL POSSIBLE FRAGMENTS: ");
        System.out.println(fragments);
        System.out.println();
        Set<List<Integer>> last = partialDigest(fragments);
        System.out.println("RESULTING POSSIBLE ORIGINAL CUTS: ");
        last.forEach(System.out::println);
    }
    public static void print(int[] values){
        for(int i = 0; i<values.length; i++)
            System.out.print(values[i]+(i!=values.length-1?",":""));
        System.out.println();
    }
    public static List<Integer> asList(int[] values){
        List<Integer> ints = new ArrayList<>();
        for(Integer i : values)
            ints.add(i);
        return ints;
    }

    public static int[] getFragments(int[] originalCuts) {
        Arrays.sort(originalCuts);
        int n = originalCuts.length;
        int[] cuts = new int[n*(n-1)/2];
        int k = 0;
        for (int i = 0; i<originalCuts.length; i++)
            for(int j = i+1; j<originalCuts.length; j++)
//                if(originalCuts[j] != originalCuts[i])
                    cuts[k++] = originalCuts[j] - originalCuts[i];
        Arrays.sort(cuts);
        return cuts;
    }public static List<Integer> getFragments(List<Integer> originalCuts) {
        Collections.sort(originalCuts);
        List<Integer> cuts = new ArrayList<>();
        for (int i = 0; i<originalCuts.size(); i++)
            for(int j = i+1; j<originalCuts.size(); j++)
//                if(originalCuts.get(j) != originalCuts.get(i))
                    cuts.add(originalCuts.get(j) - originalCuts.get(i));
        Collections.sort(cuts);
        return cuts;
    }

    public static Set<List<Integer>> partialDigest(List<Integer> fragments){
        Set<List<Integer>> last = new HashSet<>();
        Integer width = max(fragments);
        fragments.remove(width);
        List<Integer> X = new ArrayList<>(Arrays.asList(0,width));
        place(fragments,X,width,last);
        return last;
    }

    private static void place(List<Integer> L, List<Integer> X, Integer width, Set<List<Integer>> last) {
        if(L.isEmpty()){
            Collections.sort(X);
            last.add((List<Integer>) ((ArrayList<Integer>)X).clone());
            return;
        }
        Integer y = max(L);
        List<Integer> delts = delt(y,X);
        if(deepContainsAll(delts,L))
            performOperation(L,X,y,delts,width,last);

        delts = delt(width - y, X);
        Integer diff = new Integer(width - y);
        if(deepContainsAll(delts,L))
            performOperation(L,X,diff,delts,width, last);
    }

    private static boolean deepContainsAll(List<Integer> a, List<Integer> inB) {
        List<Integer> c = (List<Integer>) ((ArrayList<Integer>)inB).clone();
        for(Integer i : a)
            if(c.contains(i))
                c.remove(i);
            else
                return false;
        return true;
    }

    private static void performOperation(List<Integer> L, List<Integer> X, Integer y, List<Integer> delts, Integer width, Set<List<Integer>> last) {
        X.add(y);
        delts.forEach(a->L.remove(new Integer(a)));
        place(L,X,width, last);
        X.remove(y);
        L.addAll(delts);
    }

    private static List<Integer> delt(int y, List<Integer> x) {
        return x.stream().parallel().mapToInt(a->Math.abs(y-a)).boxed().collect(Collectors.toList());
    }

    public static List<Integer> getRandomCuts(int max, int count){
        Random r = new Random();
        Set<Integer> cuts = new HashSet<>();
//        List<Integer> cuts = new ArrayList<>();
        while(cuts.size()<count)
            cuts.add(r.nextInt(max));
        if(!cuts.contains(new Integer(0)))
            cuts.add(0);
        return new ArrayList<>(cuts);

//        List<Integer> cuts = new ArrayList<>(Arrays.asList(0,max--));
//        Random r = new Random();
//        int v = max+1;
//        while(max>0){
//            Integer rand = r.nextInt(max==1?2:max);
//            while(rand == 0)
//                rand = r.nextInt(max==1?2:max);
//            max-=rand;
//            cuts.add(rand);
//        }
//        Collections.sort(cuts);
//        int vv = cuts.stream().mapToInt(Integer::intValue).sum();
//        if(2*v - vv > 0)
//            cuts.add(1);
//        return cuts;
    }

    public static boolean contains(int[] vs, int v){
        for(int i : vs)
            if(i!=v)
                return false;
        return true;
    }
}