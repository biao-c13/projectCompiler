package ast;

import util.Environment;
import util.SemanticError;

import java.util.ArrayList;
import java.util.HashMap;

public class ClassTypeNode implements Node {
    public String id;
    public OTentry objcetTable=new OTentry();
    public HashMap<String,OTentry> listaobjectTable=new HashMap<String,OTentry>();

    public ClassTypeNode(String nome) {
        id=nome;
        objcetTable=null;
        listaobjectTable=null;
    }
    public ClassTypeNode() {
    }

    @Override
    public String toPrint(String s) {
        return s + "ClassType "+ " " +id+"\n";
    }
    //non utilizzato
    public Node typeCheck() {
        return null;
    }

    //non utilizzato
    public String codeGeneration() {
        return "";
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(Environment env) {

        return new ArrayList<SemanticError>();
    }

}
