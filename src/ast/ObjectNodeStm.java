package ast;


import java.util.ArrayList;
import java.util.HashMap;

import util.Environment;
import util.SemanticError;
import lib.FOOLlib;

public class ObjectNodeStm implements Node {
    private  String objectName;
    private String methodName;
    private Node exp;
    private int NestingLevel;
    public ObjectNodeStm(String obj,String met, Node ex){
        objectName=obj;
        methodName=met;
        exp=ex;


    }

    @Override
    public ArrayList<SemanticError> checkSemantics(Environment env) {
        //create result list
        ArrayList<SemanticError> res = new ArrayList<SemanticError>();
/*
        //env.offset = -2;
        HashMap<String,STentry> hm = env.symTable.get(env.nestingLevel);
        STentry entry = new STentry(env.nestingLevel,type, env.offset--); //separo introducendo "entry"

        if ( hm.put(id,entry) != null )
            res.add(new SemanticError("Var id "+id+" already declared"));

        res.addAll(exp.checkSemantics(env));
*/
        return res;
    }

    public String toPrint(String s) {
        return s+ "object: " +objectName+" "
                +"method: " +methodName+" \n"
                +exp.toPrint(s+" ");

    }

    //valore di ritorno non utilizzato
    public Node typeCheck () {/*
        if (! (FOOLlib.isSubtype(exp.typeCheck(),type)) ){
            System.out.println("incompatible value for variable "+id);
            System.exit(0);
        }*/
        return null;
    }

    public String codeGeneration() {
        return exp.codeGeneration();
    }
}
