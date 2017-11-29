package com.walmart.ticketservice;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * @author Anuj
 *
 *         SeatHold Class, used for holding seats for a customer.
 */
public class SeatHold {

	private final List<Seat> seats;

	private final String email;

	private final UUID seatHoldId;

	private final Instant expireDate;

	/**
	 * SeatHold Constructor.
	 * 
	 * @param seats
	 *            The list of seats this object is holding.
	 * @param email
	 *            The email of the customer for whom the seats are being held.
	 * @param expireDate
	 *            The expireDate/Time for this hold.
	 */
	public SeatHold(List<Seat> seats, String email, Instant expireDate) {
		this.seats = seats;
		this.email = email;
		this.seatHoldId = UUID.randomUUID();
		this.expireDate = expireDate;
	}

	/**
	 * Checks whether this seatHold has expired based on the given instant.
	 * 
	 * @param instant
	 *            The instant to compare the expired time to.
	 * @return True if the the hold has expired, false otherwise.
	 */
	public boolean isExpired(Instant instant) {
		return !expireDate.isAfter(instant);
	}

	/**
	 * @return the seats
	 */
	public List<Seat> getSeats() {
		return seats;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @return the seatHoldId
	 */
	public UUID getSeatHoldId() {
		return seatHoldId;
	}

	@Override
	public String toString() {
		return "SeatHold [seats=" + seats + ", email=" + email + ", seatHoldId=" + seatHoldId + ", expireDate="
				+ expireDate + "]";
	}

}