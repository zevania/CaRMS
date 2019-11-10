/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import util.enumeration.ResStatusEnum;
import util.exception.OutletNotFoundException;


public interface ReservationSessionBeanRemote {
public void updateRes(long resId, ResStatusEnum status);

    public long createMemberReservation(Reservation r, long memberId, long ccNum, long pickupId, long returnId, long categoryId, long modelId) throws OutletNotFoundException;

    public void cancelReservation(long resId);

    public List<Reservation> retrieveReservations(String email);

    public boolean searchAvailableCar(String searchType, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, long pickupid, long returnid, long categoryId, long modelId) throws OutletNotFoundException;
    
    public Reservation retrieveReservationById(long resId);
}
