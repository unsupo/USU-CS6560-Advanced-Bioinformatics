package utilities.data;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import utilities.Utility;
import utilities.filesystem.FileOptions;

public class ReadCVSFile {
	public static void main(String[] args) throws IOException {
//		List<String> cvs = FileOptions.readFileIntoListString(Utility.RESOURCE_DIRECTORY+"project/dna_sequences/Table_S2_samples.csv");
		getIDs().forEach(System.out::println);
	}
	
	public final static String 	DIR 	= Utility.RESOURCE_DIRECTORY+"project/dna_sequences/",
								TABLE 	= DIR+"Table_S2_samples.csv",
								BACKUP 	= DIR+"leftovers.csv";
	private static ReadCVSFile instance;
	
	private List<String> cvsFile, backupFile;
	private ReadCVSFile(){
		try {
			cvsFile = FileOptions.readFileIntoListString(TABLE);
			backupFile = FileOptions.readFileIntoListString(BACKUP);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static ReadCVSFile getIntance(){
		if(instance == null)
			instance = new ReadCVSFile();
		return instance;
	}
	
	public static List<String> getIDs(){
		List<String> cvs = getIntance().cvsFile.stream()
				.filter(a->!"ID".equals(a.split(",")[1]))
				.map(a->a.split(",")[1])
				.collect(Collectors.toList());
		return cvs;
	}
	
	
	public static HashMap<String,String> getIDLinkMap(){
		HashMap<String, String> backup = new HashMap<>();
		getIntance().backupFile.forEach(a->{
			String[] c = a.split(",");
			backup.put(c[0], c[1]);
		});
		return backup;
	}
}
