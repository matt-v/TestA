/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OrchestraTest;

/**
 * Future clearPhrase events
 * Allows for single play of phrases and terminating loops
 * @author mattvaughan
 */
public class ClearPhrase {
   
   private String caller;
   private int phraseNum;
   
   public ClearPhrase( String caller, int phraseNum ) {
      this.caller = caller;
      this.phraseNum = phraseNum;
   }
   
   public int getPhraseNum() {
      return phraseNum;
   }
   
   public String getCaller() {
      return caller;
   }
}
