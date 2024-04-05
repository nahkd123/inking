package io.github.nahkd123.inking.api.manager.filtering;

import java.util.function.Consumer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.nahkd123.inking.api.manager.filtering.host.FilterHost;
import io.github.nahkd123.inking.api.manager.utils.MappingGraph;
import io.github.nahkd123.inking.api.tablet.MutablePacket;
import io.github.nahkd123.inking.api.tablet.Packet;

public class PressureMappingFilter extends AbstractTabletFilter {
	private boolean enable;
	private MappingGraph graph;

	public PressureMappingFilter(boolean enable, MappingGraph graph) {
		this.enable = enable;
		this.graph = graph;
	}

	public PressureMappingFilter() {
		this.enable = true;
		this.graph = null; // will be initialized at onInitialize()
	}

	public boolean isEnabled() { return enable; }

	public void setEnable(boolean enable) { this.enable = enable; }

	public MappingGraph getGraph() { return graph; }

	@Override
	public void onInitialize(FilterHost host, Consumer<Packet> receiver) {
		super.onInitialize(host, receiver);

		if (graph == null) {
			graph = new MappingGraph();
			graph.add(0, 0);
			graph.add(host.getTablet().getInfo().getMaxPressure(), host.getTablet().getInfo().getMaxPressure());
		}
	}

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

	public static final Codec<PressureMappingFilter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.BOOL.fieldOf("enable").forGetter(PressureMappingFilter::isEnabled),
		MappingGraph.CODEC.fieldOf("graph").forGetter(PressureMappingFilter::getGraph))
		.apply(instance, PressureMappingFilter::new));

	@Override
	public Codec<? extends TabletFilter> getCodec() { return CODEC; }

	public static void register() {
		TabletFilter.register(
			"inking:pressure_mapping",
			CODEC, PressureMappingFilter::new,
			"Pressure Mapping",
			"Map pen pressure to your own desire!",
			"nahkd123");
	}
}
