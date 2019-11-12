/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationclient;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import ws.client.InvalidLoginCredentialException;
import ws.client.InvalidLoginCredentialException_Exception;
import ws.client.OutletNotFoundException_Exception;
import ws.client.Partner;
import ws.client.PartnerNotFoundException_Exception;

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
        
        OrderTypeEnum searchType = OrderTypeEnum.CATEGORY;
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
        
        List<Outlet> outlets = outletSessionBeanRemote.retrieveAllOutlets();
        System.out.printf("%5s%20s%20s%15s%15s\n", "No", "Name", "Addres", "Open Hrs", "Close Hrs");
        int i = 1;
        for(Outlet o : outlets) {
            LocalTime open = o.getOpenHrs();
            String openTime = open.format(timeFormatter);
            LocalTime close = o.getCloseHrs();
            String closeTime = close.format(timeFormatter);
            System.out.printf("%10s%20s%20s%15s%15s\n", i, o.getName(), o.getAddress(), openTime, closeTime);
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
                    for(Category c : categorySessionBeanRemote.retrieveAllCategories()) {
                        System.out.printf("%10s%20s\n", c.getCategoryId(), c.getCategoryName());
                    }
                    System.out.print("Enter category id: ");
                    categoryId = scanner.nextLong();
                    
                } 
                else if(response == 2) 
                {
                    searchType = OrderTypeEnum.MODEL;
                    System.out.printf("%10s%10s%20s\n", "Model Id", "Make", "Model");
                    for(Model m : modelSessionBeanRemote.retrieveModels()) {
                        System.out.printf("%10s%10s%20s\n", m.getModelId(), m.getMake(), m.getModelName());
                    }
                    System.out.print("Enter model id: ");
                    modelId = scanner.nextLong();
                }
                
                try
                {
                    success = partnerSearchCar(searchType.toString(), startDate, endDate, startTime, endTime, pickupNo, returnNo, categoryId, modelId);
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
        
        if(success && currPartner != null) 
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
                    
                    Customer customer = new Customer(currentMember.getName(), ccNum, currentMember.getEmail(), CustomerTypeEnum.MEMBER);
                    Reservation r = new Reservation(PaidStatusEnum.PAID, total, startDate, endDate, startTime, endTime, OrderTypeEnum.CATEGORY, customer);
                    r.setPickupLocation(pickupLocation);
                    r.setReturnLocation(returnLocation);
                
                    long theId = 0;
                    try
                    {
                        try {
                            theId = reservationSessionBeanRemote.createMemberReservation(r, currentMember.getMemberId(), ccNum, pickupNo, returnNo, categoryId, modelId);
                        } catch (MemberNotFoundException ex) {
                            System.out.println("Member not found!");
                            return;
                        }
                    }
                    catch(OutletNotFoundException ex)
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
    
    
    
    
}

    

    
    

