package io.github.nahkd123.inking.internal;

public class PlatformUtils {
	public static String getPlatformId() {
		String name = System.getProperty("os.name");
		if (name.startsWith("Windows ")) return "win";
		if (name.toLowerCase().equals("linux")) return "linux";
		if (name.startsWith("Mac OS X")) return "osx";
		return "unknown";
	}

	public static String getArchId() {
		String name = System.getProperty("os.arch");
		return name.equalsIgnoreCase("amd64") ? "x64" : name.toLowerCase();
	}

	public static String getLibraryExt() {
		String platform = getPlatformId();
		return switch (platform) {
		case "win" -> "dll";
		case "linux" -> "so";
		case "osx" -> "dylib";
		case null, default -> "so";
		};
	}
}
