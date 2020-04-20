# projectCompiler
Creation of compilers (A compiler is a translator which transforms source language (high-level language) into object language (machine language))

The project consists in the realization of a compiler for a language whose syntax must include that of FOOL

//star Grammar Fool
FOOL Grammar:
fool   : exp SEMIC                 #singleExp
       | let exp SEMIC             #letInExp;
let    : LET (dec SEMIC)+ IN ;
vardec  : type ID ;
varasm     : vardec ASM exp ;
fun    : type ID LPAR ( vardec ( COMMA vardec)* )? RPAR (let)? exp ;
dec   : varasm           #varAssignment
      | fun              #funDeclaration;
type   : INT  | BOOL ;  
    
exp : left=term (PLUS right=exp)? ;
   
term : left=factor (TIMES right=term)?;
   
factor : left=value (EQ right=value)?;     
   
value  : INTEGER                           #intVal
      | ( TRUE | FALSE )                   #boolVal
      | LPAR exp RPAR                      #baseExp
          | IF cond=exp THEN CLPAR thenBranch=exp CRPAR ELSE CLPAR elseBranch=exp CRPAR  #ifExp
          | ID                                             #varExp
          | ID ( LPAR (exp (COMMA exp)* )? RPAR )?         #funExp    ; 

*------------------------------------------------------------------
//end Grammar Fool

This language is an object-oriented extension of functional language:

  1)It is possible to declare classes and subclasses.
  2)Classes contain
    ** fields (declared in the class or inherited from the super-class)
    ** methods (explicitly declared in the class or inherited from the super-class).
  

The compiler must include a type-checker that checks the correct use of types.

  1) there is a void type, in addition to the class type.
  2)define and implement the typing rules for all constructs, in particular for the conditional.

The compiler must generate code for a virtual executor called SVM (stack
virtual machine) whose syntax is defined in the SVM.g file.
