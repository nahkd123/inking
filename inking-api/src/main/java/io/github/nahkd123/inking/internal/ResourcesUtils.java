package io.github.nahkd123.inking.internal;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collections;
import java.util.function.Consumer;

public class ResourcesUtils {
	/**
	 * <p>
	 * Try to get path to resource and consume it. If the file is inside the JAR, it
	 * will creates a new zip file system, call the consumer, then remove it.
	 * </p>
	 * 
	 * @param uri      The URI.
	 * @param consumer The resource path consumer.
	 * @throws IOException
	 */
	public static void consumeResource(URI uri, Consumer<Path> consumer) throws IOException {
		try {
			Path path = Path.of(uri);
			consumer.accept(path);
		} catch (FileSystemNotFoundException e) {
			try (FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
				Path path = fs.provider().getPath(uri);
				consumer.accept(path);
			}
		}
	}
}
