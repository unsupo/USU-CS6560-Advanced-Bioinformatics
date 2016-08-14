package project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import utilities.MinimumEditAlgorithm;
import utilities.data.ReadCVSFile;
import utilities.data.gatherers.NCBIGatherer;
import utilities.filesystem.FileOptions;

public class ProcessData {
	static final File jarFile = new File(new ProcessData().getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
	static final String path = "resources/project/dna_sequences";

	
	public static void main(String[] args) throws IOException, URISyntaxException {
		String path = "C:\\Users\\Jonathan Arndt\\Downloads\\results\\VP24_results";
		Table<String,String,Double> scores = HashBasedTable.create();
		FileOptions.getAllFiles(path,"score.txt")//.forEach(a->{try{System.out.println(FileOptions.readFileIntoString(a.getAbsolutePath()));}catch(Exception e){}});;
		.forEach(a->{
			String[] split = a.getAbsolutePath().split("\\\\");
			String[] names = split[split.length-2].split("_dna_sequences_");
			String filesContents = null;
			try {
				filesContents = FileOptions.readFileIntoString(a.getAbsolutePath()).replaceAll("[^0-9]+", "").substring(0, 4);
			} catch (Exception e) {
				e.printStackTrace();
			}
			scores.put(names[0].replace("dna_sequences_", ""), names[1], Double.parseDouble(filesContents));
			scores.put(names[1],names[0].replace("dna_sequences_", ""), Double.parseDouble(filesContents));
		});
		ArrayList<String> names = new ArrayList<>(scores.columnKeySet());
		ArrayList<ArrayList<Double>> d = new ArrayList<>(names.size());
		for(String name2 : names){
			ArrayList<Double> sub = new ArrayList<>();
			for (String name1 : names){
				if(scores.get(name1, name2) == null)
					System.out.println(name1+" "+name2);
				sub.add(scores.get(name1, name2));
			}
			d.add(sub);
		}
//		System.out.println(scores);
		for (int i = 0; i < d.size(); i++)
			for (int j = 0; j < d.get(i).size(); j++)
				d.get(i).set(j, Math.abs(d.get(i).get(j)-d.get(i).get(i)));
		
		
		NeighborJoining.join(names, d).newick(System.out);
		
	}

	public static void jarStuff(String[] args) throws IOException{
		List<File> dnaSequences = FileOptions.getAllFilesEndsWith(ReadCVSFile.DIR, NCBIGatherer.EXTENSION);
		if(dnaSequences.size() == 0){
			final JarFile jar = new JarFile(jarFile);
		    final Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
		    while(entries.hasMoreElements()) {
				JarEntry e = (JarEntry)entries.nextElement();
		        final String name = e.getName();
		        if (name.startsWith(path + "/") && name.endsWith(NCBIGatherer.EXTENSION)) { //filter according to the path
		            dnaSequences.add(extract(name));
		        }
		    }
		    jar.close();
		}

//		System.out.println(FileOptions.readFileIntoString(dnaSequences.get(0).getAbsolutePath()));
		for(String batch : args) {
			String[] options = batch.split(",");
			int s1 = Integer.parseInt(options[0]), s2 = Integer.parseInt(options[1]);
			MinimumEditAlgorithm me = new MinimumEditAlgorithm();
			MinimumEditAlgorithm.compare(me, dnaSequences.get(s1).getAbsolutePath(), dnaSequences.get(s2).getAbsolutePath());
		}
	}

	/**
	 *  This method is responsible for extracting resource files from within the .jar to the temporary directory.
	 *  @param filePath The filepath relative to the 'Resources/' directory within the .jar from which to extract the file.
	 *  @return A file object to the extracted file
	 **/
	public static File extract(String filePath){
		try{
			File f = File.createTempFile(filePath, null);
			FileOutputStream resourceOS = new FileOutputStream(f);
			byte[] byteArray = new byte[1024];
			int i;
			InputStream classIS = new ProcessData().getClass().getClassLoader().getResourceAsStream(filePath);
			//While the input stream has bytes
			while ((i = classIS.read(byteArray)) > 0){ //Write the bytes to the output stream
				resourceOS.write(byteArray, 0, i);
			}
			//Close streams to prevent errors
			classIS.close();
			resourceOS.close();
			return f;
		}
		catch (Exception e)	{
			System.out.println("An error has occurred while extracting the database. This may mean the program is unable to have any database interaction, please contact the developer.\nError Description:\n"+e.getMessage());
			return null;
		}
	}
}
