/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Partner;
import util.exception.InvalidLoginCredentialException;
import util.exception.PartnerNotFoundException;


public interface PartnerSessionBeanLocal {

    public long createPartner(Partner p);

    public Partner doPartnerLogin(String email, String password) throws PartnerNotFoundException, InvalidLoginCredentialException;

    
    
}
