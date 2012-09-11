/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OrchestraTest;

import java.util.HashMap;
import java.util.Map;

import jm.music.data.Note;

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
      void invoke(String arguments, String caller );
   }
   
   
   /** 
    * Private constructor... there should only be one interpretor
    */
   private Interpretor() {
   
      // initialize our hash map with procedures
      functionMap.put("test", new Command() 
              { public void invoke(String arguments, String caller)          { System.out.println("Test: " + arguments); } });
      
      functionMap.put("addnotes", new Command() 
              { public void invoke(String arguments, String caller)          { addNotes(arguments, caller); } });
      
      functionMap.put("clearphrase", new Command() 
              { public void invoke(String arguments, String caller)          { clearPhrase(arguments, caller ); } });
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
    * @param callers the name of the thread that called the interpretor
    */
   public void interp( String procedure, String arguments, String caller ) {
      procedure = procedure.trim().toLowerCase();
      arguments = arguments.trim().toLowerCase();
      
      try {
         functionMap.get(procedure).invoke(arguments, caller );
      } catch (Exception e) {
         System.err.println("Bad function name: " +procedure+" passed to interpretor!");
      }
   }
   
   /**
    * Calls the interpretor
    * @param procedureAndArguments a string with the procedure name and arguments separated by a '#'
    * @param caller the name of the thread that called the interpretor
    */
   public void interp( String procedureAndArguments, String caller ) {      
      String procedure = procedureAndArguments.split("#")[0];
      String arguments = procedureAndArguments.split("#")[1];
      
      interp(procedure, arguments, caller);    // I don't want to have to change it in two places...
   }
   
   
   
   /* procedures  **************************************************************/
   
   private void addNotes(String arguments, String caller) {
      System.out.println("addNotes() with args: " + arguments);
      
      int noteNum       = new Integer(arguments.split(",")[0]).intValue();
      double noteLength  = new Double(arguments.split(",")[1]).doubleValue();
      
      Note myNote = new Note( noteNum, noteLength );
      
      ScoreHolder.getInstance().phraseMap.get( caller ).addNote( myNote );
   }
   
   private void clearPhrase(String arguments, String caller ) {
      System.out.println("clearPhrase() with args: " + arguments);
      
      ScoreHolder.getInstance().phraseMap.get( caller ).empty();
   }
}
