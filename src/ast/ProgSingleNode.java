package ast;

import java.util.ArrayList;

import util.Environment;
import util.SemanticError;

public class ProgSingleNode implements Node {

  private Node exp;
  
  public ProgSingleNode (Node e) {
    exp=e;
  }
  
  public String toPrint(String s) {
    
    return "Prog Single Exp\n" + exp.toPrint("  ") ;
  }
  
  @Override
  public ArrayList<SemanticError> checkSemantics(Environment env) {
      return exp.checkSemantics(env);
  }
  
  public Node typeCheck() {
    return exp.typeCheck();
  }  
  
  public String codeGeneration() {
		return exp.codeGeneration()+"halt\n";
  }  
  
}  