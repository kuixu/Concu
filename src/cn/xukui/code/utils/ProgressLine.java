package cn.xukui.code.utils;


public class ProgressLine {
	public static double finishPoint=100;
	public static int barLength=50;
	
	
	public static String showBarByPoint(double currentPoint, double finishPoint, int barLength) {
		
		double rate = currentPoint / finishPoint;
		
		int barSign = (int) (rate * barLength);
		
		return String.format(" %.2f%% ", rate * 100)+makeBarBySignAndLength(barSign, barLength);	
	}
	
	public static String showBarByPoint(double currentPoint) {
		double rate = currentPoint / finishPoint;
		int barSign = (int) (rate * barLength);
		return  String.format(" %.2f%%", rate * 100)+makeBarBySignAndLength(barSign, barLength) ;
	
	
	}
	
	private static String makeBarBySignAndLength(int barSign, int barLength) {
		StringBuilder bar = new StringBuilder();
		bar.append("[");
		for (int i=1; i<=barLength; i++) {
			if (i < barSign) {
				bar.append("-");
			} else if (i == barSign) {
				bar.append(">");
			} else {
				bar.append(" ");
			}
		}
		bar.append("]");
		return bar.toString();
	}
	public static void main(String[] args) {
		for (int i = 0; i < 100; i+=10) {
			System.out.println(ProgressLine.showBarByPoint(i, 100, 20));
		}
		
	}
}

