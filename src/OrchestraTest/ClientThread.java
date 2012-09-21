/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OrchestraTest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import jm.JMC;

import jm.music.data.Note;


/**
 * Each instance of this thread is connection to scratch
 * @author mattvaughan
 */
class ClientThread implements Runnable, JMC {
   
   private Thread t;                // for the new socket thread
   private Socket clientSocket;     // ip address of this thread's scratch client
   
   private boolean live = true;     // should this thread continue... used for while loop below
   
   private ScoreHolder scoreHolder = ScoreHolder.getInstance();      // gets the singleton instance of ScoreHolder
   
   ClientThread( Socket clientSocket ) {

      this.clientSocket = clientSocket;
      
      // the threadName will include the ip address
      String threadName = "ClientThread: " + clientSocket.getRemoteSocketAddress().toString();
      
      t = new Thread( this, threadName );  // second argument is the thread's name
      t.start();
   }
   
   /**
    * Called when the thread is instantiated
    */
   public void run() {
      try {
         
         // Add this thread (user) to the ensemble
         scoreHolder.addPersonToEnsemble( t.getName() );
               
         // The BufferReader will give us input from Scratch
         BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream() ) );

         // while the thread is suppose to be running
         while( live ) {
            
            // get input from Scratch
            char [] cStr = new char[255];                               // buffer size of 255 seems reasonable...
            in.read(cStr);
            String currentLine = new String( cStr );
            
            // if more than one command slip come through at a time
            String [] commands = currentLine.split( "!" );
            
            // call the interpretor
            for ( int i = 1; i < commands.length; ++i ) {
               TypeAndValue temp = Interpretor.getInstance().interp( commands[i], t.getName() );
               System.out.println( "Returned a " + temp.getType() );
            }
               
         } // end while
      } // end try
      catch ( Exception e ) {
         System.err.println("Error in " + t.getName() +".run()!");
      }
      
      System.out.println( "Exiting " + t.getName() + "." );             // for debugging
   }
 
   /*
   public void executeBroadcast( String[] splitLine ) {

      // check to see what command we are executing
      if (splitLine[0].matches("addnotes")) {
         addNotes(splitLine);
      } 
      else if (splitLine[0].matches("clearphrase")) {

         // must have a operand telling us which phrase to clear
         if (splitLine.length < 2) {
            throw new RuntimeException();
         }

         Integer phraseNumber = new Integer(splitLine[1]);           // get the number of the phrase to clear
         scoreHolder.phrases[ phraseNumber.intValue()].empty();      // clear the phrase
      } 
      else if (splitLine[0].matches("setinstrument")) {

         // must have operands telling us what part to change and what instrument to change it to
         if (splitLine.length < 3) {
            throw new RuntimeException();
         }

         Integer phraseNumber = new Integer(splitLine[1]);            // get the number of the part
         Integer newInstrumentNumber = new Integer(splitLine[2]);     // get the new instrument number 

         // set the new instrument number
         scoreHolder.parts[ phraseNumber.intValue()].setInstrument(newInstrumentNumber.intValue());
      } 
      else if (splitLine[0].matches("settempo")) {

         // must habe two operands... the command name and the tempo
         if (splitLine.length < 2) {
            throw new RuntimeException();
         }

         Double newTempo = new Double(splitLine[1]);      // make a Double whose value is the tempo
         scoreHolder.setTempo(newTempo.doubleValue());    // set the tempo

      } // continue playing midi
      else if (splitLine[0].matches("play")) {
         scoreHolder.setPlayScore(true);
      } // stop playing midi
      else if (splitLine[0].matches("stop")) {
         scoreHolder.setPlayScore(false);
      } // end this socket thread
      else if (splitLine[0].matches("die")) {
         live = false;
      } // end midi thread
      else if (splitLine[0].matches("quitmidi")) {
         scoreHolder.setQuit(true);
      } // quit is a combination of quitmidi and die
      else if (splitLine[0].matches("quit")) {
         scoreHolder.setQuit(true);
         live = false;
         //  NOTE: This will only end the program if this is the only socket thread
         //        if there are other socket threads open they must 'die' as well
      } // add chords insted of notes
      else if (splitLine[0].matches("addchords")) {
         addChords(splitLine);
      }
      else if (splitLine[0].matches("connect") )  {
         // For testing...
         // new SocketThread( splitLine[1] );
           
      } else {
         // command not found
      }
   }
   */
   /** 
    * helper function for addNotes() and addChords()
    * @param noteLengthString
    * @return 
    */
/*   private double findNoteLength(String noteLengthString) {
      
      // because we dont want to be case-sensitive
      noteLengthString = noteLengthString.toUpperCase();
      
      // find noteLength
      if      (noteLengthString.matches("SN"))  { return SN;   } // sixteenth note
      else if (noteLengthString.matches("DSN")) { return DSN;  } // dotted sixteenth note
      else if (noteLengthString.matches("EN"))  { return EN;   } // eigth note
      else if (noteLengthString.matches("DEN")) { return DEN;  } // dotted eight note
      else if (noteLengthString.matches("QN"))  { return QN;   } // quarter note
      else if (noteLengthString.matches("DQN")) { return DQN;  } // dotted quarter note
      else if (noteLengthString.matches("HN"))  { return HN;   } // half note
      else if (noteLengthString.matches("DHN")) { return DHN;  } // dotted half note
      else if (noteLengthString.matches("WN"))  { return WN;   } // whole note
      else if (noteLengthString.matches("ENT")) { return SNT;  } // sixteenth note triple
      else if (noteLengthString.matches("ENT")) { return ENT;  } // eight note triple
      else if (noteLengthString.matches("QNT")) { return QNT;  } // quarter note triple
      else if (noteLengthString.matches("HNT")) { return HNT;  } // half note triple
      else                                      { return QN;   } // defualt to quarter note
   }
*/ 
   /**
    * Helper function for addChord()
    * @param baseNote root note of the chord (c for c-major)
    * @param chordType the type of chord to build. major/minor/ect...
    * @return an integer array of the notes for the chord
    */   
/*   private int [] buildChord( int baseNote, String chordType ) {
      
      int [] notes = { baseNote };
      
      chordType = chordType.toLowerCase();   // because we don't want to be case-sensitive
      
      // major traid
      if       ( chordType.matches("major") || chordType.matches("maj") ) {
      
         notes = new int[3]; // major has three notes
         
         notes[0] = baseNote;
         notes[1] = baseNote + 4;
         notes[2] = baseNote + 7;
      }
      // minor traid
      else if  ( chordType.matches("minor") || chordType.matches("min") ) {
      
         notes = new int[3]; // minor has three notes
         
         notes[0] = baseNote;
         notes[1] = baseNote + 3;
         notes[2] = baseNote + 7;
      }
      // diminished traid
      else if  ( chordType.matches("diminished") || chordType.matches("dim") ) {
      
         notes = new int[3]; // diminished traid has three notes
         
         notes[0] = baseNote;
         notes[1] = baseNote + 3;
         notes[2] = baseNote + 6;
      }
      // dominant 7th
      else if  ( chordType.matches("7th") || chordType.matches("7") ) {
      
         notes = new int[4]; // dominant 7th has four notes
         
         notes[0] = baseNote;       // root
         notes[1] = baseNote + 4;   // major third
         notes[2] = baseNote + 7;   // perfect fifth
         notes[3] = baseNote + 10;  // minor seventh
      }
      // major 7th
      else if  ( chordType.matches("major7")   || chordType.matches("m7") || 
                 chordType.matches("major7th") || chordType.matches("m7th") ||
                 chordType.matches("maj7th")   || chordType.matches("maj7") ) {
      
         notes = new int[4]; // major 7th has four notes
         
         notes[0] = baseNote;       // root
         notes[1] = baseNote + 4;   // major third
         notes[2] = baseNote + 7;   // perfect fifth
         notes[3] = baseNote + 11;  // major seventh
      }
      // minor 7th
      else if  ( chordType.matches("minor7")   || chordType.matches("min7") ||
                 chordType.matches("minor7th") || chordType.matches("min7th") ) {
      
         notes = new int[4]; // minor 7th has four notes
         
         notes[0] = baseNote;       // root
         notes[1] = baseNote + 3;   // minor third
         notes[2] = baseNote + 7;   // perfect fifth
         notes[3] = baseNote + 10;  // minor seventh
      }
      // diminished 7th
      else if  ( chordType.matches("diminished7th")    || chordType.matches("dim7th") ||
                 chordType.matches("diminished7")      || chordType.matches("dim7") ) {
      
         notes = new int[4]; // diminished 7th has four notes
         
         notes[0] = baseNote;       // root
         notes[1] = baseNote + 3;   // minor third
         notes[2] = baseNote + 6;   // diminished fifth
         notes[3] = baseNote + 9;   // diminished seventh
      }

      return notes;
   }
*/   
   /**
    * helper function for addNotes and addChords
    * @param splitLine 
    */
/*   private long getPhraseToDieOn( String[] splitLine ) {
      
      long phraseToDieOn;
      
      String [] loopInstructions = splitLine[splitLine.length-1].split("-");  // split the last operand on '-'
      
      // if the last operand is loop
      if ( loopInstructions.length == 2 &&               // there was exactly one dash
           loopInstructions[0].matches("loop") ) {       // left of the dash was the command loop
         
         // dieOn will be the phrase number this note stops looping on
         Long dieOn;
         
         // gets the phrase number which the this note should stop looping
         if ( loopInstructions[1].matches("f") ) {       // loop forever
            dieOn = new Long( -2 );                      // phrase number will never match phraseNumber + 1 - 2
         } else{
            dieOn = new Long( loopInstructions[1] );     // loop until phrase number
         }
         
         // it starts playing on the next phrase, so we want it to stop playing at the next phrase + dieOn
         phraseToDieOn = scoreHolder.getPhraseNumber() + 1 + dieOn.longValue();
         
      }
      else {
         phraseToDieOn = scoreHolder.getPhraseNumber() + 2;
      }
      
      return phraseToDieOn;
   }
*/   
   /**
    * adds notes from broadcast
    * @param splitLine broadcast string split on #
    */
/*   private void addNotes(String[] splitLine) {
      
      // gets the Phrase Number this note dies on
      long phraseToDieOn = getPhraseToDieOn( splitLine );
      
      // Used to clean up broadcast (removes #loop-x)
      String [] loopInstructions = splitLine[splitLine.length-1].split("-");  // split the last operand on '-'
      
      // if the last operand is loop
      if ( loopInstructions[0].matches("loop") ) {       // left of the dash was the command loop

         // newSplitLine will have all the note commands but not the loop command
         String [] newSplitLine = new String[ splitLine.length - 1 ];
         
         // fill newSplitLine with all commands except 'loop'
         for ( int i = 0 ; i < newSplitLine.length; ++i ) {
            newSplitLine[i] = splitLine[i];
         }
         // puts the result in splitLine
         splitLine = newSplitLine;
      }
            
      // must have at least 3 total operands, the command, phrase number, and note to add
      if (splitLine.length < 3) {
         throw new RuntimeException();
      }

      // add each note
      for (int i = 2; i < splitLine.length; ++i) {

         String noteLengthString = splitLine[i].split("-")[1];
         Integer noteNumber;
         Double noteLength = new Double( findNoteLength(noteLengthString) ); // this value will be adjusted below

         // Is it a rest or a regular note
         if (splitLine[i].split("-")[0].toLowerCase().matches("rest")) {
            noteNumber = new Integer(REST);
         } else {
            noteNumber = new Integer(splitLine[i].split("-")[0]);
         }
         
         // add Notes to phrase
         try {
            // gets the phrase number from the second operand in splitLine
            // calls the addNote method of the phrase we are adding to
            Integer index = new Integer(splitLine[1]);             // gets the index for the phrase number
            
            SpecialNote specialNote = new SpecialNote(   noteNumber.intValue(), 
                                                         noteLength.doubleValue(), 
                                                         t.getName(), 
                                                         phraseToDieOn );
            
            scoreHolder.phrases[ index.intValue()].addNote( specialNote );
            
         } catch (Exception e) {
            System.err.println("Error adding notes in addNotes()");
         }
      } // end for
   }
*/   
}
