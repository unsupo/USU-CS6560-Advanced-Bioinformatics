package utilities.data;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class ClassLoad {
	public IGatherInstance load(String path, String className) throws Exception{
		File file = new File(path);
		Class cls = null;
		URL url = file.toURL();
		URL[] urls = new URL[]{url};
		ClassLoader cl = new URLClassLoader(urls);
		cls = cl.loadClass(className);
		return (IGatherInstance)cls.getConstructor().newInstance();
	}
	
	public IGatherInstance getGatherInstance(String className) throws Exception{
		return (IGatherInstance) Class.forName(className).getConstructor().newInstance();
	}
}
