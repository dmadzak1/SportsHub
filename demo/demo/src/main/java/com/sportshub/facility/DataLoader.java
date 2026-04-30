package com.sportshub.facility;

import com.sportshub.facility.model.*;
import com.sportshub.facility.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class DataLoader implements CommandLineRunner {

    private final FacilityRepository facilityRepository;
    private final ScheduleRepository scheduleRepository;
    private final ReservationRepository reservationRepository;

    public DataLoader(FacilityRepository facilityRepository,
                      ScheduleRepository scheduleRepository,
                      ReservationRepository reservationRepository) {
        this.facilityRepository = facilityRepository;
        this.scheduleRepository = scheduleRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public void run(String... args) {

        // Kreiranje terena/sadržaja
        Facility tennisCourt1 = facilityRepository.save(new Facility("Teren 1 - Tenis", "TENNIS"));
        Facility tennisCourt2 = facilityRepository.save(new Facility("Teren 2 - Tenis", "TENNIS"));
        Facility pool         = facilityRepository.save(new Facility("Bazen A", "POOL"));
        Facility gym          = facilityRepository.save(new Facility("Teretana", "GYM"));

        // Kreiranje rasporeda (termina)
        LocalDate today    = LocalDate.now();
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        Schedule s1 = scheduleRepository.save(new Schedule(tennisCourt1, today,    LocalTime.of(9,  0)));
        Schedule s2 = scheduleRepository.save(new Schedule(tennisCourt1, today,    LocalTime.of(10, 0)));
        Schedule s3 = scheduleRepository.save(new Schedule(tennisCourt1, today,    LocalTime.of(11, 0)));
        Schedule s4 = scheduleRepository.save(new Schedule(tennisCourt2, today,    LocalTime.of(9,  0)));
        Schedule s5 = scheduleRepository.save(new Schedule(pool,         today,    LocalTime.of(8,  0)));
        Schedule s6 = scheduleRepository.save(new Schedule(pool,         tomorrow, LocalTime.of(8,  0)));
        Schedule s7 = scheduleRepository.save(new Schedule(gym,          today,    LocalTime.of(7,  0)));

        // Kreiranje rezervacija
        reservationRepository.save(new Reservation(1L, s1, ReservationStatus.CONFIRMED));
        reservationRepository.save(new Reservation(2L, s2, ReservationStatus.PENDING));
        reservationRepository.save(new Reservation(1L, s5, ReservationStatus.CONFIRMED));
        reservationRepository.save(new Reservation(3L, s4, ReservationStatus.CANCELLED));
        reservationRepository.save(new Reservation(2L, s6, ReservationStatus.PENDING));
        reservationRepository.save(new Reservation(1L, s7, ReservationStatus.CONFIRMED));

        System.out.println("✅ Facility Service: testni podaci uspješno učitani.");
    }
}
