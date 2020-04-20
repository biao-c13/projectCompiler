package ast;

import java.util.ArrayList;
import java.util.HashMap;

public class DispathTable {



    public int offset=0;
    public int nestinglevel=0;
    public String nomeClasse;
    public ArrayList<MeT> methods=new ArrayList<MeT>();
    public ArrayList<CamP> campi=new ArrayList<CamP>();

    public DispathTable () {

    }

}