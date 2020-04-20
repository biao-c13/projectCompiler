package util;

import java.util.ArrayList;
import java.util.HashMap;

import ast.*;

public class Environment {

	
	public ArrayList<HashMap<String,STentry>>  symTable = new ArrayList<HashMap<String,STentry>>();    //table of variables
	public ArrayList<HashMap<String,FTentry>> functionTable=new ArrayList<HashMap<String,FTentry>> (); //table of functions
	public HashMap<String,OTentry> objectTable=new HashMap<String,OTentry>();				//table of classes
	public int nestingLevel = -1;
	public int offset = 0;
	public int functionNestingLevel=-1;


	public HashMap<String, DispathTable> dispathTable= new  HashMap<String, DispathTable>();
	public int dispathTableOffest=0;
	public int dispathTablelevel=0;


	//livello ambiente con dichiarazioni piu' esterno � 0 (prima posizione ArrayList) invece che 1 (slides)
	//il "fronte" della lista di tabelle � symTable.get(nestingLevel)
	public Environment(){
	}
	public ArrayList<HashMap<String,STentry>> getSymTable(){
		return symTable;
	}
	public HashMap<String,OTentry> getObjectTable(){
		return objectTable;
	}
	public ArrayList<HashMap<String,FTentry>>  getFunctionTable(){
		return functionTable;
	}
	public int getNestLevel(){
		return nestingLevel;
	}
	public int decNesteLevel(){
		return nestingLevel--;
	}
	public int incNesteLevel(){
		return nestingLevel++;
	}

	public void print() {
		for (int i=0; i<symTable.size();i++){   //print variables
			System.out.println("variable nestingLevel: "+i);
			for (String key:symTable.get(i).keySet() ){
				System.out.println("var: "+key + "  " + symTable.get(i).get(key).type.toPrint(""));
			}
		}
		for (int i=0; i<functionTable.size();i++){ //print functions and for each function print parameters
			System.out.println("function nestingLevel: "+i);
			System.out.println(functionTable.get(i));

		}
		for (String key: objectTable.keySet()){
			System.out.println("Class: "+ key);
			for (String k: objectTable.get(key).campi.keySet()){
			//	System.out.println("campo: "+ k + "   type: "+ objectTable.get(key).campi.get(k));
			}
			for (String k :objectTable.get(key).methods.keySet()){
				System.out.println("method: " +k +"   type: "+objectTable.get(key).methods.get(k).type);
				for (int i=0; i<((objectTable.get(key).methods.get(k)).parlist.size()); i++){
					System.out.println("   parametri: "+((ParNode)(objectTable.get(key).methods.get(k)).parlist.get(i)).id);
				}
			}

		}

	}
	public void DipathPrint(){
		for (String key: dispathTable.keySet()){
			System.out.println("classe " +key);
			for (int i=0;i<dispathTable.get(key).methods.size();i++){
				System.out.println("  method "+ dispathTable.get(key).methods.get(i).method.id + " " +dispathTable.get(key).methods.get(i).offset );
			}
			for (int i=0; i<dispathTable.get(key).campi.size();i++){
				System.out.println(" campo " +dispathTable.get(key).campi.get(i).variable.id +" "+dispathTable.get(key).campi.get(i).offset);
			}

		}

	}
}
