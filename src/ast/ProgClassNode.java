package ast;

import java.util.ArrayList;
import java.util.HashMap;

import util.Environment;
import util.SemanticError;
import ast.STentry;
import lib.FOOLlib;



public class ProgClassNode implements Node {

    private ArrayList<Node> listaclasse;
    private ProgLetInNode letNode;
    private Node exp;
    private Node stm;


    public ProgClassNode(ArrayList<Node> object, ProgLetInNode letN, Node e, Node s) {
        listaclasse = object;
        letNode = letN;
        exp=e;
        stm=s;
    }

    public String toPrint(String s) {
        String parlstr = "class declaration: ";

        for (Node par : listaclasse)
            parlstr += par.toPrint(s + "  ");
        if(letNode==null && exp==null && stm==null){
            return s+  parlstr;
        }else{
            if (letNode!=null){
                parlstr=parlstr+ "ProgLetIn:";
                parlstr=parlstr+letNode.toPrint( s+" ");
            }else{
                if (exp!=null){
                    parlstr=parlstr+exp.toPrint(s+" ");
                }
                if (stm!=null){
                    parlstr=parlstr+stm.toPrint(s+" ");
                }
            }
        }
        return parlstr;
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(Environment env) {
        ArrayList<SemanticError> res = new ArrayList<SemanticError>();
        for (Node c : listaclasse){
            res.addAll(c.checkSemantics(env));    //check each class declared
        }
        if (letNode!=null){
            res.addAll(letNode.checkSemantics(env));
        }else{
            if(exp != null){
               // env.offset = -2;
                res.addAll(exp.checkSemantics(env));
            }
            if (stm!=null){
               // env.offset = -2;
                res.addAll(stm.checkSemantics(env));
            }
        }
        return res;
    }

    public Node typeCheck() {
        for (Node c:listaclasse){
            c.typeCheck();
        }
        if (letNode!=null){
            letNode.typeCheck();
        }else{
            if (exp!=null){
                exp.typeCheck();
            }else{
                if(stm!=null){
                    stm.typeCheck();
                }
            }
        }
        return null;
    }

    public String codeGeneration() {
        String classCode = "";
        for (Node c:listaclasse){
            c.codeGeneration();
        }
        String bodyCode="";
        if(letNode!=null){
            bodyCode+=letNode.codeGeneration();
        }else{
            if(exp!=null){
                bodyCode+=exp.codeGeneration();
            }else{
                if(stm!=null){
                    bodyCode+=stm.codeGeneration();
                }
            }
        }
        return classCode + bodyCode+ "halt\n"+ FOOLlib.getCode();
    }

}