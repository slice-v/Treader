package tests;

import java.time.LocalDate;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;


public class DateTimeTest {

	static LocalDate locDate1 = LocalDate.of(2021, 9, 2);
	static LocalDate locDate2 = LocalDate.of(2021, 3, 2);
	static LocalTime locTime = LocalTime.now();
	static ZonedDateTime locDateTime = ZonedDateTime.now();
	static ZonedDateTime locDateTime1 = ZonedDateTime.now();
	static ZonedDateTime locDateTime2 = ZonedDateTime.now().minusDays(10);
	
	
	
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		
		//locDate2 = LocalDate.parse("28.08.2021", DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		System.out.println(locDate2.minusDays(1).format(DateTimeFormatter.ofPattern("dd.MM.uuuu")).toString());
		
		locTime = LocalTime.parse("23:00");
		
		System.out.println(locTime);
		
		
		locDateTime = ZonedDateTime.of(locDate2.plusDays(3), locTime, ZoneId.of("Europe/Moscow"));
		System.out.println("Время: " + locTime.format(DateTimeFormatter.ofPattern("hh:mm")).toString());
		System.out.println("Days between two ZonedDateTime: " + ChronoUnit.DAYS.between(locDateTime2, locDateTime1));

	}

}
