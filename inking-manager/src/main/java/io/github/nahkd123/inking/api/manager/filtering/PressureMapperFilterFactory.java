package io.github.nahkd123.inking.api.manager.filtering;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import io.github.nahkd123.inking.api.manager.config.Configurable;
import io.github.nahkd123.inking.api.manager.config.ConfigurableValue;
import io.github.nahkd123.inking.api.manager.filtering.PressureMapperFilterFactory.PressureMapper;
import io.github.nahkd123.inking.api.manager.utils.MappingGraph;
import io.github.nahkd123.inking.api.tablet.MutablePacket;
import io.github.nahkd123.inking.api.tablet.Packet;

public class PressureMapperFilterFactory implements FilterFactory<PressureMapper> {
	public static class PressureMapper implements TabletFilter<PressureMapper> {
		private PressureMapperFilterFactory factory;
		private boolean enabled;
		private MappingGraph graph;

		public PressureMapper(PressureMapperFilterFactory factory, boolean enabled, MappingGraph graph) {
			this.factory = factory;
			this.enabled = enabled;
			this.graph = graph;
		}

		@Override
		public PressureMapperFilterFactory getFactory() { return factory; }

		@Override
		public void filterPacket(Packet packet, Consumer<Packet> pusher, FilterHost host) {
			if (!enabled) {
				pusher.accept(packet);
				return;
			}

			MutablePacket mutable = MutablePacket.mutableOf(packet);
			int in = mutable.getRawPressure();
			int max = host.getTablet().getInfo().getMaxPressure();
			mutable.setRawPressure(graph.map(in, max));
		}
	}

	@Override
	public PressureMapper createDefaultFilter() {
		return new PressureMapper(this, true, new MappingGraph());
	}

	@Override
	public String getFilterName() { return "Pressure Mapper"; }

	@Override
	public List<Configurable> getFilterConfig(PressureMapper filter) {
		// @formatter:off
		return Arrays.asList(
			new ConfigurableValue<>(
				boolean.class,
				"Enable",
				"Enable to map pressure. Disable for passthrough.",
				() -> filter.enabled,
				s -> filter.enabled = s),
			new ConfigurableValue<>(
				MappingGraph.class,
				"Pressure Mapping",
				"Map input pressure to desired output pressure.",
				() -> filter.graph,
				$ -> {})); // The UI will modify the graph object directly
		// @formatter:on
	}
}
