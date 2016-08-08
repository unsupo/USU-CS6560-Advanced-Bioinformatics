package project;

import java.io.File;
import java.io.IOException;
import java.util.List;

import utilities.MinimumEditAlgorithm;
import utilities.data.ReadCVSFile;
import utilities.data.gatherers.NCBIGatherer;
import utilities.filesystem.FileOptions;

public class ProcessData {
	public static void main(String[] args) throws IOException {
		List<File> dnaSequences = FileOptions.getAllFilesEndsWith(ReadCVSFile.DIR, NCBIGatherer.EXTENSION);

		MinimumEditAlgorithm me = new MinimumEditAlgorithm();
		MinimumEditAlgorithm.compare(me,dnaSequences.get(0).getAbsolutePath(),dnaSequences.get(1).getAbsolutePath());
	}
}
