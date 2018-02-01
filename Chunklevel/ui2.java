import java.util.ArrayList;
import javax.swing.*;
import java.io.*;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.*;
import java.util.List;
import java.awt.event.*;
import javax.swing.border.*;

public class ui2
{

	static File[] listOfFiles;		
	static int numfiles,currentmenu;		
	static int []button;
	static JButton []button1;
	static JButton []button2;
	static JButton up= new JButton("Upload");	
	static JPanel file_name = new JPanel();
	static JPanel right_side=new JPanel();
	static JFileChooser chooser;
	static String choosertitle;
   	static JButton folderchooser;
	static Border emptyBorder = BorderFactory.createEmptyBorder();	
	static JFrame jf = new JFrame();
	static String base;
	static List<ListingFile> listfromserver;
	static String dirname;
	static ActionListener listener = new ActionListener() 
	{
	        @Override
        	public void actionPerformed(ActionEvent e) 
		{
			if(currentmenu==0)
			{
		    		if (e.getSource() instanceof JButton) {
					String buttonclicked=((JButton) e.getSource()).getText();
					if(buttonclicked.equals("Choose Folder"))
					{
						chooser = new JFileChooser(); 
	  					chooser.setCurrentDirectory(new java.io.File("."));
						chooser.setDialogTitle(choosertitle);
						chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						chooser.setAcceptAllFileFilterUsed(false);

						if (chooser.showOpenDialog(file_name) == JFileChooser.APPROVE_OPTION)
						{
							choosertitle=chooser.getSelectedFile().toString();
						 	Filllist(choosertitle);
						}
						else 
						{
	 					}
					}	
					else
					{
						int key=Integer.parseInt(((JButton) e.getSource()).getName());
							if(listOfFiles[key].isDirectory())
							{
								Filllist(listOfFiles[key].getAbsolutePath());
								return ;
							}
						if(button[key]==1)
						{
							button1[key].setVisible(false);
							button2[key].setVisible(true);
							button[key]=0;
			       			}
						else if(button[key]==0)
						{
							button1[key].setVisible(true);
							button2[key].setVisible(false);
							button[key]=1;
						}
					}
		    		}
			}
			else
			{
			     try
				{
				String buttonclicked=((JButton) e.getSource()).getText();				
				if(buttonclicked.equals(".."))
				{
					if(!dirname.equals(base))
					dirname=dirname.substring(0,dirname.lastIndexOf('/'));
					listfromserver=ListFiles.ListFilesandDirectory(dirname);
					Filllistfromserver();						
					return ;
				}	
				else
				{
						int key=Integer.parseInt(((JButton) e.getSource()).getName());
						if((listfromserver.get(key)).isdirectory)
						{
							listfromserver=ListFiles.ListFilesandDirectory(button1[key].getText());
							dirname=button1[key].getText();
							Filllistfromserver();
							return ;				
						}
						if(button[key]==1)
						{
							button1[key].setVisible(false);
							button2[key].setVisible(true);
							button[key]=0;
			       		}
						else if(button[key]==0)
						{
							button1[key].setVisible(true);
							button2[key].setVisible(false);
							button[key]=1;
						}
						
			}
			}
			catch(Exception ex){
								ex.printStackTrace();
						}
			}
        	}
    	};

	static void Filllist(String path)
	{
		System.out.println(path);
		path=path.replace('\\','/');
		File folder=new File(path);
		listOfFiles= folder.listFiles(new FileFilter() {
    		@Override
    		public boolean accept(File file) {
		        return !file.isHidden();
		    }
		});
		file_name.removeAll();
		file_name.revalidate();
		file_name.repaint();
		right_side.removeAll();
		right_side.revalidate();
		right_side.repaint();
		numfiles=listOfFiles.length;
		button=new int[numfiles];
		button1 = new JButton[numfiles];
		button2 = new JButton[numfiles];
		for(int i=0;i<numfiles;i++)
			button[i]=1;
		for (int i=0; i<numfiles; i++)
		{
			button2[i]=new JButton();
			button2[i].setName(new Integer(i).toString());
			button2[i].setText(listOfFiles[i].getName());
			button2[i].setPreferredSize(new Dimension(200, 30));			
			button2[i].addActionListener(listener);
			button2[i].setBorder(emptyBorder);
			button2[i].setContentAreaFilled(false);
			button2[i].setBorder(BorderFactory.createCompoundBorder(
    			BorderFactory.createLineBorder(Color.CYAN, 5), 
			BorderFactory.createEmptyBorder(5, 5, 10, 10)));
			button2[i].setBorderPainted(false);
			button2[i].setHorizontalAlignment(SwingConstants.CENTER);
			button2[i].setVisible(false);	
			right_side.add(button2[i]);
		}		
		folderchooser=new JButton();
		folderchooser.setText("Choose Folder");
		folderchooser.setPreferredSize(new Dimension(200,30));
		folderchooser.addActionListener(listener);
		folderchooser.setHorizontalAlignment(SwingConstants.CENTER);		
		file_name.add(folderchooser);
		for (int j=0; j<numfiles; j++)
		{
			button1[j]=new JButton();
			button1[j].setName(new Integer(j).toString());
			button1[j].setText(button2[j].getText()+"\n");
			button1[j].setPreferredSize(new Dimension(200, 30));						
			button1[j].addActionListener(listener);
			button1[j].setBorder(emptyBorder);
			file_name.add(button1[j]);
			button1[j].setOpaque(false);
			button1[j].setContentAreaFilled(false);
			button1[j].setBorder(BorderFactory.createCompoundBorder(
    			BorderFactory.createLineBorder(Color.CYAN, 5), 
			BorderFactory.createEmptyBorder(5, 5, 10, 10)));
			button1[j].setBorderPainted(false);
			button1[j].setHorizontalAlignment(SwingConstants.CENTER);
		}		
		System.out.println("Hi");
		jf.revalidate();
	}
	static void Filllistfromserver()
	{
		file_name.removeAll();
		file_name.revalidate();
		file_name.repaint();
		right_side.removeAll();
		right_side.revalidate();
		right_side.repaint();
		numfiles=listfromserver.size()-1;
		button=new int[numfiles];
		button1 = new JButton[numfiles];
		button2 = new JButton[numfiles];
		for(int i=0;i<numfiles;i++)
			button[i]=1;
		for (int i=0; i<numfiles; i++)
		{
			button2[i]=new JButton();
			button2[i].setName(new Integer(i).toString());
			button2[i].setText(((listfromserver.get(i)).name).substring(((listfromserver.get(numfiles)).name).length()));
			button2[i].setPreferredSize(new Dimension(200, 30));			
			button2[i].addActionListener(listener);
			button2[i].setBorder(emptyBorder);
			button2[i].setContentAreaFilled(false);
			button2[i].setBorder(BorderFactory.createCompoundBorder(
    			BorderFactory.createLineBorder(Color.CYAN, 5), 
			BorderFactory.createEmptyBorder(5, 5, 10, 10)));
			button2[i].setBorderPainted(false);
			button2[i].setHorizontalAlignment(SwingConstants.CENTER);
			button2[i].setVisible(false);	
			right_side.add(button2[i]);
		}		
		folderchooser=new JButton();
		folderchooser.setText("..");
		folderchooser.setPreferredSize(new Dimension(200,30));
		folderchooser.addActionListener(listener);
		folderchooser.setHorizontalAlignment(SwingConstants.CENTER);		
		file_name.add(folderchooser);
		for (int j=0; j<numfiles; j++)
		{
			button1[j]=new JButton();
			button1[j].setName(new Integer(j).toString());
			button1[j].setText(button2[j].getText()+"\n");
			button1[j].setPreferredSize(new Dimension(200, 30));						
			button1[j].addActionListener(listener);
			button1[j].setBorder(emptyBorder);
			file_name.add(button1[j]);
			button1[j].setOpaque(false);
			button1[j].setContentAreaFilled(false);
			button1[j].setBorder(BorderFactory.createCompoundBorder(
    			BorderFactory.createLineBorder(Color.CYAN, 5), 
			BorderFactory.createEmptyBorder(5, 5, 10, 10)));
			button1[j].setBorderPainted(false);
			button1[j].setHorizontalAlignment(SwingConstants.CENTER);
		}		
		System.out.println("Hi");
		jf.revalidate();
	}
	
	public static void main(String args[])
	{	
		base=args[1];
		JPanel tex=new JPanel();		
		Border border = BorderFactory.createLineBorder(Color.BLUE, 1);
		final JLabel l1=new JLabel("Files in System");
		final JLabel l2=new JLabel("Files selected");
		l1.setBorder(border);
		l2.setBorder(border);		
		tex.add(l1);
		tex.setLayout(null);
		JPanel upload=new JPanel(); 		
		up.setPreferredSize(new Dimension(150, 50));
		chooser = new JFileChooser(); 
		chooser.setCurrentDirectory(new java.io.File("."));
		//chooser.setDialogTitle(choosertitle);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		class MenuActionListener implements ActionListener 
		{
			public void actionPerformed(ActionEvent e) 
			{
				try
				{
				System.out.println("Selected: " + e.getActionCommand());
				if(e.getActionCommand().equals("Upload"))
				{
					currentmenu=0;
					up.setText("Upload");
					l1.setText("Files in System");
					Filllist(choosertitle);
				}
				else if(e.getActionCommand().equals("Download"))
				{
					currentmenu=1;
					listfromserver=ListFiles.ListFilesandDirectory(base);
					up.setText("Download");
					Filllistfromserver();
					dirname=base;
					l1.setText("Files in Cloud");
				}
				else 
				{
					currentmenu=2;
					listfromserver=ListFiles.ListFilesandDirectory(base);
					up.setText("Delete");
					Filllistfromserver();
					dirname=base;
					l1.setText("Files in Cloud");
				}	
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
	  		}
		}
				
		currentmenu=0;
		JMenuBar jmb = new JMenuBar();
		JMenu menu=new JMenu();		
		menu = new JMenu("Options");
		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription("The only menu in this program that has menu items");
		jmb.add(menu);		
		JMenuItem uploadmenu = new JMenuItem("Upload",KeyEvent.VK_T);
		JMenuItem download = new JMenuItem("Download",KeyEvent.VK_T);		
		JMenuItem delete = new JMenuItem("Delete",KeyEvent.VK_T);
		uploadmenu.addActionListener(new MenuActionListener());
		download.addActionListener(new MenuActionListener());
		delete.addActionListener(new MenuActionListener());		
		menu.add(uploadmenu);
		menu.add(download);				
		menu.add(delete);		
		jmb.add(menu);			
		file_name.add(l1);
		right_side.add(l2);
		file_name.setLayout(new BoxLayout(file_name, BoxLayout.Y_AXIS));
		right_side.setLayout(new BoxLayout(right_side, BoxLayout.Y_AXIS));
		choosertitle="G:/";
		Filllist(choosertitle);
		for (int i = 0; i < numfiles; i++) 
		{
      			if (listOfFiles[i].isFile()) 
        			System.out.println("File " + listOfFiles[i].getName());
    			else if (listOfFiles[i].isDirectory()) 
        			System.out.println("Directory " + listOfFiles[i].getName());
    		}
		
		

		JScrollPane scrollPane = new JScrollPane(file_name);
		JScrollPane scrollPane2 = new JScrollPane(right_side);
		//scrollPane.setPreferredSize(new Dimension(600, 600));				
                JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                           scrollPane,scrollPane2);
                splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(300);
		
		
		upload.add(up);		
		
		up.addActionListener( new ActionListener()
		{
    		@Override
    		public void actionPerformed(ActionEvent e)
    		{
			try
			{
				List<String> choosedfiles=new ArrayList<String>();

				if(currentmenu==0)
				{
					for(int i=0;i<numfiles;i++)
					{
						if(button[i]==0)
						{				
							choosedfiles.add(listOfFiles[i].toString());
						}
					}
					Upload.UploadFiles(base,choosedfiles);
					Filllist(choosertitle);
				}
				else if(currentmenu==1)
				{
					for(int i=0;i<numfiles;i++)
					{
						if(button[i]==0)
						{				
							String fname=button1[i].getText();
							choosedfiles.add(fname.substring(fname.lastIndexOf('/')+1));
						}
					}
					System.out.println("india"+dirname);
					DownloadFiles.DownloadFiles(dirname,choosedfiles);
					listfromserver=ListFiles.ListFilesandDirectory(dirname);
					Filllistfromserver();
				}
				else
				{
					for(int i=0;i<numfiles;i++)
					{
						if(button[i]==0)
						{				
							String fname=button1[i].getText();
							choosedfiles.add((listfromserver.get(i)).name);
						}
					}
					DeleteFiles.DeleteFileList(choosedfiles);
					listfromserver=ListFiles.ListFilesandDirectory(dirname);
					Filllistfromserver();
				}	
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
        	}
        	});
		jf.setJMenuBar(jmb);
		jf.add(splitPane,BorderLayout.CENTER);
		jf.add(upload,BorderLayout.PAGE_END);
		jf.setSize(800,500);
		jf.setVisible(true);
		jf.setLocationRelativeTo(null);
	}
}

