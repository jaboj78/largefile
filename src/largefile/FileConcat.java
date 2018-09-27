package largefile;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.regex.Pattern;

/**
 * Concatenates all matching files to a single output file.
 * 
 * @author Jacob Boje
 *
 */
public class FileConcat extends SimpleFileVisitor<Path> {
    
    /**
     * The file name pattern.
     */
    private final Pattern pattern;
    private final FileChannel out;
    
    
    public static void main(String[] arg) throws IOException {
        //arg = new String[] {"C:\\largefile\\parts", "C:\\largefile\\output.xml", " .*"};
        
        if(arg.length<2) {
          System.err.println("Syntax: [input path] [output file] [filter]");
          System.err.println();
          System.err.println("Ex.: c:\\mydir c:\\file.out \" .*\"");
          System.err.println();
          System.err.println("Note: To escape wild card as input, enclose pattern in quotes with a leading white space");
          System.exit(1);
        }
        
        Path path = Paths.get(arg[0]).normalize();
        Path outFile=Paths.get(arg[1]);
        Pattern pattern = Pattern.compile(arg[2].trim());
        System.out.println("Contat files from '" + path + "' to '"+outFile +"' with regex patter '"+ arg[2].trim() +"'");
        
        try(FileChannel out=FileChannel.open(outFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            Files.walkFileTree(path, new FileConcat(pattern, out));    
        }
        
        System.out.println("DONE.");
      }
    
    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attributes) {
        try {
            if (pattern.matcher(path.toString()).find()) {
                System.out.println(path+"...");
                try(FileChannel in=FileChannel.open(path, StandardOpenOption.READ)) {
                      for(long p=0, l=in.size(); p<l; ) {
                        p+=in.transferTo(p, l-p, out);
                      }
                }
            }
            return FileVisitResult.CONTINUE;
        } catch (IOException ex) {
            return FileVisitResult.TERMINATE;
        }
    }
    
    /**
     * Creates the file concatenator.
     * @param pattern The pattern.
     * @param fileCounter The file counter.
     * @param lineCounter The line counter.
     */
    public FileConcat(Pattern pattern, FileChannel out) {
        this.pattern = pattern;
        this.out = out;
    }
    
    
}
