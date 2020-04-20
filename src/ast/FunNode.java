package ast;
import java.util.ArrayList;
import java.util.HashMap;

import lib.FOOLlib;
import util.Environment;
import util.SemanticError;

public class FunNode implements Node {

  public String id;    //name of function
  public Node type;		//type of function
  public ArrayList<Node> parlist = new ArrayList<Node>();  //parameters of function
  public ArrayList<Node> declist =new ArrayList<Node>();   //declerations inside function
  public Node body;  //stm or exp
  public FTentry fTentry;
  public String lablefunction;
  
  public FunNode (String i, Node t) {
    id=i;
    type=t;
  }
  
  public void addDecBody (ArrayList<Node> d, Node b) {
    declist=d;
    body=b;
  }
  
  @Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
	  //create result list
	  ArrayList<SemanticError> res = new ArrayList<SemanticError>();

	  //start new  scope level
	  HashMap<String,FTentry> functionTable=env.functionTable.get(env.functionNestingLevel);
	  FTentry entryfunction = new FTentry(env.nestingLevel,env.offset--); //every function is update on function table, function table is stored on env
	  //start new variable level scope inside the function body
	  //env.offset = -2;
	  env.nestingLevel++;
	  HashMap<String,STentry> variableTable = new HashMap<String,STentry> ();
	  env.symTable.add(variableTable);

      if ( functionTable.put(id,entryfunction) != null ) {
		  res.add(new SemanticError("Function  " + id + " is already declared"));
	  }
      else{
      	 //check if the type of function is type class
      	  if (type instanceof ClassTypeNode){
      	  	if (((ClassTypeNode) type).id!=null){   //check if type class if already delcared or not
      	  		((ClassTypeNode) type).objcetTable=env.objectTable.get(((ClassTypeNode) type).id);
				((ClassTypeNode) type).listaobjectTable=env.objectTable;
			}else{
				res.add(new SemanticError("wrong declaration type class "+id));
			}
		  }

	      //list of parameters type
	      ArrayList<Node> parTypes = new ArrayList<Node>();
	      int paroffset=1;
	      //check parameter list
	      for(Node a : parlist){
	    	  ParNode arg = (ParNode) a;
	    	  parTypes.add(arg.getType());
	    	  STentry parametroEntry=new STentry(env.nestingLevel,arg.getType(),paroffset++);  // count up from fp
	    	  if ( variableTable.put(arg.getId(),parametroEntry) != null  )
	    	      res.add(new SemanticError("Parameter  "+arg.getId()+" already declared"));
	      }
	      //set func type
		  entryfunction.addTypeParametri(parTypes);
	      //end parameters declaration


	      //check declaration list
	      if(declist.size() > 0){
			  int offesetRecovery =env.offset;
	    	  env.offset = env.offset-2;  //AL e return address
	    	  //if there are declarations then check semantics for every child and save the results
	    	  for(Node n : declist)
	    		  res.addAll(n.checkSemantics(env));
	    	  env.offset=offesetRecovery-1;
	      }
	      //check body
	      res.addAll(body.checkSemantics(env));
		  System.out.println(body.toPrint(""));
	     // env.print();
      }
      //close scope
	  env.symTable.remove(env.nestingLevel);
	  env.nestingLevel--;

	  fTentry=functionTable.get(id);
	  functionTable.get(id).type=type;

      return res;
	}
  
  public void addPar (Node p) {
    parlist.add(p);
  }  
  
  public String toPrint(String s) {
	String parlstr="";
	if (parlist!=null)
		for (Node par:parlist)
	  		parlstr+=par.toPrint(s+"  ");
	String declstr="";
	if (declist!=null) 
	  for (Node dec:declist)
	    declstr+=dec.toPrint(s+"  ");
    return s+"\nFun:" + id +" "
		   +type.toPrint(s+"  ")
		   +parlstr
	   	   +declstr
           +body.toPrint(s+"  ") ; 
  }
  

  public Node typeCheck () {
	if (declist!=null) 
	  for (Node dec:declist)
		dec.typeCheck();
    Node t=body.typeCheck();
    return t;
  }
  
  public String codeGeneration() {


	    String declCode="";
	    if (declist!=null) for (Node dec:declist)
		    declCode+=dec.codeGeneration();
	    
	    String popDecl="";
	    if (declist!=null) for (Node dec:declist)
	    	popDecl+="pop\n";
	    
	    String popParl="";
	    if (parlist!=null)for (Node dec:parlist)
	    	popParl+="pop\n";
	    
	    String funl=FOOLlib.freshFunLabel();
	    lablefunction=funl;
	    FOOLlib.putCode(funl+":\n"+
	        "cfp\n"+ 		// copy $sp to $fp
	        "lra\n"+ 		// insert return address in stack
	    		declCode+ 		// insert local declarations on stack
	    		body.codeGeneration()+
	    		"srv\n"+ 		// pop result of body from top of stack and store it on return value ra
	    		popDecl+
	    		"sra\n"+ 		// pop del return address from stack                     -1
	    		"pop\n"+ 		// pop di AL                                              0
	    		popParl+
	    		"sfp\n"+  		// store top of stack into frame point fp                 -1
	    		"lrv\n"+ 		// load from return value rv  to stack

				"lra\n"+        //load from return address
				"js\n"  		// jump to next instruction pointed by top of stack
	    		);
		return "push "+ funl +"\n";
  }
  
}  