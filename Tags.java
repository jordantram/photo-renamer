package photo_renamer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/*
 * This class is used to hold and keep a record of all the tags ever used.
 * This class follows the SINGLETON DESIGN PATTERN, as we would only ever want
 * one instance of this "container" class because we only want one thing holding every tag.
 */

/**
 * A list of tags which will keep record of all tags ever used (FOLLOWS SINGLETON DESIGN PATTERN)
 */
public class Tags {
	
	/** The list of tags. */
	private Set<String> allTags = new HashSet<>();
	
	/** The logger that records all the changes. */
	private static final Logger logger = Logger.getLogger(Image.class.getName());
	
	/** The console handler that communicates all the changes to the user. */
    private static final Handler consoleHandler = new ConsoleHandler();
	
    /** The single instance of Tags. */
	private static Tags instance = new Tags();
	
	/**
	 * A collection of image tags.
	 */
	public Tags() { 
		logger.setLevel(Level.ALL);
	    consoleHandler.setLevel(Level.ALL);
	    logger.addHandler(consoleHandler);
	}
	
	/**
	 * Getter method for the instance.
	 * @return The single instance of Tags.
	 */
	public static Tags getInstance() {
		return instance;
	}
	
	/**
	 * Getter method for the list of tags (instance variable).
	 * @return The list of all tags, allTags.
	 */
	public Set<String> getAllTags() {
		return this.allTags;
	}
	
	/**
	 * Get all the tags ever used and store it in the parameter.
	 * @param path
	 * 			The location of the file to read from
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public void readFromFile(String path) throws ClassNotFoundException{
		try {
            InputStream file = new FileInputStream(path);
            InputStream buffer = new BufferedInputStream(file);
            ObjectInput input = new ObjectInputStream(buffer);
            
            this.allTags = (Set<String>) input.readObject();
            input.close();
        } catch (IOException ex) {
        }
	}
	
	/**
	 * Add all the new tags (if any) to the collection of tags.
	 * @param d
	 * 			The directory from which we are getting the tags
	 * @param path
	 * 			The path of the text file to write to
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void addToTagList(Directory d, String path) throws IOException, ClassNotFoundException{
		Set<String> getNewTags = new HashSet<>();
		getNewTags = d.getTags();
		for(String tags: getNewTags) {
			allTags.add(tags);
		}
		
		OutputStream file = new FileOutputStream(path);
        OutputStream buffer = new BufferedOutputStream(file);
        ObjectOutput output = new ObjectOutputStream(buffer);
        
        output.writeObject(this.allTags);
        output.close();
	}
	
	/**
	 * Adds a given list of tags to the master tag list.
	 * @param tag
	 * 			The array of tags to be added.
	 * @param path
	 * 			The file in which the changes will be recorded.
	 * @throws IOException
	 */
	public void addSingleTagToList(String[] tag, String path) throws IOException {
		for (String tags: tag){
			allTags.add("@" + tags);
		}
		
		OutputStream file = new FileOutputStream(path);
        OutputStream buffer = new BufferedOutputStream(file);
        ObjectOutput output = new ObjectOutputStream(buffer);
        
        output.writeObject(this.allTags);
        output.close();
	}
	
	/**
	 * Clears the entire master tag list.
	 */
	public void deleteAllTags() {
		this.allTags.clear();
	}
	
	public static void main(String [] args) throws ClassNotFoundException, IOException {
		
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.showOpenDialog(null);
		File d = fileChooser.getSelectedFile();
		Directory x = new Directory(d);
		
		// Testing addToTagList / writing to file
		Tags test = Tags.getInstance();
		test.addToTagList(x, "C:/Users/Jordan/Documents/CSC207/a3 test/a.txt");
		System.out.println(test.getAllTags());
		
		// Testing read from file
		test.readFromFile("C:/Users/Jordan/Documents/CSC207/a3 test/b.txt");
		System.out.println(test.getAllTags());	
	}
}
