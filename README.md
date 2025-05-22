# FlexiLogger

Android Logging helper to provide flexible Log methods.

[![Release](https://jitpack.io/v/projectdelta6/FlexiLogger.svg)](https://jitpack.io/#projectdelta6/FlexiLogger)

https://jitpack.io/#projectdelta6/FlexiLogger

Add it to your `build.gradle.kts` with:
```gradle.kts
dependencyResolutionManagement {
	repositories {
		...
		maven {
			url = uri("https://jitpack.io")
		}
	}
}
```
or in your `settings.gradle.kts` with:
```gradle
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```

and: in your module with version catalog:

`[versions]`
```toml
flexiLoggerVersion = "Tag"
```

`[libraries]`
```toml
#FlexiLogger
flexiLogger = { group = "com.github.projectdelta6", name = "FlexiLogger", version.ref = "flexiLoggerVersion" }
flexiLogger-httpLogger = { group = "com.github.projectdelta6.FlexiLogger", name = "FlexiHttpLogger", version.ref = "flexiLoggerVersion" }
```

`build.gradle.kts`
```gradle.kts
dependencies {
    //FlexiLogger
    implementation(libs.flexiLogger)
    implementation(libs.flexiLogger.httpLogger)
}
```

or without version catalog:

`build.gradle.kts`
```gradle.kts
dependencies {
    //FlexiLogger
    implementation("com.github.projectdelta6:FlexiLogger:Tag")
    implementation("com.github.projectdelta6:FlexiHttpLogger:Tag")
}
```
# FlexiLogger
## Setup
Create a class that extends FlexiLog.  I have found it is best to create this class as a kotlin object called Log at the root of your project. 
```kotlin
package com.example

import com.duck.flexilogger.FlexiLog
import com.duck.flexilogger.LogType

object Log: FlexiLog() {

	override fun canLogToConsole(type: LogType): Boolean {
        	// Decide when to allow loggint to console, Returning true here will pass the Log through to android.util.Log
		// eg: return BuildConfig.DEBUG
	}

	override fun shouldReport(type: LogType): Boolean {
		//Decide what types of Logs you want to have roported to your reporting system, returning true here will call one of the report(...) methods
	}

	override fun report(type: LogType, tag: String, msg: String) {
		//Implement reporting to your reporting system eg Sentry
		/*
		 * Sentry(v4.3.2) reporting example:
		Sentry.captureEvent(SentryEvent().apply {
		   	message = Message().apply { message = msg }
		    	level = getLevel(type)
		    	logger = tag
		})
		*/
	}

	override fun report(type: LogType, tag: String, msg: String, tr: Throwable) {
		//Implement reporting to your reporting system eg Sentry
		/*
		 * Sentry(v4.3.2) reporting example:
		Sentry.captureEvent(SentryEvent().apply {
		   	message = Message().apply { message = msg }
		    	level = getLevel(type)
		    	logger = tag
		    	throwable = tr
		})
		*/
	}
    
    	/*
    	 * method for Sentry reporting example:
    	@Contract(pure = true)
    	private fun getLevel(type: LogType): SentryLevel {
	    	return when (type) {
	    		LogType.E -> SentryLevel.ERROR
	    		LogType.D, LogType.V, LogType.WTF -> SentryLevel.DEBUG
	    		LogType.I -> SentryLevel.INFO
	    		LogType.W -> SentryLevel.WARNING
	    	}
    	}
    	*/
}
```

Then in your code make sure to import your Log file (`com.example.Log` in this example) instead of `android.util.Log`
And in Java code you'll need to use it as `Log.INSTANCE.{type i,d,v, etc}(...)`
