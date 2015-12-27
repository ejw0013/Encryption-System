import java.util.*;
import java.io.*;
import java.nio.file.*;

/**
  * The main class for allowing user operations.
  * @author: Erich Wu
  * @version: 11/15/2015
  *
  */
public class Interface{
  public static void main(String[] args)throws Exception{
    Scanner s = new Scanner(System.in);
    if(args.length == 0){
      System.out.println("Must be run with command line arguments");
      System.out.println("Available Command Line Arguments:");
      System.out.println("1. -enc : Encrypt a new file system");
      System.out.println("2. -acc : Access a file system");
      System.out.println("3. -upd : Update a file system(should be done after accessing a file)");
    }else if (args[0].equals("-enc")){
      firstSetup();
    }else if (args[0].equals("-upd")){
      updateSystem();
    }else if (args[0].equals("-acc")){
      accessFile();
    }else{
      System.out.println("You're doing command arguments wrong.");
    }
  }
  private static void firstSetup()throws Exception{
    Scanner s = new Scanner(System.in);
    System.out.println("\n==============================================================");
    System.out.println("                  Preparing for first setup... ");
    System.out.println("==============================================================\n");
    String t = "a";
    System.out.println("\nAre you ready to proceed?(yes/no)");
    t = s.nextLine().trim();
    while(!t.equalsIgnoreCase("yes") && !t.equalsIgnoreCase("no")){
      System.out.print("Please enter yes or no:");
      t = s.nextLine().trim();
    }
    if(t.equalsIgnoreCase("yes")){
      System.out.println("\nPlease enter the absolute root path of the file system: ");
      String root = s.nextLine().replaceAll("\\\\\"", "/").trim();
      System.out.println("\nPlease enter the file system password: ");
      String password = s.nextLine();
      System.out.println("\nPlease enter the file system pin: ");
      int pin = Integer.parseInt(s.nextLine().trim());
      int fileNumber = newFileSystemNum();
      ProjectNew.newFileSystem(root, fileNumber, password, pin);
    }
  }
  private static void accessFile()throws Exception{
    Scanner s = new Scanner(System.in);
    System.out.println("\n==============================================================");
    System.out.println("                  File Encryption Access ");
    System.out.println("==============================================================\n");
    String t = "a";
    System.out.println("\nAre you ready to proceed?(yes/no)");
    t = s.nextLine().trim();
    while(!t.equalsIgnoreCase("yes") && !t.equalsIgnoreCase("no")){
      System.out.print("Please enter yes or no:");
      t = s.nextLine().trim().toLowerCase();
    }
    if(t.equalsIgnoreCase("yes")){
      List<String> l = returnRootPaths();
      int fileN = l.size() + 1;
      if(fileN == 1){
        System.out.println("No Encrypted File Systems currently created");
        return;
      }
      System.out.println("\nAll Encrypted File Systems:\n");
      for(int i = 0; i < l.size(); i++){
        System.out.println("\t" + i + ". " + l.get(i));
      }
      while(fileN > l.size()){
        System.out.print("\nWhich file System would you like to access? ");
        fileN = Integer.parseInt(s.nextLine().trim());
      }
      l = FileHandler.getFiles(l.get(fileN));
      int file = l.size() + 1;
      System.out.println("\nAll Files In This File System: \n");
      for(int i = 0; i < l.size(); i++){
        System.out.println("\t" + i + ". " + l.get(i));
      }
      while(file > l.size()){
          System.out.print("\nWhich file would you like to access? ");
          file = Integer.parseInt(s.nextLine().trim());
      }
      String fileName = l.get(file);
      System.out.println("\nPlease enter the file system password: ");
      String password = s.nextLine();
      System.out.println("\nPlease enter the file system pin: ");
      int pin = Integer.parseInt(s.nextLine().trim());
      ProjectNew.access(fileName, ++fileN, password, pin);
    }
  }
  private static void updateSystem()throws Exception{
    Scanner s= new Scanner(System.in);
    System.out.println("\n==============================================================");
    System.out.println("                  File Encryption Updating... ");
    System.out.println("==============================================================\n");
    String t = "a";
    List<String> list = returnRootPaths();
    System.out.println("\nAre you ready to proceed?(yes/no)");
    t = s.nextLine().trim().toLowerCase();
    while(!t.equalsIgnoreCase("yes") && !t.equalsIgnoreCase("no")){
      System.out.print("Please enter yes or no:");
      t = s.nextLine().trim();
    }
    if(t.equals("yes")){
      int a = findAccessed();
      if(a != -1){
        System.out.println("\nDetected unencrypted copy in " + list.get(a - 1));
        System.out.println("\nBeginning update procedure...");
        System.out.println("\nPlease enter the file system password: ");
        String password = s.nextLine();
        System.out.println("\nPlease enter the file system pin: ");
        int pin = Integer.parseInt(s.nextLine().trim());
        ProjectNew.update(a, password, pin, true);
      }else{
        System.out.println("\nNo decrytped files found in all systems");
        System.out.println("\nWould you still like to update a file system?(yes/no)");
        t = s.nextLine().trim();
        while(!t.equalsIgnoreCase("yes") && !t.equalsIgnoreCase("no")){
          System.out.print("Please enter yes or no: ");
          t = s.nextLine().trim().toLowerCase();
        }
        if(t.equals("yes")){
          System.out.println("\nWhich file system would you like to update?\n");
          for(int i = 0; i < list.size(); i++){
            System.out.println("\t" + i + ". " + list.get(i));
          }
          int temp = Integer.parseInt(s.nextLine().trim());
          while(temp < 0 && temp >= list.size()){
            System.out.println("\nInvalid input! Which file sytem would you like to update?\n");
            for(int i = 0; i < list.size(); i++){
              System.out.println("\t" + i + ". " + list.get(i));
            }
            temp = Integer.parseInt(s.nextLine().trim());
          }
          System.out.println("\nPlease enter the file system password: ");
          String password = s.nextLine();
          System.out.println("\nPlease enter the file system pin: ");
          int pin = Integer.parseInt(s.nextLine().trim());
          ProjectNew.update(temp + 1, password, pin, false);
        }
      }
    }
  }
  private static int newFileSystemNum(){
    int o = 1;
    String a = o + ".txt";
    while(FileHandler.Exists(a)){
      o++;
      a = o + ".txt";
    }
    return o;
  }
  private static int findAccessed(){
    int o = newFileSystemNum() - 1;
    for(int i = 1; i <= o; i ++){
      String a = i + "a.txt";
      if(FileHandler.Exists(a)){
         return i;
      }
    }
    return -1;
  }
  private static List<String> returnRootPaths()throws Exception{
    int n = newFileSystemNum() -1;
    List<String> l = new ArrayList<String>();
    for(int i = 1; i <= n; i ++){
      l.add(ProjectNew.getRoot(i));
    }
    return l;
  }
}
