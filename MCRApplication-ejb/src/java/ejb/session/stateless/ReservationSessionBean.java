/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Car;
import entity.Category;
import entity.Customer;
import entity.Member;
import entity.Model;
import entity.Outlet;
import entity.Reservation;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.CarStatusEnum;
import util.enumeration.CustomerTypeEnum;
import util.enumeration.OrderTypeEnum;
import util.enumeration.PaidStatusEnum;
import util.enumeration.ResStatusEnum;
import util.exception.InvalidReservationException;
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
    public long createMemberReservation(Reservation r, long memberId, long ccNum, long pickupId, long returnId, long categoryId, long modelId) {
        em.persist(r);
        Member member = em.find(Member.class, memberId);
        Outlet pickupOutlet = em.find(Outlet.class, pickupId);
        Outlet returnOutlet = em.find(Outlet.class, returnId);
        
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
        Query query = em.createQuery("SELECT c FROM Customer c WHERE c.email LIKE :theEmail")
                .setParameter("theEmail", email);
        Customer cust;
        try{
            cust = (Customer) query.getSingleResult();
        } catch (NoResultException ex) {
            cust = new Customer(member.getName(), ccNum, member.getEmail(), CustomerTypeEnum.MEMBER); 
            em.persist(cust);
        }
        
        r.setCustomer(cust);
        cust.getReservations().add(r);
        
        em.flush();
        
        return r.getReservationId();
    }

    @Override
    public String cancelReservation(long resId) {
        Reservation r = em.find(Reservation.class, resId);
        LocalDate pickupdate = r.getPickupDate();
        boolean proceed = false;
        int numdays = 0;
        double penalty = 0.0;
        String reply = "";
            
        if(r.getResStatus()==ResStatusEnum.ORDERED ){
            LocalDate today = LocalDate.now();
            
            if(today.compareTo(pickupdate)<0){
               if(today.getMonthValue()<pickupdate.getMonthValue()){
                   numdays = 30;
               } else {
                   numdays = pickupdate.getDayOfYear() - today.getDayOfYear();
               }
               proceed = true;
            } else {
                return "[Action is invalid]\n";
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
            return "[Action is invalid]\n";
        }
        
        return reply;
    }

    @Override
    public List<Reservation> retrieveReservations(String email) {
        Query query = em.createQuery("SELECT c FROM CUSTOMER c WHERE c.email LIKE :theEmail")
                .setParameter("theEmail", email);
        Customer cust = new Customer();
        try {
            cust = (Customer) query.getSingleResult();
        } catch( Exception ex){
            System.out.println("Something went wrong");
            return null;
        }
        
        return cust.getReservations() ;
    }

    @Override
    public boolean searchAvailableCar(String searchType, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, long pickupid, long returnid, long categoryId, long modelId) throws OutletNotFoundException {
        Outlet pickupLoc = em.find(Outlet.class, pickupid);
        Outlet returnLoc = em.find(Outlet.class, returnid);
        
        if(pickupLoc==null || returnLoc == null) throw new OutletNotFoundException();
        
        if(pickupLoc.getOpenHrs().isAfter(startTime)) return false;
        if(returnLoc.getCloseHrs().isBefore(endTime)) return false;
        
        Set<Reservation> clashingRes = new HashSet<>();
        List<Reservation> temp = new LinkedList<>();
        List<Reservation> toRemove = new LinkedList<>();
        
        LocalTime dStartTime = startTime.minusHours(2);
        LocalTime dEndTime = endTime.plusHours(2);
        
        Query query;
        searchType = searchType.toLowerCase();
        int totalCar = 0;
        
        if(searchType.equals("category")){
            query = em.createQuery("SELECT c FROM Car c WHERE c.model.category.categoryId = :catId AND c.active IS TRUE")
                    .setParameter("catId", categoryId);
            totalCar = query.getResultList().size();
            
            query = em.createQuery("SELECT r FROM Reservation r WHERE r.carCategory.categoryId = :inCat AND r.pickupDate <= :inStartDate AND r.returnDate >= :inStartDate")
                    .setParameter("inCat", categoryId)
                    .setParameter("inStartDate", startDate);
            clashingRes = new HashSet<>(query.getResultList());
            
            query = em.createQuery("SELECT r FROM Reservation r WHERE r.carCategory.categoryId = :inCat AND r.pickupDate <= :inEndDate AND r.returnDate >= :inEndDate")
                    .setParameter("inCat", categoryId)
                    .setParameter("inEndDate", endDate);
            temp = query.getResultList();
            
            for(Reservation theR: temp){
                clashingRes.add(theR);
            }
            
            for(Reservation theR: clashingRes){
                if(theR.getReturnDate().isEqual(startDate)){
                    if(theR.getReturnLocation().equals(pickupLoc)){
                        if(theR.getReturnTime().isAfter(startTime)){
                            continue;
                        } else {
                            toRemove.add(theR);
                        }
                    }else{
                        if(theR.getReturnTime().isAfter(dStartTime)){
                            continue;
                        } else {
                            toRemove.add(theR);
                        }
                    }
                } else if(theR.getPickupDate().isEqual(endDate)){
                    if(theR.getPickupLocation().equals(returnLoc)){
                        if(theR.getPickupTime().isBefore(endTime)){
                            continue;
                        } else {
                            toRemove.add(theR);
                        }
                    }else{
                        if(theR.getPickupTime().isBefore(dEndTime)){
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
           query = em.createQuery("SELECT c FROM Car c WHERE c.model.modelId = :modId AND c.active IS TRUE")
                    .setParameter("modId", modelId);
            totalCar = query.getResultList().size();
            
            query = em.createQuery("SELECT r FROM Reservation r WHERE r.carModel.modelId = :inMod AND r.pickupDate <= :inStartDate AND r.returnDate >= :inStartDate")
                    .setParameter("inMod", modelId)
                    .setParameter("inStartDate", startDate);
            clashingRes = new HashSet<>(query.getResultList());
            
            query = em.createQuery("SELECT r FROM Reservation r WHERE r.carModel.modelId = :inMod AND r.pickupDate <= :inEndDate AND r.returnDate >= :inEndDate")
                    .setParameter("inMod", modelId)
                    .setParameter("inEndDate", endDate);
            temp = query.getResultList();
            
            for(Reservation theR: temp){
                clashingRes.add(theR);
            }
            
            for(Reservation theR: clashingRes){
                if(theR.getReturnDate().isEqual(startDate)){
                    if(theR.getReturnLocation().equals(pickupLoc)){
                        if(theR.getReturnTime().isAfter(startTime)){
                            continue;
                        } else {
                            toRemove.add(theR);
                        }
                    }else{
                        if(theR.getReturnTime().isAfter(dStartTime)){
                            continue;
                        } else {
                            toRemove.add(theR);
                        }
                    }
                } else if(theR.getPickupDate().isEqual(endDate)){
                    if(theR.getPickupLocation().equals(returnLoc)){
                        if(theR.getPickupTime().isBefore(endTime)){
                            continue;
                        } else {
                            toRemove.add(theR);
                        }
                    }else{
                        if(theR.getPickupTime().isBefore(dEndTime)){
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
            
        }
        
        return false;
//        Outlet pickupLoc = em.find(Outlet.class, pickupid);
//        Outlet returnLoc = em.find(Outlet.class, returnid);
//        searchType = searchType.toLowerCase();
//        Category category;
//        Model model;
//        List<Reservation> wholeres;
//        List<Reservation> pickedres = new LinkedList<>();
//        Query query;
//        
//        startTime = startTime.minusHours(2);
//        endTime = endTime.plusHours(2);
//        
//        if(searchType.equals("category")){
//            category = em.find(Category.class, categoryId);
//            query = em.createQuery("SELECT r FROM Reservation r WHERE r.resStatus = ResStatusEnum.ORDERED OR r.resStatus = ResStatusEnum.PICKEDUP");
//            wholeres = query.getResultList();
//            int totalCar = 0;
//            
//            query = em.createQuery("SELECT c FROM Car c WHERE c.model.category.categoryId = :catId")
//                    .setParameter("catId", category.getCategoryId());
//            
//            List<Car> cars = query.getResultList();
//            
//            totalCar = cars.size();
//            
//            for(Reservation r: wholeres){
//                /*
//                if(r.getPickupDate().isBefore(endDate) && r.getPickupDate().isAfter(startDate) ){
//                    pickedres.add(r);
//                } else if (r.getReturnDate().isAfter(startDate)&& r.getReturnDate().isBefore(endDate)) {
//                    pickedres.add(r);
//                } else if(r.getReturnDate().equals(startDate) && r.getReturnTime().isAfter(startTime)){
//                    pickedres.add(r);
//                } else if (r.getPickupDate().equals(endDate) && r.getPickupTime().isBefore(endTime)) {
//                    pickedres.add(r);
//                } else if (r.getReturnDate().isAfter(endDate) && r.getPickupDate().isBefore(startDate)){
//                    pickedres.add(r);
//                } else if(r.getPickupDate().equals(startDate) && r.getPickupTime().equals(startTime))
//                */
//                
//                if(r.getReturnDate().isBefore(startDate)){
//                } else if(r.getPickupDate().isAfter(endDate)){
//                } else if(r.getReturnDate().equals(startDate) && (r.getReturnTime().isBefore(startTime) || r.getReturnTime().equals(startTime))){        
//                } else if(r.getPickupDate().equals(endDate) && (r.getPickupTime().isAfter(endTime)||r.getPickupTime().equals(endTime))){
//                } else {
//                    if(r.getOrderType() == OrderTypeEnum.CATEGORY && r.getCarCategory().getCategoryId() == category.getCategoryId())
//                        pickedres.add(r);
//                    else if(r.getOrderType() == OrderTypeEnum.MODEL && r.getCarModel().getCategory().getCategoryId() == category.getCategoryId())
//                        pickedres.add(r);
//                }    
//            }
//            
//            int leftover = totalCar-pickedres.size();
//            
//            if(leftover>0){
//                return true;
//            }
//            
//        } else if (searchType.equals("model")) {
//            model = em.find(Model.class, modelId);
//        }
//        
//        return false;
    }
    
    @Override
    public Reservation retrieveReservationById(long resId){
        // will return null if the thing does not exist
        return em.find(Reservation.class, resId);
    }
    
    
    
    
}
