package ca.jrvs.apps.grep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class JavaGrepOriginalImplement implements JavaGrepOriginal{

    final Logger logger = LoggerFactory.getLogger(JavaGrep.class);

    private String regex;
    private String rootPath;
    private String outFile;

    @Override
    public void process() throws IOException {
        List<String> matchedLines = new ArrayList<>();

        for (File f : listFiles(this.rootPath)){
            for (String l : readLines(f)){
                if (containsPattern(l)){
                    matchedLines.add(l);
                }
            }
        }
        writeToFile(matchedLines);
    }

    @Override
    public List<File> listFiles(String rootDir) {
        List<File> files;
        File directoryPath = new File(rootDir);
        files = Arrays.asList(Objects.requireNonNull(directoryPath.listFiles(File::isFile)));

        return files;
    }

    @Override
    public List<String> readLines(File inputFile) {

        List<String> lines = new ArrayList<>();

        if(inputFile.isFile()){
            try(BufferedReader br = new BufferedReader(new FileReader(inputFile))){
                String line;
                while((line = br.readLine()) != null){
                    lines.add(line);
                }
            }catch(IOException e){
                this.logger.error("Error: reading failed.", e);
            }
        }else{
            throw new IllegalArgumentException("Error: Input file is invalid.");
        }

        return lines;
    }

    @Override
    public boolean containsPattern(String line) {

        boolean retVal = false;
        Pattern pattern = Pattern.compile(this.regex);
        Matcher matcher = pattern.matcher(line);

        if (matcher.find()){
            retVal = true;
        }

        return retVal;
    }

    @Override
    public void writeToFile(List<String> lines) throws IOException {
        File fOut = new File(this.outFile);
        try (FileOutputStream fos = new FileOutputStream(fOut); BufferedWriter buffW = new BufferedWriter(new OutputStreamWriter(fos))){
            for (String s : lines){
                buffW.write(s);
                buffW.newLine();
            }
        } catch (IOException ex){
            throw new IOException("Writing to file failed",ex);
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

        JavaGrepOriginalImplement jGrep = new JavaGrepOriginalImplement();
        jGrep.setRegex(args[0]);
        jGrep.setRootPath(args[1]);
        jGrep.setOutFile(args[2]);

        try{
            jGrep.process();
        } catch (Exception ex){
            jGrep.logger.error("Error: Unable to process", ex);
        }
    }
}
