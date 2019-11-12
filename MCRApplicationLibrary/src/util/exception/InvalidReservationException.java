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
public class InvalidReservationException extends Exception {

    /**
     * Creates a new instance of <code>InvalidReservationException</code>
     * without detail message.
     */
    public InvalidReservationException() {
    }

    /**
     * Constructs an instance of <code>InvalidReservationException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidReservationException(String msg) {
        super(msg);
    }
}
