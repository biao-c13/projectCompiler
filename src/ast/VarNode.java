package ast;

import java.util.ArrayList;
import java.util.HashMap;

import sun.rmi.rmic.iiop.ClassType;
import util.Environment;
import util.SemanticError;
import lib.FOOLlib;

public class VarNode implements Node {

  public String id;  // name variable
  public Node type;  // type variable
  public Node exp;
  private int nestingLevel;
  public HashMap<String,OTentry> objectTable=new HashMap<String,OTentry>();



  public VarNode (String nome, Node tipo, Node espressione) {
    id=nome;
    type=tipo;
    exp=espressione;
  }

  @Override
  public ArrayList<SemanticError> checkSemantics(Environment env) {
  		//create result list
  		ArrayList<SemanticError> res = new ArrayList<SemanticError>();

  		HashMap<String,STentry> hm = env.symTable.get(env.nestingLevel);
  		STentry entry = new STentry(env.nestingLevel,type, env.offset--);
        nestingLevel=env.nestingLevel;
        if ( hm.put(id,entry) != null ){
          res.add(new SemanticError("Variable "+id+" already declared  " ));
        } else{
             //check if the type of variable is type class
              if (type instanceof ClassTypeNode){
                 if(((ClassTypeNode) type).id!=null){
                   ((ClassTypeNode) type).objcetTable=env.objectTable.get(((ClassTypeNode) type).id);
                     ((ClassTypeNode) type).listaobjectTable=env.objectTable;
                 }else{
                   res.add(new SemanticError("wrong declaration type class "+id));
                 }

              }
        }

        res.addAll(exp.checkSemantics(env));
        objectTable=env.objectTable;
        return res;
  }
  
  public String toPrint(String s) {
    if ((exp!=null) && (type!=null)){
      return s+"Var:" + id +type.toPrint(s+"  ") +" " +exp.toPrint(s+"  ") +"\n ";
    }else{
      if (exp!=null){
        return s+"Var:" + id + "typo: "+type.toPrint(s+"  ") +"\n ";
      }else{
        if(type!=null){
          return s+"Var:" + id + "exp: "+exp.toPrint(s+"  ") +"\n ";
        }else{
          return (s+"Var" + id + "\n");
        }
      }
    }
  }
  

  public Node typeCheck () {
    if ((exp.typeCheck() instanceof ClassTypeNode) && (type instanceof ClassTypeNode) ){

        ClassTypeNode classNode =(ClassTypeNode)exp.typeCheck(); //check if exp is subtype of variable
        if (FOOLlib.isSubtype(classNode,type)){
            return type;
        }else{
            System.out.println("incompatible value for variable "+id+" "+classNode.id +" "+((ClassTypeNode) type).id);
            return type;
        }

    }
    if (! (FOOLlib.isSubtype(exp.typeCheck(),type)) ){      //check if expression is subtype of variable
      System.out.println("incompatible value for variable "+id);
      System.exit(0);
    }     
    return type;
  }
  
  public String codeGeneration() {
		return exp.codeGeneration();
  }  
    
}  