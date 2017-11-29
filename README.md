TicketService
=============

## Building

1. Clone the project using the following command
  ```
  git clone https://github.com/anujpareek/TicketService.git
  ```
  
2. Change Directory and build project using the following commands
  ```
  cd TicketService/
  ./gradlew build
  ```
  
3. Run the tests using the following command
  ```
  ./gradlew test
  ```
  
## Assumptions

1. I wasn't sure how I was getting the input for the seating arrangement of the Venue. I assumed it would be similar to the picture, so I decided to just use two numbers (Number of rows and columns) as the input for the venue.

2. It wasn't specified how to choose the best seat. I made the assumption that seats closer to the stage (lower row number) are better and all the seats in the same row are equal.

3. I changed the seatHoldId in the reserveSeats function from int to UUID. I figured it didn't matter if the seatHoldId was an integer as long as it was unique.

4. I set the default seat hold time to be 60 seconds, if the user doesn't reserve the seat by then the hold is invalid. The default seat hold time can be changed if a new default time is provided at the construction time of the venue.
