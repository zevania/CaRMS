package carmsreservationclient;

import ejb.session.stateless.CategorySessionBeanRemote;
import ejb.session.stateless.MemberSessionBeanRemote;
import ejb.session.stateless.ModelSessionBeanRemote;
import ejb.session.stateless.OutletSessionBeanRemote;
import ejb.session.stateless.RateSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import entity.Category;
import entity.Customer;
import entity.OurMember;
import entity.Model;
import entity.Outlet;
import entity.Reservation;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.enumeration.CategoryNotFoundException;
import util.enumeration.CustomerTypeEnum;
import util.enumeration.OrderTypeEnum;
import util.enumeration.PaidStatusEnum;
import util.exception.InvalidLoginCredentialException;
import util.exception.MemberEmailExistException;
import util.exception.OutletNotFoundException;
import util.exception.RateNotFoundException;
import util.exception.IncompleteRegistrationDetailsException;
import util.exception.MemberNotFoundException;


public class MainApp {
    
    private ReservationSessionBeanRemote reservationSessionBeanRemote;
    private MemberSessionBeanRemote memberSessionBeanRemote;
    private OutletSessionBeanRemote outletSessionBeanRemote;
    private CategorySessionBeanRemote categorySessionBeanRemote;
    private ModelSessionBeanRemote modelSessionBeanRemote;
    private RateSessionBeanRemote rateSessionBeanRemote;
    
    private OurMember currentMember;

    public MainApp() {
    }

    public MainApp(ReservationSessionBeanRemote reservationSessionBeanRemote, MemberSessionBeanRemote memberSessionBeanRemote, 
            OutletSessionBeanRemote outletSessionBeanRemote, CategorySessionBeanRemote categorySessionBeanRemote, 
            ModelSessionBeanRemote modelSessionBeanRemote, RateSessionBeanRemote rateSessionBeanRemote) {
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
        this.memberSessionBeanRemote = memberSessionBeanRemote;
        this.outletSessionBeanRemote = outletSessionBeanRemote;
        this.categorySessionBeanRemote = categorySessionBeanRemote;
        this.modelSessionBeanRemote = modelSessionBeanRemote;
        this.rateSessionBeanRemote = rateSessionBeanRemote;
    }
    
    public void runApp() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Welcome to CaRMS Reservation Client  ***\n");
            System.out.println("1: Register as Customer");
            System.out.println("2: Login");
            System.out.println("3: Search Car");
            System.out.println("4: Exit\n");
            response = 0;
            
            while(response < 1 || response > 4)
            {
                System.out.print("> ");

                response = scanner.nextInt();
                scanner.nextLine();
                System.out.println();
                
                if(response == 1) 
                {
                    try {
                        doCreateMember();
                    } catch (IncompleteRegistrationDetailsException ex) {
                        ex.getMessage();
                    }
                }
                else if(response == 2)
                {
                    try
                    {
                        doLogin();
                        System.out.println("Login successful!\n");
                        
                        menuMain();
                    }
                    catch(InvalidLoginCredentialException ex) 
                    {
                        System.out.println("An error has occurred while logging in: " + ex.getMessage() + "\n");
                    }
                }
                else if (response == 3)
                {
                    doSearchCar();
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
    
    private void doLogin() throws InvalidLoginCredentialException {
        Scanner scanner = new Scanner(System.in);
        String email = "";
        String password = "";
        
        System.out.println();
        System.out.println("*** CaRMS Reservation Client :: Login ***\n");
        System.out.print("Enter email> ");
        email = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();
        
        if(email.length() > 0 && password.length() > 0)
        {
            currentMember = memberSessionBeanRemote.memberLogin(email, password);
        }
        else
        {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }
    }
    
    private void menuMain() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println();
            System.out.println("*** CaRMS Reservation Client ***\n");
            System.out.println("You are logged in as " + currentMember.getName() + "\n");
            System.out.println("1: Search Car");
            System.out.println("2: View Reservation Details");
            System.out.println("3: View All My Reservations");
            System.out.println("4: Logout\n");
            response = 0;
            
            while(response < 1 || response > 4)
            {
                System.out.print("> ");

                response = scanner.nextInt();
                scanner.nextLine();

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
                
                
            }
            if(response == 4)
                {
                    break;
                }
        }
    }
    
    private void doCreateMember() throws IncompleteRegistrationDetailsException {
        Scanner scanner = new Scanner(System.in);
        String name = "";
        String email = "";
        String password = "";
        
        System.out.println("*** CaRMS Reservation Client :: Create Member ***\n");
        System.out.print("Enter name> ");
        name = scanner.nextLine().trim();
        System.out.print("Enter email> ");
        email = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();
        
        if(name.length() > 0 && email.length() > 0 && password.length() > 0)
        {
            OurMember newMember = new OurMember(name, email, password);
            try 
            {
                Long newMemberId = memberSessionBeanRemote.createMember(newMember);
                System.out.println("New member created successfully!: " + newMemberId + "\n");
            }
            catch(MemberEmailExistException ex) 
            {
                System.out.println("An error occured while creating member: " + ex.getMessage() + "\n");
            }
        }
        else
        {
            throw new IncompleteRegistrationDetailsException("An error occured while creating member: Incomplete registration details!\n");
        }

    }
    
    private void doSearchCar() {
        Scanner scanner = new Scanner(System.in);
        Integer response;

        System.out.println("*** CaRMS Reservation Client :: Search Car ***\n");
        
        OrderTypeEnum searchType = OrderTypeEnum.CATEGORY;
        Long categoryId = 0l;
        Long modelId = 0l;
        
        System.out.print("Enter pickup date (yyyy-mm-dd): ");
        String inStartDate = scanner.nextLine().trim();
        
        Date startDate;
        
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
        
        Date startTime;
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
                break;
            }
            else 
            {
                System.out.println("Invalid hour/minute! Please try again.\n");
            }
        }
        
        System.out.print("Enter the return date (yyyy-mm-dd): ");
        String inEndDate = scanner.nextLine().trim();
        
        Date endDate;
        Date endTime;
        
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
        
        while(true) 
        {
            System.out.print("Enter return hour (0-23) > ");
            hour = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter return minute (0-59) > ");
            min = scanner.nextInt();
            scanner.nextLine();
            endTime = new Date(2000,1,1, hour, min, 0);
            
            if(hour>=0 && hour<=23 && min>=0 && min<=59) 
            {
                break;
            }
            else 
            {
                System.out.println("Invalid hour/minute! Please try again.\n");
            }
        }
        
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        
        List<Outlet> outlets = outletSessionBeanRemote.retrieveAllOutlets();
        System.out.printf("%10s%20s%20s%15s%15s\n", "Outlet Id", "Name", "Addres", "Open Hrs", "Close Hrs");
        int i = 1;
        for(Outlet o : outlets) {
            String open = dateFormat.format(o.getOpenHrs());
            String close = dateFormat.format(o.getCloseHrs());
            System.out.printf("%10s%20s%20s%15s%15s\n", o.getOutletId(), o.getName(), o.getAddress(), open, close);
            i++;
        }
        System.out.println();
        
        System.out.print("Enter pickup outlet id > ");
        int pickupId = scanner.nextInt();
        scanner.nextLine();
        Outlet pickupLocation;
        try {
            pickupLocation = outletSessionBeanRemote.retrieveOutletById((long)pickupId);
        } catch (OutletNotFoundException ex) {
            System.out.println(ex.getMessage());
            return;
        }
        System.out.print("Enter return outlet id > ");
        int returnId = scanner.nextInt();
        scanner.nextLine();
        Outlet returnLocation;
        try {
            returnLocation = outletSessionBeanRemote.retrieveOutletById((long)returnId);
        } catch (OutletNotFoundException ex) {
            System.out.println(ex.getMessage());
            return;
        }
        
        boolean success = false;
        String yesNo = "";
        double total = 0;
        
        OrderTypeEnum otype = OrderTypeEnum.CATEGORY;
        
        do
        {   
            total = 0;
            System.out.println("\n\nSearch Car By: ");
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
                    for(Category c : categorySessionBeanRemote.retrieveAllCategories()) {
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
                    List<Model> models = modelSessionBeanRemote.retrieveModels();
                    int counter = 1;
                    for(Model m : models) {
                        System.out.print(counter+" ");
                        System.out.printf("%10s%20s\n", m.getMake(), m.getModelName());
                        counter++;
                    }
                    System.out.print("Enter your choice: ");
                    int choice = scanner.nextInt();
                    
                    if(choice<1 || choice>=counter){
                        System.out.println("Invalid choice");
                        return;
                    }
                    otype = OrderTypeEnum.MODEL;
                    modelId = models.get(choice-1).getModelId();
                    categoryId = models.get(choice-1).getCategory().getCategoryId();
                    scanner.nextLine();
                }
                
               
                try
                {
                    success = reservationSessionBeanRemote.searchAvailableCar(searchType.toString(), startDate, 
                            endDate, startTime, endTime, pickupId, returnId, categoryId, modelId);
                    try 
                    {
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
                {   total = 0;
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
                                    System.out.println("Please wait for a while!");
                                }
                                break;
                            }
                            else
                            {
                                System.out.println("Invalid option, please try again!\n");
                            }
                        }

                        Customer customer = new Customer(currentMember.getName(), ccNum, currentMember.getEmail(), CustomerTypeEnum.MEMBER);
                        
                        Reservation r = new Reservation(payStatus, total, startDate, endDate, startTime, endTime, otype, customer);
                        r.setPickupLocation(pickupLocation);
                        r.setReturnLocation(returnLocation);

                        long theId = 0;
                        try
                        {
                            theId = reservationSessionBeanRemote.createMemberReservation(r, currentMember.getOurMemberId(), ccNum, pickupId, returnId, categoryId, modelId);
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
                    break;
                }
                else 
                {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
        }
        else 
        {
            System.out.println("\nLogin to make a reservation!\n");
        }
    }
    
    private void doViewResDetails() {
        Scanner scanner = new Scanner(System.in);
        Integer response;
        
        System.out.println("*** CaRMS Reservation Client :: View Reservation Details ***\n");
        System.out.print("Enter reservation id> ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        
        Reservation r = reservationSessionBeanRemote.retrieveReservationById(id);
        
        if(r == null){
            System.out.println("There is no such reservationr record!");
            System.out.println("[Action Denied]");
            return;
        }
        Date startDate = r.getPickupDate();
        Date endDate = r.getReturnDate();
        
        System.out.printf("%8s%20s%20s%20s%20s\n", "Res Id", "Pick Up Date", "Return Date",
                "PickUp Location", "Return Location");
        System.out.printf("%8s%20s%20s%20s%20s\n", id, startDate, endDate, 
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
            scanner.nextLine();

            if(response >= 1 && response <= 2)
            {
                if(response == 1) {
                    String reply = reservationSessionBeanRemote.cancelReservation(id);
                    System.out.println(reply);
                }
                break;
            }
            else 
            {
                System.out.println("Invalid option! Please try again.\n");
            }
        }
        
    }
    
    private void doViewAllRes() {
        Scanner sc = new Scanner(System.in);
        
        System.out.println("*** CaRMS Reservation Client :: View All My Reservations ***\n");
        List<Reservation> res = reservationSessionBeanRemote.retrieveReservations(currentMember.getEmail());
        
        if(res == null || res.isEmpty()){
            System.out.println("There is no reservaiton right now!");
            return;
        }
        
        System.out.printf("%8s%20s%20s%20s%20s\n", "Res Id", "PickUp Date", "Return Date", "Reservation Status", "Payment Status");
        for(Reservation r : res) {
            Date startDate = r.getPickupDate();
            Date endDate = r.getReturnDate();
            System.out.printf("%8s%20s%20s%20s%20s\n", r.getReservationId(), startDate, endDate, r.getResStatus(), r.getPaymentStatus());
        }
        System.out.println();
        System.out.println("Press enter to continue");
        sc.nextLine();
    }
}
