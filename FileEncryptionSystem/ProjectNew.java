import java.io.*;
import java.lang.Object;
import java.math.BigInteger;
import java.util.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.nio.*;
import java.nio.file.*;

/**
  * The main class for literally everything else.
  * @author: Erich Wu
  * @author: Muthu Ramaswamy
  * @version: 11/16/2015
  *
  */
public class ProjectNew {

  public static void access(String fileName, int fileNumber, String password, int pin) throws Exception {
    decryptSingle(fileName, fileNumber, password, pin);
  }
  public static void update( int fileNumber, String password, int pin, boolean encrypt) throws Exception {
    if(encrypt) {
      String fileName = getAccessFile(fileNumber);
      encryptSingle(fileName, fileNumber, password, pin);
    }
    //gets the root of the file system
    String root = getRoot(fileNumber);
    //get the KFSI
    byte[] KFSibytes = getKFSi(password, pin, fileNumber);
    //generate new seeds
    boolean[] SU = generateSeed();
    boolean[] SE = generateSeed();
    //get the session
    int session = bitToInt(getSession(fileNumber));
    //increment session number
    boolean[] sessionNumber = toBits(++session);
    //get password bytes
    boolean[] pbit = toBits(password.getBytes());
    //get new TP
    long TP = System.nanoTime();
    //convert to bits
    boolean[] TPbit = toBits(TP);
    //create Ka
    String Ka = digestSHA(toBytes(f(pbit, TPbit)), toBytes(cBits(SU, sessionNumber))).substring(0,64);
    //perform flip
    boolean[] RPP = f(cBits(pbit, toBits(pin)), SE);
    //create Kb
    String Kb = digestSHA(toBytes(RPP), toBytes(cBits(cBits(SE, TPbit), sessionNumber))).substring(0,64);
    //get TI
    long TI = System.nanoTime();
    boolean[] TIbit = toBits(TI);
    String Ki = digestSHA(toBytes(f(RPP, TIbit)), toBytes(cBits(TIbit, SE))).substring(0,64);
    byte[] SEc = EncryptDecrypt.encryptByte(toBytes(SE), hexToByte(Ka));
    byte[] KiTic = EncryptDecrypt.encryptByte(cBytes(hexToByte(Ki), toBytes(toBits(TI))), hexToByte(Kb));
    byte[] KFSic = EncryptDecrypt.encryptByte(KFSibytes,hexToByte(Ki));
    FileHandler.WriteKeyTable(fileNumber, TP, byteToHex(toBytes(SU)), byteToHex(toBytes(sessionNumber)), byteToHex(SEc), byteToHex(KiTic), byteToHex(KFSic), root);
    System.out.println("New Ki generated and KFSi encrypted!\n");
  }
  //newFileSystem could use a lot of refactoring....
  public static void newFileSystem(String root, int fileNumber, String password, int PIN) throws Exception {
    //generating inputs
    if(!FileHandler.isDirectory(root)) {
      System.out.println("Root directory provided is not a root! Breaking...");
      return;
    }
    System.out.println("\n==============================================================");
    System.out.println("       File Encryption System Starting Up ");
    System.out.println("==============================================================\n");
    System.out.println("Generating some keys...");
    boolean[] KFSi = generateSeed();
    byte[] KFSibytes = toBytes(KFSi);
    System.out.println("Encrypting all your files... ");
    //ENCRYPT ALL FILES HERE WITH Ki
    List<String> files = FileHandler.getFiles(root);
    for(int i = 0; i < files.size(); i++) {
      String line = files.get(i).replaceAll("\\\\\"", "/");
      EncryptDecrypt.encryptFile(line, KFSibytes);
    }
    System.out.println("All files at root encrypted.. starting transformations...");
    FileHandler.WriteFileTable(fileNumber, files);
    files = null;
    boolean[] SU = generateSeed();
    boolean[] SE = generateSeed();
    int sessionNumberInt = 0;
    boolean[] sessionNumber = toBits(sessionNumberInt);
    boolean[] pbit = toBits(password.getBytes());
    long TP = System.nanoTime();
    boolean[] TPbit = toBits(TP);
    String Ka = digestSHA(toBytes(f(pbit, TPbit)), toBytes(cBits(SU, sessionNumber))).substring(0,64);
    boolean[] RPP = f(cBits(pbit, toBits(PIN)), SE);
    String Kb = digestSHA(toBytes(RPP), toBytes(cBits(cBits(SE, TPbit), sessionNumber))).substring(0,64);
    long TI = System.nanoTime();
    boolean[] TIbit = toBits(TI);
    String Ki = digestSHA(toBytes(f(RPP, TIbit)), toBytes(cBits(TIbit, SE))).substring(0,64);
    System.out.println("Encrypting some stuff...");
    byte[] SEc = EncryptDecrypt.encryptByte(toBytes(SE), hexToByte(Ka));
    byte[] KiTic = EncryptDecrypt.encryptByte(cBytes(hexToByte(Ki), toBytes(toBits(TI))), hexToByte(Kb));
    byte[] KFSic = EncryptDecrypt.encryptByte(KFSibytes,hexToByte(Ki));
    FileHandler.WriteKeyTable(fileNumber, TP, byteToHex(toBytes(SU)), byteToHex(toBytes(sessionNumber)), byteToHex(SEc), byteToHex(KiTic), byteToHex(KFSic), root);
    System.out.println("File System Properly Encrypted and Created!\n");
  }
  /*
     Note: this method shouldn't be used in production code unless we are allowing a
     full file system decrypt...
   */
  private static void decryptAll(String root, int fileNumber, String password, int pin) throws Exception {
    if(!FileHandler.isDirectory(root)) {
      System.out.println("Root directory provided is not a root! Breaking...");
      return;
    }
    List<String> files = FileHandler.getFiles(root);
    byte[] KFSi = getKFSi(password, pin, fileNumber);
    for(int i = 0; i < files.size(); i++) {
      String line = files.get(i).replaceAll("\\\\\"", "/");
      EncryptDecrypt.decryptFile(line, KFSi);
    }
  }
  private static void decryptSingle(String fileName, int fileNumber, String password, int pin) throws Exception {
    if(!FileHandler.isFile(fileName)) {
      System.out.println("File does not exist or is a directory! Breaking...");
      return;
    }
    EncryptDecrypt.decryptCopy(fileName, getKFSi(password, pin, fileNumber));
    FileHandler.WriteFile(fileNumber + "a", fileName);
    System.out.println(fileName + " has been decrypted. Please check the decrypted copy.");
    System.out.println("Please perfrom any desired edits on the decrypted copy. Please do not rename the decrypted copy.");
  }
  private static void encryptSingle(String fileName, int fileNumber, String password, int pin) throws Exception {
    if(!FileHandler.isFile(fileName)) {
      System.out.println("File does not exist or is a directory! Breaking...");
      return;
    }
    EncryptDecrypt.encryptOver(fileName, getKFSi(password, pin, fileNumber));
    System.out.println(fileName + " copy has been encrypted and deleted...");
  }
  private static String getAccessFile(int fileNumber) throws Exception {
    BufferedReader br = new BufferedReader(new FileReader(fileNumber + "a.txt"));
    String a = br.readLine();
    br.close();
    Files.delete(Paths.get(fileNumber + "a.txt"));
    return a;
  }
  private static boolean[] f(boolean[] left, boolean[] right){
    int length = right.length;
    int half = length / 2;
    boolean[] x = new boolean[half];
    boolean[] rightLSB = new boolean[half];
    System.arraycopy(right,0,x,0,half);
    System.arraycopy(right,(half - 1),rightLSB,0,half);
    boolean[] y = cBits(left, rightLSB);
    boolean answer[] = rotate(y, bitsToLong(x));
    return answer;
  }

  private static boolean[] toBits(int convert) {
    boolean[] bits = new boolean[32];
    for (int i = 31; i >= 0; i--) {
      bits[31 - i] = (convert & (1 << i)) != 0;
    }
    return bits;
  }

  private static boolean[] toBits(long convert) {
    boolean[] bits = new boolean[64];
    for (int i = 63; i >= 0; i--) {
      bits[63 - i] = (convert & (1 << i)) != 0;
    }
    return bits;
  }

  private static long bitsToLong(boolean[] convert) {
    long converted = 0, length = convert.length;
    for (int i = 0; i < length; ++i) {
      converted = (converted << 1) + (convert[i] ? 1 : 0);
    }
    return converted;
  }

  private static byte[] cBytes(byte[] a, byte[] b){
    byte[] result = new byte[a.length + b.length];
    System.arraycopy(a, 0, result, 0, a.length);
    System.arraycopy(b, 0, result, a.length, b.length);
    return result;
  }
  private static boolean[] cBits(boolean[] xs, boolean[] ys) {
    boolean[] result = new boolean[xs.length + ys.length];
    System.arraycopy(xs,0,result,0,xs.length);
    System.arraycopy(ys,0,result,xs.length,ys.length);
    return result;
  }

  private static boolean[] rotate(boolean[] rotate, long amount) {
    long amountFinal = amount % rotate.length;
    for (long i = 0; i < amountFinal; i++) {
      boolean holder = rotate[0];
      System.arraycopy(rotate, 1, rotate, 0, rotate.length - 1);
      rotate[rotate.length - 1] = holder;
    }
    return rotate;
  }

  private static boolean[] toBits(byte[] bytes) {
    boolean[] bits = new boolean[bytes.length * 8];
    for (int i = 0; i < bytes.length * 8; i++) {
      if ((bytes[i / 8] & (1 << (7 - (i % 8)))) > 0)
        bits[i] = true;
    }
    return bits;
  }

  private static byte[] toBytes(boolean[] input) {
    byte[] toReturn = new byte[input.length / 8];
    for (int entry = 0; entry < toReturn.length; entry++) {
      for (int bit = 0; bit < 8; bit++) {
        if (input[entry * 8 + bit]) {
          toReturn[entry] |= (128 >> bit);
        }
      }
    }
    return toReturn;
  }
  private static Integer bitToInt(boolean[] a){
    Integer n = 0;
    for (boolean b : a)
      n = (n << 1) | (b ? 1 : 0);
    return n;
  }
  private static byte[] hexToByte(String s){
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)+ Character.digit(s.charAt(i+1), 16));
    }
    return data;
  }

  private static String byteToHex(byte[] a){
    StringBuffer hexString = new StringBuffer();
    for (int i = 0; i < a.length; i++) {
      String hex = Integer.toHexString(0xFF & a[i]);
      if(hex.length() == 1) {
        hexString.append('0');
      }
      hexString.append(hex);
    }
    return hexString.toString();
  }

  private static String digestSHA(byte[] msg, byte[] keyByte) {
    String digest = null;
    try {
      SecretKeySpec key = new SecretKeySpec(keyByte, "HmacSHA512");
      Mac mac = Mac.getInstance("HmacSHA512");
      mac.init(key);

      byte[] bytes = mac.doFinal(msg);

      StringBuffer hash = new StringBuffer();
      for (int i = 0; i < bytes.length; i++) {
        String hex = Integer.toHexString(0xFF & bytes[i]);
        if (hex.length() == 1) {
          hash.append('0');
        }
        hash.append(hex);
      }
      digest = hash.toString();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return digest;
  }

  private static boolean[] generateSeed() throws Exception
  {
    try{
      long startime11 = System.currentTimeMillis();
      long nanoGMT2 = System.nanoTime();
      long a = new Date().getTime();
      byte[] jtimeBytes=ByteBuffer.allocate(8).putLong(a).array();
      byte[] nanoBytes = ByteBuffer.allocate(8).putLong(nanoGMT2).array();
      byte [] mtimeBytes = ByteBuffer.allocate(8).putLong(startime11).array();
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
      outputStream.write(mtimeBytes);
      outputStream.write( jtimeBytes );
      outputStream.write( nanoBytes );
      byte[] TimeSeed = outputStream.toByteArray( );
      SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
      byte[] Salt1 = new byte[32];
      random.setSeed(TimeSeed);
      random.nextBytes(Salt1);
      return toBits(Salt1);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private static byte[] generateKa(String password, int fileNumber) throws Exception {
    boolean[] pbit = toBits(password.getBytes());
    boolean[] RP = f(pbit, getTP(fileNumber));
    String verify = digestSHA(toBytes(RP), toBytes(cBits(getSU(fileNumber), getSession(fileNumber)))).substring(0, 64);
    return hexToByte(verify);
  }
  private static byte[] generateKb(String password, int pin, int file) throws Exception {
    boolean[] RPP = f(cBits(toBits(password.getBytes()), toBits(pin)), getSE(password, file));
    String verify = digestSHA(toBytes(RPP), toBytes(cBits(cBits(getSE(password, file), getTP(file)), getSession(file)))).substring(0, 64);
    return hexToByte(verify);
  }
  private static boolean[] getSU(int fileNumber) throws Exception {
    BufferedReader br = new BufferedReader(new FileReader("" + fileNumber + ".txt"));
    br.readLine();
    return toBits(hexToByte(br.readLine()));
  }
  private static boolean[] getSession(int fileNumber) throws Exception {
    BufferedReader br = new BufferedReader(new FileReader("" + fileNumber + ".txt"));
    br.readLine();
    br.readLine();
    return toBits(hexToByte(br.readLine()));
  }
  private static boolean[] getTP(int fileNumber) throws Exception {
    BufferedReader br = new BufferedReader(new FileReader("" + fileNumber + ".txt"));
    return toBits(Long.parseLong(br.readLine()));
  }

  private static boolean[] getSE(String pass, int fileNumber) throws Exception {
    BufferedReader br = new BufferedReader(new FileReader("" + fileNumber + ".txt"));
    br.readLine();
    br.readLine();
    br.readLine();
    return toBits(EncryptDecrypt.decryptByte(hexToByte(br.readLine()), generateKa(pass, fileNumber)));
  }
  private static byte[] getKiTi(String pass, int pin, int fileNumber) throws Exception {
    BufferedReader br = new BufferedReader(new FileReader("" + fileNumber + ".txt"));
    br.readLine();
    br.readLine();
    br.readLine();
    br.readLine();
    return EncryptDecrypt.decryptByte(hexToByte(br.readLine()), generateKb(pass, pin, fileNumber));
  }
  public static String getRoot(int fileNumber) throws Exception {
    BufferedReader br = new BufferedReader(new FileReader("" + fileNumber + ".txt"));
    br.readLine();
    br.readLine();
    br.readLine();
    br.readLine();
    br.readLine();
    br.readLine();
    return br.readLine().replaceAll("\\\\\"", "/");
  }
  private static byte[] getKi(String pass, int pin, int fileNumber) throws Exception {
    byte[] KiTi = getKiTi(pass, pin, fileNumber);
    byte[] output = Arrays.copyOfRange(KiTi, 0, 32);
    return output;
  }
  private static byte[] getTi(String pass, int pin, int fileNumber) throws Exception {
    byte[] KiTi = getKiTi(pass, pin, fileNumber);
    byte[] output = Arrays.copyOfRange(KiTi, 32, 64);
    return output;
  }
  private static byte[] getKFSi(String pass, int pin, int fileNumber) throws Exception {
    return EncryptDecrypt.decryptByte(getKFSic(fileNumber), getKi(pass, pin, fileNumber));
  }
  private static byte[] getKFSic(int fileNumber) throws Exception {
    BufferedReader br = new BufferedReader(new FileReader("" + fileNumber + ".txt"));
    br.readLine();
    br.readLine();
    br.readLine();
    br.readLine();
    br.readLine();
    return hexToByte(br.readLine());
  }
}
