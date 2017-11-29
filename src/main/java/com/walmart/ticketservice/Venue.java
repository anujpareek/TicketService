package com.walmart.ticketservice;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.base.Preconditions;

/**
 * @author Anuj
 * 
 *         This class represents a high-demand performance venue and implements
 *         TicketService.
 */
public class Venue implements TicketService {

	/**
	 * Default hold time for testing the seats in seconds. 60 seconds.
	 */
	private static final int DEFAULT_HOLD_TIME = 60;

	private final boolean[][] venueSeats;

	private final Map<UUID, SeatHold> seatHolds;

	private final Map<String, SeatHold> reservedSeats;

	private final int numberOfSeats;

	private int reservedCount;

	private final int holdTime;

	/**
	 * Constructor for the Venue. Uses a default hold time of 1 minute.
	 * 
	 * @param rows
	 *            The number of row at the venue.
	 * @param columns
	 *            The number of columns at the venue.
	 */
	public Venue(int rows, int columns) {
		this(rows, columns, DEFAULT_HOLD_TIME);
	}

	/**
	 * Constructor for the Venue.
	 * 
	 * @param rows
	 *            The number of row at the venue.
	 * @param columns
	 *            The number of columns at the venue.
	 * @param holdTimeInSeconds
	 *            the maximum amount of time someone can hold the seat for.
	 */
	public Venue(int rows, int columns, int holdTimeInSeconds) {
		Preconditions.checkArgument(rows > 0, "The number of rows must be greater than 0");
		Preconditions.checkArgument(columns > 0, "The number of columns must be greater than 0");
		venueSeats = new boolean[rows][columns];
		this.seatHolds = new HashMap<UUID, SeatHold>();
		this.reservedSeats = new HashMap<String, SeatHold>();
		this.numberOfSeats = rows * columns;
		this.reservedCount = 0;
		this.holdTime = holdTimeInSeconds;
	}

	@Override
	public int numSeatsAvailable() {
		Instant now = Instant.now();
		return numSeatsAvailable(now);
	}

	int numSeatsAvailable(Instant instant) {
		int count = numberOfSeats;
		updateHolds(instant);
		synchronized (this) {
			count -= reservedCount;
		}
		return count;
	}

	@Override
	public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {
		Preconditions.checkArgument(numSeats > 0, "The number of seats to hold must be greater than 0");
		Preconditions.checkArgument(customerEmail != null && !customerEmail.isEmpty(),
				"customerEmail cannot be null or empty.");

		Instant now = Instant.now();
		if (numSeats > numSeatsAvailable(now)) {
			return null;
		}

		List<Seat> savedSeats = findBestAvailableSeats(numSeats);

		SeatHold seatHold = new SeatHold(savedSeats, customerEmail, now.plusSeconds(holdTime));
		addSeatHold(seatHold);

		return seatHold;
	}

	/**
	 * Add the given seat hold to the map.
	 * 
	 * @param seatHold
	 *            The seatHold to add.
	 */
	private synchronized void addSeatHold(SeatHold seatHold) {
		seatHolds.put(seatHold.getSeatHoldId(), seatHold);
	}

	/**
	 * Remove the given seat hold from the map. Used when the hold turn into a
	 * reservation.
	 * 
	 * @param seatHoldId
	 *            The if of the seatHold.
	 * @return The seatHold object that was removed.
	 */
	private synchronized SeatHold removeSeatHold(UUID seatHoldId) {
		return seatHolds.remove(seatHoldId);
	}

	/**
	 * Removes all holds that have expired.
	 * 
	 * @param instant
	 *            The time instant to compare the seatHold expiration time with.
	 */
	private synchronized void updateHolds(Instant instant) {
		seatHolds.values().removeIf(entry -> {
			boolean expired = entry.isExpired(instant);
			if (expired) {
				unHoldSeats(entry.getSeats());
			}
			return expired;
		});
	}

	/**
	 * Finds the best available seats, which are not reserved or currently on hold.
	 * 
	 * @param numSeats
	 *            Number of available seats to find.
	 * @return List of available seats.
	 */
	private synchronized List<Seat> findBestAvailableSeats(int numSeats) {
		List<Seat> savedSeats = new ArrayList<>();
		for (int i = 0; i < venueSeats.length; i++) {
			for (int j = 0; j < venueSeats[i].length; j++) {
				if (!venueSeats[i][j]) {
					Seat seat = new Seat(i, j);
					savedSeats.add(seat);
					if (savedSeats.size() == numSeats) {
						holdOrReserveSeats(savedSeats);
						break;
					}
				}
			}
			if (savedSeats.size() == numSeats) {
				break;
			}
		}
		return savedSeats;
	}

	/**
	 * Reserve the given list of seats.
	 * 
	 * @param seats
	 *            The seats to hold/reserve.
	 */
	private synchronized void holdOrReserveSeats(List<Seat> seats) {
		for (Seat seat : seats) {
			venueSeats[seat.getRow()][seat.getColumn()] = true;
		}
		reservedCount += seats.size();
	}

	/**
	 * Unhold the given list of seats.
	 * 
	 * @param seats
	 *            The seats to unhold.
	 */
	private synchronized void unHoldSeats(List<Seat> seats) {
		for (Seat seat : seats) {
			venueSeats[seat.getRow()][seat.getColumn()] = false;
		}
		reservedCount -= seats.size();
	}

	@Override
	public String reserveSeats(UUID seatHoldId, String customerEmail) {
		Preconditions.checkArgument(seatHoldId != null, "seatHoldId cannot be null");
		Preconditions.checkArgument(customerEmail != null && !customerEmail.isEmpty(),
				"customerEmail cannot be null or empty.");

		Instant now = Instant.now();
		SeatHold seatHold = getSeatHoldFromId(seatHoldId);
		if (!seatHold.getEmail().equals(customerEmail) || seatHold.isExpired(now)) {
			return null;
		}
		return reserveSeats(seatHold);
	}

	/**
	 * Get seatHold from the given seat hold id.
	 * 
	 * @param seatHoldId
	 *            The id of the seatHold to get.
	 * @return Returns the seatHold associated with the id. Null if no such seatHold
	 *         is found.
	 */
	private synchronized SeatHold getSeatHoldFromId(UUID seatHoldId) {
		return seatHolds.get(seatHoldId);
	}

	/**
	 * Move the seats from hold to reserved.
	 * 
	 * @param seatHold
	 *            The seatHold to reserve.
	 * @return The confirmationId of the reservation.
	 */
	private String reserveSeats(SeatHold seatHold) {
		removeSeatHold(seatHold.getSeatHoldId());
		String confirmationId = seatHold.getSeatHoldId().toString();
		reservedSeats.put(confirmationId, seatHold);
		return confirmationId;
	}

}
