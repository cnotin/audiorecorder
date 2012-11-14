package se.ltu.M7017E.lab1;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class NameChangeJDialog extends JDialog{

	private String filename;
	private JButton saveButton;
	private JTextField jtf;
	private NameChangeJDialog me=this;
	private JList list;
	public NameChangeJDialog(String filename, JList list){
		this.list=list;
		this.setTitle("Name the file");

		this.setLocationRelativeTo(null);
		JPanel panel = new JPanel();
		this.filename=filename;
		jtf = new JTextField(filename);
		jtf.setPreferredSize(new Dimension(300, 30));
		
		
		saveButton= new JButton("OK");
		this.configureButton(saveButton);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		me.addWindowListener(new WindowAdapter() 
		{
		  public void windowClosed(WindowEvent e)
		  {
		    System.out.println("jdialog window closed event received");
		    refreshList();
		  }

		 
		});
		
		panel.add(jtf);
		panel.add(saveButton);
		this.add(panel);
		
		this.setVisible(true);
		this.pack();
	}
	
	
	
	public void configureButton(JButton button){
		
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				System.out.println(filename);
				File source=new File(filename);
				
				System.out.println(source.getAbsolutePath());
				String newName=jtf.getText();
				source.renameTo(new File(jtf.getText()));
				System.out.println(newName);
				System.out.println(source.getName());
				System.out.println(me.list.getLastVisibleIndex());
				System.out.println(me.list.getModel().getElementAt(me.list.getLastVisibleIndex()));
				DefaultListModel model=(DefaultListModel) me.list.getModel();
				model.addElement(newName);
				
				me.refreshList();
				me.dispose();
			
			}
		});
		
	}
	
	public void refreshList(){
		
		// look in current directory for recording files
		File currDir = new File(".");
		// only ogg files
		File[] oggFiles = currDir.listFiles();
		DefaultListModel model=new DefaultListModel();
		for (File file : oggFiles) {
			if(file.getName().endsWith(".ogg"))
				model.addElement(file.getName());
			
		}
		me.list.setModel(model);
		
		
		
	}
	
}
