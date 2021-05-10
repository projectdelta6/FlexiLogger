# FlexiLogger

Android Logging helper to provide flexible Log methods.

[![](https://jitpack.io/v/projectdelta6/FlexiLogger.svg)](https://jitpack.io/#projectdelta6/FlexiLogger)

https://jitpack.io/#projectdelta6/FlexiLogger

Add it to your build.gradle with:
```gradle
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```
and:

```gradle
dependencies {
    compile 'com.github.projectdelta6:FlexiLogger:{desired version}'
}
```

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
    	 * method for Serntry reporting example:
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
