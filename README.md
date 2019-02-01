# TamilKuripugal
 
Android native app for Tamil Kuripugal

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

<li> <a href="https://developer.android.com/studio/">Android Studio </a> - Preferably the Android Studio Stable version</li>

### Installing

For development purpose deploy the app on the Emulator or a real device

Connect a device in a wireless mode. Alteratively connect the device using a USB cable.

```
### adb connect 192.168.0.X:5555 
```

View avialable devices

```
### adb devices
```

Use the Run feature of the IDE to deploy the app on to a development device 

## Branching for development

For any kind of development and code changes create a branch and work on the branch. When the work is completely and verified merge it back to the main branch.

Create a new branch for development

```
### git checkout -b "BranchName"

```   
When you branch is ready for merging to the master branch follow the below steps to merge your branch

Rebase the feature branch from the development branch and push any changes  

```
### git rebase master
```

Switch to main branch and merge the feature branch into the master branch  
```
### git merge BranchName --no-ff 

```   

Delete a branch after merging changes to master branch

```
### git branch -d <branch_name>   //Delete from local

### git push -d <remote_name> <branch_name>  //Delete from remote 

```   


## Running the tests

Run all the test cases

```
### ./gradlew test
```


## Deployment

Add additional notes about how to deploy this on a live system

### Lint Errors

Run the below command to check and any Lint warnings

```
### ./gradlew lint
```

Runs lint on just the fatal issues in the release build.

```
### ./gradlew lintVitalRelease
```

### Assemble Release Build

Assembles all Release builds. This will create the mapping.txt
```
### ./gradlew assembleRelease
```


### Packaging

- Change the minifyEnabled on the Debug build before preparing a release build
- Change the Ad Unit Id's
- Change the App Verion No and Value
- Install to device directly

```
adb install "../../AndroidWorkspace/apk/thalivargal/1.1.11/release/app-release.apk"

