package utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import org.bouncycastle.util.encoders.Hex;

public final class Hashing {

  private String salt;

  private String hashMD5(String str) {
    return Hashing.md5(str);
  }

  private String hashSHA(String str){ return Hashing.sha(str); }

  /**
   * Hash string AND salt with MD5 hash
   * @param password input string
   * @return MD5 hashed of string
   */

  //Malthe: MD5 SHOULD NOT be used since people found out to generate collisions
  public String hashWithSaltMD5(String password){

    String salting = password + this.salt;

    return hashMD5(salting);
  }

  public String hashWithSaltSHA(String password){
    String salting = password + this.salt;

    return  hashSHA(salting);
  }

  // TODO: You should add a salt and make this secure : fixed
  public static String md5(String rawString) {
    try {

      // We load the hashing algoritm we wish to use.
      MessageDigest md = MessageDigest.getInstance("MD5");

      // We convert to byte array
      byte[] byteArray = md.digest(rawString.getBytes());

      // Initialize a string buffer
      StringBuffer sb = new StringBuffer();

      // Run through byteArray one element at a time and append the value to our stringBuffer
      for (int i = 0; i < byteArray.length; ++i) {
        sb.append(Integer.toHexString((byteArray[i] & 0xFF) | 0x100).substring(1, 3));
      }

      //Convert back to a single string and return
      return sb.toString();

    } catch (java.security.NoSuchAlgorithmException e) {

      //If somethings breaks
      System.out.println("Could not hash string");
    }

    return null;
  }

  // TODO: You should add a salt and make this secure : fixed
  public static String sha(String rawString) {
    try {
      // We load the hashing algoritm we wish to use.
      MessageDigest digest = MessageDigest.getInstance("SHA-256");

      // We convert to byte array
      byte[] hash = digest.digest(rawString.getBytes(StandardCharsets.UTF_8));

      // We create the hashed string
      String sha256hex = new String(Hex.encode(hash));

      // And return the string
      return sha256hex;

    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }

    return rawString;
  }

  public void setSalt(String salt) { this.salt = salt; }

  public String getSalt() { return salt; }
}