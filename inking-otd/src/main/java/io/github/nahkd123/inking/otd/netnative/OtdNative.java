package io.github.nahkd123.inking.otd.netnative;

import java.lang.foreign.AddressLayout;
import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.file.Path;
import java.util.function.BiConsumer;

import io.github.nahkd123.inking.internal.PlatformUtils;

public class OtdNative {
	private Linker linker;
	private SymbolLookup lib;

	private MethodHandle initializeDriverHandle;
	private MethodHandle getTabletInfoHandle;
	private Arena arena;

	public OtdNative(Linker linker, SymbolLookup lib, Arena arena) {
		this.linker = linker;
		this.lib = lib;
		this.arena = arena;

		initializeDriverHandle = linker.downcallHandle(
			lib.find("initialize_driver").get(),
			FunctionDescriptor.of(
				ValueLayout.JAVA_BOOLEAN,
				ValueLayout.ADDRESS,
				ValueLayout.ADDRESS));
		getTabletInfoHandle = linker.downcallHandle(
			lib.find("get_tablet_info").get(),
			FunctionDescriptor.of(
				ValueLayout.JAVA_BOOLEAN,
				stringLayout(),
				ValueLayout.ADDRESS.withTargetLayout(OtdTabletSpec.layout())));
	}

	/**
	 * <p>
	 * Attempt to find native library and load it as {@link OtdNative}. This will
	 * attempts to copy native library from JAR file to a directory.
	 * </p>
	 * 
	 * @param copyDest Copy destination.
	 * @param linker   The native linker.
	 * @param arena    The arena.
	 * @return The library, or {@code null} if no suitable library can be found.
	 */
	public static OtdNative findNative(Path copyDest, Linker linker, Arena arena) {
		ClassLoader clsLoader = OtdNative.class.getClassLoader();
		SymbolLookup lib = PlatformUtils.loadLibrary("Inking.Otd", clsLoader, copyDest, arena);
		return new OtdNative(linker, lib, arena);
	}

	protected static AddressLayout stringLayout() {
		return ValueLayout.ADDRESS.withTargetLayout(MemoryLayout.sequenceLayout(ValueLayout.JAVA_BYTE));
	}

	public Linker getLinker() { return linker; }

	public SymbolLookup getLib() { return lib; }

	public Arena getArena() { return arena; }

	public void initializeDriver(BiConsumer<String, Boolean> connectStateCallback, BiConsumer<String, OtdPacket> packetCallback) {
		try {
			MethodHandle connectFuncHandle = MethodHandles.lookup().bind(
				(ConnectStateCallbackBridge) (serialStr, connect) -> {
					String serial = serialStr.getUtf8String(0L);
					connectStateCallback.accept(serial, connect);
				}, "onConnect", MethodType.methodType(Void.TYPE, MemorySegment.class, boolean.class));
			MethodHandle packetFuncHandle = MethodHandles.lookup().bind(
				(PacketCallbackBridge) (serialStr, packetMemory) -> {
					String serial = serialStr.getUtf8String(0L);
					OtdPacket packet = new OtdPacket(packetMemory, System.nanoTime());
					packetCallback.accept(serial, packet);
				}, "onPacket", MethodType.methodType(Void.TYPE, MemorySegment.class, MemorySegment.class));

			MemorySegment connectFunc = linker.upcallStub(
				connectFuncHandle,
				FunctionDescriptor.ofVoid(stringLayout(), ValueLayout.JAVA_BOOLEAN),
				arena);
			MemorySegment packetFunc = linker.upcallStub(
				packetFuncHandle,
				FunctionDescriptor.ofVoid(stringLayout(), OtdPacket.layout()),
				arena);

			boolean result = (boolean) initializeDriverHandle.invoke(connectFunc, packetFunc);
			if (!result) throw new RuntimeException("Driver is already initialized or failed to initialize");
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public OtdTabletSpec getTabletInfo(String serial) {
		try {
			MemorySegment serialStr = arena.allocateUtf8String(serial);
			MemorySegment specPtr = arena.allocate(OtdTabletSpec.layout());
			boolean result = (boolean) getTabletInfoHandle.invoke(serialStr, specPtr);
			if (!result) return null;
			return new OtdTabletSpec(specPtr);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@FunctionalInterface
	private static interface ConnectStateCallbackBridge {
		void onConnect(MemorySegment serialStr, boolean connected);
	}

	@FunctionalInterface
	private static interface PacketCallbackBridge {
		void onPacket(MemorySegment serialStr, MemorySegment packet);
	}
}
