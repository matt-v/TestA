/*
 * Matthew Vaughan
 * matt884987@gmail.com
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
               TypeAndValue temp = Interpreter.getInstance().interp( commands[i], t.getName() );
               System.out.println( "Returned a " + temp.getType() );
            }
               
         } // end while
      } // end try
      catch ( Exception e ) {
         System.err.println("Error in " + t.getName() +".run()!");
      }
      
      System.out.println( "Exiting " + t.getName() + "." );             // for debugging
   }
}
