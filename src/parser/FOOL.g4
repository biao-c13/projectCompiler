grammar FOOL;

@lexer::members {
   //there is a much better way to do this, check the ANTLR guide
   //I will leave it like this for now just becasue it is quick
   //but it doesn't work well
   public int lexicalErrors=0;
}

/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/

prog   : exp SEMIC                    #singleExp
       | let ((exp|stm) SEMIC)          #letInExp                  //modify
       | stm SEMIC                           #assegnamentoExp           //modify
       | classdec+   (let? ((exp|stm) SEMIC) )?  #classDichiarazione         //add
       ;



classdec: CLASS ID  (EXTENDS ID)? LPAR ( vardec ( COMMA vardec)* )? RPAR (CLPAR (fun|varasm SEMIC)* CRPAR)? ;

stm    : ID ASM exp          #assegnamento
       | ID DOT ID ASM exp   #assegnamentoMethod
       | IF exp THEN CLPAR thenBranch=stms CRPAR ELSE CLPAR elseBranch=stms CRPAR  #codizione
       ;

stms   : (stm SEMIC)+;


//-----------------

let       : LET (dec SEMIC)+ IN ;

vardec  : type ID ;

varasm     : vardec ASM exp ;

fun    : type ID LPAR ( vardec ( COMMA vardec)* )? RPAR (CLPAR (notFunDec? ( (exp|stm) SEMIC)) CRPAR)? ;  //modify

notFunDec: LET (varasm SEMIC)+ IN;  //add


dec   : varasm           #varAssignment
  //     | vardec           #vardeclaration      //modificato exp: int a;
      | fun              #funDeclaration
      ;


type   : INT
       | BOOL
       | VOID
       | ID
       | NULL
      ;

exp    : ('-')?left=term ((PLUS | MINUS) right=exp)?  //modify
       ;

term   : left=factor ((TIMES | DIV) right=term)?
      ;

factor : ('not')? left=value ((EQ|SMALLTHAN|GREATTHAN|OR|AND) right=value)?  //modify
      ;

value  : ('-')? INTEGER                           #intVal    //modify
      | ('not')?( TRUE | FALSE )                   #boolVal   //modify
      | LPAR exp RPAR                      #baseExp
      | IF cond=exp THEN CLPAR thenBranch=exp CRPAR ELSE CLPAR elseBranch=exp CRPAR  #ifExp
      | ID                                             #varExp
      | ID ( LPAR (exp (COMMA exp)* )? RPAR )?         #funExp   //callNode
      | ID DOT ID (LPAR (exp (COMMA exp)*)? RPAR)?      #methodExp
      | NEW ID LPAR( exp(COMMA exp)* )?  RPAR          #newExp
      | PRINT LPAR exp RPAR                            #printExp
      ;


/*------------------------------------------------------------------
 * LEXER RULES
 *------------------------------------------------------------------*/

SEMIC  : ';' ;
COLON  : ':' ;
COMMA  : ',' ;
EQ     : '==' ;
ASM    : '=' ;
PLUS   : '+' ;
MINUS  : '-' ;
TIMES  : '*' ;
DIV    : '/' ;
TRUE   : 'true' ;
FALSE  : 'false' ;
LPAR   : '(' ;
RPAR   : ')' ;
CLPAR  : '{' ;
CRPAR  : '}' ;
IF        : 'if' ;
THEN   : 'then' ;
ELSE   : 'else' ;
PRINT : 'print' ;
LET    : 'let' ;
IN     : 'in' ;
VAR    : 'var' ;
FUN    : 'fun' ;
INT    : 'int' ;
BOOL   : 'bool' ;
SMALLTHAN: '<=' ; // "<=" small than
GREATTHAN: '>=' ; // ">=" great than
OR   : '||';      // "||"
AND  :'&&';       // "&&"
NOT  : 'not';// "not"
CLASS : 'class';
EXTENDS:'extends';
VOID: 'void';
DOT: '.';
NEW:'new';
NULL:'null';
//Numbers
fragment DIGIT : '0'..'9';
INTEGER       : DIGIT+;

//IDs
fragment CHAR  : 'a'..'z' |'A'..'Z' ;
ID              : CHAR (CHAR | DIGIT)* ;

//ESCAPED SEQUENCES
WS              : (' '|'\t'|'\n'|'\r')-> skip;
LINECOMENTS    : '//' (~('\n'|'\r'))* -> skip;
BLOCKCOMENTS    : '/*'( ~('/'|'*')|'/'~'*'|'*'~'/'|BLOCKCOMENTS)* '*/' -> skip;




 //VERY SIMPLISTIC ERROR CHECK FOR THE LEXING PROCESS, THE OUTPUT GOES DIRECTLY TO THE TERMINAL
 //THIS IS WRONG!!!!
ERR     : . { System.out.println("Invalid char: "+ getText()); lexicalErrors++; } -> channel(HIDDEN);
