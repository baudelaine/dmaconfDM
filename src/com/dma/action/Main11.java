package com.dma.action;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

public class Main11 {

	// this could be placed as a static final constant, so the compiling is only done once
	static final Pattern pattern = Pattern.compile("[^\\w\\s\"«»\\(\\)\\[\\]\\.,!;\\{\\}%\\*/&~\\^']", Pattern.UNICODE_CHARACTER_CLASS);
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub

		
		String value = "Date dentrée";
		
//		System.out.println("fixed = " + pattern.matcher(value).replaceAll(""));
//	    System.out.println();
		
		// On cherche la valeur du caractère à la con ici le 6ème caractère
		
		byte[] utf8Bytes = value.getBytes("UTF8");
	    byte[] defaultBytes = value.getBytes();

	    String chaineDeMerde = new String(utf8Bytes, "UTF8");
	    System.out.println();
	    printBytes(utf8Bytes, "utf8Bytes");
	    System.out.println();
	    printBytes(defaultBytes, "defaultBytes");
	    System.out.println();
	    
	    // c'est les éléments 6 et 7 du tableau qui correspondent au caractère 6 et plus précisément l'élément de droite donc 0x92
	    // qui s'écrit \u0092 en Unicode

	    System.out.println("chaineDeMerde = " + chaineDeMerde);

	    //On l'affiche comme çà
	    System.out.println("caractère de merde identifié = \u0092");
	    
	    // Et on le fix comme çà
		System.out.println("chaineDeQaulité = " + value.replaceAll("[\\u0092]", "'"));
	    
	}

	public static void printBytes(byte[] array, String name) {
	    for (int k = 0; k < array.length; k++) {
	        System.out.println(name + "[" + k + "] = " + "0x" +
	            UnicodeFormatter.byteToHex(array[k]));
	    }
	}
	
    public static class UnicodeFormatter  {
   	 
 	   static public String byteToHex(byte b) {
 	      // Returns hex String representation of byte b
 	      char hexDigit[] = {
 	         '0', '1', '2', '3', '4', '5', '6', '7',
 	         '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
 	      };
 	      char[] array = { hexDigit[(b >> 4) & 0x0f], hexDigit[b & 0x0f] };
 	      return new String(array);
 	   }
 	 
 	   static public String charToHex(char c) {
 	      // Returns hex String representation of char c
 	      byte hi = (byte) (c >>> 8);
 	      byte lo = (byte) (c & 0xff);
 	      return byteToHex(hi) + byteToHex(lo);
 	   }
 	 
 	} // class	    
	
}
