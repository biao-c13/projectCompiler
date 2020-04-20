package ast;

import java.util.ArrayList;
import java.util.HashMap;

import sun.rmi.transport.ObjectTable;
import util.Environment;
import util.SemanticError;
import ast.STentry;
import lib.FOOLlib;



public class CallMethodExpNode implements Node {

    private String id;
    private String method;
    private STentry entry;
    private FTentry methodEntry;
    private OTentry objectEntry;
    private ArrayList<Node> parList;
    private int nestinglevel;
    private IdNode varNode;
    public HashMap<String,OTentry> objectTable=new HashMap<String,OTentry>();
    public HashMap<String, DispathTable> dispathTable= new  HashMap<String, DispathTable>();



    public CallMethodExpNode(String nomeObject, String met, ArrayList<Node> argomenti ) {
        id = nomeObject;
        parList = argomenti;
        method= met;
    }

    public String toPrint(String s) {
        String parlstr = "";

        for (Node par : parList)
            parlstr += par.toPrint(s + "  ");
            return s + "Call: " + id +" method: "+ method+ " paramentri: "+parlstr;
//        return s + "Call:" + id + " at nestlev " + nestLevel + "\n" + methodEntry.toPrint(s + "  ") + parlstr;
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(Environment env) {

        ArrayList<SemanticError> res = new ArrayList<SemanticError>();
        int level =env.nestingLevel;
        STentry tmp=null;
        while (level>=0 && tmp==null)
            tmp=(env.symTable.get(level--)).get(id);    //search id on variable table
        if (tmp==null)
            res.add(new SemanticError("object : "+id+" not declared"));
        else{
            nestinglevel=env.nestingLevel;
            objectTable=env.objectTable;
            dispathTable=env.dispathTable;
            entry = tmp;
            nestinglevel = env.nestingLevel;
            if(tmp.type instanceof ClassTypeNode && env.objectTable.get(((ClassTypeNode)entry.type).id)!=null ){
                ((ClassTypeNode)entry.type).listaobjectTable=env.objectTable;
                ((ClassTypeNode)entry.type).objcetTable=env.objectTable.get(((ClassTypeNode)entry.type).id);

            //    System.out.println("l----"+id+ "------------"+((ClassTypeNode)entry.type).id);

           //     System.out.println("09"+(((ClassTypeNode)entry.type).objcetTable.methods.get(method).parlist)  + method);



                if ((((ClassTypeNode)entry.type).objcetTable.methods)!=null && (((ClassTypeNode)entry.type).objcetTable.methods.get(method))!=null){
                    //methodEntry=((ClassTypeNode)entry.type).objcetTable.methods.get(method);
                   // System.out.println((((ClassTypeNode)entry.type).objcetTable.methods.get(method)).parlist+"3333");


                        methodEntry = ((ClassTypeNode) entry.type).objcetTable.methods.get(method);


                }else{

                    String nextName=((ClassTypeNode)entry.type).id;

                    while(nextName!=null){
                     //   res.add(new SemanticError(nextName+" not found---"));

                        if(env.objectTable.get(nextName).methods!=null  && env.objectTable.get(nextName).methods.get(method)!=null){
                            if( (methodEntry=env.objectTable.get(nextName).methods.get(method))!=null){
                                nextName=null;

                            }

                        }else{
                            if(env.objectTable.get(nextName).superClasse!=null){
                                nextName=env.objectTable.get(nextName).superClasse;


                            } else{
                                nextName=null;
                                res.add(new SemanticError("method "+ method +" of class  "+id+" not found"));
                            }

                        }

                    }


/*

          String nextName=null;
        if (((ClassTypeNode)a).objcetTable!=null && ((ClassTypeNode)a).objcetTable.superClasse !=null ){
          nextName=((ClassTypeNode)a).objcetTable.superClasse;
        }
         while (nextName!=null){
            if(nextName.equals(((ClassTypeNode)b).id)){
                return true;
            }else{
              if((((ClassTypeNode) a).listaobjectTable).get(nextName) !=null && (((ClassTypeNode) a).listaobjectTable).get(nextName).superClasse!=null){
                nextName=(((ClassTypeNode) a).listaobjectTable).get(nextName).superClasse;
              }else{
                nextName=null;
              }
            }

        }

*/

                }


            }else{
                System.out.println("lllllllll"+((ClassTypeNode)tmp.type).id +" ");
                res.add(new SemanticError("id : "+id+" is not object"));
            }

            for (Node arg: parList){
                res.addAll(arg.checkSemantics(env));

            }

        }



/*
        ArrayList<SemanticError> res = new ArrayList<SemanticError>();
        STentry methodTmp = null;
        if(env.objectTable.get(id)==null){
            res.add(new SemanticError("Class: "+id +" not declared"));
            System.out.println("Class: "+id +" not declared");

        }else{
            if(env.objectTable.get(id).methods.get(method)==null){
                if(env.objectTable.get(id).superClasse!=null){
                    String supClass=env.objectTable.get(id).superClasse;
                    if(env.objectTable.get(supClass).methods.get(method)==null){
                        res.add(new SemanticError("Class method: "+method +" not declared"));
                    }else{
                        methodEntry=env.objectTable.get(supClass).methods.get(method);
                        objectEntry=env.objectTable.get(supClass);
                    }
                }else{
                    res.add(new SemanticError("Class method: "+method +" not declared"));
                }
            }else{
                this.objectEntry=env.objectTable.get(id);
                this.methodEntry=env.objectTable.get(id).methods.get(method);
                this.nestLevel=env.nestingLevel;
            }
            for (Node arg: parList){
                res.addAll(arg.checkSemantics(env));

            }

        }
*/
        return res;
    }

    public Node typeCheck() {
        int numeroA=0;
        int numeroB=0;
        if(methodEntry!=null){
            numeroA=methodEntry.parlist.size();
        }
        if(parList!=null){
            numeroB=parList.size();
        }
        if(numeroA != numeroB){
            System.out.println("wrong number of parameters" +id+numeroA+numeroB);
        }else{
            for (int i=0; i<parList.size();i++){
                Node B=((parList.get(i)).typeCheck());
                Node A=(((ParNode)methodEntry.parlist.get(i)).type);
                if(!(FOOLlib.isSubtype(B,A))){
                    System.out.println("wrong type of parameter object call" + id +(parList.get(i)).typeCheck()+ " " +((ParNode)methodEntry.parlist.get(i)).type);

                }
              //  System.out.println("sdf"+ ((ClassTypeNode)((parList.get(i)).typeCheck())).id+ " " +((ParNode)methodEntry.parlist.get(i)).type);


            }


        }



//        System.out.println("++"+methodEntry.type);
   /*     int numeroA=0;
        int numeroB=0;
        if(objectEntry.parametriClass!=null){
            numeroA=objectEntry.parametriClass.size();
        }
        if(parList!=null){
            numeroB=parList.size();
        }
        if(numeroA != numeroB){
            System.out.println("wrong number of parameters" +id+numeroA+numeroB);
        }else{
            //System.out.println("wrong number of parameters" +id+numeroA+numeroB);
            for (int i=0; i<parList.size();i++){
                Node B=((parList.get(i)).typeCheck());
                Node A=(((ParNode)objectEntry.parametriClass.get(i)).type);
                if(!(FOOLlib.isSubtype(A,B))){
                    System.out.println("wrong type of parameter object call" + id +(parList.get(i)).typeCheck()+ " " +((ParNode)objectEntry.parametriClass.get(i)).type);
                   // System.out.println("-"+);
                    //System.out.println("ò"+((ParNode)objectEntry.parametriClass.get(i)).type);
                }else{
                    return null;
                }

            }
        }
*/
    return null;
    }

    public int findLevel(String id,String method){
        int i=1;
        int n=0;
        for (String key: objectTable.keySet()){
            i++;
            if((((ClassTypeNode)entry.type).id).equals(key)){
                n=i;
            }

        }
        return n;
    }
    public  int getOffset(String nome,String method){
        int ret=0;
        for (int i=0; i<dispathTable.get(nome).methods.size();i++){
         //   System.out.println("òòòòàààà" + method +" "+dispathTable.get(nome).methods.get(i).method.id);
            if (method.equals(dispathTable.get(nome).methods.get(i).method.id)){
                return i+2;
            }
        }
        return ret;

    }


    public Node findMethod(String nome,String method){
        Node ret=null;
        for (int i=0; i<dispathTable.get(nome).methods.size();i++){
            //   System.out.println("òòòòàààà" + method +" "+dispathTable.get(nome).methods.get(i).method.id);
            if (method.equals(dispathTable.get(nome).methods.get(i).method.id)){
                ret=dispathTable.get(nome).methods.get(i).method;
            }
        }
        return ret;

    }


    public String codeGeneration() {
        String getAR="";
        int levelClass=findLevel(id,method);

        for (int i=0; i< (+nestinglevel-entry.getNestinglevel());i++){
            getAR +="lw\n";
        }
        levelClass=0-levelClass;

        int methodOffset=getOffset((((ClassTypeNode)entry.type).id),method);


 //       System.out.println("òòòò"+levelClass +" "+nestinglevel+" "+entry.getNestinglevel()+ " "+getAR+" of"+methodOffset);
        String parCode = "";
        for (int i=0;i<parList.size();i++){
            parCode+=parList.get(i).codeGeneration();
        }
//--------

        FunNode methodNode=(FunNode) findMethod((((ClassTypeNode)entry.type).id),method);

   //     System.out.println(methodNode.codeGeneration());

//--------
/*        return   "lfp\n"+
                 parCode +
                "push " +((ClassTypeNode) entry.type).id +"\n"+


                  "push " + (-methodOffset) + "\n"+
                  "add \n"+
                "lw\n"+
                "js\n" ;

*/





        return
                parCode+
                methodNode.codeGeneration() +
                "lw\n" +
                "js    \n";


/*
    return
                "lfp\n"+
                parCode+
                "push " +levelClass +"\n"+
                "lfp \n"+ getAR +
                "add \n" +
                "lw\n" +
                "js \n"+


              "lw \n" +
                "push " +(-methodOffset)+"\n"+
                "add \n" +
                "lc \n" +
                "js \n";

*/







/*
        return "lfp\n"+
                parCode+

                "push " +levelClass +"\n"+
                "lfp \n"+ getAR +

                "add \n" +

                "lw\n" +
                "push " +(levelClass)+"\n"+
                "add \n"+

                "lw\n" +
                "push " +(-methodOffset)+"\n"+
                "add \n"+


                "lw \n" +
                "js \n";
*/
    }

}