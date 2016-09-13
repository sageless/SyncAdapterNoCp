## SyncAdapterNoCp

Android Sync Adapter without a Content Provider

## What  (Introduction)
This is just a collection of code to demonstrate how to easily set up an Android Sync Adapter without using an Android Content Provider.  It is based on other example code found on developer.android.com, stackoverflow.com and some other sites, so the stubbed out classes and some other bits of code may look familiar.

## Why  (Motivation)
I haven't found many small complete examples to show how all the pieces fit.  (At least at the time I started using a Sync Adapter without a Content Provider).  It is not something to be used as is, but mainly as a stripped-down example.

## How  (Installation)

This is not in project format but just the needed source 'src' and resource 'res' files that should easliy be imported into a new Android Studio or Eclipse project.
The support v7 libraries will have to be included for the project in order to run the example fragment with a Recyclerview.

## Misc

This was set up to demonstrate how to show the minimum amount of info on the accounts setting screen.
The xml configuration and synchronization code can be modified to display icon, sync stats, etc.

What else is in here:

- Cursor Loader without using Content Provider.
- RecyclerView Cursor Adapter.
- SQLite batch operations.
- Very simple synchronization manager / system

Also uses cursor wrappers and json wrappers for more compact client code.
