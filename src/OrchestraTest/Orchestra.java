/*
 * Matthew Vaughan - Jun 23, 2012
 * matt884987@gmail.com
 * Sample laptop orchesta program.
 */
package OrchestraTest;

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
      new SocketListenerThread();
      
      // NOTE:
      //   To add more users to the mix here all you would need to do
      //   is make more instance of SocketThread with their IP addresses  
   }
}
