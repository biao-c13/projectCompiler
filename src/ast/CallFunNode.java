package ast;
import java.util.ArrayList;

import util.Environment;
import util.SemanticError;
import lib.FOOLlib;

public class CallFunNode implements Node {

  private String id;
  private STentry entry; 
  private ArrayList<Node> parlist;   //tipo varNode
  private int nestinglevel;
  public FTentry Fentry;
  
  public CallFunNode (String i, STentry e, ArrayList<Node> p, int nl) {
    id=i;
    entry=e;
    parlist = p;
    nestinglevel=nl;
  }
  
  public CallFunNode(String text, ArrayList<Node> args) {
	id=text;
    parlist = args;
}

public String toPrint(String s) {  //
    String parlstr="";
	for (Node par:parlist)
	  parlstr+=par.toPrint(s+"  ");		
	return s+"Call:" + id + " at nestlev " + nestinglevel +"\n"
           +parlstr;        
  }

@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		//create the result
		ArrayList<SemanticError> res = new ArrayList<SemanticError>();


		 int j=env.functionNestingLevel;
		 FTentry tmp=null;
		 while (j>=0 && tmp==null)     //check if function was declared on the list of function table
		     tmp=(env.functionTable.get(j--)).get(id);
		 if (tmp==null) {
             res.add(new SemanticError("Function: " + id + " not declared"));
         }
		 else{
			 this.Fentry = tmp;
			 this.nestinglevel = env.nestingLevel;  //modify
		//	 nestinglevel=env.functionNestingLevel;
		//	 System.out.println("ìììììì"+env.nestingLevel+ " "+ env.functionNestingLevel);
			 for(Node arg : parlist){
				 res.addAll(arg.checkSemantics(env));
			 }
		 }
		 return res;
	}
  
  public Node typeCheck () {  //
      if (Fentry!=null){
          if (parlist.size() == Fentry.parTypes.size() ){
              for (int i=0; i<Fentry.parTypes.size();i++){
                  Node par=parlist.get(i).typeCheck();
                  if (! (FOOLlib.isSubtype(par ,Fentry.parTypes.get(i))) )
                      System.out.println("error call function"+ id  +" type parameter " +parlist.get(i).toPrint("") +" "+ Fentry.parTypes.get(i).toPrint("") );
              }
          }else{
              System.out.println("wrong number of parameters in function " +id +" " +Fentry.parTypes.size()+ "-" +parlist.size());
          }
      }
      Node t=Fentry.type;
     return t;
  }
  
  public String codeGeneration() {
	    String parCode="";
	    for (int i=parlist.size()-1; i>=0; i--)
	    	parCode+=parlist.get(i).codeGeneration();
	    
	    String getAR="";
		  for (int i=0; i<nestinglevel-Fentry.getNestinglevel(); i++)
		    	 getAR+="lw\n";
       // System.out.println("-------"+nestinglevel +" "+Fentry.getNestinglevel() + getAR);





      return "lfp\n"+ 				// copy fp on the stack
               parCode+              //copy parameters on the stack
               "lfp\n"+getAR+ 		// setto AL risalendo la catena statica
               						// ora recupero l'indirizzo a cui saltare e lo metto sullo stack
               "push "+Fentry.getOffset()+"\n"+ // metto offset sullo stack
		       "lfp\n"+getAR+ 		// risalgo la catena statica
			   "add\n"+
               "lw\n"+ 				// load the value from the memory cell pointed by top
		       "js\n";
  }

}  