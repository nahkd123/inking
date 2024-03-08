package io.github.nahkd123.inking.api.manager.config;

public record ConfigurableSection(String name, String description, Configurable... children) implements Configurable {
	public ConfigurableSection(String name, Configurable... children) {
		this(name, null, children);
	}

	public ConfigurableSection(Configurable... children) {
		this("Section", children);
	}

	@Override
	public String getName() { return name; }

	@Override
	public String getDescription() { return description; }
}
