/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationclient;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.XMLGregorianCalendar;
import ws.client.Category;
import ws.client.CategoryNotFoundException_Exception;
import ws.client.InvalidLoginCredentialException;
import ws.client.InvalidLoginCredentialException_Exception;
import ws.client.InvalidRelationIdException_Exception;
import ws.client.Model;
import ws.client.Outlet;
import ws.client.OutletNotFoundException_Exception;
import ws.client.Partner;
import ws.client.PartnerNotFoundException_Exception;
import ws.client.RateNotFoundException_Exception;
import ws.client.Reservation;
import ws.client.ReservationNotFoundException_Exception;

/**
 *
 * @author User
 */
public class HolidayReservationClient {
    
    private static Partner currPartner;

    
    public static void main(String[] args) {
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Welcome to Holiday Reservation System  ***\n");
            System.out.println("1: Login");
            System.out.println("2: Search Car");
            System.out.println("3: Exit\n");
            response = 0;
            
            while(response < 1 || response > 3)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1) 
                {                 
                    try {
                        doLogin();
                        System.out.println("Login successful!\n");
                        
                        menuMain();    
                    } catch (InvalidLoginCredentialException_Exception ex) {
                        System.out.println("An error has occurred while logging in: " + ex.getMessage() + "\n");
                    } catch (PartnerNotFoundException_Exception ex) {
                        System.out.println("An error has occurred while logging in: " + ex.getMessage() + "\n");
                    }
                                 
                }
                else if(response == 2)
                {
                    doSearchCar();
                }
                else if (response == 3)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            
                if(response == 3)
                {
                    break;
                }
            }
        }
    }
    
    private static void doLogin() throws InvalidLoginCredentialException_Exception, PartnerNotFoundException_Exception {
        Scanner scanner = new Scanner(System.in);
        String email = "";
        String password = "";
        
        System.out.println("*** CaRMS Reservation Client :: Login ***\n");
        System.out.print("Enter email> ");
        email = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();
        
        currPartner = partnerLogin(email, password);
    }
    
    private static void menuMain() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Holiday Reservation System ***\n");
            System.out.println("You are logged in!\n");
            System.out.println("1: Search Car");
            System.out.println("2: View Reservation Details");
            System.out.println("3: View All Reservations");
            System.out.println("4: Logout\n");
            response = 0;
            
            while(response < 1 || response > 4)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1)
                {
                    doSearchCar();
                }
                else if (response == 2) 
                {
                    doViewResDetails();
                }
                else if (response == 3) 
                {
                    doViewAllRes();
                }
                else if (response == 4)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
                
                if(response == 4)
                {
                    break;
                }
            }
        }
    }
    
    private static void doSearchCar() {
        Scanner scanner = new Scanner(System.in);
        Integer response;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        System.out.println("*** Holiday Reservation System :: Search Car ***\n");
        
        String searchType = "";
        Integer orderRes = 1;
        Long categoryId = 0l;
        Long modelId = 0l;
        
        System.out.print("Enter pickup date (dd/mm/yyyy) > ");
        LocalDate startDate = LocalDate.parse(scanner.nextLine().trim(), dateFormatter);
        System.out.print("Enter pickup time (HH:mm) > ");
        LocalTime startTime = LocalTime.parse(scanner.nextLine().trim(), timeFormatter);

        System.out.print("Enter return date (dd/mm/yyyy) > ");
        LocalDate endDate = LocalDate.parse(scanner.nextLine().trim(), dateFormatter);
        System.out.print("Enter return time (HH:mm) > ");
        LocalTime endTime = LocalTime.parse(scanner.nextLine().trim(), timeFormatter);
        
        List<Outlet> outlets = partnerRetrieveAllOutlets();
        System.out.printf("%10s%20s%20s%15s%15s\n", "Outlet ID", "Name", "Addres", "Open Hrs", "Close Hrs");
        int i = 1;
        for(Outlet o : outlets) {
            ws.client.LocalTime open = o.getOpenHrs();
            ws.client.LocalTime close = o.getCloseHrs();
            System.out.printf("%10s%20s%20s%15s%15s\n", o.getOutletId(), o.getName(), o.getAddress(), open, close);
            i++;
        }
        System.out.println();
        
        System.out.print("Enter pickup outlet id > ");
        int pickupId = scanner.nextInt();
        System.out.print("Enter return outlet id > ");
        int returnId = scanner.nextInt();
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
                    searchType = "category";
                    System.out.printf("%10s%20s\n", "Category Id", "Category Name");
                    for(Category c : partnerRetrieveAllCategories()) {
                        System.out.printf("%10s%20s\n", c.getCategoryId(), c.getCategoryName());
                    }
                    System.out.print("Enter category id: ");
                    categoryId = scanner.nextLong();
                    
                } 
                else if(response == 2) 
                {
                    searchType = "model";
                    orderRes = 2;
                    System.out.printf("%10s%10s%20s\n", "Model Id", "Make", "Model");
                    for(Model m : partnerRetrieveAllModels()) {
                        System.out.printf("%10s%10s%20s\n", m.getModelId(), m.getMake(), m.getModelName());
                    }
                    System.out.print("Enter model id: ");
                    modelId = scanner.nextLong();
                }
                
                try
                {
                    success = partnerSearchCar(searchType, startDate, endDate, 
                            startTime, endTime, pickupId, returnId, categoryId, modelId);
                    try {
                        total = partnerRetrieveTotalByCategory(categoryId, startDate, endDate);
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
        } while(!yesNo.equals("N"));
        
        if(success && currPartner != null) 
        {
            System.out.println("------------------------");
            System.out.println("1: Make Reservation");
            System.out.println("2: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();

            if(response == 1)
            {
                    System.out.print("Enter customer name: ");
                    String custName = scanner.nextLine().trim();
                    System.out.print("Enter customer email: ");
                    String email = scanner.nextLine().trim();
                    System.out.print("Enter credit card number: ");
                    Long ccNum = scanner.nextLong();
                    
                    Integer payRes;
                    
                    while(true) {
                        System.out.println("Select Payment Method:");
                        System.out.println("1. Pay Now");
                        System.out.println("2. Pay Later at pick up time");
                        System.out.print("> ");
                        payRes = scanner.nextInt();
                        
                        if(payRes >= 1 && payRes <= 2) 
                        {
                            if(payRes == 1) 
                            {
                                System.out.println("Payment Successful!");
                            }
                            break;
                        }
                        else
                        {
                            System.out.println("Invalid option, please try again!\n");
                        }
                    }
                
                    long theId = 0;
                    try
                    {
                        theId = partnerCreateReservation(payRes, total, startDate, endDate, startTime, endTime, 
                                orderRes, pickupNo, returnNo, categoryId, modelId, custName, email, ccNum);
                        
                    }
                    catch(OutletNotFoundException_Exception ex)
                    {
                        System.out.println("Invalid input. Outlet not found!");
                        return;
                    }
                    System.out.println("Reservation successful! The id is "+theId);
            }
        }
    }
    
    private static void doViewResDetails() {
        
    }
    
    private static void doViewAllRes() {
        
    }
    

    private static Partner partnerLogin(java.lang.String email, java.lang.String password) throws InvalidLoginCredentialException_Exception, PartnerNotFoundException_Exception {
        ws.client.MCRWebService_Service service = new ws.client.MCRWebService_Service();
        ws.client.MCRWebService port = service.getMCRWebServicePort();
        return port.partnerLogin(email, password);
    }

    private static boolean partnerSearchCar(java.lang.String searchType, ws.client.LocalDate startDate, ws.client.LocalDate endDate, ws.client.LocalTime startTime, ws.client.LocalTime endTime, long pickUpId, long returnId, long categoryId, long modelId) throws OutletNotFoundException_Exception {
        ws.client.MCRWebService_Service service = new ws.client.MCRWebService_Service();
        ws.client.MCRWebService port = service.getMCRWebServicePort();
        return port.partnerSearchCar(searchType, startDate, endDate, startTime, endTime, pickUpId, returnId, categoryId, modelId);
    }

    private static long partnerCreateReservation(java.lang.Integer payRes, java.lang.Double totalAmt, ws.client.LocalDate startDate, ws.client.LocalDate endDate, ws.client.LocalTime startTime, ws.client.LocalTime endTime, java.lang.Integer orderTypeRes, long pickUpId, long returnId, long categoryId, long modelId, java.lang.String custName, java.lang.String custEmail, long ccNum) throws OutletNotFoundException_Exception {
        ws.client.MCRWebService_Service service = new ws.client.MCRWebService_Service();
        ws.client.MCRWebService port = service.getMCRWebServicePort();
        return port.partnerCreateReservation(payRes, totalAmt, startDate, endDate, startTime, endTime, orderTypeRes, pickUpId, returnId, categoryId, modelId, custName, custEmail, ccNum);
    }

    private static Reservation partnerRetrieveReservationById(long partnerId, long reservationId) throws PartnerNotFoundException_Exception, ReservationNotFoundException_Exception, InvalidRelationIdException_Exception {
        ws.client.MCRWebService_Service service = new ws.client.MCRWebService_Service();
        ws.client.MCRWebService port = service.getMCRWebServicePort();
        return port.partnerRetrieveReservationById(partnerId, reservationId);
    }

    private static java.util.List<ws.client.Reservation> partnerRetrieveAllReservations(long partnerId) throws PartnerNotFoundException_Exception {
        ws.client.MCRWebService_Service service = new ws.client.MCRWebService_Service();
        ws.client.MCRWebService port = service.getMCRWebServicePort();
        return port.partnerRetrieveAllReservations(partnerId);
    }

    private static String partnerDoCancelReservation(long partnerId, long reservationId) throws PartnerNotFoundException_Exception, ReservationNotFoundException_Exception, InvalidRelationIdException_Exception {
        ws.client.MCRWebService_Service service = new ws.client.MCRWebService_Service();
        ws.client.MCRWebService port = service.getMCRWebServicePort();
        return port.partnerDoCancelReservation(partnerId, reservationId);
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

    private static java.util.List<ws.client.Outlet> partnerRetrieveAllOutlets() {
        ws.client.MCRWebService_Service service = new ws.client.MCRWebService_Service();
        ws.client.MCRWebService port = service.getMCRWebServicePort();
        return port.partnerRetrieveAllOutlets();
    }

    private static double partnerRetrieveTotalByCategory(long catId, ws.client.LocalDate startDate, ws.client.LocalDate endDate) throws CategoryNotFoundException_Exception, RateNotFoundException_Exception {
        ws.client.MCRWebService_Service service = new ws.client.MCRWebService_Service();
        ws.client.MCRWebService port = service.getMCRWebServicePort();
        return port.partnerRetrieveTotalByCategory(catId, startDate, endDate);
    }
    
}

    

    
    

