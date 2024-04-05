# Challenges
_A bunch of problems that I've encountered while making Inking. I decided to document them here in hope that you may find these interesting, or you just encountered the same issue as mine._

## .NET: NativeAOT trimming "unused" classes
This is the biggest problem when dealing with NativeAOT. It tries to trims classes that it believe it is not going to be used by the application. Newtonsoft.Json, a dependency of OpenTabletDriver, uses reflections, which means trimmer have no idea when the class will be used.

This can be fixed by rooting some types in `rd.xml`. And it took my precious 5 hours of my life figuring out how to fix this issue. [See this](https://github.com/dotnet/runtime/blob/main/src/coreclr/nativeaot/docs/rd-xml-format.md) to learn how to write `rd.xml`. After that, add `<RdXmlFile Include="rd.xml" />` in `<ItemGroup></ItemGroup>` inside `.csproj`.

## .NET: NativeAOT cross-compiling is not supported
Compiling for different architecture still works (eg: `linux-x64` and `linux-arm64`), provided you have the toolchain for that (eg: `gcc` for `arm64`). Compiling for different OS, however, is not supported by NativeAOT at this moment.

To solve this, I will have to ~~abuse~~ use GitHub Actions runners to build native binaries for each platform, ~~then push the binaries to a branch. And if Inking is being built on Jitpack's container/runner, I can just download all natives and put that in `src/main/resources` instead of building, which sounds dangerous at first but that's just my initial idea~~ I decided to scrap that. A better approach would be building Inking along with my application (in this case, it is InkingCraft) in GitHub actions runner.

### Alternative solution: CLR host in Java
For this solution, I want to run an entire Common Language Runtime (CLR) host under Java. One downside of this is that you need .NET runtime (which included on almost all modern Windows machines, fortunately).

### Alternative solution: Inter-process communication (IPC)
I wanted to avoid this as much as possible. Tablet devices generally can reports up to 1000 times per second, which means IPC like piping/named sockets are going to be a bottleneck. Inputs might be hundreds of milliseconds behind.

## Java: JNA stops calling callbacks from JVM
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
