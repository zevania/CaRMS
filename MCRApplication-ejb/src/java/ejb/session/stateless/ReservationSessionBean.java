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
//        System.out.println("asd"+email);
//        Query query = em.createQuery("SELECT c FROM Customer c WHERE c.email = :theEmail")
//                .setParameter("theEmail", email);

        Customer cust = new Customer(member.getName(), ccNum, email, CustomerTypeEnum.MEMBER);
        
//        List<Customer> abc = query.getResultList();
//        for(Customer asd:abc){
//            System.out.println(asd.getEmail()+" "+asd.getCustId());
//        }
//        try{
//           cust = (Customer) query.getSingleResult(); 
//        } catch (NoResultException ex) {
//            cust = new Customer(member.getName(), ccNum, member.getEmail(), CustomerTypeEnum.MEMBER); 
//            em.persist(cust);
//        }
        

        r.setCustomer(cust);
        
        //cust.getReservations().add(r);
        //em.persist(cust);
        em.persist(r);
        em.persist(cust);
        em.flush();
        
        
        //return 0;
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
        
        System.out.println("open hrs "+pickupLoc.getOpenHrs()+" - startTime "+startTime);
        System.out.println("close hrs "+returnLoc.getCloseHrs()+" - closeTime "+endTime);
        System.out.println("the year is "+startTime.getYear());
        System.out.println("the or year is "+pickupLoc.getOpenHrs().getYear());
        System.out.println("the start date is "+startDate);
        System.out.println("the startDate year is"+startDate.getYear());
        System.out.println("TMZ offset openLoc "+pickupLoc.getOpenHrs().getTimezoneOffset());
        System.out.println("TMZ offset local "+startTime.getTimezoneOffset());
        

        Date tempStartTime = startTime;
        tempStartTime.setDate(pickupLoc.getOpenHrs().getDate());
        tempStartTime.setYear(pickupLoc.getOpenHrs().getYear());
        tempStartTime.setMonth(pickupLoc.getOpenHrs().getMonth());

        System.out.println("the tempSTartTime "+tempStartTime);
        System.out.println("sikat "+pickupLoc.getOpenHrs());
        
        Date tempEndTime = endTime;
        tempEndTime.setDate(pickupLoc.getOpenHrs().getDate());
        tempEndTime.setYear(pickupLoc.getOpenHrs().getYear());
        tempEndTime.setMonth(pickupLoc.getOpenHrs().getMonth());
        
        if(pickupLoc.getOpenHrs().after(startTime)) return false;
        if(returnLoc.getCloseHrs().before(endTime)) return false;
        
        Set<Reservation> clashingRes = new HashSet<>();
        List<Reservation> temp = new LinkedList<>();
        List<Reservation> toRemove = new LinkedList<>();
        
        Date dStartTime = new Date(2000,1,1,startTime.getHours()-2,startTime.getMinutes(),startTime.getSeconds());
        Date dEndTime = new Date(2000,1,1,endTime.getHours()-2,endTime.getMinutes(),endTime.getSeconds());
        
        System.out.println("ini loo sini pak "+dStartTime.getTimezoneOffset());
        System.out.println("the input date offset "+startDate.getTimezoneOffset()+" the date "+startDate);
        
        
        
        
        Query query;
        searchType = searchType.toLowerCase();
        int totalCar = 0;
        
        
        query = em.createQuery("SELECT r FROM Reservation r");
        List<Reservation> ghi = query.getResultList();
        for(Reservation lis: ghi){
            System.out.println("the local date "+lis.getPickupDate()+" offset is"+lis.getPickupDate().getTimezoneOffset());
        }
        
        System.out.println("the type"+ searchType);
        System.out.println("the model id"+ modelId);
        if(searchType.equals("category")){
            query = em.createQuery("SELECT c FROM Car c WHERE c.model.category.categoryId = :catId AND c.active = TRUE")
                    .setParameter("catId", categoryId);
            totalCar = query.getResultList().size();
            System.out.println("theCar total is "+totalCar);
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
            
            for(Reservation theR: clashingRes){
                if(theR.getReturnDate().equals(startDate)){
                    if(theR.getReturnLocation().equals(pickupLoc)){
                        if(theR.getReturnTime().after(tempStartTime)){
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
                        if(theR.getPickupTime().before(tempEndTime)){
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
            System.out.println("sini nih pak"+leftCar);
            if(leftCar>0) return true;
            else return false;
        }else if(searchType.equals("model")){
           query = em.createQuery("SELECT c FROM Car c WHERE c.model.modelId = :modId AND c.active = TRUE")
                    .setParameter("modId", modelId);
            totalCar = query.getResultList().size();
            
            System.out.println("how mana yo"+totalCar);
            
            query = em.createQuery("SELECT r FROM Reservation r WHERE r.carModel.modelId = :inMod AND r.pickupDate >= :inStartDate AND r.pickupDate <= :inEndDate")
                    .setParameter("inMod", modelId)
                    .setParameter("inEndDate", endDate)
                    .setParameter("inStartDate", startDate);
            clashingRes = new HashSet<>(query.getResultList());
            
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
                        if(theR.getReturnTime().after(tempStartTime)){
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
                        if(theR.getPickupTime().before(tempEndTime)){
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
            if(leftCar>0) return true;
            else return false; 
            
        }
        
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
            
            query = em.createQuery("SELECT r FROM Reservation r "
                    + "WHERE r.orderType = util.enumeration.OrderTypeEnum.MODEL AND r.pickupLocation.outletId = :store AND r.pickupDate = :inTodayDate")
                    .setParameter("store", theStoreId)
                    .setParameter("inTodayDate", todayDate);
            tempReservation = query.getResultList();
            
            if(tempReservation.size()==0) continue;
            cars = o.getCars();
            
            for(int j = 0; j < tempReservation.size();j++){
                temp = tempReservation.get(j);
                System.out.println("TMZ here is "+temp.getPickupTime().getTimezoneOffset());
                
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
                System.out.println("TMZ here is "+temptime.getTimezoneOffset());
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
                System.out.println("TMZ here is "+temp.getPickupTime().getTimezoneOffset());
                
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
