# RomanDigital

Android Digital Clock App with Roman Numeral Display

## Description

RomanDigital is a clock app for displaying time in Roman rather than Arabic numerals.
Its first purpose is to provide an app to use in repurposing an old Android device otherwise sitting
around collecting dust to serve as a wall or desk clock. A broader purpose is to provide a simple
clock of a style different from the common analog Arabic, analog Roman, and digital Arabic styles,
and thereby perhaps help to popularize that style.

![Landscape screenshot of phone showing RomanDigital app displaying time as VIII:LVII](/.github/images/Screenshot_20240809_205721_RomanDigital.jpg)

## Features

RomanDigital includes several common clock app features, including:
* Choice of 12 or 24 hour display
* 12 hour display option to include indication of AM/PM
* Centered display
* Option to keep display on (when app is in foreground) and related option to do so only when charging
* Display in either portrait or landscape
* Tap on screen displays app and system controls:

![Landscape screenshot of phone showing system bars and RomanDigital app displaying time as IV:XIV with toolbar containing gear and info icons](/.github/images/Screenshot_20240809_161416_RomanDigital.jpg)

RomanDigital further includes features believed to be unique over other Roman digital clock apps
known to exist or to have existed, including:
* Choice between centered display and display aligned with a fixed separator
* AM/PM indicator integrated into the time display, such that the separator is displayed as "·" for AM and ":" for PM:

![Landscape screenshot of phone showing RomanDigital app displaying time as XI·XXXIV](/.github/images/Screenshot_20240809_113408_RomanDigital.jpg)

* Display in the largest possible text for the device screen width (excepting a narrow margin) regardless of whether display is centered or fixed
* A widget for providing a Roman digital clock display on a device's Home screen:

![Portrait screenshot of phone Home screen showing RomanDigital widget](/.github/images/Screenshot_20240807_133709_One_UI_Home.jpg)

* And... It's open source :)

## Requirements

RomanDigital requires Android 5.0 or greater and is designed to run on a phone or tablet. Running on
a laptop, desktop pc, TV, or watch will likely result in a time display that's "unoptimized for the
given device's screen size"; in other words, "too small" or "too large".

When the widget is installed on the Home screen of a device running Android 14 or greater, the app
will request permission to set exact alarms. In order for the widget to be able to provide accurate
time on such a device, the user needs to grant this permission. Otherwise, the widget will still
work, but the time it displays will lag the system clock by several seconds.

## Known Device Limitations

The RomanDigital app will run on a 5th Generation Amazon Kindle Fire, which is based on Android 5.1,
but the widget will not. It seems that the Fire's modifications to Android prevent installation of
*any* app widget. This was confirmed using both the Fire's own launcher and
[Nova Launcher](https://novalauncher.com/) (free version). RomanDigital has not been tested on other
Fire versions.

## FAQ (Foremost Anticipated Questions)

> Q: "There's no alarm feature, so why the need to set exact alarms?"
> 
> A: The Android API doesn't enable widgets to receive the ACTION_TIME_TICK intent broadcast thru the system each minute. Setting an alarm for the exact time of each next minute is the only straightforward way (that I know of) for a widget to get a tick each minute. 

> Q: "Will there ever be an alarm feature?"
> 
> A: Maybe, but it's not high on the priority list at this point.  

> Q: (Android 14 or greater) "Why is the exact alarm permission request so awkward? Why not just have a switch right on the Settings screen?"
> 
> A: Because of the way Google set it up. They the user to go to the system's permission setting screen and switch the switch there themselves. A permission to use exact alarms could be set without asking for user permission, but at the risk of an app store blocking or removing the app if they think the permission is being misused. 

> Q: "Why can't I change the font/text color/background color/widget background transparency/etc?"
> 
> A: Haven't gotten to them yet.

> Q: "Why no seconds?"
> 
> A: They would crowd the display and look inelegant. Besides, most people (myself included) would likely have trouble reading a lengthy Roman numeral within a second.

> Q: "Why no date?"
> 
> A: Because it's just a simple clock. At least for now. 

> Q: "Why no version for a watch?"
> 
> A: Because I don't have one to test on. Yet. I'd rather not rely *entirely* on virtual devices for testing.

> Q: "Why Java? Why not Kotlin?"
> 
> A: Because I haven't taken up Kotlin yet. Figured I'd learn a bit about the Android API first.

> Q: "This is a fairly new repository, so why is the main branch called 'master' rather than 'main'?"
> 
> A: Because I created the repository locally rather than on GitHub and forgot that client-side git hadn't yet changed the default main branch name from 'master' to 'main'. I should really change it soon because it looks *sooooo* 20th Century.

> Q: "XIII:XXXVII... 13:37... ;)  Was that intentional?"
> 
> A: Nope. I realized just as I was taking the screenshot. Leet of you to notice. Only a total dweeb would deliberately do something like that, right?