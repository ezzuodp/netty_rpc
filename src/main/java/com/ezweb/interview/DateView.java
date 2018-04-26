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

	private static int monthDays(int year, int month) {
		if (month > 7) {
			return month % 2 == 1 ? 30 : 31;
		} else {
			return month % 2 == 1 ? 31 : ((month == 2) ? (isLeap(year) ? 29 : 28) : 30);
		}
	}

	public static String add(String strDate, String strDays) {
		int year = str2Int(strDate.substring(0, 4));
		int month = str2Int(strDate.substring(4, 6));
		int day = str2Int(strDate.substring(6));
		int days = str2Int(strDays);

		if ((day + days) > monthDays(year, month)) {
			// 翻到下个月1号
			int c = monthDays(year, month) - day + 1;
			days -= c;
			++month;
			day = 1;
			if (month > 12) {
				++year;
				month = 1;
			}

			// 开始减
			while (days > 0) {
				int mdays = monthDays(year, month);
				if (days > mdays) {
					days -= mdays;
					day = 1;
					++month;
					if (month > 12) {
						++year;
						month = 1;
					}
				} else {
					day += days;
					days = 0;
					if (day > mdays) {
						day = 1;
						++month;
						if (month > 12) {
							++year;
							month = 1;
						}
					}
				}
			}
		} else {
			day += days;
		}
		return String.format("%d-%02d-%02d", year, month, day);
	}

	public static void main(String[] args) {
		for (int i = 0; i < 20 * 365; ++i) {
			String f = add("19790917", String.valueOf(i));
			String v = LocalDate.of(1979, 9, 17).plusDays(i).toString();
			System.out.printf("%04d : f = %s -->  v = %s \n", i, f, v);
		}
	}
}
