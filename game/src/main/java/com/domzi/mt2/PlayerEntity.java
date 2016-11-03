package com.domzi.mt2;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by M43734 on 22.07.2016.
 */
public class PlayerEntity {

    private int entityIndex;
    private String entityName;

    private int strength;
    //private int level;

    final static int maximumLevel = 10;
    final static int minimumLevel = -10;
    final static int maximumStrength = 12;
    final static int minimumStrength = 0;

    static final String[] PlayerNames = {"Mali", "Ari", "Inc", "Mouz"};

    Map<String, Number> attributes;

    public PlayerEntity(int index)
    {
        entityIndex = index;
        entityName = PlayerNames[index];

        strength = 0;
//        level = 1;

        attributes = new HashMap<>();
        attributes.put("strength", 0);
        attributes.put("level", 1);

        Number num = attributes.get("strength");
        num = num.intValue() + 1;
        attributes.put("strength", num);
    }

    private int getAttr(String AttribString)
    {
        return attributes.get(AttribString).intValue();
    }

    private void setAttr(String AttribString, int value)
    {
        attributes.put(AttribString, value);
    }

    /**
     * Increase the player level unless it's at the maximum level already
     */
    public void increaseLevel()
    {
        if(getAttr("level") < maximumLevel) {
            setAttr("level", getAttr("level") + 1);
        }
    }

    /**
     * Decrease the player level unless it's at the minimum level already
     */
    public void decreaseLevel()
    {
        if(getAttr("level") > minimumLevel) {
            setAttr("level", getAttr("level") - 1);
        }
    }

    /**
     * Return the current level
     * @return The current Level
     */
    public int getLevel()
    {
        return getAttr("level");
    }

    /**
     * Increase the player strength unless it's at the maximum level already
     */
    public void increaseStrength()
    {
        if(strength < maximumStrength) {
            strength++;
        }
    }

    /**
     * Decrease the player level unless it's at the minimum level already
     */
    public void decreaseStrength()
    {
        if(strength > minimumStrength) {
            strength--;
        }
    }

    /**
     * Return the current strength
     * @return The current strength
     */
    public int getStrength()
    {
        return strength;
    }
}
