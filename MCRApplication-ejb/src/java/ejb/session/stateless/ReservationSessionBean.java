/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Car;
import entity.Category;
import entity.Customer;
import entity.DriverDispatchRecord;
import entity.OurMember;
import entity.Model;
import entity.Outlet;
import entity.Reservation;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.CarStatusEnum;
import util.enumeration.CustomerTypeEnum;
import util.enumeration.DispatchStatusEnum;
import util.enumeration.OrderTypeEnum;
import util.enumeration.PaidStatusEnum;
import util.enumeration.ResStatusEnum;
import util.exception.InvalidReservationException;
import util.exception.MemberNotFoundException;
import util.exception.OutletNotFoundException;
import util.exception.ReservationNotFoundException;

/**
 *
 * @author User
 */
@Stateless
@Local(ReservationSessionBeanLocal.class)
@Remote(ReservationSessionBeanRemote.class)
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @EJB
    private TransitDriverDispatchRecordSessionBeanLocal transitDriverDispatchRecordSessionBean;

    @PersistenceContext(unitName = "MCRApplication-ejbPU")
    private EntityManager em;

    
    @Override
    public void updateRes(long resId, ResStatusEnum status) throws ReservationNotFoundException, InvalidReservationException{
        Reservation r = em.find(Reservation.class, resId);
        
        if(r == null) throw new ReservationNotFoundException();
        if(r.getResStatus() != ResStatusEnum.ORDERED) throw new InvalidReservationException();
        
        Car c = r.getCar();
        Outlet o;
        if(status == ResStatusEnum.PICKEDUP){
            c.setLocation("CUSTOMER " +r.getCustomer().getName() );
            o = c.getOutlet();
            c.setOutlet(null);
            c.setStatus(CarStatusEnum.ONRENTAL);
            c.setReservation(r);
            r.setResStatus(status);
            r.setPaymentStatus(PaidStatusEnum.PAID);
            o.getCars().remove(c);
        } else if (status == ResStatusEnum.DONE){
            o = r.getReturnLocation();
            o.getCars().add(c);
            c.setOutlet(r.getReturnLocation());
            c.setLocation(r.getReturnLocation().getName());
            c.setStatus(CarStatusEnum.OUTLET);
            c.setReservation(null);
            r.setResStatus(status);
        }
    }

    @Override
    public long createMemberReservation(Reservation r, long memberId, long ccNum, long pickupId, long returnId, long categoryId, long modelId) throws OutletNotFoundException, MemberNotFoundException{
        OurMember member = em.find(OurMember.class, memberId);
        Outlet pickupOutlet = em.find(Outlet.class, pickupId);
        Outlet returnOutlet = em.find(Outlet.class, returnId);
        
        if(member==null) throw new MemberNotFoundException();
        if(pickupOutlet==null) throw new OutletNotFoundException();
        if(returnOutlet==null) throw new OutletNotFoundException();
        
        
        r.setPickupLocation(pickupOutlet);
        pickupOutlet.getPickReservation().add(r);
        r.setReturnLocation(returnOutlet);
        returnOutlet.getReturnReservation().add(r);
        if(r.getOrderType()== OrderTypeEnum.CATEGORY){
            Category category = em.find(Category.class, categoryId);
            r.setCarCategory(category);
        } else if(r.getOrderType()== OrderTypeEnum.MODEL){
            Model model = em.find(Model.class, modelId);
            r.setCarModel(model);
            Category cat = em.find(Category.class, model.getCategory().getCategoryId());
            r.setCarCategory(cat);
        }
        
        String email = member.getEmail();

        Customer cust = new Customer(member.getName(), ccNum, email, CustomerTypeEnum.MEMBER);
        
        em.persist(cust);
        
        r.setCustomer(cust);
        
        
        em.persist(r);
        em.flush();       
        return r.getReservationId();
    }
    
    

    @Override
    public String cancelReservation(long resId) {
        Reservation r = em.find(Reservation.class, resId);
        Date pickupdate = r.getPickupDate();
        boolean proceed = false;
        int numdays = 0;
        double penalty = 0.0;
        String reply = "";
            
        if(r.getResStatus()==ResStatusEnum.ORDERED ){
            Date today = Calendar.getInstance().getTime();
            
            if(today.before(pickupdate)){
                
               if(today.getYear()== pickupdate.getYear() && today.getMonth()<pickupdate.getMonth()){
                   numdays = 30;
                   if((pickupdate.getMonth()+1)-(today.getMonth()+1)==1){
                       numdays = 30-today.getDate()+pickupdate.getDate();
                   }
               } else if(today.getYear()<pickupdate.getYear()){
                   numdays = 30;
               } else {
                   numdays = pickupdate.getDate()- today.getDate();
               }
               proceed = true;
            } else {
                return "The order date is in the past!\n [Action is invalid]\n";
            }
            
            if(proceed){
                if(numdays<14 && numdays>=7){
                    penalty = r.getTotal()*0.2;
                } else if(numdays<7 && numdays>=3){
                    penalty = r.getTotal()*0.5;
                } else if(numdays<3){
                    penalty = r.getTotal()*0.7;
                } else {
                    penalty = 0;
                }
            }
            
            if(r.getPaymentStatus()==PaidStatusEnum.PAID && proceed){
                reply+="The penalty charge is $"+penalty+"\n";
                reply+="Customer is refunded a total of $"+(r.getTotal()-penalty)+"\n";
                reply+="The card number is "+r.getCustomer().getCcNum()+"\n";
                r.setResStatus(ResStatusEnum.CANCELLED);
            } else if(r.getPaymentStatus()==PaidStatusEnum.UNPAID && proceed){
                reply+="The penalty charge is $"+penalty+"\n";
                reply+="The card number is "+r.getCustomer().getCcNum()+"\n";
                r.setResStatus(ResStatusEnum.CANCELLED);
            }   
        } else {
            return "The reservation status is no longer ordered!\n [Action is invalid]\n";
        }
        r.setPenalty(penalty);
        return reply;
    }

    @Override
    public List<Reservation> retrieveReservations(String email) {
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.customer.email LIKE :theEmail")
                .setParameter("theEmail", email);
        
        
        
        return query.getResultList() ;
    }

    @Override
    public boolean searchAvailableCar(String searchType, Date startDate, Date endDate, Date startTime, Date endTime, long pickupid, long returnid, long categoryId, long modelId) throws OutletNotFoundException {
        Outlet pickupLoc = em.find(Outlet.class, pickupid);
        Outlet returnLoc = em.find(Outlet.class, returnid);
        
        if(pickupLoc==null || returnLoc == null) throw new OutletNotFoundException();

        Date tempStartTime = startTime;
        tempStartTime.setDate(pickupLoc.getOpenHrs().getDate());
        tempStartTime.setYear(pickupLoc.getOpenHrs().getYear());
        tempStartTime.setMonth(pickupLoc.getOpenHrs().getMonth());
        
        Date tempEndTime = endTime;
        tempEndTime.setDate(returnLoc.getCloseHrs().getDate());
        tempEndTime.setYear(returnLoc.getCloseHrs().getYear());
        tempEndTime.setMonth(returnLoc.getCloseHrs().getMonth());
        
//        System.out.println("start: "+pickupLoc.getOpenHrs()+" "+ tempStartTime);
//        System.out.println("end: "+returnLoc.getCloseHrs()+" "+ tempEndTime);
        if(pickupLoc.getOpenHrs().after(tempStartTime)) return false;
        if(returnLoc.getCloseHrs().before(tempEndTime)) return false;
        
        Set<Reservation> clashingRes = new HashSet<>();
        List<Reservation> temp = new LinkedList<>();
        List<Reservation> toRemove = new LinkedList<>();
        
        Date dStartTime = new Date(2000,1,1,startTime.getHours()-2,startTime.getMinutes(),startTime.getSeconds());
        Date dEndTime = new Date(2000,1,1,endTime.getHours()-2,endTime.getMinutes(),endTime.getSeconds());
        
        dStartTime.setYear(startTime.getYear());
        dStartTime.setMonth(startTime.getMonth());
        dStartTime.setDate(startTime.getDate());
        
        dEndTime.setYear(endTime.getYear());
        dEndTime.setMonth(endTime.getMonth());
        dEndTime.setDate(endTime.getDate());
        
        
        Query query;
        searchType = searchType.toLowerCase();
        int totalCar = 0;
        
        if(searchType.equals("category")){
            System.out.println("kini");
            query = em.createQuery("SELECT c FROM Car c WHERE c.model.category.categoryId = :catId AND c.active = TRUE")
                    .setParameter("catId", categoryId);
            totalCar = query.getResultList().size();
            query = em.createQuery("SELECT r FROM Reservation r WHERE r.carCategory.categoryId = :inCat AND r.pickupDate >= :inStartDate AND r.pickupDate <= :inEndDate")
                    .setParameter("inCat", categoryId)
                    .setParameter("inStartDate", startDate)
                    .setParameter("inEndDate", endDate);
            clashingRes = new HashSet<>(query.getResultList());
            
            query = em.createQuery("SELECT r FROM Reservation r WHERE r.carCategory.categoryId = :inCat AND r.returnDate <= :inEndDate AND r.returnDate >= :inStartDate")
                    .setParameter("inCat", categoryId)
                    .setParameter("inStartDate", startDate)
                    .setParameter("inEndDate", endDate);
            temp = query.getResultList();
            
            for(Reservation theR: temp){
                clashingRes.add(theR);
            }
            System.out.println("clash res size is "+clashingRes.size());
            for(Reservation theR: clashingRes){
                if(theR.getReturnDate().equals(startDate)){
                    if(theR.getReturnLocation().equals(pickupLoc)){
                        if(theR.getReturnTime().after(startTime)){
                            continue;
                        } else {
                            toRemove.add(theR);
                        }
                    }else{
                        if(theR.getReturnTime().after(dStartTime)){
                            continue;
                        } else {
                            toRemove.add(theR);
                        }
                    }
                } else if(theR.getPickupDate().equals(endDate)){
                    if(theR.getPickupLocation().equals(returnLoc)){
                        if(theR.getPickupTime().before(endTime)){
                            continue;
                        } else {
                            toRemove.add(theR);
                            System.out.println("bruhla");
                        }
                    }else{
                        if(theR.getPickupTime().before(dEndTime)){
                            continue;
                        } else {
                            toRemove.add(theR);
                        }
                    }
                }
            }
            
            for(Reservation rmv: toRemove){
                clashingRes.remove(rmv);
            }
            
            int leftCar = totalCar - clashingRes.size();
            if(leftCar>0) return true;
            else return false;
        }else if(searchType.equals("model")){
           query = em.createQuery("SELECT c FROM Car c WHERE c.model.modelId = :modId AND c.active = TRUE")
                    .setParameter("modId", modelId);
            totalCar = query.getResultList().size();
            
            query = em.createQuery("SELECT r FROM Reservation r WHERE r.carModel.modelId = :inMod AND r.pickupDate >= :inStartDate AND r.pickupDate <= :inEndDate")
                    .setParameter("inMod", modelId)
                    .setParameter("inEndDate", endDate)
                    .setParameter("inStartDate", startDate);
            clashingRes = new HashSet<>(query.getResultList());
            System.out.println("nah man "+clashingRes.size());
            query = em.createQuery("SELECT r FROM Reservation r WHERE r.carModel.modelId = :inMod AND r.returnDate <= :inEndDate AND r.returnDate >= :inStartDate")
                    .setParameter("inMod", modelId)
                    .setParameter("inStartDate",startDate)
                    .setParameter("inEndDate", endDate);
            temp = query.getResultList();
            
            for(Reservation theR: temp){
                clashingRes.add(theR);
            }
            
            for(Reservation theR: clashingRes){
                if(theR.getReturnDate().equals(startDate)){
                    if(theR.getReturnLocation().equals(pickupLoc)){
                        if(theR.getReturnTime().after(startTime)){
                            continue;
                        } else {
                            toRemove.add(theR);
                        }
                    }else{
                        System.out.println("disni ada");
                        if(theR.getReturnTime().after(dStartTime)){
                            System.out.println("masukah");
                            System.out.println("dtsrt "+dStartTime);
                            System.out.println("theR return time "+theR.getReturnTime());
                            continue;
                        } else {
                            toRemove.add(theR);
                        }
                    }
                } else if(theR.getPickupDate().equals(endDate)){
                    if(theR.getPickupLocation().equals(returnLoc)){
                        if(theR.getPickupTime().before(endTime)){
                            continue;
                        } else {
                            toRemove.add(theR);
                        }
                    }else{
                        if(theR.getPickupTime().before(dEndTime)){
                            continue;
                        } else {
                            toRemove.add(theR);
                        }
                    }
                }
            }
            
            for(Reservation rmv: toRemove){
                clashingRes.remove(rmv);
            }
            
            int leftCar = totalCar - clashingRes.size();
            
            System.out.println("left cat total bru is "+leftCar);
            
            if(leftCar>0) return true;
            else return false; 
            
        }
        System.out.println("sini bruh "+searchType.toString());
        return false;
    }
    
    @Override
    public Reservation retrieveReservationById(long resId){
        // will return null if the thing does not exist
        return em.find(Reservation.class, resId);
    }
    
    
    public void manualAllocateCars(Date date) {
        Query query = em.createQuery("SELECT o FROM Outlet o");
        List<Outlet> outlet = query.getResultList();
        Outlet o;
        List<Reservation> tempReservation;
        Reservation temp;
        Car c;
        List<Car> cars;
        List<Reservation> toRemove = new LinkedList<>();
        long theStoreId;
        
        Date todayDate = date;
        DriverDispatchRecord ddr;
        long id;
        
        for(int i = 0; i < outlet.size(); i++){
            o = outlet.get(i);
            theStoreId = o.getOutletId();
            toRemove.clear();
            
            query = em.createQuery("SELECT r FROM Reservation r WHERE r.orderType = util.enumeration.OrderTypeEnum.MODEL AND r.pickupLocation.outletId = :store AND r.pickupDate = :inTodayDate")
                    .setParameter("store", theStoreId)
                    .setParameter("inTodayDate", todayDate);
            tempReservation = query.getResultList();
            System.out.println("oId "+theStoreId+" the total"+tempReservation.size());
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
                        String fromoutlet = "";
                        if(c.getOutlet()!=null) fromoutlet = c.getOutlet().getName();
                        else if(c.getReservation()!=null) fromoutlet = c.
                        c.setReservation(temp);
                        temp.setCar(c);
                        toRemove.add(temp);
                        cars.remove(c);
                        Date pickTime = new Date(2000,1,1,temp.getPickupTime().getHours()-2,temp.getPickupTime().getMinutes(),temp.getPickupTime().getSeconds());
                        ddr = new DriverDispatchRecord(DispatchStatusEnum.NOTCOMPLETED, temp.getPickupDate(), pickTime , fromoutlet);
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
                        String fromoutlet = c.getReservation().getReturnLocation().getName();
                        c.setReservation(temp);
                        temp.setCar(c);
                        cars.remove(c);
                        toRemove.add(temp);
                        Date pickTime = new Date(2000,1,1,temp.getPickupTime().getHours()-2,temp.getPickupTime().getMinutes(),temp.getPickupTime().getSeconds());
                        ddr = new DriverDispatchRecord(DispatchStatusEnum.NOTCOMPLETED, temp.getPickupDate(), pickTime , fromoutlet);
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
            query = em.createQuery("SELECT r FROM Reservation r "
                    + "WHERE r.pickupLocation.outletId = :store AND r.orderType = util.enumeration.OrderTypeEnum.CATEGORY  AND r.pickupDate = :inTodayDate ")
                    .setParameter("store", theStoreId)
                    .setParameter("inTodayDate",todayDate);
            tempReservation = query.getResultList();
            cars = o.getCars();
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
                        break;
                    } 
                }
            }
            
            
            for(Reservation r: toRemove){
                tempReservation.remove(r);
            }
            
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
            
            for(int j = 0; j < tempReservation.size();j++){
                temp = tempReservation.get(j);
                for(int k = 0; k < cars.size();k++){
                    c = cars.get(k);
                    if(c.isActive() && c.getModel().getCategory().getCategoryId() == temp.getCarCategory().getCategoryId() &&
                        c.getReservation()==null){
                        String fromoutlet = c.getReservation().getReturnLocation().getName();
                        c.setReservation(temp);
                        temp.setCar(c);
                        cars.remove(c);
                        toRemove.add(temp);
                        Date pickTime = new Date(2000,1,1,temp.getPickupTime().getHours()-2,temp.getPickupTime().getMinutes(),temp.getPickupTime().getSeconds());
                        ddr = new DriverDispatchRecord(DispatchStatusEnum.NOTCOMPLETED, temp.getPickupDate(), pickTime , fromoutlet);
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
                        String fromoutlet = c.getReservation().getReturnLocation().getName();
                        c.setReservation(temp);
                        temp.setCar(c);
                        cars.remove(c);
                        toRemove.add(temp);
                        Date pickTime = new Date(2000,1,1,temp.getPickupTime().getHours()-2,temp.getPickupTime().getMinutes(),temp.getPickupTime().getSeconds());
                        ddr = new DriverDispatchRecord(DispatchStatusEnum.NOTCOMPLETED, temp.getPickupDate(), pickTime ,fromoutlet);
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
    }
    
    
}
