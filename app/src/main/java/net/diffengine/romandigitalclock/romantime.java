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

	private static String getHours (Calendar cal, boolean ampm) {
		int h = (ampm) ? cal.get(Calendar.HOUR) : cal.get(Calendar.HOUR_OF_DAY);
		return  (ampm) ? itor( (h>0)?h:12 ) : itor(h);
	}

	/** @noinspection ReassignedVariable*/
	private static String getSeparator (Calendar cal, boolean ampm, boolean ampmSeparator) {
		String separator = ":";
		if (ampm) {
			separator = ( (ampmSeparator && (cal.get(Calendar.AM_PM) == Calendar.AM) ) ? "·" : ":");
		}
		return separator;
	}

	public static String now(boolean ampm, boolean ampmSeparator, boolean center) {

		// ampm		ampmSeparator
		// T		T 				12 hr / ampm separator
		// T		F 				12 hr / constant separator
		// F		- 				24 hr / constant separator

		/* GET TIME */
		Calendar cal = Calendar.getInstance();
		String rHours	 = getHours(cal, ampm);
		String rMinutes  = itor(cal.get(Calendar.MINUTE));
		String separator = getSeparator(cal, ampm, ampmSeparator);
		//noinspection ReassignedVariable
		String rtime     = rHours + separator + rMinutes;

		if (!center) {
			/* ADD PADDING */
			// Each `NBSP` in the following is a "U+00A0 NO-BREAK SPACE".
			// These are treated by a TextView as visible chars rather than whitespace.
			//
			// Note also that four padding spaces are needed for the ampm (i.e. 12-hour) format,
			// even though none will then be used for VIII o'clock, because the substring method to
			// retrieve the appropriate length of padding will throw an exception due to reading
			// past the end of the string if only three are used. As is, it returns "" for VIII.
			String lpad = ("    " + ((ampm) ? "" : " ")).substring(rHours.length());
			String rpad = "       ".substring(rMinutes.length());
			rtime = lpad + rtime + rpad;
		}

		return rtime;
	}
}
