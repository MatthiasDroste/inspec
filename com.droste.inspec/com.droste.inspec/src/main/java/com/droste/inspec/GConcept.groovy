package com.droste.inspec

import com.droste.inspec.model.Figure
import com.droste.inspec.model.StockDate


class GConcept
{
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
			println("currentValue " + currentValue)
			List afterCurrentPoint = entries[index+1.. entries.size()-1]
			afterCurrentPoint.each
			{
				if (it.getLow() < currentValue * 0.95) println("stop under Value: now " + it.getLow() + " high value was: " + currentValue)
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
			println "ENTRY: " + item.getDate()
			if (index == entries.size()-1) return figures
			//			for each nextentry after it:
			def maxpoint = item
			def afterCurrentPoint = entries[index+1.. entries.size()-1]
			afterCurrentPoint.each {
				println "  SubEntry: " + it.getDate()
				//	if nextentry > maxpoint : maxpoint = nextentry
				if (it.getHigh() > maxpoint.getHigh())
				{
					maxpoint = it
					println "  new maxpoint " + maxpoint.getDate() + " " + maxpoint.getHigh()
				}
				//	if nextentry < maxpoint * 0.95
				if (it.getClose() < maxpoint.getHigh() * 0.95)
				{
					println "  it.getClose is " + it.getClose() + " smaller than " + maxpoint.getHigh() * 0.95
					//							cutoff = nextentry
					def cutoff = it
					// => wert mit mind 10% gewinn
					if (item != cutoff && cutoff.getClose() > item.getHigh() * 1.1)
					{
						figures.add(new Figure(item, maxpoint, cutoff))
						println "    added new figure with items from " + item.getDate() + maxpoint.getDate() + cutoff.getDate()
					} else {
						println "    no new figure as item != cutoff = " + (item != cutoff) +	" or cutoff.getClose() > item.getHigh() * 1.1 is " + cutoff.getClose() + " > " + item.getHigh() * 1.1
					}
				}
				else
				{
					println "----afterCurrentPoint.indexOf(item) = " + afterCurrentPoint.indexOf(it)
					println "----afterCurrentPoint.size()-1 " +afterCurrentPoint.size()-1
					println "----maxPoint.getHigh() " + maxpoint.getHigh()
					println "----item.getHigh * 1.1 " + item.getHigh() * 1.1

					if (afterCurrentPoint.indexOf(it) == afterCurrentPoint.size()-1 && maxpoint.getHigh() > item.getHigh() * 1.1)
					{
						println("    hit at the end")
						figures.add(new Figure(item, maxpoint, it))
						println "    added new figure with items from " + item.getDate() + maxpoint.getDate() + it.getDate()
					}
				}
			}
		}
		return figures;
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


