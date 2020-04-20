package ast;

import java.util.ArrayList;
import java.util.HashMap;

import sun.jvm.hotspot.debugger.cdbg.VoidType;
import util.Environment;
import util.SemanticError;
import lib.FOOLlib;

public class VarNodeStm implements Node {

    private String id;
    private Node type;
    private Node exp;
    private int nestinglevel;
    private STentry entry;

    public VarNodeStm(String nome, Node tipo, Node espressione) {
        id = nome;
        type = tipo;
        exp = espressione;
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(Environment env) {
        //create result list
        ArrayList<SemanticError> res = new ArrayList<SemanticError>();

        int level = env.nestingLevel;
        STentry tmp = null;
        while (level >= 0 && tmp == null)
            tmp = (env.symTable.get(level--)).get(id); //search variable on table of variable
        if (tmp == null) {
            res.add(new SemanticError("Id " + id + " not declared"));  //if not find error
        } else {
            type = tmp.getType();
            entry = tmp;
            nestinglevel = env.nestingLevel;
            if (tmp.type instanceof ClassTypeNode && env.objectTable.get(((ClassTypeNode) entry.type).id) != null) {
                ((ClassTypeNode) entry.type).listaobjectTable = env.objectTable;
                ((ClassTypeNode) entry.type).objcetTable = env.objectTable.get(((ClassTypeNode) entry.type).id);
                // System.out.println("l----------------"+((ClassTypeNode)entry.type).objcetTable.superClasse);
            }
        }
        //nestinglevel = env.nestingLevel;
        exp.checkSemantics(env);
        return res;
    }

    public String toPrint(String s) {
        if ((exp != null) && (type != null)) {
            return s + "Var:" + id
                    + type.toPrint(s + " ") + " "
                    + exp.toPrint(s + " ") + "\n";
        } else {
            if (exp == null) {
                return s + "Var:" + id +
                        type.toPrint(s + "  ") + "\n";

            } else {
                return s + "stm:" + id +
                        exp.toPrint(s + "  ") + "\n";
            }
        }
    }


    public Node typeCheck() {
        if (type == null) {
            System.out.println("type of " + id + " not found");
            return new VoidTypeNode();
        }
//--------
        if ((exp.typeCheck() instanceof ClassTypeNode) && (type instanceof ClassTypeNode)) {
            ClassTypeNode classNode = (ClassTypeNode) exp.typeCheck();
            if (FOOLlib.isSubtype(classNode, type)) {
                return null;
            } else {
                System.out.println("incompatible value for variable " + id + " " + classNode.id + " " + ((ClassTypeNode) type).id);
                return null;
            }
        }
        //------
        if (!(FOOLlib.isSubtype(exp.typeCheck(), type))) {
            System.out.println("incompatible value for variable " + id);
            return new NullNode();
            // System.exit(0);
        }
        return new VoidTypeNode();
        /*

            if ((exp.typeCheck() instanceof ClassTypeNode) && (type instanceof ClassTypeNode) ){
        ClassTypeNode classNode =(ClassTypeNode)exp.typeCheck();
        if (FOOLlib.isSubtype(classNode,type)){
            return null;
        }else{
            System.out.println("incompatible value for variable "+id+" "+classNode.id +" "+((ClassTypeNode) type).id);
            return null;
        }

    }
    if (! (FOOLlib.isSubtype(exp.typeCheck(),type)) ){      //check if expression is subtype of variable
      System.out.println(type.toPrint(""));
      System.out.println("incompatible value for variable "+id);
      System.exit(0);
     // if type
    }
    return null;

        */

    }

    public String codeGeneration() {
        String getAR = "";
        System.out.println(" ----"+ nestinglevel +" "+ entry.getNestinglevel());
        for (int i = 0; i < nestinglevel - entry.getNestinglevel(); i++) {
            getAR += "lw\n";
        }
        return "push " + entry.getOffset() + "\n" + //metto offset sullo stack
                "lfp\n" + getAR + //risalgo la catena statica
                "add\n" +
                "sw \n" + exp.codeGeneration();


       // return getAR;
    }
}