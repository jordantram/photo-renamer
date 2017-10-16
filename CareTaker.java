package photo_renamer;

import java.io.Serializable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * An object which holds stored Memento objects.
 */
public class CareTaker implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/** The list of Memento objects. */
	public List<Memento> mementoList = new ArrayList<Memento>();

	/**
	 * Add a given Memento object to the list.
	 * @param state
	 * 			Given Memento object to add.
	 */
	public void add(Memento state) {
		mementoList.add(state);
	}

	/**
	 * Get a Memento object from the list given an index.
	 * @param index
	 * 			Given index of the Memento list.
	 * @return
	 * 			The Memento object stored at the given index.
	 */
	public Memento get(int index) {
		return mementoList.get(index);
	}

	public static void main(String[] args) {
		Originator originator = new Originator();
		CareTaker caretaker = new CareTaker();

		// First state
		File test = new File("C:/Users/Jordan/Documents/CSC207/a3 test/KD GSW.jpg"); // place .jpg file here
		originator.setState(test);
		System.out.println("Current state/name of file: " + originator.getState().getName());
		caretaker.add(originator.saveStateToMemento());
		System.out.println("The last saved state: " + caretaker.get(0).getState().getName());
		System.out.println("File's current name: " + test.getName() + "\n");

		// Second state + renaming file to a new name
		File test2 = new File("C:/Users/Jordan/Documents/CSC207/a3 test/KD GSW 2.jpg"); // place a different .jpg file here
		test.renameTo(test2);
		test = test2;
		originator.setState(test);
		System.out.println("Current state/name of file: " + originator.getState().getName());
		caretaker.add(originator.saveStateToMemento());
		System.out.println("The last saved state: " + caretaker.get(1).getState().getName());
		System.out.println("File's current name: " + test.getName() + "\n");

		// Renaming file back to first state name
		System.out.println("First saved state file name: " + caretaker.get(0).getState().getName());
		File test3 = caretaker.get(0).getState();
		test.renameTo(test3);
		test = test3;
		System.out.println("File renamed to first state: " + test.getName());
	}
}