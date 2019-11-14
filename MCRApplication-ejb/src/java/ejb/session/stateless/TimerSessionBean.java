/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Car;
import entity.DriverDispatchRecord;
import entity.Outlet;
import entity.Reservation;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.CarStatusEnum;
import util.enumeration.DispatchStatusEnum;

/**
 *
 * @author User
 */
@Stateless
@LocalBean
public class TimerSessionBean {

    @EJB
    private TransitDriverDispatchRecordSessionBeanLocal transitDriverDispatchRecordSessionBean;

    @PersistenceContext(unitName = "MCRApplication-ejbPU")
    private EntityManager em;
    
    
    
    @Schedule(dayOfWeek = "Mon-Sun", month = "*", hour = "1", dayOfMonth = "*", year = "*", minute = "0", second = "0")
    public void AllocateCars() {
        Query query = em.createQuery("SELECT o FROM Outlet o");
        List<Outlet> outlet = query.getResultList();
        Outlet o;
        List<Reservation> tempReservation;
        Reservation temp;
        Car c;
        List<Car> cars;
        List<Reservation> toRemove = new LinkedList<>();
        long theStoreId;
        Calendar cal = Calendar.getInstance();
        Date todayDate = cal.getTime();
        DriverDispatchRecord ddr;
        long id;
        
        for(int i = 0; i < outlet.size(); i++){
            o = outlet.get(i);
            theStoreId = o.getOutletId();
            toRemove.clear();
            
            query = em.createQuery("SELECT r FROM Reservation r "
                    + "WHERE r.orderType = util.enumeration.OrderTypeEnum.MODEL AND r.pickupLocation.outletId = :store AND r.pickupDate = :inTodayDate")
                    .setParameter("store", theStoreId)
                    .setParameter("inTodayDate", todayDate);
            tempReservation = query.getResultList();
            
            if(tempReservation.size()==0) continue;
            cars = o.getCars();
            
            for(int j = 0; j < tempReservation.size();j++){
                temp = tempReservation.get(j);
                for(int k = 0; k < cars.size();k++){
                    c = cars.get(k);
                    if(c.getModel().getModelId() == temp.getCarModel().getModelId() &&
                       c.getReservation()==null && c.isActive()){
                        c.setReservation(temp);
                        temp.setCar(c);
                        toRemove.add(temp);
                        cars.remove(c);
                        break;
                    } 
                }
            }
            
            
            for(Reservation r: toRemove){
                tempReservation.remove(r);
            }
            
            if(tempReservation.size()==0) continue;
            
            toRemove.clear();
            
            query = em.createQuery("SELECT c FROM Car c WHERE c.active = TRUE AND c.reservation.returnLocation.outletId = :store")
                    .setParameter("store",theStoreId);
            cars = query.getResultList();
            
            Date temptime;
            for(int j = 0; j < tempReservation.size();j++){
                temp = tempReservation.get(j);
                temptime = temp.getPickupTime();
                for(int k = 0; k < cars.size();k++){
                    c = cars.get(k);
                    if(c.isActive() && c.getReservation()!=null &&c.getModel().getModelId() == temp.getCarModel().getModelId() &&
                        c.getReservation().getReturnDate().equals(temp.getPickupDate()) &&
                        (c.getReservation().getReturnTime().before(temptime) || c.getReservation().getReturnTime().equals(temptime))){
                        c.setReservation(temp);
                        temp.setCar(c);
                        cars.remove(c);
                        toRemove.add(temp);
                        break;
                    } 
                }
            }
            
            for(Reservation r: toRemove){
                tempReservation.remove(r);
            }
            if(tempReservation.size()==0) continue;
            
            toRemove.clear();
            
            query = em.createQuery("SELECT c FROM Car c WHERE c.active = TRUE");
            cars = query.getResultList();
            
            for(int j = 0; j < tempReservation.size();j++){
                temp = tempReservation.get(j);
                for(int k = 0; k < cars.size();k++){
                    c = cars.get(k);
                    if(c.getModel().getModelId() == temp.getCarModel().getModelId() &&
                        c.getReservation()==null && c.isActive()){
                        c.setReservation(temp);
                        temp.setCar(c);
                        toRemove.add(temp);
                        cars.remove(c);
                        Date pickTime = new Date(2000,1,1,temp.getPickupTime().getHours()-2,temp.getPickupTime().getMinutes(),temp.getPickupTime().getSeconds());
                        ddr = new DriverDispatchRecord(DispatchStatusEnum.NOTCOMPLETED, temp.getPickupDate(), pickTime , c.getReservation().getReturnLocation().getName());
                        id = transitDriverDispatchRecordSessionBean.createDispatchRecord(ddr, temp.getReservationId(), theStoreId);
                        break;
                    } 
                }
            }
            
            for(Reservation r: toRemove){
                tempReservation.remove(r);
            }
            if(tempReservation.size()==0) continue;
            
            toRemove.clear();
            
            query = em.createQuery("SELECT c FROM Car c WHERE c.active = TRUE");
            cars = query.getResultList();
            
            for(int j = 0; j < tempReservation.size();j++){
                temp = tempReservation.get(j);
                
                temptime = new Date(2000,1,1,temp.getPickupTime().getHours()-2,temp.getPickupTime().getMinutes(),temp.getPickupTime().getSeconds());        
                for(int k = 0; k < cars.size();k++){
                    c = cars.get(k);
                    if( c.isActive() && c.getReservation()!=null &&c.getModel().getModelId() == temp.getCarModel().getModelId() &&
                        c.getReservation().getReturnDate().equals(temp.getPickupDate()) &&
                        (c.getReservation().getReturnTime().before(temptime) || c.getReservation().getReturnTime().equals(temptime))){
                        c.setReservation(temp);
                        temp.setCar(c);
                        cars.remove(c);
                        toRemove.add(temp);
                        Date pickTime = new Date(2000,1,1,temp.getPickupTime().getHours()-2,temp.getPickupTime().getMinutes(),temp.getPickupTime().getSeconds());
                        ddr = new DriverDispatchRecord(DispatchStatusEnum.NOTCOMPLETED, temp.getPickupDate(), pickTime , c.getReservation().getReturnLocation().getName());
                        id = transitDriverDispatchRecordSessionBean.createDispatchRecord(ddr, temp.getReservationId(), theStoreId);
                        break;
                    } 
                }
            }
            for(Reservation r: toRemove){
                tempReservation.remove(r);
            }
            if(tempReservation.size()==0) continue;
            
        }
        
        for(int i = 0; i < outlet.size(); i++){
            o = outlet.get(i);
            theStoreId = o.getOutletId();
            toRemove.clear();
            System.out.println("hereloo");
            query = em.createQuery("SELECT r FROM Reservation r "
                    + "WHERE r.pickupLocation.outletId = :store AND r.orderType = util.enumeration.OrderTypeEnum.CATEGORY  AND r.pickupDate = :inTodayDate ")
                    .setParameter("store", theStoreId)
                    .setParameter("inTodayDate",todayDate);
            tempReservation = query.getResultList();
            cars = o.getCars();
            System.out.println("the total r is "+tempReservation.size()+" "+todayDate);
            if(tempReservation.size()==0) continue;
            
            for(int j = 0; j < tempReservation.size();j++){
                temp = tempReservation.get(j);
                for(int k = 0; k < cars.size();k++){
                    c = cars.get(k);
                    if(c.getModel().getCategory().getCategoryId() == temp.getCarCategory().getCategoryId() &&
                       c.getReservation()==null && c.isActive()){
                        c.setReservation(temp);
                        temp.setCar(c);
                        toRemove.add(temp);
                        cars.remove(c);
                        System.out.println("masuk pak");
                        break;
                    } 
                }
            }
            
            
            for(Reservation r: toRemove){
                tempReservation.remove(r);
            }
            System.out.println("got ini");
            if(tempReservation.size()==0) continue;
            
            toRemove.clear();
            
            query = em.createQuery("SELECT c FROM Car c WHERE c.reservation.returnLocation.outletId = :store AND c.active = TRUE")
                    .setParameter("store",theStoreId);
            cars = query.getResultList();
            
            Date temptime;
            for(int j = 0; j < tempReservation.size();j++){
                temp = tempReservation.get(j);
                temptime = temp.getPickupTime();
                for(int k = 0; k < cars.size();k++){
                    c = cars.get(k);
                    if(c.isActive() && c.getReservation()!=null && c.getModel().getCategory().getCategoryId() == temp.getCarCategory().getCategoryId() &&
                        c.getReservation().getReturnDate().equals(temp.getPickupDate()) &&
                        (c.getReservation().getReturnTime().before(temptime) || c.getReservation().getReturnTime().equals(temptime))){
                        c.setReservation(temp);
                        temp.setCar(c);
                        cars.remove(c);
                        toRemove.add(temp);
                        break;
                    } 
                }
            }
            
            for(Reservation r: toRemove){
                tempReservation.remove(r);
            }
            if(tempReservation.size()==0) continue;
            
            toRemove.clear();
            
            query = em.createQuery("SELECT c FROM Car c WHERE c.active = TRUE");
            cars = query.getResultList();
            System.out.println("Jumlah mobil sini is "+cars.size());
            for(int j = 0; j < tempReservation.size();j++){
                temp = tempReservation.get(j);
                for(int k = 0; k < cars.size();k++){
                    c = cars.get(k);
                    if(c.isActive() && c.getModel().getCategory().getCategoryId() == temp.getCarCategory().getCategoryId() &&
                        c.getReservation()==null){
                        c.setReservation(temp);
                        temp.setCar(c);
                        cars.remove(c);
                        toRemove.add(temp);
                        Date pickTime = new Date(2000,1,1,temp.getPickupTime().getHours()-2,temp.getPickupTime().getMinutes(),temp.getPickupTime().getSeconds());
                        ddr = new DriverDispatchRecord(DispatchStatusEnum.NOTCOMPLETED, temp.getPickupDate(), pickTime , c.getReservation().getReturnLocation().getName());
                        id = transitDriverDispatchRecordSessionBean.createDispatchRecord(ddr, temp.getReservationId(), theStoreId);
                        break;
                    }
                }
            }
            
            for(Reservation r: toRemove){
                tempReservation.remove(r);
            }
            if(tempReservation.size()==0) continue;
            
            toRemove.clear();
            
            query = em.createQuery("SELECT c FROM Car c WHERE c.active = TRUE");
            cars = query.getResultList();
            
            for(int j = 0; j < tempReservation.size();j++){
                temp = tempReservation.get(j);
                temptime = new Date(2000,1,1,temp.getPickupTime().getHours()-2,temp.getPickupTime().getMinutes(),temp.getPickupTime().getSeconds());
                        
                for(int k = 0; k < cars.size();k++){
                    c = cars.get(k);
                    if(c.isActive() && c.getReservation()!=null && c.getModel().getCategory().getCategoryId() == temp.getCarCategory().getCategoryId() &&
                        !c.getReservation().getReturnDate().after(temp.getPickupDate()) &&
                        (c.getReservation().getReturnTime().before(temptime) || c.getReservation().getReturnTime().equals(temptime))){
                        c.setReservation(temp);
                        temp.setCar(c);
                        cars.remove(c);
                        toRemove.add(temp);
                        Date pickTime = new Date(2000,1,1,temp.getPickupTime().getHours()-2,temp.getPickupTime().getMinutes(),temp.getPickupTime().getSeconds());
                        System.out.print("sampe ini jga");
                        ddr = new DriverDispatchRecord(DispatchStatusEnum.NOTCOMPLETED, temp.getPickupDate(), pickTime , c.getReservation().getReturnLocation().getName());
                        id = transitDriverDispatchRecordSessionBean.createDispatchRecord(ddr, temp.getReservationId(), theStoreId);
                        break;
                    } 
                }
            }
            
            for(Reservation r: toRemove){
                tempReservation.remove(r);
            }
            if(tempReservation.size()==0) continue;
            System.out.println("sampe");
            
        }
    }

}
