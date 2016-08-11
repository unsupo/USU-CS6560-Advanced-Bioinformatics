package utilities;

/**
 * Created by jarndt on 8/1/16.
 */
public class Utility {
    public final static String  SEPERATOR           = System.getProperty("file.separator"),
                                DIR                 = System.getProperty("user.dir")+SEPERATOR,
                                RESOURCE_DIRECTORY  = DIR+"src/main/resources/",
                                HOSTS_FILE          = RESOURCE_DIRECTORY+"project/hosts/cluster_hosts",
                                RUNNABLE_JAR        = RESOURCE_DIRECTORY+"project/MinimumEditDistance.jar";
}
