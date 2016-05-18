package com.linedetection.postprocess;

import java.util.ArrayList;

import com.linedetection.window.Window;

public class PostProcessLines {
	public ArrayList<Window[]> postProcessVLines(ArrayList<Window[]> lines,
			int num) {
		boolean[] visited = new boolean[lines.size()];
		ArrayList<Window[]> linesProcessed = new ArrayList<>();
		for (int i = 0; i < lines.size(); i++) {
			if (!visited[i]) {
				visited[i] = true;
				Window[] temp = lines.get(i);
				for (int j = 0; j < lines.size(); j++) {
					Window[] tt = lines.get(j);
					if ((temp[1].id == tt[1].id
							|| temp[1].id + 2 * num == tt[0].id || temp[1].id
							+ 3 * num == tt[0].id)
							&& !visited[j]) {
						temp[1] = tt[1];
						visited[j] = true;
					}
				}
				linesProcessed.add(temp);
			}
		}
System.out.println("vertical lines "+linesProcessed.size());
		return linesProcessed;
	}

	/*
	 * This method takes the arraylist of windows where diagonal changes have
	 * been identified and returns diagonal lines in form of arraylist of array
	 * of windows
	 */
	public ArrayList<Window[]> postProcessDLines(ArrayList<Window[]> lines,
			int num) {
		System.out.println("number of dlines before= " + lines.size());
		/*
		 * boolean[] visited = new boolean[lines.size()]; ArrayList<Window[]>
		 * linesProcessed = new ArrayList<>(); for (int i = 0; i < lines.size();
		 * i++) { if (!visited[i]) { visited[i] = true; Window[] temp =
		 * lines.get(i); for (int j = i + 1; j < lines.size(); j++) { Window[]
		 * tt = lines.get(j); if (temp[1].id == tt[0].id + num - 1 || temp[1].id
		 * == tt[0].id + num + 1 && !visited[j]) { temp[1] = tt[1]; visited[j] =
		 * true; } } linesProcessed.add(temp); } } return linesProcessed;
		 */
		boolean[] visited = new boolean[lines.size()];
		ArrayList<Window[]> linesProcessed = new ArrayList<>();
		for (int i = 0; i < lines.size(); i++) {
			if (!visited[i]) {
				visited[i] = true;
				Window[] line1 = lines.get(i);
				for (int j = i + 1; j < lines.size(); j++) {
					Window[] line2= lines.get(j);
					if (!visited[j]&&line1[1].getId()==line2[0].getId()) {
						visited[j]=true;
						line1[1]=line2[1];
					}
				}
				linesProcessed.add(line1);
			}
			
		}
		System.out.println("diagonal lines "+linesProcessed.size());
		return linesProcessed;
	}

	/*
	 * This method takes the arraylist of windows where vertical changes have
	 * been identified and returns horizontal lines in form of arraylist of
	 * array of windows
	 */

	public ArrayList<Window[]> postProcessHLines(ArrayList<Window> ch) {
		int group = 1;
		ArrayList<Window[]> lines = new ArrayList<Window[]>();
		int[] groups = new int[ch.size()];
		for (int i = 0; i < ch.size() - 1; i++) {
			Window w = ch.get(i);
			if (i == 0) {
				groups[i] = group;
			} else if (w.getId() == ch.get(i - 1).getId() + 1
					|| w.getId() == ch.get(i - 1).getId() + 2) {
				groups[i] = group;
			} else {
				groups[i] = ++group;
			}
		}
		for (int i = 0; i < ch.size(); i++) {
			if (i == 0) {
				Window[] t = new Window[2];
				t[0] = ch.get(0);
				t[1] = t[0];
				lines.add(t);
			} else if (groups[i] == groups[i - 1]) {
				int index = lines.size() - 1;
				Window[] t = lines.remove(index);
				t[1] = ch.get(i);
				lines.add(t);
			} else {
				Window[] t = new Window[2];
				t[0] = ch.get(0);
				t[1] = t[0];
				lines.add(t);
			}
		}
		return lines;
	}
}
