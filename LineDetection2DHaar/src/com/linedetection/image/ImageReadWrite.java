/**
 * 
 */
package com.linedetection.image;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.linedetection.window.Window;

/**
 * @author saikiran
 * 
 */

public class ImageReadWrite {

	/**
	 * @param args
	 */
	private static BufferedImage image;
	int window_size=16;

	public double[][] readImage(File file) {
		double array[][] = null;
		try {
			image = ImageIO.read(file);
			ColorConvertOp op = new ColorConvertOp(
					ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
			BufferedImage grayImage = image;
			op.filter(image, grayImage);
			Raster raster = grayImage.getData();
			int w = grayImage.getWidth();
			int h = grayImage.getHeight();
			array = new double[h][w];
			for (int j = 0; j < h; j++) {
				for (int k = 0; k < w; k++) {
					array[j][k] = raster.getSample(j, k, 0);
				}
			}
		} catch (IOException e) {
			System.out.println("Unable to find the image");
		}
		return array;
	}

	public void writeImage(ArrayList<Window[]> points, String name) {
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(Color.RED);
		BasicStroke bs = new BasicStroke(3f);
		graphics.setStroke(bs);
		for (Window[] coord : points) {
			graphics.drawLine(coord[0].getX(), coord[0].getY(),
					coord[1].getX(), coord[1].getY());
		}
		bs = new BasicStroke(0.6f);
		graphics.setStroke(bs);
		graphics.setColor(Color.GREEN);
		for (int i = 0; i < image.getWidth() / window_size; i++) {
			graphics.drawLine(i * window_size, 0, i * window_size, image.getWidth());
			graphics.drawLine(0, i * window_size, image.getHeight(), i * window_size);

		}
		try {
			ImageIO.write(image, "JPEG", new File(name));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public double[][] getSubMatrix(double[][] source, int xStart, int xEnd,
			int yStart, int yEnd) {
		/*
		 * System.out.println("Extracting sub matrix (xStart,yStart):(" + xStart
		 * + "," + yStart + ") (xEnd,yEnd):(" + xEnd + "," + yEnd + ")");
		 */
		double[][] subMatrix = new double[ yEnd - yStart][xEnd - xStart];
		for (int i = 0; i + yStart < yEnd && i + yStart < source.length; i++) {
			for (int j = 0; j + xStart < xEnd && j + xStart < source[0].length; j++) {
				subMatrix[i][j] = source[yStart + i][xStart + j];		
			}
		}
		return subMatrix;
	}

	public void writeTextOntoImage(String text, int x, int y) {
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(Color.BLACK);
		graphics.setFont(new Font("Arial",Font.PLAIN,8));
		graphics.drawString(text, x, y);
	}
	
	public void fillWindow(int x,int y){
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setPaint(Color.BLUE);
		graphics.fillRect(x, y, window_size,window_size);
	}

}
