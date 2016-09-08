package com.domzi.mt2;

/**
 * Created by M43734 on 22.07.2016.
 */
public class PlayerEntity {

    private int entityIndex;
    private String entityName;
    private String PlayerNames[];

    private int strength;
    private int level;

    public final static int maximumLevel = 10;
    public final static int minimumLevel = -10;
    public final static int maximumStrength = 12;
    public final static int minimumStrength = 0;

    // There are events which occur on multiple rounds. Rather than
    // fetching an event from the BoardField->BoardEvent list we
    // have save the event from last round
    private int hasEventAssigned;

    public void init(int index)
    {
        // Move to JAVA enum equivalent
        PlayerNames[0] = new String("Mali");
        PlayerNames[1] = new String("Ari");
        PlayerNames[2] = new String("Supr");
        PlayerNames[3] = new String("Mouz");

        entityIndex = index;
        entityName = PlayerNames[index];

        strength = 0;
        level = 1;
    }

    // Increase the player level unless it's at the maximum level already
    public void increaseLevel()
    {
        if (level < maximumLevel) {
            level++;
        }
    }

    // Decrease the player level unless it's at the minimum level already
    public void decreaseLevel()
    {
        int minimumLevel = -10;
        if(level > minimumLevel) {
            level--;
        }
    }

    public int getLevel()
    {
        return level;
    }

    public void increaseStrength()
    {
        int maximumStrength = 12;
        if(strength < maximumStrength) {
            strength++;
        }
    }

    public void decreaseStrength()
    {
        int minimumStrength = 0;
        if(strength > minimumStrength) {
            strength--;
        }
    }

    public int getStrength()
    {
        return strength;
    }
}
