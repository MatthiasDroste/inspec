package com.droste.inspec.model;

import groovy.util.GroovyTestCase

class TestStockDate extends GroovyTestCase {

	void testDateParsing() {
		def dateSingle = "1-MAR-12"
		def stockDate = new StockDate()
		stockDate.setDate(dateSingle);
		//		println(stockDate.getDate().getDateString())
		assertEquals("01.03.12", stockDate.getDate().getDateString());
		def doubleQuoteDateSingle = "\"1-Mar-12\""
		stockDate.setDate(doubleQuoteDateSingle)
		assertEquals("01.03.12", stockDate.getDate().getDateString());
	}

	void testCleanString() {
		def doubleQuoteDateSingle = "\"1-Mar-12\""
		def sDate = new StockDate()
		def result = sDate.cleanString(doubleQuoteDateSingle)
		assertEquals("1-Mar-12", result)
	}
}
