# Building Inking
_A guide on building Inking that targets your platform (OS and CPU architecture)_

## What are supported OSes and architectures?
- Operating systems
  + Windows
  + Linux
  + Mac OS X
- Architectures
  + x86_64
  + arm64/aarch64

> **Note**: Cross-compile for aarch64 from x86_64 (and vice versa) requires installing "C++ ARM64 build tools" from Visual Studio on Windows or a set of packages on Linux (see [.NET Docs/NativeAOT Cross-compile](https://github.com/dotnet/docs/blob/main/docs/core/deploying/native-aot/cross-compile.md#linux) for more information). Mac OS X users only need Xcode to cross-compile (can do x64 and arm64 out of the box).

## Prerequisites
- Exactly Java 21.
- Maven (bundled Maven on Eclipse IDE or Intellij should be fine, or you can get it  from [this page](https://maven.apache.org/download.cgi)).
- Git (to clone this repository).
- Optional: .NET SDK 8.0+ (SDK is needed; you don't need .NET runtime like ASP.NET but it will come with your .NET SDK installation anyways).
  + You only need to get this if you want to build native libraries and test your application.

## Step 1: Clone this repository
Find a nice place to store Inking code, then fire up your terminal and clone Inking reposutory:

```console
$ git clone https://github.com/nahkd123/inking.git
```

## (Optional) Step 2.1: Build OpenTabletDriver bridge (native libraries)
> Requires .NET SDK 8.0+

This step is optional so that you can build your application that only interact with Inking API. If you are making, let's say, osu! clone or a nice little Minecraft HID mod, you need to do this step.

### Option 1: On Windows
> Requires Windows (obviously) with PowerShell installed (either pwsh or preinstalled PowerShell)

Run the `inking-otd\windows-build.ps1` script with the RID of the system that you want to build. For example, let's say I want to run Inking on my machine with Intel CPU:

```powershell
# If you have PowerShell Core installed (which you should use it instead of preinstalled PowerShell)
pwsh inking-otd\windows-build.ps1 win-x64

# If you don't have PowerShell Core installed
powershell inking-otd\windows-build.ps1 win-x64
```

Supported RIDs:
- `win-x64`: Windows x86-64 (for most Windows devices out there, you likely want to use this)
- `win-arm64`: Windows ARM64 (for Windows on ARM devices, like Surface Pro X or Windows Dev Kit)

To cross-compile from `win-x64` to `win-arm64` or vice versa, see the note on cross-compiling above.

> `linux` and `osx` may be used, but .NET NativeAOT would scream at you anyways.

### Option 2: On all platforms (Windows is also included)
> Requires GNU Make (for Windows, you can obtain it from Cygwin)

Change your current working directory to `inking/inking-otd` and run `make`:

```console
$ cd inking-otd
$ make
```

This will build the native library for your **current system only**. To build for other system:
  + Same OS but different architecture: Cross-compile toolchain needs to be installed. See note above for details.
  + Different OS, any architecture: NativeAOT does not support this.
  + Supply `make` with the RIDs of the system. For example: `make win-x64 win-arm64` or `make linux-arm64`

Supported RIDs:
- Build on Windows only:
  - `win-x64`: Windows x86-64 (for most Windows devices out there, you likely want to use this)
  - `win-arm64`: Windows ARM64 (for Windows on ARM devices, like Surface Pro X or Windows Dev Kit)
- Build on Linux only:
  - `linux-x64`: Linux x86-64
  - `linux-arm64`: Linux ARM64/AARCH64 (for Android and RPi devices). Yes it does works on Android.
- Build on Mac OS only:
  - `osx-x64`: Mac OS x86-64 (a.k.a Intel Mac)
  - `osx-arm64`: Max OS ARM64 (a.k.a M1/M2/M3 Mac)

Once you've done this, native libraries should be copied to `inking/inking-otd/src/main/resources/natives/<RID>/Inking.Otd.*`, along with its SHA-1 hash.

## Step 2.2: Build Inking
Simply use Maven to build Inking. Make sure to build the parent project, not the child one!

```bash
# If you ran "cd inking-otd" before:
cd ..

# Build and install Inking to local Maven repository
mvn install

# Or you can just package it
mvn package
```

## Step 3: Use Inking in your application
If you used `mvn install` from previous step, you can use Inking inside your application by including Inking as dependency:

```xml
<dependency>
    <groupId>io.github.nahkd123</groupId>
    <artifactId>inking-api</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```
