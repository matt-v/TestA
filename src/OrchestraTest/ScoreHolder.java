/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OrchestraTest;

import java.util.*;
import jm.JMC;
import jm.music.data.CPhrase;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;
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
   private long phraseNumber = 0;                  // the current phrase number
                                                   //    to be incremented first time through the loop
   private boolean playScore = true;               // should we play the song
   private double tempo = 120.0;                   // song tempo   
   private boolean quit = false;                   // should we end the midi thread?
      
   public Map<String,Phrase> phraseMap;           // Maps user clientThreads to Phrases   + KEYS SHOULD MATCH
   public Map<String,Part> partMap;                // Maps user clientThreads to Parts     + KEYS SHOULD MATCH
   
   private Score score = new Score("Our Score", tempo);
   
   // private constuctor since we're using the singleton pattern
   private ScoreHolder() {
      phraseMap = new HashMap();
      partMap   = new HashMap();
   }
   
   // adds one clientThread to the ensemble by adding a new part and phrase to their respective maps
   public void addPersonToEnsemble( String id ) {
      
      Phrase newPhrase = new Phrase(0.0);                // phrase whose start time is 0.0
      Part newPart = new Part( id, PIANO, channel++ );   // part whose default instrument is PIANO
      
      phraseMap.put(id, newPhrase );
      partMap.put(id, newPart );
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
      
      score.removeAllParts();
      
      String[] keys = phraseMap.keySet().toArray( new String[0]);
      
      for ( int i = 0; i < keys.length; ++i ) {
                  
         partMap.get( keys[i] ).removeAllPhrases();                           // empty old parts
         
         if ( phraseMap.get( keys[i] ).length() != 0 ) {
                 
            if ( phraseMap.get( keys[i] ).length() != 0 ) {
                partMap.get( keys[i] ).addPhrase( phraseMap.get( keys[i] ) ); // add phrase to the part
            }
            
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
   
   
   /**
    * executesCurrentEvents and removes all timed out notes
    */
   public void updatePhrase() {
      phraseNumber++;         // increment phrase number
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