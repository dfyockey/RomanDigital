# RomanDigital

Android Digital Clock App with Roman Numeral Display

## Description

RomanDigital is a digital clock app that displays the current time in
Roman numerals. It can be used to repurpose an old Android device that's
sitting around collecting dust, or to provide a clock of unique style on
a current phone or tablet.

![Landscape screenshot of phone showing RomanDigital app displaying time as VIII:LVII](/.github/images/Screenshot_20240809_205721_RomanDigital.jpg)

## Features

RomanDigital includes several common clock app features, including:

* Choice of 12 or 24 hour display
* Centered display
* Option to keep display on when app is in foreground
* Display in either portrait or landscape
* Tap on screen displays app and system controls:

![Landscape screenshot of phone showing system bars and RomanDigital app displaying time as IV:XIV with toolbar containing gear and info icons](/.github/images/Screenshot_20240809_161416_RomanDigital.jpg)

RomanDigital further includes features believed to be unique over other
known Roman digital clock Android apps, including:

* Choice between centered display and display aligned with a fixed separator
* AM/PM indicator integrated into the time display, such that the separator is displayed as "·" for AM and ":" for PM:

![Landscape screenshot of phone showing RomanDigital app displaying time as XI·XXXIV](/.github/images/Screenshot_20240809_113408_RomanDigital.jpg)

* Option to only keep display on when device is charging
* Adaptive display providing the largest possible monospace text for the device screen width (excepting a narrow margin)
* A widget for providing a Roman digital clock display on a device's Home screen:

![Portrait screenshot of phone Home screen showing RomanDigital widget](/.github/images/Screenshot_20240910_174429_One_UI_Home_scaled.jpg)

* And... RomanDigital is Apache-2.0-licensed open source :slightly_smiling_face:

## Requirements

RomanDigital requires Android 5.0 or greater and is designed to run on a phone or tablet.

## Widget Settings

When the widget is added to the Home screen, an activity is provided to allow selection of
settings. This activity can be accessed later in one of two ways depending on the Android version.
On Android 11 and earlier, a tap on the widget brings up the activity. On Android 12 and later, the
activity is accessed by a long press on the widget and a tap on the settings or reconfigure button
in the normal manner for the particular Android version.

## Permissions

The USE_EXACT_ALARM permission is set by this app. This permission is
necessary on Android 13 and greater to enable the widget to provide an
accurate time display. The permission cannot be disabled without modifying the source code.

The SCHEDULE_EXACT_ALARM permission is also set by this app and is
necessary on Android 12 for the same reason as the USE_EXACT_ALARM
permission discussed above. This permission can be disable in the
_Alarms & Reminders_ section of the system settings. Disabling this
permission will cause inaccuracy in the widget's time display.

## Known Issues

After updating the app, e.g. from compiled source in Android Studio, a widget that had previously been added to the home screen may stop updating. It may be 'kickstarted' by simply opening and then closing the widget's settings screen.

The RomanDigital app will run on a 5th Generation Amazon Kindle Fire, which is based on Android 5.1, but the widget will not. RomanDigital has not been tested on other Fire versions.

## FAQ (Foremost Anticipated Questions)

> Q: "There's no alarm feature, so why the need to set exact alarms? What's this got to do with an accurate time display?"
> 
> A: Android doesn't enable widgets to receive the ACTION_TIME_TICK intent broadcast through the system each minute. Setting an alarm for the exact time of each next minute at the end of a minute is the only straightforward way (that I know of) for the app's widget to know when to update the time display.

> Q: "Will there ever be an alarm feature?"
> 
> A: Maybe, but it's quite low on the priority list at this point.  

> Q: "Why can't I change the font/text color/background color/widget background transparency/etc?"
> 
> A: Haven't gotten to them yet. But, as opposed to an alarm feature, adjustable widget background transparency in particular is *very* high on the priority list.

> Q: "Why no seconds?"
> 
> A: They would crowd the display and look inelegant. Besides, most people (myself included) would likely have trouble reading a lengthy Roman numeral within a second.

> Q: "Why no date?"
> 
> A: Because it's just a simple clock. At least for now.

> Q: "Can I put the widget on my phone's lock screen?"
>
> A: Maybe. I was able to on my Samsung Galaxy A14 5G by purchasing the awkwardly-named [Lockscreen Widgets and Drawer](https://play.google.com/store/apps/details?id=tk.zwander.lockscreenwidgets) app for the low, low price of $1.49. YMMV. Here's what my lock screen looks like:

![Portrait screenshot of phone lock screen showing RomanDigital widget](/.github/images/Screenshot_20240910_174514_One_UI_Home_scaled.jpg)

> Notes:
>
> * I get nothing if you click on the "Lock Screen Widgets and Drawer" link and/or buy the app, and my purchase and use of it is not meant as an endorsement; there may be other such apps that would work as well or better.
>
> * The image on the lock screen is from the Valve Corporation game "Portal". My personal use of the image is not in any way an endorsement of my app by Valve Corporation. But it is an endorsement by me of "Portal" and it's sequal "Portal 2" as being great games :slightly_smiling_face:

> Q: "Why no version specifically for a watch?"
> 
> A: Because I don't have one to test on. Yet. I'd rather not rely *entirely* on virtual devices for testing.

> Q: "Will it work on a watch or a TV?"
>
> A: It might — haven't tried — but if it did, it would likely result in a time display that's "unoptimized for the given device's screen size." In other words, "too large" or "too small".

> Q: "Can the app and widgets be set to different time zones?"
>
> A: Not at this point because only one global set of preferences is used. But providing independent preferences for the app and each widget is something I'd like to get to pretty soon.
