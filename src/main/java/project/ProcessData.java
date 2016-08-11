package project;

import utilities.MinimumEditAlgorithm;
import utilities.data.ReadCVSFile;
import utilities.data.gatherers.NCBIGatherer;
import utilities.filesystem.FileOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ProcessData {
	static final File jarFile = new File(new ProcessData().getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
	static final String path = "resources/project/dna_sequences";

	
	public static void main(String[] args) throws IOException, URISyntaxException {
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
