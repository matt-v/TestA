/*
 * Matthew Vaughan
 * matt884987@gmail.com
 */
package OrchestraTest;

import java.util.*;
import jm.JMC;
import jm.music.data.CPhrase;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;
import jm.music.data.Note;
import jm.util.Play;


/**
 * This class holds a Score to play
 * Single pattern
 * @author mattvaughan
 */
class ScoreHolder implements JMC {
   
   // stores the only instance of ScoreHolder
   private static ScoreHolder SINGLE_INSTANCE = new ScoreHolder();
   
   private int channel = 0;                        // the next channel to use   
   private int phraseNumber = 0;                  // the current phrase number
                                                   //    to be incremented first time through the loop
   private boolean playScore = true;               // should we play the song
   private double tempo = 120.0;                   // song tempo   
   private boolean quit = false;                   // should we end the midi thread?
      
   public Map<String,Phrase> phraseMap;            // Maps user clientThreads to Phrases   + KEYS SHOULD MATCH
   public Map<String,Part> partMap;                // Maps user clientThreads to Parts     + KEYS SHOULD MATCH
   
   /***** NOTE:  We should consolidate fututeClearPhrases and futureEvents... or just remove fcp ******/
   private Vector<ClearPhrase> futureClearPhrases; // future clearPhrase events
   private Vector<FutureEvent> futureEvents; // future clearPhrase events
   
   private Score score = new Score("Our Score", tempo);              // the score to play
   private Score emptyMessure = new Score( "Empty Messure", tempo);  // for when the score is empty
   
   
   // private constuctor since we're using the singleton pattern
   private ScoreHolder() {
      // initialize hashmaps and future events vector
      phraseMap     = new HashMap();
      partMap       = new HashMap();
      futureEvents = new Vector<FutureEvent>();
      futureClearPhrases = new Vector<ClearPhrase>();
      
      // set up empty messure
      Part part = new Part();
      part.addNote( new Note(REST, 1), 0);
      emptyMessure.add(part);
   }     
   
   public Score getEmptyMessure() {
      return emptyMessure;
   }
   
   // adds one clientThread to the ensemble by adding a new part and phrase to their respective maps
   public void addPersonToEnsemble( String id ) {
      
      Phrase newPhrase = new Phrase(0.0);                    // phrase whose start time is 0.0
      Part newPart = new Part( id, PIANO, channel++ );       // part whose default instrument is PIANO
      //Part newFuturePart = new Part( id, PIANO, channel++ ); // part whose default instrument is PIANO
      //FuturePhrase newFuturePhrase = new FuturePhrase();     // new FuturePhrase class for client
      
      phraseMap.put(id, newPhrase );
      partMap.put(id, newPart );
      //futurePartMap.put(id, newFuturePart );
      //futureMap.put(id, newFuturePhrase );
   }
   
   /**
    * Gets the ONLY instance of ScoreHolder
    * @return the single instance of ScoreHolder
    */
   public static ScoreHolder getInstance() {
      return SINGLE_INSTANCE;
   }
   
   // empty and re-assembles the score before Play.midi() is called
   public void assemble() {
      //Phrase tempPhrase;                                                      // temp phrase to check for future phrases
      
      score.removeAllParts();
      
      String[] keys = phraseMap.keySet().toArray( new String[0]);
      
      for ( int i = 0; i < keys.length; ++i ) {
                  
         partMap.get( keys[i] ).removeAllPhrases();                           // empty old parts
         
         if ( phraseMap.get( keys[i] ).length() != 0 ) {
                 
            if ( phraseMap.get( keys[i] ).length() != 0 ) {

                /* Checks to see if there is a part queued up in the futureMap
                if ( (tempPhrase = futureMap.get( keys[i] ).getFuturePhrase( phraseNumber )) != null ) {
                    futurePartMap.get( keys[i] ).addPhrase( tempPhrase );
                }*/
                
                partMap.get( keys[i] ).addPhrase( phraseMap.get( keys[i] ) ); // add phrase to the part
            }
            
            //score.addPart( futurePartMap.get( keys[i] ) );                    // and add the future part to the score
            score.addPart( partMap.get( keys[i] ) );                          // and add the part to the score
         }
      }
   }
   
   /**
    * Plays score and increments phrase number
    */
   public void playScore() {
      updatePhrase();
      Play.midi( score, false, true, 2 );
   }
   
   public void playEmptyMessure() {
      updatePhrase();
      Play.midi( emptyMessure, false, true, 2 );
   }
   
   /**
    * executesCurrentEvents and removes all timed out notes
    */
   public void updatePhrase() {
      
      execFutureEvents();
      clearPhrases();
      phraseNumber++;         // increment phrase number
   }
   
   /**
    * Executes futureEvents
    */
   private void execFutureEvents() {
      
      for ( int i = 0; i < futureEvents.size(); ++i ) {
         
         if ( futureEvents.get(i).getPhraseNum() == phraseNumber ) {
            futureEvents.get(i).execute();
         }
      }
   }
   
   /**
    * 
    * @param command the command to execute
    * @param caller the name of the calling thread
    * @param phraseToDie the phrase number on which to execute the command
    */
   public void addFutureEvent( String command, String caller, int phraseToExec ) {
      FutureEvent futureEvent = new FutureEvent( command, caller, phraseToExec );
      futureEvents.add(futureEvent);
   }
   /**
    * Stops phrases from looping forever...
    */
   private void clearPhrases() {
      
      // check each future clearPhrase
      for ( int i = 0; i < futureClearPhrases.size(); ++i ) {
         
         // if it's time to remove it
         if ( futureClearPhrases.get(i).getPhraseNum() == phraseNumber ) {
            
            String caller = futureClearPhrases.get(i).getCaller();
            Interpreter.getInstance().interp("!@clearphrase()", caller );
         }
      }
   }
   
   /**
    * Adds a future clear phrase event
    * @param caller the clientThread who called us
    * @param phraseToDieOn the phraseNumber to stop playing the phrase
    */
   public void addClearPhrase( String caller, int phraseToDieOn ) {
      ClearPhrase clearPhrase = new ClearPhrase(caller, phraseToDieOn);
      futureClearPhrases.add(clearPhrase);
   }
   
   /**
    * Gets the current phrase number
    * @return currentPhraseNumber 
    */
   public int getPhraseNumber() {
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
      emptyMessure.setTempo( tempo );
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
   ClientThread thread;
   
   ScoreEvent( String [] parsedBroadcast, long phraseNumber, ClientThread thread ) {
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
   
   public ClientThread getThread() {
      return thread;
   }
}