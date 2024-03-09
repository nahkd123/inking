package io.github.nahkd123.inking.api.manager.config;

import java.util.Optional;

import io.github.nahkd123.inking.api.manager.Rectangle;
import io.github.nahkd123.inking.api.manager.filtering.FiltersList;
import io.github.nahkd123.inking.api.tablet.Tablet;
import io.github.nahkd123.inking.api.tablet.TabletInfo;
import io.github.nahkd123.inking.api.util.MeasurementUnit;

public class TabletConfig {
	private String tabletId;
	private TabletInfo info;
	private AreaConfig areaConfig;
	private FiltersList filters;

	public TabletConfig(String tabletId, TabletInfo info, AreaConfig areaConfig, FiltersList filters) {
		this.tabletId = tabletId;
		this.info = info;
		this.areaConfig = areaConfig;
		this.filters = filters;
	}

	public String getTabletId() { return tabletId; }

	public TabletInfo getInfo() { return info; }

	public Optional<AreaConfig> getAreaConfig() { return Optional.ofNullable(areaConfig); }

	public FiltersList getFilters() { return filters; }

	public static TabletConfig createDefault(Tablet tablet) {
		TabletInfo info = tablet.getInfo();
		AreaConfig areaConfig = info.getInputSize()
			.map(size -> new AreaConfig(new Rectangle(0, 0, size.x(), size.y(), MeasurementUnit.UNITLESS), true))
			.orElse(null);
		return new TabletConfig(tablet.getTabletId(), info, areaConfig, new FiltersList());
	}
}
