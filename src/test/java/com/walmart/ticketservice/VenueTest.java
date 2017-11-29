package com.walmart.ticketservice;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Anuj
 *
 *         Test Class for Venue and Ticket Service.
 */
public class VenueTest {

	/**
	 * Default hold time for testing the seats in seconds. 10 seconds.
	 */
	private static final int DEFAULT_HOLD_TIME = 10;

	private static final String TEST_EMAIL = "TestEmail@gmail.com";

	private static final int NUMBER_OF_ROWS = 10;

	private static final int NUMBER_OF_COLUMNS = 50;

	private static final int TOTAL_NUMBER_OF_SEATS = NUMBER_OF_ROWS * NUMBER_OF_COLUMNS;

	private Venue venue;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() {
		this.venue = new Venue(NUMBER_OF_ROWS, NUMBER_OF_COLUMNS, DEFAULT_HOLD_TIME);
	}

	@Test
	public void testNumSeatsAvailable() {
		assertEquals(TOTAL_NUMBER_OF_SEATS, venue.numSeatsAvailable());
		assertNotNull(venue.findAndHoldSeats(1, TEST_EMAIL));
		assertEquals(TOTAL_NUMBER_OF_SEATS - 1, venue.numSeatsAvailable());
		assertEquals(TOTAL_NUMBER_OF_SEATS, venue.numSeatsAvailable(Instant.now().plusSeconds(DEFAULT_HOLD_TIME)));
	}

	@Test
	public void testFindAndHoldSeats() {
		// Test hold with 0 or less seats
		try {
			venue.findAndHoldSeats(0, TEST_EMAIL);
		} catch (IllegalArgumentException e) {
			// DO NOTHING.
		}

		// Test find and hold with more seats than total number of seats
		assertNull(venue.findAndHoldSeats(TOTAL_NUMBER_OF_SEATS + 1, TEST_EMAIL));

		// Test find and hold with a 1 seat.
		SeatHold seatHold = venue.findAndHoldSeats(1, TEST_EMAIL);
		assertNotNull(seatHold);
		assertEquals(TOTAL_NUMBER_OF_SEATS - 1, venue.numSeatsAvailable());

		// Test find and hold with the remaining seats.
		seatHold = venue.findAndHoldSeats(TOTAL_NUMBER_OF_SEATS - 1, TEST_EMAIL);
		assertNotNull(seatHold);
		assertEquals(0, venue.numSeatsAvailable());

		// See if the holds expired after the default_hold_time
		Instant nowPlusHoldTime = Instant.now().plusSeconds(DEFAULT_HOLD_TIME);
		assertEquals(TOTAL_NUMBER_OF_SEATS, venue.numSeatsAvailable(nowPlusHoldTime));
	}

	@Test
	public void testReserveSeats() {
		// Hold and reserve seats
		SeatHold seatHold = venue.findAndHoldSeats(5, TEST_EMAIL);
		venue.reserveSeats(seatHold.getSeatHoldId(), TEST_EMAIL);
		assertEquals(TOTAL_NUMBER_OF_SEATS - 5, venue.numSeatsAvailable());

		// Check if the seats are still reserved after the hold_time
		assertEquals(TOTAL_NUMBER_OF_SEATS - 5, venue.numSeatsAvailable(Instant.now().plusSeconds(DEFAULT_HOLD_TIME)));

		// Hold and reserve with different emails
		SeatHold seatHold2 = venue.findAndHoldSeats(1, TEST_EMAIL);
		assertNull(venue.reserveSeats(seatHold2.getSeatHoldId(), "IncorrectEmail"));
	}

}