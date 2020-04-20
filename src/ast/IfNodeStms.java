package ast;

import java.util.ArrayList;

import util.Environment;
import util.SemanticError;
import lib.FOOLlib;

public class IfNodeStms implements Node {

    private Node cond;
    private ArrayList<Node> statmentsThen;
    private ArrayList<Node> statmentsElse;

    public IfNodeStms (Node c, ArrayList<Node> t, ArrayList<Node> e) {
        cond=c;
        statmentsThen=t;
        statmentsElse=e;
    }

    public String toPrint(String s) {
        String thenlist="";
        String elselist="";
        for (Node stm:statmentsThen)
            thenlist += stm.toPrint(s+" ");
        for (Node stm:statmentsElse)
            elselist += stm.toPrint(s+" ");
        return s+"If\n" + cond.toPrint(s+"  ") +"\nthen:\n"+thenlist+"else:\n"+elselist;

    }

/*
  	String esplist="";
	String asslist="";
    for (Node dec:declist)
      declstr += dec.toPrint(s+" ");
    if (exp!=null) {
        return s + "ProgLetIn" + declstr +"exp: \n " + exp.toPrint(s + "  ");
    }else {
        return s + "ProgLetIn" + declstr +"stm: \n" + stm.toPrint(s + "  ");
    }
 /*/

    @Override
    public ArrayList<SemanticError> checkSemantics(Environment env) {
        //create the result
        ArrayList<SemanticError> res = new ArrayList<SemanticError>();

        //check semantics in the condition
        res.addAll(cond.checkSemantics(env));

        //check semantics in the then and in the else exp
        for (int i=0; i<statmentsElse.size();i++){
            res.addAll(statmentsElse.get(i).checkSemantics(env));
        }

        for (int i=0; i<statmentsThen.size();i++){
            res.addAll(statmentsThen.get(i).checkSemantics(env));
        }

        return res;

    }




    public Node typeCheck() {
        if (!(FOOLlib.isSubtype(cond.typeCheck(),new BoolTypeNode()))) {
            System.out.println("non boolean condition in if");
            System.exit(0);
        }
        for (int i=0; i<statmentsElse.size();i++){
            Node typetmp=statmentsThen.get(i).typeCheck();
            if (!(FOOLlib.isSubtype(typetmp,new VoidTypeNode()))){
                System.out.println("error type then");
            }
        }
        for (int i=0; i<statmentsThen.size();i++){
            Node typetmp=statmentsThen.get(i).typeCheck();
            if (!(FOOLlib.isSubtype(typetmp,new VoidTypeNode()))){
                System.out.println("error type else");
            }
        }
        return new NullNode();
    }
    public String codeGeneration() {
        String l1 = FOOLlib.freshLabel();
        String l2 = FOOLlib.freshLabel();

        String thenCode="";
        String elseCode="";
        for (int i=0; i<statmentsThen.size();i++){
            thenCode +=statmentsThen.get(i).codeGeneration();
        }
        for (int i=0; i<statmentsElse.size();i++){
            elseCode +=statmentsElse.get(i).codeGeneration();
        }
        return cond.codeGeneration()+
                "push 1\n"+
                "beq "+ l1 +"\n"+
                thenCode+
                "b " + l2 + "\n" +
                l1 + ":\n"+
                elseCode +
                l2 + ":\n";
    }

}