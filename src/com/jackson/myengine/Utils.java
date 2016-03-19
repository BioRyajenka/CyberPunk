package com.jackson.myengine;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.BiFunction;

import org.newdawn.slick.UnicodeFont;

public class Utils {
	public enum Direction {
		LEFT, RIGHT, UP, DOWN
	};

	public enum Orientation {
		HORIZONTAL, VERTICAL;
	};

	public static float EPS = (float) 1e-7;
	public static int INF = 1000_000_000;
	public static Random rand = new Random();

	private Utils() {
	}

	public static boolean inBounds(float x, float a, float b) {
		return x >= a && x <= b;
	}

	public static boolean inBounds(int x, int a, int b) {
		return x >= a && x <= b;
	}

	/**
	 * Left-up and right-down
	 */
	public static boolean boundingBox(int ax1, int ay1, int ax2, int ay2, int bx1,
			int by1, int bx2, int by2) {
		if (ax1 > bx1 || (ax1 == bx1 && ax2 > bx2)) {
			int temp = ax1;
			ax1 = bx1;
			bx1 = temp;
			temp = ay1;
			ay1 = by1;
			by1 = temp;
			temp = ax2;
			ax2 = bx2;
			bx2 = temp;
			temp = ay2;
			ay2 = by2;
			by2 = temp;
		}
		if (bx1 <= ax2 && (by1 <= ay2 && by2 >= ay1))
			return true;
		return false;
	}

	public static String floatToString(float f, int k) {
		String fract = String.valueOf(f - (int) f);
		return String.valueOf((int) f) + "." + fract.substring(2, Math.min(fract
				.length(), k + 2));
	}

	public static boolean eq(float a, float b, float eps) {
		return Math.abs(a - b) < eps;
	}

	public static boolean eq(float a, float b) {
		return eq(a, b, EPS);
	}

	public static float dist(float x1, float y1, float x2, float y2) {
		return (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}

	public static int manhattanDist(int x1, int y1, int x2, int y2) {
		return Math.abs(x1 - x2) + Math.abs(y1 - y2);
	}

	/**
	 * validator gets n, m, i, j
	 */
	public static int[][] bfs(int n, int m, int startI, int startJ,
			BiFunction<Integer, Integer, Boolean> validator) {
		int queue[][] = new int[3 * n * m][2], d[][] = new int[n][m], uk1 = 0, uk2 = 0;

		for (int i = 0; i < n; i++)
			for (int j = 0; j < m; j++)
				d[i][j] = -1;
		d[startI][startJ] = 0;

		queue[uk2][0] = startI;
		queue[uk2++][1] = startJ;

		while (uk1 != uk2) {
			int i = queue[uk1][0], j = queue[uk1++][1];
			for (int di = -1; di <= 1; di++)
				for (int dj = -1; dj <= 1; dj++) {
					if ((di == 0 && dj == 0) || (di != 0 && dj != 0))
						continue;
					int ni = i + di, nj = j + dj;
					if (!inBounds(ni, 0, n - 1) || !inBounds(nj, 0, m - 1))
						continue;
					if (validator.apply(ni, nj) && (d[ni][nj] == -1 || d[i][j]
							+ 1 < d[ni][nj])) {
						queue[uk2][0] = ni;
						queue[uk2++][1] = nj;
						d[ni][nj] = d[i][j] + 1;
					}
				}
		}

		return d;
	}

	public static <T> void randomShuffle(ArrayList<T> ar) {
		for (int i = ar.size() - 1; i > 0; i--) {
			int index = rand.nextInt(i + 1);
			// Simple swap
			T a = ar.get(index);
			ar.set(index, ar.get(i));
			ar.set(i, a);
		}
	}

	public static class Pair<T1, T2> {
		public T1 first;
		public T2 second;

		public Pair(T1 first, T2 second) {
			this.first = first;
			this.second = second;
		}
	}

	public static class IntPair extends Pair<Integer, Integer> {
		public IntPair(Integer first, Integer second) {
			super(first, second);
		}
	}

	public static class BresenhamLine {
		private IntPair start, target;

		public BresenhamLine(IntPair start, IntPair target) {
			this.start = start;
			this.target = target;
		}

		public BresenhamLineIterator getIterator() {
			return new BresenhamLineIterator(start.first, start.second, target.first,
					target.second);
		}

		public static class BresenhamLineIterator {
			int x0, y0, x1, y1;
			int deltaX, deltaY;
			int stepX, stepY;
			int error;

			public BresenhamLineIterator(int x0, int y0, int x1, int y1) {
				this.x0 = x0;
				this.y0 = y0;
				this.x1 = x1;
				this.y1 = y1;

				deltaX = Math.abs(x1 - x0);
				deltaY = Math.abs(y1 - y0);

				stepX = (x0 < x1 ? 1 : -1);
				stepY = (y0 < y1 ? 1 : -1);

				error = deltaX - deltaY;
			}

			public boolean hasNext() {
				return !(x0 == x1 && y0 == y1);
			}

			public IntPair next() {
				IntPair point = new IntPair(x0, y0);

				int twoError = 2 * error;
				if (twoError > -deltaY) {
					error -= deltaY;
					x0 += stepX;
				}
				if (twoError < deltaX) {
					error += deltaX;
					y0 += stepY;
				}

				return point;
			}
		}
	}

	private static String[] nameBeginnings = { "Кр", "Ка", "Ра", "Мрок", "Кру", "Рэй",
			"Бре", "Зэд", "Драк", "Мор", "Джа", "Мер", "Джар", "Мжо", "Зорк", "Мэд",
			"Край", "Зур", "Крео", "Азак", "Азур", "Рей", "Кро", "Мар", "Люк" };
	private static String[] nameMiddles = { "аир", "ир", "ми", "сор", "ми", "кло",
			"рэд", "кра", "арк", "мири", "лори", "крес", "мур", "зер", "марак",
			"зоир", "слам", "салм", "урак", "" };
	private static String[] nameEndings = { "д", "ед", "арк", "эс", "ер", "дер",
			"трон", "мэд", "юр", "зур", "крэд", "мур", ""};

	public static String generateRandomName() {
		String name = nameBeginnings[rand.nextInt(nameBeginnings.length)] + nameMiddles[rand
				.nextInt(nameMiddles.length)];
		if (rand.nextFloat() < .6f) {
			name += nameEndings[rand.nextInt(nameEndings.length)];
		}
		for (int i = 1; i < name.length(); i++) {
			if (name.charAt(i) == name.charAt(i - 1)) {
				name = name.substring(0, i) + "'" + name.substring(i); 
			}
		}
		return name;
	}

	/**
	 * Truncates text to look like "Some long desc..."
	 */
	public static String truncate(String text, UnicodeFont font, float width) {
		if (font.getWidth(text) <= width) {
			return text;
		}
		for (int i = 1; i < text.length(); i++) {
			String sub = text.substring(0, i) + "...";
			if (font.getWidth(sub) > width) {
				return sub;
			}
		}
		Log.e("Text truncation failed");
		Log.printStackTrace();
		return null;
	}
}