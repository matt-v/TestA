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
   
   
   // we should hold on to the ScoreHolder locally...
   ScoreHolder scoreHolder = ScoreHolder.getInstance();
   
   // our hashmap for functions
   private Map<String,Command> functionMap = new HashMap<String,Command>();
   
   // We'll need this since Java doesn't allow first class refs...
   public interface Command {
      TypeAndValue invoke(String arguments, String caller );
   }
   
   
   /** 
    * Private constructor... there should only be one interpretor
    */
   private Interpretor() {
   
      // initialize our hash map with procedures
      
      functionMap.put("addnote", new Command() 
              { public TypeAndValue invoke(String arguments, String caller)   { return addNote(arguments, caller); } });
      
      functionMap.put("clearphrase", new Command() 
              { public TypeAndValue invoke(String arguments, String caller)   { return clearPhrase(arguments, caller ); } });
      
      functionMap.put("setinstrument", new Command() 
              { public TypeAndValue invoke(String arguments, String caller)   { return setInstrument(arguments, caller ); } });

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
   public TypeAndValue interp( String procedure, String arguments, String caller ) {
      
      TypeAndValue ret = new MyVoid();
      
      procedure = procedure.trim().toLowerCase();
      arguments = arguments.trim().toLowerCase();
      
      try {
         ret = functionMap.get(procedure).invoke(arguments, caller );
      } catch (Exception e) {
         System.err.println("Bad function name: " +procedure+" passed to interpretor!");
      }
      
      return ret;
   }
   
   /**
    * Calls the interpretor
    * @param procedureAndArguments a string with the procedure name and arguments separated by a '#'
    * @param caller the name of the thread that called the interpretor
    */
   public TypeAndValue interp( String procedureAndArguments, String caller ) {      
      
      int firstAt       = procedureAndArguments.indexOf("@");
      int firstPound    = procedureAndArguments.indexOf("(");
      int lastPound     = procedureAndArguments.lastIndexOf(")");
 
      // split from first @ to first (
      String procedure  = procedureAndArguments.substring(firstAt+1, firstPound);
      
      // and puts the remainder in arguments
      String arguments  = procedureAndArguments.substring( firstPound+1, lastPound );
      
      System.out.println( procedure + " with args " + arguments );
      
      return interp(procedure, arguments, caller);    // I don't want to have to change it in two places...
   }
   
   
   
   /* procedures  **************************************************************/
   
   private TypeAndValue addNote(String arguments, String caller) {
      System.out.println("addNotes() with args: " + arguments);
      
      int noteNum       = new Integer(arguments.split(",")[0]).intValue();
      double noteLength  = new Double(arguments.split(",")[1]).doubleValue();
      
      Note myNote = new Note( noteNum, noteLength );
      
      scoreHolder.phraseMap.get( caller ).addNote( myNote );
      
      return new MyVoid();
   }
   
   private TypeAndValue clearPhrase(String arguments, String caller ) {
      System.out.println("clearPhrase() with args: " + arguments);
      // if clear phrase was passed arguments they get ignored...
      // the caller is used to determine the phrase that is cleared
      
      scoreHolder.phraseMap.get( caller ).empty();
      
      return new MyVoid();
   }
   
   private TypeAndValue setInstrument( String arguments, String caller ) {
      System.out.println("setInstrument() with args: " +arguments );
      
      // set it to zero so java my IDE doesn't yell at me
      int instrumentNumber = 0;
      
      // if the argument is a function evaluate the function, otherwise it's a value...
      if ( arguments.contains( "@" ) ) {
         
         // call the interpretor on the argument, making the argument the procedure and argument
         TypeAndValue value = interp( arguments, caller );
         
         if ( value.getType().compareTo("Integer") == 0 ) {
            
            // we just checked the type above so we should be comfortable casting here
            Integer theValue = (Integer) value.getValue();
            
            instrumentNumber = theValue.intValue();
         }
         else {
            System.err.println("Interpretor type mismatch in Interpretor.setInstument()!");
            System.err.println("Expected Integer and got " + value.getType() );
            System.exit(-1);
         }
          
      }
      // else... the argument is a value
      else {
         instrumentNumber = new Integer( arguments.split(",")[0] ).intValue();
      }
      
      // finally, change the instrument number and return a MyVoid to the interpretor
      scoreHolder.partMap.get( caller ).setInstrument( instrumentNumber );
      
      return new MyVoid();
   }
   
}

