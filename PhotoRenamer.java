package photo_renamer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class PhotoRenamer implements WindowListener{
	
	private final JFrame jframe;
	private final JPanel buttonContainer;
	private final JPanel imagePanel;
	private final JPanel listPanel;
	private final JPanel buttonContainer2;
	private final JButton exitButton;
	private final JButton selectImage;
	
	private final JList<String> tagList;
	
	private final JTextArea filePath;
	private final JTextArea tag;
	
	private final JButton addNewTag;
	private final JButton addExistingTag;
	private final JButton deleteTag;
	private final JScrollPane scrollPane;
	
	public int size;
	public boolean replace;
	public String [] converted;
	public String [] existingTagsToAdd;
	public String [] tagsToDelete;
	public String [] newTagToAdd;
	
	private final JButton selectDirectory;
	private final JLabel imageName;
	private final JLabel imageLabel;
	public Tags allTags;
	public File imageFile;
	public File directoryFile;
	public Image selectedImage;
	public Directory selectedDirectory;
	public String pathImage;
	public String pathDirectory;
	private final ImageIcon icon;
	private BufferedImage img;
	
	public PhotoRenamer () {
		this.img = null;
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(15,15,15,15);
		
		this.scrollPane = new JScrollPane();
		this.scrollPane.setPreferredSize(new Dimension(175, 401));
		this.tag = new JTextArea(2, 30);
		this.filePath = new JTextArea(1, 12);
		this.filePath.setText("ENTER ONE TEXTFILE FOR EACH IMAGE BUT KEEP A CONSTANE TEXTFILE FOR EACH DIRECTORY");
		this.filePath.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				selectImage.setVisible(true);
				selectDirectory.setVisible(true);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				if (filePath.getText().equals("")){
					selectImage.setVisible(false);
					selectDirectory.setVisible(false);
				}
					
				
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				selectImage.setVisible(true);
				selectDirectory.setVisible(true);
			}
			
		});
		
		this.tagList = new JList<String>();
		this.tagList.setFixedCellWidth(175);
		this.tagList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.allTags = new Tags();
		scrollPane.setViewportView(tagList);
		this.pathImage = new String();
		this.pathDirectory = new String();
		this.replace = true;

		
		this.jframe = new JFrame();
		this.jframe.setPreferredSize(new Dimension(1050, 700));
		this.imageName = new JLabel();
		this.exitButton =  new JButton("exit");
		
		this.addNewTag = new JButton("add new tag");
		this.addNewTag.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					allTags.addToTagList(selectedDirectory, pathDirectory);
				} catch (ClassNotFoundException | IOException e1) {
				}
				
				newTagToAdd = tag.getText().trim().split(" ");
				size = allTags.getAllTags().size();
				converted = allTags.getAllTags().toArray(new String[size + newTagToAdd.length + 
				                                                    selectedImage.caretaker.mementoList.size()]);
				for (int i=0; i<newTagToAdd.length; i++) {
					converted[size + i] = "@" + newTagToAdd[i];
				}
				
				for (int j=0; j < selectedImage.caretaker.mementoList.size(); j++) {
					converted[size + newTagToAdd.length + j] = selectedImage.caretaker.get(j).getState().getName();
				}
				tagList.setListData(converted);
				try {
					allTags.addSingleTagToList(newTagToAdd, pathDirectory);
				} catch (IOException e1) {
				}
				imageName.setText(selectedImage.imageFile.getName());
			}
			
		});
		this.addExistingTag = new JButton("add existing tag(s)");
		this.addExistingTag.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				replace = true;
				size = tagList.getSelectedValuesList().size();
				existingTagsToAdd = tagList.getSelectedValuesList().toArray(new String[size]);
				for (int b=0; b < existingTagsToAdd.length; b++) {
					if (existingTagsToAdd[b].startsWith("@")) {
						existingTagsToAdd[b] = existingTagsToAdd[b].substring(1);
						replace = false;
					}
				}
				
				size = allTags.getAllTags().size();
				converted = allTags.getAllTags().toArray(new String[size + selectedImage.caretaker.mementoList.size()]);
				for (int j=0; j < selectedImage.caretaker.mementoList.size(); j++) {
					converted[size + j] = selectedImage.caretaker.get(j).getState().getName();
				}
				tagList.setListData(converted);
				try {
					if (replace == true) {
						selectedImage.revertName(selectedImage.getIndex(existingTagsToAdd[0]));
					}
					else {
						selectedImage.addTag(existingTagsToAdd);
					}
				} catch (IOException e1) {
				}
				imageName.setText(selectedImage.imageFile.getName());
			}
		});
		
		this.deleteTag = new JButton("delete tag");
		this.deleteTag.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tagsToDelete = tag.getText().trim().split(" ");
				
				size = allTags.getAllTags().size();
				converted = allTags.getAllTags().toArray(new String[size + selectedImage.caretaker.mementoList.size()]);
				for (int j=0; j < selectedImage.caretaker.mementoList.size(); j++) {
					converted[size + j] = selectedImage.caretaker.get(j).getState().getName();
				}
				tagList.setListData(converted);
				
				try {
					selectedImage.deleteTag(tagsToDelete);
				} catch (IOException e1) {
				}
				imageName.setText(selectedImage.imageFile.getName());
			}
			
		});
		
		this.icon = new ImageIcon();
		this.imageLabel = new JLabel(this.icon);
		

		this.exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);;
			}
		});
		
		this.selectImage = new JButton("Select Image");
		this.selectImage.setVisible(false);
		this.selectImage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				imageFile = showImageFileChooser();
				try {
					img = ImageIO.read(imageFile);
				} catch (IOException e1) {
					
				}
				pathImage = filePath.getText();
				try {
					selectedImage = new Image(imageFile, pathImage);
				} catch (ClassNotFoundException | IOException e1) {
				}
				icon.setImage(img.getScaledInstance(400, 400, 20));
				imageName.setText(imageFile.getName());
				imageName.setHorizontalAlignment(JLabel.CENTER);
			}
		});
		
		this.selectDirectory = new JButton("Select Directory");
		this.selectDirectory.setVisible(false);
		this.selectDirectory.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				directoryFile = showDirectoryFileChooser();
				try {
					selectedDirectory = new Directory(directoryFile);
				} catch (ClassNotFoundException | IOException e1) {
				}
				try {
					pathDirectory = filePath.getText();
					allTags.readFromFile(pathDirectory);
				} catch (ClassNotFoundException e1) {
				}
				size = allTags.getAllTags().size();
				converted = allTags.getAllTags().toArray(new String[size]);
				tagList.setListData(converted);
			}
		});
		
		
		
		this.buttonContainer = new JPanel();
		this.buttonContainer.add(this.selectImage);
		this.buttonContainer.add(this.selectDirectory);
		this.buttonContainer.add(this.filePath);
		this.buttonContainer.add(this.exitButton);
		this.jframe.add(this.buttonContainer, BorderLayout.NORTH);
		
		gbc.gridx = 2;
		gbc.gridy = 0;
		this.buttonContainer2 = new JPanel(new GridBagLayout());
		this.buttonContainer2.add(this.tag, gbc);
		gbc.gridx = 1;
		gbc.gridy = 1;
		this.buttonContainer2.add(this.addNewTag, gbc);
		gbc.gridx = 3;
		gbc.gridy = 1;
		this.buttonContainer2.add(this.deleteTag, gbc);
		this.jframe.add(this.buttonContainer2, BorderLayout.SOUTH);
		
		this.imagePanel = new JPanel();
		this.imagePanel.add(this.imageLabel);
		this.imagePanel.add(this.imageName);
		this.jframe.add(this.imagePanel, BorderLayout.CENTER);
		
		this.listPanel = new JPanel();
		this.listPanel.add(this.scrollPane);
		this.listPanel.add(this.addExistingTag);
		this.jframe.add(this.listPanel, BorderLayout.WEST);
		
		this.jframe.pack();
		this.jframe.setVisible(true);
		
		//Ask the window to listen to its own events
		this.jframe.addWindowListener((WindowListener)this);
	}
	
	/**
	 * Asks the user to choose a File(image file).
	 * @return
	 * 		The File(image file).
	 */
	public File showImageFileChooser() {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnVal = chooser.showOpenDialog(jframe);
		while (returnVal != JFileChooser.APPROVE_OPTION) {
			chooser.showOpenDialog(jframe);
		}
		return chooser.getSelectedFile();
	}
	
	/**
	 * Asks the user to choose a File(directory file).
	 * @return
	 * 		The File(directory file).
	 */
	public File showDirectoryFileChooser() {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = chooser.showOpenDialog(jframe);
		while (returnVal != JFileChooser.APPROVE_OPTION) {
			chooser.showOpenDialog(jframe);
		}
		return chooser.getSelectedFile();
	}
	
	public void windowClosing(WindowEvent e) {
        System.exit(0);
	}

	public void windowOpened(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	
	public static void main(String [] args) {
		PhotoRenamer x = new PhotoRenamer();
	}
}
