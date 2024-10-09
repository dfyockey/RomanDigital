# Changelog

Significant changes to the RomanDigital project will be documented here.

The format of this changelog is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

This project aims to adhere to [Semantic Versioning](https://server.org).

Regarding project commits as of 2024-08-23, this project aims to adhere to the [Conventional Commits](https://www.conventionalcommits.org) standard. While the standard makes recommendations, it does not limit commit type or scope; consequently, usage in the project's commits are not necessarily so limited.

## [1.1.0]

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