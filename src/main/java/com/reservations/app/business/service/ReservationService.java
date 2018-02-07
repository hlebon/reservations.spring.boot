package com.reservations.app.business.service;

import com.reservations.app.business.domain.RoomReservation;
import com.reservations.app.data.entity.Guest;
import com.reservations.app.data.repository.GuestRepository;

import com.reservations.app.data.entity.Room;
import com.reservations.app.data.repository.RoomRepository;

import com.reservations.app.data.entity.Reservation;
import com.reservations.app.data.repository.ReservationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReservationService {
    private RoomRepository roomRepository;
    private GuestRepository guestRepository;
    private ReservationRepository reservationRepository;

    @Autowired
    public ReservationService(
            RoomRepository roomRepository,
            GuestRepository guestRepository,
            ReservationRepository reservationRepository) {
        this.roomRepository = roomRepository;
        this.guestRepository = guestRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<RoomReservation> getRoomReservationsForDate(Date date){
        Iterable<Room> rooms = this.roomRepository.findAll();
        Map<Long, RoomReservation> roomReservationMap = new HashMap<>();
        rooms.forEach(room -> {
            RoomReservation roomReservation = new RoomReservation();
            roomReservation.setRoomId(room.getId());
            roomReservation.setRoomNumber(room.getNumber());
            roomReservationMap.put(room.getId(), roomReservation);
        });

        Iterable<Reservation> reservations = this.reservationRepository
                .findByDate(new java.sql.Date(date.getTime()));
        if(reservations != null){
            reservations.forEach(reservation -> {
                Guest guest = this.guestRepository.findOne(reservation.getGuestId());
                if(guest != null){
                    RoomReservation roomReservation = roomReservationMap.get(reservation.getGuestId());
                    roomReservation.setDate(date);
                    roomReservation.setFirstName(guest.getFirst_name());
                    roomReservation.setLastName(guest.getLast_name());
                    roomReservation.setGuestId(guest.getGuest_id());
                }
            });
        }
        List<RoomReservation> roomReservations = new ArrayList<>();
        for(Long roomId:roomReservationMap.keySet()){
            roomReservations.add(roomReservationMap.get(roomId));
        }
        return roomReservations;
    }
}
