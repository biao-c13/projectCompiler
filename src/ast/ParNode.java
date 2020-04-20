package ast;

import java.util.ArrayList;

import util.Environment;
import util.SemanticError;

public class ParNode implements Node {

  public String id;
  public Node type;
  
  public ParNode (String i, Node t) {
   id=i;
   type=t;
  }
  
  public String getId(){
	  return id;
  }
  
  public Node getType(){
	  return type;
  }
  
  @Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {

	  return new ArrayList<SemanticError>();
	}
  
  public String toPrint(String s) {
	  return s+"\n Par:" + id +" "
			 +type.toPrint(s+"  ") ; 
  }
  
  //non utilizzato
  public Node typeCheck () {
     return null;
  }
  
  //non utilizzato
  public String codeGeneration() {
		return "";
  }
    
}  