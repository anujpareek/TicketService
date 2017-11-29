package com.walmart.ticketservice;

/**
 * @author Anuj
 * 
 *         Class that represents a seat object.
 */
public class Seat {

	private final int row;

	private final int column;

	/**
	 * Seat constructor.
	 * 
	 * @param row
	 *            The row this seat is in.
	 * @param column
	 *            The column this seat is in.
	 */
	public Seat(int row, int column) {
		this.row = row;
		this.column = column;
	}

	/**
	 * @return the row
	 */
	public int getRow() {
		return row;
	}

	/**
	 * @return the column
	 */
	public int getColumn() {
		return column;
	}

	@Override
	public String toString() {
		return "Seat [row=" + row + ", column=" + column + "]";
	}

}