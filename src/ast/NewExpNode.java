package ast;

import java.util.ArrayList;
import java.util.HashMap;

import util.Environment;
import util.SemanticError;
import ast.STentry;
import lib.FOOLlib;



public class NewExpNode implements Node {

    private String id;
    private FTentry methodEntry;
    private ArrayList<Node> parList;
    private int nestLevel;
    private IdNode varNode;
    private ClassTypeNode type;


    public NewExpNode(String nomeObject, ArrayList<Node> arg) {
        id = nomeObject;
        parList = arg;
        type=new ClassTypeNode();
    }

    public String toPrint(String s) {
        String parlstr = "";

        for (Node par : parList)
            parlstr += par.toPrint(s + "  ");
        return s + "object nome: " + id + " paramentri: "+parlstr;
//        return s + "Call:" + id + " at nestlev " + nestLevel + "\n" + methodEntry.toPrint(s + "  ") + parlstr;
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(Environment env) {
        ArrayList<SemanticError> res = new ArrayList<SemanticError>();

        if (env.objectTable.get(id)==null){
            res.add(new SemanticError("class: " + id+" errort  class  not find " + id));
            return res;
        }else{
            type.id=id;
            type.objcetTable=env.objectTable.get(id);
            type.listaobjectTable=env.objectTable;
         //   System.out.println("sdadadsadasdsa"+type.objcetTable.parametriClass.size());
        }

/*        if(env.objectTable.get(id).methods!=null && env.objectTable.get(id).methods.get(id)!=null){
            methodEntry=env.objectTable.get(id).methods.get(id);
          //  System.out.println("dsfalllllllllldsf"+env.objectTable.get(id).methods.get(id).parlist +" "+ id);
        }else{

            res.add(new SemanticError("class: " + id+" class parameter wrong " + id));
        }*/
        for (int i=0; i<parList.size();i++){
            res.addAll(parList.get(i).checkSemantics(env));
        }
        return res;
    }

    public Node typeCheck() {
        int numeroa=0;
        int numerob=0;
        if(parList != null){
            numeroa=parList.size();
        }
        //if (methodEntry!=null && methodEntry.parlist!=null){
        if (type.objcetTable !=null && type.objcetTable.parametriClass!=null){
            numerob=type.objcetTable.parametriClass.size();
        //    System.out.println("dsfadsfèèèèèèèèèèèèèèèèèèèèèèèèèèèè"+type.objcetTable.parametriClass.get(0));

        }
        if(numeroa != numerob){
            System.out.println("error instanziazione new object wrong parameter " + id);
        }else{
            for (int i=0; i<numerob;i++){
                Node a=(parList.get(i).typeCheck());  //funzione: b e` sottotipo del tipo di una funzione a
                Node b=((ParNode)type.objcetTable.parametriClass.get(i)).type; //se ogni tipo di parametro di a e` sopratipo del corrisponde tipo di parametro di b
                if(!(FOOLlib.isSubtype(a,b))){
                    System.out.println("errore  instanziazione new object " + id+" control type"+ a.toPrint("")+b.toPrint(""));
                }

            }

        }



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
        }*/
        if(type!=null){
            return type;
        }else{
            return new NullNode();
        }
    }

    public String codeGeneration() {
        String l1 = FOOLlib.freshLabel();
        String code = "";
         code+= "push "+id+"\n";   //deve essere memorizzato in heap



    /*    for (int i = parList.size() - 1; i >= 0; i--)
            parCode += parList.get(i).codeGeneration();

        String getAR = "";
        //meno uno percheè le classi non creano un nuovo record di attivazione
        for (int i = 0; i < nestLevel - methodEntry.getNestLevel(); i++)
            getAR += "lw\n";

        String thisRef = varNode.codeGeneration();

        return "lfp\n" + //CL
                parCode + thisRef + "lfp\n" + getAR + //setto AL risalendo la catena statica
                // ora recupero l'indirizzo a cui saltare e lo metto sullo stack
                "push " + methodEntry.getOffset() + "\n" + //metto offset sullo stack
                "lfp\n" + getAR + //risalgo la catena statica
                "add\n" + "lw\n" + //carico sullo stack il valore all'indirizzo ottenuto
                "js\n";*/
        return code;
    }

}