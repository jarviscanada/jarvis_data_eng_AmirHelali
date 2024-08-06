package ca.jrvs.apps.grep;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.*;
import java.util.stream.*;

public class JavaGrepImplement implements JavaGrep{

    final Logger logger = LoggerFactory.getLogger(JavaGrep.class);

    private String regex;
    private String rootPath;
    private String outFile;
    private Pattern pattern;

    @Override
    public void process() throws IOException {
        try (Stream<File> files = listFiles(this.rootPath)) {
            try (Stream<String> matchedLines = files.flatMap(file -> {
                try {
                    return readLines(file).filter(this::containsPattern);
                } catch (IOException e) {
                    logger.error("Error reading file: " + file.getPath(), e);
                    return Stream.empty();
                }
            })) {
                writeToFile(matchedLines);
            }
        }
    }

    @Override
    public Stream<File> listFiles(String rootDir) {
        try {
            return Files.walk(Paths.get(rootDir)).filter(Files::isRegularFile).map(Path::toFile);
        } catch (IOException e) {
            logger.error("Error listing files in directory: " + rootDir, e);
            return Stream.empty();
        }
    }

    @Override
    public Stream<String> readLines(File inputFile) throws IOException{
        if (!inputFile.isFile()) {
            throw new IllegalArgumentException("Error: Input file is invalid.");
        }
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        return reader.lines().onClose(() -> {
            try {
                reader.close();
            } catch (IOException e) {
                logger.error("Error closing BufferedReader", e);
            }
        });
    }

    @Override
    public boolean containsPattern(String line) {
        return pattern.matcher(line).find();
    }

    @Override
    public void writeToFile(Stream<String> lines) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outFile);
             OutputStreamWriter osw = new OutputStreamWriter(fos);
             BufferedWriter writer = new BufferedWriter(osw)) {
            lines.forEach(line -> {
                try {
                    writer.write(line);
                    writer.newLine();
                } catch (IOException e) {
                    logger.error("Error writing to file: " + outFile, e);
                    throw new UncheckedIOException(e);
                }
            });
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    @Override
    public String getRootPath() {
        return rootPath;
    }

    @Override
    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public String getRegex() {
        return regex;
    }

    @Override
    public void setRegex(String regex) {
        this.regex = regex;
        this.pattern = Pattern.compile(regex);
    }

    @Override
    public String getOutFile() {
        return outFile;
    }

    @Override
    public void setOutFile(String outFile) {
        this.outFile = outFile;
    }

    public static void main(String[] args) {
        if (args.length != 3){
            throw new IllegalArgumentException("USAGE: JavaGrep regex rootPath outFile");
        }

        JavaGrepImplement javaGrepImp = new JavaGrepImplement();
        javaGrepImp.setRegex(args[0]);
        javaGrepImp.setRootPath(args[1]);
        javaGrepImp.setOutFile(args[2]);

        try{
            javaGrepImp.process();
        } catch (Exception e){
            javaGrepImp.logger.error("Error: Unable to process", e);
        }
    }
}
