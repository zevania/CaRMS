/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationclientapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import ws.client.Category;
import ws.client.CategoryNotFoundException_Exception;
import ws.client.Customer;
import ws.client.CustomerTypeEnum;
import ws.client.InvalidLoginCredentialException_Exception;
import ws.client.InvalidRelationIdException_Exception;
import ws.client.Model;
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
    SimpleDateFormat dateformatter = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat timeformatter = new SimpleDateFormat("HH:mm");
    public void runApp(){
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Welcome to Holiday Reservation Client  ***\n");
            System.out.println("1: Login");
            System.out.println("2: Search car");
            System.out.println("3: Exit\n");
            response = 0;
            
            while(response < 1 || response > 3)
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
                } else if (response == 2){
                    doSearchCar();
                    break;
                }
                else if (response == 3)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 3){
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
        Scanner scanner = new Scanner(System.in);
        Integer response;
        
        System.out.println("*** Holiday Reservation Client :: Search Car ***\n");
        
        OrderTypeEnum searchType = OrderTypeEnum.CATEGORY;
        Long categoryId = 0l;
        Long modelId = 0l;
        
        Reservation res = new Reservation();
        
        System.out.print("Enter pickup date (yyyy-mm-dd): ");
        String inStartDate = scanner.nextLine().trim();
        
        Date startDate;
        XMLGregorianCalendar xmlStartDate;
        
        try{
            startDate = new SimpleDateFormat("yyyy-MM-dd").parse(inStartDate);
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(startDate);
            xmlStartDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            res.setPickupDate(xmlStartDate);
        } catch (DateTimeParseException ex){
            System.out.println("Invalid pickup date!");
            return;
        } catch (ParseException ex) {
            System.out.println("Date input is invalid!");
            System.out.println("[Access Denied]");
            return;
        } catch (DatatypeConfigurationException ex) {
            System.out.println("Invalid pickup date!");
            return;
        }
        
        Date startTime;
        XMLGregorianCalendar xmlStartTime;
        int hour;
        int min;
        
        while(true) 
        {
            System.out.print("Enter pickup hour (0-23) : ");
            hour = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter pickup minute (0-59) : ");
            min = scanner.nextInt();
            scanner.nextLine();
            startTime = new Date(2000,1,1, hour, min, 0);
            
            if(hour>=0 && hour<=23 && min>=0 && min<=59) 
            {
                GregorianCalendar c = new GregorianCalendar();
                c.setTime(startTime);
                try 
                {
                    xmlStartTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
                    res.setPickupTime(xmlStartTime);
                    break;
                } catch (DatatypeConfigurationException ex) {
                    System.out.println("Invalid pickup time");
                    return;
                }
            }
            else 
            {
                System.out.println("Invalid hour/minute! Please try again.\n");
            }
        }
        
        System.out.print("Enter the return date (yyyy-mm-dd): ");
        String inEndDate = scanner.nextLine().trim();
        
        Date endDate;
        XMLGregorianCalendar xmlReturnDate;
        
        try{
            endDate = new SimpleDateFormat("yyyy-MM-dd").parse(inEndDate);
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(endDate);
            xmlReturnDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            res.setReturnDate(xmlReturnDate);
           
        } catch (DateTimeParseException ex){
            System.out.println("Invalid return date!");
            return;
        } catch (ParseException ex) {
            System.out.println("Date input is invalid!");
            System.out.println("[Access Denied]");
            return;
        } catch (DatatypeConfigurationException ex) {
            System.out.println("Invalid return date!");
            return;
        }
        
        Date endTime;
        XMLGregorianCalendar xmlEndTime;
        
        while(true) 
        {
            System.out.print("Enter return hour (0-23) : ");
            hour = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter return minute (0-59) : ");
            min = scanner.nextInt();
            scanner.nextLine();
            endTime = new Date(2000,1,1, hour, min, 0);
            
            if(hour>=0 && hour<=23 && min>=0 && min<=59) 
            {
                GregorianCalendar c = new GregorianCalendar();
                c.setTime(endTime);
                try {
                    xmlEndTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
                    res.setReturnTime(xmlEndTime);
                    break;
                } catch (DatatypeConfigurationException ex) {
                    System.out.println("Invalid return time");
                    return;
                }
            }
            else 
            {
                System.out.println("Invalid hour/minute! Please try again.\n");
            }
        }
        
        List<Outlet> outlets = partnerRetrieveAllOutlets();
        System.out.printf("%10s%20s%20s%15s%15s\n", "Outlet Id", "Name", "Addres", "Open Hrs", "Close Hrs");
        int i = 1;
        for(Outlet o : outlets) {
            XMLGregorianCalendar open = o.getOpenHrs();
            XMLGregorianCalendar close = o.getCloseHrs();
            
            Date openTime = open.toGregorianCalendar().getTime();
            Date closeTime = close.toGregorianCalendar().getTime();
            
            System.out.printf("%10s%20s%20s%15s%15s\n", o.getOutletId(), o.getName(), o.getAddress(), timeformatter.format(openTime), timeformatter.format(closeTime));
            i++;
        }
        System.out.println();
        
        System.out.print("Enter pickup outlet id > ");
        int pickupId = scanner.nextInt();
        scanner.nextLine();
        Outlet pickupLocation;
        try {
            pickupLocation = partnerRetrieveOutletById(pickupId);
        } catch (OutletNotFoundException_Exception ex) {
            System.out.println(ex.getMessage());
            return;
        }
        System.out.print("Enter return outlet id > ");
        int returnId = scanner.nextInt();
        scanner.nextLine();
        Outlet returnLocation;
        try {
            returnLocation = partnerRetrieveOutletById(returnId);
        } catch (OutletNotFoundException_Exception ex) {
            System.out.println(ex.getMessage());
            return;
        }
        
        boolean success = false;
        String yesNo = "";
        double total = 0;
        
        do
        {
            total = 0;
            System.out.println("\nSearch Car By: ");
            System.out.println("1: Category");
            System.out.println("2: Model\n");
            System.out.print("> ");
            response = scanner.nextInt();
            scanner.nextLine();
            
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
                    scanner.nextLine();
                } 
                else if(response == 2) 
                {
                    searchType = OrderTypeEnum.MODEL;
                    System.out.printf("%10s%20s\n", "Make", "Model");
                    List<Model> models = partnerRetrieveAllModels();
                    int counter = 1;
                    for(Model m : models) {
                        System.out.print(counter + " ");
                        System.out.printf("%10s%20s\n", m.getMake(), m.getModelName());
                        counter++;
                    }
                    System.out.print("Enter your choice: ");
                    int choice = scanner.nextInt();
                    
                    if(choice<1 || choice>=counter){
                        System.out.println("Invalid choice");
                        return;
                    }
                    modelId = models.get(choice-1).getModelId();
                    categoryId = models.get(choice-1).getCategory().getCategoryId();
                    scanner.nextLine();
                }
                
                try
                {
                    success = partnerSearchCar(searchType.toString(), xmlStartDate, xmlReturnDate, xmlStartTime, xmlEndTime, pickupId, returnId, categoryId, modelId);
                    try 
                    {
                        total = partnerRetrieveTotalByCategory(categoryId, xmlStartDate, xmlReturnDate);
                        System.out.println("Total rental rate: $" + total);
                    } 
                    catch (CategoryNotFoundException_Exception ex) 
                    {
                        System.out.println("An error occurred while retrieving rental rate: Car Category Not Found!");
                        return;
                    } 
                    catch (RateNotFoundException_Exception ex) 
                    {
                        System.out.println("An error occurred while retrieving rental rate: Rental Rate Not Found!");
                        return;
                    }
                }
                catch(OutletNotFoundException_Exception ex)
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
            System.out.println();
        } while(!yesNo.equals("N"));
        
        
        if(success && curPartner != null) 
        {
            while(true) {
                System.out.println("------------------------");
                System.out.println("1: Make Reservation");
                System.out.println("2: Back\n");
                System.out.print("> ");
                response = scanner.nextInt();
                scanner.nextLine();
                
                if(response >= 1 && response <= 2) 
                {
                    if(response == 1)
                    {
                        System.out.print("Enter customer name: ");
                        String name = scanner.nextLine().trim();
                        System.out.print("Enter customer email: ");
                        String email = scanner.nextLine().trim();
                        System.out.print("Enter credit card number: ");
                        Long ccNum = scanner.nextLong();
                        scanner.nextLine();
                        PaidStatusEnum payStatus = PaidStatusEnum.UNPAID;

                        while(true) 
                        {
                            System.out.println("Select Payment Method:");
                            System.out.println("1. Pay Now");
                            System.out.println("2. Pay Later at pick up time");
                            System.out.print("> ");
                            Integer payRes = scanner.nextInt();
                            scanner.nextLine();

                            if(payRes >= 1 && payRes <= 2) 
                            {
                                if(payRes == 1) 
                                {
                                    System.out.println("Payment Successful!");
                                    payStatus = PaidStatusEnum.PAID;
                                } else if(payRes == 2){
                                    System.out.println("Payment is defferred");
                                    payStatus = PaidStatusEnum.UNPAID;
                                }
                                break;
                            }
                            else
                            {
                                System.out.println("Invalid option, please try again!\n");
                            }
                        }

                        Customer customer = new Customer();
                        customer.setName(name);
                        customer.setCcNum(ccNum);
                        customer.setEmail(email);
                        customer.setCustomerType(CustomerTypeEnum.PARTNER);
                        
                        res.setPaymentStatus(payStatus);
                        res.setOrderType(searchType);
                        res.setCustomer(customer);
                        res.setTotal(total);
                        res.setPickupLocation(pickupLocation);
                        res.setReturnLocation(returnLocation);
                        res.setResStatus(ResStatusEnum.ORDERED);
                        
                        long theId = 0;
                        try
                        {
                            theId = partnerCreateReservation(res, ccNum, pickupId, returnId, categoryId, modelId, name, email, curPartner.getPartnerId());
                            if(theId == -1) {
                                System.out.println("An error occured while creating the reservation");
                                System.out.println("Name/CCNum/Email is too long. Customer cannot be created\n");
                                return;
                            }
                        }
                        catch(OutletNotFoundException_Exception ex)
                        {
                            System.out.println("Invalid input. Outlet not found!");
                            return;
                        }
                        System.out.println("Reservation successful! The id is "+theId);
                        break;
                    }
                    break;
                }
            }
        }
        else if(curPartner == null)
        {
            System.out.println("Login to make a reservation!\n");
        }
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
        
        if(r == null){
            System.out.println("There is no such reservation record!");
            System.out.println("[Action Denied]");
            return;
        }
        
        XMLGregorianCalendar startDate = r.getPickupDate();
        XMLGregorianCalendar endDate = r.getReturnDate();
        
        Date aDate = startDate.toGregorianCalendar().getTime();
        Date bDate = endDate.toGregorianCalendar().getTime();
        
        System.out.printf("%8s%20s%20s%20s%20s\n", "Res Id", "Pick Up Date", "Return Date",
                "PickUp Location", "Return Location");
        System.out.printf("%8s%20s%20s%20s%20s\n", id, dateformatter.format(aDate), dateformatter.format(bDate), 
                r.getPickupLocation().getName(), r.getReturnLocation().getName());
        System.out.println("Reservation Status: " + r.getResStatus());
        System.out.println("Total Amount: " + r.getTotal());
        System.out.println("Payment Status: " + r.getPaymentStatus());
        System.out.println();
        
        while(true) {
            System.out.println("------------------------");
            System.out.println("1: Cancel Reservation");
            System.out.println("2: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();

            if(response >= 1 && response <= 2) 
            {
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
                break;
            }
            else 
            {
                System.out.println("Invalid option. Please try again.\n");
            }
        }
    }
    
    private void doViewAllRes(){
        Scanner sc = new Scanner(System.in);
        
        System.out.println("*** Holiday Reservation Client :: View All My Reservations ***\n");
        List<Reservation> res = new ArrayList<>();
        try {
            res = partnerRetrieveAllReservations(curPartner.getPartnerId());
        } catch (PartnerNotFoundException_Exception ex) {
            System.out.println("Partner not found!");
            return;
        }
        
        if(res.isEmpty()){
            System.out.println("There is currently no reservation!");
            return;
        }
        
        XMLGregorianCalendar startDate;
        XMLGregorianCalendar endDate;
        System.out.printf("%8s%20s%20s%20s%20s\n", "Res Id", "PickUp Date", "Return Date", "Reservation Status", "Payment Status");
        for(Reservation r : res) {
            startDate = r.getPickupDate();
            
            endDate = r.getReturnDate();
            Date aDate = startDate.toGregorianCalendar().getTime();
            Date bDate = endDate.toGregorianCalendar().getTime();
                    
            System.out.printf("%8s%20s%20s%20s%20s\n", r.getReservationId(), dateformatter.format(aDate), dateformatter.format(bDate), r.getResStatus(), r.getPaymentStatus());
        }
        System.out.println();
        System.out.println("Press enter to continue!");
        sc.nextLine();
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

    private static boolean partnerSearchCar(java.lang.String searchType, javax.xml.datatype.XMLGregorianCalendar startDate, javax.xml.datatype.XMLGregorianCalendar endDate, javax.xml.datatype.XMLGregorianCalendar startTime, javax.xml.datatype.XMLGregorianCalendar endTime, long pickUpId, long returnId, long categoryId, long modelId) throws OutletNotFoundException_Exception {
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

    private static Outlet partnerRetrieveOutletById(long outletId) throws OutletNotFoundException_Exception {
        ws.client.MCRWebService_Service service = new ws.client.MCRWebService_Service();
        ws.client.MCRWebService port = service.getMCRWebServicePort();
        return port.partnerRetrieveOutletById(outletId);
    }
}
