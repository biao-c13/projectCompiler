package ast;

import java.util.ArrayList;

import util.Environment;
import util.SemanticError;

public class IdNode implements Node {

  private String id;
  private STentry entry;
  private int nestinglevel;
  
  public IdNode (String i) {
    id=i;
  }
  
  public String toPrint(String s) {
	return s+"Id:" + id ;
  }
  
  @Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
	  //create result list
	  ArrayList<SemanticError> res = new ArrayList<SemanticError>();
	  int level=env.nestingLevel;
	  STentry tmp=null; 
	  while (level>=0 && tmp==null)
		  tmp=(env.symTable.get(level--)).get(id);    //search id on variable table
      if (tmp==null)
          res.add(new SemanticError("id: "+id+" not declared"));
      else{
          entry = tmp;
          nestinglevel = env.nestingLevel;
          if(tmp.type instanceof ClassTypeNode && env.objectTable.get(((ClassTypeNode)entry.type).id)!=null ){ //check if id has type class
              ((ClassTypeNode)entry.type).listaobjectTable=env.objectTable;
              ((ClassTypeNode)entry.type).objcetTable=env.objectTable.get(((ClassTypeNode)entry.type).id);
          }
      }


	  return res;
	}
  
  public Node typeCheck () {
   if (entry == null || entry.getType()==null){
        System.out.println(" id: " + id+ " type not found");
        return new NullNode();
    }else{
        return entry.getType();
    }

  }
  
  public String codeGeneration() {
      String getAR="";
	  for (int i=0; i<nestinglevel-entry.getNestinglevel(); i++) 
	    	 getAR+="lw\n";
	    return "push "+entry.getOffset()+"\n"+ //load offset on stack
		       "lfp\n"+getAR+ //risalgo la catena statica
			   "add\n"+       //sum up offeset and nestinglevel in order to access the value of variable
               "lw\n"; //carico sullo stack il valore all'indirizzo ottenuto

  }
}  