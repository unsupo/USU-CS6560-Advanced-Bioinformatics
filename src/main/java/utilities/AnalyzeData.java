package utilities;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class AnalyzeData {
	public static void main(String[] args) throws IOException, NumberFormatException{
		List<String> v = GatherData.getAllFilesInFolder(GatherData.RESULTS_DIR, new String[]{"score.txt"});
//		new ScoreCompare(v.get(0),Integer.parseInt(GatherData.readFileIntoString(v.get(0))));
		List<ScoreCompare> vv = v.stream().parallel().map(a->{
				try{return new ScoreCompare(a, Integer.parseInt(GatherData.readFileIntoString(a)));}catch(Exception e){return null;}
			}).collect(Collectors.toList());
		Collections.sort(vv,(a,b)->a.score.compareTo(b.score));
		String data = vv.stream().map(a->a.toString()).collect(Collectors.joining("\n"));
		GatherData.dumpIntoFile(GatherData.RESULTS_DIR+"/alldata.txt", data);
		
		
//		Map<String,Integer> scoreMap = new ConcurrentHashMap<>();
//		v.stream().parallel().forEach(a->{
//				try{ 
//					scoreMap.put(a, Integer.parseInt(GatherData.readFileIntoString(a).trim()));
//				}catch(IOException e){}
//			});
//		scoreMap.keySet().stream().parallel()
//			.sorted((a,b)->scoreMap.get(a).compareTo(scoreMap.get(b)))
//			.forEach(a->System.out.println(scoreMap.get(a)+": "+a));
	}
	public static class ScoreCompare{
		Integer score;
		String name1, name2;
		public ScoreCompare(String path, Integer score){
			this.score = score; 
			String[] split = path.split("\\\\");
			String dirSplit = split[split.length-2];
			String a =dirSplit.replaceFirst("([A-Za-z()]+?_)*[A-Za-z()]+[0-9]+", "");
			name1 = dirSplit.substring(0, dirSplit.length()-a.length());
			name2 = a.substring(1);
		}
		@Override public int hashCode(){
			return score.hashCode()+name1.hashCode()+name2.hashCode();
		}@Override public String toString(){
			return name1+" and "+name2+"\n has a score of "+score;
		}@Override public boolean equals(Object o){
			if(!(o instanceof ScoreCompare)) return false;
			ScoreCompare s = (ScoreCompare)o;
			return s.score.equals(score) && 
					(s.name1.equals(name1) || s.name1.equals(name2)) &&
					(s.name2.equals(name1) || s.name2.equals(name2)); 
		}
	}
}
