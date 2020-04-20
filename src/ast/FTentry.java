package ast;

import java.util.ArrayList;
import java.util.HashMap;

public class FTentry {

    public int nl;
    public Node type;
    public int offset;
    public int nestinglevel;
    public ArrayList<Node> parTypes;    //lista tipi parametri di funzione        FunNode ->  ParNode
    public ArrayList<Node> parlist = new ArrayList<Node>();   //lista parametri
    public ArrayList<Node> declist =new ArrayList<Node>();
   //----------
    public HashMap<String,STentry> symTable;

    public FTentry (int n, int os)
    {nl=n;
        offset=os;}

    public FTentry (int n, Node t, int os)
    {nl=n;
        type=t;
        offset=os;}

    public void addTypeParametri (ArrayList<Node> t)
    {parTypes=t;}

    public ArrayList<Node> getTypeParametri(){
        return parTypes;
    }

    public Node getType ()
    {return type;}

    public void addType(ArrayList<Node> tps){
        parTypes=tps;
    }
    public int getOffset ()
    {return offset;}

    public int getNestinglevel ()
    {return nl;}

    public String toPrint(String s) { //
        return s+"FTentry: nestlev " + Integer.toString(nl) +"\n"+
                s+"FTentry: type\n" +
                type.toPrint(s+"  ") +
                s+"FTentry: offset " + Integer.toString(offset) + "\n";
    }
}