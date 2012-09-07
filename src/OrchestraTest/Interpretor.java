/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OrchestraTest;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mattvaughan
 */
public class Interpretor {
   
   // singleton instance of the interpretor
   private static Interpretor SINGLETON_INSTANCE = new Interpretor();
   
   // our hashmap for functions
   private Map<String,Command> functionMap = new HashMap<String,Command>();
   
   // We'll need this since Java doesn't allow first class refs...
   public interface Command {
      void invoke(String arguments);
   }
   
   
   /** 
    * Private constructor... there should only be one interpretor
    */
   private Interpretor() {
   
      // initialize our hash map with procedures
      functionMap.put("test", new Command() 
              { public void invoke(String arguments)          { System.out.println("Test: " + arguments); } });
      
      functionMap.put("addnotes", new Command() 
              { public void invoke(String arguments)          { addNotes(arguments); } });
      
      functionMap.put("clearphrase", new Command() 
              { public void invoke(String arguments)          { clearPhrase(arguments); } });
   }
   
   /**
    * Get the singleton instance of the interpretor
    * @return the interpretor
    */
   public static Interpretor getInstance() {
      return SINGLETON_INSTANCE;
   }
   
  
   
   // overloaded interpretor function calls - call with procedure and arguments (split) or with procedureAndArguments (stil paired)
   
   /**
    * Calls the interpretor
    * @param procedure the string which maps to the correct procedure
    * @param arguments the string of arguments for the procedure
    */
   public void interp( String procedure, String arguments ) {
      procedure = procedure.trim().toLowerCase();
      arguments = arguments.trim().toLowerCase();
      
      try {
         functionMap.get(procedure).invoke(arguments);
      } catch (Exception e) {
         System.err.println("Bad function name: " +procedure+" passed to interpretor!");
      }
   }
   
   /**
    * Calls the interpretor
    * @param procedureAndArguments a string with the procedure name and arguments separated by a '#'
    */
   public void interp( String procedureAndArguments ) {      
      String procedure = procedureAndArguments.split("#")[0];
      String arguments = procedureAndArguments.split("#")[1];
      
      interp(procedure, arguments);    // I don't want to have to change it in two places...
   }
   
   
   
   /* procedures  **************************************************************/
   
   private void addNotes(String arguments) {
      System.out.println("addNotes() with args: " + arguments);
   }
   
   private void clearPhrase(String arguments) {
      System.out.println("clearPhrase() with args: " + arguments);
   }
}
