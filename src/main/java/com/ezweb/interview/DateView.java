package com.ezweb.interview;

import java.time.LocalDate;

/**
 * DateUtils
 *
 * @author zuodengpeng
 * @version 1.0.0
 * @date 2018/4/24
 */
public class DateView {
	private static int str2Int(String f) {
		int v = 0;
		int c = 1;
		if (f.charAt(0) == '-') {
			c = -1;
		}
		for (int j = 0; j < f.length(); ++j) {
			int x = f.charAt(j) - '0';
			v = v * 10 + x;
		}
		return c < 0 ? -v : v;
	}

	private static boolean isLeap(int year) {
		return (year % 400) == 0 || ((year % 4) == 0 && (year % 100 > 0));
	}

	private static int[] DAYS1 = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	private static int[] DAYS2 = new int[]{31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

	public static String add(String strDate, String strDays) {
		int year = str2Int(strDate.substring(0, 4));
		int month = str2Int(strDate.substring(4, 6));
		int day = str2Int(strDate.substring(6));
		int days = str2Int(strDays);

		int[] cur_days = isLeap(year) ? DAYS2 : DAYS1;
		if ((day + days) > cur_days[month - 1]) {
			// 翻到下个月1号
			int c = cur_days[month - 1] - day + 1;
			days -= c;
			++month;
			day = 1;
			if (month > 12) {
				++year;
				month = 1;
			}

			// 开始减
			while (days > 0) {
				cur_days = isLeap(year) ? DAYS2 : DAYS1;
				if (days > cur_days[month - 1]) {
					days -= cur_days[month - 1];
					day = 1;
					++month;
					if (month > 12) {
						++year;
						month = 1;
					}
				} else {
					day += days;
					if (day > cur_days[month - 1]) {
						day = 1;
						++month;
						if (month > 12) {
							++year;
							month = 1;
						}
					}
					break;
				}
			}
		} else {
			day += days;
		}
		return String.format("%d-%02d-%02d", year, month, day);
	}

	public static void main(String[] args) {
		for (int i = 0; i < 200 * 365; ++i) {
			String f = add("19790917", String.valueOf(i));
			String v = LocalDate.of(1979, 9, 17).minusDays(-i).toString();
			System.out.println("f = " + f + "-->  v = " + v);
		}
	}
}
