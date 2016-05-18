package com.linedetection.detectlines;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.linedetection.config.Configuration;
import com.linedetection.haar.TwoDHaar;
import com.linedetection.image.ImageReadWrite;
import com.linedetection.postprocess.PostProcessLines;
import com.linedetection.window.Window;

public class LineDetection2D {
	private static Logger LOG = Logger.getLogger("LineDetection2D");

	public static void main(String[] args) throws IOException {
		FileHandler fileHandler = new FileHandler("loog.log");
		fileHandler.setFormatter(new SimpleFormatter());
		LOG.addHandler(fileHandler);
		LOG.setLevel(Level.FINE);

		File folder = new File(Configuration.INPUT_PATH);
		LOG.log(Level.FINE, "Reading images from " + Configuration.INPUT_PATH);
		File[] listOfFiles = folder.listFiles();
		ImageReadWrite imageIO = new ImageReadWrite();
		PostProcessLines processLines = new PostProcessLines();
		int index = 1;
		for (File file : listOfFiles) {
			double[][] source = imageIO.readImage(file);
			if (source == null) {
				LOG.log(Level.WARNING, "Reading images from "
						+ Configuration.INPUT_PATH);
				continue;
			}
			int temp = 0;
			ArrayList<Window[]> lines = new ArrayList<Window[]>();
			ArrayList<Window[]> vLines = new ArrayList<Window[]>();
			ArrayList<Window[]> dLines = new ArrayList<Window[]>();
			ArrayList<Window> prevHChanges = new ArrayList<Window>();
			ArrayList<Window> prevVChanges = new ArrayList<Window>();
			ArrayList<Window> prevDChanges = new ArrayList<Window>();
			ArrayList<ArrayList<Window>> result = new ArrayList<ArrayList<Window>>();
			int windowsPerLine = source[0].length / Configuration.WINDOW_SIZE;
			LOG.log(Level.FINE, " Number of Windows per line " + windowsPerLine);
			for (int i = 0; i < source.length ; i += Configuration.WINDOW_SIZE) {
				// sample holds a row of image in form of matrix
				double[][] sample = imageIO.getSubMatrix(source, 0, source[0].length, i, i
						+ Configuration.WINDOW_SIZE);
				System.out.println("rows0 = "+sample.length+" cols0 = "+sample[0].length);
				// call findChang dc count = 1esInRow method to get the windows
				// where changes
				// have been identified in a row.
				result = findChangesInRow(sample, temp, i,
						Configuration.WINDOW_SIZE);
				if (i != 0) {

					// tempH and tempD are 2 array lists holds horizontal and
					// diagonal changes of current row respectively
					ArrayList<Window> currentHChanges = new ArrayList<Window>();
					ArrayList<Window> currentDChanges = new ArrayList<Window>();
					currentHChanges = result.get(0);
					currentDChanges = result.get(2);

					// compare horizontal changes in previous row and current
					// row and add them to vLines array list
					for (Window x : currentHChanges) {
						for (Window y : prevHChanges) {
							if (x.getId() == y.getId()
									+ (source[0].length / Configuration.WINDOW_SIZE)) {
								vLines.add(new Window[] { y, x });
								break;
							}
						}
						continue;
					}

					// compare diagonal changes in previous row and current row
					// and add them to dLines array list

					for (Window prev : prevDChanges) {
						for (Window curr : currentDChanges) {
							if (curr.getId() == prev.getId() + windowsPerLine
									- 1
									|| curr.getId() == prev.getId()
											+ windowsPerLine + 1
									|| curr.getId() == prev.getId()
											+ windowsPerLine - 2
									|| curr.getId() == prev.getId()
											+ windowsPerLine + 2) {
								dLines.add(new Window[] { prev, curr });
								// break;
							}

						}
					}
				}

				// save the current row results, so that they can be used in
				// next iteration for comparison

				prevHChanges = result.get(0);
				prevVChanges = result.get(1);
				prevDChanges = result.get(2);
				temp = result.get(3).get(0).getId();

				// use vChanges to compute horizontal lines in a row.
				if (prevVChanges.size() > 0)
					lines.addAll(processLines.postProcessHLines(prevVChanges));
				System.out.println(i);
			}

			// post process the diagonal and vertical lines and add them to
			// lines arraylist

			lines.addAll(processLines.postProcessVLines(vLines, windowsPerLine));
			lines.addAll(processLines.postProcessDLines(dLines, windowsPerLine));

			// drawLines
			imageIO.writeImage(lines, getOutPutFileName(index));
			index++;
		}
		System.out.println("Done");
	}

	/*
	 * This method takes current row to be processed, rowid, windowSize, id of
	 * last window in previous row. Takes the row, breaks that row to windows of
	 * size specified in windowSize. On each window,
	 * orderedFastHaarWaveletTransformForNumIters is called and result in saved
	 * in change arraylist. It processes the windows and the changes. If the
	 * numbers of changes in each window is greater than or equal to the
	 * threshold, it adds that window to the respective arraylist.
	 */
	public static ArrayList<ArrayList<Window>> findChangesInRow(
			double[][] source, int temp, int row, int windowSize)
			throws FileNotFoundException {
		ImageReadWrite imageIO = new ImageReadWrite();
		ArrayList<Window> hChanges = new ArrayList<Window>();
		ArrayList<Window> vChanges = new ArrayList<Window>();
		ArrayList<Window> dChanges = new ArrayList<Window>();
		ArrayList<ArrayList<Window>> result = new ArrayList<ArrayList<Window>>();
		System.out.println("rows = "+source.length+" cols = "+source[0].length);

		for (int j = 0; j < source[0].length; j += windowSize) {

			// extract a window from the row.
			double[][] sample = imageIO.getSubMatrix(source, j,
					j + windowSize,0, windowSize);
			
			if(temp == 19){
				for(int k=0;k<sample.length;k++){
					for(int l=0;l<sample[0].length;l++){
						System.out.print(sample[k][l]);
					}
					System.out.println();
				}
			}

			// apply 2D haar on the window.
			ArrayList<double[][]> change = TwoDHaar
					.orderedFastHaarWaveletTransformForNumItersAux(sample, 3);
			double[][] tempdc = null, temphc = null, tempvc = null;
			tempdc = change.get(change.size() - 2);
			tempvc = change.get(change.size() - 3);
			temphc = change.get(change.size() - 4);
			int dcCount = 0, hcCount = 0, vcCount = 0;

			// count the number of horizontal, vertical, diagonal changes
			for (int l = 0; l < tempdc.length; l++) {
				for (int m = 0; m < tempdc[0].length; m++) {

					if (Math.abs(tempdc[l][m]) >= Configuration.D_THRESHOLD_LOW
							&& Math.abs(tempdc[l][m]) <= Configuration.D_THRESHOLD_HIGH) {
						// System.out.println("dc change "+Math.abs(tempdc[l][m]));
						dcCount++;
					}
					LOG.fine("Vertical change = " + Math.abs(tempvc[l][m]));
					if (Math.abs(tempvc[l][m]) >= Configuration.V_THRESHOLD_LOW
							&& Math.abs(tempvc[l][m]) <= Configuration.V_THRESHOLD_HIGH) {
						vcCount++;
					}
					if (Math.abs(temphc[l][m]) >= Configuration.H_THRESHOLD_LOW
							&& Math.abs(temphc[l][m]) <= Configuration.H_THRESHOLD_HIGH) {
						hcCount++;
					}
				}
			}

			// if number of diagonal changes exceeds the threshold, add the
			// window to dChanges arraylist
			LOG.log(Level.FINE, "dc count = " + dcCount + " hc Count = "
					+ hcCount + " vcCount = " + vcCount + " for Window id "
					+ temp);
			System.out.println("dc count = " + dcCount + " hc Count = "
					+ hcCount + " vcCount = " + vcCount + " for Window id "
					+ temp);
			// System.out.println(dcCount);
			if (dcCount >= Configuration.D_CHANGE_COUNT) {
				Window w = new Window();
				w.setdChange(dcCount);
				w.setId(temp);
				dChanges.add(w);
				w.setX(row);
				w.setY(j);
				imageIO.fillWindow(row, j);
			}

			// if number of horizontal changes exceeds the threshold, add the
			// window to hChanges arraylist
			if (hcCount >= Configuration.H_CHANGE_COUNT) {
				Window w = new Window();
				w.sethChange(hcCount);
				w.setId(temp);
				w.setX(row);
				w.setY(j);
				imageIO.fillWindow(row, j);
			}

			// if number of vertical changes exceeds the threshold, add the
			// window to vChanges arraylist
			if (vcCount >= Configuration.V_CHANGE_COUNT) {
				Window w = new Window();
				w.setvChange(vcCount);
				w.setId(temp);
				w.setX(row);
				w.setY(j);
				imageIO.fillWindow(row, j);
			}
			imageIO.writeTextOntoImage(String.valueOf(temp),row ,j );
			temp++;
		}

		// add the results of the row to result arraylist in order.
		result.add(hChanges);
		result.add(vChanges);
		result.add(dChanges);
		Window w = new Window();
		ArrayList<Window> t = new ArrayList<Window>();
		w.setId(temp);
		t.add(w);
		result.add(t);
		return result;
	}

	/* this method is used to generate a name to the output image */
	public static String getOutPutFileName(int i) {
		String tt = "/home/saikiran/Desktop/output/"
				+ Configuration.INPUT_FOLDER + "/" + i + "_output.jpg";
		return tt;
	}

}
