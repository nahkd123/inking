package io.github.nahkd123.inking.api.manager.info;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.nahkd123.inking.api.manager.utils.InkingCodecs;
import io.github.nahkd123.inking.api.tablet.ButtonType;
import io.github.nahkd123.inking.api.tablet.TabletInfo;
import io.github.nahkd123.inking.api.util.Vector2;

public record ImmutableTabletInfo(String tabletId, String tabletName, int maxPressure, Optional<Vector2> physicalSize, Optional<Vector2> inputSize, int penButtons, int auxButtons) implements TabletInfo {
	public ImmutableTabletInfo(String tabletId, String tabletName, int maxPressure, Vector2 physicalSize, Vector2 inputSize, int penButtons, int auxButtons) {
		this(tabletId, tabletName, maxPressure, Optional.of(physicalSize), Optional
			.of(inputSize), penButtons, auxButtons);
	}

	public ImmutableTabletInfo(String tabletId, String tabletName, int maxPressure, int penButtons, int auxButtons) {
		this(tabletId, tabletName, maxPressure, Optional.empty(), Optional.empty(), penButtons, auxButtons);
	}

	public static final Codec<ImmutableTabletInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.STRING.fieldOf("id").forGetter(ImmutableTabletInfo::tabletId),
		Codec.STRING.fieldOf("name").forGetter(ImmutableTabletInfo::tabletName),
		Codec.INT.fieldOf("maxPressure").forGetter(ImmutableTabletInfo::maxPressure),
		InkingCodecs.VECTOR.optionalFieldOf("physicalSize").forGetter(ImmutableTabletInfo::physicalSize),
		InkingCodecs.VECTOR.optionalFieldOf("inputSize").forGetter(ImmutableTabletInfo::inputSize),
		Codec.INT.fieldOf("penButtons").forGetter(ImmutableTabletInfo::penButtons),
		Codec.INT.fieldOf("auxButtons").forGetter(ImmutableTabletInfo::auxButtons))
		.apply(instance, ImmutableTabletInfo::new));

	/**
	 * <p>
	 * Make a copy of tablet info. Since the tablet info is always a constant, it
	 * should be possible to save the info to storage.
	 * </p>
	 * 
	 * @param info The info to copy from.
	 * @return A new immutable copy.
	 */
	public static ImmutableTabletInfo copyFrom(String tabletId, TabletInfo info) {
		// @formatter:off
		return new ImmutableTabletInfo(
			tabletId,
			info.getTabletName(),
			info.getMaxPressure(),
			info.getPhysicalSize(),
			info.getInputSize(),
			info.getButtonsCount(ButtonType.PEN),
			info.getButtonsCount(ButtonType.AUXILIARY));
		// @formatter:on
	}

	@Override
	public String getTabletName() { return tabletName; }

	@Override
	public int getMaxPressure() { return maxPressure; }

	@Override
	public Optional<Vector2> getPhysicalSize() { return physicalSize; }

	@Override
	public Optional<Vector2> getInputSize() { return inputSize; }

	@Override
	public int getButtonsCount(ButtonType type) {
		return switch (type) {
		case PEN -> penButtons;
		case AUXILIARY -> auxButtons;
		default -> 0;
		};
	}
}
