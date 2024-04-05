package io.github.nahkd123.inking.api.manager.info;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.JsonOps;

import io.github.nahkd123.inking.api.tablet.Tablet;
import io.github.nahkd123.inking.api.tablet.TabletInfo;
import io.github.nahkd123.inking.api.util.SimpleLogger;

public class SimpleTabletInfoManager implements TabletInfoManager {
	private static ThreadLocal<Gson> gson = ThreadLocal.withInitial(() -> new GsonBuilder()
		.disableHtmlEscaping()
		.setPrettyPrinting()
		.create());

	private Path infoRoot;
	private Map<String, TabletInfo> info = new HashMap<>();
	private SimpleLogger logger;

	public SimpleTabletInfoManager(Path infoRoot, SimpleLogger logger) {
		this.infoRoot = infoRoot;
		this.logger = logger;
		loadFromPath(infoRoot);
	}

	private void loadFromPath(Path path) {
		try {
			if (Files.isDirectory(path)) Files.list(path).forEach(this::loadFromPath);

			if (Files.isRegularFile(path)) {
				try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
					JsonElement json = JsonParser.parseReader(reader);
					ImmutableTabletInfo info = ImmutableTabletInfo.CODEC
						.decode(JsonOps.INSTANCE, json)
						.resultOrPartial(msg -> logger.warning(path + ": " + msg))
						.map(pair -> pair.getFirst())
						.orElse(null);

					if (info == null) return;
					this.info.put(info.tabletId(), info);
					logger.info("Loaded tablet info: " + info.tabletId());
				} catch (JsonSyntaxException e) {
					logger.warning(path + ": Malformed JSON: " + e.getMessage());
				}
			}
		} catch (IOException e) {
			logger.warning(path + ": Failed to process info (IOException, see above)");
		}
	}

	@Override
	public void saveIfAbsent(Tablet tablet) {
		if (!info.containsKey(tablet.getTabletId())) try {
			ImmutableTabletInfo info = ImmutableTabletInfo.copyFrom(tablet.getTabletId(), tablet.getInfo());
			Path filePath = getPathForId(tablet.getTabletId());
			JsonElement json = ImmutableTabletInfo.CODEC
				.encodeStart(JsonOps.INSTANCE, info)
				.resultOrPartial(msg -> logger.warning(tablet.getTabletId() + ": " + msg))
				.orElse(new JsonObject());

			if (!Files.exists(filePath.resolve(".."))) Files.createDirectories(filePath.resolve(".."));

			try (JsonWriter writer = new JsonWriter(Files.newBufferedWriter(filePath, StandardCharsets.UTF_8))) {
				writer.setIndent("    ");
				gson.get().toJson(json, writer);
				logger.info(tablet.getTabletId() + ": Saved information");
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.warning(tablet.getTabletId() + ": Failed to save tablet info. Please see above.");
		}

		info.put(tablet.getTabletId(), tablet.getInfo());
	}

	@Override
	public TabletInfo get(String tabletId) {
		return info.get(tabletId);
	}

	@Override
	public Map<String, TabletInfo> getAll() { return Collections.unmodifiableMap(info); }

	private Path getPathForId(String id) {
		return infoRoot.resolve(id.replace(':', '/') + ".json");
	}
}
