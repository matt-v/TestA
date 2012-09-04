/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OrchestraTest;

import jm.JMC;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;
import jm.util.Play;

import java.util.Vector;

/**
 * This class holds a Score to play
 * Single pattern
 * @author mattvaughan
 */
class ScoreHolder implements JMC {
   
   // stores the only instance of ScoreHolder
   private static ScoreHolder SINGLE_INSTANCE = new ScoreHolder();
   
   
   private long phraseNumber = 0;                 // the current phrase number
                                                  //    to be incremented first time through the loop
   private boolean playScore = true;              // should we play the song
   private double tempo = 120.0;                  // song tempo   
   private boolean quit = false;                  // should we end the midi thread?
   
   // four new phrases that all have a start time of 0.0
   private Phrase tempPhrase1 = new Phrase(0.0);
   private Phrase tempPhrase2 = new Phrase(0.0);
   private Phrase tempPhrase3 = new Phrase(0.0);
   private Phrase tempPhrase4 = new Phrase(0.0);
   
   // Part(name of the part, instrument to play, channel number)
   private Part tempPart1 = new Part("Part1", PIANO, 0 );
   private Part tempPart2 = new Part("Part2", FLUTE, 1 );
   private Part tempPart3 = new Part("Part2", GUITAR, 2 );
   private Part tempPart4 = new Part("Part4", TRUMPET, 3 );

   // put our parts and phrases in arrays
   public Phrase[] phrases = { tempPhrase1, tempPhrase2, tempPhrase3, tempPhrase4 };
   public Part[] parts     = { tempPart1, tempPart2, tempPart3, tempPart4 };
   
   private Score score = new Score("Our Score", tempo);
   
   // private constuctor since we're using the singleton pattern
   private ScoreHolder() {}
   
   
   /**
    * Gets the ONLY instance of ScoreHolder
    * @return the single instance of ScoreHolder
    */
   public static ScoreHolder getInstance() {
      return SINGLE_INSTANCE;
   }
   
   public void Assemble() {
      
      // clear the score
      score.removeAllParts();

      for ( int i = 0; i < parts.length ; ++i ) {
         
         parts[i].removeAllPhrases();        // clear parts        
         
         if ( phrases[i].length() != 0 ) {
            parts[i].addPhrase( phrases[i] );   // add the user phrase to this part
            score.addPart( parts[i] );
         }
      }
   }
   
   /**
    * Plays score and increments phrase number
    */
   public void playScore() {
      
      updatePhrase();
      Play.midi( score );
   }
   
   
   /**
    * executesCurrentEvents and removes all timed out notes
    */
   public void updatePhrase() {
      
      phraseNumber++;         // increment phrase number
      
      killTimedOutNotes();    // remove notes set to die on this phrase
      
      FutureEvents.getInstance().executeCurrentEvents();
   }
   
   /**
    * Iterates through all notes in all phrases and turns timed out notes into rests
    */
   private void killTimedOutNotes() {
      
      // *****  NOTE : I'm temporarily using a cast until I make a specialPhrase class
      // *****         I intend to do this a more safe and friendly way in the future
      
      // go through each phrase
      for ( int i = 0; i < phrases.length; ++i ) {
         
         Phrase currentPhrase = phrases[i];
         
         // go through each note
         for ( int j = 0; j < currentPhrase.getNoteArray().length; ++j ) {
            
            // get special note from currentPhrase 
            SpecialNote currentNote = (SpecialNote) currentPhrase.getNote(j);
            
            // and if this is this note's time to die change it to a rest
            if ( currentNote.getPhraseToDieOn() == phraseNumber ) {
                 // currentNote.setPitch( REST );
               
               currentPhrase.removeNote(j);     // remove note to die
               j--;                             // decrement j, so we dont skip a note
            } // end if
         } // end for j
      } // end for i
      
   }
   
   /**
    * Gets the current phrase number
    * @return currentPhraseNumber 
    */
   public long getPhraseNumber() {
      return phraseNumber;
   }
   
   /**
    * Wrapper for getEndTime();
    * @return score's end time 
    */
   public double getEndTime() {
      return score.getEndTime();
   }
      
   public void setTempo( double newTempo ) {
      tempo = newTempo;
      score.setTempo( tempo );
   }
   
   public boolean getPlayScore() {
      return playScore;
   }
   
   public void setPlayScore( boolean newPlayScore ) {
      playScore = newPlayScore;
   }
   
   public void setQuit( boolean newQuit ) {
      quit = newQuit;
   }
   
   // used in midi thread to determine when to quit the while loop
   public boolean getQuit() {
      return quit;
   }
}


/**
 * Stores a command from scratch
 * @author mattvaughan
 */
class ScoreEvent {
   
   String [] broadcast;
   long phraseNumber;
   SocketThread thread;
   
   ScoreEvent( String [] parsedBroadcast, long phraseNumber, SocketThread thread ) {
      this.phraseNumber = phraseNumber;
      broadcast = parsedBroadcast;
      this.thread = thread;
   }
   
   public String [] getBroadcast() {
      return broadcast;
   }
   
   public long getPhraseNumber() {
      return phraseNumber;
   }
   
   public SocketThread getThread() {
      return thread;
   }
}

/**
 * Stores all future events (singleton pattern)
 * @author mattvaughan
 */
class FutureEvents {
   
   // stores the one and only instance of future event
   private static FutureEvents SINGLE_INSTANCE = new FutureEvents();
   
   // vector of scoreEvents
   private Vector<ScoreEvent> events = new Vector();
   
   // the instance of ScoreHolder
   private ScoreHolder scoreHolder = ScoreHolder.getInstance();
   
   // private constructor
   private FutureEvents() {}          // we don't need instances of this class
   
   /**
    * Returns THE instance of this class to the user
    * @return the only instance of FutureEvents 
    */
   public static FutureEvents getInstance() {
      return SINGLE_INSTANCE;
   }
   
   public void addEvent( String [] broadcast, long phraseNumber, SocketThread thread ) {
      
      // if the phrase to execute the command on has already passed then ignore it
      // otherwise add the event to the vector
      if ( phraseNumber >= scoreHolder.getPhraseNumber() ) {
         
         ScoreEvent thisEvent = new ScoreEvent(broadcast, phraseNumber, thread);
         events.add( thisEvent );
      }
   }
   
   /**
    * Call executeBroadcast for current events
    */
   public void executeCurrentEvents() {
      
      // for each event...      
      for ( int i = 0; i < events.size(); ++i ) {
         
         ScoreEvent thisEvent = events.get(i);
         
         // see we are on the phrase the event executes on
         if ( thisEvent.getPhraseNumber() == scoreHolder.getPhraseNumber() ) {
            
            // get the thread to execute it with
            SocketThread thread = thisEvent.getThread();
            thread.executeBroadcast( thisEvent.getBroadcast() );
         }
      }
   }
}