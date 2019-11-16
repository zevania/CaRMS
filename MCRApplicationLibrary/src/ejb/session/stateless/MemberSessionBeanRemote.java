/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.OurMember;
import util.exception.InvalidLoginCredentialException;
import util.exception.MemberEmailExistException;


public interface MemberSessionBeanRemote {
    
    public long createMember(OurMember m) throws MemberEmailExistException;

    public OurMember memberLogin(String email, String password) throws InvalidLoginCredentialException;
    
}
