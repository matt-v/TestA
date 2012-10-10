/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OrchestraTest;

import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author mattvaughan
 */
public class SocketListenerThread implements Runnable {
   
   private Thread t;                         // thread
   
   private ServerSocket serverSocket;
   
   
   public SocketListenerThread() { 
      
      try {
         serverSocket = new ServerSocket( 42001 ); // Put our server on the Scratch port
      }
      catch ( Exception e ) {
         System.err.println( "There was an error in SocketListenerThread constructor while trying to listen on port 42001" );
      }
      
      t = new Thread( this, "SocketListener" );
      t.start();
   }
   
   public void run() {
      
      Socket clientSocket = null;
      while( true ) {
         // Step 1 - Acception connection from a client
         try {
            clientSocket = serverSocket.accept();     // wait for a client to connect
         } catch (Exception e) {
            System.err.println("Problem accepting connection on port 42001: SocketListenerThread.run()");
            // Exiting here would be an over reaction... the thread should be able to deal with a failed connection
            // gracefully without needing to be reset.
         }

         // Step 2 - Create a thread for the new connection
         if (clientSocket != null) {
            System.err.println("Connect made to " + clientSocket.toString());
            new ClientThread(clientSocket);
         }
      }
   }
}
