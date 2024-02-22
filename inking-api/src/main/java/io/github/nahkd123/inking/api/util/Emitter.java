package io.github.nahkd123.inking.api.util;

import java.util.function.Consumer;

public interface Emitter<T> {
	public boolean listen(Consumer<T> callback);

	public boolean stopListening(Consumer<T> callback);
}
