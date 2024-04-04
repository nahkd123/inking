package io.github.nahkd123.inking.otd.sample;

import java.util.Scanner;

public class SamplePicker {
	private static interface MainFunction {
		public void main(String[] args) throws Throwable;
	}

	private static record MainEntry(String name, MainFunction function) {
	}

	private static final MainEntry[] MAINS = {
		new MainEntry("Tablets list", TabletsListMain::main),
		new MainEntry("Tablet packet tracking", TabletPacketMain::main),
		new MainEntry("Tablet polling rate", TabletPollingRateMain::main)
	};

	public static void main(String[] args) throws Throwable {
		System.out.println("Inking OpenTabletDriver bridge: Samples picker");
		System.out.println("Available samples:");
		for (int i = 0; i < MAINS.length; i++) System.out.println(i + ". " + MAINS[i].name);

		System.out.print("Enter number to pick: ");

		try (Scanner scanner = new Scanner(System.in)) {
			int val = scanner.nextInt();
			if (val < 0 || val >= MAINS.length) throw new IllegalArgumentException("Invalid sample index: " + val);
			MAINS[val].function.main(args);
		}
	}
}
