import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.security.*;
import java.security.spec.*;
import java.security.NoSuchProviderException;
import java.nio.file.*;
import javax.crypto.*;
import javax.crypto.spec.*;

/**
  * The main class for encrypting and decryption tasks.
  * @author: Erich Wu
  * @version: 11/09/2015
  *
  */
  
public class EncryptDecrypt{
  private static final int GCM_TAG_LENGTH = 16;
  private static byte[] tag;

  public static byte[] encryptByte(byte[] msg, byte[] keyBytes){
    try{
      tag = new byte[GCM_TAG_LENGTH];
      SecretKey key = new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");
      Cipher eCipher = Cipher.getInstance("AES/GCM/NoPadding");
      byte[] iv = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,16};
      GCMParameterSpec algoParam = new GCMParameterSpec(128, iv);
      eCipher.init(Cipher.ENCRYPT_MODE, key, algoParam);
      eCipher.updateAAD(tag);
      return eCipher.doFinal(msg);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
  public static byte[] decryptByte(byte[] msg, byte[] keyBytes){
    try{
      tag = new byte[GCM_TAG_LENGTH];
      SecretKey key = new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");
      Cipher dCipher = Cipher.getInstance("AES/GCM/NoPadding");
      byte[] iv = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,16};
      GCMParameterSpec algoParam = new GCMParameterSpec(128, iv);
      dCipher.init(Cipher.DECRYPT_MODE, key, algoParam);
      dCipher.updateAAD(tag);
      return dCipher.doFinal(msg);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
  public static void encryptFile(String fileName, byte[] keyBytes){
    try{
      tag = new byte[GCM_TAG_LENGTH];
      SecretKey key = new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");
      Cipher eCipher = Cipher.getInstance("AES/GCM/NoPadding");
      byte[] iv = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,16};
      GCMParameterSpec algoParam = new GCMParameterSpec(128, iv);
      eCipher.init(Cipher.ENCRYPT_MODE, key, algoParam);
      byte[] plain = getBytesFromFile(new File(fileName));
      eCipher.updateAAD(tag);
      byte[] cipher = eCipher.doFinal(plain);
      BigInteger bi = new BigInteger(1, cipher);
      String hex = bi.toString(16);
      clearFile(fileName);
      FileOutputStream output = new FileOutputStream(fileName);
      //write hex file
      output.write(hex.getBytes());
      output.close();
    }catch(Exception e){
      e.printStackTrace();
    }
  }
  public static void decryptFile(String fileName, byte[] keyBytes){
    try{
      tag = new byte[GCM_TAG_LENGTH];
      SecretKey key = new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");
      byte[] cipher = HexIO.byteFromHex(fileName);
      Cipher dCipher = Cipher.getInstance("AES/GCM/NoPadding");
      byte[] iv = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,16};
      GCMParameterSpec algoParam = new GCMParameterSpec(128, iv);
      dCipher.init(Cipher.DECRYPT_MODE, key, algoParam);
      dCipher.updateAAD(tag);
      byte[] plain = dCipher.doFinal(cipher);
      clearFile(fileName);
      FileOutputStream output = new FileOutputStream(fileName);
      output.write(plain);
      output.close();
    }catch(Exception e){
      e.printStackTrace();
    }
  }
  public static void decryptCopy(String fileName, byte[] keyBytes){
    try{
      tag = new byte[GCM_TAG_LENGTH];
      SecretKey key = new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");
      byte[] cipher = HexIO.byteFromHex(fileName);
      Cipher dCipher = Cipher.getInstance("AES/GCM/NoPadding");
      byte[] iv = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,16};
      GCMParameterSpec algoParam = new GCMParameterSpec(128, iv);
      dCipher.init(Cipher.DECRYPT_MODE, key, algoParam);
      dCipher.updateAAD(tag);
      String file = fileName.substring(fileName.lastIndexOf("\\") + 1);
      String path = fileName.substring(0, fileName.lastIndexOf("\\"));
      FileOutputStream output = new FileOutputStream( path + "\\decryptCopy" + file);
      byte[] plain = dCipher.doFinal(cipher);
      output.write(plain);
      output.close();
    }catch(Exception e){
      e.printStackTrace();
    }
  }
  public static void encryptOver(String fileName, byte[] keyBytes){
    try{
      tag = new byte[GCM_TAG_LENGTH];
      SecretKey key = new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");
      Cipher eCipher = Cipher.getInstance("AES/GCM/NoPadding");
      byte[] iv = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,16};
      GCMParameterSpec algoParam = new GCMParameterSpec(128, iv);
      eCipher.init(Cipher.ENCRYPT_MODE, key, algoParam);
      String file = fileName.substring(fileName.lastIndexOf("\\") + 1);
      String path = fileName.substring(0, fileName.lastIndexOf("\\"));
      byte[] plain = getBytesFromFile(new File(path + "\\decryptCopy" + file));
      eCipher.updateAAD(tag);
      byte[] cipher = eCipher.doFinal(plain);
      BigInteger bi = new BigInteger(1, cipher);
      String hex = bi.toString(16);
      clearFile(fileName);
      FileOutputStream output = new FileOutputStream(fileName);
      output.write(hex.getBytes());
      output.close();
      Files.delete(Paths.get(path + "\\decryptCopy" + file));
    }catch(Exception e){
      e.printStackTrace();
    }
  }
  public static byte[] getBytesFromFile(File file) throws IOException {
     InputStream is = new FileInputStream(file);
     long length = file.length();
     if (length > Integer.MAX_VALUE);
     byte[] bytes = new byte[(int)length];
     int offset = 0;
     int numRead = 0;
     while (offset < bytes.length&& (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0)
        offset += numRead;
     if (offset < bytes.length)
        throw new IOException("Could not completely read file "+file.getName());
     is.close();
     return bytes;
  }
  public static void clearFile(String fileName)throws Exception{
    PrintWriter w = new PrintWriter(fileName);
    w.close();
  }
}
