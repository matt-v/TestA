/*
 * Matthew Vaughan
 * matt884987@gmail.com
 */
package OrchestraTest;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import jm.music.data.*;
import jm.JMC;

/**
 * Singleton interpretor, recursively calls itself until it evaluates to a TypeAndValue, see interp()
 * @author mattvaughan
 */
public class Interpreter implements JMC {

   // singleton instance of the interpretor
   private static Interpreter SINGLETON_INSTANCE = new Interpreter();
   
   // we should hold on to the ScoreHolder locally...
   ScoreHolder scoreHolder = ScoreHolder.getInstance();
   
   // our hashmap for functions
   private Map<String, Command> functionMap = new HashMap<String, Command>();

   // We'll need this since Java doesn't allow first class refs...
   public interface Command {
      TypeAndValue invoke(Vector<TypeAndValue> arguments, String caller);
   }

   /**
    * Private constructor... there should only be one interpretor
    */
   private Interpreter() {

      // initialize our hash map with procedures
      // Note: caller can be thought of as a 'hidden' argument associated with the ClientThread who called the procedure

      functionMap.put("addnote", new Command() {
         public TypeAndValue invoke(Vector<TypeAndValue> arguments, String caller) { return addNote(arguments, caller); }});
      
      functionMap.put("addrest", new Command() {
         public TypeAndValue invoke(Vector<TypeAndValue> arguments, String caller) { return addRest(arguments, caller); }});
      
      functionMap.put("clearphrase", new Command() {
         public TypeAndValue invoke(Vector<TypeAndValue> arguments, String caller) { return clearPhrase(arguments, caller); }});
      
      functionMap.put("setinstrument", new Command() {
         public TypeAndValue invoke(Vector<TypeAndValue> arguments, String caller) { return setInstrument(arguments, caller); }});
      
      functionMap.put("addchord", new Command() {
         public TypeAndValue invoke(Vector<TypeAndValue> arguments, String caller) { return addChord(arguments, caller); }});
      
      functionMap.put("+", new Command() {
         public TypeAndValue invoke(Vector<TypeAndValue> arguments, String caller) { return plus(arguments, caller); }});
      
      functionMap.put("currentphrase", new Command() {
         public TypeAndValue invoke(Vector<TypeAndValue> arguments, String caller) { return currentPhrase(arguments, caller); }});
      
      functionMap.put("queue", new Command() {
         public TypeAndValue invoke(Vector<TypeAndValue> arguments, String caller) { return addToQueue(arguments, caller); }});
      
   }

   /**
    * Get the singleton instance of the interpretor
    * @return the interpretor
    */
   public static Interpreter getInstance() {
      return SINGLETON_INSTANCE;
   }

   // overloaded interpretor function calls - call with procedure and arguments (split) or with procedureAndArguments (stil paired)
   /**
    * Calls the interpretor
    *
    * @param procedure the string which maps to the correct procedure
    * @param arguments the string of arguments for the procedure
    * @param callers the name of the thread that called the interpretor
    */
   public TypeAndValue interpEvaluator(String procedure, String arguments, String caller) {

      TypeAndValue ret = new MyVoid();

      procedure = procedure.trim().toLowerCase();
      arguments = arguments.trim().toLowerCase();

      // will contain each argument, typed.
      Vector<TypeAndValue> argumentVector = new Vector<TypeAndValue>();

      // break arguments into a vector, each containing exactly one argument
      while (arguments.compareTo("") != 0) {

         // there are two possible cases... a value or procedure
         // if it starts with at '@' then it's a procedure
         if (arguments.startsWith("@")) {

            int startIndex = arguments.indexOf("@");
            int endIndex = -1;                           // if this remains at -1 then we know we've made a mistake
            int firstParen = arguments.indexOf("(");     // first paren

            int numberOfParens = 1;                      // start 1 parens deep
            // find the end of this argument by parsing parens...
            for (int i = firstParen + 1; i < arguments.length(); ++i) {

               if (arguments.charAt(i) == '(') {
                  numberOfParens++;
               }
               if (arguments.charAt(i) == ')') {
                  numberOfParens--;
               }

               if (numberOfParens == 0) {
                  endIndex = i;              // our ending index is the last paren
                  i = arguments.length();    // so the loop terminates
               }
            } // end for

            // finally we have a current argument
            String currentArgument = arguments.substring(startIndex, endIndex + 1);

            // and we remove that argument from the list
            arguments = arguments.substring(endIndex + 1).trim();

            // remove the leading comma
            if (arguments.startsWith(",")) {
               arguments = arguments.substring(1);
            }

            // begin mutual recursion
            argumentVector.add(interp(currentArgument, caller));

            System.out.println(currentArgument + " : " + arguments); //DEBUG
         } // otherwise it's a value (this is the terminal case of the mutual recursion)
         else {

            String currentArgument;

            // if this is one of multiple arguments
            if (arguments.contains(",")) {
               
               int endIndex = 0;
               
               if ( arguments.startsWith("'") ) {
                  System.err.println("STARTS WITH QUOTE");
                  int endQuote = arguments.indexOf("'", 1);
                  System.err.println("END QUOTE AT " +endQuote);
                  if ( arguments.charAt(endQuote+1 ) == ',' ) {
                     endIndex = endQuote+1;
                     System.err.println("COMMA AT "+endIndex);
                  }
                  else {
                     endIndex = endQuote;
                  }
               }
               else {
                  endIndex = arguments.indexOf(",");   
               }
                  
               currentArgument = arguments.substring(0, endIndex);
               arguments = arguments.substring(endIndex + 1);
     
               System.out.println(currentArgument);
               System.err.println(" --- " + arguments);
            } // if this is a lone argument
            else {
               currentArgument = arguments;

               // remove this argument from arguments
               arguments = "";
            }

            // what's the type?
              // integer
            if (currentArgument.matches("\\d+")) {
               int value = new Integer(currentArgument).intValue();
               argumentVector.add(new MyInteger(value));
            } // number (double)
            else if (currentArgument.matches("\\d*.\\d+")) {
               double value = new Double(currentArgument).doubleValue();
               argumentVector.add(new MyDouble(value));
            } // string
            else if ( currentArgument.startsWith("'") && currentArgument.endsWith("'") ) {
               argumentVector.add(new MyString(currentArgument));
            }
               // type unknown
            else {
               System.err.println("Type of " + currentArgument + " unknown!");
               System.exit(-1);
            }
         }
      }

      try {
         ret = functionMap.get(procedure).invoke(argumentVector, caller);
      } catch (Exception e) {
         System.err.println("Bad function name: " + procedure + " passed to interpretor!");
      }

      return ret;
   }

   /**
    * Calls the interpretor
    * @param procedureAndArguments a string with the procedure name and
    * arguments separated by a '#'
    * @param caller the name of the thread that called the interpretor
    */
   public TypeAndValue interp(String procedureAndArguments, String caller) {

      int firstAt = procedureAndArguments.indexOf("@");
      int firstPound = procedureAndArguments.indexOf("(");
      int lastPound = procedureAndArguments.lastIndexOf(")");


      // split from first @ to first (
      String procedure = procedureAndArguments.substring(firstAt + 1, firstPound);

      // and puts the remainder in arguments
      String arguments = procedureAndArguments.substring(firstPound + 1, lastPound);

      System.out.println(procedure + " with args " + arguments);

      return interpEvaluator(procedure, arguments, caller);    // I don't want to have to change it in two places...
   }

   /* *************************************************************************
    * procedures  *************************************************************
    * *************************************************************************
    */
   private TypeAndValue addNote(Vector<TypeAndValue> arguments, String caller) {
      
      // default values so netbeans wont yell at me, at the interpretor wont die if the procedure fails
      Integer noteNum = new Integer(0);
      Double noteLength = new Double(0);

      
      if ( arguments.size() != 2 ) {
         System.err.println("addNote expected 2 arguments and got " + arguments.size() );
         System.exit(-1);
      }
      
      if (arguments.get(0).getType().compareTo("Integer") == 0) {
         noteNum = (Integer) arguments.get(0).getValue();    // we know it's an Integer, cause we checked
      } else {
         System.err.println("addNote expected Integer as first argument and got " + arguments.get(0).getType());
      }

      if (arguments.get(1).getType().compareTo("Double") == 0) {
         noteLength = (Double) arguments.get(1).getValue();    // we know it's an Integer, cause we checked
      } else {
         System.err.println("addNote expected Double as second argument and got " + arguments.get(1).getType());
      }

      Note myNote = new Note(noteNum.intValue(), noteLength.doubleValue());

      scoreHolder.phraseMap.get(caller).addNote(myNote);

      return new MyVoid();
   }

   // adds a rest
   private TypeAndValue addRest(Vector<TypeAndValue> arguments, String caller) {

      // default value so netbeans wont yell at me, at the interpretor wont die if the procedure fails
      Double restLength = new Double(0);

      if (arguments.size() != 1) {
         System.err.println("addRest expected one argument and got " + arguments.size());
         System.exit(-1);
      }

      if (arguments.get(0).getType().compareTo("Double") == 0) {
         restLength = (Double) arguments.get(0).getValue();    // we know it's a Double, cause we checked
      } else {
         System.err.println("Type Mismatch in addRest Expected Double and got " + arguments.get(0).getType());
      }

      Note myNote = new Note(REST, restLength.doubleValue());

      scoreHolder.phraseMap.get(caller).addNote(myNote);

      return new MyVoid();
   }

   
   private TypeAndValue clearPhrase(Vector<TypeAndValue> arguments, String caller) {
      
      if ( arguments.size() > 1) {
         System.err.println("ClearPhrase expected one or no arguments and got " + arguments.size() );
         System.err.println("Arguments will be ignored");
      }
      // if we get one argument then it's a future event 
      else if ( arguments.size() == 1 ) {
         // argument must be an integer
         if ( arguments.get(0).getType().compareTo("Integer") != 0 ) {
            System.err.println("ClearPhrase expected an Integer or no arguments but got a " + arguments.get(0).getType() );
         }
         else {
            int phraseToDieOn = ((Integer) arguments.get(0).getValue()).intValue();
            
            /*if ( scoreHolder.futureMap.get( caller ).endMeasureExist( phraseToDieOn ) ){
                scoreHolder.futurePartMap.get( caller ).removeAllPhrases();
            }*/
            scoreHolder.addClearPhrase(caller, phraseToDieOn);
         }
         
      }

      scoreHolder.phraseMap.get(caller).empty();

      return new MyVoid();
   }

   private TypeAndValue setInstrument(Vector<TypeAndValue> arguments, String caller) {
      System.out.println("setInstrument() with args: " + arguments);

      // set it to zero so java my IDE doesn't yell at me
      int instrumentNumber = 0;

      if (arguments.size() < 1) {
         System.err.println("setInstrument expected one argument got zero!");
         System.exit(-1);
      }

      // check the type and if correct type get the value
      if (arguments.get(0).getType().compareTo("Integer") == 0) {
         instrumentNumber = ((Integer) arguments.get(0).getValue()).intValue();
      } else {
         System.err.println("setInstrument expected an Integer but got a " + arguments.get(0).getType() );
      }
      

      // finally, change the instrument number and return a MyVoid to the interpretor
      scoreHolder.partMap.get(caller).setInstrument(instrumentNumber);

      return new MyVoid();
   }

   private TypeAndValue addChord(Vector<TypeAndValue> arguments, String caller) {
      
      System.err.println("********** GOT THE THE BEGINIG OF addChord **************");

      if (arguments.size() < 2) {
         System.err.println("addChord expected two or more arguments and got " + arguments.size());
         System.exit(-1);
      }

      Vector<Integer> notes = new Vector<Integer>();
      
      // default value to keep netbeans from yelling and the interpretor from dieing if procedure fails      
      double noteLength = 0;
      
      // get each of the notes
      for ( int i = 0; i < (arguments.size()-1); ++i ) {
         // check the type and if correct type get the value
         if (arguments.get(i).getType().compareTo("Integer") == 0) {
            notes.add( ((Integer) arguments.get(i).getValue()) );
         }
         System.err.println("**** " + i + " ****");
      }
      
      // get the note length
      if (arguments.get( arguments.size()-1 ).getType().compareTo("Double") == 0) {
         noteLength = ((Double) arguments.get( arguments.size()-1 ).getValue()).doubleValue();
      }

      Integer[] temp = new Integer[notes.size()];
      temp = notes.toArray(temp);
      int[] notePitches = new int[temp.length];
            
      for ( int i = 0; i < temp.length; ++i ) {
         notePitches[i] = temp[i].intValue();
      }

      System.err.println("got to the end of addchord ****************");
      scoreHolder.phraseMap.get(caller).addChord(notePitches, noteLength);

      return new MyVoid();
   }
   
   // does MyDouble and MyInteger addition
   //   If there is a mix then MyIntegers are promoted to MyDoubles and a MyDouble is returned
   private TypeAndValue plus(Vector<TypeAndValue> arguments, String caller) {
      
      if ( arguments.size() < 1 ) {
         System.err.println("Plus expected at least one argument and got zero.");
         System.exit(-1);
      }
      
      // our return value
      TypeAndValue ret = new MyVoid();
      
      // what type of arithmetic?
      boolean canDoInt     = true;
      boolean canDoDouble  = true;
      
      // check the types, if all ints return an int, if there are any double use doubles, string fail
      for ( int i = 0; i < arguments.size(); ++i ) {
         
         // if any argument is not an integer
         if ( arguments.get(i).getType().compareTo("Integer") != 0 ) {
            
            // we cant do integer arthimetic
            canDoInt = false;
            
            // if it is also not a double
            if ( arguments.get(i).getType().compareTo("Double") != 0 ) {
               canDoDouble = false;
            }
            
         }
      }
      
      if ( canDoInt ) {
         int total = 0;
         for ( int i = 0; i < arguments.size(); ++i ) {
            total += ((Integer) arguments.get(i).getValue()).intValue();
         }
         ret = new MyInteger( total );
         
      } else if ( canDoDouble) {
         double total = 0;
         for ( int i = 0; i < arguments.size(); ++i ) {
            
            // if it's an int
            if ( arguments.get(i).getType().compareTo("Integer") == 0 ) {
               total += ((Integer) arguments.get(i).getValue()).doubleValue();
            }
            // it must be a double
            else {
               total += ((Double) arguments.get(i).getValue()).doubleValue();
            }
            ret = new MyDouble( total );
         }
      
      } else {
         System.err.println("Error in plus()! Can only add types that are Integers or Doubles!");
         System.exit(-1);
      }
      
      return ret;
   }
   
   // returns a MyInteger containing the phraseNumber from SocketHolder
   private TypeAndValue currentPhrase(Vector<TypeAndValue> arguments, String caller) {
      
      MyInteger ret = new MyInteger( scoreHolder.getPhraseNumber() );
      return ret;
   }
   
   private TypeAndValue addToQueue(Vector<TypeAndValue> arguments, String caller) {
       
      if ( arguments.size() != 2 ) {
         System.err.println("Queue() expected 2 arguments and got " + arguments.size() );
         System.exit(-1);
      }
      
      String command = "";
      int phraseNumber = 0;
      
      // if first argument isn't a MyString
      if ( arguments.get(0).getType().compareTo("String") != 0 ) {
         System.err.println("Queues() expected first argument to be a String and got " + arguments.get(0).getType() );
         System.exit(-1);
      } else {
          command = (String) arguments.get(0).getValue();  // we know the type, because we checked 
      }
      
      // if second argument isn't an MyInteger
      if ( arguments.get(1).getType().compareTo("Integer") != 0 ) {
         System.err.println("Queues() expected second argument to be an Integer and got " + arguments.get(1).getType() );
         System.exit(-1);
      } else {
         phraseNumber = ((Integer) arguments.get(1).getValue()).intValue(); // we know the type, because we checked 
      }
      
      scoreHolder.addFutureEvent( command, caller, phraseNumber);

      
      return new MyVoid();
   }
}
