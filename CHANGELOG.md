# Changelog

Significant changes to the RomanDigital project will be documented here.

The format of this changelog is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

This project aims to adhere to [Semantic Versioning](https://server.org).

Regarding project commits: As of 2024-08-23, this project aims to adhere to the [Conventional Commits](https://www.conventionalcommits.org) standard. While the standard makes recommendations, it does not limit commit type or scope; consequently, neither type nor scope is limited to those recommendations in the project commits.

## [2.0.1] - 2025-03-07

### Fixed

* Failure of widgets to restart at device boot.

## [2.0.0] - 2025-02-20

### Added

* Ability to configure settings independently for app and for each of one or more widgets. Settings are distinguished by a postfix widget id added to preference keys corresponding to a given widget, and by app settings having no postfix. __[This addition may require the User to reset settings for any widgets currently in use.]__
* Ability to select a different time zone for each widget.
* Selectable widget layouts enabling addition of a time zone label above or below the time display.
* A custom preference that has no function except to provide a separator line in the arrangement of preferences on a settings activity.
* The id of the widget currently being configured to the widget settings activity title when the project is built for debugging.
* Commit type 'cleanup' for chores limited to removal of unused files, unused code, unneeded comments (or portions thereof), and/or superfluous whitespace.

### Changed

* XML implementation of time and opacity preferences to code implementation to enable programmatic change of preference keys so the same hierarchy could be used to independently set different preference values for app and widget.
* Tick intent (action MINUTE_TICK) so as to eliminate its unnecessary duplication.
* Frequency of updating widget features by determining when to update such features based on the action of a received intent, especially preventing these features from being updated with every receipt of a MINUTE_TICK intent.

### Deprecated

* The back arrow on the about activity, to be replaced with an "Ok" or similarly labeled button in the style of the buttons now provided on settings activities.

### Removed

* The back arrow from the title bar of each settings activity.

### Fixed

* Issues with widget time display text size being incorrect for the width of the widget (3 or >4 tiles wide) by adding setting of text size to be updated along with other widget features.

## [1.3.0] - 2024-11-18

### Added

* Variable widget background opacity, with opacity being selectable from 0% to 100% by tens.
* A slider — technically, a SeekBarPreference — to the widget settings activity so as to enable the user to select the desired opacity.
* Automatic setting of widget text color in light mode to white (#FAFAFA) for opacity < 50% and to black (#040404) for opacity >= 50% to improve contrast for very white backgrounds. Text color is set to white (#FAFAFA) in dark mode regardless of background opacity.
* Intent SETTINGS_KICK and modifications to the widget so SETTINGS_KICK is treated identically to MINUTE_TICK with respect to time updating and additionally used to enable update of background opacity when changed by the user or update is initiated by the widget itself.

### Changed

* Widget corners for all Android versions to the same degree of curvature (8dp radius).
* The broadcast of a kickstart intent in the widget config activity onPause method and calls to update the widget in the widget itself to include SETTINGS_KICK instead of MINUTE_TICK. 

## [1.2.0] - 2024-10-29

### Added

* `Cancel` and `Save` buttons to both widget and app settings activities (addresses Issue #12).

## [1.1.2] - 2024-10-19

### Fixed

* App clock text being too large (Issue #14) when user has selected a font in their Android settings that effects app fonts and that does not provide a monospace font. (Note: While this fix enables use of variable-width fonts, the "Align to Divider" option functionality does not work correctly for such fonts.)

## [1.1.1] - 2024-10-15

### Changed

* Location of versionName definition from strings.xml to app/build.gradle.kts; this facilitates building with systems (e.g. F-Droid) for which definition from a string reference is problematic.
* AboutActivity to fetch versionName for display from app/build.gradle.kts rather than from a string reference.
* Fastlane full_description to improve readability of reference to the README.md file.

## [1.1.0] - 2024-10-09

### Added

* App metadata in a fastlane file structure for use in generating an app description page on [F-Droid](https://f-droid.org)
* distributionSha256Sum value in gradle-wrapper.properties matching the SHA256SUM of file gradle-8.2-bin.zip referred to by the distributionUrl (see https://gradle.org/release-checksums/) to improve app security
* FUNDING.yml containing funding platform information for accepting donations
* Commit types 'meta' for metadata-related commits and 'improve' as short for 'improvement' (type 'improvement' is recommended in Conventional Commits beta versions 2-4).
* This CHANGELOG file

### Changed

* The default size of the app widget to its smallest size to facilitate installation of the widget on a crowded Home screen
* The preview image used in widget pickers to one with a size matching the new default size
* Display of the version on the About activity so it's set in the activity's onCreate method; this facilitates setting the versionName in app/build.gradle.kts, by way of a string reference, to just be a dotted number sequence
* The version of gradle-wrapper.jar to match the version of gradle-8.2-bin.zip (i.e. 8.2) to improve app security
* The README file

### Refactored

* Deleted unneeded and/or unused matter from AndroidManifest.xml

## [1.0.0] - 2024-09-13

### Added

* Everything. This is the first release.