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
public class CarNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>CarNotFoundException</code> without
     * detail message.
     */
    public CarNotFoundException() {
    }

    /**
     * Constructs an instance of <code>CarNotFoundException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CarNotFoundException(String msg) {
        super(msg);
    }
}
