package com.droste.inspec;

import java.util.regex.Pattern.First;

import org.junit.Before;

import groovy.util.GroovyTestCase

import com.droste.inspec.model.Figure
import com.droste.inspec.model.StockDate

class TestFindEntryPoints extends GroovyTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		GConcept.setMINGAIN(1.1)
		GConcept.setMAXLOSS(0.95)
	}

	void testFindEntryPoints() {
		def entries = GConcept.readCsvFile("src/main/resources/googledaily.csv")
		entries.sort { it.date }
		GConcept.findEntryPoints(entries);
	}

	void testGetFiguresOnGoogle() {
		def figures = getFiguresOnGoogle()
		println figures.size()
		for (int i = 0; i < 10; i++) {
			println i + " startpoint " + figures[i].getStartPoint()
			println i + " maxpoint " + figures[i].getMaxPoint()
			println i + " cutoff " + figures[i].getCutoff()
		}
		//TODO not working
		//		assert (figures.size() < entries.size())
	}

	private List getFiguresOnGoogle() {
		def entries = GConcept.readCsvFile("src/main/resources/google2004-2012.csv")
//		def entries = GConcept.readCsvFile("src/main/resources/googledaily.csv")
		entries = entries.sort{it.date}
		def figures = GConcept.getFigures(entries)
		return figures
	}

	/** break the search when the value goes down more than 5% after the start */
	void testBreakSearchWhenLosingMoney() {
		def entries = getEntries()
		entries[1] = new StockDate("02-Mar-12", "0.1", "0.11", "0.00", "0.11", "40")
		def figures = GConcept.getFigures(entries)
		//we only get a Figure with ending at high point starting from the second position:
		assertEquals (1, figures.size())
		assertEquals (entries[1], figures[0].getStartPoint())
	}

	void testSimpleFigureHitAtEnd() {
		def entries = getEntries()

		def figures = GConcept.getFigures(entries)
		assert (figures.size() == 1)
		assert(figures[0].getStartPoint().getDate() == entries[0].getDate())
		assert(figures[0].getMaxPoint().getDate() == entries[2].getDate())
		assert(figures[0].getCutoff().getDate() == entries[3].getDate())
	}

	/** it's also a figure if there is no decline at all */
	void testSimpleFigureHighAtEnd() {
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
	void testSimpleFigureLowAtEnd() {
		def entries = getEntries()
		entries[3] = new StockDate("04-Mar-12", "1.00", "1.00", "1.00", "1.00", "40")

		def figures = GConcept.getFigures(entries)
		assert (figures.size() == 0)
	}

	/** returns 3 rising points, last declines so that is's 10% higher than the start and 5% lower than the maxpoint => hit */
	private List getEntries() {
		StockDate date1 = new StockDate("01-Mar-12", "1.00", "1.01", "1.00", "1.00", "40")
		StockDate date2 = new StockDate("02-Mar-12", "1.01", "1.11", "1.00", "1.01", "40")
		StockDate date3 = new StockDate("03-Mar-12", "1.21", "1.23", "1.21", "1.21", "40")
		StockDate date4 = new StockDate("04-Mar-12", "1.12", "1.12", "1.12", "1.12", "40")
		def entries = [date1, date2, date3, date4]
		return entries
	}

	// TODO 1. anscheinend schlägt  der cutoff erst nach erreichen des mingain zu? zwischendrin gabs auf jeden fall verluste, die einen Verkauf bedingt hätten.
	//TODO 2. Schritt: für jeden Entrypoint (der genügend vorlauf hat, ein TechChartData-Objekt berechnen, das den Stand aller möglichen Indikatoren hat.
	//TODO 3. Schritt: Schnittmenge der indikatoren nehmen. 
	//TODO 3. Schritt: Diese gemeinsamen Indikatoren sollten nicht in beliebigen anderen Punkten auftauchen, 
	//TODO speziell nicht in solchen, nach denen man keinen Gewinn machen würde!
	public void testCheckGoldPointSettings() {
		GConcept.setMINGAIN(1.9)
		GConcept.setMAXLOSS(0.95)
		def figures = getFiguresOnGoogle();
		println "figures.size()" + figures.size()
		figures.each {
			println it
		}
	}

	public void testSimplentersect() {

		def list1 = [1, 2, 3, 4, 5]
		def list2 = [3, 4, 5, 6]
		def intersect = list1.intersect(list2)
		println intersect
		assert intersect.size() == 3
		def entries = getEntries();
		def figure1 = new Figure(entries[0], entries[1], entries[2])
		def figure2 = new Figure(entries[2], entries[1], entries[3])
		def figure3 = new Figure(entries[1], entries[2], entries[3])
		def figlist1 = [figure1, figure2]
		def figlist2 = [figure2, figure3]
		def figintersect = figlist1.intersect(figlist2)
		println" hash fig 3 " + figure3.hashCode()
		println" hash fig 2 " + figure2.hashCode()
		println" hash fig 1 " + figure1.hashCode()
		println "equals " + figure2.equals(figure1)
		println "intersect "+ figintersect
		assert figintersect.size() == 1
	}
}