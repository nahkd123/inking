package io.github.nahkd123.inking.internal;

public enum Platform {
	WINDOWS("win", "dll"),
	LINUX("linux", "so"),
	OSX("osx", "dylib"),
	UNKNOWN("unknown", "so");

	private String platformId;
	private String dlExtension;

	private Platform(String platformId, String dlExtension) {
		this.platformId = platformId;
		this.dlExtension = dlExtension;
	}

	public String getPlatformId() { return platformId; }

	public String getDynamicLibExtension() { return dlExtension; }

	public static Platform getCurrent() {
		String name = System.getProperty("os.name");
		if (name.startsWith("Windows ")) return WINDOWS;
		if (name.toLowerCase().equals("linux")) return LINUX;
		if (name.startsWith("Mac OS X")) return OSX;
		return UNKNOWN;
	}
}
