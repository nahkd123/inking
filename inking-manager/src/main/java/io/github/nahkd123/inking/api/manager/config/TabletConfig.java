package io.github.nahkd123.inking.api.manager.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import io.github.nahkd123.inking.api.manager.filtering.FilterHost;
import io.github.nahkd123.inking.api.manager.filtering.FiltersList;
import io.github.nahkd123.inking.api.manager.filtering.HostHasSize;
import io.github.nahkd123.inking.api.manager.utils.Rectangle;
import io.github.nahkd123.inking.api.tablet.MutablePacket;
import io.github.nahkd123.inking.api.tablet.Packet;
import io.github.nahkd123.inking.api.tablet.Tablet;
import io.github.nahkd123.inking.api.tablet.TabletInfo;
import io.github.nahkd123.inking.api.util.MeasurementUnit;

public class TabletConfig {
	private String tabletId;
	private TabletInfo info;
	private boolean enabled;
	private AreaConfig areaConfig;
	private FiltersList filters;

	public TabletConfig(String tabletId, TabletInfo info, boolean enabled, AreaConfig areaConfig, FiltersList filters) {
		this.tabletId = tabletId;
		this.info = info;
		this.enabled = enabled;
		this.areaConfig = areaConfig;
		this.filters = filters;
	}

	public String getTabletId() { return tabletId; }

	public TabletInfo getInfo() { return info; }

	public boolean isEnabled() { return enabled; }

	public void setEnabled(boolean enabled) { this.enabled = enabled; }

	public Optional<AreaConfig> getAreaConfig() { return Optional.ofNullable(areaConfig); }

	public FiltersList getFilters() { return filters; }

	public void filter(Packet packet, FilterHost host, Consumer<Packet> callback) {
		if (!enabled) return;

		List<Packet> packets = new ArrayList<>();
		filters.filter(packet, host, packets::add);

		packets.forEach(partiallyFiltered -> {
			MutablePacket filtered = MutablePacket.mutableOf(packet);

			if (host instanceof HostHasSize sizeHost && filtered.getPenPosition().unit() == MeasurementUnit.UNITLESS)
				filtered.setPenPosition(areaConfig.map(partiallyFiltered.getPenPosition(), sizeHost.getHostSize()));

			callback.accept(filtered);
		});
	}

	public static TabletConfig createDefault(Tablet tablet) {
		TabletInfo info = tablet.getInfo();
		AreaConfig areaConfig = info.getInputSize()
			.map(size -> new AreaConfig(new Rectangle(0, 0, size.x(), size.y(), MeasurementUnit.UNITLESS), true))
			.orElse(null);
		return new TabletConfig(tablet.getTabletId(), info, true, areaConfig, new FiltersList());
	}
}
