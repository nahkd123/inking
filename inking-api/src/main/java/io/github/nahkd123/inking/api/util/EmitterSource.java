package io.github.nahkd123.inking.api.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * <p>
 * An implementation of {@link Emitter}, with additional method for emitting
 * objects.
 * </p>
 * 
 * @param <T> The type of object.
 */
public class EmitterSource<T> implements Emitter<T> {
	private Set<Consumer<T>> callbacks = new HashSet<>();
	private Set<Consumer<T>> pendingRemoval = new HashSet<>();
	private Set<Consumer<T>> pendingAdd = new HashSet<>();
	private List<T> objectsStream = new ArrayList<>();
	private boolean emitting = false;

	@Override
	public boolean listen(Consumer<T> callback) {
		if (!emitting) return callbacks.add(callback);
		if (pendingRemoval.remove(callback)) return true;
		if (pendingAdd.contains(callback)) return false;
		if (callbacks.contains(callback)) return false;

		pendingAdd.add(callback);
		return true;
	}

	@Override
	public boolean stopListening(Consumer<T> callback) {
		if (!emitting) return callbacks.remove(callback);
		if (pendingAdd.remove(callback)) return true;
		if (pendingRemoval.contains(callback)) return false;
		if (!callbacks.contains(callback)) return false;

		pendingRemoval.add(callback);
		return true;
	}

	public void push(T obj) {
		objectsStream.add(obj);

		if (!emitting) {
			emitting = true;
			while (consumeStream());
			emitting = false;
		}
	}

	private boolean consumeStream() {
		if (objectsStream.size() == 0) return false;
		T head = objectsStream.remove(0);

		for (Consumer<T> callback : callbacks) {
			try {
				callback.accept(head);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		for (Consumer<T> callback : pendingAdd) {
			try {
				callback.accept(head);
			} catch (Exception e) {
				e.printStackTrace();
			}

			callbacks.add(callback);
		}

		for (Consumer<T> callback : pendingRemoval) callbacks.remove(callback);
		pendingRemoval.clear();
		pendingAdd.clear();
		return true;
	}
}
