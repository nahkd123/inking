package io.github.nahkd123.inking.api.util;

public record Flags(long raw) {
	public boolean is(Flag flag) {
		return (raw & flag.getFlagBit()) != 0L;
	}
}
