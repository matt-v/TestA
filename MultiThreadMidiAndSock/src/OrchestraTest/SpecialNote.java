/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OrchestraTest;

import jm.music.data.Note;

/**
 * Extends the jMusic class Note with two added members - id and phraseToDieOn
 * @author mattvaughan
 */
public class SpecialNote extends Note {
   
   private String id;               // id of the user who sounded the note
   private long phraseToDieOn;    // phrase number to stop playing this note
   
   /**
    * 
    * @param pitch 0 - 127 with 60 as middle-C : passed to super class
    * @param rhythmValue length of the note in beats : passed to super class
    * @param id id of the user who sounded the note
    * @param phraseToDieOn the phrase number on which note should stop playing
    */
   SpecialNote( int pitch, double rhythmValue, String id, long phraseToDieOn ) {
      
      super( pitch, rhythmValue );              // call super class constructor  
      this.id = id;
      this.phraseToDieOn = phraseToDieOn;
   }
   
   public String getID() {
      return id;
   }
   
   public long getPhraseToDieOn() {
      return phraseToDieOn;
   }
   
   public void setID( String id ) {
      this.id = id;
   }
   
   public void setPhraseToDieOn( long phraseToDieOn ) {
      this.phraseToDieOn = phraseToDieOn;
   }
}
