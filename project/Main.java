import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception{
        List<String> files;
        File folder;
        //read arguments
        for(int i=0; i<args.length;i++){
            folder = new File(args[i]);
            //if argument is a directory check all java files it contains
            if(folder.isDirectory()) {
                files = readDir(args[i]);
                for (int j=0; j<files.size();j++)
                    checkFile(files.get(j));
            }
            //if argument is file check it
            else if(folder.isFile())
                checkFile(args[i]);
        }

    }

    //check the program for semantic errors
    public static void checkFile(String filename) throws Exception{
        System.out.println("\u001B[33mChecking file " + filename + "\u001B[0m");
        CheckProgram.evaluate(filename);
//        printList(readAllLines(filename.substring(0, filename.length() - 5) + ".out"));
        (new Scanner(System.in)).nextLine();        //it exists to give time to the programmer to check if the output is correct
        System.out.println();
    }

    //read directory and store all java files to check them later
    public static List<String> readDir(String arg){
        List<String> files = new ArrayList<>();
        File folder = new File(arg);
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                if(file.getName().endsWith(".java")){
                    files.add(file.getPath());
                }
            }
            else if(file.isDirectory()) {
                files.addAll(readDir(file.getPath()));
            }
        }
        return files;
    }

    //read a file
    public static List<String> readAllLines(String fileName) {
        List<String> result = new ArrayList<>();
        try {
            result.addAll(Files.readAllLines(Paths.get(fileName)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    //print a list (used to print a read file)
    public static void printList(List<String> list){
        if(list==null)
            return;
        for(int i=0; i<list.size(); i++)
            System.out.println(list.get(i));
    }
}