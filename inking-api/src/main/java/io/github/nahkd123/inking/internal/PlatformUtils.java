package io.github.nahkd123.inking.internal;

import java.io.IOException;
import java.io.InputStream;
import java.lang.foreign.Arena;
import java.lang.foreign.SymbolLookup;
import java.nio.file.Files;
import java.nio.file.Path;

public class PlatformUtils {
	/**
	 * <p>
	 * Load native library using Java 21 Foreign Functions and Memory API. Please
	 * note that Inking is vulnerable to dynamic library injection, where others can
	 * replace the library with sus one. In fact, you wouldn't trust a random mod
	 * from internet anyways, so is it really important?
	 * </p>
	 * <p>
	 * SHA-1 checksum could be implemented in the future, but I don't have time for
	 * that right now.
	 * </p>
	 * 
	 * @param path         Path to resource inside class loader. The actual path to
	 *                     natives that class loader must resolves is
	 *                     {@code natives/<platform-arch>/<path>.<ext>}.
	 * @param clsLoader    The class loader that hold the resources.
	 * @param nativesStore The directory where natives will be copied.
	 * @param arena        The arena for memory thing.
	 * @return A {@link SymbolLookup} if such native does exists in class loader.
	 */
	public static SymbolLookup loadLibrary(String path, ClassLoader clsLoader, Path nativesStore, Arena arena) {
		Platform platform = Platform.getCurrent();
		PlatformArch arch = PlatformArch.getCurrent();
		String rid = platform.getPlatformId() + "-" + arch.getArchId();
		String resPath = "natives/" + rid + "/" + path + "." + platform.getDynamicLibExtension();
		Path nativePath = nativesStore.resolve(resPath);

		if (!Files.exists(nativePath)) {
			if (!Files.exists(nativePath.resolve(".."))) try {
				Files.createDirectories(nativePath.resolve(".."));
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}

			try (InputStream res = clsLoader.getResourceAsStream(resPath)) {
				if (res == null) return null;
				Files.copy(res, nativePath);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		return SymbolLookup.libraryLookup(nativePath, arena);
	}
}
