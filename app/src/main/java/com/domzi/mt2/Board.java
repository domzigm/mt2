package com.domzi.mt2;

/**
 * Created by M43734 on 22.07.2016.
 */
public class Board {

    public enum Field {
      LOC1,
      LOC2,
      LOC3,
      LOC4,
      LOC5,
      LOC6
    }

    // Has a lot of BoardFields
    
    ArrayList<BoardEvents> usedEvents;    // Events that only occur once and have been used
    ArrayList<BoardEvents> availEvents;   // Available events that haven't been used or can be reused
    //ArrayList<BoardEvents> setEvents;   // In-use events are managed by the boardfields
    
    private BoardFields fields[];
    
  public void board()
  {
    fields = new BoardFields[10];
    
  }
    
}
