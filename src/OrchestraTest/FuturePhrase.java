/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OrchestraTest;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import jm.music.data.Note;
import jm.music.data.Phrase;

/**
 * Allows for starting phrases
 * on future measures
 * @author Angelo Gamarra
 */
public class FuturePhrase {
   
   private Map<Integer,Phrase> futurePhrases;
   private Vector<Integer> endMeasures;
   
   public FuturePhrase() {
      futurePhrases         = new HashMap();
   }
   
   public void addFutureNote( Note newNote, Integer measure, Integer endMeasure ){
       Phrase foundPhrase;
       
       if ( (foundPhrase = this.futurePhrases.get(measure)) != null ) {
           foundPhrase.addNote(newNote);
       }
       else {
           foundPhrase = new Phrase(newNote);
           futurePhrases.put(measure, foundPhrase);
       }
       endMeasures.add( endMeasure );
   }
   
   public Phrase getFuturePhrase( int measure ) {
       Integer i = new Integer(measure);
       Phrase nextPhrase;
       
       if ( (nextPhrase = this.futurePhrases.get(i)) != null ) {
           return nextPhrase;
       }
       
       return null;
   }
   
   public void removeFuturePhrase( int measure ) {
       Integer i = new Integer(measure);
       
       if ( this.futurePhrases.get(i) != null ) {
           this.futurePhrases.remove(i);
       }
   }
   
   public Boolean endMeasureExist( int measure ) {
       return endMeasures.contains( (Integer) measure );
   }
}
