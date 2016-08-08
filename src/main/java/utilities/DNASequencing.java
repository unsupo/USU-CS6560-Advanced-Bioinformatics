package utilities;

import java.io.IOException;
import java.util.Arrays;

public class DNASequencing {
	public static void main(String[] args) throws IOException {
//		Arrays.asList('A','C','G','T','-').forEach(a->System.out.println((int)a));
		String s1 = GatherData.readFileIntoString(GatherData.DATA_DIR+"/Neandertal.txt").toUpperCase()
					,s2 = GatherData.readFileIntoString(GatherData.DATA_DIR+"/Prototypical_human.txt").toUpperCase();
		new DNASequencing().getScoreValue(s1, s2);
	}
	private int score;
	private int[][] scoringSchema = {
			{5,-1,2,-1,-3},//a[0]//A match with
			{-1,5,-3,-2,-4},//a[0]//C match with
			{2,-3,5,-2,-2},//a[0]//G match with
			{-1,-2,-2,5,-1},//a[0]//T match with
			{-3,-4,-2,-1,0},//a[0]//- match with 
	};
	private int[] mapper;
	public DNASequencing(){
		init();
	}private void init(){
		mapper = new int['T'+1];
		mapper['A']=0;
		mapper['C']=1;
		mapper['G']=2;
		mapper['T']=3;
		mapper['-']=4;
	}
	public int getScoreValue(String s1, String s2){
		char[] c1 = s1.toCharArray(), c2 = s2.toCharArray();
		hilbert(c1,c2);
		return score;
	}
	public String[] getScore(String s1, String s2){
		char[] c1 = s1.toCharArray(), c2 = s2.toCharArray();
		char[][] a = hilbert(c1,c2);
		return new String[]{String.valueOf(a[0]), String.valueOf(a[1])};
	}
	private char[][] hilbert(char[] c1, char[] c2) {
		int s1Len = c1.length, s2Len = c2.length;
		int maxLen = Math.max(s1Len, s2Len);
		char[] Z = new char[maxLen], W = new char[maxLen];
		int xlen = s1Len, xmid = s1Len/2, ylen = s2Len;
		if(xlen == 0 || ylen == 0){
			int len = xlen==0?ylen:xlen;
			for(int i = 1; i<len; i++){
				if(xlen==0){
					Z[i-1]='-';
					W[i-1]=c2[i];
					score+=scoringSchema[mapper['-']][mapper[c2[i-1]]];
				}else{
					Z[i-1]=c1[i];
					W[i-1]='-';
					score+=scoringSchema[mapper['-']][mapper[c1[i-1]]];
				}
			}
			return new char[][]{Z,W};
		}
		if(xlen == 1 || ylen == 1) return needlemanWunsch(c1,c2);
		
		char[] first = Arrays.copyOfRange(c1,0, xmid), second = Arrays.copyOfRange(c1,xmid, xlen);
		int[] scorel = NWScore(first, c2);
		int[] scorer = NWScore(reverse(second),reverse(c2));
		int ymid = partitionY(scorel,reverse(scorer));
		
		if(ymid == ylen) return hilbert(first, Arrays.copyOfRange(c2,0, ymid));
		char[][] r1 = hilbert(first, Arrays.copyOfRange(c2,0, ymid));
		char[][] r2 = hilbert(second, Arrays.copyOfRange(c2,ymid, ylen));
		return addStrings(r1,r2);	
	}
	private char[][] addStrings(char[][] a, char[][] b) {
		int max = a.length>b.length?a.length:b.length;
		char[][] v = new char[2][max];
		for(int i = 0; i<2; i++)
			v[i] = String.valueOf(a[i]).concat(String.valueOf(b[b.length-1-i])).toCharArray();		
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
	}
	private char[][] needlemanWunsch(char[] c1, char[] c2) {
		int[][] F = new int[c1.length+1][c2.length+1];
		F[0][0] = 0;
		for(int i = 1; i<=c1.length; i++)
			F[i][0] = F[i-1][0] + scoringSchema[4][mapper[c1[i-1]]]*i;
		for(int j = 1; j<=c2.length; j++)
			F[0][j] = F[0][j-1] + scoringSchema[4][mapper[c2[j-1]]]*j;
		for(int i = 1; i<=c1.length; i++)
			for(int j = 1; j<=c2.length; j++){
				int match  = F[i-1][j-1]+scoringSchema[mapper[c1[i-1]]][mapper[c2[j-1]]],
					delete = F[i-1][j]+scoringSchema[4][mapper[c1[i-1]]],
					insert = F[i][j-1]+scoringSchema[4][mapper[c2[j-1]]];
				F[i][j] = Math.max(Math.max(insert, match),delete);//max(new int[]{match,delete,insert});
			}
		
		score += F[c1.length][c2.length];
		return alignment(c1,c2,F);
	}private char[][] alignment(char[] c1, char[] c2, int[][] F) {
		int i = c1.length, j = c2.length;
		int max = Math.max(i, j);
		char[] AlignmentA = new char[max], AlignmentB = new char[max];
		while(i>0 || j>0){
			if(i>0 && j>0 && F[i][j] == F[i-1][j-1]+scoringSchema[mapper[c1[i-1]]][mapper[c2[j-1]]]){
				AlignmentA[AlignmentA.length-(--i)-1]=c1[i];
				AlignmentB[AlignmentB.length-(--j)-1]=c2[j];
			}else if(i>0 && F[i][j] == F[i-1][j] + scoringSchema[4][mapper[c1[i-1]]]){
				AlignmentA[AlignmentA.length-(--i)-1]=c1[i];
				AlignmentB[AlignmentB.length-j-1]='-';	
			}else if(j>0){
				AlignmentB[AlignmentB.length-(--j)-1]=c2[j];
				AlignmentA[AlignmentA.length-i-1]='-';
			}
		}
		return new char[][]{AlignmentA,AlignmentB};
	}
	private int[] NWScore(char[] c1, char[] c2){
		int i = c1.length, j = c2.length;
		int[] cacheM = new int[i+1], cacheN = new int[j], tempCacheN = new int[j];
		cacheM[0] = 0;		
		for(int m = 1; m<=i; m++)
			cacheM[m]+=cacheM[m-1]+scoringSchema[4][mapper[c1[m-1]]];
		for(int n = 0; n<j; n++)
			cacheN[n]+=(n==0?cacheM[0]:cacheN[n-1])+scoringSchema[4][mapper[c2[n]]];//scoringSchmea.get(cc2[n]+"", "-");

		int[] cc2 = tempCacheN, cc1 = cacheN;
		for(int m = 1; m<=i; m++){
			cc2 = tempCacheN; cc1 = cacheN;
			if(m%2==0){
				cc1 = tempCacheN; cc2 = cacheN;
			}for(int n = 0; n<j; n++){		
				int a = cc1[n]						+ scoringSchema[4][mapper[c1[m-1]]], //a = cache[m-1][n]	+ scoringSchmea.get("-", X.charAt(m-1)+"")
					b =(n==0?cacheM[m]:cc2[n-1])	+ scoringSchema[4][mapper[c2[n]]], //b = cache[m][n-1] 	+ scoringSchmea.get("-", Y.charAt(n-1)+"")
					c =(n==0?cacheM[m-1]:cc1[n-1])	+ scoringSchema[mapper[c1[m-1]]][mapper[c2[n]]]; //cache[m-1][n-1] + scoringSchmea.get(X.charAt(m-1)+"", Y.charAt(n-1)+"")
				cc2[n] = Math.max(Math.max(a, b),c);//max(new int[]{a,b,c});
			}	
		}
		return prepend(cacheM[i],cc2);		
	}private int[] prepend(int i, int[] c2) {
		int[] v = new int[c2.length+1];
		v[0] = i;
		for(int j = 1; j<c2.length+1; j++)
			v[j] = c2[j-1]; 
		return v;
	}
	public static char[] reverse(char[] s){
	    int n = s.length;
	    int halfLength = n / 2;
	    for (int i=0; i<halfLength; i++){
	        char temp = s[i];
	        s[i] = s[n-1-i];
	        s[n-1-i] = temp;
	    }
	    return s;
	}
	
	private int[] reverse(int[] scorer) {
		int[] reverse = new int[scorer.length];
		for(int i = 0; i<scorer.length; i++)
			reverse[i] = scorer[scorer.length-1-i];
		return reverse;
	}
}
