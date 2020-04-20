package ast;

import java.util.ArrayList;

import ast.BoolTypeNode;
import ast.Node;
import util.Environment;
import util.SemanticError;

public class NullNode implements Node {

    public NullNode () {
    }

    public String toPrint(String s) {
        return s+"null";
    }

    public Node typeCheck() {
        return new BoolTypeNode();
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(Environment env) {
        return new ArrayList<SemanticError>();
    }

    public String codeGeneration() {
        return null;
    }

}