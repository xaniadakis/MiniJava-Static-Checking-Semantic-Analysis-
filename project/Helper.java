import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Helper {

    public static void printOutput() throws Exception
    {
        List<String> table;
        String[] result;
        String[] name;
        String[] tmp;
        int[] variableCounter = new int[MyVisitor.classes.size()];
        Arrays.fill(variableCounter, 0);
        int[] methodCounter = new int[MyVisitor.classes.size()];
        Arrays.fill(methodCounter, 0);
        int booleanSpace = 1;
        int integerSpace = 4;
        int pointerSpace = 8;
        int vCounter = 0;
        int mCounter = 0;
        for(int i=0;i<MyVisitor.classes.size();i++)
        {
            table = new ArrayList<String>(MyVisitor.symbolTable.get(MyVisitor.classes.get(i)).values());
            if(!table.isEmpty())
            {
                name = MyVisitor.classes.get(i).split("\\s");
                //System.out.println(table);
                vCounter = 0;
                mCounter = 0;
                if(name.length>1) {
//                        System.out.println(eval.classes.get(i));
//                        System.out.println(eval.classes.indexOf(name[2]) + " found " + name[2]);
                    if(name[1].equals("extends"))
                    {
                        int index = MyVisitor.classes.indexOf(name[2]);
                        if(index==-1)
                            for(int k=0;k<MyVisitor.classes.size();k++)
                            {
                                tmp = MyVisitor.classes.get(k).split("\\s");
                                if(tmp.length>2)
                                    if(tmp[0].equals(name[2]))
                                        index = k;
                            }
                        if(index==-1) {
                            System.out.println("ERROR: class being extended does not exist");
//                            System.exit(1);
                            throw new ParseException("ERROR");
                        }
                        vCounter = variableCounter[index];
                        mCounter = methodCounter[index];
                    }
                }
                for(int j=0;j<table.size();j++)
                {
                    result = table.get(j).split("\\s");
                    if(!result[0].equals("method"))
                        System.out.println(name[0] + "." + result[1] + " : " + vCounter);
                    else {
                        if(name.length>1)
                        {
                            if(name[1].equals("extends"))
                            {
//                                    System.out.println(eval.symbolTable.get(name[2]));
                                String ar = null;
                                List<String> temp;
                                if(MyVisitor.symbolTable.get(name[2])==null) {
                                    for (int k = 0; k < MyVisitor.classes.size(); k++) {
                                        tmp = MyVisitor.classes.get(k).split("\\s");
                                        if (tmp.length > 2)
                                            if (tmp[0].equals(name[2]))
                                                ar = MyVisitor.classes.get(k);
                                    }
                                    if(ar!=null)
                                        temp = new ArrayList<String>(MyVisitor.symbolTable.get(ar).values());
                                    else
                                        continue;
                                }else
                                    temp = new ArrayList<String>(MyVisitor.symbolTable.get(name[2]).values());
                                int found=0;
                                for(int k=0;k<temp.size();k++)
                                {
                                    tmp = temp.get(k).split("\\s");
                                    if(tmp.length>2)
                                        if(tmp[2].equals(result[2]))
                                            found=1;
                                }
                                if(found==0) {
                                    System.out.println(name[0] + "." + result[2] + " : " + mCounter);
                                    mCounter += pointerSpace;
                                }
                            }
                        }
                        else {
                            System.out.println(name[0] + "." + result[2] + " : " + mCounter);
                            mCounter += pointerSpace;
                        }
                    }
//                        System.out.println(eval.symbolTables.get(i) + "." + result[0] + " " + result[1] + " : " + counter);
                    if(!result[0].equals("method")) {
                        if (result[0].equals("int"))
                            vCounter += integerSpace;
                        else if (result[0].equals("boolean"))
                            vCounter += booleanSpace;
                        else
                            vCounter += pointerSpace;
//                        System.out.println(counter);
                    }
                }
                variableCounter[i] = vCounter;
                methodCounter[i] = mCounter;
            }
            table.clear();
        }
    }

    public static void doubleDeclarationCheck() throws Exception
    {
        String[] tmp;
        //check if a variable or method is declared more than once
        for(int i = 0; i< MyVisitor.symbolTables.size(); i++)
        {
            List<String> temp1;
            temp1 = new ArrayList<String>(MyVisitor.symbolTable.get(MyVisitor.symbolTables.get(i)).values());
            for(int j=0;j<temp1.size();j++)
            {
                int occurrences = Collections.frequency(temp1, temp1.get(j));
                String[] temp2 = temp1.get(j).split("\\s");
                int found = 0;
                for (int k = 0; k < temp1.size(); k++) {
                    tmp = temp1.get(k).split("\\s");
                    if (tmp[1].equals(temp2[1]) && !tmp[0].equals("method") && !temp2[0].equals("method")) {
//                            System.out.println("Ey " + temp1.get(k) + " " + temp1.get(j));
                        found++;
                    }
                    if(found==0 && tmp.length>2 && temp2.length>2)
                        if (tmp[2].equals(temp2[2]) && tmp[0].equals("method") && temp2[0].equals("method")) {
//                                System.out.println("Ey " + temp1.get(k) + " " + temp1.get(j));
                            found++;
                        }
                }
                if (occurrences > 1 || found > 1) {
                    System.out.println("ERROR: declared more than once");
//                    System.exit(1);
                    throw new ParseException("ERROR");
                }

            }
            //System.out.println(eval.symbolTables.get(i));
        }
        //check if a class is declared more than once
        for(int i = 0; i< MyVisitor.classes.size(); i++)
        {
            String[] temp2 = MyVisitor.classes.get(i).split("\\s");
            int occurrences = Collections.frequency(MyVisitor.classes, MyVisitor.classes.get(i));
            int found = 0;
            for (int k = 0; k < MyVisitor.classes.size(); k++) {
                tmp = MyVisitor.classes.get(k).split("\\s");
                if (tmp[0].equals(temp2[0]))
                    found++;
            }
            if (occurrences > 1 || found > 1) {
                System.out.println("ERROR: class declared more than once");
//                System.exit(1);
                throw new ParseException("ERROR");
            }
        }
    }

    public static void methodsCheck() throws Exception
    {
        //check methods (for right parameters and return type if overriding, for declared return type and actual return value)
        String[] tmp;
        for(int i = 0; i< MyVisitor.methods.size(); i++)
        {
            int retsuccess = 0;
            String[] method = MyVisitor.methods.get(i).split("/");
            if(method[1].equals(method[2])||(isAssignable(method[1],method[2])==1))
                retsuccess++;
            String[] tmp5 = method[2].split("\\s");
            if(tmp5.length>2 && tmp5[0].equals("new")) {
                if(method[1].equals(tmp5[1])||(isAssignable(method[1],tmp5[1])==1))
                    retsuccess++;
                if(tmp5[2].equals("[]"))
                    if(method[1].equals(tmp5[1]+"[]"))
                        retsuccess++;
            }
//                System.out.println(method[0]);
            //check for declared return type and actual return type
//                System.out.println("methodMan '" + method[0] + "' " + method[1]+" "+method[2]);
            if(method[1].equals("int")){
                if(method[2].equals("int")){
                    retsuccess = 1;
                }
            }
            if(method[1].equals("boolean")){
                if(method[2].equals("boolean")){
                    retsuccess = 1;
                }
            }
            if(retsuccess==0)
            {
                tmp = method[0].split("[.]");
                String tmp2 = tmp[0];
                List<String> temp2 = new ArrayList<String>(MyVisitor.symbolTable.get(method[0]).values());
                List<String> temp3 = new ArrayList<String>(MyVisitor.symbolTable.get(tmp2).values());
                int found = 0;
                //search the variables declared inside the method
                for(int k=0;k<temp2.size();k++)
                {
                    tmp = (temp2.get(k)).split("\\s");
                    if(tmp.length>1)
                        if(method[2].equals(tmp[1]) && !tmp[0].equals("method"))
                        {
//                            System.out.println(temp2.get(k));
                            found++;
                            if(method[1].equals(tmp[0]))
                                retsuccess = 1;
                        }
                    tmp = null;
                }
                //search the variables declared inside the class
                if(found==0)
                {
                    for(int k=0;k<temp3.size();k++)
                    {
                        tmp = (temp3.get(k)).split("\\s");
                        if(tmp.length>1)
                            if(method[2].equals(tmp[1]) && !tmp[0].equals("method"))
                            {
//                                System.out.println(temp3.get(k));
                                found++;
                                if(method[1].equals(tmp[0]))
                                    retsuccess = 1;
                            }
                        tmp = null;
                    }
                }
                //search the variables declared in the parent class if it exists
                int parentIndex = -1;
                if(found==0)
                {
                    tmp = method[0].split("[.]");
                    String[] tmp3 = tmp[0].split("\\s");
                    String[] tmp4;
                    if (tmp3.length > 2)    //else there is no parent class
                    {
                        parentIndex = MyVisitor.classes.indexOf(tmp3[2]);
                        if(parentIndex==-1)
                            for(int k = 0; k< MyVisitor.classes.size(); k++)
                            {
                                tmp4 = MyVisitor.classes.get(k).split("\\s");
                                if(tmp4.length>2)
                                    if(tmp4[0].equals(tmp3[2]))
                                        parentIndex = k;
                            }
                        if(parentIndex==-1)
                            break;
                        temp3 = new ArrayList<String>(MyVisitor.symbolTable.get(MyVisitor.classes.get(parentIndex)).values());
                        for(int k=0;k<temp3.size();k++)
                        {
                            tmp = (temp3.get(k)).split("\\s");
                            if(tmp.length>1)
                                if(method[2].equals(tmp[1]) && !tmp[0].equals("method"))
                                {
                                    found++;
                                    if(method[1].equals(tmp[0]))
                                        retsuccess = 1;
                                }
                            tmp = null;
                        }
                    }
                }
                //search deeper in ancestors
                while(found==0)
                {
                    if(parentIndex!=-1)
                    {
                        String[] tmp3 = MyVisitor.classes.get(parentIndex).split("\\s");
//                            System.out.println(eval.classes.get(parentIndex));
                        String[] tmp4;
                        if (tmp3.length > 2)    //else there is no parent class
                        {
                            parentIndex = MyVisitor.classes.indexOf(tmp3[2]);    //parentOfparent
                            if (parentIndex == -1)
                                for (int k = 0; k < MyVisitor.classes.size(); k++) {
                                    tmp4 = MyVisitor.classes.get(k).split("\\s");
                                    if (tmp4.length > 2)
                                        if (tmp4[0].equals(tmp3[2]))
                                            parentIndex = k;
                                }
                            if (parentIndex == -1)
                                break;
                            temp3 = new ArrayList<String>(MyVisitor.symbolTable.get(MyVisitor.classes.get(parentIndex)).values());
                            for (int k = 0; k < temp3.size(); k++) {
                                tmp = (temp3.get(k)).split("\\s");
                                if (tmp.length > 1)
                                    if(method[2].equals(tmp[1]) && !tmp[0].equals("method"))
                                    {
                                        found++;
                                        if(method[1].equals(tmp[0]))
                                            retsuccess = 1;
                                    }
                                tmp = null;
                            }
                        }
                        else break;
                    }
                    else break;
                }
            }
            if(retsuccess==0) {
                System.out.println("ERROR: wrong return type ");
//                System.exit(1);
                throw new ParseException("ERROR");
            }
            //end of return type check

            //for overriding, check if return type is equal to parent method, and if parameters are the same
            tmp = method[0].split("[.]");
            String parentMethod = tmp[1];
            tmp = tmp[0].split("\\s");
            int paramsuccess = 1;
            retsuccess = 1;
            if(tmp.length>1)            //if there exists a parent
            {
                if(tmp[1].equals("extends"))
                {
                    String parentClass = tmp[2];
                    String par2 = parentClass + "." + parentMethod;
//                        System.out.println("Ey " + parentClass + "." + parentMethod);
                    for(int j = 0; j< MyVisitor.methods.size(); j++)
                    {
                        String[] temp4 = MyVisitor.methods.get(j).split("/");
                        if(temp4[0].equals(par2)){          //found parent method which is getting overriden
                            paramsuccess = 0;
                            retsuccess = 0;
                            List<String> myParameters = new ArrayList<String>(MyVisitor.parameters.get(method[0]).values());
                            List<String> parentParameters = new ArrayList<String>(MyVisitor.parameters.get(temp4[0]).values());
                            if(myParameters.size()==parentParameters.size()){
                                for(int k=0;k<myParameters.size();k++)
                                {
                                    String[] myprm = myParameters.get(k).split("\\s");
                                    String[] prntprm = parentParameters.get(k).split("\\s");
//                                        System.out.println("PARAMETERS " + myParameters.get(k)+ " " + parentParameters.get(k));
                                    if(myprm[0].equals(prntprm[0]))
                                        paramsuccess++;
                                }
                                if(paramsuccess<myParameters.size())
                                    paramsuccess=0;
                                else
                                    paramsuccess=1;
                            }
                            if(method[1].equals(temp4[1]))
                                retsuccess=1;
                            break;
                        }
                        else{
                            String[] temp5 = temp4[0].split("[.]");
                            String[] temp6 = temp5[0].split("\\s");
                            String par = temp6[0] + "." + temp5[1];
                            if(par.equals(par2)){           //found parent method which is getting overriden
                                paramsuccess = 0;
                                retsuccess = 0;
                                List<String> myParameters = new ArrayList<String>(MyVisitor.parameters.get(method[0]).values());
                                List<String> parentParameters = new ArrayList<String>(MyVisitor.parameters.get(temp4[0]).values());
                                if(myParameters.size()==parentParameters.size()){
                                    for(int k=0;k<myParameters.size();k++)
                                    {
                                        String[] myprm = myParameters.get(k).split("\\s");
                                        String[] prntprm = parentParameters.get(k).split("\\s");
                                        if(myprm[0].equals(prntprm[0]))
                                            paramsuccess++;
                                    }
                                    if(paramsuccess<myParameters.size())
                                        paramsuccess=0;
                                    else
                                        paramsuccess=1;
                                }
                                if(method[1].equals(temp4[1]))
                                    retsuccess=1;
                                break;
                            }
                        }
                    }
                }
            }
            if(paramsuccess==0){
                System.out.println("ERROR: overriding method has wrong parameters");
//                System.exit(1);
                throw new ParseException("ERROR");
            }
            if(retsuccess==0){
                System.out.println("ERROR: overriding method has wrong return type");
//                System.exit(1);
                throw new ParseException("ERROR");
            }

//                System.out.println("class " + tmp[0] + " length = " + tmp.length);
            //end of overriding check
        }
    }

    public static int isInt(String str) throws Exception
    {
        int isint = 1;
        try{Integer.parseInt(str);}
        catch(Exception ex){isint=0;}
        return isint;
    }

    public static int isAssignable(String superclass, String subclass) throws Exception
    {
//        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!! " + superclass + " " + subclass);
        String[] splitsub = subclass.split("\\s");
        if(splitsub[0].equals("new")){
            if(superclass.equals("int[]")&&splitsub[1].equals("int")&&splitsub[2].equals("[]")){
                return 1;}
            if(isAssignable(superclass,splitsub[1])==1)
                return 1;
        }
        if(superclass.equals(subclass))
            return 1;
        int subIndex = -1;
        int superIndex = -1;
        int parentIndex = -1;
        int found=0;
        superIndex = MyVisitor.classes.indexOf(superclass);
        subIndex = MyVisitor.classes.indexOf(subclass);
        if(superIndex==-1)
            for(int k = 0; k< MyVisitor.classes.size(); k++)
            {
                String[] tmp = MyVisitor.classes.get(k).split("\\s");
                if(tmp.length>2)
                    if(tmp[0].equals(superclass))
                        superIndex = k;
            }
        if(subIndex==-1)
            for(int k = 0; k< MyVisitor.classes.size(); k++) {
                String[] tmp = MyVisitor.classes.get(k).split("\\s");
                if (tmp.length > 2)
                    if (tmp[0].equals(subclass))
                        subIndex = k;
            }
        /*if(superIndex==-1 && subIndex==-1) {
            System.out.println(superclass.class.isAssignableFrom(subclass.class));
            if (superclass.class.isAssignableFrom(subclass.class))
                return 1;
            else
                return 0;
        }
        else*/ if(superIndex!=-1 && subIndex!=-1){
        String[] tmp3 = MyVisitor.classes.get(subIndex).split("\\s");
        String[] tmp4;
        if (tmp3.length>2)    //else there is no parent class
        {
            parentIndex = MyVisitor.classes.indexOf(tmp3[2]);
            if(parentIndex==-1)
                for(int k = 0; k< MyVisitor.classes.size(); k++)
                {
                    tmp4 = MyVisitor.classes.get(k).split("\\s");
                    if(tmp4.length>2)
                        if(tmp4[0].equals(tmp3[2])) {
                            parentIndex = k;
                            if(tmp4[0].equals(superclass))
                                found=1;
                        }
                }
        } else return 0;
//            System.out.println("1here " + eval.classes.get(parentIndex) + found);
        while(found==0)
        {
            if(parentIndex!=-1)
            {
                tmp3 = MyVisitor.classes.get(parentIndex).split("\\s");
//                    System.out.println("2here " + eval.classes.get(parentIndex) + found);
                if (tmp3.length>2)    //else there is no parent class
                {
                    parentIndex = MyVisitor.classes.indexOf(tmp3[2]);    //parentOfparent
                    if (parentIndex == -1)
                        for (int k = 0; k < MyVisitor.classes.size(); k++) {
                            tmp4 = MyVisitor.classes.get(k).split("\\s");
                            if (tmp4.length > 2)
                                if (tmp4[0].equals(tmp3[2])) {
                                    parentIndex = k;
                                    if (tmp4[0].equals(superclass))
                                        found = 1;
                                }
                                else if(MyVisitor.classes.get(k).equals(superclass))
                                    found=1;
                        }
                    if (parentIndex == -1)
                        break;
                }
                else if(MyVisitor.classes.get(parentIndex).equals(superclass))
                    found=1;
                else break;
            }
            else break;
        }
    }
//        System.out.println("foundClass " + found);
        return found;

    }

    public static boolean typeOfVariable(String myClass, String myMethod, String type, String variable) throws Exception
    {
//        System.out.println("typeOfVariable " + myClass + " " + myMethod + " " + type+ " "+ variable);
        if(type==null)
            return false;
        String[] tmp;
        String tmp2 = myClass;
        int methodcall = 0;
        if(myMethod!=null) {
            tmp = myMethod.split("[.]");
            tmp2 = tmp[0];
        }
        if(variable.equals("this"))
        {
            String[] c = tmp2.split("\\s");
            if(c.length>1) {
                if (c[0].equals(type))
                    return true;
            }
            else {
                if(tmp2.equals(type))
                    return true;
            }
        }
        List<String> temp3 = new ArrayList<String>(MyVisitor.symbolTable.get(tmp2).values());
        int found = 0;
        //search the variables declared inside the method
        if(myMethod!=null)
        {
            List<String> temp2 = new ArrayList<String>(MyVisitor.symbolTable.get(myMethod).values());
            for (int k = 0; k < temp2.size(); k++) {
                tmp = (temp2.get(k)).split("\\s");
                if (tmp.length > 1)
                    if (variable.equals(tmp[1])) {
                        found++;
                        if(type.equals(tmp[0])||(type.equals("new int []")&&tmp[0].equals("int[]")))
                            return true;
                        else if(isAssignable(tmp[0],type)==1)
                            return true;
                        else{
                            String[] type1 = type.split("\\s");
                            if( type1.length>2 && type1[0].equals("new") && ( type1[1].equals(tmp[0]) || (isAssignable(tmp[0], type1[1])==1) ) )
                                return true;
                        }
                    }
                tmp = null;
            }
        }
        //search the variables declared inside the class
        if(found==0)
        {
            for(int k=0;k<temp3.size();k++)
            {
                tmp = (temp3.get(k)).split("\\s");
                if(tmp.length>1)
                    if (variable.equals(tmp[1])) {
                        found++;
                        if(type.equals(tmp[0])||(type.equals("new int []")&&tmp[0].equals("int[]")))
                            return true;
                        else if(isAssignable(tmp[0],type)==1)
                            return true;
                        else{
                            String[] type1 = type.split("\\s");
                            if( type1.length>2 && type1[0].equals("new") && ( type1[1].equals(tmp[0]) || (isAssignable(tmp[0], type1[1])==1) ) )
                                return true;
                        }
                    }
                tmp = null;
            }
        }

        //search the variables declared in the parent class if it exists
        int parentIndex = -1;
        if(found==0)
        {
            tmp = myMethod.split("[.]");
            String[] tmp3 = tmp[0].split("\\s");
            String[] tmp4;
            if (tmp3.length > 2)    //else there is no parent class
            {
                parentIndex = MyVisitor.classes.indexOf(tmp3[2]);
                if(parentIndex==-1)
                    for(int k = 0; k< MyVisitor.classes.size(); k++)
                    {
                        tmp4 = MyVisitor.classes.get(k).split("\\s");
                        if(tmp4.length>2)
                            if(tmp4[0].equals(tmp3[2]))
                                parentIndex = k;
                    }
                temp3 = new ArrayList<String>(MyVisitor.symbolTable.get(MyVisitor.classes.get(parentIndex)).values());
                for(int k=0;k<temp3.size();k++)
                {
                    tmp = (temp3.get(k)).split("\\s");
                    if(tmp.length>1)
                        if (variable.equals(tmp[1])) {
                            found++;
                            if(type.equals(tmp[0])||(type.equals("new int []")&&tmp[0].equals("int[]")))
                                return true;
                            else if(isAssignable(tmp[0],type)==1)
                                return true;
                            else{
                                String[] type1 = type.split("\\s");
                                if( type1.length>2 && type1[0].equals("new") && ( type1[1].equals(tmp[0]) || (isAssignable(tmp[0], type1[1])==1) ) )
                                    return true;
                            }
                        }
                    tmp = null;
                }
            }
        }
        //search deeper in ancestors
        while(found==0)
        {
            if(parentIndex!=-1)
            {
                String[] tmp3 = MyVisitor.classes.get(parentIndex).split("\\s");
//                            System.out.println(eval.classes.get(parentIndex));
                String[] tmp4;
                if (tmp3.length > 2)    //else there is no parent class
                {
                    parentIndex = MyVisitor.classes.indexOf(tmp3[2]);    //parentOfparent
                    if (parentIndex == -1)
                        for (int k = 0; k < MyVisitor.classes.size(); k++) {
                            tmp4 = MyVisitor.classes.get(k).split("\\s");
                            if (tmp4.length > 2)
                                if (tmp4[0].equals(tmp3[2]))
                                    parentIndex = k;
                        }
                    if (parentIndex == -1)
                        break;
                    temp3 = new ArrayList<String>(MyVisitor.symbolTable.get(MyVisitor.classes.get(parentIndex)).values());
                    for (int k = 0; k < temp3.size(); k++) {
                        tmp = (temp3.get(k)).split("\\s");
                        if(tmp.length>1)
                            if (variable.equals(tmp[1])) {
                                found++;
                                if(type.equals(tmp[0])||(type.equals("new int []")&&tmp[0].equals("int[]")))
                                    return true;
                                else if(isAssignable(tmp[0],type)==1)
                                    return true;
                                else{
                                    String[] type1 = type.split("\\s");
                                    if( type1.length>2 && type1[0].equals("new") && ( type1[1].equals(tmp[0]) || (isAssignable(tmp[0], type1[1])==1) ) )
                                        return true;
                                }
                            }
                        tmp = null;
                    }
                }
                else break;
            }
            else break;
        }
        return false;

    }

    public static String getTypeOfVariable(String myClass, String myMethod, String variable) throws Exception
    {
//        System.out.println("getTypeOfVariable " + myClass + " " + myMethod + " " + " "+ variable);
        String[] tmp;
        String tmp2 = myClass;
        int found = 0;
        String[] tmp5 = variable.split("\\s");
//        if(tmp5.length>2 && tmp5[0].equals("new")) {
//            variable = tmp5[1];
//            System.out.println(tmp5[0]+" "+tmp5[1]+" "+tmp5[2]+" "+variable);
//            if(tmp5[2].equals("[]"))
//                variable += "[]";
//            return variable;
//        }
        if(myMethod!=null) {
            tmp = myMethod.split("[.]");
            tmp2 = tmp[0];
        }
        if(variable.equals("this"))
        {
            String[] c = tmp2.split("\\s");
            if(c.length>1) {
                return c[0];
            }
            else {
                return tmp2;
            }
        }
        for(int i=0; i<MyVisitor.classes.size();i++)
        {
            if(variable.equals(MyVisitor.classes.get(i)))
                return variable;
        }
        List<String> temp3 = new ArrayList<String>(MyVisitor.symbolTable.get(tmp2).values());
        //search the variables declared inside the method
        if(myMethod!=null)
        {
            List<String> temp2 = new ArrayList<String>(MyVisitor.symbolTable.get(myMethod).values());
            for (int k = 0; k < temp2.size(); k++) {
                tmp = (temp2.get(k)).split("\\s");
                if (tmp.length > 1)
                    if (variable.equals(tmp[1])) {
                        found++;
                        return tmp[0];
                    }
                tmp = null;
            }
        }
        //search the variables declared inside the class
        if(found==0)
        {
            for(int k=0;k<temp3.size();k++)
            {
                tmp = (temp3.get(k)).split("\\s");
                if(tmp.length>1)
                    if (variable.equals(tmp[1])) {
                        found++;
                        return tmp[0];
                    }
                tmp = null;
            }
        }

        //search the variables declared in the parent class if it exists
        int parentIndex = -1;
        if(found==0)
        {
            tmp = myMethod.split("[.]");
            String[] tmp3 = tmp[0].split("\\s");
            String[] tmp4;
            if (tmp3.length > 2)    //else there is no parent class
            {
                parentIndex = MyVisitor.classes.indexOf(tmp3[2]);
                if(parentIndex==-1)
                    for(int k = 0; k< MyVisitor.classes.size(); k++)
                    {
                        tmp4 = MyVisitor.classes.get(k).split("\\s");
                        if(tmp4.length>2)
                            if(tmp4[0].equals(tmp3[2]))
                                parentIndex = k;
                    }
                temp3 = new ArrayList<String>(MyVisitor.symbolTable.get(MyVisitor.classes.get(parentIndex)).values());
                for(int k=0;k<temp3.size();k++)
                {
                    tmp = (temp3.get(k)).split("\\s");
                    if(tmp.length>1)
                        if (variable.equals(tmp[1])) {
                            found++;
                            return tmp[0];
                        }
                    tmp = null;
                }
            }
        }
        //search deeper in ancestors
        while(found==0)
        {
            if(parentIndex!=-1)
            {
                String[] tmp3 = MyVisitor.classes.get(parentIndex).split("\\s");
//                            System.out.println(eval.classes.get(parentIndex));
                String[] tmp4;
                if (tmp3.length > 2)    //else there is no parent class
                {
                    parentIndex = MyVisitor.classes.indexOf(tmp3[2]);    //parentOfparent
                    if (parentIndex == -1)
                        for (int k = 0; k < MyVisitor.classes.size(); k++) {
                            tmp4 = MyVisitor.classes.get(k).split("\\s");
                            if (tmp4.length > 2)
                                if (tmp4[0].equals(tmp3[2]))
                                    parentIndex = k;
                        }
                    if (parentIndex == -1)
                        break;
                    temp3 = new ArrayList<String>(MyVisitor.symbolTable.get(MyVisitor.classes.get(parentIndex)).values());
                    for (int k = 0; k < temp3.size(); k++) {
                        tmp = (temp3.get(k)).split("\\s");
                        if(tmp.length>1)
                            if (variable.equals(tmp[1])) {
                                found++;
                                return tmp[0];
                            }
                        tmp = null;
                    }
                }
                else break;
            }
            else break;
        }
        return "";

    }

    public static String getTypeOfMessage(String classname, String message) throws Exception
    {
        //give return type of method call and check for semantic correctness
        String[] s = message.split("[.]");
        String[] tmp;
        String method1[] = s[s.length-1].split("\\(",2);

//        System.out.println("is==?? "+classname+" "+s[0]);
//        for(int i=0;i<s.length;i++)
//            System.out.println("s["+i+"] = "+s[i]);
//        for(int i=0;i<method1.length;i++)
//            System.out.println("method1["+i+"] = "+method1[i]);
        if(method1.length<2)
            return "";
        else
            if(!method1[1].endsWith(")"))
                return "";
        String item;
        String cl = null;
        if(s.length<2)
            return "";

        String[] ncl = s[0].split("\\s");
        if(ncl[0].equals("new")&&ncl[2].equals("()"))
            cl=ncl[1];
//        System.out.println("is a method");
        //find class of method
//        int idx = 0;
////        while(true)
////        {
////            if(idx>s.length-2)
////                break;
//            item = s[idx];
//            if(item.equals("this"))
//            {
//                String[] c = myClass.split("\\s");
//                if (c.length > 1)
//                    cl = c[0];
//                else
//                    cl = myClass;
//            }
//            else
//            {
//                List<String> temp3 = new ArrayList<String>(MyVisitor.symbolTable.get(tmp2).values());
//                int found = 0;
//                //search the variables declared inside the method
//                if (myMethod != null)
//                {
//                    List<String> temp2 = new ArrayList<String>(MyVisitor.symbolTable.get(myMethod).values());
//                    for (int k = 0; k < temp2.size(); k++)
//                    {
//                        tmp = (temp2.get(k)).split("\\s");
//                        if (tmp.length > 1)
//                            if (item.equals(tmp[1]))
//                            {
//                                found++;
//                                cl = tmp[0];
////                            if(type.equals(tmp[0]))
////                                return true;
//                            }
//                        tmp = null;
//                    }
//                }
//                //search the variables declared inside the class
//                if (found == 0)
//                {
//                    for (int k = 0; k < temp3.size(); k++)
//                    {
//                        tmp = (temp3.get(k)).split("\\s");
//                        if (tmp.length > 1)
//                            if (item.equals(tmp[1]))
//                            {
//                                found++;
//                                cl = tmp[0];
////                            if(type.equals(tmp[0]))
////                                return true;
//                            }
//                        tmp = null;
//                    }
//                }
//                //search the variables declared in the parent class if it exists
//                int parentIndex = -1;
//                if (found == 0)
//                {
//                    tmp = myMethod.split("[.]");
//                    String[] tmp3 = tmp[0].split("\\s");
//                    String[] tmp4;
//                    if (tmp3.length > 2)    //else there is no parent class
//                    {
//                        parentIndex = MyVisitor.classes.indexOf(tmp3[2]);
//                        if (parentIndex == -1)
//                            for (int k = 0; k < MyVisitor.classes.size(); k++)
//                            {
//                                tmp4 = MyVisitor.classes.get(k).split("\\s");
//                                if (tmp4.length > 2)
//                                    if (tmp4[0].equals(tmp3[2]))
//                                        parentIndex = k;
//                            }
//                        temp3 = new ArrayList<String>(MyVisitor.symbolTable.get(MyVisitor.classes.get(parentIndex)).values());
//                        for (int k = 0; k < temp3.size(); k++)
//                        {
//                            tmp = (temp3.get(k)).split("\\s");
//                            if (tmp.length > 1)
//                                if (item.equals(tmp[1]))
//                                {
//                                    found++;
//                                    cl = tmp[0];
////                                if(type.equals(tmp[0]))
////                                    return true;
//                                }
//                            tmp = null;
//                        }
//                    }
//                }
//                //search deeper in ancestors
//                while (found == 0)
//                {
//                    if (parentIndex != -1)
//                    {
//                        String[] tmp3 = MyVisitor.classes.get(parentIndex).split("\\s");
////                            System.out.println(eval.classes.get(parentIndex));
//                        String[] tmp4;
//                        if (tmp3.length > 2)    //else there is no parent class
//                        {
//                            parentIndex = MyVisitor.classes.indexOf(tmp3[2]);    //parentOfparent
//                            if (parentIndex == -1)
//                                for (int k = 0; k < MyVisitor.classes.size(); k++)
//                                {
//                                    tmp4 = MyVisitor.classes.get(k).split("\\s");
//                                    if (tmp4.length > 2)
//                                        if (tmp4[0].equals(tmp3[2]))
//                                            parentIndex = k;
//                                }
//                            if (parentIndex == -1)
//                                break;
//                            temp3 = new ArrayList<String>(MyVisitor.symbolTable.get(MyVisitor.classes.get(parentIndex)).values());
//                            for (int k = 0; k < temp3.size(); k++)
//                            {
//                                tmp = (temp3.get(k)).split("\\s");
//                                if (tmp.length > 1)
//                                    if (item.equals(tmp[1]))
//                                    {
//                                        found++;
//                                        cl = tmp[0];
////                                    if(type.equals(tmp[0]))
////                                        return true;
//                                    }
//                                tmp = null;
//                            }
//                        }
//                        else
//                            break;
//                    }
//                    else
//                        break;
//                }
//            }
////        }
//        System.out.println("class = " + cl);
//        for(int i=0;i<s.length;i++)
//            System.out.printf(s[i]+" ");
//        System.out.println();
//        System.out.println(cl+"."+method1[0]);

        List<String> temp3 = null;
        if(cl==null)
            cl = s[0];
        if(MyVisitor.classes.contains(cl)){
//            System.out.println("1st if");
            temp3 = new ArrayList<String>(MyVisitor.symbolTable.get(cl).values());
        }
        else{
//            System.out.println("3rd if");
            for(int i=0; i<MyVisitor.classes.size(); i++)
            {
                String[] temporary = MyVisitor.classes.get(i).split("\\s");
                if(temporary.length>2 && temporary[1].equals("extends") && temporary[0].equals(cl)){
                    cl = MyVisitor.classes.get(i);
                    temp3 = new ArrayList<String>(MyVisitor.symbolTable.get(cl).values());
                }
            }
        }

        if(temp3==null && MyVisitor.classes.contains(classname)) {
//            System.out.println("2nd if");
            temp3 = new ArrayList<String>(MyVisitor.symbolTable.get(classname).values());
            cl = classname;
        }

        //check parameters
//        System.out.println(message);
//        System.out.println(cl+"."+method1[0]);

        List<String> temp4 = null;
        if(MyVisitor.parameters.containsKey(cl+"."+method1[0]))
            temp4 = new ArrayList<String>(MyVisitor.parameters.get(cl+"."+method1[0]).values());
        else{
            //search if the parent class has the method, if class doesnt have one
            String[] n = cl.split("\\s");
            if(n.length>2 && n[1].equals("extends"))
            {
                String parentclass = null;
                String pc = n[2];
                while(true){
                    //first find parent class
                    if(MyVisitor.classes.contains(pc))
                        parentclass=pc;
                    else{
                        for(int i=0; i<MyVisitor.classes.size(); i++)
                        {
                            String[] temporary = MyVisitor.classes.get(i).split("\\s");
                            if(temporary.length>2 && temporary[1].equals("extends") && temporary[0].equals(pc)){
                                parentclass = MyVisitor.classes.get(i);
                                break;
                            }
                        }
                    }
                    //check if parent class has method
                    if(MyVisitor.parameters.containsKey(parentclass+"."+method1[0])) {
                        temp4 = new ArrayList<String>(MyVisitor.parameters.get(parentclass + "." + method1[0]).values());
                        break;
                    }
                    n = parentclass.split("\\s");
                    if(n.length>2 && n[1].equals("extends"))
                        pc = n[2];
                    else
                        break;
                }
            }
        }
        if(temp4==null){
            System.out.println("No "+s[s.length-1]+ " method");
//            System.exit(1);
            throw new ParseException("ERROR");
        }
        if(method1[1].endsWith(")")){
            method1[1] = method1[1].substring(0, method1[1].length()-1);
            String[] myParameters = method1[1].split(",");
            if(method1[1].equals("")&&(temp4.size()!=0)){
                System.out.println("ERROR: messageSend: wrong parameters: " + s[1]);
//                System.exit(1);
                throw new ParseException("ERROR");
            }else if(!method1[1].equals("")&&myParameters.length!=temp4.size()){
                System.out.println("ERROR: messageSend: wrong parameters: " + s[1] +" "+ myParameters.length+" "+temp4.size());
//                System.exit(1);
                throw new ParseException("ERROR");
            }
            for(int i=0;i<temp4.size();i++)
            {
//                System.out.println(myParameters[i] + " "+ temp4.get(i));
                String[] decParameter = temp4.get(i).split("\\s");
                if(!(myParameters[i].trim()).equals(decParameter[0].trim())){
                    System.out.println(decParameter[0].trim()+" "+myParameters[i].trim());
                    if(isAssignable(decParameter[0].trim(),myParameters[i].trim())!=1){
                        String[] crop = myParameters[i].trim().split("\\s");
                        if(crop.length>2 && crop[0].equals("new") && crop[2].equals("()") && (isAssignable(decParameter[0].trim(),crop[1])==1))
                            continue;
                        System.out.println("ERROR: messageSend: wrong parameters: " + s[1]);
//                        System.exit(1);
                        throw new ParseException("ERROR");
                    }
                }
            }
        }else{
            System.out.println("ERROR: messageSend: wrong parameters: " + s[1]);
//            System.exit(1);
            throw new ParseException("ERROR");
        }
        //
        if(temp3==null)
            return "";
        //find method
        int found = 0;
        item = method1[0];
        //in class
        for (int k = 0; k < temp3.size(); k++) {
            tmp = (temp3.get(k)).split("\\s");
            if (tmp.length > 2 && tmp[0].equals("method"))
                if (item.equals(tmp[2])) {
                    found++;
                    return tmp[1];
                }
            tmp = null;
        }
        //search the methods declared in the parent class if it exists
        int parentIndex = -1;
        String[] tmp4;
        if (found == 0) {
            String[] tmp3 = cl.split("\\s");
            if (tmp3.length > 2)    //else there is no parent class
            {
                parentIndex = MyVisitor.classes.indexOf(tmp3[2]);
                if (parentIndex == -1)
                    for (int k = 0; k < MyVisitor.classes.size(); k++) {
                        tmp4 = MyVisitor.classes.get(k).split("\\s");
                        if (tmp4.length > 2)
                            if (tmp4[0].equals(tmp3[2]))
                                parentIndex = k;
                    }
                temp3 = new ArrayList<String>(MyVisitor.symbolTable.get(MyVisitor.classes.get(parentIndex)).values());
                for (int k = 0; k < temp3.size(); k++) {
                    tmp = (temp3.get(k)).split("\\s");
                    if (tmp.length > 2 && tmp[0].equals("method"))
                        if (item.equals(tmp[2])) {
                            found++;
                            return tmp[1];
                        }
                    tmp = null;
                }
            }
        }
        //search deeper in ancestors
        while (found == 0) {
            if (parentIndex != -1) {
                String[] tmp3 = MyVisitor.classes.get(parentIndex).split("\\s");
//                            System.out.println(eval.classes.get(parentIndex));
                if (tmp3.length > 2)    //else there is no parent class
                {
                    parentIndex = MyVisitor.classes.indexOf(tmp3[2]);    //parentOfparent
                    if (parentIndex == -1)
                        for (int k = 0; k < MyVisitor.classes.size(); k++) {
                            tmp4 = MyVisitor.classes.get(k).split("\\s");
                            if (tmp4.length > 2)
                                if (tmp4[0].equals(tmp3[2]))
                                    parentIndex = k;
                        }
                    if (parentIndex == -1)
                        break;
                    temp3 = new ArrayList<String>(MyVisitor.symbolTable.get(MyVisitor.classes.get(parentIndex)).values());
                    for (int k = 0; k < temp3.size(); k++) {
                        tmp = (temp3.get(k)).split("\\s");
                        if (tmp.length > 2 && tmp[0].equals("method"))
                            if (item.equals(tmp[2])) {
                                found++;
                                return tmp[1];
                            }
                        tmp = null;
                    }
                } else break;
            } else break;
        }
        return "";

    }
}
