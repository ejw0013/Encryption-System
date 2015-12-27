import java.nio.file.*;
import java.util.*;
import java.io.*;

/**
  * The main class for handling files.
  * @author: Erich Wu
  * @version: 11/08/2015
  *
  */

public class FileHandler{
  public static boolean Exists(String filePath){
    File f = new File(filePath);
    if(f.exists()) {
      return true;
    }else{
      return false;
    }
  }
  public static void WriteFile(String fileName, String msg)throws Exception{
    PrintWriter w = new PrintWriter(fileName + ".txt");
    w.println(msg);
    w.close();
  }
  public static void WriteKeyTable(int fileSystem, long TP, String SU, String session, String SEc, String KiTic, String KFSi, String root) throws Exception{
    PrintWriter w = new PrintWriter("" + fileSystem + ".txt", "UTF-8");
    w.println(TP);
    w.println(SU);
    w.println(session);
    w.println(SEc);
    w.println(KiTic);
    w.println(KFSi);
    w.println(root);
    w.close();
  }
  public static boolean isDirectory(String root){
    Path rootPath = Paths.get(root);
    if(rootPath.toFile().isDirectory())
      return true;
    return false;
  }
  public static boolean isFile(String name){
    Path filePath = Paths.get(name);
    if(filePath.toFile().isFile())
      return true;
    return false;
  }

  public static void WriteFileTable(int file, List<String> files)throws Exception{
    PrintWriter w = new PrintWriter("" + file + "file.txt", "UTF-8");
    for(int i = 0; i < files.size(); i++){
      w.println(files.get(i));
      w.flush();
    }
    w.close();
  }

  public static List<String> getFiles(String root){
    List<String> list = new ArrayList<String>();
    Path rootPath = Paths.get(root);
    if(rootPath.toFile().isDirectory()){
      return getFileNames(list, rootPath);
    }else{
      System.out.println("Is not a directory!");
      return null;
    }
  }
  private static List<String> getFileNames(List<String> fileNames, Path dir) {
    try(DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
        for (Path path : stream) {
            if(path.toFile().isDirectory()) {
                getFileNames(fileNames, path);
            } else {
                fileNames.add(path.toAbsolutePath().toString());
            }
        }
    } catch(IOException e) {
        e.printStackTrace();
    }
    return fileNames;
  }
}
