# projectCompiler
Creation of compiler(A compiler is a translator which transforms source language (high-level language) into object language (machine language))

The project consists in the realization of a compiler for a language whose syntax must include that of FOOL

//start Grammar FOOL

fool   : exp SEMIC             | let exp SEMIC             

let    : LET (dec SEMIC)+ IN ;

vardec  : type ID ;

varasm     : vardec ASM exp ;

fun    : type ID LPAR ( vardec ( COMMA vardec)* )? RPAR (let)? exp ;

dec   : varasm          | fun              

type   : INT  | BOOL ;  

    
exp : left=term (PLUS right=exp)? ;
   
term : left=factor (TIMES right=term)?;
   
factor : left=value (EQ right=value)?;     
   
value  : INTEGER                         | ( TRUE | FALSE )                   
      | LPAR exp RPAR                      
          | IF cond=exp THEN CLPAR thenBranch=exp CRPAR ELSE CLPAR elseBranch=exp CRPAR  
          | ID                                             
          | ID ( LPAR (exp (COMMA exp)* )? RPAR )?             ; 

*------------------------------------------------------------------
//end Grammar Fool

This language is an object-oriented language, extension of functional language:

1)It is possible to declare classes and subclasses.
2)Classes contain
    ** fields (declared in the class or inherited from the super-class)
    ** methods (explicitly declared in the class or inherited from the super-class).
3) there is a void type, in addition to the class type.  


The compiler must include a type-checker that checks the correct use of types.
define and implement the typing rules for all constructs, in particular for the conditional.


The compiler must generate code for a virtual executor called SVM (stack
virtual machine) whose syntax is defined in the SVM.g file.


the main file is located in projectCompiler/src/main
