package utilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GatherData {
	public static void main(String[] args) throws IOException {
		// http://www.ncbi.nlm.nih.gov/sviewer/viewer.fcgi?val=251831106&db=nuccore&dopt=genbank&extrafeat=976&fmt_mask=0&retmode=html&withmarkup=on&log$=seqview&maxplex=3&maxdownloadsize=1000000

//		String url = "http://www.ncbi.nlm.nih.gov/nuccore/?term=ebola";
//		Document document = Jsoup.connect(url).userAgent("Chrome").get();
//		System.out.println(document.select("div.rslt"));
			
		new GatherData("ebola,http://www.ncbi.nlm.nih.gov/nuccore/661348605");
//		\Africa\AF346967.zip
//		System.out.println(GatherData.readZipFileIntoString(GatherData.DATA_DIR+"\\Africa\\AF346967.zip", "Bamileke(AF346967).txt"));
//		GatherData.getAllFilesInFolder(GatherData.DATA_DIR).stream().filter(a->a.endsWith(".txt")).forEach(System.out::println);
		
//		List<String> l = GatherData.getAllFilesInFolder(GatherData.DATA_DIR, new String[]{".zip",".txt"});
//		for(String s : l)
//			System.out.println(s);
		

//		List<String> l = GatherData.getAllFilesInFolder(GatherData.DATA_DIR, new String[]{".zip",".txt"});
//		GatherData.writeResults(l.get(0),l.get(1),new String[]{"",""},0);
		
	}

	private Document doc;
	private HashMap<String, DNASequence> continents;
	private String URL;
	private final String lookUpURL = "http://www.ncbi.nlm.nih.gov/sviewer/viewer.fcgi?val=[*REPLACE*]&db=nuccore&dopt=genbank&extrafeat=976&fmt_mask=0&retmode=html&withmarkup=on&log$=seqview&maxplex=3&maxdownloadsize=1000000";
	private final String REPLACE_STRING = "[*REPLACE*]";
	private final String CONTAINS_STRING = "http://www.ncbi.nlm.nih.gov/nuccore/";
	public static final String[] DATA_SOURCE = new String[] {
			"Prototypical_human,http://www.ncbi.nlm.nih.gov/nuccore/251831106",
			"Neandertal,http://www.ncbi.nlm.nih.gov/nuccore/196123578",
			"Human_diversity,http://www.mtdb.igp.uu.se" };

	public static final String DATA_DIR = System.getProperty("user.dir")
			+ "/data";
	public static final String RESULTS_DIR = System.getProperty("user.dir")
			+ "/results";
	
	public GatherData(String... url) {
		continents = new HashMap<>();
		for (String s : url) {
			try {
				String[] ss = s.split(",");
				String fileName = ss[0];
				URL = ss[1];
				String file = "/" + fileName + ".txt";
				String data = readFileIntoString(DATA_DIR + "/" + file);
				if (data == null) {
					new File(DATA_DIR).mkdir();
					if (URL.contains(CONTAINS_STRING)) {
						getData(URL, DATA_DIR + "/" + file);
					} else {
						doc = Jsoup.connect(URL + "/sequences.php")
								.userAgent("Chrome").get();
						eagerLoadDNASequenceObject();
						for (String key : continents.keySet()) {
							String nPath = DATA_DIR + "/" + key;
							DNASequence c = continents.get(key);
							new File(nPath).mkdir();
							Map<String, Link> clm = c.getCountryLinkMap();
							for (String cKey : clm.keySet()) {
								String zipFile = clm.get(cKey).getZipFile();
								if(zipFile.isEmpty()){
									String path = nPath + "/"+ clm.get(cKey).name+"_"+cKey+".txt";
									if (!new File(path).exists())	
										getData(clm.get(cKey).getHtml(),path);
									continue;
								}
								
								String[] split = zipFile.split("/");
								if (!new File(nPath + "/"+ split[split.length - 1]).exists())
									downloadFileFromLink(URL + "/" + zipFile,
											nPath + "/"
													+ split[split.length - 1]);
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void getData(String uRL, String path) throws IOException {
		String[] split = uRL.split("/");
		doc = Jsoup
				.connect(
						lookUpURL.replace(REPLACE_STRING,
								split[split.length - 1]))
				.userAgent("Chrome").get();
		eagerLoadDNASequence(path);
	}

	public static boolean exists(String s1, String s2) {
		if(!new File(RESULTS_DIR).exists()) return false;
		String[] ss1 = s1.split("\\\\"), ss2 = s2.split("\\\\");
		String name = ss1[ss1.length-2]+"_"+ss1[ss1.length-1].substring(0, ss1[ss1.length-1].indexOf(".")-1);
		name += "_"+ss2[ss2.length-2]+"_"+ss2[ss2.length-1].substring(0, ss2[ss2.length-1].indexOf(".")-1);
		
		String path =RESULTS_DIR+"/"+name.replace(" ", "_");
		File f = new File(path);
		if(!f.exists()) return false;
		File[] ff = f.listFiles();
		boolean m = false, s = false;
		for(File fil : ff){
			if(fil.getName().matches("string_[0-9]+.txt"))
				m=true;
			if(fil.getName().equals("score.txt"))
				s = true;
		}
		
		return m&&s;
	}
	
	public static void writeResults(String p1, String p2, String[] correctedStrings, int score) throws IOException {
		new File(RESULTS_DIR).mkdir();
		p1 = p1.replace("/", "\\");
		p2 = p2.replace("/", "\\");
		String[] s1 = p1.split("\\\\"), s2 = p2.split("\\\\");
		String name="", v1 = "", v2 = "";
		if(p1.endsWith(".txt"))
			v1 = s1[s1.length-1].substring(0, s1[s1.length-1].indexOf("."));
		else
			v1 =  s1[s1.length-2]+"_"+s1[s1.length-1].substring(0, s1[s1.length-1].indexOf("."));
		if(p2.endsWith(".txt"))
			v2 = s2[s2.length-1].substring(0, s2[s2.length-1].indexOf("."));
		else
			v2 = s2[s2.length-2]+"_"+s2[s2.length-1].substring(0, s2[s2.length-1].indexOf("."));
		
		name = v1+"_"+v2;
		
		String path =RESULTS_DIR+"/"+name.replace(" ", "_");
		new File(path).mkdir();
		int i = 0;
		for(String s : correctedStrings)
			dumpIntoFile(path+"/string_"+i+++".txt", s);
		dumpIntoFile(path+"/score.txt", score+"");
	}

	
	public static List<String> getAllFilesInFolder(String path, String[] type) throws IOException{
		return Files.walk(Paths.get(path))
		        .filter(Files::isRegularFile)
		        .map(a->a.toAbsolutePath().toString())
		        .filter(a->{
		        	for(String s : type)
		        		if(a.endsWith(s) || a.equals(s))
		        			return true;
		        	return false;
		        })
		        .collect(Collectors.toList());
	}public static List<String> getAllFilesInFolder(String path) throws IOException{
		return Files.walk(Paths.get(path))
		        .filter(Files::isRegularFile)
		        .map(a->a.toAbsolutePath().toString())
		        .collect(Collectors.toList());
	}

	public static String readZipFileIntoString(String path, String fileName, boolean...endsWith) throws IOException {
		boolean b = false;
		if(endsWith != null && endsWith.length == 1)
			b = endsWith[0];
		
		ZipFile zipFile = new ZipFile(path);

		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		InputStream stream = null;
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			if(b && entry.getName().endsWith(fileName))
				stream = zipFile.getInputStream(entry);
			else if(entry.getName().equals(fileName))
				stream = zipFile.getInputStream(entry);
		}
		String value = "", line;
		BufferedReader br = new BufferedReader(new InputStreamReader(stream,"UTF-8"));
		while((line = br.readLine())!=null)
			value+=line;
		br.close();
		return value;
	}

	public static String readFileIntoString(String path) throws IOException {
		if (!new File(path).exists())
			return null;
		String result = "";
		BufferedReader br = new BufferedReader(new FileReader(path));
		String l;
		while ((l = br.readLine()) != null)
			result += l;
		br.close();
		return result.isEmpty() ? null : result;
	}

	public static void dumpIntoFile(String strFilePath, String data)
			throws IOException {
		FileOutputStream fos = new FileOutputStream(strFilePath, true);
		fos.write(data.getBytes());
		fos.close();
	}

	private void eagerLoadDNASequence(String filePath) throws IOException {
		String a = doc.select("span.ff_line").text().trim();
		if(new File(filePath).exists())
			filePath = filePath.substring(0, filePath.indexOf(".txt"))+"1.txt";
		dumpIntoFile(filePath, a.trim().replaceAll("[^actgACTG]+", ""));
	}

	private void eagerLoadDNASequenceObject() throws IOException {
		Elements a = doc.select("tbody > tr");
		for (Element e : a) {
			if (e.select("td.hl1").size() == 0)
				continue;
			String name = e.select("td.hl1").get(0).childNode(0).toString();
			DNASequence dna = new DNASequence(name);
			for (Element el : e.select("td.hl1").select("tbody")) {
				Map<String, Link> map = dna.getCountryLinkMap();
				for (Element ele : el.select("tr")) {
					Elements country = ele.select("td.fl1 > a"), link = ele
							.select("td.bc1 > a");
					if(country.size() == 0)
						country = ele.select("td.fl1");
					if (country.size() == 0 || link.size() == 0)
						continue;
					String key = country.get(0).attr("href");
					if(key.isEmpty())
						key = link.get(0).text();
					map.put(key, new Link(country.get(0)
							.attr("href"), link.get(0).attr("href"), link
							.get(0).text(),country.get(0).text()));
				}
			}
			continents.put(name, dna);
		}
	}

	private void downloadFileFromLink(String link, String path)
			throws IOException {
		URL url = new URL(link);
		InputStream in = url.openStream();
		OutputStream out = new BufferedOutputStream(new FileOutputStream(path));
		final int BUFFER_SIZE = 1024 * 4;
		byte[] buffer = new byte[BUFFER_SIZE];
		BufferedInputStream bis = new BufferedInputStream(in);
		int length;
		while ((length = bis.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}
		out.close();
		in.close();
	}

	public class DNASequence {
		private String continent;
		private Map<String, Link> countryLinkMap;

		public DNASequence() {
			init();
		}

		public DNASequence(String continent) {
			init();
			this.setContinent(continent);
		}

		private void init() {
			setCountryLinkMap(new HashMap<String, Link>());
		}

		public String getContinent() {
			return continent;
		}

		public void setContinent(String continent) {
			this.continent = continent;
		}

		public Map<String, Link> getCountryLinkMap() {
			return countryLinkMap;
		}

		public void setCountryLinkMap(Map<String, Link> countryLink) {
			this.countryLinkMap = countryLink;
		}

	}

	public class Link {
		private String zipFile, html, htmlName, name;

		public Link(String zipFile, String html, String htmlName, String name) {
			this.zipFile = zipFile;
			this.html = html;
			this.htmlName = htmlName;
			this.name = name;
		}

		public String getZipFile() {
			return zipFile;
		}

		public void setZipFile(String zipFile) {
			this.zipFile = zipFile;
		}

		public String getHtml() {
			return html;
		}

		public void setHtml(String html) {
			this.html = html;
		}

		public String getHtmlName() {
			return htmlName;
		}

		public void setHtmlName(String htmlName) {
			this.htmlName = htmlName;
		}
	}



}
