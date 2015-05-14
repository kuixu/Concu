package cn.xukui.code.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.xukui.code.main.RunConcuShell;

public class Tasker implements Runnable {
	private Map<String, Object> paramsMap;
	private String taskName;
	private int taskId;
	public static ThreadGroup taskGroup = new ThreadGroup("CGDNA");

	public static int TotalTaskCount;

	public final static int TASK_Concurrent = 0;

	public static List<Thread> threadList = new ArrayList<Thread>();
	public static List<String> currentThreadList = new ArrayList<String>();
	public static int currentTaskSetCount;

	public static void main(String[] args) {

		Tasker multiTask = new Tasker();
		int[] a = multiTask.getThreadTask(48, 30);
		for (int i = 0; i < a.length; i++) {
			System.out.println(a[i] + " ");
		}

	}

	public Tasker() {
	}

	public Tasker(Map<String, Object> paramsMap) {
		this.paramsMap = paramsMap;
	}

	public Tasker(Map<String, Object> paramsMap, String taskName, int taskId) {
		this.paramsMap = paramsMap;
		this.taskName = taskName;
		this.taskId = taskId;
	}

	public void start() {
		new Thread(taskGroup, this, this.taskName).start();
	}

	@Override
	public void run() {
		switch (taskId) {

		case TASK_Concurrent: {
			RunConcuShell.TASK_INDEXING(this.paramsMap, Thread.currentThread().getName());
			break;
		}

		default:
			System.out.println("Error: task id " + taskId + " is wrong!");
			break;
		}

	}

	
	public static List<String> getCurrentTasksName() {
		List<String> currentTasksNameList = new ArrayList<String>();
		Map<Thread, StackTraceElement[]> maps = Thread.getAllStackTraces();
		// System.err.println("------------------" + maps.size() +
		// "----------------------------");
		for (Iterator<Thread> it = maps.keySet().iterator(); it.hasNext();) {
			Thread t = it.next();
			// System.out.println("name:" + t.getName() + " ialive:" +
			// t.isAlive() + " count:" + t.activeCount() + " state:" +
			// t.getState() + " :" + t.isInterrupted());
			if (t.getName().equals("Reference Handler") || t.getName().equals("Signal Dispatcher") || t.getName().equals("main") || t.getName().equals("Finalizer")
					|| t.getName().equals("Attach Listener")) {
				continue;
			}
			 System.out.println("name:" + t.getName() + " pid:" + t.getId()
			 + " prop:" + t.getPriority());
			currentTasksNameList.add(t.getName());
		}
		return currentTasksNameList;

	}
	
	public static int  getPsCount(String execStr,String proInfo) {
		int count=0;
		String line;
		// Executable file name of the application to check.
		// final String applicationToCheck = "application.jar"
		//boolean applicationIsOk = false;
		// Running command that will get all the working processes.
		Process proc;
		try {
			// proc =
			// Runtime.getRuntime().exec("tasklist.exe /FI 'imagename eq notepad.exe' /nh");
			proc = Runtime.getRuntime().exec(execStr);
			//System.out.println("PS: " + execStr);
			InputStream stream = proc.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			// Parsing the input stream.
			while ((line = reader.readLine()) != null) {
				//System.out.println(line);
				if (line.contains(proInfo)) {
					count++;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}

	public int[] getThreadTask(int totalTask, int threadNum) {

		int ProcessNum = Runtime.getRuntime().availableProcessors();
		// System.out.println("available Processors Num:" + ProcessNum);

		int useProcessNum = (int) (0.75 * ProcessNum);
		// if (threadNum > useProcessNum) {
		useProcessNum = threadNum;
		// }

		int pre = totalTask / useProcessNum;
		int mod = totalTask % useProcessNum;
		int[] startArr = new int[useProcessNum * 2];
		// int[] endLineArr = new int[useProcessNum];
		// last one thread
		if (mod > 0) {
			startArr = new int[useProcessNum + 2];
			startArr[startArr.length - 2] = pre * useProcessNum;
			startArr[startArr.length - 1] = pre * useProcessNum + mod - 1;
		}
		System.out.println("used Processors Num:" + startArr.length);
		for (int i = 0; i < useProcessNum; i += 2) {
			startArr[i] = i * pre;
			startArr[i + 1] = startArr[i] + pre;
		}
		return startArr;
	}

	// public StartEnd[] getThreadTaskSE(int totalTask, int threadNum) {
	//
	// int ProcessNum = Runtime.getRuntime().availableProcessors();
	// // System.out.println("available Processors Num:" + ProcessNum);
	//
	// int useProcessNum = (int) (0.75 * ProcessNum);
	// // if (threadNum > useProcessNum) {
	// useProcessNum = threadNum;
	// // }
	//
	// int pre = totalTask / useProcessNum;
	// int mod = totalTask % useProcessNum;
	// StartEnd[] seArr = new StartEnd[useProcessNum];
	// // int[] endLineArr = new int[useProcessNum];
	// // last one thread
	// if (mod > 0) {
	// seArr = new StartEnd[useProcessNum + 1];
	// seArr[seArr.length - 1] = new StartEnd(pre * useProcessNum, pre
	// * useProcessNum + mod - 1);
	// }
	// // System.out.println("used Processors Num:" + seArr.length);
	// for (int i = 0; i < useProcessNum; i++) {
	// seArr[i] = new StartEnd(i * pre, (i + 1) * pre);
	// }
	// return seArr;
	// }

	public static void waitTask(String stepinfo) {
		while (taskGroup.activeCount() != 0) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// System.out.println(readMappingTGroup.activeCount()+" : alive");
			System.out.println(stepinfo + " : " + taskGroup.activeCount() + " left");
		}
		System.out.println(stepinfo + " task sets completely finshed!");
		// currentTaskSetCount=0;

	}

}
