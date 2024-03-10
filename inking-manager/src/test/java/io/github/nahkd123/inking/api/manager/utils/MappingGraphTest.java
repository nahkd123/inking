package io.github.nahkd123.inking.api.manager.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MappingGraphTest {
	@Test
	void test() {
		MappingGraph graph = new MappingGraph();
		graph.add(512, 1024);
		graph.add(1024, 512);
		assertEquals(0, graph.map(0, 1024));
		assertEquals(1024, graph.map(512, 1024));
		assertEquals(768, graph.map(768, 1024));
		assertEquals(512, graph.map(1024, 1024));
	}
}
