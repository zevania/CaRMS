/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationclientapp;

import ws.client.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
//import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import ws.client.Category;
import ws.client.CategoryNotFoundException_Exception;
import ws.client.InvalidLoginCredentialException;
import ws.client.InvalidLoginCredentialException_Exception;
import ws.client.InvalidRelationIdException_Exception;
import ws.client.Model;
import ws.client.ObjectFactory;
import ws.client.OrderTypeEnum;
import ws.client.Outlet;
import ws.client.OutletNotFoundException_Exception;
import ws.client.PaidStatusEnum;
import ws.client.Partner;
import ws.client.PartnerNotFoundException_Exception;
import ws.client.RateNotFoundException_Exception;
import ws.client.ResStatusEnum;
import ws.client.Reservation;
import ws.client.ReservationNotFoundException_Exception;

/**
 *
 * @author User
 */
public class MainApp {
    
    
    Partner curPartner;
            
    public void runApp(){
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        
        
        
        
        while(true)
        {
            System.out.println("*** Welcome to Holiday Reservation Client  ***\n");
            System.out.println("1: Log in");
            System.out.println("2: Exit\n");
            response = 0;
            
            while(response < 1 || response > 2)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                        doLogin();
                        if(curPartner==null){
                            System.out.println("Something is wrong with your login!");
                            break;
                        }
                        
                        System.out.println("Login successful!\n");
                        
                        mainApp();
                        doLogout();
                        break;
                }
                else if (response == 2)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 2){
                break;
            }
        }
    }
    
    private void doLogin(){
        Scanner scanner = new Scanner(System.in);
        String email = "";
        String password = "";
        
        System.out.println("*** Holiday Reservation Client :: Login ***\n");
        System.out.print("Enter email> ");
        email = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();
        
        if(email.length() > 0 && password.length() > 0)
        {
            try {
                curPartner = partnerLogin(email, password);
            } catch (InvalidLoginCredentialException_Exception ex) {
                System.out.println("Log in credential is invalid!");
                System.out.println("[Action denied]");
                return;
            } catch (PartnerNotFoundException_Exception ex) {
                System.out.println("Partner not found!");
                System.out.println("[Action denied]");
                return;
            }
        } else {
            System.out.println("Input is invalid!");
            System.out.println("[Action denied]");
            return;
        } 
          
    }
    
    private void doLogout(){
        curPartner = null;
        System.out.println("Logout successfully!");
    }
    
    private void mainApp(){
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Holiday Reservation Client :: Partner Reservation Manager ***\n");
            System.out.println("You are logged in as " + curPartner.getCompanyName() + "\n");
            System.out.println("1: Search Car");
            System.out.println("2: View Reservation Details");
            System.out.println("3: View All My Reservations");
            System.out.println("4: Logout\n");
            response = 0;
            
            while(response < 1 || response > 4)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1)
                {
                    doSearchCar();
                    break;
                }
                else if (response == 2) 
                {
                    doViewResDetails();
                    break;
                }
                else if (response == 3) 
                {
                    doViewAllRes();
                    break;
                }
                else if (response == 4)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
                
                
            }
            
            if(response == 4)
                {
                    break;
                }
        }
    }
    
    private void doSearchCar(){
        /*
        Scanner scanner = new Scanner(System.in);
        Integer response;
        
        System.out.println("*** Holiday Reservation Client :: Search Car ***\n");
        
        OrderTypeEnum searchType = OrderTypeEnum.CATEGORY;
        Long categoryId = 0l;
        Long modelId = 0l;
        
        System.out.print("Enter the start date (yyyy-mm-dd): ");
        String inStartDate = scanner.nextLine().trim();
        
        Reservation lolita = new Reservation();
        Time nitea = new Time();
        nitea.setTime(0);
       lolita.setPickupTime(value);
        
        Date startDate = new Date(0,0,0);
        
        try{
            startDate = new SimpleDateFormat("yyyy-MM-dd").parse(inStartDate);
        } catch (DateTimeParseException ex){
            System.out.println("Invalid start date!");
            return;
        } catch (ParseException ex) {
            System.out.println("Date input is invalid!");
            System.out.println("[Access Denied]");
            return;
        }
        
        System.out.print("Enter pickup hour (0-23) : ");
        int time = scanner.nextInt();
        Time a = new Time();
        Date
        
        System.out.print("Enter the start date (yyyy-mm-dd): ");
        String inEndDate = scanner.nextLine().trim();
        
        Date endDate = new Date(0,0,0);
        
        try{
            endDate = new SimpleDateFormat("yyyy-MM-dd").parse(inEndDate);
        } catch (DateTimeParseException ex){
            System.out.println("Invalid start date!");
            return;
        } catch (ParseException ex) {
            System.out.println("Date input is invalid!");
            System.out.println("[Access Denied]");
            return;
        }
        
        
        
        System.out.print("Enter return hour (0-23) > ");
        time = scanner.nextInt();
        //Time endTime = new Time(time,0,0);
        
        
        List<Outlet> outlets = partnerRetrieveAllOutlets();
        System.out.printf("%5s%20s%20s%15s%15s\n", "No", "Name", "Addres", "Open Hrs", "Close Hrs");
        int i = 1;
        for(Outlet o : outlets) {
            Time open = o.getOpenHrs();
            Time close = o.getCloseHrs();
            System.out.printf("%10s%20s%20s%15s%15s\n", i, o.getName(), o.getAddress(), open, close);
            i++;
        }
        System.out.println();
        
        System.out.print("Enter pickup outlet no > ");
        int pickupNo = scanner.nextInt();
        Outlet pickupLocation = outlets.get(pickupNo - 1);
        System.out.print("Enter return outlet no > ");
        int returnNo = scanner.nextInt();
        Outlet returnLocation = outlets.get(returnNo - 1);
        boolean success = false;
        String yesNo = "";
        double total = 0;
        
        do
        {
            System.out.println("Search Car By: ");
            System.out.println("1: Category");
            System.out.println("2: Model\n");
            System.out.print("> ");
            response = scanner.nextInt();
            
            if(response >= 1 && response <= 2)
            {
                if(response == 1) 
                {
                    searchType = OrderTypeEnum.CATEGORY;
                    System.out.printf("%10s%20s\n", "Category Id", "Category Name");
                    for(Category c : partnerRetrieveAllCategories()) {
                        System.out.printf("%10s%20s\n", c.getCategoryId(), c.getCategoryName());
                    }
                    System.out.print("Enter category id: ");
                    categoryId = scanner.nextLong();
                    
                } 
                else if(response == 2) 
                {
                    searchType = OrderTypeEnum.MODEL;
                    System.out.printf("%10s%10s%20s\n", "Model Id", "Make", "Model");
                    for(Model m : partnerRetrieveAllModels()) {
                        System.out.printf("%10s%10s%20s\n", m.getModelId(), m.getMake(), m.getModelName());
                    }
                    System.out.print("Enter model id: ");
                    modelId = scanner.nextLong();
                }
                
                try
                {
                    GregorianCalendar c = new GregorianCalendar();
                    c.setTime(startDate);
                    XMLGregorianCalendar xmlStartDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
                    c.setTime(endDate);
                    XMLGregorianCalendar xmlEndDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
                    
                    Time lol = objfac.createTime();
                    
                    success = partnerSearchCar(searchType.toString(), xmlStartDate, xmlEndDate, startTime, lol, pickupNo, returnNo, categoryId, modelId);
                    try {
                        total = rateSessionBeanRemote.retrieveTotalByCategory(categoryId, startDate, endDate);
                        System.out.println("Total rental rate: $" + total);
                    } 
                    catch (CategoryNotFoundException ex) 
                    {
                        System.out.println("An error occurred while retrieving rental rate: Car Category Not Found!");
                        return;
                    } 
                    catch (RateNotFoundException ex) 
                    {
                        System.out.println("An error occurred while retrieving rental rate: Rental Rate Not Found!");
                        return;
                    }
                }
                catch(OutletNotFoundException ex)
                {
                    System.out.println("Invalid input! Outlet not found");
                    return;
                }
                
                if(success) 
                {
                    break;
                } 
                else 
                {
                    System.out.println("No available car matches the searching criteria");
                    System.out.print("Do you want to search again? Y/N > ");
                    yesNo = scanner.nextLine().trim().toUpperCase();
                }
            }
            else
            {
                System.out.println("Invalid option, please try again!\n");
            }
        } while(!yesNo.equals("N"));
        
        
        if(success && currentMember != null) 
        {
            System.out.println("------------------------");
            System.out.println("1: Make Reservation");
            System.out.println("2: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();

            if(response == 1)
            {
                    System.out.print("Enter credit card number: ");
                    Long ccNum = scanner.nextLong();
                    PaidStatusEnum payStatus = PaidStatusEnum.UNPAID;
                    
                    while(true) {
                        System.out.println("Select Payment Method:");
                        System.out.println("1. Pay Now");
                        System.out.println("2. Pay Later at pick up time");
                        System.out.print("> ");
                        Integer payRes = scanner.nextInt();
                        
                        if(payRes >= 1 && payRes <= 2) 
                        {
                            if(payRes == 1) 
                            {
                                System.out.println("Payment Successful!");
                                payStatus = PaidStatusEnum.PAID;
                            }
                            break;
                        }
                        else
                        {
                            System.out.println("Invalid option, please try again!\n");
                        }
                    }
                    
                    Customer customer = new Customer(currentMember.getName(), ccNum, currentMember.getEmail(), CustomerTypeEnum.MEMBER);
                    Reservation r = new Reservation(payStatus, total, startDate, endDate, startTime, endTime, OrderTypeEnum.CATEGORY, customer);
                    r.setPickupLocation(pickupLocation);
                    r.setReturnLocation(returnLocation);
                
                    long theId = 0;
                    try
                    {
                        theId = reservationSessionBeanRemote.createMemberReservation(r, currentMember.getMemberId(), ccNum, pickupNo, returnNo, categoryId, modelId);
                    }
                    catch(OutletNotFoundException ex)
                    {
                        System.out.println("Invalid input. Outlet not found!");
                        return;
                    }
                    catch(MemberNotFoundException ex) 
                    {
                        System.out.println("Member not found!");
                    }
                    System.out.println("Reservation successful! The id is "+theId);
            }
        }*/
    }
    
    private void doViewResDetails(){
        Scanner scanner = new Scanner(System.in);
        Integer response;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy HH:mm");
        
        System.out.println("*** Holiday Reservation Client :: View Reservation Details ***\n");
        System.out.print("Enter reservation id> ");
        Long id = scanner.nextLong();
        
        Reservation r;
        try {
            r = partnerRetrieveReservationById(curPartner.getPartnerId(), id);
        } catch (ReservationNotFoundException_Exception ex) {
                System.out.println("Reservation Not Found!");
                return;
        } catch (PartnerNotFoundException_Exception ex) {
                System.out.println("Partner Not Found!");
                return;
        } catch (InvalidRelationIdException_Exception ex) {
                System.out.println("Reservation Not Found!");
                return;
        }
        
        if(r==null){
            System.out.println("There is no such reservationr record!");
            System.out.println("[Action Denied]");
            return;
        }
        
        XMLGregorianCalendar startDate = r.getPickupDate();
        XMLGregorianCalendar endDate = r.getReturnDate();
        
        System.out.printf("%8s%20s%20s%20s%20s\n", "Res Id", "Pick Up Date", "Return Date",
                "PickUp Location", "Return Location");
        System.out.printf("%8s%20s%20s%20s%20s\n", id, startDate, endDate, 
                r.getPickupLocation().getName(), r.getReturnLocation().getName());
        System.out.println("Reservation Status: " + r.getResStatus());
        System.out.println("Total Amount: " + r.getTotal());
        System.out.println("Payment Status: " + r.getPaymentStatus());
        System.out.println();
        
        System.out.println("------------------------");
        System.out.println("1: Cancel Reservation");
        System.out.println("2: Back\n");
        System.out.print("> ");
        response = scanner.nextInt();
        
        if(response == 1)
        {
            String reply = "";
            try {
                reply = partnerDoCancelReservation(curPartner.getPartnerId(), id);
            } catch (ReservationNotFoundException_Exception ex) {
                System.out.println("Reservation Not Found!");
                return;
            } catch (PartnerNotFoundException_Exception ex) {
                System.out.println("Partner Not Found!");
                return;
            } catch (InvalidRelationIdException_Exception ex) {
                System.out.println("Reservation Not Found!");
                return;
            }
            System.out.println(reply);
        }
        
    }
    
    private void doViewAllRes(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy HH:mm");
        
        
        
        System.out.println("*** Holiday Reservation Client :: View All My Reservations ***\n");
        List<Reservation> res = new ArrayList<>();
        try {
            res = partnerRetrieveAllReservations(curPartner.getPartnerId());
        } catch (PartnerNotFoundException_Exception ex) {
            System.out.println("Partner not found!");
            return;
        }
        
        if(res.size()==0){
            System.out.println("There is no reservation currently!");
            return;
        }
        
        System.out.printf("%8s%20s%20s%20s%20s\n", "Res Id", "PickUp Date", "Return Date", "Reservation Status", "Payment Status");
        for(Reservation r : res) {
            XMLGregorianCalendar startDate = r.getPickupDate();
            XMLGregorianCalendar endDate = r.getReturnDate();
            System.out.printf("%8s%20s%20s%20s%20s\n", r.getReservationId(), startDate, endDate, r.getResStatus(), r.getPaymentStatus());
        }
        System.out.println();
    }
    
    private static Partner partnerLogin(java.lang.String email, java.lang.String password) throws InvalidLoginCredentialException_Exception, PartnerNotFoundException_Exception {
        ws.client.MCRWebService_Service service = new ws.client.MCRWebService_Service();
        ws.client.MCRWebService port = service.getMCRWebServicePort();
        return port.partnerLogin(email, password);
    }

    private static long partnerCreateReservation(ws.client.Reservation reservation, long ccNum, long pickUpId, long returnId, long categoryId, long modelId, java.lang.String custName, java.lang.String custEmail, long partnerId) throws OutletNotFoundException_Exception {
        ws.client.MCRWebService_Service service = new ws.client.MCRWebService_Service();
        ws.client.MCRWebService port = service.getMCRWebServicePort();
        return port.partnerCreateReservation(reservation, ccNum, pickUpId, returnId, categoryId, modelId, custName, custEmail, partnerId);
    }

    private static java.util.List<ws.client.Outlet> partnerRetrieveAllOutlets() {
        ws.client.MCRWebService_Service service = new ws.client.MCRWebService_Service();
        ws.client.MCRWebService port = service.getMCRWebServicePort();
        return port.partnerRetrieveAllOutlets();
    }

    private static java.util.List<ws.client.Category> partnerRetrieveAllCategories() {
        ws.client.MCRWebService_Service service = new ws.client.MCRWebService_Service();
        ws.client.MCRWebService port = service.getMCRWebServicePort();
        return port.partnerRetrieveAllCategories();
    }

    private static java.util.List<ws.client.Model> partnerRetrieveAllModels() {
        ws.client.MCRWebService_Service service = new ws.client.MCRWebService_Service();
        ws.client.MCRWebService port = service.getMCRWebServicePort();
        return port.partnerRetrieveAllModels();
    }

    private static boolean partnerSearchCar(java.lang.String searchType, javax.xml.datatype.XMLGregorianCalendar startDate, javax.xml.datatype.XMLGregorianCalendar endDate, ws.client.Time startTime, ws.client.Time endTime, long pickUpId, long returnId, long categoryId, long modelId) throws OutletNotFoundException_Exception {
        ws.client.MCRWebService_Service service = new ws.client.MCRWebService_Service();
        ws.client.MCRWebService port = service.getMCRWebServicePort();
        return port.partnerSearchCar(searchType, startDate, endDate, startTime, endTime, pickUpId, returnId, categoryId, modelId);
    }

    private static double partnerRetrieveTotalByCategory(long catId, javax.xml.datatype.XMLGregorianCalendar startDate, javax.xml.datatype.XMLGregorianCalendar endDate) throws CategoryNotFoundException_Exception, RateNotFoundException_Exception {
        ws.client.MCRWebService_Service service = new ws.client.MCRWebService_Service();
        ws.client.MCRWebService port = service.getMCRWebServicePort();
        return port.partnerRetrieveTotalByCategory(catId, startDate, endDate);
    }

    private static Reservation partnerRetrieveReservationById(long partnerId, long reservationId) throws ReservationNotFoundException_Exception, InvalidRelationIdException_Exception, PartnerNotFoundException_Exception {
        ws.client.MCRWebService_Service service = new ws.client.MCRWebService_Service();
        ws.client.MCRWebService port = service.getMCRWebServicePort();
        return port.partnerRetrieveReservationById(partnerId, reservationId);
    }

    private static String partnerDoCancelReservation(long partnerId, long reservationId) throws ReservationNotFoundException_Exception, PartnerNotFoundException_Exception, InvalidRelationIdException_Exception {
        ws.client.MCRWebService_Service service = new ws.client.MCRWebService_Service();
        ws.client.MCRWebService port = service.getMCRWebServicePort();
        return port.partnerDoCancelReservation(partnerId, reservationId);
    }

    private static java.util.List<ws.client.Reservation> partnerRetrieveAllReservations(long partnerId) throws PartnerNotFoundException_Exception {
        ws.client.MCRWebService_Service service = new ws.client.MCRWebService_Service();
        ws.client.MCRWebService port = service.getMCRWebServicePort();
        return port.partnerRetrieveAllReservations(partnerId);
    }
    
    
}
