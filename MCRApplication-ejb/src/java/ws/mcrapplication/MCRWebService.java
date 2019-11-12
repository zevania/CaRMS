/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.mcrapplication;

import ejb.session.stateless.CategorySessionBeanLocal;
import ejb.session.stateless.ModelSessionBeanLocal;
import ejb.session.stateless.OutletSessionBeanLocal;
import ejb.session.stateless.PartnerSessionBeanLocal;
import ejb.session.stateless.RateSessionBeanLocal;
import ejb.session.stateless.ReservationSessionBeanLocal;
import entity.Category;
import entity.Customer;
import entity.Model;
import entity.Outlet;
import entity.Partner;
import entity.Reservation;
import java.sql.Time;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.management.relation.InvalidRelationIdException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.CategoryNotFoundException;
import util.enumeration.CustomerTypeEnum;
import util.enumeration.OrderTypeEnum;
import util.enumeration.PaidStatusEnum;
import util.enumeration.ResStatusEnum;
import util.exception.InvalidLoginCredentialException;
import util.exception.OutletNotFoundException;
import util.exception.PartnerNotFoundException;
import util.exception.RateNotFoundException;
import util.exception.ReservationNotFoundException;

/**
 *
 * @author User
 */
@WebService(serviceName = "MCRWebService")
@Stateless()
public class MCRWebService {

    @EJB
    private RateSessionBeanLocal rateSessionBean;

    @EJB
    private OutletSessionBeanLocal outletSessionBean;

    @EJB
    private ModelSessionBeanLocal modelSessionBean;

    @EJB
    private CategorySessionBeanLocal categorySessionBean;

    @EJB
    private ReservationSessionBeanLocal reservationSessionBean;

    @EJB
    private PartnerSessionBeanLocal partnerSessionBean;
    
    
    @PersistenceContext(unitName = "MCRApplication-ejbPU")
    private EntityManager em;
    
    
    @WebMethod(operationName = "partnerLogin")
    public Partner doPartnerLogin(@WebParam(name = "email") String email,@WebParam(name="password") 
            String password) throws PartnerNotFoundException, InvalidLoginCredentialException{
        
        Partner p = partnerSessionBean.doPartnerLogin(email, password);
        
        return p;
    }
    
    @WebMethod(operationName = "partnerSearchCar")
    public boolean doSeacrhCar(@WebParam(name = "searchType") String searchType,@WebParam(name="startDate") 
            Date startDate,@WebParam(name = "endDate")Date endDate, @WebParam(name = "startTime") Time startTime
            ,@WebParam(name = "endTime")Time endTime, @WebParam(name = "pickUpId")long pickupid,
            @WebParam(name = "returnId")long returnid, @WebParam(name = "categoryId")long categoryId, 
            @WebParam(name = "modelId")long modelId) throws OutletNotFoundException {
        
        boolean temp = reservationSessionBean.searchAvailableCar(searchType, startDate, endDate, startTime, endTime, pickupid, returnid, categoryId, modelId);
        
        return temp;
    }
    
    @WebMethod(operationName = "partnerCreateReservation")
    public long doCreateReservation(@WebParam(name = "payRes") Integer payRes, @WebParam(name = "totalAmt") Double totalAmt,
            @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate,
            @WebParam(name = "startTime") Time startTime, @WebParam(name = "endTime") Time endTime,
            @WebParam(name = "orderTypeRes") Integer orderTypeRes,
            @WebParam(name = "pickUpId") long pickupId, @WebParam(name = "returnId")long returnId, 
            @WebParam(name = "categoryId") long categoryId, @WebParam(name = "modelId") long modelId, 
            @WebParam(name = "custName") String custName, @WebParam(name = "custEmail")String custEmail, 
            @WebParam(name = "ccNum") long ccNum) throws OutletNotFoundException {
        
        PaidStatusEnum payStatus = PaidStatusEnum.UNPAID;
        if(payRes == 1) {
            payStatus = PaidStatusEnum.PAID;
        }
        OrderTypeEnum orderType = OrderTypeEnum.CATEGORY;
        if(orderTypeRes == 2) {
            orderType = OrderTypeEnum.MODEL;
        }
        Customer c = new Customer(custName, ccNum, custEmail, CustomerTypeEnum.PARTNER);
        Reservation r = new Reservation(payStatus, totalAmt, startDate, endDate, startTime, endTime, orderType, c);
        
        Outlet pickupOutlet = em.find(Outlet.class, pickupId);
        Outlet returnOutlet = em.find(Outlet.class, returnId);
        
        if(pickupOutlet==null) throw new OutletNotFoundException();
        if(returnOutlet==null) throw new OutletNotFoundException();
        
        
        r.setPickupLocation(pickupOutlet);
        pickupOutlet.getPickReservation().add(r);
        r.setReturnLocation(returnOutlet);
        returnOutlet.getReturnReservation().add(r);
        
        if(r.getOrderType()== OrderTypeEnum.CATEGORY)
        {
            Category category = em.find(Category.class, categoryId);
            r.setCarCategory(category);
        } 
        else if(r.getOrderType()== OrderTypeEnum.MODEL)
        {
            Model model = em.find(Model.class, modelId);
            r.setCarModel(model);
            Category cat = em.find(Category.class, model.getCategory().getCategoryId());
            r.setCarCategory(cat);
        }
        
        Query query = em.createQuery("SELECT c FROM Customer c WHERE c.email LIKE :theEmail")
                .setParameter("theEmail", custEmail);
        Customer cust;
        try{
            cust = (Customer) query.getSingleResult();
        } catch (NoResultException ex) {
            cust = new Customer(custName, ccNum, custEmail, CustomerTypeEnum.PARTNER); 
            em.persist(cust);
        }
        
        r.setCustomer(cust);
        cust.getReservations().add(r);
        
        em.persist(r);
        em.flush();
        
        return r.getReservationId();
    }
    
    @WebMethod(operationName = "partnerRetrieveAllReservations")
    public List<Reservation> doRetrieveAllResservations(@WebParam(name = "partnerId") long partnerId) throws PartnerNotFoundException {
        Partner p = em.find(Partner.class, partnerId);
        if(p==null) throw new PartnerNotFoundException();
        
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.customer.customerType = CustomerTypeEnum.PARTNER AND r.partner.partnerId = :inPartner")
                .setParameter("inPartner", partnerId);
        
        List<Reservation> reservations = query.getResultList();
        return reservations;
    }
    
    @WebMethod(operationName = "partnerRetrieveReservationById")
    public Reservation doRetrieveResservationById(@WebParam(name = "partnerId") long partnerId, @WebParam(name = "reservationId") long resId ) throws PartnerNotFoundException, ReservationNotFoundException, InvalidRelationIdException {
        Partner p = em.find(Partner.class, partnerId);
        Reservation r = em.find(Reservation.class, resId);
        
        if(r==null) throw new ReservationNotFoundException();
        if(p==null) throw new PartnerNotFoundException();
        
        if(r.getCustomer().getCustomerType()!=CustomerTypeEnum.PARTNER || r.getPartner().getPartnerId()!=partnerId){
            throw new InvalidRelationIdException();
        }
        
        return r;
    }
    
    @WebMethod(operationName = "partnerDoCancelReservation")
    public String doCancelReservation(@WebParam(name = "partnerId") long partnerId, @WebParam(name = "reservationId") long resId ) throws PartnerNotFoundException, ReservationNotFoundException, InvalidRelationIdException {
        
        Reservation r = em.find(Reservation.class, resId);
        
        if(r==null) throw new ReservationNotFoundException();
        if(r.getCustomer().getCustomerType()!=CustomerTypeEnum.PARTNER || r.getPartner().getPartnerId()!=partnerId){
            throw new InvalidRelationIdException();
        }
        
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
    
    @WebMethod(operationName = "partnerRetrieveAllCategories")
    public List<Category> doRetrieveAllCategories() {
        return categorySessionBean.retrieveAllCategories();
    }
    
    @WebMethod(operationName = "partnerRetrieveAllModels")
    public List<Model> doRetrieveAllModels() {
        return modelSessionBean.retrieveModels();
    }
    
    @WebMethod(operationName = "partnerRetrieveAllOutlets")
    public List<Outlet> doRetrieveAllOutlets() {
        return outletSessionBean.retrieveAllOutlets();
    }
    
    @WebMethod(operationName = "partnerRetrieveTotalByCategory")
    public double doRetrieveTotalByCategory(@WebParam(name = "catId") long catId, 
            @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate) throws CategoryNotFoundException, RateNotFoundException {
        return rateSessionBean.retrieveTotalByCategory(catId, startDate, endDate);
    }
}
