import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

import ast.OTentry;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

import parser.*;
import parser.ExecuteVM;
import parser.FOOLLexer;
import parser.FOOLParser;
import parser.SVMLexer;
import parser.SVMParser;
import util.Environment;
import util.SemanticError;
import ast.FoolVisitorImpl;
import ast.Node;
import ast.SVMVisitorImpl;

import lib.FOOLlib;

public class Main {
    public static void main(String[] args) throws Exception {
      
        String fileName = "prova.fool";
      	CharStream input=CharStreams.fromFileName(fileName);
        FOOLLexer lexer = new FOOLLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
        if(lexer.lexicalErrors > 0){
        	System.out.println("The program was not in the right format. Exiting the compilation process now");
        }else{
		//star TYPE-CHECKER
		FOOLParser parser=new FOOLParser(tokens);
            	FoolVisitorImpl visitor=new FoolVisitorImpl();
            	Node ast=visitor.visit(parser.prog());
            	System.out.println(ast.toPrint(""));   
/*	check the correct use of types
    catch errors: multiple declaration, undeclared variable, wrong arguments etc。。。
 */

           	 System.out.println("    \n\n       semantic analysis: \n\n");
           	 Environment env=new Environment();
           	 ArrayList<SemanticError> err = ast.checkSemantics(env);

           	 System.out.println("\n                parser  \n");
           	 System.out.println(err);
		Node type=ast.typeCheck();

           
	//start generation of machine/object code (the code shoul be execute for machine SVM)
            String code=ast.codeGeneration();
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName+".asm"));
            out.write(code);
            out.close();
            System.out.println("Code generated! Assembling and running generated code.");
            FileInputStream isASM = new FileInputStream(fileName+".asm");
            ANTLRInputStream inputASM = new ANTLRInputStream(isASM);
            SVMLexer lexerASM = new SVMLexer(inputASM);
            CommonTokenStream tokensASM = new CommonTokenStream(lexerASM);
            SVMParser parserASM = new SVMParser(tokensASM);

            SVMVisitorImpl visitorSVM = new SVMVisitorImpl();
            visitorSVM.visit(parserASM.assembly());
            System.out.println("You had: "+lexerASM.lexicalErrors+" lexical errors and "+parserASM.getNumberOfSyntaxErrors()+" syntax errors.");
            if (lexerASM.lexicalErrors>0 || parserASM.getNumberOfSyntaxErrors()>0) System.exit(1);

            System.out.println("Starting Virtual Machine...");
            ExecuteVM vm = new ExecuteVM(visitorSVM.code);
            vm.cpu();

        }
       
        
    }
}
