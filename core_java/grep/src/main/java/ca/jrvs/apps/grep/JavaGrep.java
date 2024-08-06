package ca.jrvs.apps.grep;

import java.io.File;
import java.io.IOException;
import java.util.stream.*;

public interface JavaGrep {

    /**
     * Top level search workflow
     * @throws IOException if process failed
     */
    void process() throws IOException;

    /**
     * Traverse a given directory and return all files
     * @param rootDir input directory
     * @return files under rootDir
     */
    Stream<File> listFiles(String rootDir);

    /**
     * Read a file and return the lines
     * @param inputFile file to be read
     * @return lines
     * @throws IOException if read failed
     * @throws IllegalArgumentException if a given inputFile is not a file
     */
    Stream<String> readLines(File inputFile) throws IOException;

    /**
     * Check if a line contains the regex pattern
     * @param line input string
     * @return true if there is a match
     */
    boolean containsPattern(String line);

    /**
     * Write lines to a file
     * @param lines input
     * @throws IOException if write failed
     */
    void writeToFile(Stream<String> lines) throws IOException;

    String getRootPath();

    void setRootPath(String rootPath);

    String getRegex();

    void setRegex(String regex);

    String getOutFile();

    void setOutFile(String outFile);
}
