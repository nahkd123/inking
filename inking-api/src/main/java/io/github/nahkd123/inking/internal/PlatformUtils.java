package io.github.nahkd123.inking.internal;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.Linker;
import java.lang.foreign.SymbolLookup;
import java.net.URISyntaxException;

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

	public static SymbolLookup lookupNative(ClassLoader clsLoader, String libName, Linker linker, Arena arena) throws IOException, URISyntaxException {
		Holder<SymbolLookup> lib = new Holder<>(null);
		ResourcesUtils.consumeResource(clsLoader.getResource("natives/"
			+ getPlatformId() + "-" + getArchId() + "/"
			+ libName + "." + getLibraryExt()).toURI(), path -> lib.obj = SymbolLookup.libraryLookup(path, arena));
		return lib.obj;
	}
}
