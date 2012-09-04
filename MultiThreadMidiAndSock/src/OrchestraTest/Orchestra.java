/*
 * Matthew Vaughan - Jun 23, 2012
 * matt884987@gmail.com
 * Sample laptop orchesta program.
 */
package OrchestraTest;

import java.io.*;
import java.net.*;
import jm.music.data.*;
import jm.util.Play;
import jm.JMC;


/**
 * Here's my super simple main()
 * @author mattvaughan
 */
public class Orchestra {

   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) {
     
      // run our two main threads
      new MidiThread(); 
      new SocketThread( "127.0.0.1" );
      new SocketThread( "24.147.248.241" );
      new SocketThread( "98.217.222.83" );
      // NOTE:
      //   To add more users to the mix here all you would need to do
      //   is make more instance of SocketThread with their IP addresses  
   }
}
