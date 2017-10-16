package photo_renamer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
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

/**
 * A directory that represents the collection of image files.
 */
public class Directory {
	
	/** The directory file that is given by the user. */
	public File directoryFile;

	/**
	 * A directory of images
	 * @param directoryFile
	 * 					The given directory file.
	 */
	public Directory(File directoryFile) throws ClassNotFoundException, IOException {
		this.directoryFile = directoryFile;
	}
	
	/**
	 * Find and return a list of image files (".JPG") located anywhere in a given directory.
	 * @param directory
	 * 				The directory
	 * @param imageFiles
	 * 				The list of image files.
	 * @return The image files located in this directory.
	 */
	public ArrayList<String> getImageFiles(File directory, ArrayList<String> imageFiles) {
		File[] items = directory.listFiles();
		for (File x: items) {
			if (x.isFile() && (x.getName().endsWith(".JPG") || x.getName().endsWith(".jpg"))) {
				imageFiles.add(x.getName());
			}
			else if (x.isDirectory()) {
				this.getImageFiles(x, imageFiles);
			}
		}
		return imageFiles;
	}
	
	/**
	 * Find and return a list of the names of all the image files that contain a particular set of tags.
	 * @param tags
	 * 			The list of tags.
	 * @return The names of the image files.
	 */
	public ArrayList<String> getImagesWithTags (String[] tags) {
		ArrayList<String> imageFiles = new ArrayList<>();
		imageFiles = this.getImageFiles(this.directoryFile, imageFiles);
		ArrayList<String> imageWithTags = new ArrayList<>();
		for (String images: imageFiles) {
			for (String tag : tags) {
				if (images.contains(tag) && imageWithTags.contains(images) == false) {
					imageWithTags.add(images);
				}
			}
		}
		return imageWithTags;
	}
	
	/**
	 * Find and return all the individual tags located in the current directory file.
	 * @return A list of the tags used in images located in the directory.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Set<String> getTags () throws IOException, ClassNotFoundException{
		Set<String> tags = new HashSet<>();
		ArrayList<String> imageTags = new ArrayList<>();
		String [] words;
		String ending = new String();
		imageTags = this.getImageFiles(this.directoryFile, imageTags);
		for (String usedTags : imageTags) {
				if (usedTags.endsWith(".JPG")) {
					ending = ".JPG";
				}
				else {
					ending = ".jpg";
				}
				words = usedTags.split(" ");
				for (String word: words) {
					if (word.substring(0, 1).equals("@")) {
						tags.add(word.split(ending)[0]);
					}
				}
		}
		return tags;
	}
	
	public static void main(String [] args) throws ClassNotFoundException, IOException {
		
		
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.showOpenDialog(null);
		File d = fileChooser.getSelectedFile();
		
		// Testing getImageFiles
		Directory x = new Directory(d);
		ArrayList<String> images = new ArrayList<>();
		images = x.getImageFiles(d, images);
		System.out.println(images);
		
		// Testing getTags
		Set<String> tags = x.getTags();
		System.out.println(tags);
		
		// Testing Tags.java
		Tags test = Tags.getInstance();
		System.out.println(test.getAllTags()); // should be empty at this point
		test.addToTagList(x, "C:/Users/Jordan/Documents/CSC207/a3 test/c.txt");
		System.out.println(test.getAllTags()); // should have stuff
	}
}

