package io.github.nahkd123.inking.api.manager.filtering;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.nahkd123.inking.api.manager.filtering.host.FilterHost;
import io.github.nahkd123.inking.api.manager.filtering.host.HostHasSize;
import io.github.nahkd123.inking.api.tablet.MutablePacket;
import io.github.nahkd123.inking.api.tablet.Packet;
import io.github.nahkd123.inking.api.tablet.Tablet;
import io.github.nahkd123.inking.api.util.MeasurementUnit;
import io.github.nahkd123.inking.api.util.Vector2;

public class AreaMappingFilter extends AbstractTabletFilter {
	private boolean enable;
	private boolean letterboxing;
	private InputRectangle inputRectangle;

	public static record InputRectangle(double x, double y, double width, double height) {
	}

	public AreaMappingFilter(boolean enable, boolean letterboxing, InputRectangle inputRectangle) {
		this.enable = enable;
		this.letterboxing = letterboxing;
		this.inputRectangle = inputRectangle;
	}

	public AreaMappingFilter(Tablet tablet) {
		this.enable = true;
		this.letterboxing = true;
		this.inputRectangle = tablet.getInfo().getInputSize()
			.map(size -> new InputRectangle(0, 0, size.x(), size.y()))
			.orElseGet(() -> new InputRectangle(0, 0, 640, 360));
	}

	public AreaMappingFilter() {
		this.enable = true;
		this.letterboxing = true;
		this.inputRectangle = null;
	}

	@Override
	public void onInitialize(FilterHost host, Consumer<Packet> receiver) {
		super.onInitialize(host, receiver);

		if (inputRectangle == null) {
			Optional<Vector2> inputSize = host.getTablet().getInfo().getInputSize();
			inputRectangle = inputSize.isPresent()
				? new InputRectangle(0, 0, inputSize.get().x(), inputSize.get().y())
				: new InputRectangle(0, 0, 640, 360); // The UI will not display area mapper anyways
		}
	}

	public boolean isEnabled() { return enable; }

	public void setEnable(boolean enable) { this.enable = enable; }

	public boolean isLetterboxing() { return letterboxing; }

	public void setLetterboxing(boolean letterboxing) { this.letterboxing = letterboxing; }

	public InputRectangle getInputRectangle() { return inputRectangle; }

	public void setInputRectangle(InputRectangle inputRectangle) { this.inputRectangle = inputRectangle; }

	@Override
	public void onPacket(Packet incoming) {
		Vector2 penPosition = incoming.getPenPosition();

		if (penPosition.unit() == MeasurementUnit.PIXEL ||
			!enable ||
			!(getHost() instanceof HostHasSize sized) ||
			getHost().getTablet().getInfo().getInputSize().isEmpty()) {
			push(incoming);
			return;
		}

		Vector2 hostSize = sized.getHostSize();
		MutablePacket mutable = MutablePacket.mutableOf(incoming);
		double areaX = inputRectangle.x, areaY = inputRectangle.y;
		double areaW = inputRectangle.width, areaH = inputRectangle.height;

		if (letterboxing) {
			double areaRatio = inputRectangle.width / inputRectangle.height;
			double hostRatio = hostSize.x() / hostSize.y();

			if (areaRatio > hostRatio) {
				// If the area is wider than the host
				areaW = areaH / hostRatio;
				areaX += (inputRectangle.width - areaW) / 2d;
			} else {
				// If the host is wider than the area
				areaH = areaW / hostRatio;
				areaY += (inputRectangle.height - areaH) / 2d;
			}
		}

		double hostX = (penPosition.x() - areaX) * hostSize.x() / areaW;
		double hostY = (penPosition.y() - areaY) * hostSize.y() / areaH;
		mutable.setPenPosition(new Vector2(hostX, hostY, hostSize.unit()));
		push(mutable);
	}

	private static final Codec<InputRectangle> INPUT_RECT_CODEC = Codec.DOUBLE.listOf().comapFlatMap(
		list -> list.size() != 4
			? DataResult.error(() -> "Input rectangle must be a list of 4 numbers")
			: DataResult.success(new InputRectangle(list.get(0), list.get(1), list.get(2), list.get(3))),
		rect -> List.of(rect.x, rect.y, rect.width, rect.height));

	public static final Codec<AreaMappingFilter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.BOOL.fieldOf("enable").forGetter(AreaMappingFilter::isEnabled),
		Codec.BOOL.fieldOf("letterboxing").forGetter(AreaMappingFilter::isLetterboxing),
		INPUT_RECT_CODEC.fieldOf("inputRectangle").forGetter(AreaMappingFilter::getInputRectangle))
		.apply(instance, AreaMappingFilter::new));

	@Override
	public Codec<? extends TabletFilter> getCodec() { return CODEC; }

	public static void register() {
		TabletFilter.register(
			"inking:area_mapping",
			CODEC, AreaMappingFilter::new,
			"Area Mapping",
			"Map pen position to your screen!",
			"nahkd123");
	}
}
