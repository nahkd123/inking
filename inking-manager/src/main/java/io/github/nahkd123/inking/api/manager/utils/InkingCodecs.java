package io.github.nahkd123.inking.api.manager.utils;

import java.util.List;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.nahkd123.inking.api.util.MeasurementUnit;
import io.github.nahkd123.inking.api.util.ValueUnit;
import io.github.nahkd123.inking.api.util.Vector2;

public interface InkingCodecs {
	public static Codec<ValueUnit> VALUE_UNIT = Codec.STRING.comapFlatMap(
		s -> {
			try {
				return DataResult.success(ValueUnit.fromString(s));
			} catch (IllegalArgumentException e) {
				return DataResult.error(e::getMessage);
			}
		},
		ValueUnit::toString);

	public static Codec<MeasurementUnit> UNIT = Codec.STRING.comapFlatMap(
		s -> {
			for (MeasurementUnit unit : MeasurementUnit.values()) {
				for (String suffix : unit.getPossibleSuffixes()) {
					if (suffix.isEmpty()) continue;
					if (suffix.equals(s)) return DataResult.success(unit);
				}
			}

			return DataResult.error(() -> "Unknown measurement unit: " + s, MeasurementUnit.UNITLESS);
		},
		unit -> unit.getFullName());

	public static Codec<Vector2> VECTOR_MAP = RecordCodecBuilder.create(instance -> instance.group(
		Codec.DOUBLE.fieldOf("x").forGetter(Vector2::x),
		Codec.DOUBLE.fieldOf("y").forGetter(Vector2::y),
		UNIT.fieldOf("unit").forGetter(Vector2::unit))
		.apply(instance, Vector2::new));

	public static Codec<Vector2> VECTOR_LIST = VALUE_UNIT.listOf().comapFlatMap(
		list -> {
			if (list.size() != 2) return DataResult.error(() -> "Vector must be a list of size 2");
			if (list.get(0).unit() != list.get(1).unit())
				return DataResult.error(() -> "Unit mismatch: " + list.get(0).unit() + " != " + list.get(1).unit());
			return DataResult.success(Vector2.from(list.get(0), list.get(1)));
		},
		vector -> List.of(vector.xUnit(), vector.yUnit()));

	public static Codec<Vector2> VECTOR = Codec.either(VECTOR_MAP, VECTOR_LIST).xmap(
		either -> either.left().orElseGet(either.right()::get),
		vector -> Either.left(vector));
}
