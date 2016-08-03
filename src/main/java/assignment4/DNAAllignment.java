package assignment4;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by jarndt on 8/3/16.
 */
public class DNAAllignment {
    public static void main(String[] args) {
        System.out.println((char)(new Random().nextInt('z'-'a')+'a'));

//        DNAAllignment align = new DNAAllignment();
//        System.out.println(align.getScoreDP("ba","bb"));
//        System.out.println(align.s1+"\t"+align.s2);
//        System.out.println(align.getTraceBack());
    }

    private int[][] cache;
    private String s1, s2;
    private List<Results> traceback;

    public void init(String s1, String s2, boolean...shouldNotAssign){
        if(!(shouldNotAssign != null && shouldNotAssign.length != 0 && shouldNotAssign[0]))
            this.s1 = s1; this.s2 = s2;
    }

    public int getScoreDP(String s1, String s2, boolean...shouldNotAssign){
        init(s1, s2, shouldNotAssign);
        char[] c1 = s1.toCharArray(), c2 = s2.toCharArray();
        int i = s1.length(), j = s2.length();
        cache = new int[i+1][j+1];
        cache[0][0] = 0;
        for(int m = 1; m<=i; m++)
            cache[m][0]+=cache[m-1][0]+m;
        for(int n = 1; n<=j; n++)
            cache[0][n]+=cache[0][n-1]+n;

        for(int m = 1; m<=i; m++){
            for(int n = 1; n<=j; n++){
                int     a = cache[m-1][n]	+ 1,
                        b = cache[m][n-1] 	+ 1,
                        c = cache[m-1][n-1] + (c1[m-1] != c2[n-1] ? 1 : 0);
                cache[m][n] = Math.min(Math.min(a, b),c);
            }
        }
        return cache[i][j];
    }
    public List<Results> getTraceBack(){
        this.traceback = new ArrayList<Results>();
        traceBackRecurse(s1.length(), s2.length());
        Collections.reverse(traceback);
        return traceback;
    }

    private void traceBackRecurse(int i, int j) {
        if(i==0 && j == 0) return;
        int a = (i-1>=0?(cache[i-1][j] + 1):Integer.MIN_VALUE);
        int b = (j-1>=0?(cache[i][j-1] + 1):Integer.MIN_VALUE);
        int c = (i-1>=0 && j-1>=0?(cache[i-1][j-1] + (s1.charAt(i-1) != s2.charAt(j-1) ? 1 : 0)):Integer.MIN_VALUE);

        int check = cache[i][j];
        if(check == a){
            traceback.add(new Results("-", s1.charAt(i-1)+"", Results.INSERT));
            traceBackRecurse(i-1, j);
        }else if(check == b){
            traceback.add(new Results("-", s2.charAt(j-1)+"", Results.DELETE));
            traceBackRecurse(i, j-1);
        }else if(check == c){
            traceback.add(new Results(s1.charAt(i-1)+"", s2.charAt(j-1)+"", Results.UPDATE));
            traceBackRecurse(i-1, j-1);
        }

    }

    public class Results{
        final static String DELETE = "Delete";
        final static String UPDATE = "Update";
        final static String INSERT = "Insert";
        final static String MATCH  = "Match";

        String s1, s2, action;
        int cost;
        public Results(String s1, String s2, String action){
            this.s1 = s1; this.s2 = s2; this.action = action;
            cost = s1.equals(s2)?0:1;
            if(cost == 0)
                action = MATCH;
            else
                action = UPDATE;
        }

        @Override public String toString(){
            return "("+(s1.equals(s2)?MATCH:action)+" "+s1+"->"+s2+" COST:"+cost+")";
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Results results = (Results) o;

            if (cost != results.cost) return false;
            if (s1 != null ? !s1.equals(results.s1) : results.s1 != null) return false;
            if (s2 != null ? !s2.equals(results.s2) : results.s2 != null) return false;
            return action != null ? action.equals(results.action) : results.action == null;

        }

        @Override
        public int hashCode() {
            int result = s1 != null ? s1.hashCode() : 0;
            result = 31 * result + (s2 != null ? s2.hashCode() : 0);
            result = 31 * result + (action != null ? action.hashCode() : 0);
            result = 31 * result + cost;
            return result;
        }
    }
}

/*

DP
S = new int[n+1,m+1] //array
for i in range(0,n+1):
	S[i,0]=i
for j in range(o,m+1):
	S[0,j]=j
for i in range(1,n+1):
	for j in range(1,m+1):
		s[i,j]=min(S[i-1,j]+1,s[i,j-1]+1,s[i-1,j-1]+A[i]!=B[j])
return S[n,m]


Trace Back
def TB(i,j):
	if i==0 && j==0:
		return
	if i==0:
		print(“_”+B[j])
		TB(i,j-1)
		return;
	if j==0:
		print(A[i]+“_”)
		TB(i-1,j)
		return;
	if s[i,j]==s[i-1,j]+1:
		print(A[i]+“_”)
		TB(i-1,j)
		return;
	if s[i,j]==s[i,j-1]+i:
		print(“_”+B[j])
		TB(i,j-1)
		return;

 */
