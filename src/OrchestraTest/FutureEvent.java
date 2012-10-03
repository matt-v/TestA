/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OrchestraTest;

/**
 *
 * @author mattvaughan
 */
public class FutureEvent {
      
   private String command;
   private String caller;
   private int phraseNum;
   
   public FutureEvent( String command, String caller, int phraseNum ) {
      this.command = command;
      this.caller = caller;
      this.phraseNum = phraseNum;
   }
   
   public void execute() {
      TypeAndValue temp = Interpreter.getInstance().interp( command, caller );
   }
   
   public String getCommand() {
      return command;
   }

   public int getPhraseNum() {
      return phraseNum;
   }
   
   public String getCaller() {
      return caller;
   }
}
