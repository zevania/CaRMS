package carmsmanagementclient;

import ejb.session.stateless.CarSessionBeanRemote;
import ejb.session.stateless.CategorySessionBeanRemote;
import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.MemberSessionBeanRemote;
import ejb.session.stateless.ModelSessionBeanRemote;
import ejb.session.stateless.OutletSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.RateSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.TransitDriverDispatchRecordSessionBeanRemote;
import entity.Car;
import entity.DriverDispatchRecord;
import entity.Employee;
import entity.Model;
import entity.Rate;
import entity.Reservation;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.enumeration.CarStatusEnum;
import util.enumeration.CategoryNotFoundException;
import util.enumeration.PaidStatusEnum;
import util.enumeration.ResStatusEnum;
import util.enumeration.RoleEnum;
import util.exception.CarNotFoundException;
import util.exception.DDRCompletedException;
import util.exception.DDRNotFoundException;
import util.exception.EmployeeNotFoundException;
import util.exception.InvalidLoginCredentialException;
import util.exception.InvalidModelException;
import util.exception.InvalidReservationException;
import util.exception.ModelNotFoundException;
import util.exception.OutletNotFoundException;
import util.exception.RateNotFoundException;
import util.exception.ReservationNotFoundException;

public class MainApp {
    
    private TransitDriverDispatchRecordSessionBeanRemote transitDriverDispatchRecordSessionBean;
    
    private ReservationSessionBeanRemote reservationSessionBean;

    private RateSessionBeanRemote rateSessionBean;

    private PartnerSessionBeanRemote partnerSessionBean;

    private OutletSessionBeanRemote outletSessionBean;

    private ModelSessionBeanRemote modelSessionBean;

    private MemberSessionBeanRemote memberSessionBean;

    private EmployeeSessionBeanRemote employeeSessionBean;

    private CategorySessionBeanRemote categorySessionBean;

    private CarSessionBeanRemote carSessionBean;
    
    private Employee currEmployee;
    
    public MainApp(TransitDriverDispatchRecordSessionBeanRemote transitDriverDispatchRecordSessionBean, ReservationSessionBeanRemote reservationSessionBean, RateSessionBeanRemote rateSessionBean, PartnerSessionBeanRemote partnerSessionBean, OutletSessionBeanRemote outletSessionBean, ModelSessionBeanRemote modelSessionBean, MemberSessionBeanRemote memberSessionBean, EmployeeSessionBeanRemote employeeSessionBean, CategorySessionBeanRemote categorySessionBean, CarSessionBeanRemote carSessionBean) {
        this.transitDriverDispatchRecordSessionBean = transitDriverDispatchRecordSessionBean;
        this.reservationSessionBean = reservationSessionBean;
        this.rateSessionBean = rateSessionBean;
        this.partnerSessionBean = partnerSessionBean;
        this.outletSessionBean = outletSessionBean;
        this.modelSessionBean = modelSessionBean;
        this.memberSessionBean = memberSessionBean;
        this.employeeSessionBean = employeeSessionBean;
        this.categorySessionBean = categorySessionBean;
        this.carSessionBean = carSessionBean;
    }
    
    public void runApp(){
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Welcome to CaRMS Management Client  ***\n");
            System.out.println("1: Log in");
            System.out.println("2: Manual trigger allocate cars");
            System.out.println("3: Exit\n");
            response = 0;
            
            while(response < 1 || response > 3)
            {
                System.out.print("> ");

                response = scanner.nextInt();
                scanner.nextLine();
                System.out.println();
                
                if(response == 1)
                {
                    try
                    {
                        doLogin();
                        System.out.println("Login successful!\n");
                        
                        if(currEmployee.getRole()==RoleEnum.SALES){
                            salesApp();
                        } else if (currEmployee.getRole()==RoleEnum.OPERATION){
                            operationApp();
                        } else if (currEmployee.getRole()==RoleEnum.CUSTOMERSERVICE){
                            custServApp();
                        } else if(currEmployee.getRole()==RoleEnum.EMPLOYEE) {
                            empApp();
                        }
                        doLogout();
                    }
                    catch(InvalidLoginCredentialException ex) 
                    {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    }
                }
                else if (response == 3)
                {
                    break;
                } else if (response==2){
                    System.out.println();
                    System.out.println("=============================");
                    System.out.println("Manual Trigger Car Allocation");
                    System.out.println("=============================\n");
                    
                    String inDate;
                    Date theDate;
                    System.out.print("Enter the start date (yyyy-mm-dd): ");
                    inDate = scanner.nextLine().trim();
                    
                    try{
                        theDate = new SimpleDateFormat("yyyy-MM-dd").parse(inDate);
                        theDate.setHours(0);
                        theDate.setMinutes(0);
                        theDate.setSeconds(0);
                        reservationSessionBean.manualAllocateCars(theDate);
                    } catch (DateTimeParseException ex){
                        System.out.println("Invalid start date!");
                        break;
                    } catch (ParseException ex) {
                        System.out.println("Date input is invalid!");
                        System.out.println("[Access Denied]");
                        break;
                    }
                    
                    
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
    
    private void empApp(){
        Scanner sc = new Scanner(System.in);
        
        System.out.println("*** CaRMS Management Client :: EMPLOYEE ***\n");
        System.out.println("You are just employee. Can only log out, so sad :(");
        System.out.println("Press enter to log out!");
        sc.nextLine();
    }
    
    private void doLogin() throws InvalidLoginCredentialException {
        Scanner scanner = new Scanner(System.in);
        String email = "";
        String password = "";
        
        System.out.println("*** CaRMS Management Client :: Login ***\n");
        System.out.print("Enter email> ");
        email = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();
        
        if(email.length() > 0 && password.length() > 0)
        {
            currEmployee = employeeSessionBean.employeeLogin(email, password);
        }
        else
        {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }
        
        if(currEmployee==null){
           throw new InvalidLoginCredentialException("Account not found or wrong password!");
       }
    }
    
    private void doLogout(){
        System.out.println("***** Log out successful!\n");
        currEmployee = null;
    }
    
    private void salesApp(){
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** CaRMS Management Client :: SALES MANAGER ***\n");
            System.out.println("You are logged in as " + currEmployee.getName() + "\n");
            System.out.println("1: Create Rental Rate");
            System.out.println("2: View All Rental Rates");
            System.out.println("3: View Rental Rate Details");
            System.out.println("4: Logout\n");
            response = 0;
            
            while(response < 1 || response > 4)
            {
                System.out.print("> ");

                response = scanner.nextInt();
                scanner.nextLine();
                System.out.println();
                
                if (response == 1)
                {
                    doCreateRentalRate();
                    break;
                }
                else if (response == 2) 
                {
                    doViewRentalRates();
                    break;
                }
                else if (response == 3) 
                {
                    doViewRateDetails();
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
            
            if(response == 4){
                break;
            }
        }
    }
    
    private void doCreateRentalRate() {
        Scanner scanner = new Scanner(System.in);
        String name = "";
        Double rate = 0.0;
        Double peakRate = 0.0;
        long catId = 0;
        String yesno = "";
        String inStartDate = "";
        String inEndDate = "";
        
        Date startDate;
        Date endDate;
        
        System.out.println("*** CaRMS Management Client :: Create Rental Rate ***\n");
        System.out.print("Enter name> ");
        name = scanner.nextLine().trim();
        
        while(true){
            System.out.print("Is there a validity period?(y/n) : ");
            yesno = scanner.nextLine().toLowerCase();
            if(yesno.equals("y")){
                
                System.out.print("Enter the start date (yyyy-mm-dd): ");
                inStartDate = scanner.nextLine().trim();
                System.out.print("Enter the end date (yyyy-mm-dd): ");
                inEndDate = scanner.nextLine().trim();
                
                
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
                
                try{
                    endDate = new SimpleDateFormat("yyyy-MM-dd").parse(inEndDate);
                } catch (ParseException ex) {
                    System.out.println("Date input is invalid!");
                    System.out.println("[Access Denied]");
                    return;
                } catch (DateTimeParseException ex){
                    System.out.println("Invalid end date!");
                    return;
                }
                
                if(startDate.after(endDate)){
                    System.out.println("Invalid start date: start date is after end date!");
                    return;
                }
                break;
            } else if(yesno.equals("n")) {
                startDate = new Date(0L);
                endDate = new Date(2100,10,12);
                break;
            }
        }
            
        System.out.print("Enter rate : $ ");
        rate = scanner.nextDouble();
        System.out.print("Enter peak rate : $ ");
        peakRate = scanner.nextDouble();
        System.out.print("Enter category Id> ");
        catId = scanner.nextLong();
        scanner.nextLine();
        
        Rate r = new Rate(name, rate, peakRate,startDate, endDate);
        long id = 0;
        try{
            id = rateSessionBean.createRate(r,catId);
            System.out.println("Successfully created a rate.");
            System.out.println("The rate id is "+id+". \n");
        }catch(CategoryNotFoundException ex){
            System.out.println("Invalid category id!");
            System.out.println("Category was not found!");
        }
        
        
        System.out.println("Press enter to continue!");
        scanner.nextLine();
        System.out.println();
    }
    
    private void doViewRentalRates(){
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS Management Client :: View All Rental Rates ***\n");
        List<Rate> rates = rateSessionBean.retrieveRates();
        System.out.printf("%8s%30s%20s%20s%20s\n", "Rate Id", "Name", "Category", "Rate", "Peak Rate");
        for(Rate r : rates) {
            System.out.printf("%8s%30s%20s%20s%20s\n", r.getRateId(), r.getName(), r.getCategory().getCategoryName(), r.getRate(), r.getPeakRate());
        }
        System.out.println();
        
        System.out.println("Press enter to continue!");
        sc.nextLine();
        System.out.println();
    }
    
    private void doViewRateDetails(){
        Scanner sc = new Scanner(System.in);
        
        System.out.println("*** CaRMS Management Client :: View Rate Details ***\n");
               
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        
        System.out.print("Enter the rate id: ");
        long rateId;
        rateId = sc.nextLong();
        sc.nextLine();
        
        if(rateId<0){
            System.out.println("The id is invalid!");
            return;
        }
        
        Rate rate;
        
        try{
            rate = rateSessionBean.retrieveRateById(rateId);
            System.out.printf("%8s%25s%25s%20s%20s%20s%20s\n", "Rate Id", "Start Date", "End Date", "Name", "Category", "Rate", "Peak Rate");
            if(rate.getEndPeriod().getYear() >= 2099   ){
                System.out.printf("%8s%25s%25s%20s%20s%20s%20s\n", rate.getRateId(), "N.A.", "N.A.", rate.getName(), rate.getCategory().getCategoryName(), rate.getRate(), rate.getPeakRate());
            } else {
                System.out.printf("%8s%25s%25s%20s%20s%20s%20s\n", rate.getRateId(), formatter.format(rate.getStartPeriod()), formatter.format(rate.getEndPeriod()), rate.getName(), rate.getCategory().getCategoryName(), rate.getRate(), rate.getPeakRate());
            }
        }catch(RateNotFoundException ex){
            System.out.println();
            System.out.println("Rate is not found!");
            System.out.println("Id is invalid\n");
            System.out.println("Press enter to continue!");
            sc.nextLine();
            return;
        
        }
        System.out.println("================");
        System.out.println("1. Update this rate");
        System.out.println("2. Delete this rate");
        System.out.println("3. Back");
        int choice = 0;
        while(true){
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            sc.nextLine();
            
            if(choice==1){
                doUpdateRate(rate);
                break;
            }else if(choice==2){
                doDeleteRate(rateId);
                break;
            }else if(choice==3){
                break;
            }else{
                System.out.println("Invalid input!");
            }
        }        
        
    }
    
    private void doUpdateRate(Rate old){
        Scanner sc = new Scanner(System.in);
        
        System.out.println("*** CaRMS Management Client :: Update Rate Details ***\n");
        
        String name = "";
        Double rate = 0.0;
        String yesno = "";
        String inStartDate = "";
        String inEndDate = "";
        
        Date startDate;
        Date endDate;
        
        System.out.print("Enter new name> ");
        name = sc.nextLine().trim();
        
        while(true){
            System.out.print("Is there a validity period?(y/n) : ");
            yesno = sc.nextLine().toLowerCase();
            if(yesno.equals("y")){
                
                System.out.print("Enter the start date and time (yyyy-mm-dd HH:mm:ss): ");
                inStartDate = sc.nextLine().trim();
                System.out.print("Enter the end date and time (yyyy-mm-dd HH:mm:ss): ");
                inEndDate = sc.nextLine().trim();
                
                try{
                    startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(inStartDate);
                } catch (ParseException ex){
                    System.out.println("Invalid start date and time!");
                    return;
                } 
                
                try{
                    endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(inEndDate);
                } catch (ParseException ex){
                    System.out.println("Invalid end date and time!");
                    return;
                }
                
                if(startDate.after(endDate)){
                    System.out.println("Invalid start date: start date is after end date!");
                    return;
                }
                break;
            } else if(yesno.equals("n")) {
                startDate = new Date(0L);
                endDate = new Date(2100,10,12);
                break;
            }
        }
        Double peakRate = 0.0;
        
        System.out.print("Enter new rate: $ ");
        rate = sc.nextDouble();
        System.out.print("Enter new peak rate: $ ");
        peakRate = sc.nextDouble();
        
        old.setPeakRate(peakRate);
        old.setName(name);
        old.setStartPeriod(startDate);
        old.setEndPeriod(endDate);
        old.setRate(rate);
        
        rateSessionBean.updateRate(old);
        System.out.println("Rate is successfully updated!");
        
        System.out.println("Press enter to continue!");
        sc.nextLine();
        System.out.println();
    }
    
    private void doDeleteRate(long rateId){
        Scanner sc = new Scanner(System.in);
        
        try{
            rateSessionBean.deleteRate(rateId);
        }catch(RateNotFoundException ex){
            System.out.println("Rate not found!");
            System.out.println("[Action Denied]");
            return;
        }
        
        System.out.println("The rate is successfully deleted!");
        
        System.out.println("Press enter to continue!");
        sc.nextLine();
        System.out.println();
    }
    
    private void custServApp(){
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** CaRMS Management Client :: CUSTOMER SERVICE EXECUTIVE ***\n");
            System.out.println("You are logged in as " + currEmployee.getName() + "\n");
            System.out.println("1: Pickup Car");
            System.out.println("2: Return Car");
            System.out.println("3: Logout\n");
            
            response = 0;
            
            while(response < 1 || response > 3)
            {
                System.out.print("> ");

                response = scanner.nextInt();
                scanner.nextLine();
                
                if (response == 1)
                {
                    doPickUp();
                    break;
                }
                else if (response == 2) 
                {
                    doReturnCar();
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
    
    private void doPickUp(){
        Scanner sc = new Scanner(System.in);
        
        System.out.println("*** CaRMS Management Client :: Pick Up Car ***\n");
        System.out.println("");
        
        System.out.print("Enter the reservation id: ");
        long resId = sc.nextLong();
        sc.nextLine();
        
        Reservation r = reservationSessionBean.retrieveReservationById(resId);
        
        String yesno = "";
        
        
        if(r == null){
            System.out.println("There is no such reservation!");
            System.out.println("[Action Denied]\n");
            return;
        }
        
        if(r.getPaymentStatus()==PaidStatusEnum.UNPAID){
            while(true){
                System.out.println("Has the customer pay on site? (y/n)");
                System.out.print(">");
                yesno = sc.nextLine().trim();
                
                if(yesno.equals("y")){
                    break;
                } else if(yesno.equals("n")){
                    System.out.println("You need to pay before you can take the car!");
                    System.out.println("[Action Denied]");
                    return;
                } else {
                    System.out.println("Invalid input! Please input again!");
                }
            }
        }
        
        
        try{
            reservationSessionBean.updateRes(resId, ResStatusEnum.PICKEDUP);
        }catch(ReservationNotFoundException ex){
            System.out.println("There is no such reservation");
            System.out.println("[Action Denied]");
            return;
        }catch(InvalidReservationException ex){
            System.out.println("The reservation is invalid!");
            System.out.println("The car could have already been pick-up!");
            System.out.println("The reservation could have been cancelled or completed!");
            return;
        }
        
        System.out.println("The car is pick-up by the customer!");
        System.out.println("Press enter key to continue!");
        sc.nextLine();
        
    }
    
    private void doReturnCar(){
        Scanner sc = new Scanner(System.in);
        
        System.out.println("*** CaRMS Management Client :: Return Car ***\n");
        System.out.println("");
        
        System.out.print("Enter the reservation id: ");
        long resId = sc.nextLong();
        sc.nextLine();
        
        Reservation r = reservationSessionBean.retrieveReservationById(resId);
        
        if(r == null){
            System.out.println("There is no such reservation!");
            System.out.println("[Action Denied]\n");
            return;
        }
        
        try{
            reservationSessionBean.updateRes(resId, ResStatusEnum.DONE);
        }catch(ReservationNotFoundException ex){
            System.out.println("There is no such reservation");
            System.out.println("[Action Denied]");
            return;
        }catch(InvalidReservationException ex){
            System.out.println("The reservation is invalid!");
            System.out.println("The car could have already been pick-up!");
            System.out.println("The reservation could have been cancelled or completed!");
            return;
        }
        
        System.out.println("The car is successfully returned by the customer!");
        System.out.println("System has successfully record the changes!");
        System.out.println("Press enter key to continue!");
        sc.nextLine();
    }
    
    private void operationApp(){
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** CaRMS Management Client :: OPERATION MANAGER ***\n");
            System.out.println("You are logged in as " + currEmployee.getName() + "\n");
            System.out.println("1: Create New Model");
            System.out.println("2: View All Models");
            System.out.println("3: Update Model");
            System.out.println("4: Delete Model");
            System.out.println("5: Create New Car");
            System.out.println("6: View All Cars");
            System.out.println("7: View Car Details");
            System.out.println("8: View Today Transit Driver Dispatch Records");
            System.out.println("9: Assign Transit Driver");
            System.out.println("10: Update Transit as Completed");
            System.out.println("11: Logout\n");
            response = 0;
            
            while(response < 1 || response > 11)
            {
                System.out.print("> ");

                response = scanner.nextInt();
                scanner.nextLine();
                
                if (response == 1)
                {
                    doCreateNewModel();
                    break;
                }
                else if (response == 2) 
                {
                    doViewModels();
                    break;
                }
                else if (response == 3) 
                {
                    doUpdateModel();
                    break;
                } else if(response == 4){
                    doDeleteModel();
                    break;
                } else if(response == 5){
                    doCreateNewCar();
                    break;
                } else if(response == 6){
                    doViewCars();
                    break;
                } else if(response == 7){
                    doViewCarDetails();
                    break;
                } else if(response == 8){
                    doViewDDR();
                    break;
                } else if(response == 9){
                    doAssignDriver();
                    break;
                } else if(response == 10){
                    doTransitCompleted();
                    break;
                }
                else if (response == 11)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 11){
                break;
            }
        }
        
    }
    
    private void doCreateNewModel(){
        Scanner sc = new Scanner(System.in);
        
        System.out.println("*** CaRMS Management Client :: Create New Model ***\n");
        System.out.println("");
        Model m = new Model();
        
        System.out.print("Enter the model name: ");
        String modelName = sc.nextLine().trim();
        System.out.print("Enter the make name: ");
        String make = sc.nextLine().trim();
        System.out.print("Enter the category Id: ");
        long categoryId = sc.nextLong();
        sc.nextLine();
        m = new Model(modelName, make);
        long id = 0;
        
        System.out.println("Please wait for a moment.");
        
        try{
            id = modelSessionBean.createModel(m, categoryId);
        }catch(CategoryNotFoundException ex){
            System.out.println("Invalid Category Id");
            System.out.println("");
            return;
        }
        
        System.out.println("The model is successfuly created.");
        System.out.println("The model id is "+id+"\n");
        System.out.println("Press enter to continue!");
        sc.nextLine();
        System.out.println("");
    }
    
    private void doViewModels(){
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS Management Client :: View All Models ***\n");
        List<Model> models = modelSessionBean.retrieveModels();
        System.out.printf("%8s%20s%20s%20s\n", "Model Id", "Name", "Make", "Category");
        for(Model m : models) {
            System.out.printf("%8s%20s%20s%20s\n", m.getModelId(), m.getModelName(), m.getMake(), m.getCategory().getCategoryName());
        }
        System.out.println();
        
        System.out.println("Press enter to continue!");
        sc.nextLine();
        System.out.println();
    }
    
    private void doUpdateModel(){
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS Management Client :: View All Models ***\n");
        
        System.out.print("Enter the model id: ");
        long modelId = sc.nextLong();
        sc.nextLine();
        Model model = modelSessionBean.retrieveModelById(modelId);
        
        if(model==null){
            System.out.println("There is no such model!");
            System.out.println("");
            return;
        }
        
        System.out.println("");
        System.out.print("Enter the new model name: ");
        String modelName = sc.nextLine().trim();
        System.out.print("Enter the new make name: ");
        String make = sc.nextLine().trim();
        System.out.print("Do you want to update the category? (Y/N): ");
        String yesno = sc.nextLine().trim().toLowerCase();
        
        long catId = model.getCategory().getCategoryId();
        
        if(yesno.equals("y")){
            System.out.print("Enter the new category id: ");
            catId = sc.nextLong();
            sc.nextLine();
        }
        
        model.setMake(make);
        model.setModelName(modelName);
        
        try{
            modelSessionBean.updateModel(model, catId);
        }catch(CategoryNotFoundException ex){
            System.out.println("The category is not valid!");
            System.out.println("Model is not updated");
            System.out.println("");
            return;
        }
        
        System.out.println("");
        System.out.println("Model successfuly updated!");
        System.out.println("");
        System.out.println("Press enter key to continue!");
        sc.nextLine();
    }
    
    private void doDeleteModel(){
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS Management Client :: Delete a model ***\n");
        
        System.out.print("Enter the model id: ");
        long modelId = sc.nextLong();
        sc.nextLine();
        
        try{
            modelSessionBean.deleteModel(modelId);
        }catch(ModelNotFoundException ex){
            System.out.println("Input is invalid!");
            System.out.println("Model is not found!");
            return;
        }
        
        System.out.println("");
        System.out.println("Model is deleted. The id of deleted model is "+modelId);
        System.out.println("");
        System.out.println("Press enter key to continue!");
        sc.nextLine();
    }
    
    private void doCreateNewCar(){
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS Management Client :: Create a car ***\n");
        
        System.out.print("Enter plate number: ");
        String plateNumber = sc.nextLine().trim();
        System.out.print("Enter the colour: ");
        String colour = sc.nextLine().trim();
        System.out.print("Enter the outlet id: ");
        long outletId = sc.nextLong();
        sc.nextLine();
        System.out.print("Enter the model id: ");
        long modelId = sc.nextLong();
        sc.nextLine();
        
        Car car = new Car(plateNumber, colour, CarStatusEnum.OUTLET);
        long carId = 0;
        
        try{
            carId = carSessionBean.createCar(car, modelId, outletId);
        }catch(InvalidModelException ex){
            System.out.println("The model is invalid!");
            return;
        }catch(OutletNotFoundException ex){
            System.out.println("The outlet is invalid!");
            return;
        }
        
        System.out.println("The car is successfully created! The car id is "+carId);
        
        System.out.println();
        System.out.println("Press enter key to continue!");
        sc.nextLine();
    }
    
    private void doViewCars(){
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS Management Client :: View All Cars ***\n");
        List<Car> cars = carSessionBean.retrieveCars();
        System.out.printf("%8s%20s%30s%20s%20s%20s\n", "Car Id", "Plate Number", "Category", "Make", "Model", "Location");
        for(Car c : cars) {
            System.out.printf("%8s%20s%30s%20s%20s%20s\n", c.getCarId(), c.getPlateNumber(), c.getModel().getCategory().getCategoryName(), c.getModel().getMake(), c.getModel().getModelName(), c.getLocation());
        }
        System.out.println();
        
        System.out.println("Press enter to continue!");
        sc.nextLine();
        
    }
    
    private void doViewCarDetails(){
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS Management Client :: View Car Details ***\n");
        System.out.print("Enter the car id: ");
        long carId = sc.nextLong();
        sc.nextLine();
        Car car;
        try{
            car = carSessionBean.retrieveCarById(carId);
        }catch(CarNotFoundException ex){
            System.out.println("Car id is invalid!");
            return;
        }
        
        System.out.printf("%8s%20s%20s%20s%30s%20s%20s\n", "Car Id", "Plate Number", "Make", "Model", "Category", "Location", "Colour");
        System.out.printf("%8s%20s%20s%20s%30s%20s%20s\n", car.getCarId(), car.getPlateNumber(), car.getModel().getMake(), car.getModel().getModelName(), car.getModel().getCategory().getCategoryName(), car.getLocation(), car.getColour());
        System.out.println("");
        System.out.println("============");
        System.out.println("1. Update car");
        System.out.println("2. Delete car");
        System.out.println("3. Back");
        
        int choice;
        
        while(true){
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            sc.nextLine();
            if(choice==1){
                doUpdateCar(car);
                break;
            }else if(choice==2){
                doDeleteCar(car.getCarId());
                break;
            }else if(choice==3){
                break;
            }else{
                System.out.print("Input is invalid! Please input again!");
            }
        }
        
    }
    
    private void doUpdateCar(Car car){
        Scanner sc = new Scanner(System.in);
        System.out.println("");
        System.out.println("=== Update Car ===");
        System.out.print("Enter the new plateNumber: ");
        String newPlateNumber = sc.nextLine().trim();
        System.out.print("Enter the new colour: ");
        String colour = sc.nextLine().trim();
        System.out.print("Enter the new location: ");
        String location = sc.nextLine().trim();
        
        CarStatusEnum carStatus;
        
        System.out.println("");
        System.out.println("1. OUTLET");
        System.out.println("2. ON RENTAL");
        System.out.println("3. REPAIR");
        
        int statusNum = 0;
        
        while(true){
            System.out.print("Choose the new status: ");
            statusNum = sc.nextInt();
            sc.nextLine();
            if(statusNum == 1){
                carStatus = CarStatusEnum.OUTLET;
                car.setActive(true);
                break;
            }else if(statusNum == 2){
                carStatus = CarStatusEnum.ONRENTAL;
                car.setActive(true);
                break;
            } else if(statusNum == 3){
                carStatus = CarStatusEnum.REPAIR;
                car.setActive(false);
                break;
            } else {
                System.out.println("Invalid input! Please input again!");
            }
        }
        
        String yesno = "";
        
        long modelId = car.getModel().getModelId();
        
        while(true){
            System.out.println("Do you want to update the Model? (y/n)");
            System.out.print("> ");
            yesno = sc.nextLine().trim().toLowerCase();
            
            if(yesno.equals("y")){
                System.out.print("Enter the new model id: ");
                modelId = sc.nextLong();
                sc.nextLine();
                break;
            }else if (yesno.equals("n")){
               break; 
            } else {
                System.out.println("Invalid input! Please input again!");
            }
        }
        
        yesno = "";
        
        long outletId = car.getOutlet().getOutletId();
        
        while(true){
            System.out.println("Do you want to update the OUTLET? (y/n)");
            System.out.print("> ");
            yesno = sc.nextLine().trim().toLowerCase();
            
            if(yesno.equals("y")){
                System.out.print("Enter the new outlet id: ");
                outletId = sc.nextLong();
                sc.nextLine();
                break;
            }else if (yesno.equals("n")){
               break; 
            } else {
                System.out.println("Invalid input! Please input again!");
            }
        }
        
        car.setColour(colour);
        car.setLocation(location);
        car.setPlateNumber(newPlateNumber);
        car.setStatus(carStatus);
        
        try{
            carSessionBean.updateCar(car, outletId, modelId);
        }catch(InvalidModelException ex){
            System.out.println("Model is invalid!");
            System.out.println("Update denied!\n");
            return;
        }catch(OutletNotFoundException ex){
            System.out.println("Outlet is invalid!");
            System.out.println("Update denied\n");
            return;
        }
        
        System.out.println("");
        System.out.println("Update is successful!");
        System.out.println("Press enter to continue!");
        sc.nextLine();
    }
    
    private void doDeleteCar(long carId){
        Scanner sc = new Scanner(System.in);
        
        System.out.println("");
        try{
            carSessionBean.deleteCar(carId);
        }catch(CarNotFoundException ex){
            System.out.println("Car input is invalied");
            System.out.println("[Action denied]");
            System.out.println("");
        }
        
        System.out.println("Car is deleted succesfully!");
        System.out.println("Press enter to continue!");
        sc.nextLine();
                
    }
    
    private void doViewDDR(){
        Scanner sc = new Scanner(System.in);
        
        SimpleDateFormat dateformatter = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println("*** CaRMS Management Client :: View Transit Driver Dispatch Records ***\n");
        
        List<DriverDispatchRecord> ddrs = transitDriverDispatchRecordSessionBean.retrieveDispatchRecords(currEmployee.getOutlet().getOutletId(), Calendar.getInstance().getTime());
        
        if(ddrs.size() == 0){
            System.out.println("There is no driver dispatch record for today!");
            return;
        }
        
        System.out.printf("%8s%40s%20s%30s%40s\n", "DDR id", "Origin Outlet", "Date", "Latest Time", "Status");
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        
        for(DriverDispatchRecord ddr: ddrs){
            System.out.printf("%8s%40s%20s%30s%40s\n", ddr.getDispatchId(), ddr.getFromOutlet(), dateformatter.format(ddr.getDispatchDate()), dateFormat.format(ddr.getDispatchTime()), ddr.getDispatchStatus().toString());
        }
        
        System.out.println("");
        System.out.println("Press enter to continue!");
        sc.nextLine();
    }
    
    private void doAssignDriver(){
        Scanner sc = new Scanner(System.in);
        
        System.out.println("*** CaRMS Management Client :: Assign TDR Driver ***\n");
        
        System.out.println("For demonstration purposes, the program allow user to assign driver to DDR from another day");
        
        List<Employee> employees = employeeSessionBean.retrieveEmployees(currEmployee.getOutlet().getOutletId());
        
        System.out.printf("%8s%40s%40s%40s\n", "Staff id", "Name", "Email", "Role");
        
        for(Employee e: employees){
            System.out.printf("%8s%40s%40s%40s\n", e.getEmployeeId(), e.getName(), e.getEmail(), e.getRole().toString());
        }
        System.out.println("");
        System.out.println("==============================");
        System.out.print("Enter the DDR id: ");
        long ddrId = sc.nextLong();
        sc.nextLine();
        System.out.print("Enter the employee id: ");
        long empId = sc.nextLong();
        sc.nextLine();
        
        try{
            transitDriverDispatchRecordSessionBean.assignDriver(empId, ddrId);
        }catch(DDRNotFoundException ex){
            System.out.println("Invalid input!");
            System.out.println("The DDR is not found in the system!");
            return;
        }catch(EmployeeNotFoundException ex){
            System.out.println("Invalid input!");
            System.out.println("The employee is not found in the system!");
            return;
        }
        
        System.out.println("");
        System.out.println("The driver is assigned successfully!");
        System.out.println("Press enter to continue!");
        sc.nextLine();
    }
    
    private void doTransitCompleted(){
        Scanner sc = new Scanner(System.in);
        
        System.out.println("*** CaRMS Management Client :: Completed TDR ***\n");
        
        System.out.print("Enter the DDR id: ");
        long ddrId = sc.nextLong();
        sc.nextLine();
        
        try{
            transitDriverDispatchRecordSessionBean.updateDispatchRecordAsCompleted(ddrId);
        }catch(DDRNotFoundException ex){
            System.out.println("");
            System.out.println("There is no such DDR!");
            System.out.println("[Action Denied]");
            return;
        }catch(DDRCompletedException ex){
            System.out.println("");
            System.out.println("The DDR is already completed!");
            System.out.println("[Action Denied]");
            return;
        }
        
        System.out.println("The update is completed successfully!");
        System.out.println("Press enter to continue!");
        sc.nextLine();
    }
}
