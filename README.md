# Inking
_Reading pen tablet inputs from Java. Actually no, it should be "Java bridge to pen tablet drivers"._

## Quick Info
- Targets Java 21 with **preview features**.
- `groupId`: `io.github.nahkd123`

## Table of Contents
- [Table of Contents](#table-of-contents)
- [Introduction](#introduction)
    + [Why?](#why)
- [Drivers](#drivers)
- [Building](#building)
    + [Requirements](#requirements)
    + [Note](#note)
    + [Building](#building-1)
- [Challenges](#challenges)
    + [.NET: NativeAOT trimming "unused" classes](#net-nativeaot-trimming-unused-classes)
    + [.NET: NativeAOT cross-compiling is not supported](#net-nativeaot-cross-compiling-is-not-supported)
    + [Java: JNA stops calling callbacks from JVM](#java-jna-stops-calling-callbacks-from-jvm)
- [License](#license)

## Introduction
Inking is the bridge from Java to different pen tablet drivers, such as [OpenTabletDriver](https://github.com/OpenTabletDriver/OpenTabletDriver), Windows Ink and x11. This allows Java applications to read tablet inputs, such as pen pressure and pen tilting angles.

### Why?
My initial idea is to somehow read the pen tablet input data in Minecraft so that I can do some terrain sculpturing/terraforming. While you can do that already (by using Relative Mode in OpenTabletDriver or Mouse Mode in Wacom official driver), the game still couldn't get the pen's pressures and tilting angles.

Ok, I know there is no need for reading pen pressure and tilting info for a block game, but the pen hovering height is what I'm interested in. For example, I can draw blocks in the air by holding the button (pen hovering height is only available for OpenTabletDriver at this moment).

## Drivers
- [x] OpenTabletDriver (OTD)
    + [ ] NativeAOT cross-compilation (Windows + Linux + MacOS)
- [ ] Windows Ink
- [ ] Linux X11/Wayland input
- _MacOS is not on the list as OTD already supports MacOS, plus there is no official API known to me. And I don't own a Mac either._

## Building
### Requirements
- Java Development Kit (JDK) 21. Get this from Eclipse Adoptium, [openjdk.org](https://openjdk.org) or from your favorite package manager.
    + In the future, JDK 22 may be used, but JDK 21 should be used as I'm using preview features that might be changed in newer JDK verison.
- Maven the build system
- .NET 8.0 for OpenTabletDriver

### Note
- .NET NativeAOT does not support cross-compile at the time of this README.md being written. That means `inking-otd` built on Linux can only be used on Linux, the same goes for Windows and MacOS.

### Building
- Clone this repository: `git clone https://github.com/nahkd123/Inking.git`
- Build everything and install the artifacts to local Maven repository: `cd Inking && mvn install`
- Use the artifact:

```groovy
repositories {
    mavenLocal()
}

dependencies {
    api 'io.github.nahkd123:inking-api:0.0.1-SNAPSHOT'

    // Add driver implementations
    implementation 'io.github.nahkd123:inking-otd:0.0.1-SNAPSHOT'
    // ...
}
```

## Challenges
_A bunch of problems that I've encountered while making Inking. It is honestly a big waste of time, but I've learned how to deal with these issues. I will document them here so you don't have to waste your time._

### .NET: NativeAOT trimming "unused" classes
This is the biggest problem when dealing with NativeAOT. It tries to trims classes that it believe it is not going to be used by the application. Newtonsoft.Json, a dependency of OpenTabletDriver, uses reflections, which means trimmer have no idea when the class will be used.

This can be fixed by rooting some types in `rd.xml`. And it took my precious 5 hours of my life figuring out how to fix this issue. [See this](https://github.com/dotnet/runtime/blob/main/src/coreclr/nativeaot/docs/rd-xml-format.md) to learn how to write `rd.xml`. After that, add `<RdXmlFile Include="rd.xml" />` in `<ItemGroup></ItemGroup>` inside `.csproj`.

### .NET: NativeAOT cross-compiling is not supported
Compiling for different architecture still works (eg: `linux-x64` and `linux-arm64`), provided you have the toolchain for that (eg: `gcc` for `arm64`). Compiling for different OS, however, is not supported by NativeAOT at this moment.

To solve this, I will have to ~~abuse~~ use GitHub Actions runners to build native binaries for each platform, then push the binaries to a branch. And if Inking is being built on Jitpack's container/runner, I can just download all natives and put that in `src/main/resources` instead of building, which sounds dangerous at first but that's just my initial idea.

#### Alternative solution: Commercial solutions
These solutions requires a license key to be activated, which sounds really dumb for an open source project. I'm not gonna talk about this but if you want to learn more, you can search something like "use c# in java".

#### Alternative solution: CLR host in Java
For this solution, I want to run an entire Common Language Runtime (CLR) host under Java. I believe JNA is sufficient enough. It's just that I couldn't find the C header for CLR (yet). You also need .NET runtime installed as well, so NativeAOT is clearly the winner here.

#### Alternative solution: Inter-process communication (IPC)
This is the **LAST** thing I'll need to use if I want to read inputs from tablet. Not only does it requires spawning a separate process, but it is also introduces more latency Oh and you also need .NET runtime as well, so yeah, more burden to end users.

### Java: JNA stops calling callbacks from JVM
I believe this is a bug from JNA, where trying to access instances' fields inside callback can causes JNA to stop calling your callback in JVM. It usually happens when your native code brust calling the callback (calling the callback in quick succession or something like that). For example, this is the C# code, which will be compiled with NativeAOT:

```csharp
public struct Packet {
    public long Flags;
    // ...
}

[UnmanagedCallerOnly]
public static void CallMe(delegate*<Packet, void> callback) {
    myStaticField.EventHandler += (_, flags) =>
    {
        Packet p = new Packet() { Flags = flags };
        callback(p);
        Console.WriteLine("C#: Hey!");
    };
}
```

And this is the Java code (JNA):

```java
MyLibrary lib;
lib.CallMe((packet) -> {
    this.newFlags = packet.flags;
    System.out.println("Hey!");
});
```

When `myStaticField.EventHandler` from .NET calls the handler in quick succession, the Java code still prints `Hey!` to console. However, after a unspecified amount of calls to the callback, the JVM side stops printing, while the .NET side stills printing `C#: Hey!`. I'd love to make a reproducible repository and report that to JNA developers, but right now I'm so tired after spending time to get JNA working with my OTD bridge.

I have to use [Foreign Function & Memory API](https://openjdk.org/jeps/454) from Java 21 preview feature, which means you'll have to add `--enable-preview` in the command-line arguments. Unfortunately, ~~Minecraft hasn't moved to Java 21 yet, so people adopting Inking is likely going to be way less~~ Minecraft 24w14a just updated to Java 21 and deprecated 32-bit system. Very exciting.

## License
MIT License. See [LICENSE](./LICENSE) for notices.