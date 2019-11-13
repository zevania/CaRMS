/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import java.sql.Time;
import java.util.Date;
import java.util.List;
import util.enumeration.ResStatusEnum;
import util.exception.InvalidReservationException;
import util.exception.MemberNotFoundException;
import util.exception.OutletNotFoundException;
import util.exception.ReservationNotFoundException;


public interface ReservationSessionBeanRemote {
    public void updateRes(long resId, ResStatusEnum status)throws ReservationNotFoundException, InvalidReservationException;

    public long createMemberReservation(Reservation r, long memberId, long ccNum, long pickupId, long returnId, long categoryId, long modelId) throws OutletNotFoundException, MemberNotFoundException;

    public String cancelReservation(long resId);

    public List<Reservation> retrieveReservations(String email);

    public boolean searchAvailableCar(String searchType, Date startDate, Date endDate, Time startTime, Time endTime, long pickupid, long returnid, long categoryId, long modelId) throws OutletNotFoundException;
    
    public Reservation retrieveReservationById(long resId);
    
    public void manualAllocateCars(Date date);
}
