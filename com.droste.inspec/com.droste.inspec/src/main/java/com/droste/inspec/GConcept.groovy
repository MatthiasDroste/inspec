package com.droste.inspec

import com.droste.inspec.model.Figure
import com.droste.inspec.model.StockDate


class GConcept
{

	static Double MINGAIN = 1.2
	static Double MAXLOSS = 0.93
	
	/**
	 * http://www.google.com/finance/historical?cid=12607212&startdate=Mar+7%2C+2010&enddate=Mar+5%2C+2012&num=30",,,,,
	 */
	public static void main(String[] args)
	{

		//Date","Open","High","Low","Close","Volume"
		def entries = readCsvFile("src/main/resources/googledaily.csv")
		def entryPoints = findEntryPoints(entries)
	}

	/** entrypoint == StockDate after that the high course rises for > 10% before the low course falls > 5% from the peak. */
	static List<StockDate> findEntryPoints(List<StockDate> entries)
	{
		def entryPoints = new ArrayList<StockDate>()
		entries.eachWithIndex
		{ item, index ->
			if (index == entries.size()-1) return entryPoints
			Double currentValue = item.getOpen()
//			println("currentValue " + currentValue)
			List afterCurrentPoint = entries[index+1.. entries.size()-1]
			afterCurrentPoint.each
			{
				if (it.getLow() < currentValue * MAXLOSS) println("stop under Value: now " + it.getLow() + " high value was: " + currentValue)
				else if (it.getHigh() > currentValue) currentValue = it.getHigh()
			}
		}
	}

	/** a figure is a set of an entry-point, max-point and cutoff-point where the cutoff is at least 10% over the entry point */
	static List<Figure> getFigures(List<StockDate> entries)
	{
		def figures = new ArrayList<Figure>()
		//steps:
		//for each given entry:
		entries.eachWithIndex { item, index ->
//			println "ENTRY: " + item.getDate()
			if (index == entries.size()-1) return figures
			
			//			for each nextentry after it:
			def afterCurrentPoint = entries[index+1.. entries.size()-1]
		    def newFigure = findFigureForCurrentPoint(item, afterCurrentPoint)
			if (newFigure != null) figures.add(newFigure)
		}
		return figures;
	}
	
	static Figure findFigureForCurrentPoint( StockDate item, List<StockDate> afterCurrentPoint)
	{
		def maxpoint = item
		for (StockDate current : afterCurrentPoint) {
			
//			println "  SubEntry: " + current.getDate()
			//	if nextentry > maxpoint : maxpoint = nextentry
			if (current.getHigh() > maxpoint.getHigh())
			{
				maxpoint = current
//				println "  new maxpoint " + maxpoint.getDate() + " " + maxpoint.getHigh()
			}
			//	if nextentry < maxpoint * 0.95
			if (current.getClose() < maxpoint.getHigh() * MAXLOSS)
			{
//				println "  current.getClose is " + current.getClose() + " smaller than " + maxpoint.getHigh() * MAXLOSS
				//							cutoff = nextentry
				def cutoff = current
				// => wert mit mind 10% gewinn
				if (item != cutoff && cutoff.getClose() > item.getHigh() * MINGAIN)
				{
					return new Figure(item, maxpoint, cutoff)
//					println "    added new figure with items from " + item.getDate() + maxpoint.getDate() + cutoff.getDate()
				} else if (item.getHigh() * MAXLOSS > current.getHigh())
				{
					return null;
				}
				else {
//					println "    no new figure as item != cutoff = " + (item != cutoff) +	" or cutoff.getClose() > item.getHigh() * 1.1 is " + cutoff.getClose() + " > " + item.getHigh() * MINGAIN
				}
			}
			else
			{
//				println "----afterCurrentPoint.indexOf(item) = " + afterCurrentPoint.indexOf(current)
//				println "----afterCurrentPoint.size()-1 " +afterCurrentPoint.size()-1
//				println "----maxPoint.getHigh() " + maxpoint.getHigh()
//				println "----item.getHigh * 1.1 " + item.getHigh() * MINGAIN

				if (afterCurrentPoint.indexOf(current) == afterCurrentPoint.size()-1 && maxpoint.getHigh() > item.getHigh() * MINGAIN)
				{
//					println("    hit at the end")
//					println "    adding new figure with items from " + item.getDate() + maxpoint.getDate() + current.getDate()
					return new Figure(item, maxpoint, current)
				}
			}
		}
	}

	static List<StockDate> readCsvFile(String filename) {
		def entries = new ArrayList<StockDate>()
		def file = new File(filename).splitEachLine(",") { fields ->
			entries.add(new StockDate(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5]))
		}
		def removeList = new ArrayList()
		entries.each {
			if (it.volume == 0) removeList.add(it)
		}
		removeList.each { entries.remove(it) }
		return entries
	}
	
}


