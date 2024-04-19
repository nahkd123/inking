package io.github.nahkd123.inking.api.manager.filtering;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.nahkd123.inking.api.manager.utils.MappingGraph;
import io.github.nahkd123.inking.api.tablet.MutablePacket;
import io.github.nahkd123.inking.api.tablet.Packet;
import io.github.nahkd123.inking.api.tablet.TabletInfo;

public class PressureMappingFilter extends AbstractTabletFilter {
	private boolean enable;
	private MappingGraph graph;

	public PressureMappingFilter(boolean enable, MappingGraph graph) {
		this.enable = enable;
		this.graph = graph;
	}

	public PressureMappingFilter(TabletInfo info) {
		this.enable = true;
		this.graph = new MappingGraph();
		this.graph.add(0, 0);
		this.graph.add(info.getMaxPressure(), info.getMaxPressure());
	}

	public boolean isEnabled() { return enable; }

	public void setEnable(boolean enable) { this.enable = enable; }

	public MappingGraph getGraph() { return graph; }

	public void setGraph(MappingGraph graph) { this.graph = graph; }

	@Override
	public void onPacket(Packet incoming) {
		if (!enable) {
			push(incoming);
			return;
		}

		MutablePacket mutable = MutablePacket.mutableOf(incoming);
		mutable.setRawPressure(graph.map(mutable.getRawPressure(), getHost().getTablet().getInfo().getMaxPressure()));
		push(mutable);
	}

	public static final MapCodec<PressureMappingFilter> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.BOOL.fieldOf("enable").forGetter(PressureMappingFilter::isEnabled),
		MappingGraph.CODEC.fieldOf("graph").forGetter(PressureMappingFilter::getGraph))
		.apply(instance, PressureMappingFilter::new));

	@Override
	public MapCodec<? extends TabletFilter> getCodec() { return CODEC; }

	public static void register() {
		TabletFilter.register(
			"inking:pressure_mapping",
			CODEC, PressureMappingFilter::new,
			"Pressure Mapping",
			"Map pen pressure to your own desire!",
			"nahkd123");
	}
}
