package com.droste.inspec;

import groovy.util.GroovyTestCase

class TestFindEntryPoints extends GroovyTestCase {
	void testFindEntryPoints() {
		def entries = GConcept.readCsvFile("src/main/resources/googledaily.csv")
		entries.sort { it.date }
		GConcept.findEntryPoints(entries);
	}
}
