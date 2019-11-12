/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Member;
import util.exception.InvalidLoginCredentialException;
import util.exception.MemberEmailExistException;

public interface MemberSessionBeanLocal {

    public Long createMember(Member m) throws MemberEmailExistException;

    public Member memberLogin(String email, String password) throws InvalidLoginCredentialException;
    
}
