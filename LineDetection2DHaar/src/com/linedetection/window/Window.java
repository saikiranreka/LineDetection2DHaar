package com.linedetection.window;

public class Window {
	public Window(int x, int y, double hChange, double vChange,
			double dChange, int id) {
		super();
		this.x = x;
		this.y = y;
		this.hChange = hChange;
		this.vChange = vChange;
		this.dChange = dChange;
		this.id = id;
	}

	public Window() {
		super();
		this.x = 0;
		this.y = 0;
		this.hChange = 0;
		this.vChange = 0;
		this.dChange = 0;
		this.id = 0;
	}

	public int x;
	public int y;
	public double hChange;
	public double vChange;
	public double dChange;
	public int id = 0;

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public double gethChange() {
		return hChange;
	}

	public void sethChange(double hChange) {
		this.hChange = hChange;
	}

	public double getvChange() {
		return vChange;
	}

	public void setvChange(double vChange) {
		this.vChange = vChange;
	}

	public double getdChange() {
		return dChange;
	}

	public void setdChange(double dChange) {
		this.dChange = dChange;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}