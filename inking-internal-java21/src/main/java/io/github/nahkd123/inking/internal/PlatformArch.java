package io.github.nahkd123.inking.internal;

public enum PlatformArch {
	X86_64(new String[] { "amd64" }, "x64"),
	ARM64(new String[] { "arm64", "aarch64" }, "arm64"),
	UNKNOWN(new String[] { "unknown" }, "unknown");

	private String[] systemPropValues;
	private String archId;

	private PlatformArch(String[] systemPropValues, String archId) {
		this.systemPropValues = systemPropValues;
		this.archId = archId;
	}

	public String[] getSystemPropValues() { return systemPropValues; }

	public String getArchId() { return archId; }

	public static PlatformArch getCurrent() {
		String name = System.getProperty("os.arch");
		for (PlatformArch arch : values()) {
			for (String sysProp : arch.systemPropValues) if (name.equals(sysProp)) return arch;
		}
		return UNKNOWN;
	}
}
