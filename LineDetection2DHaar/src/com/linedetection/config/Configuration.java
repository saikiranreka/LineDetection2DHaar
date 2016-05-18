package com.linedetection.config;

public interface Configuration {

	public static int H_THRESHOLD_LOW = 50;
	public static int H_THRESHOLD_HIGH = 200;
	public static int V_THRESHOLD_LOW = 30;
	public static int V_THRESHOLD_HIGH = 220;
	public static int D_THRESHOLD_LOW = 40;
	public static int D_THRESHOLD_HIGH = 210;
	public static int ITERATIONS = 2;
	public static int WINDOW_SIZE = 16;
	public static int WINDOW_SIZE_POWER_OF_2 = 3;
	public static int D_CHANGE_COUNT = 1;
	public static int V_CHANGE_COUNT = 1;
	public static int H_CHANGE_COUNT = 2;
	public static String INPUT_ROOT = "/home/saikiran/Desktop/sampleimages/";
	//public static String INPUT_FOLDER = "All";
	 public static String INPUT_FOLDER="DiagonalLines";
	 //public static String INPUT_FOLDER="HorizontalLines";
	// public static String INPUT_FOLDER="VerticalLines";
	 //public static String INPUT_FOLDER="Mixed";
	public static String INPUT_PATH = INPUT_ROOT + INPUT_FOLDER;

}
