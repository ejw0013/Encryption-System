import java.util.*;
import java.io.*;

public class HexIO{
   /*
    *  public static void ToHEX(byte[], String)
    *  @PARAMS:
    *  byte[] text: the text need to be stored in Hex
    *  String String fileNam: name of the file
    *  @OUTPUT:
    *    Returns: noting
    *    Creates: hex file.
    */

   public static void toHEX(byte[] text, String fileName) throws IOException{

    String hexString = "";
   for(int i = 0; i < text.length; i++)
   {
     String hex = Integer.toHexString(text[i]&0xFF );
     if (hex.length() == 1) {
         hex = "0" + hex;
     }
     hexString = hexString + hex;
   }
    FileOutputStream fos = new FileOutputStream(fileName);
    fos.write(hexString.getBytes());
    fos.close();
   }

   /*
    *  public static void ByteFromHex( String)
    *  @PARAM fileName name of the file
    *  @return returns byte data from hex file
    */

   public static byte[] byteFromHex(String filename) throws IOException
 {
     BufferedReader reader = new BufferedReader( new FileReader (filename));
     String line  = null;
     StringBuilder stringBuilder = new StringBuilder();
     while( ( line = reader.readLine() ) != null ) {
         stringBuilder.append( line );
     }
     String sIV = stringBuilder.toString();
     stringBuilder = null;
     int len = sIV.length();
     byte[] bIV = new byte[len / 2];
     for (int i = 0; i < len; i += 2) {
         bIV[i / 2] = (byte) ((Character.digit(sIV.charAt(i), 16) << 4)
                              + Character.digit(sIV.charAt(i+1), 16));
     }
     return bIV;
 }
}
