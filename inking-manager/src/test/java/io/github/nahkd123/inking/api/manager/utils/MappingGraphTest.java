package io.github.nahkd123.inking.api.manager.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MappingGraphTest {
	@Test
	void test() {
		MappingGraph graph = new MappingGraph();
		graph.add(0.5, 1, 1);
		graph.add(0.75, 0.5, 1);
		assertEquals(0, graph.map(-1, 1));
		assertEquals(0, graph.map(0, 1));
		assertEquals(0.5, graph.map(0.25, 1));
		assertEquals(1, graph.map(0.5, 1));
		assertEquals(0.5, graph.map(0.75, 1));
		assertEquals(0.5, graph.map(1, 1));
		assertEquals(0.5, graph.map(2, 1));
	}
}
