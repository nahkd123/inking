package io.github.nahkd123.inking.api.manager.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import io.github.nahkd123.inking.api.manager.filtering.FilterHost;
import io.github.nahkd123.inking.api.manager.filtering.FiltersList;
import io.github.nahkd123.inking.api.manager.filtering.HostHasSize;
import io.github.nahkd123.inking.api.manager.utils.MappingGraph;
import io.github.nahkd123.inking.api.manager.utils.Rectangle;
import io.github.nahkd123.inking.api.tablet.ButtonType;
import io.github.nahkd123.inking.api.tablet.Packet;
import io.github.nahkd123.inking.api.tablet.SimplePacket;
import io.github.nahkd123.inking.api.tablet.Tablet;
import io.github.nahkd123.inking.api.tablet.TabletInfo;
import io.github.nahkd123.inking.api.util.MeasurementUnit;
import io.github.nahkd123.inking.api.util.Vector2;

public class TabletConfig {
	private String tabletId;
	private TabletInfo info;
	private AreaConfig areaConfig;
	private MappingGraph pressureMapping;
	private FiltersList filters;

	public TabletConfig(String tabletId, TabletInfo info, AreaConfig areaConfig, MappingGraph pressureMapping, FiltersList filters) {
		this.tabletId = tabletId;
		this.info = info;
		this.areaConfig = areaConfig;
		this.pressureMapping = pressureMapping;
		this.filters = filters;
	}

	public String getTabletId() { return tabletId; }

	public TabletInfo getInfo() { return info; }

	public Optional<AreaConfig> getAreaConfig() { return Optional.ofNullable(areaConfig); }

	public MappingGraph getPressureMapping() { return pressureMapping; }

	public FiltersList getFilters() { return filters; }

	public void applyFilter(Packet packet, FilterHost host, Consumer<Packet> callback) {
		List<Packet> packets = new ArrayList<>();
		filters.filter(packet, host, packets::add);

		packets.forEach(partiallyFiltered -> {
			Vector2 penPos = partiallyFiltered.getPenPosition();
			penPos = host instanceof HostHasSize sizeHost && penPos.unit() == MeasurementUnit.UNITLESS
				? areaConfig.map(partiallyFiltered.getPenPosition(), sizeHost.getHostSize())
				: partiallyFiltered.getPenPosition();
			int pressure = pressureMapping.map(partiallyFiltered.getRawPressure(), info.getMaxPressure());

			// TODO mutable packets
			// @formatter:off
			Packet filtered = new SimplePacket(
				penPos,
				partiallyFiltered.getTilt(),
				pressure,
				partiallyFiltered.getRawHoverDistance(),
				partiallyFiltered.getPenStates(),
				partiallyFiltered.getButtonsDown(ButtonType.PEN),
				partiallyFiltered.getButtonsDown(ButtonType.AUXILIARY),
				partiallyFiltered.getTimestamp());
			// @formatter:on

			callback.accept(filtered);
		});
	}

	public static TabletConfig createDefault(Tablet tablet) {
		TabletInfo info = tablet.getInfo();
		AreaConfig areaConfig = info.getInputSize()
			.map(size -> new AreaConfig(new Rectangle(0, 0, size.x(), size.y(), MeasurementUnit.UNITLESS), true))
			.orElse(null);
		return new TabletConfig(tablet.getTabletId(), info, areaConfig, new MappingGraph(), new FiltersList());
	}
}
