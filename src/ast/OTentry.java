package ast;

import java.util.ArrayList;
import java.util.HashMap;

public class OTentry {
    private int nl;
    private int offset;
    public ClassNode classNode;
    public String superClasse=null;
    public Node superClasseNode;
    public ArrayList<Node>  parametriClass=new ArrayList<Node> ();
    public HashMap<String,Node>  campi=new HashMap<String,Node>();  //node tipo
    public HashMap<String,FTentry> methods= new HashMap<String,FTentry>();

    public ArrayList<Node> methodsNode=new  ArrayList<Node>();
    public int campiNumero=0;
    public HashMap<String,Node>  campiField=new HashMap<String,Node>();  //contiene solo campo, no i parametri
    public ArrayList<Node> campilista=new  ArrayList<Node>();

    public OTentry (int n, int os)
    {   nl=n;
        offset=os;
    }
    public OTentry(){

    }
}
