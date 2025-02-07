/*
 * romantime.java
 * - This file is part of the Android app RomanDigital
 *
 * Copyright 2024 David Yockey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package net.diffengine.romandigitalclock;

import java.util.Calendar;
import java.util.TimeZone;

/** @noinspection SpellCheckingInspection*/
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

	public static String now(boolean ampm, boolean ampmSeparator, boolean center, String tzId) {

		// ampm		ampmSeparator
		// T		T 				12 hr / ampm separator
		// T		F 				12 hr / constant separator
		// F		- 				24 hr / constant separator

		/* GET TIME */
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone(tzId));
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
