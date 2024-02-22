import java.nio.file.Files

// (c) nahkd and Inking contributors 2024
// Licensed under MIT license. See "LICENSE" for more information.

// Welcome to inking-otd build script.
// Due to NativeAOT limitation, cross-compiling for different platforms is not
// supported. Cross-compiling for different architecture is still possible.
// Make sure you have all required tools to build for respective platform:
// - Windows:
//   + C++ build tools
//   + C++ ARM64 build tools
// - Linux: gcc for whatever platform you want to use
// - Mac OS X: I think you need XCode to compile.

// Configure your .NET build tools here
def dotnet = "dotnet"
def version = "net8.0"
def configuration = "Release"
def targets = [
	"win": [
		"x64": "Inking.Otd.dll",         // "C++ build tools" is required
		"arm64": "Inking.Otd.dll"        // "C++ ARM64 build tools" is required
	],
	"linux": [
		"x64": "Inking.Otd.so",
		"arm64": "Inking.Otd.so"
	],
	"osx": [
		"x64": "Inking.Otd.dylib",       // XCode is required
		"arm64": "Inking.Otd.dylib"
	]
]
// End of configuration

def platform = System.getProperty("os.name").startsWith("Windows") ? "win"
	: System.getProperty("os.name").toLowerCase().equals("linux") ? "linux"
	: System.getProperty("os.name").startsWith("Mac OS X") ? "osx"
	: "unknown"
def targetsToCompile = targets[platform]

def csproj = new File(properties["csproj"])
def nativesDir = new File(properties["nativesDir"])
def compiled = 0

println("Building Inking.Otd C# project...")
println("C# project: $csproj")
println("Natives will be copied to $nativesDir")
println("Building for platform: $platform (all architectures)")

targetsToCompile.entrySet().each {
	def platformArch = "${platform}-${it.key}"
	def command = "$dotnet publish -r $platformArch -c $configuration $csproj"
	println("  Running '$command'")

	def process = command.execute()
	process.consumeProcessOutput(System.out, System.err)

	if (process.waitFor() != 0) {
		System.err.println("  Command '$command' exited with code ${process.exitValue()}")
		System.err.println("  You might want to make sure that you have enough tools")
	} else {
		def nativeLib = new File(csproj, "../bin/$configuration/$version/$platformArch/native/${it.value}").getAbsoluteFile()
		def destDir = new File(nativesDir, "natives/$platformArch")
		if (!destDir.exists()) destDir.mkdirs()

		def destFile = new File(destDir, it.value);
		println("  Copying ${nativeLib.toPath().normalize()} to ${destFile}...")
		if (destFile.exists()) destFile.delete()
		Files.copy(nativeLib.toPath(), destFile.toPath())
		compiled++
	}
}

if (compiled == 0) {
	throw new RuntimeException("No binaries built! Please check your .NET build tool")
} else {
	println("Done! Compiled $compiled binaries")
}