import syntaxtree.*;
import visitor.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class CheckProgram {
    public static void evaluate(String arg) throws Exception {
        if(arg==null){
            System.err.println("Usage: java Main <inputFile>");
            System.exit(1);
        }
        FileInputStream fis = null;
        try{

            fis = new FileInputStream(arg);
            MiniJavaParser parser = new MiniJavaParser(fis);

            Goal root = parser.Goal();
            MyVisitor eval = new MyVisitor();
            MyVisitor.symbolTable = new TreeMap<>();
            MyVisitor.classes = new ArrayList<>();
            MyVisitor.simpleClasses = new ArrayList<>();
            MyVisitor.used = new TreeMap<>();
            MyVisitor.parameters = new TreeMap<>();
            MyVisitor.symbolTables = new ArrayList<>();
            MyVisitor.useds = new ArrayList<>();
            MyVisitor.methods = new ArrayList<>();

            root.accept(eval, null);
            Helper.doubleDeclarationCheck();

            TypeCheckVisitor eval1 = new TypeCheckVisitor();
            root.accept(eval1, null);
            Helper.methodsCheck();
            Helper.printOutput();

            System.out.println(ANSI_GREEN+"File " + arg + " parsed successfully."+ANSI_RESET);

            MyVisitor.symbolTable = null;
            MyVisitor.classes = null;
            MyVisitor.simpleClasses = null;
            MyVisitor.used = null;
            MyVisitor.parameters = null;
            MyVisitor.symbolTables = null;
            MyVisitor.useds = null;
            MyVisitor.methods = null;
            System.gc();
        }
        catch(ParseException ex){
            System.out.println(ANSI_RED+"Caught "+ex.getMessage()+" in file "+arg+ANSI_RESET);
            MyVisitor.symbolTable = null;
            MyVisitor.classes = null;
            MyVisitor.simpleClasses = null;
            MyVisitor.used = null;
            MyVisitor.parameters = null;
            MyVisitor.symbolTables = null;
            MyVisitor.useds = null;
            MyVisitor.methods = null;
            System.gc();
        }
        catch(FileNotFoundException ex){
            System.err.println(ANSI_BLUE+ex.getMessage()+ANSI_RESET);
        }
        finally{
            try{
                if(fis != null) fis.close();
            }
            catch(IOException ex){
                System.err.println(ex.getMessage());
            }
        }
    }

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE = "\u001B[34m";
}


class MyVisitor extends GJDepthFirst<String, String>{

    public static Map<String, Map<Integer,String>> symbolTable;    //scope integer, string variable, memory position integer
    public static ArrayList<String> classes;
    public static ArrayList<String> simpleClasses;
    public static Map<String, Map<Integer,String>> used;
    public static Map<String, Map<Integer,String>> parameters;
    public static ArrayList<String> symbolTables;
    public static ArrayList<String> useds;
    public static ArrayList<String> methods;

    /**
     * f0 -> MainClass()
     * f1 -> ( TypeDeclaration() )*
     * f2 -> <EOF>
     */
    public String visit(Goal n, String argu) throws Exception {
        String retStr=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return retStr;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> "public"
     * f4 -> "static"
     * f5 -> "void"
     * f6 -> "main"
     * f7 -> "("
     * f8 -> "String"
     * f9 -> "["
     * f10 -> "]"
     * f11 -> Identifier()
     * f12 -> ")"
     * f13 -> "{"
     * f14 -> ( VarDeclaration() )*
     * f15 -> ( Statement() )*
     * f16 -> "}"
     * f17 -> "}"
     */
    public String visit(MainClass n, String argu) throws Exception {
//        n.f0.accept(this, argu);
        String classname = n.f1.accept(this, argu);
//        System.out.println("Class: " + classname);
        Map<Integer,String> scope = new TreeMap<>();
        symbolTable.put(classname, scope);
        symbolTables.add(classname);
        classes.add(classname);
//        n.f2.accept(this, argu);
//        n.f3.accept(this, argu);
//        n.f4.accept(this, argu);
//        n.f5.accept(this, argu);
//        n.f6.accept(this, argu);
//        n.f7.accept(this, argu);
//        n.f8.accept(this, argu);
//        n.f9.accept(this, argu);
//        n.f10.accept(this, argu);
//        n.f11.accept(this, argu);
//        n.f12.accept(this, argu);
//        n.f13.accept(this, argu);
        n.f14.accept(this, classname);
//        n.f15.accept(this, argu);
//        n.f16.accept(this, argu);
//        n.f17.accept(this, argu);
//        scope.put("myVar",12);
//        super.visit(n, classname);
//        System.out.println();
        return null;
    }

    /**
     * f0 -> ClassDeclaration()
     *       | ClassExtendsDeclaration()
     */
    public String visit(TypeDeclaration n, String argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
    public String visit(ClassDeclaration n, String argu) throws Exception {
        n.f0.accept(this, argu);
        String classname = n.f1.accept(this, argu);
//        System.out.println("Class: " + classname );
        Map<Integer,String> scope = new TreeMap<>();
        symbolTable.put(classname , scope);
//        n.f2.accept(this, argu);
        n.f3.accept(this, classname);
        n.f4.accept(this, classname);
//        n.f5.accept(this, argu);
        symbolTables.add(classname);
        classes.add(classname);
        simpleClasses.add(classname);
//        super.visit(n, classname);
//        System.out.println();
        return null;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "extends"
     * f3 -> Identifier()
     * f4 -> "{"
     * f5 -> ( VarDeclaration() )*
     * f6 -> ( MethodDeclaration() )*
     * f7 -> "}"
     */
    public String visit(ClassExtendsDeclaration n, String argu) throws Exception {
        n.f0.accept(this, argu);
        String classname = n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        String extended = n.f3.accept(this, argu);
        String extendOf = " extends " + extended;
        if(!simpleClasses.contains(extended)){
            System.out.println("ERROR: undefined parent class: " + extended);
            throw new ParseException("ERROR");
        }
//        System.out.println("Class: " + classname+extendOf );
        Map<Integer,String> scope = new TreeMap<>();
        symbolTable.put(classname+extendOf , scope);
//        n.f4.accept(this, argu);
        n.f5.accept(this, classname+extendOf);
        n.f6.accept(this, classname+extendOf);
//        n.f7.accept(this, argu);
        classes.add(classname+extendOf);
        simpleClasses.add(classname);
        symbolTables.add(classname+extendOf);
//        super.visit(n, classname+extendOf);
//        System.out.println();
        return null;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    public String visit(VarDeclaration n, String argu) throws Exception{
        String type = n.f0.accept(this, argu);
        String name = n.f1.accept(this, argu);
        n.f2.accept(this, argu);
//        System.out.println(type + " " + name);
        symbolTable.get(argu).put(symbolTable.get(argu).size(),type + " " + name);
        return null;
    }

    /**
     * f0 -> "public"
     * f1 -> Type()
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( FormalParameterList() )?
     * f5 -> ")"
     * f6 -> "{"
     * f7 -> ( VarDeclaration() )*
     * f8 -> ( Statement() )*
     * f9 -> "return"
     * f10 -> Expression()
     * f11 -> ";"
     * f12 -> "}"
    **/
    public String visit(MethodDeclaration n, String argu) throws Exception {
        n.f0.accept(this, argu);
        String myType = n.f1.accept(this, argu);
        String myName = n.f2.accept(this, argu);
        symbolTable.get(argu).put( symbolTable.get(argu).size(),"method " + myType  + " " + myName);
        Map<Integer,String> scope = new TreeMap<>();
        symbolTable.put(argu + "." + myName , scope);
        symbolTables.add(argu + "." + myName);
//        n.f3.accept(this, argu);
        String argumentList = n.f4.present() ? n.f4.accept(this, argu + "." + myName) : "";
        Map<Integer,String> param = new TreeMap<>();
        String[] arg = argumentList.split(",");
        if(!argumentList.equals(""))
            for(int i=0; i<arg.length;i++)
                param.put(param.size(),arg[i].trim());
        parameters.put(argu + "." + myName , param);
//        n.f5.accept(this, argu);
//        n.f6.accept(this, argu);
        n.f7.accept(this, argu + "." + myName);
//        n.f8.accept(this, argu);
//        n.f9.accept(this, argu);
        n.f10.accept(this, argu + "." + myName);
        String retval = n.f10.accept(this, argu + "." + myName);
//        MyVisitor.methods.add(argu + "." + myName + "/" + myType + "/" + retval);//        n.f11.accept(this, argu);
//        n.f11.accept(this, argu);
//        n.f12.accept(this, argu);
        return null;
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> FormalParameterTail()
     **/
    @Override
    public String visit(FormalParameterList n, String argu) throws Exception {
        String ret = n.f0.accept(this, argu);
        if (n.f1 != null) {
            ret += n.f1.accept(this, argu);
        }
        return ret;
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> FormalParameterTail()
     **/
    public String visit(FormalParameterTerm n, String argu) throws Exception {
        return n.f1.accept(this, argu);
    }

    /**
     * f0 -> ","
     * f1 -> FormalParameter()
     **/
    @Override
    public String visit(FormalParameterTail n, String argu) throws Exception {
        String ret = "";
        for ( Node node: n.f0.nodes) {
            ret += ", " + node.accept(this, argu);
        }

        return ret;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     **/
    @Override
    public String visit(FormalParameter n, String argu) throws Exception{
        String type = n.f0.accept(this, argu);
        String name = n.f1.accept(this, argu);
        symbolTable.get(argu).put(symbolTable.get(argu).size(),type + " " + name);
        return  type + " " + name;
    }
    /**
     * f0 -> ArrayType()
     *       | BooleanType()
     *       | IntegerType()
     *       | Identifier()
     */
    public String visit(Type n, String argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "int"
     * f1 -> "["
     * f2 -> "]"
     */
    public String visit(ArrayType n, String argu) {
        return "int[]";
    }

    /**
     * f0 -> "boolean"
     */
    public String visit(BooleanType n, String argu) {
        return "boolean";
    }


    /**
     * f0 -> "int"
     */
    public String visit(IntegerType n, String argu) {
        return "int";
    }

    /**
     * f0 -> Block()
     *       | AssignmentStatement()
     *       | ArrayAssignmentStatement()
     *       | IfStatement()
     *       | WhileStatement()
     *       | PrintStatement()
     */
    public String visit(Statement n, String argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "{"
     * f1 -> ( Statement() )*
     * f2 -> "}"
     */
    public String visit(Block n, String argu) throws Exception {
        String retStr=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return retStr;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "="
     * f2 -> Expression()
     * f3 -> ";"
     */
    public String visit(AssignmentStatement n, String argu) throws Exception {
        String retStr=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        return retStr;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "["
     * f2 -> Expression()
     * f3 -> "]"
     * f4 -> "="
     * f5 -> Expression()
     * f6 -> ";"
     */
    public String visit(ArrayAssignmentStatement n, String argu) throws Exception {
        String retStr=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        return retStr;
    }

    /**
     * f0 -> "if"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     * f5 -> "else"
     * f6 -> Statement()
     */
    public String visit(IfStatement n, String argu) throws Exception {
        String retStr=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        return retStr;
    }

    /**
     * f0 -> "while"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     */
    public String visit(WhileStatement n, String argu) throws Exception {
        String retStr=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return retStr;
    }

    /**
     * f0 -> "System.out.println"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> ";"
     */
    public String visit(PrintStatement n, String argu) throws Exception {
        String retStr=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return retStr;
    }

    /**
     * f0 -> AndExpression()
     *       | CompareExpression()
     *       | PlusExpression()
     *       | MinusExpression()
     *       | TimesExpression()
     *       | ArrayLookup()
     *       | ArrayLength()
     *       | MessageSend()
     *       | PrimaryExpression()
     */
    public String visit(Expression n, String argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "&&"
     * f2 -> PrimaryExpression()
     */
    public String visit(AndExpression n, String argu) throws Exception {
        String retStr=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return retStr;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "<"
     * f2 -> PrimaryExpression()
     */
    public String visit(CompareExpression n, String argu) throws Exception {
        String retStr=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return retStr;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "+"
     * f2 -> PrimaryExpression()
     */
    public String visit(PlusExpression n, String argu) throws Exception {
        String retStr=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return retStr;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "-"
     * f2 -> PrimaryExpression()
     */
    public String visit(MinusExpression n, String argu) throws Exception {
        String retStr=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return retStr;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "*"
     * f2 -> PrimaryExpression()
     */
    public String visit(TimesExpression n, String argu) throws Exception {
        String retStr=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return retStr;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "["
     * f2 -> PrimaryExpression()
     * f3 -> "]"
     */
    public String visit(ArrayLookup n, String argu) throws Exception {
        String retStr=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        return retStr;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> "length"
     */
    public String visit(ArrayLength n, String argu) throws Exception {
        String retStr=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return retStr;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( ExpressionList() )?
     * f5 -> ")"
     */
    public String visit(MessageSend n, String argu) throws Exception {
        String retStr=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        return retStr;
    }

    /**
     * f0 -> Expression()
     * f1 -> ExpressionTail()
     */
    public String visit(ExpressionList n, String argu) throws Exception {
        String retStr=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return retStr;
    }

    /**
     * f0 -> ( ExpressionTerm() )*
     */
    public String visit(ExpressionTail n, String argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> ","
     * f1 -> Expression()
     */
    public String visit(ExpressionTerm n, String argu) throws Exception {
        String retStr=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return retStr;
    }

    /**
     * f0 -> IntegerLiteral()
     *       | TrueLiteral()
     *       | FalseLiteral()
     *       | Identifier()
     *       | ThisExpression()
     *       | ArrayAllocationExpression()
     *       | AllocationExpression()
     *       | NotExpression()
     *       | BracketExpression()
     */
    public String visit(PrimaryExpression n, String argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    public String visit(IntegerLiteral n, String argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "true"
     */
    public String visit(TrueLiteral n, String argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "false"
     */
    public String visit(FalseLiteral n, String argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    public String visit(Identifier n, String argu) throws Exception {
        String retStr = n.f0.toString();
        return retStr;
    }

    /**
     * f0 -> "this"
     */
    public String visit(ThisExpression n, String argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "new"
     * f1 -> "int"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    public String visit(ArrayAllocationExpression n, String argu) throws Exception {
        String retStr=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return retStr;
    }

    /**
     * f0 -> "new"
     * f1 -> Identifier()
     * f2 -> "("
     * f3 -> ")"
     */
    public String visit(AllocationExpression n, String argu) throws Exception {
        String retStr=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        return retStr;
    }

    /**
     * f0 -> "!"
     * f1 -> PrimaryExpression()
     */
    public String visit(NotExpression n, String argu) throws Exception {
        String retStr=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return retStr;
    }

    /**
     * f0 -> "("
     * f1 -> Expression()
     * f2 -> ")"
     */
    public String visit(BracketExpression n, String argu) throws Exception {
        String retStr=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return retStr;
    }
}


class TypeCheckVisitor extends GJDepthFirst<String, String>{

    /**
     * f0 -> MainClass()
     * f1 -> ( TypeDeclaration() )*
     * f2 -> <EOF>
     */
    public String visit(Goal n, String argu) throws Exception {
        String retStr=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return retStr;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> "public"
     * f4 -> "static"
     * f5 -> "void"
     * f6 -> "main"
     * f7 -> "("
     * f8 -> "String"
     * f9 -> "["
     * f10 -> "]"
     * f11 -> Identifier()
     * f12 -> ")"
     * f13 -> "{"
     * f14 -> ( VarDeclaration() )*
     * f15 -> ( Statement() )*
     * f16 -> "}"
     * f17 -> "}"
     */
    public String visit(MainClass n, String argu) throws Exception {
        String retStr=null;
        //        n.f0.accept(this, argu);
        String classname = n.f1.accept(this, argu);
//        n.f2.accept(this, argu);
//        n.f3.accept(this, argu);
//        n.f4.accept(this, argu);
//        n.f5.accept(this, argu);
//        n.f6.accept(this, argu);
//        n.f7.accept(this, argu);
//        n.f8.accept(this, argu);
//        n.f9.accept(this, argu);
//        n.f10.accept(this, argu);
//        n.f11.accept(this, argu);
//        n.f12.accept(this, argu);
//        n.f13.accept(this, argu);
//        n.f14.accept(this, argu);
        n.f15.accept(this, classname);
//        n.f16.accept(this, argu);
//        n.f17.accept(this, argu);
        return retStr;
    }

    /**
     * f0 -> ClassDeclaration()
     *       | ClassExtendsDeclaration()
     */
    public String visit(TypeDeclaration n, String argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
    public String visit(ClassDeclaration n, String argu) throws Exception {
        String retStr=null;
//        n.f0.accept(this, argu);
        String classname = n.f1.accept(this, argu);
//        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, classname);
//        n.f5.accept(this, argu);
        return retStr;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "extends"
     * f3 -> Identifier()
     * f4 -> "{"
     * f5 -> ( VarDeclaration() )*
     * f6 -> ( MethodDeclaration() )*
     * f7 -> "}"
     */
    public String visit(ClassExtendsDeclaration n, String argu) throws Exception {
        String retStr=null;
//        n.f0.accept(this, argu);
//        n.f1.accept(this, argu);
        String classname = n.f1.accept(this, argu);
//        n.f2.accept(this, argu);
        String extendOf = " extends " + n.f3.accept(this, argu);
//        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, classname+extendOf);
//        n.f7.accept(this, argu);
        return retStr;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    public String visit(VarDeclaration n, String argu) throws Exception{
        String type = n.f0.accept(this, argu);
        if(!MyVisitor.simpleClasses.contains(type) && !type.equals("int") && !type.equals("int[]") && !type.equals("boolean")){
            System.out.println("ERROR: undefined identifier: " + type);
            throw new ParseException("ERROR");
        }
        String name = n.f1.accept(this, argu);
        n.f2.accept(this, argu);
//        System.out.println(type + " " + name);
        return type + " " + name;
    }

    /**
     * f0 -> "public"
     * f1 -> Type()
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( FormalParameterList() )?
     * f5 -> ")"
     * f6 -> "{"
     * f7 -> ( VarDeclaration() )*
     * f8 -> ( Statement() )*
     * f9 -> "return"
     * f10 -> Expression()
     * f11 -> ";"
     * f12 -> "}"
    **/
    public String visit(MethodDeclaration n, String argu) throws Exception {
        String retStr=null;
//        n.f0.accept(this, argu);
        String myType = n.f1.accept(this, argu);
        String myName = n.f2.accept(this, argu);
//        n.f1.accept(this, argu);
//        n.f2.accept(this, argu);
//        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
//        n.f5.accept(this, argu);
//        n.f6.accept(this, argu);
        n.f7.accept(this, argu);
        n.f8.accept(this, argu + "." + myName);
//        n.f9.accept(this, argu);
        String retval = n.f10.accept(this, argu + "." + myName);
        MyVisitor.methods.add(argu + "." + myName + "/" + myType + "/" + retval);//        n.f11.accept(this, argu);
//        n.f12.accept(this, argu);
        return retStr;
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> FormalParameterTail()
     **/
    @Override
    public String visit(FormalParameterList n, String argu) throws Exception {
        String ret = n.f0.accept(this, argu);
        if (n.f1 != null) {
            ret += n.f1.accept(this, argu);
        }
        return ret;
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> FormalParameterTail()
     **/
    public String visit(FormalParameterTerm n, String argu) throws Exception {
        return n.f1.accept(this, argu);
    }

    /**
     * f0 -> ","
     * f1 -> FormalParameter()
     **/
    @Override
    public String visit(FormalParameterTail n, String argu) throws Exception {
        String ret = "";
        for ( Node node: n.f0.nodes) {
            ret += ", " + node.accept(this, argu);
        }

        return ret;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     **/
    @Override
    public String visit(FormalParameter n, String argu) throws Exception{
        String type = n.f0.accept(this, argu);
        if(!MyVisitor.simpleClasses.contains(type) && !type.equals("int") && !type.equals("int[]") && !type.equals("boolean")){
            System.out.println("ERROR: undefined identifier: " + type);
            throw new ParseException("ERROR");
        }
        String name = n.f1.accept(this, argu);
        return  type + " " + name;
    }
    /**
     * f0 -> ArrayType()
     *       | BooleanType()
     *       | IntegerType()
     *       | Identifier()
     */
    public String visit(Type n, String argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "int"
     * f1 -> "["
     * f2 -> "]"
     */
    public String visit(ArrayType n, String argu) {
        return "int[]";
    }

    /**
     * f0 -> "boolean"
     */
    public String visit(BooleanType n, String argu) {
        return "boolean";
    }


    /**
     * f0 -> "int"
     */
    public String visit(IntegerType n, String argu) {
        return "int";
    }

    /**
     * f0 -> Block()
     *       | AssignmentStatement()
     *       | ArrayAssignmentStatement()
     *       | IfStatement()
     *       | WhileStatement()
     *       | PrintStatement()
     */
    public String visit(Statement n, String argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "{"
     * f1 -> ( Statement() )*
     * f2 -> "}"
     */
    public String visit(Block n, String argu) throws Exception {
        String retStr=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return retStr;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "="
     * f2 -> Expression()
     * f3 -> ";"
     */
    public String visit(AssignmentStatement n, String argu) throws Exception {
        String retStr=null;
        String[] crop = argu.split("[.]");
        String name = n.f0.accept(this, argu);
        String[] cropname = name.split("\\s");
//        System.out.println(argu+" " +name);
//        n.f1.accept(this, argu);
        String exp = n.f2.accept(this, argu);
        if(!Helper.typeOfVariable(crop[0],argu,exp,name))
        {
            System.out.println("ERROR: wrong assignment: incombatible types: " + name);
//            System.exit(1);
            throw new ParseException("ERROR");
        }
//        System.out.println(name+" = "+exp );
//        n.f3.accept(this, argu);
        return retStr;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "["
     * f2 -> Expression()
     * f3 -> "]"
     * f4 -> "="
     * f5 -> Expression()
     * f6 -> ";"
     */
    public String visit(ArrayAssignmentStatement n, String argu) throws Exception {
        String retStr=null;
        String name = n.f0.accept(this, argu);
        String[] crop = argu.split("[.]");
//        System.out.println(name);
        n.f1.accept(this, argu);
        String index = n.f2.accept(this, argu);
        //check if index is integer
        if(!index.equals("int")) {
            System.out.println("ERROR: array index not integer");
//            System.exit(1);
            throw new ParseException("ERROR");
        }
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        String exp2 = n.f5.accept(this, argu);
        //check if expression assigned to array is integer and if identifier is integer array
        if(!Helper.typeOfVariable(crop[0],argu,exp2+"[]",name))
        {
            System.out.println("ERROR: wrong assignment: incombatible types: " + name);
//            System.exit(1);
            throw new ParseException("ERROR");
        }
//        System.out.println(exp2);
        n.f6.accept(this, argu);
        return retStr;
    }

    /**
     * f0 -> "if"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     * f5 -> "else"
     * f6 -> Statement()
     */
    public String visit(IfStatement n, String argu) throws Exception {
        String retStr=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String iff = n.f2.accept(this, argu);
        if(!iff.equals("boolean")){
            System.out.println("ERROR: incompatible types: cannot be converted to boolean");
//            System.exit(1);
            throw new ParseException("ERROR");
        }
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        return retStr;
    }

    /**
     * f0 -> "while"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     */
    public String visit(WhileStatement n, String argu) throws Exception {
        String retStr=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String whill = n.f2.accept(this, argu);
        if(!whill.equals("boolean")){
            System.out.println("ERROR: incompatible types: cannot be converted to boolean");
//            System.exit(1);
            throw new ParseException("ERROR");
        }
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return retStr;
    }

    /**
     * f0 -> "System.out.println"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> ";"
     */
    public String visit(PrintStatement n, String argu) throws Exception {
        String retStr=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String prnt = n.f2.accept(this, argu);
        if(!prnt.equals("int")){
            System.out.println("ERROR: incompatible types: cannot be converted to int");
//            System.exit(1);
            throw new ParseException("ERROR");
        }
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return retStr;
    }

    /**
     * Expression -> AndExpression() OK epistrefei typeBoolean
     *       | CompareExpression() OK epistrefei typeBoolean
     *       | PlusExpression() OK epistrefei typeInt
     *       | MinusExpression() OK epistrefei typeInt
     *       | TimesExpression() OK epistrefei typeInt
     *       | ArrayLookup() OK epistrefei typeInt
     *       | ArrayLength() OK epistrefei typeInt
     *       | MessageSend() OK epistrefei ton typo methodou tha prepei na elegxei an h klhsh exei swsta orismata
     *       | PrimaryExpression()
     */
    public String visit(Expression n, String argu) throws Exception {
        String retStr=n.f0.accept(this, argu);
//        System.out.println(retStr);
        return retStr;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "&&"
     * f2 -> PrimaryExpression()
     */
    public String visit(AndExpression n, String argu) throws Exception {
        String retStr="boolean";
        String[] crop = argu.split("[.]");
        String arg1 = n.f0.accept(this, argu);
        if(!arg1.equals("boolean")&&!Helper.typeOfVariable(crop[0],argu,"boolean",arg1) ){
            System.out.println("ERROR: incompatible types: cannot be converted to boolean");
//            System.exit(1);
            throw new ParseException("ERROR");
        }
        n.f1.accept(this, argu);
        String arg2 = n.f2.accept(this, argu);
        if(!arg2.equals("boolean")&&!Helper.typeOfVariable(crop[0],argu,"boolean",arg2) ){
            System.out.println("ERROR: incompatible types: cannot be converted to boolean");
//            System.exit(1);
            throw new ParseException("ERROR");
        }
        return retStr;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "<"
     * f2 -> PrimaryExpression()
     */
    public String visit(CompareExpression n, String argu) throws Exception {
        String retStr="boolean";
        String[] crop = argu.split("[.]");
        String arg1 = n.f0.accept(this, argu);
        if(!arg1.equals("int")&&!Helper.typeOfVariable(crop[0],argu,"int",arg1) ){
            System.out.println("ERROR: incompatible types: cannot be converted to int");
//            System.exit(1);
            throw new ParseException("ERROR");
        }
        n.f1.accept(this, argu);
        String arg2 = n.f2.accept(this, argu);
        if(!arg2.equals("int")&&!Helper.typeOfVariable(crop[0],argu,"int",arg2) ){
            System.out.println("ERROR: incompatible types: cannot be converted to int");
//            System.exit(1);
            throw new ParseException("ERROR");
        }
        return retStr;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "+"
     * f2 -> PrimaryExpression()
     */
    public String visit(PlusExpression n, String argu) throws Exception {
        String retStr="int";
        String[] crop = argu.split("[.]");
        String arg1 = n.f0.accept(this, argu);
        if(!arg1.equals("int")&&!Helper.typeOfVariable(crop[0],argu,"int",arg1) ){
            System.out.println("ERROR: incompatible types: cannot be converted to int");
//            System.exit(1);
            throw new ParseException("ERROR");
        }
        n.f1.accept(this, argu);
        String arg2 = n.f2.accept(this, argu);
        if(!arg2.equals("int")&&!Helper.typeOfVariable(crop[0],argu,"int",arg2) ){
            System.out.println("ERROR: incompatible types: cannot be converted to int");
//            System.exit(1);
            throw new ParseException("ERROR");
        }
        return retStr;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "-"
     * f2 -> PrimaryExpression()
     */
    public String visit(MinusExpression n, String argu) throws Exception {
        String retStr="int";
        String[] crop = argu.split("[.]");
        String arg1 = n.f0.accept(this, argu);
        if(!arg1.equals("int")&&!Helper.typeOfVariable(crop[0],argu,"int",arg1) ){
            System.out.println("ERROR: incompatible types: cannot be converted to int");
//            System.exit(1);
            throw new ParseException("ERROR");
        }
        n.f1.accept(this, argu);
        String arg2 = n.f2.accept(this, argu);
        if(!arg2.equals("int")&&!Helper.typeOfVariable(crop[0],argu,"int",arg2) ){
            System.out.println("ERROR: incompatible types: cannot be converted to int");
//            System.exit(1);
            throw new ParseException("ERROR");
        }
        return retStr;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "*"
     * f2 -> PrimaryExpression()
     */
    public String visit(TimesExpression n, String argu) throws Exception {
        String retStr="int";
        String[] crop = argu.split("[.]");
        String arg1 = n.f0.accept(this, argu);
        if(!arg1.equals("int")&&!Helper.typeOfVariable(crop[0],argu,"int",arg1) ){
            System.out.println("ERROR: incompatible types: cannot be converted to int");
//            System.exit(1);
            throw new ParseException("ERROR");
        }
        n.f1.accept(this, argu);
        String arg2 = n.f2.accept(this, argu);
        if(!arg2.equals("int")&&!Helper.typeOfVariable(crop[0],argu,"int",arg2) ){
            System.out.println("ERROR: incompatible types: cannot be converted to int");
//            System.exit(1);
            throw new ParseException("ERROR");
        }
        return retStr;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "["
     * f2 -> PrimaryExpression()
     * f3 -> "]"
     */
    public String visit(ArrayLookup n, String argu) throws Exception {
        String retStr="int";
        //to f0 tha prepei na einai Identifier enos int array
        //tha prepei na epistrefw ton typo tou identifier(xwris to array) afou koitaksw to symbolTable
        String arg1 = n.f0.accept(this, argu);
        String[] crop = argu.split("[.]");
        if(!arg1.equals("int[]")&&!Helper.typeOfVariable(crop[0],argu,"int[]",arg1)&&!arg1.equals("new int []"))
        {
            System.out.println("ERROR: wrong assignment: incombatible types: " + arg1);
//            System.exit(1);
            throw new ParseException("ERROR");
        }
        n.f1.accept(this, argu);
        String arg2 = n.f2.accept(this, argu);
        if(!arg2.equals("int")&&!Helper.typeOfVariable(crop[0],argu,"int",arg2) ){
            System.out.println("ERROR: incompatible types: array index is not int");
//            System.exit(1);
            throw new ParseException("ERROR");
        }
        n.f3.accept(this, argu);
        return retStr;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> "length"
     */
    public String visit(ArrayLength n, String argu) throws Exception {
        String retStr="int";
        String arg1 = n.f0.accept(this, argu);
        String[] crop = argu.split("[.]");
        if(!arg1.equals("int[]")&&!Helper.typeOfVariable(crop[0],argu,"int[]",arg1)&&!arg1.equals("new int []"))
        {
            System.out.println("ERROR: wrong assignment: incombatible types: " + arg1 + " is not an array");
//            System.exit(1);
            throw new ParseException("ERROR");
        }
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return retStr;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( ExpressionList() )?
     * f5 -> ")"
     */
    public String visit(MessageSend n, String argu) throws Exception {
        String retStr=null;
        //tha prepei na epistrefw ton typo epistrofhs ths methodou
        String arg1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String method = n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        String argList = n.f4.present() ? n.f4.accept(this, argu) : "";
        n.f5.accept(this, argu);
//        System.out.println("new");
//        System.out.println(argu+" "+arg1+"."+method+"("+argList+")");
        String[] crop = argu.split("[.]");


        retStr = Helper.getTypeOfMessage(crop[0],arg1+"."+method+"("+argList+")");
//        System.out.println("ret "+ retStr);
        if(retStr==null) {
            System.out.println("ERROR: messageSend");
//            System.exit(1);
            throw new ParseException("ERROR");
        }
        if(retStr.equals("")){
            System.out.println("ERROR: messageSend");
//            System.exit(1);
            throw new ParseException("ERROR");
        }
        return retStr;
    }


    //    ExpressionList 	::= 	Expression() ExpressionTail()
    /**
     * f0 -> 	Expression()
     * f1 -> 	ExpressionTail()
     **/
    @Override
    public String visit(ExpressionList n, String argu) throws Exception{
//        System.out.println();
//        super.visit(n, argu);
//        System.out.println();
        String ret = n.f0.accept(this, argu);
        if (n.f1 != null) {
            ret += n.f1.accept(this, argu);
        }
        return ret;
    }

//    ExpressionTail() 	::= 	"," Expression
    /**
     * f0 -> ( ExpressionTerm() )*
     */
    @Override
    public String visit(ExpressionTail n, String argu) throws Exception{
        String ret = "";
        for ( Node node: n.f0.nodes) {
            ret += ", " + node.accept(this, argu);
        }
        return ret;
    }

//    ExpressionTerm() 	::= 	"," Expression
    /**
     * f0 -> ","
     * f1 -> Expression()
     */
    @Override
    public String visit(ExpressionTerm n, String argu) throws Exception{
		return n.f1.accept(this, argu);
    }

    /**
     * f0 -> IntegerLiteral() OK epistrefei typeInt
     *       | TrueLiteral() OK epistrefei typeBoolean
     *       | FalseLiteral() OK epistrefei typeBoolean
     *       | Identifier() OK epistrefei id
     *       | ThisExpression() OK epistrefei this
     *       | ArrayAllocationExpression() OK θα πρεπει ο identifier na elegxei an yparxei tetoio class
     *                                        kai ua prepei na elgxetai apo to anwtero epipedo an ginetai swsto assignment
     *       | AllocationExpression() OK θα πρεπει ο identifier na elegxei an yparxei tetoio class
     *                                  kai ua prepei na elgxetai apo to anwtero epipedo an ginetai swsto assignment
     *       | NotExpression() OK epistrefei typeBoolean
     *       | BracketExpression() OK epistrefei typeBoolean na elegxei an einai boolean mesa
     */
    public String visit(PrimaryExpression n, String argu) throws Exception {
        String retStr = n.f0.accept(this, argu);
        String arg;
        String[] crop = argu.split("[.]");
        if(retStr==null){
            System.out.println("PrimaryExpression received null");
            throw new ParseException("ERROR");
//            System.exit(1);
        }
        if(!retStr.equals("int")&&!retStr.equals("boolean")) {
            arg = Helper.getTypeOfVariable(crop[0], argu, retStr);
            if(!arg.equals(""))
                return arg;
        }

        return retStr;
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    public String visit(IntegerLiteral n, String argu) throws Exception {
        if(Helper.isInt(n.f0.toString())==1)
            return "int";
        else{
            System.out.println("ERROR: invalid integer: "+ n.f0.toString());
            throw new ParseException("ERROR");
        }
    }

    /**
     * f0 -> "true"
     */
    public String visit(TrueLiteral n, String argu) throws Exception {
        return "boolean";
    }

    /**
     * f0 -> "false"
     */
    public String visit(FalseLiteral n, String argu) throws Exception {
        return "boolean";
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    public String visit(Identifier n, String argu) throws Exception {
        return n.f0.toString();
    }

    /**
     * f0 -> "this"
     */
    public String visit(ThisExpression n, String argu) throws Exception {
        return n.f0.toString();
    }

    /**
     * f0 -> "new"
     * f1 -> "int"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    public String visit(ArrayAllocationExpression n, String argu) throws Exception {
        String retStr="new int []";
//        n.f0.accept(this, argu);
//        n.f1.accept(this, argu);
//        n.f2.accept(this, argu);
        String[] crop = argu.split("[.]");
        String arg = n.f3.accept(this, argu);
        if(!arg.equals("int")&&!Helper.typeOfVariable(crop[0],argu,"int",arg) ){
            System.out.println("ERROR: incompatible types: array index is not int");
//            System.exit(1);
            throw new ParseException("ERROR");
        }
//        n.f4.accept(this, argu);
        return retStr;
    }

    /**
     * f0 -> "new"
     * f1 -> Identifier()
     * f2 -> "("
     * f3 -> ")"
     */
    public String visit(AllocationExpression n, String argu) throws Exception {
//        n.f0.accept(this, argu);
        String name = n.f1.accept(this, argu);
        String retStr= "new " + name + " ()";
//        if(!name.equals("int")&&!name.equals("int[]")&&!name.equals("boolean")) and is not any class is error
//        n.f2.accept(this, argu);
//        n.f3.accept(this, argu);
        return retStr;
    }

    /**
     * f0 -> "!"
     * f1 -> PrimaryExpression()
     */
    public String visit(NotExpression n, String argu) throws Exception {
        String retStr="boolean";
        n.f0.accept(this, argu);
        String[] crop = argu.split("[.]");
        String arg = n.f1.accept(this, argu);
        if(!arg.equals("boolean")&&!Helper.typeOfVariable(crop[0],argu,"boolean",arg) ){
            System.out.println("ERROR: not boolean clause");
            throw new ParseException("ERROR");
        }
        return retStr;
    }

    /**
     * f0 -> "("
     * f1 -> Expression()
     * f2 -> ")"
     */
    public String visit(BracketExpression n, String argu) throws Exception {
        String retStr=null;
        n.f0.accept(this, argu);
        String name = n.f1.accept(this, argu);
        retStr=name;
//        System.exit(1);
        n.f2.accept(this, argu);
        return retStr;
    }
}
