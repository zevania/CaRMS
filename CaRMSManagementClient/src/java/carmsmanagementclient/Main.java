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
import javax.ejb.EJB;

public class Main {

    @EJB
    private static TransitDriverDispatchRecordSessionBeanRemote transitDriverDispatchRecordSessionBean;

    @EJB
    private static ReservationSessionBeanRemote reservationSessionBean;

    @EJB
    private static RateSessionBeanRemote rateSessionBean;

    @EJB
    private static PartnerSessionBeanRemote partnerSessionBean;

    @EJB
    private static OutletSessionBeanRemote outletSessionBean;

    @EJB
    private static ModelSessionBeanRemote modelSessionBean;

    @EJB
    private static MemberSessionBeanRemote memberSessionBean;

    @EJB
    private static EmployeeSessionBeanRemote employeeSessionBean;

    @EJB
    private static CategorySessionBeanRemote categorySessionBean;

    @EJB
    private static CarSessionBeanRemote carSessionBean;

    

    public static void main(String[] args) {
        MainApp mainApp = new MainApp(transitDriverDispatchRecordSessionBean, reservationSessionBean, rateSessionBean, partnerSessionBean, outletSessionBean, modelSessionBean, memberSessionBean, employeeSessionBean, categorySessionBean, carSessionBean);
        mainApp.runApp();
    }
    
}
