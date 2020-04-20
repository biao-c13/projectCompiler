package ast;


import util.Environment;
import util.SemanticError;

import java.util.ArrayList;

public class VoidNode implements Node {
    @Override
    public String toString()
    {
        return "void";
    }

    @Override
    public Node typeCheck()
    {
        return new VoidTypeNode();
    }

    @Override
    public String codeGeneration()
    {
        //TODO: shouldn't we consider a non-empty machine code value for void?
        return "";
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(Environment env)
    {
        return new ArrayList<SemanticError>();
    }
    @Override
    public String toPrint(String s) {
        return null;
    }
}
