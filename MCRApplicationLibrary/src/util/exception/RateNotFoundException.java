/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author User
 */
public class RateNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>RateNotFoundException</code> without
     * detail message.
     */
    public RateNotFoundException() {
    }

    /**
     * Constructs an instance of <code>RateNotFoundException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public RateNotFoundException(String msg) {
        super(msg);
    }
}
