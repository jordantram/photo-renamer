package photo_renamer;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.swing.JFileChooser;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TagsTest {
	public File d;
	public String filePath;
	public Tags allTags;
	
	/**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }
	
    @Before
    public void setUp() throws Exception {
        JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.showOpenDialog(null);
		this.d = fileChooser.getSelectedFile();
		// This is my personal file path, this represents the place where the master tag list is stored
		this.filePath = "/Users/PrinceMinhas/Documents/test/a.txt";
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }
	
	@Test
	public void testAddToTagList() throws IOException {
		String [] x = {"a", "b", "c"};
		this.allTags.addSingleTagToList(x, this.filePath);
		int size = this.allTags.getAllTags().size();
		org.junit.Assert.assertArrayEquals(x, this.allTags.getAllTags().toArray(new String[size]));
	}
	
	@Test
	public void testSameInstance() throws IOException {
		String [] x = {"a", "b", "c"};
		this.allTags.addSingleTagToList(x, this.filePath);
		Tags newtracker = new Tags();
		int size = this.allTags.getAllTags().size();
		String [] c = this.allTags.getAllTags().toArray(new String[size]);
		int size2 = newtracker.getAllTags().size();
		String [] d = newtracker.getAllTags().toArray(new String[size2]);
		org.junit.Assert.assertArrayEquals(c, d);
	}
	
	@Test
	public void testReadFromFile() throws IOException, ClassNotFoundException {
		String [] x = {"a", "b", "c"};
		this.allTags.addSingleTagToList(x, this.filePath);
		this.allTags.deleteAllTags();
		this.allTags.readFromFile(this.filePath);
		int size = this.allTags.getAllTags().size();
		org.junit.Assert.assertArrayEquals(x, this.allTags.getAllTags().toArray(new String[size]));
	}
	
	@Test
	public void testDeleted() throws IOException, ClassNotFoundException {
		String [] x = {"a", "b", "c"};
		this.allTags.addSingleTagToList(x, this.filePath);
		this.allTags.deleteAllTags();
		this.allTags.readFromFile(this.filePath);
		Directory h = new Directory(this.d);
		this.allTags.addToTagList(h, this.filePath);
		
		org.junit.Assert.assertTrue(this.allTags.getAllTags().contains("a") && 
				this.allTags.getAllTags().contains("b") && this.allTags.getAllTags().contains("c"));
	}

}
