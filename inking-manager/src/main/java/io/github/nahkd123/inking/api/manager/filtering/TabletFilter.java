package io.github.nahkd123.inking.api.manager.filtering;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;

import io.github.nahkd123.inking.api.manager.filtering.host.FilterHost;
import io.github.nahkd123.inking.api.tablet.ImmutablePacket;
import io.github.nahkd123.inking.api.tablet.MutablePacket;
import io.github.nahkd123.inking.api.tablet.Packet;

/**
 * <p>
 * Tablet filters allows user to apply effects/filters to the tablet's packets
 * stream. The filter can cancel the packet, modify the packet or generate new
 * packets based on historical data.
 * </p>
 * <p>
 * <b>Modifications to a single packet</b>: Use
 * {@link MutablePacket#mutableOf(Packet)} for better memory usage. This will
 * returns a new {@link MutablePacket} if the packet is not mutable, or returns
 * the value from parameter if it is mutable.
 * </p>
 * 
 * @param <T> The filter type.
 */
public interface TabletFilter {
	/**
	 * <p>
	 * Called when this filter is being initialized, such as filters chain
	 * initialization or when filters ordering changed.
	 * </p>
	 * <p>
	 * Some notes:
	 * <li>This method must be called before the filter's configuration UI interacts
	 * with this filter. This is required to initialize default values.</li>
	 * <li>TODO</li>
	 * </p>
	 * 
	 * @param host     The filter host.
	 * @param receiver The packet receiver, which allow this filter to push to next
	 *                 packet or to application.
	 */
	public void onInitialize(FilterHost host, Consumer<Packet> receiver);

	/**
	 * <p>
	 * Called when this filter received packet from input device or a filter before
	 * this filter in a chain. A simple filter does do nothing will always push
	 * received packet to receiver (which is obtained from
	 * {@link #onInitialize(FilterHost, Consumer)}).
	 * </p>
	 * <p>
	 * For 1:1 packet modifications, {@link MutablePacket#mutableOf(Packet)} should
	 * be used to reduce the allocation of packets. For 1:x packet modifications,
	 * use {@link MutablePacket#copyFrom(Packet)} or
	 * {@link ImmutablePacket#copyFrom(Packet)}.
	 * </p>
	 * <p>
	 * Note: This method will be called from input thread. Make sure to move to
	 * application thread (like Minecraft rendering thread) if you are interacting
	 * with the application from this packet filter.
	 * </p>
	 * 
	 * @param incoming The incoming packet.
	 */
	public void onPacket(Packet incoming);

	/**
	 * <p>
	 * Get the codec that can be used to serialize or deserialize filter
	 * configurations. The codec should not includes {@code type} as it is used by
	 * registry dispatcher.
	 * </p>
	 * <p>
	 * The codec must be the same as codec that was registered to filters registry.
	 * </p>
	 * 
	 * @return The codec.
	 */
	public Codec<? extends TabletFilter> getCodec();

	/**
	 * <p>
	 * Get the ID of this filter whose codec registered from
	 * {@link #register(String, Codec, Supplier, String, String, String)} method.
	 * </p>
	 * 
	 * @return The ID of this filter, or {@code null} if such filter is not
	 *         registered.
	 */
	default String getId() { return CODECS_MAP.inverse().get(getCodec()); }

	/**
	 * <p>
	 * Get the filter info of this tablet filter. Mainly used for creating new
	 * filter and displaying human-readable information like filter name, filter
	 * description and author.
	 * </p>
	 * 
	 * @return The filter info.
	 */
	default FilterInfo getInfo() { return INFO_MAP.get(getId()); }

	public static record FilterInfo(String id, Supplier<? extends TabletFilter> supplier, String name, String description, String author) {
	}

	public static final BiMap<String, Codec<? extends TabletFilter>> CODECS_MAP = HashBiMap.create();
	public static final Map<String, FilterInfo> INFO_MAP = new HashMap<>();

	/**
	 * <p>
	 * Register a new filter type.
	 * </p>
	 * 
	 * @param id          The ID of the filter. It should be namespaced, like
	 *                    {@code inking:pressure_mapping} for example.
	 * @param codec       The codec, used for deserializing the filter
	 *                    configurations.
	 * @param supplier    The supplier, which returns a new filter with initial
	 *                    configurations.
	 * @param name        The display name of this filter. Use {@code null} to
	 *                    indicate this filter does not have a display name.
	 * @param description The display description of this filter. Use {@code null}
	 *                    to indicate this filter does not have a description.
	 * @param author      The author of this filter. Use {@code null} if there is no
	 *                    author, or you are too shy to show up.
	 * @return {@code true} if the filter is registered successfully.
	 */
	public static <T extends TabletFilter> boolean register(String id, Codec<T> codec, Supplier<T> supplier, String name, String description, String author) {
		if (CODECS_MAP.containsKey(id)) return false;

		CODECS_MAP.put(id, codec);
		INFO_MAP.put(id, new FilterInfo(id, supplier, name, description, author));
		return true;
	}

	/**
	 * <p>
	 * See {@link #register(String, Codec, Supplier, String, String, String)}
	 * </p>
	 * 
	 * @param id       The ID of the filter. It should be namespaced, like
	 *                 {@code inking:pressure_mapping} for example.
	 * @param codec    The codec, used for deserializing the filter configurations.
	 * @param supplier The supplier, which returns a new filter with initial
	 *                 configurations.
	 * @param name     The display name of this filter. Use {@code null} to indicate
	 *                 this filter does not have a display name.
	 * @return {@code true} if the filter is registered successfully.
	 */
	public static <T extends TabletFilter> boolean register(String id, Codec<T> codec, Supplier<T> supplier, String name) {
		return register(id, codec, supplier, name, null, null);
	}

	/**
	 * <p>
	 * See {@link #register(String, Codec, Supplier, String, String, String)}
	 * </p>
	 * 
	 * @param id       The ID of the filter. It should be namespaced, like
	 *                 {@code inking:pressure_mapping} for example.
	 * @param codec    The codec, used for deserializing the filter configurations.
	 * @param supplier The supplier, which returns a new filter with initial
	 *                 configurations.
	 * @return {@code true} if the filter is registered successfully.
	 */
	public static <T extends TabletFilter> boolean register(String id, Codec<T> codec, Supplier<T> supplier) {
		return register(id, codec, supplier, null, null, null);
	}

	/**
	 * <p>
	 * Register default filters. Default filters are not registered by default on
	 * purpose (despite being called "defaults").
	 * </p>
	 */
	public static void registerDefaults() {
		AreaMappingFilter.register();
		PressureMappingFilter.register();
	}

	/**
	 * <p>
	 * Get the codec that can be serialize or deserialize all registered filters.
	 * </p>
	 */
	public static final Codec<TabletFilter> CODEC = Codec.STRING.dispatch("type", TabletFilter::getId, CODECS_MAP::get);
}
