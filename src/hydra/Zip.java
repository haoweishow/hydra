
package hydra;

/** Used for zipping of the files for
 *  demo purposes. Example adopted from 
 *  http://java.sun.com/developer/technicalArticles/Programming/compression/
 *
 * @author Edmon Begoli
 */
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.*;

public class Zip {

   static final int BUFFER = 2048;

   public static void zipFile ( String source, String outputName ) {
      
      try {
	 long start_time = System.currentTimeMillis();
         BufferedInputStream origin = null;
         byte data[] = new byte[BUFFER];
	 InputStream is = Zip.class.getResourceAsStream( source );

	 if ( is == null ){ 
	    throw new IllegalArgumentException( "File " + source +
		  " cannot be found on classpath." );
	 }
         FileOutputStream dest = new FileOutputStream( outputName );
         ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
         origin = new BufferedInputStream( is , BUFFER);
	 ZipEntry entry = new ZipEntry( source );
         out.putNextEntry(entry);
	 int count;

         while((count = origin.read(data, 0,BUFFER)) != -1) {
               out.write(data, 0, count);
         }
         origin.close();
         out.close();
         long end_time = System.currentTimeMillis();
         System.out.println( "file " + " zipped in " + (end_time - start_time) + " ms."  );

      } catch( Exception e ) {
	 Logger.getLogger(SingleLevelCyclicBarrier.class.getName()).log(Level.SEVERE, "Zipping failed. ", e );
         e.printStackTrace();
      }//end catch

   }//end zipFile

   public static void main(String args[] ){
	 Zip.zipFile( "sample.pdf" , "source.zip" );
   }//end main

}//end class
