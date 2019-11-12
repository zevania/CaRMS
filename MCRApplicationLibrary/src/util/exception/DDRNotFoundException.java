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
public class DDRNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>DDRNotFoundException</code> without
     * detail message.
     */
    public DDRNotFoundException() {
    }

    /**
     * Constructs an instance of <code>DDRNotFoundException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DDRNotFoundException(String msg) {
        super(msg);
    }
}
