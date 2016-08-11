package utilities.data.gatherers;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import utilities.Utility;
import utilities.data.IGatherInstance;
import utilities.data.ReadCVSFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class NCBIGatherer implements IGatherInstance{
	public static void main(String[] args) throws IOException {
//		Document doc = Jsoup.connect("http://www.ncbi.nlm.nih.gov/nuccore/KM233110.1").followRedirects(true).get();
//		System.out.println(doc);
//		Arrays.asList(doc.select("#help-desk-link").attr("href").split("\\+"));

		new NCBIGatherer().gatherData(Utility.RESOURCE_DIRECTORY+"project/dna_sequences/");

	}
	public static final String EXTENSION = "dna";
	
	private List<String> IDs;
	private HashMap<String, String> idAltMap;
	private Document doc;
	private final String lookUpURL = "http://www.ncbi.nlm.nih.gov/sviewer/viewer.fcgi?val=[*REPLACE*]&db=nuccore&dopt=genbank&extrafeat=976&fmt_mask=0&retmode=html&withmarkup=on&log$=seqview&maxplex=3&maxdownloadsize=1000000";
	private final String REPLACE_STRING = "[*REPLACE*]";
	private static final String SEARCH_LINK = "http://www.ncbi.nlm.nih.gov/nuccore/?term=";
	private static final String INITIAL_LINK = "http://www.ncbi.nlm.nih.gov/nuccore/";
	public NCBIGatherer() {
		IDs = ReadCVSFile.getIDs();
		idAltMap = ReadCVSFile.getIDLinkMap();
	}
	
	@Override
	public String gatherData(String path) {
		String file = "";
		try{
			for(String id : IDs)
				if(!new File(path+id+"."+EXTENSION).exists())
					load(id, path);
		}catch(Exception e){
			e.printStackTrace();
		}
		return file;
	}

	private void load(String id, String path) throws IOException {
		if(new File(path+"/"+id+".txt").exists())
			return;
		String url;
		if(idAltMap.containsKey(id)){
			url = idAltMap.get(id);
			String[] split = url.split("/");
			if(!split[split.length-1].replaceAll("[0-9]+", "").isEmpty())
				url = Jsoup.connect(url.trim()).userAgent("Chrome").timeout(10*1000).get().select("meta[name='ncbi_uidlist']").attr("content");
		}else{
			Response doc = Jsoup.connect(SEARCH_LINK+id)
					.followRedirects(true)
					.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2")
					.timeout(10*1000)
					.execute();
			
			url = doc.url().toString();
		}
		getData(url, path+"/"+id+"."+EXTENSION);
	}

	private String URL;
	private void getData(String uRL, String path) throws IOException {
		String[] split = uRL.split("/");
		URL = uRL;
		doc = Jsoup
				.connect(
						lookUpURL.replace(REPLACE_STRING,
								split[split.length - 1]))
				.userAgent("Chrome").get();

		eagerLoadDNASequence(path);
	}
	private void eagerLoadDNASequence(String filePath) throws IOException {
		String a = doc.select("span.ff_line").text().trim();
		if(new File(filePath).exists())
			filePath = filePath.substring(0, filePath.indexOf("."+EXTENSION))+"1."+EXTENSION;
		String result = a.trim().replaceAll("[^actgACTG]+", "");
		if(result.isEmpty()){
			System.out.println(URL);
			return;
		}
		dumpIntoFile(filePath, result);
	}

	public static void dumpIntoFile(String strFilePath, String data)
			throws IOException {
		FileOutputStream fos = new FileOutputStream(strFilePath, true);
		fos.write(data.getBytes());
		fos.close();
	}
}
