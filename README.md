# Eclipse Origins 4: Solar Eclipse

> Welcome to _**Eclipse Origins 4**_, a love letter to Visual Basic 2D ORPG Engines. 

&emsp; This build, nicknamed _Solar Eclipse_, is version `4.4.1.1-alpha`.


## Table of Contents
- [Requirements](#requirements)
- [Project Overview](#project-overview)
- [Sub-projects](#sub-projects)
  - [Core](#core)
  - [Server](#server)
  - [Editor](#editor)
  - [Client](#client)
- [Building](#building)
  - [Clean and Build All](#clean-and-build-all)
  - [Clean and Build Core](#clean-and-build-core)
  - [Clean and Build Server](#clean-and-build-server)
  - [Clean and Build Editor](#clean-and-build-editor)
  - [Clean and Build Client](#clean-and-build-client)
- [Execution](#execution)
  - [Running the Server](#running-the-server)
  - [Running the Editor](#running-the-editor)
  - [Running the Client](#running-the-client)
- [Distribution](#distribution)
- [Errata](#errata)


## Requirements
- **Written in Java 17**
- * OpenJDK "Temurin" version 21-0.3+9 (Eclipse Adoptium JRE)
- **Built with:** Gradle 8.12 (Groovy configuration)



Currently only tested on Windows 10 & 11.


## Project Overview

Version strings follow the **MAJOR**(#?).**MODEL**(#??).**CONTROLLER**(#??).**VIEW**(#??)-**MILESTONE** format.  

So, Solar Eclipse (`v4`) is currently using Model `v4`, Controller `v1`, View `v1`, and is in its `alpha` milestone.

The project's packages and modules exist within the `dev.atomixsoft.solar_eclipse` namespace.  The only module is `core` - `server`, `editor`, and `client` sub-projects are comprised of a series of packages.

| **Module**      | **Package**     | **Description** |
| ---------------------------------------- | ---------------------------------------- | -------------------------------------------------------------------------------- |
| _Core_ | _Game_ | _TODO_ |
| _Core_ | _Utils_ | _TODO_ |
| _Core_ | _Network_ | _TODO_ |
| _N/A_ | _Server_ | _TODO_ |
| _N/A_ | _Editor_ | _TODO_ |
| _N/A_ | _Client_ | _TODO_ |
| _N/A_ | _ClientAudio_ | _TODO_ |
| _N/A_ | _ClientGraphics_ | _TODO_ |
| _N/A_ | _ClientScene_ | _TODO_ |
| _N/A_ | _ClientUtil_ | _TODO_ |


## Sub-projects

### Core
The `core` sub-project includes essential libraries and classes shared across the other sub-projects.

| **Dependencies**:
- [apache-commons-configuration 2.10.1](https://commons.apache.org/proper/commons-configuration/)
- [apache-commons-logging 1.3.2](https://commons.apache.org/proper/commons-logging/)
- [junit-jupiter 5.10.0](https://junit.org/junit5/)

### Server
The `server` sub-project handles the back-end operations of the game.

| **Dependencies**:
- `:core`

### Editor
The `editor` sub-project provides tools for developers to create and manage game content.

| **Dependencies**:
- `:core`

### Client
The `client` sub-project is the game front-end.  

| **Dependencies**:
- `:core`
- [lwjgl 3.3.6](https://www.lwjgl.org/)
- * [joml 1.10.5](https://joml-ci.github.io/JOML/)
- * [imgui-java 1.89.0](https://github.com/SpaiR/imgui-java)


## Building

### Clean and Build All
To clean & build all sub-projects, execute the following:
```sh
./gradlew clean 
./gradlew build
```

### Clean and Build Core
You must build, preferably in a clean environment, the `core` sub-project before building `server`, `editor`, or `client`.

To clean & build only the `core` sub-project, execute the following:
```sh
./gradlew :core:clean 
./gradlew :core:build
```

### Clean and Build Server
To clean & build only the `server` sub-project, execute the following:
```sh
./gradlew :server:clean 
./gradlew :server:build
```

### Clean and Build Editor
To clean & build only the `editor` sub-project, execute the following:
```sh
./gradlew :editor:clean 
./gradlew :editor:build
```

### Clean and Build Client
To clean & build only the `client` sub-project, execute the following:
```sh
./gradlew :client:clean 
./gradlew :client:build
```


## Execution

### Running the Server

You must run the `server` sub-project before `editor` or `client`.

To run the `server`, execute the following:
```sh
./gradlew :server:run
```

To run `server` in debug mode, execute the following instead:
```sh
./gradlew :server:debug
```

### Running the Editor

To run the `editor`, execute the following:
```sh
./gradlew :editor:run
```

To run `editor` in debug mode, execute the following instead:
```sh
./gradlew :editor:debug
```

### Running the Client

To run the `client`, execute the following:
```sh
./gradlew :client:run
```

To run `client` in debug mode, execute the following instead:
```sh
./gradlew :client:debug
```


## Distribution
| _TODO_


## Errata

Current planned Milestones are `alpha`, `beta`, `rc1`, `rc2`, and `rtm`.  

For a little background, take a look at these links:
- [Eclipse - Free MMORPG Maker - Index](https://web.archive.org/web/20110901224553/http://www.touchofdeathforums.com/smf/index.php)
- [Forums - Eclipse Origins](https://forum.eclipseorigins.com/)

Of course, make sure you also visit us at [[atomixsoft.dev]](https://atomixsoft.dev/) !

> We hope you find **Eclipse Origins 4: Solar Eclipse** a very clean, somewhat nostalgic, highly performant, and infinitely flexible engine.  For any issues or contributions, please refer to our [issue tracker](#) or [contributing guide](#).

_Enjoy!_
