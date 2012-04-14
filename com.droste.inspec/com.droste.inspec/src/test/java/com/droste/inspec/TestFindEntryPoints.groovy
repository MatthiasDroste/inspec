package com.droste.inspec;

import groovy.util.GroovyTestCase

import com.droste.inspec.model.StockDate

class TestFindEntryPoints extends GroovyTestCase
{
	void testFindEntryPoints()
	{
		def entries = GConcept.readCsvFile("src/main/resources/googledaily.csv")
		entries.sort
		{ it.date }
		GConcept.findEntryPoints(entries);
	}

	void testGetFiguresOnGoogle()
	{
		def entries = GConcept.readCsvFile("src/main/resources/googledaily.csv")
		def figures = GConcept.getFigures(entries)
		//TODO not working
		//		assert (figures.size() < entries.size())
	}

	void testSimpleFigureHitAtEnd()
	{
		def entries = getEntries()

		def figures = GConcept.getFigures(entries)
		assert (figures.size() == 1)
		assert(figures[0].getStartPoint().getDate() == entries[0].getDate())
		assert(figures[0].getMaxPoint().getDate() == entries[2].getDate())
		assert(figures[0].getCutoff().getDate() == entries[3].getDate())
	}

	/** it's also a figure if there is no decline at all */
	void testSimpleFigureHighAtEnd()
	{
		def entries = getEntries()
		entries[3] = new StockDate("04-Mar-12", "1.30", "1.30", "1.30", "1.30", "40")
		def figures = GConcept.getFigures(entries)
		assert (figures.size() == 2)
		assertEquals(figures[0].getCutoff(), figures[1].getCutoff())
		assertEquals (figures[0].getMaxPoint().getHigh(), 1.30)
		assertEquals (figures[1].getMaxPoint().getHigh(), 1.30)
		assertEquals(figures[1].getStartPoint(), entries[1])
	}

	/** no figure as the last point is as low as the first one -> no gain*/
	void testSimpleFigureLowAtEnd()
	{
		def entries = getEntries()
		entries[3] = new StockDate("04-Mar-12", "1.00", "1.00", "1.00", "1.00", "40")

		def figures = GConcept.getFigures(entries)
		assert (figures.size() == 0)
	}

	/** returns 3 rising points, last declines so that is's 10% higher than the start and 5% lower than the maxpoint => hit*/
	private List getEntries()
	{
		StockDate date1 = new StockDate("01-Mar-12", "1.00", "1.01", "1.00", "1.00", "40")
		StockDate date2 = new StockDate("02-Mar-12", "1.01", "1.11", "1.00", "1.01", "40")
		StockDate date3 = new StockDate("03-Mar-12", "1.21", "1.23", "1.21", "1.21", "40")
		StockDate date4 = new StockDate("04-Mar-12", "1.12", "1.12", "1.12", "1.12", "40")
		def entries = [date1, date2, date3, date4]
		return entries
	}
}