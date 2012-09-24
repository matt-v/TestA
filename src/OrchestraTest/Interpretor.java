/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OrchestraTest;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import jm.music.data.*;

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
   private Map<String, Command> functionMap = new HashMap<String, Command>();

   // We'll need this since Java doesn't allow first class refs...
   public interface Command {

      TypeAndValue invoke(Vector<TypeAndValue> arguments, String caller);
   }

   /**
    * Private constructor... there should only be one interpretor
    */
   private Interpretor() {

      // initialize our hash map with procedures

      functionMap.put("addnote", new Command() {

         public TypeAndValue invoke(Vector<TypeAndValue> arguments, String caller) {
            return addNote(arguments, caller);
         }
      });

      functionMap.put("clearphrase", new Command() {

         public TypeAndValue invoke(Vector<TypeAndValue> arguments, String caller) {
            return clearPhrase(arguments, caller);
         }
      });

      functionMap.put("setinstrument", new Command() {

         public TypeAndValue invoke(Vector<TypeAndValue> arguments, String caller) {
            return setInstrument(arguments, caller);
         }
      });

      functionMap.put("addchord", new Command() {

         public TypeAndValue invoke(Vector<TypeAndValue> arguments, String caller) {
            return addChord(arguments, caller);
         }
      });
   }

   /**
    * Get the singleton instance of the interpretor
    *
    * @return the interpretor
    */
   public static Interpretor getInstance() {
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
               int endIndex = arguments.indexOf(",");
               currentArgument = arguments.substring(0, endIndex);

               // remove this argument from arguments
               arguments = arguments.substring(endIndex + 1);
               System.out.println(" --- " + arguments);
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
               argumentVector.add(new MyInt(value));
            } // number (double)
            else if (currentArgument.matches("\\d*.\\d+")) {
               double value = new Double(currentArgument).doubleValue();
               argumentVector.add(new MyDouble(value));
            } // string
            else {
               argumentVector.add(new MyString(currentArgument));
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
    *
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

   /*
    * procedures  *************************************************************
    */
   private TypeAndValue addNote(Vector<TypeAndValue> arguments, String caller) {
      System.out.println("addNotes() with args: " + arguments);

      // default values so netbeans wont yell at me, at the interpretor wont die if the procedure fails
      Integer noteNum = new Integer(0);
      Double noteLength = new Double(0);

      if (arguments.get(0).getType().compareTo("Integer") == 0) {
         noteNum = (Integer) arguments.get(0).getValue();    // we know it's an Integer, cause we checked
      } else {
         System.err.println("Type Mismatch in addNote! Expected Integer and got " + arguments.get(0).getType());
      }

      if (arguments.get(1).getType().compareTo("Double") == 0) {
         noteLength = (Double) arguments.get(1).getValue();    // we know it's an Integer, cause we checked
      } else {
         System.err.println("Type Mismatch in addNote! Expected Double and got " + arguments.get(1).getType());
      }

      Note myNote = new Note(noteNum.intValue(), noteLength.doubleValue());

      scoreHolder.phraseMap.get(caller).addNote(myNote);

      return new MyVoid();
   }

   private TypeAndValue clearPhrase(Vector<TypeAndValue> arguments, String caller) {
      System.out.println("clearPhrase() with args: " + arguments);
      // if clear phrase was passed arguments they get ignored...
      // the caller is used to determine the phrase that is cleared

      scoreHolder.phraseMap.get(caller).empty();
      scoreHolder.chordMap.get(caller).empty();

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
      }

      // finally, change the instrument number and return a MyVoid to the interpretor
      scoreHolder.partMap.get(caller).setInstrument(instrumentNumber);

      return new MyVoid();
   }

   private TypeAndValue addChord(Vector<TypeAndValue> arguments, String caller) {
      System.out.println("addChord() with args: " + arguments);

      if (arguments.size() < 4) {
         System.err.println("addChord expected 4 arguments and got " + arguments.size());
         System.exit(-1);
      }

      // default values to keep netbeans from yelling and the interpretor from dieing if procedure fails
      int noteNum1 = 0, noteNum2 = 0, noteNum3 = 0;
      double noteLength = 0;
      
      // check the type and if correct type get the value
      if (arguments.get(0).getType().compareTo("Integer") == 0) {
         noteNum1 = ((Integer) arguments.get(0).getValue()).intValue();
      }
      // check the type and if correct type get the value
      if (arguments.get(1).getType().compareTo("Integer") == 0) {
         noteNum2 = ((Integer) arguments.get(1).getValue()).intValue();
      }
      // check the type and if correct type get the value
      if (arguments.get(2).getType().compareTo("Integer") == 0) {
         noteNum3 = ((Integer) arguments.get(2).getValue()).intValue();
      }
      // check the type and if correct type get the value
      if (arguments.get(3).getType().compareTo("Double") == 0) {
         noteLength = ((Double) arguments.get(3).getValue()).doubleValue();
      }


      int[] notePitches = {noteNum1, noteNum2, noteNum3};

      scoreHolder.chordMap.get(caller).addChord(notePitches, noteLength);

      return new MyVoid();
   }
}
