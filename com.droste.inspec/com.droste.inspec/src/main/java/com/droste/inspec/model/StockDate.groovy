package com.droste.inspec.model

import java.text.SimpleDateFormat



class StockDate {
	Date date;
	double open, high, low, close
	long volume

	StockDate() {
	}

	StockDate(date, open, high, low, close, volume) {
		setDate(date)
		setOpen(open);
		setHigh(high)
		setLow(low)
		setClose(close)
		setVolume(volume)
	}

	void setDate(String entryDate) {
		entryDate = cleanString(entryDate);
		if (entryDate.matches("\\d-\\S*")) {
			entryDate = 0 + entryDate
		}
		SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yy", Locale.US)
		date = format.parse(entryDate)
	}

	void setOpen(String entry) {
		entry = cleanString(entry)
		open = Double.parseDouble(entry)
	}
	void setHigh(String entry) {
		entry = cleanString(entry)
		high = Double.parseDouble(entry)
	}
	void setLow(String entry) {
		entry = cleanString(entry)
		low = Double.parseDouble(entry)
	}
	void setVolume(String entry) {
		entry = cleanString(entry)
		volume = Integer.parseInt(entry)
	}
	void setClose(String entry) {
		entry = cleanString(entry)
		close = Double.parseDouble(entry)
	}

	String cleanString(String entry) {
		if (entry[0] == '"')
			entry = entry.substring(1, entry.length()-1)
		return entry
	}

	@Override
	public int hashCode() {
		return date.hashCode() + open.hashCode() * 1 + high.hashCode() * 2 + low.hashCode() * 3 + close.hashCode() * 4 + volume
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (obj.hashCode() == hashCode()) return true
		return false
	}

	String toString() 
	{
		return "date " + date + " open " + open + " high " + high + " low " + low + " close " + close + " volume " + volume
	}
}
