# Reddit Media Grabber

<img src="https://raw.githubusercontent.com/Georodin/RedditMediaGrabber/main/logo/Grabber.png" align="right"
     alt="Reddit Media Grabber logo" width="180" height="180">

Reddit Media Grabber is a Java 11 tool to download and save various media subreddits. It is based on Java and SQL to download the media and
comes with a minimal HTML and PHP based web viewer as well.

<img src="https://raw.githubusercontent.com/Georodin/RedditMediaGrabber/main/logo/interface.png"
     alt="Reddit Media Grabber interface" width="540" height="492">

## Requirements

* Java 11
* XAMPP to run a local SQL and PHP server

## How to install

* Download and install [Java 11](https://adoptopenjdk.net/releases.html?variant=openjdk11&jvmVariant=hotspot) or newer
* Download and install [XAMPP](https://www.apachefriends.org/index.html)
* Download the [latest release here](https://github.com/Georodin/RedditMediaGrabber/releases/latest)

## How to use

Start XAMPP and start within XAMPP the Apache module for the Viewer. 
Then start the MySQL module for the Grabber and the Viewer. 
This will provide a PHP server to the Viewer and a SQL server to the Grabber and Viewer. 

Run the RedditMediaGrabber.exe and go to "Options" -> "Set XAMPP path" -> find and select the XAMPP root path -> press "Deploy". This will move the necessary Viewer files
to the XAMPP directory. Now you can open the Viewer via "Options" -> "Open Viewer" or by visiting the [local page](http://localhost/redditgrabber) here.

Press "change Path" to select the storage directory of the downloaded media files. You will need to restart the XAMPP Apache module every time you change the media path.

Now add your first subreddit or reddit user by clicking the "+" button. You will just need the name and not the whole URL of the subreddit or user.
When you select a user, make sure to tick the user checkbox.

If you are adding a subreddit, the new button will grab new post, while leaving it unchecked will only download the "Hot" section.

When you added a subreddit or user, then you can press "Start" and the Reddit Media Grabber will grab all media from the user or subreddit and save it to your disk.

Now you can also reload the [local Viewer page](http://localhost/redditgrabber). 

## Log file

Next to .exe or .jar of the Grabber a log.txt will be created. It will protocol common incompatibilities and errors.
The implemented LogUtility is not perfect and you will need to run the .jar file via the command line to see the stacktrace of a serious error or bug.

## Grabber Dependencies 

* json-20210307.jar (to parse JSON strings)
* jsoup-1.13.1.jar (to parse HTML strings)
* flatlaf-1.2.jar (software visual theme)
* flatlaf-intellij-themes-1.2.jar (software visual theme)
* jcodec-0.2.5.jar (create preview thumbnails from MP4 files)
* jcodecjavase-0.2.5.jar (create preview thumbnails from MP4 files)
* mysql-connector-j-8.0.31.jar (connect to XAMPP SQL Server)

## Known Issues

* deprecated code comments
* missing compatibility for some media hosts 
* not tested for Unix or Mac systems
* unclean PHP Viewer code
* unclean MVC Java implementation
* unclean Java class, field and method setups
