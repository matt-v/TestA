/*
 * Matthew Vaughan - Sep/20/2012
 * matt884987@gmail.com
 * Defines the main types for the interpretor
 */
package OrchestraTest;

/**
 *
 * @author mattvaughan
 */
public abstract class TypeAndValue {
   
   protected String type;
   
   public String getType() {
      return type;
   }
   
   public abstract Object getValue();
}

class MyVoid extends TypeAndValue {
   
   private Void value;
   
   public MyVoid() {
      this.type = "Void";
   }
   
   public Void getValue() {
      return value;
   }
}


class MyInteger extends TypeAndValue {
   
   private Integer value;
   
   public MyInteger( int value ) {
      this.type = "Integer";
      this.value = value;
   }
   
   public Integer getValue() {
      return value;
   }
}

class MyString extends TypeAndValue {
   
   private String value;
   
   public MyString( String value ) {
      this.type = "String";
      this.value = value;
   }
   
   public String getValue() {
      return value;
   }
}

class MyDouble extends TypeAndValue {
   
   private Double value;
   
   public MyDouble( double value ) {
      this.type = "Double";
      this.value = value;
   }
   
   public Double getValue() {
      return value;
   }
}