package com.capg.campsite.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capg.campsite.dto.BookingDto;
import com.capg.campsite.entity.Booking;
import com.capg.campsite.entity.Campsite;
import com.capg.campsite.exception.BookingDateRangeException;
import com.capg.campsite.exception.BookingNotFoundException;
//
import com.capg.campsite.repository.BookingRepository;
import com.capg.campsite.repository.CampsiteRepository;

@Service
public class BookingServiceImpl implements BookingService {

	@Autowired
	BookingRepository bookingRepository;
	private static final Logger logger =  LoggerFactory.getLogger(BookingServiceImpl.class);

	@Autowired
	CampsiteRepository campsiteRepository;

	@Override
	public List<Booking> getAllBookings() throws BookingNotFoundException {
		List<Booking> bookings = bookingRepository.findAll();
		if (bookings.isEmpty()) {
			throw new BookingNotFoundException("Bookings Not Found in Data Base");
		}
		logger.info("get list of bookings");
		return bookings;
	}

	@Override
	public Booking addBooking(BookingDto booking, int campsiteId) throws BookingDateRangeException, BookingNotFoundException {

		Booking b = new Booking();
		Campsite c = campsiteRepository.findById(campsiteId).orElseThrow(
				() -> new BookingNotFoundException("Campsite with id : " + campsiteId + " Doesn't exists"));
		long days = ChronoUnit.DAYS.between(LocalDate.now(), booking.getArrivalDate());
		System.out.println(days);
		if (days >= 30) {
			b.setArrivalDate(booking.getArrivalDate());
			b.setAvailability(booking.getAvailability());
			b.setBookingDate(LocalDate.now());
			b.setDepartureDate(booking.getDepartureDate());
			b.setCampsite(c);
			logger.info("add booking");
			return bookingRepository.save(b);
		} else {
			throw new BookingDateRangeException("Booking Has not Done before one month");
		}
	}

	@Override
	public Booking getBookingById(long bookingId) throws BookingNotFoundException {
          logger.info("get booking by Id ");
		return bookingRepository.findById(bookingId)
				.orElseThrow(() -> new BookingNotFoundException("Booking not found with id : " + bookingId));
	}

	@Override
	public Booking updateBooking(BookingDto booking, long id) throws BookingNotFoundException {

		Booking b = bookingRepository.findById(id)
				.orElseThrow(() -> new BookingNotFoundException("Booking not found with id : " + id));
		b.setCampsite(null);

		bookingRepository.delete(b);
		Booking existingBooking = new Booking();
		existingBooking.setCampsite(b.getCampsite());
		existingBooking.setArrivalDate(booking.getArrivalDate());
		existingBooking.setAvailability(booking.getAvailability());
		existingBooking.setBookingDate(b.getBookingDate());
		existingBooking.setDepartureDate(booking.getDepartureDate());
        logger.info("update booking");
		return bookingRepository.save(existingBooking);
	}

	@Override
	public void deleteBooking(long bookingId) throws BookingNotFoundException {
		Booking b = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new BookingNotFoundException("Booking not found with id : " + bookingId));
		b.setCampsite(null);
		logger.info("delete booking");
		bookingRepository.delete(b);
	}

	@Override
	public List<Booking> findAvailableBookings(LocalDate arrivalDate, LocalDate departureDate) {
         logger.info("find available booking");
		return bookingRepository.findAll().stream()
				.filter(a -> a.getArrivalDate().equals(arrivalDate) && a.getDepartureDate().equals(departureDate))
				.collect(Collectors.toList());
	}
	
	


}