package largefile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

/**
 * Prints a index of start lineno, end lineno and line counts in each file parts.
 * 
 * @author Jacob Boje
 */
public final class PartsLineIndex extends SimpleFileVisitor<Path> {

    /**
     * @param args The command line arguments.
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        //args = new String[] {"C:\\largefile\\parts", " .*"};
        if(args.length != 2) {
            System.err.println("Usage:");
            System.err.println("  " + PartsLineIndex.class.getName() + " [parts path] [pattern]");
            System.err.println();
            System.err.println("Where [path] is the relative or absolute path and [pattern] is the regex pattern for matching indexable files.");
            System.err.println();
            System.err.println("Ex.: c:\\largefile\\parts \" .*\"");
            System.err.println();
            System.err.println("Note: To escape wild card as input, enclose pattern in quotes with a leading white space");
            return;
        }
                
        Pattern pattern = Pattern.compile(args[1].trim());
        
        AtomicInteger fileCounter = new AtomicInteger();
        AtomicLong lineCounter = new AtomicLong();
        
        Path path = Paths.get(args[0]).normalize();
        Files.walkFileTree(path, new PartsLineIndex(pattern, fileCounter, lineCounter));
        
        System.out.println(lineCounter.get() + " lines in " + fileCounter.get() + " source files");
        System.out.println("  in " + path + " and subdirectories");
        System.out.println("  matching the pattern " + pattern);
    }
    
    /**
     * The file name pattern.
     */
    private final Pattern pattern;
    
    /**
     * The file counter.
     */
    private final AtomicInteger fileCounter;
    
    /**
     * The line counter.
     */
    private final AtomicLong lineCounter;
    
    /**
     * Creates the line counter.
     * @param pattern The pattern.
     * @param fileCounter The file counter.
     * @param lineCounter The line counter.
     */
    public PartsLineIndex(Pattern pattern, AtomicInteger fileCounter, AtomicLong lineCounter) {
        this.pattern = pattern;
        this.fileCounter = fileCounter;
        this.lineCounter = lineCounter;
    }

    /*
     * (non-Javadoc)
     * @see java.nio.file.SimpleFileVisitor#visitFile(java.lang.Object, java.nio.file.attribute.BasicFileAttributes)
     */
    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attributes) {
        try {
            if (pattern.matcher(path.toString()).find()) {
                fileCounter.incrementAndGet();
                
                int lines = (int) Files.lines(path).count();
                        
                ByteBuffer dst = ByteBuffer.allocate(1);
                try (FileChannel fc = FileChannel.open(path, StandardOpenOption.READ)) {
                    long size = fc.size();
                    fc.read(dst, size-1);
                    
                    if (dst.get(0) != '\n') {
                        lines--;
                    }
                    
                }
           
                long fromLine = lineCounter.longValue() +1;
                lineCounter.addAndGet(lines);
                long toLine = lineCounter.longValue();
                System.out.println(fromLine + " - " + toLine +"(" + lines +" lines): " + path.toString());
            }
            return FileVisitResult.CONTINUE;
        } catch (IOException ex) {
            return FileVisitResult.TERMINATE;
        }
    }

}