package io.github.nahkd123.inking.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.foreign.Arena;
import java.lang.foreign.SymbolLookup;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class PlatformUtils {
	private static final String HEX = "0123456789abcdef";

	private static byte[] hexToBytes(char[] chars) {
		byte[] bs = new byte[chars.length / 2];

		for (int i = 0; i < bs.length; i++) {
			int msb = HEX.indexOf(chars[i * 2]);
			int lsb = HEX.indexOf(chars[i * 2 + 1]);
			bs[i] = (byte) ((msb << 4) | lsb);
		}

		return bs;
	}

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
	 * @param path           Path to resource inside class loader. The actual path
	 *                       to natives that class loader must resolves is
	 *                       {@code natives/<platform-arch>/<path>.<ext>}.
	 * @param clsLoader      The class loader that hold the resources.
	 * @param nativesStore   The directory where natives will be copied.
	 * @param arena          The arena for memory thing.
	 * @param logger         A simple logger, or {@code null} if you don't need
	 *                       logging.
	 * @param ignoreChecksum Ignore checksum.
	 * @return A {@link SymbolLookup} if such native does exists in class loader.
	 */
	public static SymbolLookup loadLibrary(String path, ClassLoader clsLoader, Path nativesStore, Arena arena, SimpleLogger logger, boolean ignoreChecksum) {
		Platform platform = Platform.getCurrent();
		PlatformArch arch = PlatformArch.getCurrent();
		String rid = platform.getPlatformId() + "-" + arch.getArchId();
		String resPath = "natives/" + rid + "/" + path + "." + platform.getDynamicLibExtension();
		Path nativePath = nativesStore.resolve(resPath);
		byte[] hash = null;
		boolean doCopy;

		try (InputStream res = clsLoader.getResourceAsStream(resPath + ".sha1")) {
			if (res != null) {
				Reader reader = new InputStreamReader(res, StandardCharsets.UTF_8);
				char[] hex = new char[40];
				int offset = 0, charsRead;
				while (offset < 40 && (charsRead = reader.read(hex, offset, 40 - offset)) != -1) offset += charsRead;

				// If otherwise then our .sha1 file is corrupted
				if (offset == 40) hash = hexToBytes(hex);
				if (hash == null && logger != null)
					logger.warning(resPath + ".sha1 is corrupted! Checksum option will be ignored.");
			} else if (logger != null) {
				if (logger != null) logger.warning(resPath + ".sha1 does not exists! Checksum option will be ignored.");
			}
		} catch (IOException e) {
			e.printStackTrace();
			if (logger != null)
				logger.warning("IOException reading " + resPath + ".sha1! Checksum option will be ignored.");
		}

		if (ignoreChecksum) {
			// Allow user to override the library
			doCopy = Files.notExists(nativePath);
		} else if (Files.exists(nativePath)) {
			try (InputStream nativeObj = Files.newInputStream(nativePath)) {
				MessageDigest md = MessageDigest.getInstance("SHA-1");
				byte[] buffer = new byte[4096];

				while (nativeObj.available() > 0) {
					int bytesRead = nativeObj.read(buffer);
					md.update(buffer, 0, bytesRead);
				}

				byte[] digested = md.digest();
				boolean valid = Arrays.equals(hash, digested);
				doCopy = !valid;

				if (!valid && logger != null)
					logger.error(nativePath + ": Invalid SHA-1 hash, copying new file...");
			} catch (IOException e) {
				e.printStackTrace();
				if (logger != null) logger.warning(nativePath + ": Can't open for checksum, copying new file anyways.");
				doCopy = true;
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				if (logger != null)
					logger.warning(resPath + ": SHA-1 algorithm is not available, copying new file anyways.");
				doCopy = true;
			}
		} else {
			doCopy = true;
		}

		if (doCopy) {
			if (!Files.exists(nativePath.resolve(".."))) try {
				Files.createDirectories(nativePath.resolve(".."));
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}

			try (InputStream res = clsLoader.getResourceAsStream(resPath)) {
				if (res == null) return null;
				Files.copy(res, nativePath);
				logger.info(resPath + " => " + nativePath);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		return SymbolLookup.libraryLookup(nativePath, arena);
	}
}
