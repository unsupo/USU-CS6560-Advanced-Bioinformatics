package utilities.shell;

import com.jcraft.jsch.*;
import utilities.Utility;
import utilities.data.ReadCVSFile;
import utilities.data.gatherers.NCBIGatherer;
import utilities.filesystem.FileOptions;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RemoteShell {
	public static void main(String[] args) throws Exception {
		List<String> hosts = FileOptions.readFileIntoListString(Utility.HOSTS_FILE);
		String username = args[0], password = args[1];
//		runProgram(args);
		RemoteShell.multiExecute(hosts,username,password,"ps -ef|grep [M]inimumEditDistance");

//		RemoteShell.multiSCP(hosts,Utility.RUNNABLE_JAR,"MinimumEditDistance.jar",username,password);
//		RemoteShell.multiExecuteRun(hosts,username,password,"sudo rpm -Uhv --force jdk-8u51-linux-x64.rpm &");
//		RemoteShell.multiExecute(hosts,username,password,"sudo rpm -Uhv --force jdk-8u51-linux-x64.rpm");
//		RemoteShell.multiSCP(hosts,Utility.RESOURCE_DIRECTORY+"/project/jdk-8u51-linux-x64.rpm","jdk-8u51-linux-x64.rpm",username,password);

//		HashMap<String,String> hostValue = new HashMap<>();
//		String name = "jdk-8u51-linux-x64.rpm", myFile = Utility.RESOURCE_DIRECTORY+"project/"+name;
//		((List<String>)((ArrayList<String>)hosts).clone())
//				.forEach(a->hostValue.put(a,"dd if='"+myFile+"' | ssh  -p 22 -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null "+a+" \"sudo su - cxamon dd of='"+name+"'\""));
//		RemoteShell.multiExecute(hostValue,username,password);

//		RemoteShell.multiExecute(hosts,username,password,"java -version");
//		RemoteShell.multiExecute(hosts,username,password,"ls MinimumEditDistance.jar");
	}

	public static final int MAX_THREAD_COUNT = 100;

	public static void runProgram(String[] args) throws IOException, JSchException, InterruptedException {
		List<File> dnaSequences = FileOptions.getAllFilesEndsWith(ReadCVSFile.DIR, NCBIGatherer.EXTENSION);

		String username = args[0], password = args[1];
		List<String> hosts = FileOptions.readFileIntoListString(Utility.HOSTS_FILE);
		HashMap<String,String> hostOutput = new HashMap<>();
		int k = 0;
		for(int i = 0; i<dnaSequences.size(); i++)
			for(int j = i; j<dnaSequences.size(); j++){
				String host = hosts.get(k++);
				k%=hosts.size();
				if(!hostOutput.containsKey(host))
					hostOutput.put(host,i+","+j);
				else
					hostOutput.put(host,hostOutput.get(host)+" "+i+","+j);
			}


		HashMap<String,String> hostValue = new HashMap<>();
		((List<String>)((ArrayList<String>)hosts).clone())
				.forEach(a->hostValue.put(a,"java -jar MinimumEditDistance.jar "+ hostOutput.get(a) + " >> output.txt  &"));
		RemoteShell.multiExecute(hostValue,username,password);
		multiExecute(hostValue,username,password);
	}

	public static void copyJarToAllHosts(String[] args) throws InterruptedException, IOException {
		String username = args[0], password = args[1];
		List<String> hosts = FileOptions.readFileIntoListString(Utility.HOSTS_FILE);

		RemoteShell.multiSCP(hosts,Utility.RUNNABLE_JAR,"MinimumEditDistance.jar",username,password);
	}


	public static void multiExecute(final List<String> hosts, String user, String password, String command) throws InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(MAX_THREAD_COUNT);
		for(final String host : hosts)
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					RemoteShell rs = new RemoteShell(user,password,host);
					rs.execute(command);
					System.out.println(host+"\n\t"+rs.getResults()+"\n\t"+rs.getErrors());
//					if(rs.getErrors().stream().filter(a->a.contains("1.7")).collect(Collectors.toList()).size() != 0)
//						System.out.println(host+" "+rs.getErrors());
//					if(!rs.getErrors().isEmpty())
//						System.out.println(host);
					rs.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		executor.shutdown();
		executor.awaitTermination(5, TimeUnit.MINUTES);
	}
	public static void multiExecute(final HashMap<String,String> hostsKeyAndCommandValue, String user, String password) throws InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(MAX_THREAD_COUNT);
		for(final String host : hostsKeyAndCommandValue.keySet())
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						RemoteShell rs = new RemoteShell(user,password,host);
						rs.execute(hostsKeyAndCommandValue.get(host));
						System.out.println(host+"\n\t"+rs.getResults()+"\n\t"+rs.getErrors());
						rs.disconnect();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		executor.shutdown();
		executor.awaitTermination(5, TimeUnit.MINUTES);
	}
	public static void multiExecuteRun(final List<String> hosts, String user, String password, String command) throws InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(MAX_THREAD_COUNT);
		for(String host : hosts)
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						RemoteShell rs = new RemoteShell(user,password,host);
						rs.execRun(command);
						rs.disconnect();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		executor.shutdown();
		executor.awaitTermination(5, TimeUnit.MINUTES);
	}
	public static void multiSCP(final List<String> hosts, final String fromFile, final String toFile, final String user, final String password) throws InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(MAX_THREAD_COUNT);
		for(String host : hosts)
			executor.execute(new Runnable() {
				@Override
				public void run() {
					RemoteShell rs = new RemoteShell(user,password,host);
					rs.scpTO(toFile,fromFile);
					rs.disconnect();
				}
			});
		executor.shutdown();
		executor.awaitTermination(5, TimeUnit.MINUTES);
	}


	private String user, host, password;
	private JSch jsch;
	private Session session;
	private List<String> results = new ArrayList<String>(), errors = new ArrayList<String>();

	public void setUsername(String user) {
		this.user = user;
	}

	public void setPassword(String pass) {
		this.password = pass;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void clearResults() {
		this.results = new ArrayList<String>();
	}

	public String getUsername() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public String getHost() {
		return host;
	}

	public List<String> getResults() {
		return results;
	}

	public String[] getResultsString() {
		if(results.isEmpty())
			return new String[]{};
		return results.get(0).split("\n");
	}

	public String getExec() {
		return exec;
	}

	public void printResults() {
		for (String s : results)
			System.out.println(s);
		for (String s : errors)
			System.out.println(s);
	}

	public RemoteShell(String user, String password, String host) {
		this.user = user;
		this.host = host;
		this.password = password;
		jsch = new JSch();
		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		try {
			session = jsch.getSession(user, host, 22);
		} catch (JSchException e) {
			System.err.println("Can't get a session with username: " + user
					+ " host: " + host + "\n" + e);
		}
		session.setPassword(password);
		session.setConfig(config);
	}

	public void disconnect() {
		session.disconnect();
	}

	public void scp(String rfile, String lfile) {
		//I expect this
		//scp("/u01/app/somefile.txt","root@gpcdev18.tmx.com:/u01/app/somefile.txt")
		//first copy it to local host
		scpFROM(rfile,".");
		//then create the new session this split will get the root@gpcdev18.tmx.com part 
		String location = lfile.split(":")[0];
		String username = location.split("@")[0];
		String password = location.split("@")[0];
		String host = location.split("@")[1];
		RemoteShell rs = new RemoteShell(username,password,host);
		rs.scpTO(rfile.split("/")[rfile.split("/").length-1],lfile.split(":")[1]);
		rs.disconnect();
	}

	private void scpTO(String rfile, String lfile) {
		FileInputStream fis = null;
		try {
			session.connect();

			boolean ptimestamp = true;

			// exec 'scp -t rfile' remotely
			String command = "scp " + (ptimestamp ? "-p" : "") + " -t " + rfile;
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			// get I/O streams for remote scp
			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();

			channel.connect();

			if (checkAck(in) != 0) {
				System.exit(0);
			}

			File _lfile = new File(lfile);

			if (ptimestamp) {
				command = "T " + (_lfile.lastModified() / 1000) + " 0";
				// The access time should be sent here,
				// but it is not accessible with JavaAPI ;-<
				command += (" " + (_lfile.lastModified() / 1000) + " 0\n");
				out.write(command.getBytes());
				out.flush();
				if (checkAck(in) != 0) {
					System.exit(0);
				}
			}

			// send "C0644 filesize filename", where filename should not include
			// '/'
			long filesize = _lfile.length();
			command = "C0644 " + filesize + " ";
			if (lfile.lastIndexOf('/') > 0) {
				command += lfile.substring(lfile.lastIndexOf('/') + 1);
			} else {
				command += lfile;
			}
			command += "\n";
			out.write(command.getBytes());
			out.flush();
			if (checkAck(in) != 0) {
				System.exit(0);
			}

			// send a content of lfile
			fis = new FileInputStream(lfile);
			byte[] buf = new byte[1024];
			while (true) {
				int len = fis.read(buf, 0, buf.length);
				if (len <= 0)
					break;
				out.write(buf, 0, len); // out.flush();
			}
			System.out.println("Done copying "+rfile+" TO "+host);
			fis.close();
			fis = null;
			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();
			if (checkAck(in) != 0) {
				System.exit(0);
			}
			out.close();

			channel.disconnect();
			session.disconnect();

		} catch (Exception e) {
			System.err.println("Machine Failed: "+host);
			e.printStackTrace();
			try {
				if (fis != null)
					fis.close();
			} catch (Exception ee) {
			}
		}
	}

	private void scpFROM(String rfile, String lfile) {
		FileOutputStream fos = null;
		try {
			session.connect();
		} catch (JSchException e) {
			System.err.println("Can't connect to session\n" + e);
		}
		try {
			String prefix = null;
			if (new File(lfile).isDirectory()) {
				prefix = lfile + File.separator;
			}

			// exec 'scp -f rfile' remotely
			String command = "scp -f " + rfile;
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			// get I/O streams for remote scp
			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();

			channel.connect();

			byte[] buf = new byte[1024];

			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();

			while (true) {
				int c = checkAck(in);
				if (c != 'C') {
					break;
				}

				// read '0644 '
				in.read(buf, 0, 5);

				long filesize = 0L;
				while (true) {
					if (in.read(buf, 0, 1) < 0) {
						// error
						break;
					}
					if (buf[0] == ' ')
						break;
					filesize = filesize * 10L + (long) (buf[0] - '0');
				}

				String file = null;
				for (int i = 0;; i++) {
					in.read(buf, i, 1);
					if (buf[i] == (byte) 0x0a) {
						file = new String(buf, 0, i);
						break;
					}
				}

				// System.out.println("filesize="+filesize+", file="+file);

				// send '\0'
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();

				// read a content of lfile
				fos = new FileOutputStream(prefix == null ? lfile : prefix
						+ file);
				int foo;
				while (true) {
					if (buf.length < filesize)
						foo = buf.length;
					else
						foo = (int) filesize;
					foo = in.read(buf, 0, foo);
					if (foo < 0) {
						// error
						break;
					}
					fos.write(buf, 0, foo);
					filesize -= foo;
					if (filesize == 0L)
						break;
				}
				System.out.println("Done copying FROM");
				fos.close();
				fos = null;

				if (checkAck(in) != 0) {
					return;
				}

				// send '\0'
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();
			}

		} catch (Exception e) {
			System.out.println(e);
			try {
				if (fos != null)
					fos.close();
			} catch (Exception ee) {
			}
		}
		session.disconnect();
	}

	private int checkAck(InputStream in) throws IOException {
		int b = in.read();
		// b may be 0 for success,
		// 1 for error,
		// 2 for fatal error,
		// -1
		if (b == 0)
			return b;
		if (b == -1)
			return b;

		if (b == 1 || b == 2) {
			StringBuffer sb = new StringBuffer();
			int c;
			do {
				c = in.read();
				sb.append((char) c);
			} while (c != '\n');
			if (b == 1) { // error
				System.out.print(sb.toString());
			}
			if (b == 2) { // fatal error
				System.out.print(sb.toString());
			}
		}
		return b;
	}

	private String exec;
	
	/**
	 * Run the shell command with no expected output.  Useful when running a command that might run forever or to be stopped at a later date.
	 * @param exe
	 * @throws IOException
	 * @throws JSchException
	 */
	public void execRun(String exe) throws IOException, JSchException{
		this.exec = exe;
		try {
			session.connect();
		} catch (JSchException j) {
			System.err.println(host);
			if (!j.getMessage().equals("session is already connected")) {
				throw j;
			}
		}
		Channel channel = null;
		try {
			channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(exe);
			channel.setInputStream(null);
			channel.connect();
		} catch (JSchException je) {
			channel = session.openChannel("shell");
			OutputStream ops = channel.getOutputStream();
			PrintStream ps = new PrintStream(ops, true);
			ps.println(exe);
			ps.close();
			channel.setInputStream(null);
			channel.connect();
		}
		channel.disconnect();		
	}

	public void execute(String exe) throws JSchException, IOException, InterruptedException {
		this.exec = exe;
		try {
			session.connect();
		} catch (JSchException j) {
			System.err.println(host);
			if (!j.getMessage().equals("session is already connected")) {
				throw j;
			}
		}
		Channel channel = null;
		InputStream in = null, inerr = null;
		try {
			channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(exe);
			channel.setInputStream(null);
//			((ChannelExec) channel).setErrStream(System.err);
			in = channel.getInputStream();
			inerr = ((ChannelExec) channel).getErrStream();
			channel.connect();
		} catch (JSchException je) {
			channel = session.openChannel("shell");
			OutputStream ops = channel.getOutputStream();
			PrintStream ps = new PrintStream(ops, true);
			ps.println(exe);
			ps.close();
			channel.setInputStream(null);
			in = null;
			in = channel.getInputStream();
			channel.connect();
		}
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inerr));
		String line; while((line = bufferedReader.readLine()) != null) errors.add(line);
		byte[] tmp = new byte[1024];
		while (true) {
			while (in.available() > 0) {
				int i = in.read(tmp, 0, 1024);
				if (i < 0)
					break;
				results.add(new String(tmp, 0, i));
			}

			if (channel.isClosed()) {
				if (in.available() > 0)
					continue;
				break;
			}
			Thread.sleep(1000);
		}
		channel.disconnect();
	}

	public List<String> getErrors() {
		return errors;
	}

}
