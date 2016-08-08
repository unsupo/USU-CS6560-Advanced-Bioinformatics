package utilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Optional;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Table;

import utilities.data.ReadCVSFile;
import utilities.data.gatherers.NCBIGatherer;

public class MinimumEditAlgorithm {
	public static void main(String[] args) throws IOException{
//		String s1 = "atccga", s2 = "ttagca";
//		MinimumEditAlgorithm me = new MinimumEditAlgorithm();
//		for(String s : me.getScoreDevideAndConquer(s1, s2))
//			System.out.println(s);
//		System.out.println(MinimumEditAlgorithm.score);
		
//		System.out.println(me.getScoreDP(s1, s2));
//		System.out.println(me.getTraceBack());
//		System.out.println(me.getModfiedString());
//		
//		System.out.println(me.getScoreDP(s1, s2));
//		System.out.println(me.getTraceBack());
//		System.out.println(me.getModfiedString());
		
//		new GatherData(GatherData.DATA_SOURCE);

//		MinimumEditAlgorithm me = new MinimumEditAlgorithm();
//		List<String> l = GatherData.getAllFilesInFolder(GatherData.DATA_DIR, new String[]{".zip",".txt"});
//		
//		for(int i = 0; i<l.size(); i++)
//			for(int j = i+1; j<l.size(); j++)
//				compare(me,l.get(i),l.get(j));
	
		MinimumEditAlgorithm me = new MinimumEditAlgorithm();
//		compare(me,GatherData.DATA_DIR+"/Prototypical_human.txt",GatherData.DATA_DIR+"/Prototypical_human.txt");
		
//		List<String> l = GatherData.getAllFilesInFolder(GatherData.DATA_DIR+"/Great Apes", new String[]{".txt"});
//		l.stream().parallel().forEach(a->compare(me,a,GatherData.DATA_DIR+"/Prototypical_human.txt"));
		compare(me,GatherData.DATA_DIR+"/Neandertal.txt",GatherData.DATA_DIR+"/Prototypical_human.txt");
//		l.forEach(a->compare(me,GatherData.DATA_DIR+"/Prototypical_human.txt",a));
//		
//		System.out.println(new MinimumEditAlgorithm().getScoreDC("", ""));
	
//		String s1 = "TAATC", s2 = "GTGGA";
//		MinimumEditAlgorithm meda = new MinimumEditAlgorithm();
//		System.out.println(meda.getScoreDC(s1, s2)+"\n\t"+meda.fs1+"\n\t"+meda.fs2);
//		System.out.println(meda.getScoreDP(s1, s2)+"\n\t"+s1+"\n\t"+meda.getModfiedString());
//		System.out.println(meda.getScoreRecurse(s1, s2)+"\n\t"+s1+"\n\t"+meda.getModfiedString());
	}
	

	public static void compare(MinimumEditAlgorithm me, String p1, String p2) {
		String s1 = null, s2 = null;
		me.score = 0;
		try {
			if(p1.endsWith(".txt") || p1.endsWith(NCBIGatherer.EXTENSION))
				s1 = GatherData.readFileIntoString(p1);
			else if(p1.endsWith(".zip"))
				s1 = GatherData.readZipFileIntoString(p1, ".txt", true);
			if(p2.endsWith(".txt") || p2.endsWith(NCBIGatherer.EXTENSION))
				s2 = GatherData.readFileIntoString(p2);
			else if(p2.endsWith(".zip"))
				s2 = GatherData.readZipFileIntoString(p2, ".txt", true);
			if(GatherData.exists(p1,p2))
				return;
			if(s1 == null || s2 == null)
				return;
			System.out.println("Working on:\n\t"+p1+"\n\t"+p2);
			String[] scores = me.getScoreDevideAndConquer(s1.trim().replaceAll("[^actgACTG]+", ""), s2.trim().replaceAll("[^actgACTG]+", ""));
			GatherData.writeResults(p1,p2,scores,me.score);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
	}

	private static Table<String,String,Integer> scoringSchmea;
	private int[][] cache;
	private Table<Integer,Integer,Integer> tableCache;
	private List<Results> traceback;
	private String s1, s2;
	
	public MinimumEditAlgorithm(){
		initDefaultTable();
		this.traceback = new ArrayList<MinimumEditAlgorithm.Results>();
	}

	
	public int getScore(String s1, String s2){
		this.s1 = s1; this.s2 = s2;
		return getScoreRecurse(s1, s2);
	}public List<Results> getTraceBack(){
		this.traceback = new ArrayList<MinimumEditAlgorithm.Results>();
		traceBackRecurse(s1.length(), s2.length());
		Collections.reverse(traceback);
		return traceback;
	}

	public int getScoreDP(String s1, String s2, boolean...shouldNotAssign){
		if(shouldNotAssign == null || shouldNotAssign.length == 0){
			this.s1 = s1; this.s2 = s2;
		}
		char[] c1 = s1.toCharArray(), c2 = s2.toCharArray();
		int i = s1.length(), j = s2.length();
		cache = new int[i+1][j+1];
		cache[0][0] = scoringSchmea.get("-", "-");
		for(int m = 1; m<=i; m++)
			cache[m][0]+=cache[m-1][0]+scoringSchmea.get(c1[m-1]+"", "-");
		for(int n = 1; n<=j; n++)
			cache[0][n]+=cache[0][n-1]+scoringSchmea.get(c2[n-1]+"", "-");
		
		for(int m = 1; m<=i; m++){
			for(int n = 1; n<=j; n++){				
				int a = cache[m-1][n]	+ scoringSchmea.get("-", c1[m-1]+""),
					b = cache[m][n-1] 	+ scoringSchmea.get("-", c2[n-1]+""),
					c = cache[m-1][n-1] + scoringSchmea.get(c1[m-1]+"", c2[n-1]+"");
				cache[m][n] = Math.max(Math.max(a, b),c);//max(new int[]{a,b,c});
			}	
		}
		return cache[i][j];
	}public int getScoreDPHashTable(String s1, String s2, boolean...shouldNotAssign){
		if(shouldNotAssign == null || shouldNotAssign.length == 0){
			this.s1 = s1; this.s2 = s2;
		}
		int i = s1.length(), j = s2.length();
		tableCache = HashBasedTable.create();
		tableCache.put(0, 0, scoringSchmea.get("-", "-"));
		for(int m = 1; m<=i; m++)
			tableCache.put(m,0,tableCache.get(m-1,0)+scoringSchmea.get(s1.charAt(m-1)+"", "-"));
		for(int n = 1; n<=j; n++)
			tableCache.put(0,n,tableCache.get(n-1,0)+scoringSchmea.get(s2.charAt(n-1)+"", "-"));
		
		for(int m = 1; m<=i; m++){
			for(int n = 1; n<=j; n++){				
				int a = tableCache.get(m-1,n)	+ scoringSchmea.get("-", s1.charAt(m-1)+""),
					b = tableCache.get(m,n-1) 	+ scoringSchmea.get("-", s2.charAt(n-1)+""),
					c = tableCache.get(m-1,n-1) + scoringSchmea.get(s1.charAt(m-1)+"", s2.charAt(n-1)+"");
				tableCache.put(m,n,Math.max(Math.max(a, b),c)/*max(new int[]{a,b,c})*/);
			}	
		}
		return tableCache.get(i,j);
	}
	
	public String[] getScoreDevideAndConquer(String s1, String s2){
		String Z = "", W = "";
		char[] c1 = s1.toCharArray(), c2 = s2.toCharArray();
		int s1Len = s1.length();
		int xlen = s1Len, xmid = s1Len/2, ylen = s2.length();
		if(xlen == 0 || ylen == 0){
			int len = xlen==0?ylen:xlen;
			char[] c = xlen==0?c2:c1;
			for(int i = 1; i<len; i++){
				Z=Z.concat(xlen==0?"-":c1[i]+"");
				W=W.concat(xlen==0?c2[i]+"":"-");
				score+=scoringSchmea.get("-", c[i-1]+"");
			}
			return new String[]{Z,W};
		}
		if(xlen == 1 || ylen == 1) return needlemanWunsch(s1,s2);
		
		String first = s1.substring(0, xmid), second = s1.substring(xmid, xlen);
		int[] scorel = NWScore(first, s2);
		int[] scorer = NWScore(reverse(second),reverse(c2));
		int ymid = partitionY(scorel,reverse(scorer));
		
		if(ymid == ylen) return getScoreDevideAndConquer(first, s2.substring(0,ymid));
		String[] r1 = getScoreDevideAndConquer(first, s2.substring(0,ymid));
		String[] r2 = getScoreDevideAndConquer(second, s2.substring(ymid,ylen));
		return addStrings(r1,r2);	
	}
	public static String reverse(String orig){
	    char[] s = orig.toCharArray();
	    int n = s.length;
	    int halfLength = n / 2;
	    for (int i=0; i<halfLength; i++){
	        char temp = s[i];
	        s[i] = s[n-1-i];
	        s[n-1-i] = temp;
	    }
	    return new String(s);
	}public static String reverse(char[] s){
	    int n = s.length;
	    int halfLength = n / 2;
	    for (int i=0; i<halfLength; i++){
	        char temp = s[i];
	        s[i] = s[n-1-i];
	        s[n-1-i] = temp;
	    }
	    return new String(s);
	}
	
	private int[] reverse(int[] scorer) {
		int[] reverse = new int[scorer.length];
		for(int i = 0; i<scorer.length; i++)
			reverse[i] = scorer[scorer.length-1-i];
		return reverse;
	}

	public int score;
	private String[] needlemanWunsch(String A, String B) {
		char[] c1 = A.toCharArray(), c2 = B.toCharArray();
		int[][] F = new int[A.length()+1][B.length()+1];
		F[0][0] = scoringSchmea.get("-", "-");
		for(int i = 1; i<=A.length(); i++)
			F[i][0] = F[i-1][0] + scoringSchmea.get("-", c1[i-1]+"")*i;
		for(int j = 1; j<=B.length(); j++)
			F[0][j] = F[0][j-1] + scoringSchmea.get("-", c2[j-1]+"")*j;
		for(int i = 1; i<=A.length(); i++)
			for(int j = 1; j<=B.length(); j++){
				int match  = F[i-1][j-1]+scoringSchmea.get(c1[i-1]+"", c2[j-1]+""),
					delete = F[i-1][j]+scoringSchmea.get("-", c1[i-1]+""),
					insert = F[i][j-1]+scoringSchmea.get("-", c2[j-1]+"");
				F[i][j] = Math.max(Math.max(insert, match),delete);//max(new int[]{match,delete,insert});
			}
		
		score += F[A.length()][B.length()];
		return alignment(A,B,F);
	}private String[] alignment(String A, String B, int[][] F) {
		String AlignmentA = "", AlignmentB = "";
		int i = A.length(), j = B.length();
		while(i>0 || j>0){
			if(i>0 && j>0 && F[i][j] == F[i-1][j-1]+scoringSchmea.get(A.charAt(i-1)+"", B.charAt(j-1)+"")){
				AlignmentA=A.charAt(--i)+AlignmentA;
				AlignmentB=B.charAt(--j)+AlignmentB;
			}else if(i>0 && F[i][j] == F[i-1][j] + scoringSchmea.get("-", A.charAt(i-1)+"")){
				AlignmentA=A.charAt(--i)+AlignmentA;
				AlignmentB="-"+AlignmentB;	
			}else if(j>0){
				AlignmentB=B.charAt(--j)+AlignmentB;
				AlignmentA="-"+AlignmentA;
			}
		}
		return new String[]{AlignmentA,AlignmentB};
	}


	private String[] addStrings(String[] a, String[] b) {
		int max = a.length>b.length?a.length:b.length;
		String[] v = new String[max];
		for(int i = 0; i<max; i++)
			v[i] = a[i].concat(b[b.length-1-i]);		
		return v;
	}


	private int partitionY(int[] scorel, int[] scorer) {
		int max = Integer.MIN_VALUE, maxIndex = -1;
		for(int i = 0; i<scorel.length; i++)
			if(scorel[i]+scorer[i]>max){
				max = scorel[i]+scorer[i];
				maxIndex = i;
			}
		return maxIndex;
	}private int[] NWScore(String X, String Y){
		char[] cc1 = X.toCharArray(), cc2 = Y.toCharArray();
		int i = X.length(), j = Y.length();
		int[] cacheM = new int[i+1], cacheN = new int[j], tempCacheN = new int[j];
		cacheM[0] = scoringSchmea.get("-", "-");
		
		for(int m = 1; m<=i; m++)
			cacheM[m]+=cacheM[m-1]+scoringSchmea.get(cc1[m-1]+"", "-");
		for(int n = 0; n<j; n++)
			cacheN[n]+=(n==0?cacheM[0]:cacheN[n-1])+scoringSchmea.get(cc2[n]+"", "-");

		int[] c2 = tempCacheN, c1 = cacheN;
		for(int m = 1; m<=i; m++){
			c2 = tempCacheN; c1 = cacheN;
			if(m%2==0){
				c1 = tempCacheN; c2 = cacheN;
			}for(int n = 0; n<j; n++){		
				int a = c1[n]						+ scoringSchmea.get("-", cc1[m-1]+""), //a = cache[m-1][n]	+ scoringSchmea.get("-", X.charAt(m-1)+"")
					b =(n==0?cacheM[m]:c2[n-1])		+ scoringSchmea.get("-", cc2[n]+""), //b = cache[m][n-1] 	+ scoringSchmea.get("-", Y.charAt(n-1)+"")
					c =(n==0?cacheM[m-1]:c1[n-1])	+ scoringSchmea.get(cc1[m-1]+"", cc2[n]+""); //cache[m-1][n-1] + scoringSchmea.get(X.charAt(m-1)+"", Y.charAt(n-1)+"")
				c2[n] = Math.max(Math.max(a, b),c);//max(new int[]{a,b,c});
			}	
		}
		return prepend(cacheM[i],c2);		
	}private int[] prepend(int i, int[] c2) {
		int[] v = new int[c2.length+1];
		v[0] = i;
		for(int j = 1; j<c2.length+1; j++)
			v[j] = c2[j-1]; 
		return v;
	}


	public String getModfiedString() {
		String s = "";
		for(Results r : getTraceBack()){
			if(r.action.equals(Results.UPDATE)){
				s+=r.s2;
			}else if(r.action.equals(Results.INSERT)){
				s+=r.s1;
			}else if(r.action.equals(Results.DELETE)){
				continue;
			}
		}
		
		return s;
	}
	
	public void traceBackRecurse(int i, int j) {
		if(i==0 && j == 0) return;
		int a = (i-1>=0?(cache[i-1][j] + scoringSchmea.get("-", s1.charAt(i-1)+"")):Integer.MIN_VALUE);
		int b = (j-1>=0?(cache[i][j-1] + scoringSchmea.get("-", s2.charAt(j-1)+"")):Integer.MIN_VALUE);
		int c = (i-1>=0 && j-1>=0?(cache[i-1][j-1] + scoringSchmea.get(s1.charAt(i-1)+"", s2.charAt(j-1)+"")):Integer.MIN_VALUE);
		
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
		final static String DELETE = "Delete",
							UPDATE = "Update",
							INSERT = "Insert",
							MATCH  = "Match";
		
		String s1, s2, action;
		int cost;
		public Results(String s1, String s2, String action){
			this.s1 = s1; this.s2 = s2; this.action = action;
			cost = scoringSchmea.get(s1, s2);
		}		
		
		@Override public String toString(){
			return "("+(s1.equals(s2)?MATCH:action)+" "+s1+"->"+s2+" COST:"+cost+")";
		}
	}
	
	public int getScoreRecurse(String s1, String s2){
		this.s1 = s1; this.s2 = s2;
		return _getScoreRecurse(s1.length(), s2.length(), s1, s2);
	}private int _getScoreRecurse(int i, int j, String s1, String s2){
		if(i == 0 || j == 0){
			int s = 0;
			for(int a = 1; a<(i==0?j:i); a++)
				s+=scoringSchmea.get("-", (i==0?s2:s1).charAt(a-1)+"");
			return s;
		}
	
		int 	a = _getScoreRecurse(i-1, j, s1, s2)	+ scoringSchmea.get("-", s1.charAt(i-1)+""),
				b = _getScoreRecurse(i, j-1, s1, s2)	+ scoringSchmea.get("-", s2.charAt(j-1)+""),
				c = _getScoreRecurse(i-1, j-1, s1, s2)	+ scoringSchmea.get(s1.charAt(i-1)+"", s2.charAt(j-1)+"");
		
		return Math.max(Math.max(a, b),c);//max(new int[]{a,b,c});
	}
	
	private int max(int[] values) {
		int max = Integer.MIN_VALUE;
		for(int i : values)
			if(i>max) max = i;
		return max;
	}
	
//	int[][] scoringSchema;
	private void initDefaultTable() {
//		scoringSchema = new int[][];
		
		scoringSchmea = HashBasedTable.create();
		scoringSchmea.put("A", "A", 5);
		scoringSchmea.put("A", "C", -1);
		scoringSchmea.put("A", "G", -2);
		scoringSchmea.put("A", "T", -1);
		scoringSchmea.put("A", "-", -3);
		scoringSchmea.put("C", "A", -1);
		scoringSchmea.put("C", "C", 5);
		scoringSchmea.put("C", "G", -3);
		scoringSchmea.put("C", "T", -2);
		scoringSchmea.put("C", "-", -4);
		scoringSchmea.put("G", "A", -2);
		scoringSchmea.put("G", "C", -3);
		scoringSchmea.put("G", "G", 5);
		scoringSchmea.put("G", "T", -2);
		scoringSchmea.put("G", "-", -2);
		scoringSchmea.put("T", "A", -1);
		scoringSchmea.put("T", "C", -2);
		scoringSchmea.put("T", "G", -2);
		scoringSchmea.put("T", "T", 5);
		scoringSchmea.put("T", "-", -1);
		scoringSchmea.put("-", "A", -3);
		scoringSchmea.put("-", "C", -4);
		scoringSchmea.put("-", "G", -2);
		scoringSchmea.put("-", "T", -1);
		scoringSchmea.put("-", "-", 0);

		final List<String> 	columns = ImmutableList.copyOf(scoringSchmea.columnKeySet()),
				 			rows = ImmutableList.copyOf(scoringSchmea.columnKeySet());
		for(String column : columns){
			for(String row : rows){
				scoringSchmea.put(column.toLowerCase(), row.toLowerCase(), scoringSchmea.get(column, row));
			}			
		}
	}

	String fs1, fs2;
	public int getScoreDC(String s12, String s22) {
		score= 0;
		String[] s = getScoreDevideAndConquer(s12, s22);
		fs1 = s[0]; fs2 = s[1];
//		return getScoreWithoutAlign(fs1,s12,fs2,s22);
		return score;
	}
	
	public int getScoreWithoutAlign(String s1, String ss1, String s2, String ss2){
		int s = 0;
		for (int i = 0; i < s1.length(); i++) {
			s+=scoringSchmea.get(s1.charAt(i)+"", ss1.charAt(i)+"");
		}for (int i = 0; i < s2.length(); i++) {
			s+=scoringSchmea.get(s2.charAt(i)+"", ss2.charAt(i)+"");
		}
		return s;
	}
}
