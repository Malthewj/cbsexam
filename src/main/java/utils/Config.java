package utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public final class Config {

  private static String DATABASE_HOST;
  private static int DATABASE_PORT;
  private static String DATABASE_USERNAME;
  private static String DATABASE_PASSWORD;
  private static String DATABASE_NAME;
  private static boolean ENCRYPTION;
  private static String SOLR_HOST;
  private static int SOLR_PORT;
  private static String SOLR_PATH;
  private static String SOLR_CORE;
  private static long PRODUCT_TTL;
  private static long ORDER_ID;
  private static char key1;
  private static char key2;
  private static char key3;
  private static char key4;
  private static char key5;
  private static char key6;
  private static char[] keyArray;
  private static String salt;

  public static long getProductTtl() {
    return PRODUCT_TTL;
  }

  public static long getOrderID() { return ORDER_ID; }

  public static String getDatabaseHost() {
    return DATABASE_HOST;
  }

  public static int getDatabasePort() {
    return DATABASE_PORT;
  }

  public static String getDatabaseUsername() {
    return DATABASE_USERNAME;
  }

  public static String getDatabasePassword() {
    return DATABASE_PASSWORD;
  }

  public static String getDatabaseName() {
    return DATABASE_NAME;
  }

  public static Boolean getEncryption() {
    return ENCRYPTION;
  }

  public static String getSolrHost() {
    return SOLR_HOST;
  }

  public static int getSolrPort() {
    return SOLR_PORT;
  }

  public static String getSolrPath() {
    return SOLR_PATH;
  }

  public static String getSolrCore() {
    return SOLR_CORE;
  }

  public static char[] getKeyArray() { return keyArray; }

  public static String getSalt() { return salt; }

  public static void initializeConfig() throws IOException {

    // Init variables to parse JSON
    JsonObject json;
    JsonParser parser = new JsonParser();

    // Read File and store input
    InputStream input = Config.class.getResourceAsStream("/config.json");
    BufferedReader reader = new BufferedReader(new InputStreamReader(input));

    // Go through the lines one by one
    StringBuffer stringBuffer = new StringBuffer();
    String str;

    // Read file one line at a time
    while ((str = reader.readLine()) != null) {
      stringBuffer.append(str);
    }

    // Konverterer json til variabler ved at typecaste til JsonObject
    json = (JsonObject) parser.parse(stringBuffer.toString());

    // Hiv teksten ud og sæt klassens variable til senere brug
    DATABASE_HOST = json.get("DATABASE_HOST").toString().replace("\"", "");
    DATABASE_PORT = Integer.parseInt(json.get("DATABASE_PORT").toString().replace("\"", ""));
    DATABASE_USERNAME = json.get("DATABASE_USERNAME").toString().replace("\"", "");
    DATABASE_PASSWORD = json.get("DATABASE_PASSWORD").toString().replace("\"", "");
    DATABASE_NAME = json.get("DATABASE_NAME").toString().replace("\"", "");
    ENCRYPTION = json.get("ENCRYPTION").getAsBoolean();
    SOLR_HOST = json.get("SOLR_HOST").toString().replace("\"", "");
    SOLR_PORT = Integer.parseInt(json.get("SOLR_PORT").toString().replace("\"", ""));
    SOLR_PATH = json.get("SOLR_PATH").toString().replace("\"", "");
    SOLR_CORE = json.get("SOLR_CORE").toString().replace("\"", "");
    PRODUCT_TTL = json.get("PRODUCT_TTL").getAsLong();
    key1 = json.get("Key1").getAsCharacter();
    key2 = json.get("Key2").getAsCharacter();
    key3 = json.get("Key3").getAsCharacter();
    key4 = json.get("Key4").getAsCharacter();
    key5 = json.get("Key5").getAsCharacter();
    key6 = json.get("Key6").getAsCharacter();

    keyArray = new char[]{key1, key2, key3, key4, key5, key6};

    salt = json.get("SaltKey").getAsString();

  }


}