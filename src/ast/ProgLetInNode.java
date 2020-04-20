package ast;
import java.util.ArrayList;
import java.util.HashMap;

import lib.FOOLlib;
import util.Environment;
import util.SemanticError;

public class ProgLetInNode implements Node {

  private ArrayList<Node> declist;  //list of declarations
  private Node exp;     //espressione
  private Node stm;     //assegnamento
  
  public ProgLetInNode (ArrayList<Node> d, Node e,Node a) {
    declist=d;
    exp=e;
    stm=a;
  }
  
  public String toPrint(String s) {
	String declstr="";
    for (Node dec:declist)
      declstr += dec.toPrint(s+" ");
    if (exp!=null) {
        return s + "ProgLetIn:\n" + declstr +"\nexp: \n " + exp.toPrint(s + "  ");
    }else {
        return s + "ProgLetIn:\n" + declstr +"\nstm: \n" + stm.toPrint(s + "  ");
    }
  }
  
  @Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
	  env.nestingLevel++;
	  env.functionNestingLevel++;
	  //------------
      HashMap<String,STentry> hm = new HashMap<String,STentry> ();
      env.symTable.add(hm);
      //------------
      HashMap<String,FTentry> fm= new HashMap<String,FTentry>();
      env.functionTable.add(fm);
      //------------
      //declare result list
      ArrayList<SemanticError> res = new ArrayList<SemanticError>();
      
      //check semantics in the dec list
      if(declist.size() > 0){
    	  env.offset = -2;
    	  for(Node n : declist)
    		  res.addAll(n.checkSemantics(env));
      }
      //check semantics in the exp body
      if (exp!=null){
          res.addAll(exp.checkSemantics(env));

      }//check semantics in the stm body
      if (stm!=null){
          res.addAll(stm.checkSemantics(env));
      }
      //clean the scope, we are leaving a let scope
      //------------
      env.functionTable.remove(env.functionNestingLevel);
      env.functionNestingLevel--;
      //------------
      env.symTable.remove(env.nestingLevel);
      env.nestingLevel--;
      //------------
      return res;
	}
  
  public Node typeCheck () {
    for (Node dec:declist)
      dec.typeCheck();
    if (exp!=null){
        return exp.typeCheck();
    }else{
        return stm.typeCheck();
      }
  }
  
  public String codeGeneration() {
      if(exp!=null){
          String declCode="";
          for (Node dec:declist)
              declCode+=dec.codeGeneration();
          return  "push 0\n"+
                  declCode+
                  exp.codeGeneration()+"halt\n"+
                  FOOLlib.getCode();
      }else{
          String declCode="";
          for (Node dec:declist)
              declCode+=dec.codeGeneration();
          return  "push 0\n"+
                  declCode+
                  stm.codeGeneration()+"halt\n"+
                  FOOLlib.getCode();
      }
  } 


}  