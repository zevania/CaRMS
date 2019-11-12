package carmsreservationclient;

import ejb.session.stateless.CategorySessionBeanRemote;
import ejb.session.stateless.MemberSessionBeanRemote;
import ejb.session.stateless.ModelSessionBeanRemote;
import ejb.session.stateless.OutletSessionBeanRemote;
import ejb.session.stateless.RateSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import entity.Category;
import entity.Customer;
import entity.Member;
import entity.Model;
import entity.Outlet;
import entity.Reservation;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
    
    private Member currentMember;

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
            
                if(response == 4)
                {
                    break;
                }
            }
        }
    }
    
    private void doLogin() throws InvalidLoginCredentialException {
        Scanner scanner = new Scanner(System.in);
        String email = "";
        String password = "";
        
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
            Member newMember = new Member(name, email, password);
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
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        System.out.println("*** CaRMS Reservation Client :: Search Car ***\n");
        
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
                    success = reservationSessionBeanRemote.searchAvailableCar(searchType.toString(), startDate, 
                            endDate, startTime, endTime, pickupNo, returnNo, categoryId, modelId);
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
                    
                    Customer customer = new Customer(currentMember.getName(), ccNum, currentMember.getEmail(), CustomerTypeEnum.MEMBER);
                    Reservation r = new Reservation(PaidStatusEnum.PAID, total, startDate, endDate, startTime, endTime, OrderTypeEnum.CATEGORY, customer);
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
                        return;
                    }
                    System.out.println("Reservation successful! The id is "+theId);
            }
        }
    }
    
    private void doViewResDetails() {
        Scanner scanner = new Scanner(System.in);
        Integer response;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy HH:mm");
        
        System.out.println("*** CaRMS Reservation Client :: View Reservation Details ***\n");
        System.out.print("Enter reservation id> ");
        Long id = scanner.nextLong();
        
        Reservation r = reservationSessionBeanRemote.retrieveReservationById(id);
        LocalDateTime a = LocalDateTime.of(r.getPickupDate(), r.getPickupTime());
        String startDateTime = a.format(formatter);
        LocalDateTime b = LocalDateTime.of(r.getReturnDate(), r.getReturnTime());
        String endDateTime = b.format(formatter);
            
        System.out.printf("%8s%20s%20s%20s%20s%20s\n", "Res Id", "PickUp Date/Time", "Return Date/Time", "Car",
                "PickUp Location", "Return Location");
        System.out.printf("%8s%20s%20s%20s%20s%20s\n", id, startDateTime, endDateTime, r.getCarModel().getModelName(),
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
            String reply = reservationSessionBeanRemote.cancelReservation(id);
            System.out.println(reply);
        }
    }
    
    private void doViewAllRes() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy HH:mm");
        
        System.out.println("*** CaRMS Reservation Client :: View All My Reservations ***\n");
        List<Reservation> res = reservationSessionBeanRemote.retrieveReservations(currentMember.getEmail());
        System.out.printf("%8s%20s%20s%20s%20s\n", "Res Id", "PickUp Date/Time", "Return Date/Time", "Reservation Status", "Payment Status");
        for(Reservation r : res) {
            LocalDateTime a = LocalDateTime.of(r.getPickupDate(), r.getPickupTime());
            String startDateTime = a.format(formatter);
            LocalDateTime b = LocalDateTime.of(r.getReturnDate(), r.getReturnTime());
            String endDateTime = b.format(formatter);
            System.out.printf("%8s%20s%20s%20s%20s\n", r.getReservationId(), startDateTime, endDateTime, r.getResStatus(), r.getPaymentStatus());
        }
        System.out.println();
    }
}
