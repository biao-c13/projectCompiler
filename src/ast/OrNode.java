package ast;


import lib.FOOLlib;
import org.antlr.v4.runtime.ParserRuleContext;
import ast.BoolTypeNode;
import util.Environment;
import util.SemanticError;

import java.util.ArrayList;

public class OrNode implements Node {
    private Node left;
    private Node right;
    private ParserRuleContext ctx;

    public OrNode(Node left, Node right)
    {
        this.left = left;
        this.right = right;
        this.ctx = ctx;
    }

    public String toPrint(String s) {
        return s + " \n Or" + left.toPrint(s + "  ") + right.toPrint(s + "  ");
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(Environment env) {
        //create the result
        ArrayList<SemanticError> res = new ArrayList<SemanticError>();

        //check semantics in the left and in the right exp

        res.addAll(left.checkSemantics(env));
        res.addAll(right.checkSemantics(env));

        return res;
    }

    public Node typeCheck() {
        Node l = left.typeCheck();
        Node r = right.typeCheck();
        if (!(FOOLlib.isSubtype(l, new BoolTypeNode()) && FOOLlib.isSubtype(r, new BoolTypeNode()))) {
            System.out.println("Incompatible types for '||' operand:\n" +
                    l.toPrint("    ") +
                    r.toPrint("    "));
            return new BoolTypeNode();
        }
        return new BoolTypeNode();
    }

    public String codeGeneration() {
        String l1 = FOOLlib.freshLabel();
        String l2 = FOOLlib.freshLabel();
        return
                "push 1\n"+
                left.codeGeneration() +
                right.codeGeneration() +
                "add \n"+
                "bleq " + l1 + "\n" +
                "push 0\n" +
                "b " + l2 + "\n" +
                l1 + ":\n" +
                "push 1\n" +
                l2 + ":\n";

    }
}