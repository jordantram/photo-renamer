package photo_renamer;

import java.io.File;
import java.util.ArrayList;

/**
 * An object that uses Memento to store its state.
 */
public class Originator {
	
	/** The current state */
	private File state;

	/**
	 * Setter method which sets the current state given a File.
	 * @param state
	 * 			The current state.
	 */
	public void setState(File state) {
		this.state = state;
	}

	/**
	 * Getter method which gets the current state.
	 * @return the current state.
	 */
	public File getState() {
		return this.state;
	}

	/**
	 * Create a Memento object to hold the current state.
	 * @return A Memento object.
	 */
	public Memento saveStateToMemento() {
		return new Memento(this.state);
	}

	/**
	 * Get the state from a given Memento object.
	 * @param Memento
	 * 			The given Memento object to get the state from.
	 */
	public void getStateFromMemento(Memento Memento) {
		this.state = Memento.getState();
	}
}
