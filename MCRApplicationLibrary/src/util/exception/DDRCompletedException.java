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
public class DDRCompletedException extends Exception {

    /**
     * Creates a new instance of <code>DDRCompletedException</code> without
     * detail message.
     */
    public DDRCompletedException() {
    }

    /**
     * Constructs an instance of <code>DDRCompletedException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DDRCompletedException(String msg) {
        super(msg);
    }
}
