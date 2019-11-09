/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carmsreservationclient;

import ejb.session.stateless.CategorySessionBeanRemote;
import ejb.session.stateless.MemberSessionBeanRemote;
import ejb.session.stateless.ModelSessionBeanRemote;
import ejb.session.stateless.OutletSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import javax.ejb.EJB;

/**
 *
 * @author User
 */
public class Main {

    @EJB
    private static ReservationSessionBeanRemote reservationSessionBeanRemote;
    @EJB
    private static MemberSessionBeanRemote memberSessionBeanRemote;
    @EJB
    private static OutletSessionBeanRemote outletSessionBeanRemote;
    @EJB
    private static CategorySessionBeanRemote categorySessionBeanRemote;
    @EJB
    private static ModelSessionBeanRemote modelSessionBeanRemote;
  
    public static void main(String[] args) {
        MainApp mainApp = new MainApp(reservationSessionBeanRemote, memberSessionBeanRemote, outletSessionBeanRemote, categorySessionBeanRemote, modelSessionBeanRemote);
        mainApp.runApp();
    }
    
}
