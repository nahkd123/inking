package io.github.nahkd123.inking.api.manager.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.nahkd123.inking.api.manager.filtering.AreaMappingFilter;
import io.github.nahkd123.inking.api.manager.filtering.TabletFiltersList;
import io.github.nahkd123.inking.api.tablet.Tablet;

public class TabletConfig {
	private String tabletId;
	private boolean enable;
	private TabletFiltersList filters;

	public TabletConfig(String tabletId, boolean enable, TabletFiltersList filters) {
		this.tabletId = tabletId;
		this.enable = enable;
		this.filters = filters;
	}

	public TabletConfig(Tablet tablet) {
		this(tablet.getTabletId(), true, new TabletFiltersList());
		filters.add(new AreaMappingFilter(tablet.getInfo()));
	}

	public static final Codec<TabletConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.STRING.fieldOf("id").forGetter(TabletConfig::getTabletId),
		Codec.BOOL.fieldOf("enable").forGetter(TabletConfig::isEnabled),
		TabletFiltersList.CODEC.fieldOf("filters").forGetter(TabletConfig::getFilters))
		.apply(instance, TabletConfig::new));

	public String getTabletId() { return tabletId; }

	public boolean isEnabled() { return enable; }

	public void setEnable(boolean enable) { this.enable = enable; }

	public TabletFiltersList getFilters() { return filters; }
}
