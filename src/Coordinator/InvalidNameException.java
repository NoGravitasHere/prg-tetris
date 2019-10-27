/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Coordinator;

/**
 *
 * @author pontus.soderlund
 */
public class InvalidNameException extends Exception {

    private String name;

    public InvalidNameException(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Invalid Name";
    }

}
