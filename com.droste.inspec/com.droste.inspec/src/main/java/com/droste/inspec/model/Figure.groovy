package com.droste.inspec.model

class Figure {
	StockDate startPoint, maxPoint, cutoff;
	public Figure(startPoint, maxPoint, cutoff) {
		this.startPoint = startPoint
		this.maxPoint = maxPoint
		this.cutoff = cutoff
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!obj instanceof Figure)	return false
		Figure figObj = (Figure) obj
		if (startPoint.equals(figObj.startPoint) && maxPoint.equals(figObj.maxPoint) && cutoff.equals(figObj.cutoff)) return true

		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		int hashCode = startPoint.hashCode() * 1 + maxPoint.hashCode() * 2 + cutoff.hashCode() * 3
//		println "hash " + hashCode + " for " + toString()
		return hashCode
	} 
	
	@Override
	public String toString() {
		return "startpoint: " + startPoint + " \n maxpoint: " + maxPoint + " \n cutoff: " + cutoff
	}
}
