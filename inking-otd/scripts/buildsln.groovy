import java.nio.file.Files

// Configure your .NET build tools here
def dotnet = "dotnet"
def version = "net8.0"
def configuration = "Release"
def targets = [
	"win-x64": "Inking.Otd.dll",
	"linux-x64": "Inking.Otd.so",
	"osx-x64": "Inking.Otd.dylib",
	"osx-arm64": "Inking.Otd.dylib"
]

def csproj = new File(properties["csproj"])
def nativesDir = new File(properties["nativesDir"])

println("Building Inking.Otd C# project...")
println("C# project: $csproj")
println("Natives will be copied to $nativesDir")

targets.entrySet().each {
	def command = "$dotnet publish -r ${it.key} -c $configuration $csproj"
	println("  Running '$command'")
	
	def process = command.execute()
	process.consumeProcessOutput(System.out, System.err)

	if (process.waitFor() != 0) {
		System.err.println("Command '$command' exited with code ${process.exitValue()}")
	} else {
		def nativeLib = new File(csproj, "../bin/$configuration/$version/${it.key}/native/${it.value}").getAbsoluteFile()
		def destDir = new File(nativesDir, "natives/${it.key}")
		if (!destDir.exists()) destDir.mkdirs()

		def destFile = new File(destDir, it.value);
		println("Copying ${nativeLib.toPath().normalize()} to ${destFile}...")
		if (destFile.exists()) destFile.delete()
		Files.copy(nativeLib.toPath(), destFile.toPath())
	}
}

println("Done!")