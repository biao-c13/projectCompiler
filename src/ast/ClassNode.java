package ast;

import java.util.ArrayList;
import java.util.HashMap;

import lib.FOOLlib;
import util.Environment;
import util.SemanticError;

public class ClassNode implements Node {

    private String classNome;
    private String superClassNome;
    private ArrayList<Node> parlist;
    private ArrayList<Node> methodslista;
    private ArrayList<Node> campilista;

    public HashMap<String, DispathTable> dispathTable= new  HashMap<String, DispathTable>();


    public ClassNode (String n,String sn,ArrayList<Node> p, ArrayList<Node> m ,ArrayList<Node> c ) {
        classNome=n;
        superClassNome=sn;
        parlist=p;
        methodslista=m;
        campilista=c;

    }
    String decpar="";
    public String toPrint(String s) {
        for (Node dec:parlist)
            decpar += dec.toPrint(s+" ");
        for (Node c:campilista){
            decpar+="\n";
            decpar += c.toPrint(s+ "  ");
        }
        for (Node f:methodslista){
            decpar+="\n";
            decpar += f.toPrint(s+ "  ");
        }
        return "\nclass: "+classNome +" " +decpar;
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(Environment env) {
        ArrayList<SemanticError> res = new ArrayList<SemanticError>();
        if (env.objectTable.get(classNome)!=null){  //if class already present on the table print send error
            res.add(new SemanticError(" Multiple declaration Class:'" + classNome + "' ") );
        }else{
            int offset=1;
            //check superclass
            OTentry ot= new OTentry(env.nestingLevel,env.offset);
            if (superClassNome !=null){ //check if class has superclass
                if(env.objectTable.get(superClassNome)==null){ //check if superclass was declared
                    res.add(new SemanticError("superClass: "+ superClassNome+ " not declared") );
                }else{
                    ot.superClasse=superClassNome;



                    //------------------  dispatch table
                    String tmpClasse=superClassNome;
                    ArrayList<Node> array= new ArrayList<Node>();
                    ArrayList<Node> arrayc=new ArrayList<Node>();
                    for (int i=0;i<methodslista.size();i++){
                        array.add(methodslista.get(i));
                    }
                    for (int i=0;i<campilista.size();i++){
                        arrayc.add(campilista.get(i));
                    }
                    while(tmpClasse!=null){
                       if(env.objectTable.get(tmpClasse)!=null) {
                           for (int i=0;i<env.objectTable.get(tmpClasse).methodsNode.size();i++){
                               array.add(env.objectTable.get(tmpClasse).methodsNode.get(i));
                           }
                           for (int i=0;i<env.objectTable.get(tmpClasse).campilista.size();i++){
                               arrayc.add(env.objectTable.get(tmpClasse).campilista.get(i));
                           }
                           if(env.objectTable.get(tmpClasse).superClasse!=null){
                               tmpClasse=env.objectTable.get(tmpClasse).superClasse;

                           }else{
                               tmpClasse=null;
                           }

                       }else{
                           tmpClasse=null;
                       }
                    }
                    //------------------------------------------------------
                        DispathTable d=new DispathTable();
                        env.dispathTable.put(classNome,d);
                        env.dispathTableOffest--;

                        for (int j=array.size()-1;j>=0;j--) {
                            MeT m = new MeT();
                            m.method = (FunNode) array.get(j);
                            m.offset = d.offset;
                            d.offset--;
                            m.level = d.nestinglevel;

                            boolean find=false;
                            for (int k=0; k<env.dispathTable.get(classNome).methods.size() && (find ==false);k++){
                              //  System.out.println("sono entrato ....."+env.dispathTable.get(classNome).methods.get(k).method.id +" "+m.method.id);
                                if((m.method.id).equals(env.dispathTable.get(classNome).methods.get(k).method.id)){
                                    find=true;
                                    m.offset=env.dispathTable.get(classNome).methods.get(k).offset;
                                    m.belong=classNome;
                                    env.dispathTable.get(classNome).methods.set(k,m);
                                    d.offset++;
                                }
                            }
                            if(find ==false){
                                env.dispathTable.get(classNome).methods.add(m);
                                m.belong=classNome;
                            }

                        }
                       //---------------
                   for (int j=arrayc.size()-1;j>=0;j--) {
                        //              System.out.println ("-------------");
                        CamP m = new CamP();
                        m.variable = (VarNode)arrayc.get(j);
                        m.offset = d.offset;
                        d.offset--;
                        m.level = d.nestinglevel;

                        boolean find=false;
                        for (int k=0; k<env.dispathTable.get(classNome).campi.size() && (find ==false);k++){
                            if((m.variable.id).equals(env.dispathTable.get(classNome).campi.get(k).variable.id)){
                                find=true;
                                m.offset=env.dispathTable.get(classNome).campi.get(k).offset;
                                env.dispathTable.get(classNome).campi.set(k,m);

                            }
                        }
                        if(find ==false)
                            env.dispathTable.get(classNome).campi.add(m);

                    }


                }
            }else{

                DispathTable d=new DispathTable();
                env.dispathTable.put(classNome,d);
                env.dispathTableOffest--;
                for (int i=methodslista.size()-1;i>=0;i--){
                    MeT m= new MeT();
                    m.method=(FunNode) methodslista.get(i);
                    m.offset=d.offset;
                    d.offset--;
                    m.level=d.nestinglevel;
                    m.belong=classNome;
                    env.dispathTable.get(classNome).methods.add(m);
                }

                for (int i=campilista.size()-1;i>=0;i--){
                    CamP m= new CamP();
                    m.variable=(VarNode) campilista.get(i);
                    m.offset=d.offset;
                    d.offset--;
                    m.level=d.nestinglevel;
                    m.belong=classNome;
                    env.dispathTable.get(classNome).campi.add(m);
                }

                //--------------------------------------------------------------------end of dispatch table
            }


//---------------------------------
            //add field of class in variable table
            env.nestingLevel++;
            HashMap<String,STentry> tm = new HashMap<String,STentry> ();
            env.symTable.add(tm);
            //add parameters of class
            if (parlist!=null){
                ot.parametriClass=parlist;
                for (Node c:parlist){
                    ParNode campo =(ParNode) c;
                    if (ot.campi.put(campo.id,campo.type)!=null){
                        res.add(new SemanticError("campo name '" + campo.id+ "' has already been declared.") );
                    }else{
                        STentry CampoEntry=new STentry(env.nestingLevel,campo.type,offset--);
                        if ( tm.put(campo.id,CampoEntry) != null  )  //not happen
                            res.add(new SemanticError("campo name '" + campo.id+ "' has already been declared."));
                    }
                }

            }
            //add field of class
            if(campilista!=null){
                ot.campilista=campilista;
            }
            for (Node c:campilista){
                VarNode campo =(VarNode) c;
                campo.exp.checkSemantics(env);
                if (ot.campi.put(campo.id,campo.type)!=null){
                    res.add(new SemanticError("campo name '" + campo.id+ "' has already been declared.") );
                }else{
                    ot.campiField.put(campo.id,campo.type);
                    STentry CampoEntry=new STentry(env.nestingLevel,campo.type,offset--);
                    if ( tm.put(campo.id,CampoEntry) != null  )  //not happen
                        res.add(new SemanticError("campo name '" + campo.id+ "' has already been declared."));
                }
            }


            //insert new class on class table
            ot.methodsNode=methodslista;
            env.objectTable.put(classNome,ot);
            dispathTable=env.dispathTable;

            //add method of class in function table
            env.functionNestingLevel++;
            HashMap<String,FTentry> fm= new HashMap<String,FTentry>();
            env.functionTable.add(fm);
            for (Node m:methodslista){
                FTentry ft=new FTentry(env.nestingLevel,offset--);  //
                FunNode method=(FunNode)m;
                res.addAll(m.checkSemantics(env));
                ft.parlist=((FunNode) m).parlist;
                ft.declist=((FunNode) m).declist;
                ft.type=((FunNode) m).type;
                if (ot.methods.put(((FunNode) m).id,ft)!=null){
                    res.add(new SemanticError("method name '" + ((FunNode) m).id+ "' has already been declared.") );
                }
            }
            //insert new class on class table
            //ho cambiato

            //leave from scope of functions declared inside class
            env.functionTable.remove(env.functionNestingLevel);
            env.functionNestingLevel--;

            //leave from scope of fields declared inside class
            env.symTable.remove(env.nestingLevel);
            env.nestingLevel--;

        }

        return res;
    }

    public Node typeCheck() {
        for (Node method :methodslista){
            method.typeCheck();
        }
        for (Node campo:campilista){
            campo.typeCheck();
        }
        return null;
    }


    public String codeGeneration() {

        String popParl="";
        if(parlist!=null) for(Node dec:parlist)
            popParl+="pop \n";

        //----
        String codeClass="";
        for (int i=0;i<dispathTable.get(classNome).methods.size();i++){
            codeClass+=dispathTable.get(classNome).methods.get(i).method.codeGeneration();
        }
        String popClass="";
        for (int i=0;i<dispathTable.get(classNome).methods.size();i++){
            popClass+="pop \n";
        }

        //---

  /*
        String codeClass="";
        for(Node m:methodslista){
            codeClass +=m.codeGeneration();
        }


        String popClass="";
        for(Node m:methodslista){
            popClass+="pop \n";
        }
*/
        String codeCampi="";
        for (Node c:campilista){
            codeCampi+=c.codeGeneration();
        }
        String codeCampiPop="";
        for (Node c:campilista){
            codeCampiPop+="pop \n";
        }


        String cl=classNome;
        FOOLlib.putCode(
          cl+ ":\n" +
                "cfp \n"+               // copy $sp to $fp
                "lra\n"+                //insert return address on the stack
                codeClass+
                codeCampi+
                 "srv \n"+              //store return value from stack into RV
                codeCampiPop+
                popClass+
                "sra \n"+               //store top in return address
                "pop \n"+               //pop al
                popParl+
                "sfp \n"+               //store top into frame pointer
                "lrv \n"+               //load from return value
                "lra \n" +
                "js \n"
        );
        return "push" +cl +"\n";


    }
    public void addPar (Node p) {
        parlist.add(p);
    }
}