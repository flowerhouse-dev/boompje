/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import stamboom.domain.Administratie;

public class SerializationMediator implements IStorageMediator {

    private Properties props;

    /**
     * creation of a non configured serialization mediator
     */
    public SerializationMediator() {
        props = null;
    }

    @Override
    public Administratie load() throws IOException {
        Administratie admin = new Administratie();
        if (!isCorrectlyConfigured()) {
            throw new RuntimeException("Serialization mediator isn't initialized correctly.");
        }
        
        // todo opgave 2
        
        try
        {
            ObjectInputStream in;
            FileInputStream stream;
            stream = new FileInputStream((File) this.props.get("file"));
            in = new ObjectInputStream(stream);
            admin = (Administratie) in.readObject();  
            in.close();
            stream.close();
        } 
        catch (IOException exc) { 
            exc.printStackTrace();
            System.out.println("niet geladen");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SerializationMediator.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("niet geladen");
        }
        
        return admin;        
    }
    
    //@Override
    public void save(Administratie admin) throws IOException {
        if (!isCorrectlyConfigured()) {
            throw new RuntimeException("Serialization mediator isn't initialized correctly.");
        }
        
        // todo opgave 2
        
        try {
            ObjectOutputStream out;
            FileOutputStream stream;
            stream = new FileOutputStream((File) this.props.get("file"));
            System.out.println(this.props.get("file"));
            out = new ObjectOutputStream(stream);
            out.writeObject(admin);
            out.close();
            stream.close();
        } 
        catch (IOException exc) { 
            exc.printStackTrace();
            System.out.println("niet opgeslagen");
        }
    }

    @Override
    public boolean configure(Properties props) {
        this.props = props;
        return isCorrectlyConfigured();
    }

    @Override
    public Properties config() {
        return props;
    }

    /**
     *
     * @return true if config() contains at least a key "file" and the
     * corresponding value is a File-object, otherwise false
     */
    @Override
    public boolean isCorrectlyConfigured() {
        if (props == null) {
            return false;
        }
        if (props.containsKey("file")) {
            return props.get("file") instanceof File;
        } else {
            return false;
        }
    }
}
