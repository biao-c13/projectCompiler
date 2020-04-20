package ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import parser.*;
import parser.FOOLParser.BaseExpContext;
import parser.FOOLParser.BoolValContext;
import parser.FOOLParser.DecContext;
import parser.FOOLParser.ExpContext;
import parser.FOOLParser.FactorContext;
import parser.FOOLParser.FunContext;
import parser.FOOLParser.FunExpContext;
import parser.FOOLParser.IfExpContext;
import parser.FOOLParser.IntValContext;
import parser.FOOLParser.LetInExpContext;
import parser.FOOLParser.SingleExpContext;
import parser.FOOLParser.TermContext;
import parser.FOOLParser.TypeContext;
import parser.FOOLParser.VarExpContext;
import parser.FOOLParser.VarasmContext;
import parser.FOOLParser.VardecContext;
import parser.FOOLParser.ClassDichiarazioneContext;
import sun.jvm.hotspot.opto.TypeNode;
import util.SemanticError;

public class FoolVisitorImpl extends FOOLBaseVisitor<Node> {
	
	
	@Override
	public Node visitLetInExp(LetInExpContext ctx) {
		
		//resulting node of the right type
		ProgLetInNode res;
		
		//list of declarations
		ArrayList<Node> declarations = new ArrayList<Node>();
		Node exp= null;
		Node asg= null;


		for(DecContext dc : ctx.let().dec()){
			declarations.add( visit(dc) );
		}
        if (ctx.exp()!=null) {
			//visit exp context
			exp=visit(ctx.exp());
	/*		for (ExpContext exp: ctx.exp()) {
				espressioni.add(visit(exp));
			}*/
		}
		if( ctx.stm()!=null) {
			asg=visit(ctx.stm());

		}

		res = new ProgLetInNode(declarations,exp,asg);
		return res;
	}
	
	@Override
	public Node visitSingleExp(SingleExpContext ctx) {
		
		//return the result of the visit to the inner exp
		return new ProgSingleNode(visit(ctx.exp()));
		
	}
	@Override
	public Node visitExp(ExpContext ctx) {
		//check whether this is a simple or binary expression
		if(ctx.right == null){
			//it is a simple expression
			return visit( ctx.left );
		}else{
			//it is a binary expression, visit left and right
			if(ctx.PLUS() != null) {
				return new PlusNode(visit(ctx.left), visit(ctx.right));
			}else return new MinusNode(visit(ctx.left), visit(ctx.right));
		}

	}





	@Override
	public Node visitTerm(TermContext ctx) {
		//check whether this is a simple or binary expression
		if(ctx.right == null){
			//it is a simple expression
			return visit( ctx.left );
		}else{
			//it is a binary expression,  visit left and right
			if(ctx.TIMES() != null)
				return new MultNode(visit(ctx.left), visit(ctx.right));
			else return new DivNode(visit(ctx.left), visit(ctx.right));
		}
	}

//	factor : ('not')? left=value ((EQ|SMALLTHAN|GREATTHAN|OR|AND) right=value)?
	@Override
	public Node visitFactor(FactorContext ctx) {
		//check whether this is a simple or binary expression
		if(ctx.right == null){
			//it is a simple expression
			return visit( ctx.left );
		}else{
			if (ctx.EQ()!=null) {
				return new EqualNode(visit(ctx.left), visit(ctx.right));
			} else if (ctx.GREATTHAN()!=null) {
				return new GreatThanNode(visit(ctx.left), visit(ctx.right));
			} else if (ctx.SMALLTHAN()!=null){
				return new SmallThanNode(visit(ctx.left), visit(ctx.right));
			}else if (ctx.OR()!=null){
				return new OrNode(visit(ctx.left), visit(ctx.right));
			}
			return new AndNode(visit(ctx.left), visit(ctx.right));
		}

	}


	@Override
	public Node visitIntVal(IntValContext ctx) {
		//there is no need to perform a check here, the lexer ensures this text is an int
		return new IntNode(Integer.parseInt(ctx.INTEGER().getText()));
	}

	@Override
	public Node visitBoolVal(BoolValContext ctx) {
		//there is no need to perform a check here, the lexer ensures this text is a boolean
		return new BoolNode(Boolean.parseBoolean(ctx.getText()));
	}



	@Override
	public Node visitBaseExp(BaseExpContext ctx) {

		return visit (ctx.exp());

	}

	@Override
	public Node visitIfExp(IfExpContext ctx) {

		//create the resulting node
		IfNode res;


		Node condExp = visit (ctx.cond);
		Node thenExp = visit (ctx.thenBranch);
		Node elseExp = visit (ctx.elseBranch);
		res = new IfNode(condExp, thenExp, elseExp);

		return res;
	}
//IF exp THEN CLPAR stms CRPAR ELSE CLPAR stms CRPAR  #codizione
	@Override
	public Node visitCodizione(FOOLParser.CodizioneContext ctx){
		IfNodeStms res;
		ArrayList<Node> statmentsThen= new ArrayList<>();
		ArrayList<Node> statmentsElse= new ArrayList<>();
		Node CondExp=visit((ctx.exp()));
		for ( FOOLParser.StmContext stmc : ctx.thenBranch.stm()){
			Node stm=visit(stmc);
			statmentsThen.add(stm);
		}
		for ( FOOLParser.StmContext stmc : ctx.elseBranch.stm()){
			Node stm=visit(stmc);
			statmentsElse.add(stm);
		}
		res =new IfNodeStms(CondExp,statmentsThen,statmentsElse);
		return res;
	}


	@Override
	public Node visitVarExp(VarExpContext ctx) {


		return new IdNode(ctx.ID().getText());

	}
	@Override
	public Node visitFunExp(FunExpContext ctx) {
		//declare the result
		Node res;

		//get the invocation arguments
		ArrayList<Node> args = new ArrayList<Node>();

		for(ExpContext exp : ctx.exp())
			args.add(visit(exp));

		if(ctx.ID().getText().equals("print"))
			res = new PrintNode(args.get(0));

		else {
			//instantiate the invocation
			res = new CallFunNode(ctx.ID().getText(), args); //look after

		}
		return res;
	}

	@Override
	public Node visitType(TypeContext ctx) {
		if(ctx.getText().equals("int"))
			return new IntTypeNode();
		else if(ctx.getText().equals("bool"))
			return new BoolTypeNode();
		else if (ctx.getText().equals("void"))
			return new VoidTypeNode();
		else if (ctx.getText().equals("null"))
			return new VoidTypeNode();
		return new ClassTypeNode(ctx.getText());

	}
	@Override
	public Node visitVarasm(VarasmContext ctx){

		Node type=  visit(ctx.vardec().type());
		Node exp = visit(ctx.exp());

		return new VarNode(ctx.vardec().ID().getText(),type,exp);

	}


	@Override
	public Node visitVardec (VardecContext ctx){
		Node vardecNode;
		return null;
	}

//	fun    : type ID LPAR ( vardec ( COMMA vardec)* )? RPAR (CLPAR (notFunDec? ( (exp|stm) SEMIC)) CRPAR)? ;  //modify
	@Override
	public Node visitFun(FunContext ctx){
		//inizializza funNode con nome funzione e tipo funzione
		FunNode funNode =new FunNode(ctx.ID().getText(),visit(ctx.type()));

		// add parameters
		for (VardecContext vdec: ctx.vardec()){
			funNode.addPar(new ParNode(vdec.ID().getText(),visit(vdec.type())));
		}
		//add declaration, but only variable declaration
		ArrayList <Node> declaration =new ArrayList<Node>();
		if (ctx.notFunDec()!=null){
			for (FOOLParser.VarasmContext notFun: ctx.notFunDec().varasm()) {
				declaration.add(visit(notFun));
			}
		}
		Node body=null;
		if(ctx.exp()!=null){
			body=visit(ctx.exp());
		}else {
			if(ctx.stm()!=null){
				body=visit(ctx.stm());
			}
		}
		funNode.addDecBody(declaration,body);
		return  funNode;

	}
	@Override
	public Node visitAssegnamento(FOOLParser.AssegnamentoContext ctx){
		String varName =ctx.ID().getSymbol().getText();
		Node exp=visit(ctx.exp());
		return new VarNodeStm(varName,null,exp); //VarNode(varName,null,exp)
	}
	@Override
	public Node visitAssegnamentoExp(FOOLParser.AssegnamentoExpContext ctx){
		return visit(ctx.stm());
	}
	//ID DOT ID ASM exp   #assegnamentoMethod
	@Override
	public Node visitAssegnamentoMethod(FOOLParser.AssegnamentoMethodContext ctx){
		String object=ctx.ID(0).getText();
		String method=ctx.ID(1).getText();
		Node exp =visit(ctx.exp());
		return new ObjectNodeStm(object,method,exp); //ObjectNode(object,method,exp)
	}
	//ID DOT ID (LPAR (exp (COMMA exp)*)? RPAR)?      #methodExp
	@Override
	public Node visitMethodExp(FOOLParser.MethodExpContext ctx){
		Node ret;
		ArrayList<Node> argomenti=new ArrayList<Node>();
		for (FOOLParser.ExpContext exp: ctx.exp()){
			argomenti.add(visit(exp));
		}
		String nameClass=ctx.ID(0).getText();
		String nameMethod=ctx.ID(1).getText();
		CallMethodExpNode methodEspressione= new CallMethodExpNode(nameClass,nameMethod,argomenti);
		return methodEspressione;
	}

  //     NEW ID LPAR( exp(COMMA exp)* )?  RPAR          #newExp
	@Override
	public Node visitNewExp(FOOLParser.NewExpContext ctx){
		Node ret;
		ArrayList <Node> argomenti =new ArrayList<Node>();
		for (ExpContext exp : ctx.exp()){
			argomenti.add(visit(exp));
		}
		String nomeClass= ctx.ID().getText();
		ret=new NewExpNode(nomeClass, argomenti);
		return ret;

	}

	//classdec+   (let? ((exp|stm) SEMIC) )?  #classDichiarazione
	@Override
	public Node visitClassDichiarazione(FOOLParser.ClassDichiarazioneContext ctx){
		ArrayList<Node> listaClass =new ArrayList<Node>();
		ArrayList<Node> listaDichiarazioni=new ArrayList<>();
		Node exp=null;
		Node stm=null;
		ProgLetInNode letNode=null;
		for (FOOLParser.ClassdecContext clac:ctx.classdec()){
			listaClass.add(visit(clac));
		}
		if (ctx.let()!=null){
			for (DecContext decc :ctx.let().dec()){
				listaDichiarazioni.add(visit(decc));
			}
			if (ctx.exp()!=null){
				exp=visit(ctx.exp());
				letNode=new ProgLetInNode(listaDichiarazioni,exp,stm);
			}
			if (ctx.stm()!=null){
				stm=visit(ctx.stm());
				letNode=new ProgLetInNode(listaDichiarazioni,exp,stm);
			}
		}else{
			if (ctx.exp()!=null){
				exp=visit(ctx.exp());
			}
			if (ctx.stm()!=null){
				stm=visit(ctx.stm());
			}
		}
		return (new ProgClassNode(listaClass,letNode,exp,stm));

	}

	//classdec: CLASS ID  (EXTENDS ID)? LPAR ( vardec ( COMMA vardec)* )? RPAR (CLPAR (dec)* CRPAR)? ;
	@Override
	public Node visitClassdec(FOOLParser.ClassdecContext ctx){
		String nomeClass=ctx.ID(0).getText();
		String extendClassNome=null;
		ArrayList<Node> parlist=new ArrayList<Node>();
		ArrayList<Node> methodlist=new ArrayList<Node>();
		ArrayList<Node> campilist=new ArrayList<Node>();
		if(ctx.ID(1) !=null){
			extendClassNome=ctx.ID(1).getText();
		}
		for (VardecContext vc : ctx.vardec()){
			parlist.add(new ParNode(vc.ID().getText(),visit(vc.type())));
		}
		for (FunContext fc :ctx.fun()){
			FunNode fun=(FunNode) visit(fc);
			methodlist.add(fun);
		}
		for (VarasmContext var:ctx.varasm()){
			Node va=visit(var);
			campilist.add(va);
		}
		return new ClassNode(nomeClass,extendClassNome,parlist,methodlist,campilist);
	}

}
