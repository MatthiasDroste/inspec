package com.droste.inspec;

import groovy.util.GroovyTestCase

class TestFileReading extends GroovyTestCase {

	void testFileReading() {
		def entries = GConcept.readCsvFile("src/main/resources/googledaily.csv")
		assertEquals(399, entries.size)

		assertEquals(703435, entries[0].getVolume())
		assertEquals(19.00, entries[398].getOpen())

		assertEquals(33.81, entries[2].getClose())
		assertEquals(33.64, entries[2].getOpen())
		assertEquals(34.44, entries[2].getHigh())
		assertEquals(33.17, entries[2].getLow())
		assertEquals(612183, entries[2].getVolume())
		entries.each { assert(it.volume > 0) }
		//		entries.each {
		//			println(it.date.getDateString() + " " + it.open + " " + it.high + " " + it.low + " " + it.close + " " + it.volume)
		//		}
	}
}
