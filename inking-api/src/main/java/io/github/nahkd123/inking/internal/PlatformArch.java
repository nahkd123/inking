package io.github.nahkd123.inking.internal;

public enum PlatformArch {
	X86_64("amd64", "x64"),
	ARM64("arm64", "arm64"),
	UNKNOWN("unknown", "unknown");

	private String systemPropValue;
	private String archId;

	private PlatformArch(String systemPropValue, String archId) {
		this.systemPropValue = systemPropValue;
		this.archId = archId;
	}

	public String getSystemPropValue() { return systemPropValue; }

	public String getArchId() { return archId; }

	public static PlatformArch getCurrent() {
		String name = System.getProperty("os.arch");
		for (PlatformArch arch : values()) if (name.equals(arch.systemPropValue)) return arch;
		return UNKNOWN;
	}
}
