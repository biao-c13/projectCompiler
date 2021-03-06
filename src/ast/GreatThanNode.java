package ast;

import java.util.ArrayList;

import util.Environment;
import util.SemanticError;
import lib.FOOLlib;



public class GreatThanNode implements Node{

    private Node left;
    private Node right;

    public GreatThanNode(Node l, Node r) {
        left = l;
        right = r;
    }

    public String toPrint(String s) {
        return s + "Greater Than\n" + left.toPrint(s + "  ") + right.toPrint(s + "  ");
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
        if (!(FOOLlib.isSubtype(l, r) || FOOLlib.isSubtype(r, l))) {
            System.out.println("Incompatible types for '>=' operand:\n" +
                    l.toPrint("    ") +
                    r.toPrint("    "));
            return new BoolTypeNode();
        }
        return new BoolTypeNode();
    }

    public String codeGeneration() {
        String l1 = FOOLlib.freshLabel();
        String l2 = FOOLlib.freshLabel();
        String l3= FOOLlib.freshLabel();
        return left.codeGeneration() +
                right.codeGeneration() +
                "bleq " + l1 + "\n" +
                "push 1\n" +
                "b " + l2 + "\n" +
                l1 + ":\n" +
                left.codeGeneration() +
                right.codeGeneration() +
                "beq " + l3 +"\n"+
                "push 0\n" +
                "b " +l2 +"\n"+
                l3 +":\n" +
                "push 1\n"+
                l2 + ":\n";

    }

}