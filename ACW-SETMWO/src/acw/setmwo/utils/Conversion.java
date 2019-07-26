package acw.setmwo.utils;


public class Conversion {
	
	public static String zeroPad(int number, int width){
	      StringBuffer result = new StringBuffer("");
	      for( int i = 0; i < width-Integer.toString(number).length(); i++ )
	         result.append( "0" );
	      result.append( Integer.toString(number) );
	     
	      return result.toString();
	}
}
