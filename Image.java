package photo_renamer;


import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

import java.util.ArrayList;

/**
 * An image file.
 */
public class Image {
	
	/** The image file that is given by the user. */
	public File imageFile;
	
	/** The logger that records all the changes. */
	private static final Logger logger =
            Logger.getLogger(Image.class.getName());
	
	/** The console handler that communicates all the changes to the user. */
    private static final Handler consoleHandler = new ConsoleHandler();
    
    /** The file that this image writes and reads from. */
    public File file;
    
    /** Extension of the image file. */
    public String ending;
    
    /** Originator which will allow saving to the memento CareTaker */
    public Originator originator;
    
    /** CareTaker which holds all the past states */
    public CareTaker caretaker; // holds all previous file names
    
	/**
	 * The image file the corresponds to a ".JPG" file.
	 * @param imageFile
	 * 				The given JPG file.
	 * @param filePath
	 * 				The path of the file that this image communicates with.
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public Image (File imageFile, String filePath) throws ClassNotFoundException, IOException{
		this.imageFile = imageFile;
		this.originator = new Originator();
		this.caretaker = new CareTaker();
		
		originator.setState(this.imageFile);
		caretaker.add(originator.saveStateToMemento());
		
		if (this.imageFile.getName().endsWith(".JPG")) {
			this.ending = ".JPG";
		}
		else {
			this.ending = ".jpg";
		}
		
		logger.setLevel(Level.ALL);
        consoleHandler.setLevel(Level.ALL);
        logger.addHandler(consoleHandler);

        this.file = new File(filePath);
        if (file.exists() && file.length() != 0) {
            readFromFile(filePath);
        } else {
        	file.createNewFile();
        } 
	}
	
	/**
	 * Read the list of previous tags from a file and set it to the parameter value.
	 * @param path
	 * 			The file from which the reading is to be done.
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public void readFromFile(String path) throws ClassNotFoundException{
		try {
			FileInputStream fis = new FileInputStream(path);
			ObjectInputStream ois = new ObjectInputStream(fis);
			CareTaker result = (CareTaker) ois.readObject();
			this.caretaker = result;
			ois.close();
        } catch (IOException ex) {
        }
	}
	
	/**
	 * Save the list of previous tags of this image to a file.
	 * @param filePath
	 * 				The file where the writing is to be done.
	 * @throws IOException
	 */
	public void saveToFile(String filePath) throws IOException {

		FileOutputStream fos = new FileOutputStream(filePath);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(this.caretaker);
		oos.close();
    }
	
	/**
	 * Add the given tag(s) to the current tag and log the event.
	 * @param tags
	 * 			The list of tag(s) that are to be added.
	 * @throws IOException
	 */
	public void addTag(String[] tags) throws IOException{
		String pathName = this.imageFile.toString();
		String[] parts = pathName.split(this.imageFile.getName());
		String nameOfFile = this.imageFile.getName().split(this.ending)[0];
		for (String item: tags) {
			nameOfFile += " " + "@" + item;
		}
		File newFile = new File(parts[0] + nameOfFile + this.ending);
		
		logger.log(Level.FINE, "Changed (added) tag from " + this.imageFile.getName() + 
				" to " + (newFile.getName()));
		
		this.imageFile.renameTo(newFile);
		this.imageFile = newFile;
		
		this.originator.setState(this.imageFile);
		this.caretaker.add(originator.saveStateToMemento()); // adds new file name to list of mementos
		this.saveToFile(this.file.getPath());
	}
	
	/**
	 * Delete the given tag(s) from the current tag and log the event.
	 * @param tags
	 * 			The list of tag(s) that are to be removed.
	 * @throws IOException
	 */
	public void deleteTag(String[] tags) throws IOException{
		String pathName = this.imageFile.toString();
		String[] parts = pathName.split(this.imageFile.getName());
		String nameOfFile = this.imageFile.getName().split(this.ending)[0];
		for (String item: tags) {
			if (nameOfFile.contains(item)) {
				nameOfFile = nameOfFile.replace(" " + "@" + item, "");
			}
		}
		File newFile = new File(parts[0] + nameOfFile + this.ending);
		
		logger.log(Level.FINE, "Changed (deleted) tag from " + this.imageFile.getName() + 
				" to " + (newFile.getName()));

		this.imageFile.renameTo(newFile);
		this.imageFile = newFile;
		
		this.originator.setState(this.imageFile);
		this.caretaker.add(originator.saveStateToMemento()); // adds new file name to list of mementos
		this.saveToFile(this.file.getPath());
	}

	/**
	 * Return a list of all the names this current image file has had.
	 * @return The list of previous names.
	 */
	public ArrayList<String> seePrevNames() {
		ArrayList<String> nameList = new ArrayList<String>();
		
		for (Memento m: this.caretaker.mementoList) {
			nameList.add(m.getState().getName());
		}
		
		return nameList;
	}
	
	
	/**
	 * Find and return a list of all the tags in the current image file.
	 * @return The list of tags in the name of the image file.
	 */
	public ArrayList<String> getTags() {
		String nameOfFile = this.imageFile.getName().split(this.ending)[0];
		String [] words = nameOfFile.split(" ");
		ArrayList<String> tags = new ArrayList<>();
		for (String word: words) {
			if (word.substring(0, 1).equals("@")) {
				tags.add(word);
			}
		}
		return tags;
	}
	
	/**
	 * Get the index of the particular previous name in the care taker.
	 * @param name
	 * 			The particular previous name of this file.
	 * @return
	 * 			The index of the previous name in the care taker.
	 */
	public int getIndex(String name) {
		ArrayList<String> nameList = this.seePrevNames();
		int index = 0;
		int increment = 0;
		for (String names: nameList) {
			if (names.equals(name)) {
				index = increment;
			}
			increment += 1;
		}
		return (index);
	}
	
	/**
	 * Replace the current name of this image with one of its previous names.
	 * @param index
	 * 			The index in caretaker where the old name was stored.
	 */
	public void revertName(int index) throws IOException {
		File newFile = caretaker.get(index).getState();
		logger.log(Level.FINE, "Replaced name from " + this.imageFile.getName() + 
				" to " + (newFile.getName()));
		this.imageFile.renameTo(newFile);
		this.imageFile = newFile;
		
		this.originator.setState(this.imageFile);
		this.caretaker.add(originator.saveStateToMemento()); // adds new file name to list of mementos
		this.saveToFile(this.file.getPath());
	}
	
	public static void main(String [] args) throws ClassNotFoundException, IOException {
		
		
		JFileChooser fileChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("JPEG file", "jpg", "jpeg");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.addChoosableFileFilter(filter);
		fileChooser.setAcceptAllFileFilterUsed(true);
		fileChooser.showOpenDialog(null);
		File image = fileChooser.getSelectedFile();
		
		Image a = new Image(image, "C:/Users/Jordan/Documents/CSC207/a3 test/a.txt");
		String tags [] = {"a"};
		a.addTag(tags);
		System.out.println(a.caretaker.get(0).getState().getName()); // should be original name of file
		System.out.println(a.caretaker.get(1).getState().getName()); // should be original name with "@a" at the end
		a.deleteTag(tags);
		System.out.println(a.caretaker.get(2).getState().getName()); // should be original name of the file (because we deleted the tag "@a")
		a.revertName(1);
		System.out.println(a.caretaker.get(3).getState().getName() + "\n"); // should be original name with "@a" at the end (because we reverted to caretaker[1] name)
		
		Image b = new Image(image, "C:/Users/Jordan/Documents/CSC207/a3 test/a.txt");
		System.out.println(b.caretaker.get(0).getState().getName()); 
		System.out.println(b.caretaker.get(1).getState().getName()); 
		System.out.println(b.caretaker.get(2).getState().getName()); 
		System.out.println(b.caretaker.get(3).getState().getName() + "\n"); 
		
		System.out.println(a.seePrevNames());
		
		
		// Users/PrinceMinhas/Documents/test/a.txt is the path of the text file that this specfic 
		// image communicates with.
		
		/* Previous test cases (need to change due to code refactoring)
		Image a = new Image(image, "/Users/PrinceMinhas/Documents/test/a.txt");
		Directory d = new Directory(image.getParentFile(),"/Users/PrinceMinhas/Documents/test/b.txt");
		String tags [] = {"a"};
		a.deleteTag(tags);
		String tags2 [] = {"b"};
		a.deleteTag(tags2);
		a.replaceTag("photo @a @b @c @d.JPG");
		String tags3 [] = {"e"};
		a.addTag(tags3);
		d.addToTagList();
		System.out.println(Directory.allTags);
		System.out.println(a.seePrevTags());
		System.out.println(a.imageFile.getName());
		*/
	}
}
