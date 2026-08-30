package ch.sjna.alexinf.simple;

import ch.sjna.claude.SJNA;
import ch.sjna.claude.SJNAConfig;
import ch.sjna.claude.model.Document;
import ch.sjna.claude.model.EnumDefinition;
import ch.sjna.claude.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class SYNAConfig {

    private final SJNAConfig config;

    private final File file;

    private Document doc;

    public SYNAConfig(File file){
        Document doc;
        try {
            doc = SJNA.load(file.getAbsolutePath());
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        this.file = file;
        config = SJNA.asConfig(doc);
        doc = config.getDocument();
    }

    public SYNAConfig(String filePath){
        Document doc;
        try {
            doc = SJNA.load(filePath);
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        config = SJNA.asConfig(doc);
        file = new File(filePath);
        doc = config.getDocument();
    }

    public SYNAConfig(Path filePath){
        Document doc;
        try {
            doc = SJNA.load(filePath.toString());
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
        file = filePath.toFile();
        config = SJNA.asConfig(doc);
        doc = config.getDocument();
    }


    public void save(){
        try {
            SJNA.save(config.getDocument(), file.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getString(String path){
        return config.getString(path);
    }

    public int getInt(String path){
        return config.getInt(path);
    }

    public double getDouble(String path){
        return config.getDouble(path);
    }

    public long getLong(String path){
        return config.getLong(path);
    }

    public boolean getBoolean(String path){
        return config.getBoolean(path);
    }

    public List<String> getStringList(String path){
        return config.getList(path);
    }

    public List<Integer> getIntList(String path){
        return config.getIntList(path);
    }

    public List<String> getKeys(){
        return config.getKeys();
    }

   /* public List<String> getEnumOptions(){
        return new EnumDefinition()
    }*/

    public Document getDocument() {
        return doc;
    }

    public SJNAConfig getSJNAConfig(){
        return config;
    }

    // Set methods

    public void setString(String path, String value){
        config.setString(path,value);
    }

    public void setInt(String path, int value){
        config.setInt(path,value);
    }

    public void setDouble(String path, double value){
        config.setDouble(path,value);
    }

    public void setLong(String path, long value){
        config.setLong(path,value);
    }

    public void setBoolean(String path, boolean value){
        config.setBoolean(path,value);
    }

    public void setStringList(String path, List<String> value){
        config.setList(path,value);
    }

}
