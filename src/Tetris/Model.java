/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tetris;

import java.beans.PropertyChangeListener;
import java.io.Serializable;

/**
 *
 * @author pontus.soderlund
 */
public interface Model extends Serializable {

    void addPropertyChangeListener(PropertyChangeListener l);

    void addPropertyChangeListener(String propertyName, PropertyChangeListener l);

    void removePropertyChangeListener(PropertyChangeListener l);

    void removePropertyChangeListener(String propertyName, PropertyChangeListener l);
}
