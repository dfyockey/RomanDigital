package net.diffengine.romandigitalclock;

import java.util.Calendar;

public class romantime {
	private static String itor(int i) {
		String[] R = {
			"","I","II","III","IV","V","VI","VII","VIII","IX",
			"","X","XX","XXX","XL","L"
		};
		return R[(i/10)+10] + R[i%10];
	}

	public static String now(boolean ampmSeperator) {
		Calendar cal = Calendar.getInstance();
		int h = cal.get(Calendar.HOUR);
		String rHours	= itor( (h>0)?h:12 );
		String rMinutes = itor(cal.get(Calendar.MINUTE));
		String ampm     = ( (ampmSeperator && (cal.get(Calendar.AM_PM) == Calendar.AM) ) ? "·" : ":");

		// Each `NBSP` in the following is a "U+00A0 NO-BREAK SPACE".
		// These are used so as to be treated by a TextView as visible chars rather than whitespace.
		String lpad = "     ".substring(rHours.length());
		String rpad = "        ".substring(rMinutes.length());

		return lpad + rHours + ampm + rMinutes + rpad;
	}
}
