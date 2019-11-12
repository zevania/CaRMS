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
public class IncompleteRegistrationDetailsException extends Exception {

    /**
     * Creates a new instance of
     * <code>IncompleteRegistrationDetailsException</code> without detail
     * message.
     */
    public IncompleteRegistrationDetailsException() {
    }

    /**
     * Constructs an instance of
     * <code>IncompleteRegistrationDetailsException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public IncompleteRegistrationDetailsException(String msg) {
        super(msg);
    }
}
