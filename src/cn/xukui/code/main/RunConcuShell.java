package cn.xukui.code.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import cn.xukui.code.utils.ProgressLine;
import cn.xukui.code.utils.Tasker;

public class RunConcuShell {

	public static void main(String[] args) {
		// 1.#### Parameter configuration
		if (args.length < 2 || (args.length == 1 && args[0].equals("-h"))) {
			System.out.println("Concu   : Concurrent to run optionally numbers of commamdline.");
			System.out.println("Version : 1.0.1");
			System.out.println("Usage   : ");
			System.out.println(" -f  <str>    Commandline list file.");
			System.out.println(" -t  <int>    Number of concurrent threads [1, 50]; ");
			System.out.println(" -w  <str>    Daemon name to be watched. eg.'java','wget'.");
			System.out.println(" -v           View output.");
			System.out.println("");
			System.exit(1);
		}

		String file = "";
		String sh = "";
		boolean isOutput = false;
		int t = 0;

		int i = 0;
		while (i < args.length) {
			switch (args[i]) {
			case "-f":
				file = args[i + 1];
				System.out.println("Commandline list file :" + file);
				File f = new File(file);
				if (!f.exists()) {
					System.out.println("file not found:" + file);
					return;
				}
				i += 2;
				break;
			case "-v":
				isOutput = true;
				i++;
				// variation = double.Parse(args[i + 1]);
				break;
			case "-w":
				sh = args[i + 1];
				i += 2;
				break;
			case "-t":
				try {
					t = Integer.parseInt(args[i + 1]);
					if (t < 1 || t > 50) {
						System.err.println("-t should is not in [1, 50]");
						return;
					}
					i += 2;
				} catch (Exception e) {
					System.err.println("wrong format option value if -t");
					System.err.println(" -t <int>    Number of concurrent threads [1, 50];");
					return;
				}

				break;
			default:
				System.out.println("no option:" + args[i]);
				return;

			}
		}

		List<String> execs = GetFileContentList(file);
		if (t == 0) {
			System.out.println(execs.get(0));
			runOneShell(sh, execs.get(0), isOutput);
		} else {
			runConcurrentShell(sh, execs, isOutput, t);
		}

		System.out.println("\n All the commandline were started, but it wasn't mean that they were done.d");
		//threadCount = Tasker.getPsCount("ps -a", sh);
		//监控剩余进程，
		System.out.println("Thank you!");

	}

	/***
	 * 进度显示时用的旋转的字符
	 * @param i
	 * @return
	 */
	private static char getChar(int i) {
		int j = i % 4;
		char a = '-';
		switch (j) {
		case 0:
			a = '-';
			break;
		case 1:
			a = '\\';
			break;
		case 2:
			a = '|';
			break;
		case 3:
			a = '/';
			break;

		default:
			break;
		}
		return a;
	}


	/***
	 * 并发执行多个程序	
	 * @param sh 可执行文件
	 * @param execs  程序列表
	 * @param isOutput 是否显示执行结果
	 * @param t 并发执行的线程数
	 */
	private static void runConcurrentShell(String sh, List<String> execs, boolean isOutput, int t) {
		// TODO Auto-generated method stub
		int total = execs.size();
		List<String> shellList = new ArrayList<String>();
		shellList.addAll(execs);
		int threadCount = Tasker.getPsCount("ps -a", sh);
		int a = 0;
		while (!shellList.isEmpty()) {
			int curr = 100 * (total - shellList.size()) / total;
			threadCount = Tasker.getPsCount("ps -a", sh);
			int startNum = t - threadCount;
			// System.out.print("\r" + ProgressLine.showBarByPoint(curr) +
			// " "+getChar(a) + threadCount + " " + (total - shellList.size()) +
			// "/" + total);
			System.out.print("\r" + ProgressLine.showBarByPoint(curr) + " " + getChar(a++) + " " + (total - shellList.size()) + "/" + total + " " + threadCount);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (startNum <= 0) {

			} else {
				// System.out.println("New Thread:"+startNum);
				for (int i = 0; i < startNum; i++) {
					if (shellList.isEmpty()) {
						break;
					}
					Map<String, Object> tmpParamsMap1 = new HashMap<String, Object>();
					tmpParamsMap1.put("sh", sh);
					tmpParamsMap1.put("shellline", shellList.get(0));
					tmpParamsMap1.put("isOutput", isOutput + "");
					String taskname = total - shellList.size() + "-shell-starting";
					new Tasker(tmpParamsMap1, taskname, Tasker.TASK_Concurrent).start();
					shellList.remove(0);
				}
			}
		}
		threadCount = Tasker.getPsCount("ps -a", sh);
		System.out.print("\r" + ProgressLine.showBarByPoint(100) + " " + getChar(a) + " " + (total - shellList.size()) + "/" + total + " " + threadCount);

	}

	@Test
	public void testOneshell() {
		// runOneShell("", "E:\\xukui\\00sparse\\bats2\\5-0.bat", false);
		// runOneShell("",
		// "java weka.classifiers.functions.Logistic -v -i -t E:/xukui/00sparse/pcadata/amazon.arff5.arff.ori-0-0.arff -x 10 -R 1.0E-8 -M -1 ",
		// false);
		// Tasker.getCurrentTasksName();
		// Tasker.getPsCount("ps -a","java");
		Tasker.getPsCount("tasklist.exe /FI 'imagename eq notepad.exe' /nh", "notepad");
		// runOneShell("", "tasklist.exe /FI 'imagename eq notepad.exe' /nh",
		// false);
	}

	/***
	 * �?���?��进程运行�?��可执行程序（参数�?
	 * 
	 * @param sh
	 *            可执行文�?
	 * @param shellline
	 *            参数�?
	 * @param isOutput
	 *            是否显示执行结果
	 * @return 该线程的对象
	 */
	private static Process runOneShell(String sh, String shellline, boolean isOutput) {
		Process proc = null;
		try {
//			String shStr = sh + " " + shellline;
			String shStr = shellline;
			shStr = shStr.trim();
			if (!isOutput) {
				Runtime rt = Runtime.getRuntime();
				proc = rt.exec(shStr);
				// System.out.println(shStr);
				// InputStream stream = proc.getInputStream();
				// BufferedReader reader = new BufferedReader(new
				// InputStreamReader(stream));
				// //Parsing the input stream.
				// String line;
				// while ((line = reader.readLine()) != null) {
				// // Pattern pattern = Pattern.compile(applicationToCheck);
				// // Matcher matcher = pattern.matcher(line);
				// // if (matcher.find()) {
				// // applicationIsOk = true;
				// // break;
				// // }
				// System.out.println(line);
				// }

			} else {
				String shellname = "";
				String shellargs = "";
				if (shStr.indexOf(" ") == -1) {
					shellname = shStr;
				} else {
					shellname = shStr.substring(0, shStr.indexOf(" "));
					shellargs = shStr.substring(shStr.indexOf(" ") + 1);
				}
				// Process process; // "/bin/sh","-c",
				// System.out.println("shellname:" + shellname);
				// System.out.println("shellargs:" + shellargs);
				proc = Runtime.getRuntime().exec(new String[] { shellname, shellargs }, null, null);
				// proc.waitFor();
				InputStreamReader ir = new InputStreamReader(proc.getInputStream());
				LineNumberReader input = new LineNumberReader(ir);
				String line;
				proc.waitFor();
				while ((line = input.readLine()) != null) {
					System.out.println(line);
				}
				input.close();
				ir.close();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return proc;

	}

	public static void TASK_INDEXING(Map<String, Object> paramsMap, String name) {
		// TODO Auto-generated method stub
		String sh = "";
		try {
			sh = (String) paramsMap.get("sh");
		} catch (Exception e) {
			// TODO: handle exception
		}

		String shellline = String.valueOf(paramsMap.get("shellline"));
		boolean isOutput = Boolean.valueOf((String) paramsMap.get("isOutput"));
		runOneShell(sh, shellline, isOutput);
	}
	
	public static List<String> GetFileContentList(String filepath) {
		List<String> fileContentList = new ArrayList<String>();
		File file = new File(filepath);
		if (!file.isFile())
			return null;
		
		BufferedReader bReader;
		try {
			bReader = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = bReader.readLine()) != null) {
				fileContentList.add(line);
			}
			bReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fileContentList = null;
		} catch (IOException e) {
			e.printStackTrace();
			fileContentList = null;
		}

		return fileContentList;
	}

}
