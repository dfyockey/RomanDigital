![Banner image : RomanDigital clock displayed on a phone resting horizontally on a black Lego stand to the left of a Lego Botanicals bonsai tree](/fastlane/metadata/android/en-US/images/featureGraphic.png)

# RomanDigital

Android Digital Clock App and Widget with Roman Numeral Display

[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png"
alt="Get it on F-Droid"
height="80" align="left">](https://f-droid.org/packages/net.diffengine.romandigitalclock/)<img src="/.github/images/clearpixel.gif" height="23" width="30"><br />
<a href="https://f-droid.org/packages/net.diffengine.romandigitalclock/">![F-Droid Version](https://img.shields.io/f-droid/v/net.diffengine.romandigitalclock?label=Latest%20Version)</a><br />
<br />
<br />
[<img src="/.github/images/get-the-latest-apk-on-github.png"
alt="Get the latest apk on GitHub"
height="80" align="left">](https://github.com/dfyockey/RomanDigital/releases/latest)<img src="/.github/images/clearpixel.gif" height="23" width="30"><br />
<a href="https://github.com/dfyockey/RomanDigital/releases/latest">![GitHub Release](https://img.shields.io/github/v/release/dfyockey/RomanDigital?label=Latest%20Version)</a><br />
<br />

## Description

RomanDigital is a digital clock app that displays the current time in
Roman numerals. It can be used to repurpose an old Android device that's
sitting around collecting dust, or to provide a clock of unique style on
a current phone or tablet.

![Landscape screenshot of phone showing RomanDigital app displaying time as VIII:LVII](/.github/images/Screenshot_20240809_205721_RomanDigital.png)

Further, RomanDigital includes a widget that can be added to a device's Home screen.

![Portion of a portrait screenshot of a phone Home page showing RomanDigital widget displaying time as XVII:XV, the screenshot portion having a torn-paper-effect bottom edge](/.github/images/Torn_Screenshot_20240913_171548_One_UI_Home.png)

## Features

RomanDigital includes several common clock app features, including:

* Choice of 12 or 24 hour display
* Centered display
* Option to keep display on when app is in foreground
* Display in either portrait or landscape
* Tap on screen displays app and system controls:

![Landscape screenshot of phone showing system bars and RomanDigital app displaying time as IV:XIV with toolbar containing gear and info icons](/.github/images/Screenshot_20240809_161416_RomanDigital.png)

RomanDigital further includes:

* Choice between centered display and display aligned with a fixed divider
* AM/PM indicator integrated into the time display, such that the divider is displayed as "·" for AM and ":" for PM:

![Landscape screenshot of phone showing RomanDigital app displaying time as XI·XXXIV](/.github/images/Screenshot_20240809_113408_RomanDigital.png)

* Option to only keep display on when device is charging
* Adaptive display providing the largest possible monospace text for the device screen width (excepting a narrow margin)
* Choice of app clock display color by selecting RGB values or by entering a hex code:

![Portrait screenshot of phone cropped to show a Time Color dialog enabling setting of app display color by 6-character hex code or by setting red, green, and blue sliders, and including a clock display preview](/.github/images/Screenshot_20250603_104752_RomanDigital.jpg)

* A widget for providing a Roman digital clock display on a device's Home screen, with display text automatically sized to fit the widget's dimensions:

![Portrait screenshot of phone Home screen showing RomanDigital widget](/.github/images/Screenshot_20240910_174429_One_UI_Home_scaled.jpg)

* Independent setting of widget background transparency, time zone, and other settings:

![Portrait screenshot of a second phone Home screen showing four RomanDigital widgets with center-aligned Continental U.S. time zone times, time zone labels below times, and different transparency backgrounds](/.github/images/Screenshot_20250219_115336_One_UI_Home_scaled.jpg) ![Portrait screenshot of a third phone Home screen showing six RomanDigital widgets with fixed-divider-aligned international location time zone times and time zone labels above times](/.github/images/Screenshot_20250219_120411_One_UI_Home_scaled.jpg)

* And... RomanDigital is Apache-2.0-licensed open source :slightly_smiling_face:

## Requirements

RomanDigital requires Android 5.0 or greater and is designed to run on a phone or tablet.

## Widget Settings

When the widget is added to the Home screen, an activity is displayed to allow selection of
settings. This activity can be accessed later in one of two ways depending on the Android version.
On Android 11 and earlier, a tap on the widget brings up the activity. On Android 12 and later, the
activity is accessed by a long press on the widget and a tap on the settings or reconfigure button
in the normal manner for the particular Android version.

## Permissions

The USE_EXACT_ALARM permission is set by this app. This permission is
necessary on Android 13 and greater to enable the widget to provide an
accurate time display without inconveniencing the user by asking for the permission.
It cannot be disabled without modifying the source code.

The SCHEDULE_EXACT_ALARM permission is also set by this app and is
necessary on Android 12 for the same reason as the USE_EXACT_ALARM
permission discussed above. This permission can be disabled in the
_Alarms & Reminders_ section of the system settings. Disabling this
permission will cause inaccuracy in the widget's time display.

Android versions 11 and lower allow setting of exact alarms by default.

In addition, net.diffengine.romandigitalclock.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION
is set by this app. This was set during the app build process by use of a particular Android
library required to enable the app to function as expected on versions of Android 12 and earlier
(i.e. prior to API 33). It cannot be disabled without modifying the source code. As noted in
an fdroidserver commit, "it's basically just an internal hack, rather than a real permission." For
a more technical explanation of this "permission", see the full commit message at
https://gitlab.com/fdroid/fdroidserver/-/merge_requests/1336/diffs?commit_id=71697f9c88ec73980f63be5955f36cdc3ba7a02c

## Known Issues

As widget updating is currently implemented, text of a widget rotated between portrait and landscape may not be automatically resized until the next widget update, which may be up to a minute after the rotation. This is not an issue for devices that do not support home screen rotation or that are set not to rotate the home screen. It is also not an issue for any device having a UI/launcher rendering the device's home screen on which the widget is installed that notifies widgets when the device is rotated (e.g. Samsung One UI 6.1 and 7.0). 

After building and updating the app in Android Studio, a widget that had previously been added to the home screen may stop updating. It may be 'kickstarted' by simply opening and then closing the widget's settings screen.

The 'Align to Divider' option does not correctly align the display when a variable width font is used.

The RomanDigital app will run on a 5th Generation Amazon Kindle Fire, which is based on Android 5.1, but the widget will not. RomanDigital has not been tested on other Fire versions.

## FAQ (Foremost Anticipated Questions)

> Q: "I just updated RomanDigital / updated Android / restarted my device, and now the widget doesn't work! How do I get it running again?"
>
> A: As of version 2.0.1, this should only happen when running RomanDigital on a device through Android Studio. The workaround to get the widget going again is to "kickstart" it by simply opening and then canceling a settings screen from any widget or from the app.

> Q: "Why does the position of the divider change when 'Align to Divider' is selected? Isn't it supposed to stay in one place?"
>
> A: Yes, it's supposed to stay in one place, but the positioning was designed with the expectation of using a monospace font. Shortsighted of me, I know, but implementation for variable width fonts would be _really hard_. If you've changed a device setting effecting the font used, e.g. your system-wide font, to something that doesn't provide for monospace, then the calculated display position based on expected equal-width characters, and thus the divider position, will unfortunately be off.

> Q: "There's no alarm feature, so why the need to set exact alarms? What's this got to do with an accurate time display?"
> 
> A: Android doesn't enable widgets to receive the ACTION_TIME_TICK intent broadcast through the system each minute. Setting an alarm for the exact time of each next minute at the end of a minute is the only straightforward way (that I know of) for the app's widget to know when to update the time display.

> Q: "Will there ever be an alarm feature?"
> 
> A: Maybe, but it's quite low on the priority list at this point.

> Q: "Why can't I change the font/text color/background color/widget corner curvature/etc?"
> 
> A: App text color can now be changed, as noted in the **Features** above. Also, background _transparency_ can be changed, with background fully white/black for 100% opacity in light/dark mode. As for other stylistic changes, I haven't gotten to them yet.

> Q: "Why no seconds?"
> 
> A: They would crowd the display and look inelegant. Besides, most people (myself included) would likely have trouble reading a lengthy Roman numeral within a second.

> Q: "Why no date?"
> 
> A: Because it's just a simple clock. At least for now.

> Q: "I want to change widget time zone labels to particular place names. Is there any way to do this?"
> 
> A: Not right now, but it should be added pretty soon since I want that option myself and it should be easy to implement.

> Q: "Can I put the widget on my phone's lock screen?"
>
> A: Maybe. I was able to on my Samsung Galaxy A14 5G by purchasing the awkwardly-named [Lockscreen Widgets and Drawer](https://play.google.com/store/apps/details?id=tk.zwander.lockscreenwidgets) app for the low, low price of $1.49. YMMV. Here's what my lock screen looks like:

![Portrait screenshot of phone lock screen showing RomanDigital widget](/.github/images/Screenshot_20241008_143851_One_UI_Home_scaled.jpg)

> Note:
> 
> * I get nothing if you click on the "Lock Screen Widgets and Drawer" link and/or buy the app, and my purchase and use of it are not meant as an endorsement. There may be other such apps that would work as well or better.
> * This doesn't work so well after my Galaxy A14 was updated from Android 14 to 15 and from One UI 6.1 to 7.0. The power management of Android 15 is understood to be more aggressive and apparently stops apps, including the widget on the lock screen, after a much shorter period when the phone is locked. :slightly_frowning_face:

> Q: "Why no version specifically for a watch?"
> 
> A: Because I don't have one to test on. Yet. I'd rather not rely *entirely* on virtual devices for testing.

> Q: "Will it work on a watch or a TV?"
>
> A: It might — haven't tried — but if it did, it would likely result in a time display that's "unoptimized for the given device's screen size." In other words, "too large" or "too small".
