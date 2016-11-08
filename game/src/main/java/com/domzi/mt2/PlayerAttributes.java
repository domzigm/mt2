/*
 *
 * (c) domzigm 2016 - GPLv3
 * https://github.com/domzigm/mt2
 *
 */
package com.domzi.mt2;

import java.util.HashMap;
import java.util.Map;

public class PlayerAttributes {

    class Attribute {
        int currentValue;
        int minimumValue;
        int maximumValue;
        Object userData;
    }

    Map<String, Attribute> attributes;

    PlayerAttributes()
    {
        attributes = new HashMap<>();
    }

    /**
     * Register an attribute with the handler
     * @param attributeName Name of the attribute
     * @param startValue Starting value
     * @param minValue Minimum value allowed
     * @param maxValue Maximum value allowed
     */
    public void addAttribute(String attributeName, Object userData, int startValue, int minValue, int maxValue) {
        Attribute addAttribute = new Attribute();
        addAttribute.currentValue = startValue;
        addAttribute.maximumValue = maxValue;
        addAttribute.minimumValue = minValue;
        addAttribute.userData = userData;
        attributes.put(attributeName, addAttribute);
    }

    public Object getUserData(String attributeName) throws Exception {
        Attribute attr = attributes.get(attributeName);
        if(attr == null) {
            throw new Exception("Attribute not defined");
        }
        return attr.userData;
    }

    /**
     * Get the value of an attribute
     * @param attributeName Name of the attribute
     * @return The current value of the attribute
     * @throws Exception Attribute not found
     */
    public int getAttribValue(String attributeName) throws Exception {
        Attribute attr = attributes.get(attributeName);
        if(attr == null) {
            throw new Exception("Attribute not defined");
        }
        return attr.currentValue;
    }

    /**
     * Set the value of an attribute
     * @param attributeName Name of the attribute
     * @return The new value of the attribute
     * @throws Exception Attribute not found
     */
    public int setAttribValue(String attributeName, int value) throws Exception {
        Attribute attr = attributes.get(attributeName);
        if(attr == null) {
            throw new Exception("Attribute not defined");
        }
        if(value > attr.maximumValue) {
            value = attr.maximumValue;
        }
        else if(value < attr.minimumValue) {
            value = attr.minimumValue;
        }
        attributes.put(attributeName, attr);

        return value;
    }

    /**
     * Check if an attribute can be incremented
     * @param attributeName Name of the attribute
     * @return True if the attribute cannot be incremented, False otherwise
     * @throws Exception Attribute not found
     */
    public boolean isAttribMaximum(String attributeName) throws Exception {
        Attribute attr = attributes.get(attributeName);
        if(attr == null) {
            throw new Exception("Attribute not defined");
        }
        return (attr.currentValue == attr.maximumValue);
    }

    /**
     * Check if an attribute can be decremented
     * @param attributeName Name of the attribute
     * @return True if the attribute cannot be decremented, False otherwise
     * @throws Exception Attribute not found
     */
    public boolean isAttribMinimum(String attributeName) throws Exception {
        Attribute attr = attributes.get(attributeName);
        if(attr == null) {
            throw new Exception("Attribute not defined");
        }
        return (attr.currentValue == attr.minimumValue);
    }
}
