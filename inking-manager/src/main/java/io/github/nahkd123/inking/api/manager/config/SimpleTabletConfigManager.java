package io.github.nahkd123.inking.api.manager.config;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.JsonOps;

import io.github.nahkd123.inking.api.tablet.Tablet;
import io.github.nahkd123.inking.api.util.SimpleLogger;

public class SimpleTabletConfigManager implements TabletConfigManager {
	private static ThreadLocal<Gson> gson = ThreadLocal.withInitial(() -> new GsonBuilder()
		.disableHtmlEscaping()
		.setPrettyPrinting()
		.create());

	private Path configRoot;
	private Map<String, TabletConfig> configurations = new HashMap<>();
	private SimpleLogger logger;

	public SimpleTabletConfigManager(Path configRoot, SimpleLogger logger) {
		this.configRoot = configRoot;
		this.logger = logger;
		loadFromPath(configRoot);
	}

	private void loadFromPath(Path path) {
		try {
			if (Files.isDirectory(path)) Files.list(path).forEach(this::loadFromPath);

			if (Files.isRegularFile(path)) {
				try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
					JsonElement json = JsonParser.parseReader(reader);
					TabletConfig config = TabletConfig.CODEC
						.decode(JsonOps.INSTANCE, json)
						.resultOrPartial(msg -> logger.warning(path + ": " + msg))
						.map(pair -> pair.getFirst())
						.orElse(null);

					if (config == null) return;
					configurations.put(config.getTabletId(), config);
					logger.info("Loaded configuration: " + config.getTabletId());
				} catch (JsonSyntaxException e) {
					logger.warning(path + ": Malformed JSON: " + e.getMessage());
				}
			}
		} catch (IOException e) {
			logger.warning(path + ": Failed to process configuration(s) (IOException, see above)");
		}
	}

	@Override
	public TabletConfig get(Tablet tablet) {
		Objects.requireNonNull(tablet, "tablet can't be null");
		return configurations.compute(tablet.getTabletId(), (id, config) -> config != null
			? config
			: new TabletConfig(tablet));
	}

	@Override
	public void save(TabletConfig config) {
		Objects.requireNonNull(config, "config can't be null");
		Path configPath = getPathForId(config.getTabletId());
		JsonElement json = TabletConfig.CODEC
			.encodeStart(JsonOps.INSTANCE, config)
			.resultOrPartial(msg -> logger.warning(config.getTabletId() + ": " + msg))
			.orElse(new JsonObject());

		try {
			if (!Files.exists(configPath.resolve(".."))) Files.createDirectories(configPath.resolve(".."));

			try (JsonWriter writer = new JsonWriter(Files.newBufferedWriter(configPath, StandardCharsets.UTF_8))) {
				writer.setIndent("    ");
				gson.get().toJson(json, writer);
				logger.info(config.getTabletId() + ": Saved configuration");
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.warning(config.getTabletId() + ": Failed to save configuration: IOException (see above)");
		}
	}

	private Path getPathForId(String id) {
		return configRoot.resolve(id.replace(':', '/') + ".json");
	}

	@Override
	public Map<String, TabletConfig> getAllConfig() { return Collections.unmodifiableMap(configurations); }
}
