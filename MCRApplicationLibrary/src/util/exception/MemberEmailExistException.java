/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author vanes
 */
public class MemberEmailExistException extends Exception {

    /**
     * Creates a new instance of <code>MemberEmailExistException</code> without
     * detail message.
     */
    public MemberEmailExistException() {
    }

    /**
     * Constructs an instance of <code>MemberEmailExistException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public MemberEmailExistException(String msg) {
        super(msg);
    }
}
