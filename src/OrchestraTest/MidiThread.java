/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OrchestraTest;

/**
 * This thread plays the music
 * @author mattvaughan
 */
class MidiThread implements Runnable {
   
   Thread t;
   private ScoreHolder scoreHolder = ScoreHolder.getInstance();
   
   MidiThread() {
      
      t = new Thread( this, "Midi Thread");
      t.start();
   }
   
   public void run() {
      try {
         while ( ! scoreHolder.getQuit() ) {
            
            // clean up and assebmle the score
            scoreHolder.assemble(); 
            
            if (   scoreHolder.getEndTime() != 0 &&      // make sure there is music to play
                   scoreHolder.getPlayScore() ) {        // and that it is time to play it
               
               scoreHolder.playScore();                  // play the music
               System.out.println("DEBUG--Play Music--Phrase # " + scoreHolder.getPhraseNumber() );
            }    
            
         } // end while
      } // end try
      catch ( Exception e ) {
         System.err.println( e.toString() );
         System.err.println("MidiThread error");
      }
      
      System.out.println( "Exiting " + t.getName() + "." );       // for debugging
   }   

}