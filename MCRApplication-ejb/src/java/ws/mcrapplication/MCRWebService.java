/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.mcrapplication;

import ejb.session.stateless.PartnerSessionBeanLocal;
import ejb.session.stateless.ReservationSessionBeanLocal;
import entity.Partner;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import util.exception.InvalidLoginCredentialException;
import util.exception.OutletNotFoundException;
import util.exception.PartnerNotFoundException;

/**
 *
 * @author User
 */
@WebService(serviceName = "MCRWebService")
@Stateless()
public class MCRWebService {

    @EJB
    private ReservationSessionBeanLocal reservationSessionBean;

    @EJB
    private PartnerSessionBeanLocal partnerSessionBean;
    
    
    
    @WebMethod(operationName = "partnerLogin")
    public Partner doPartnerLogin(@WebParam(name = "email") String email,@WebParam(name="password") 
            String password) throws PartnerNotFoundException, InvalidLoginCredentialException{
        
        Partner p = partnerSessionBean.doPartnerLogin(email, password);
        
        return p;
    }
    
    @WebMethod(operationName = "partnerSearchCar")
    public boolean doSeacrhCar(@WebParam(name = "searchType") String searchType,@WebParam(name="startDate") 
            LocalDate startDate,@WebParam(name = "endDate")LocalDate endDate, @WebParam(name = "startTime") LocalTime startTime
            ,@WebParam(name = "endTime")LocalTime endTime, @WebParam(name = "pickUpId")long pickupid,@WebParam(name = "returnId")long returnid,
            @WebParam(name = "categoryId")long categoryId, @WebParam(name = "modelId")long modelId) throws OutletNotFoundException {
        
        boolean temp = reservationSessionBean.searchAvailableCar(searchType, startDate, endDate, startTime, endTime, pickupid, returnid, categoryId, modelId);
        
        return temp;
    }
    
    
}
