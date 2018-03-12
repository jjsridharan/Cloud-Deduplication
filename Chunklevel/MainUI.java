import java.util.ArrayList;
import javax.swing.*;
import java.io.*;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.*;
import java.util.List;
import java.awt.event.*;
import javax.swing.border.*;

public class MainUI
{
	static JPopupMenu menu,upmenu;
	static JLabel l1=new JLabel("Files in System");
	static JLabel l2=new JLabel("Files in Cloud");
	static JLabel l3=new JLabel("Files selected");
	static JMenuItem item;
	static File[] listOfFiles;		
	static int numfiles,numfilesserver,currentmenu;		
	static int []button;
	static JButton []button1;
	static JButton []button2;
	static JButton []button3;
	static JButton up= new JButton("Upload");	
	static JPanel file_name = new JPanel();
	static JPanel right_side=new JPanel();
	static JPanel cloud=new JPanel();
	static JFileChooser chooser;
	static String choosertitle;
   	static JButton folderchooser;
	static JButton folderchooser1;
	static Border emptyBorder = BorderFactory.createEmptyBorder();	
	static JFrame jf = new JFrame();
	static String base,username;
	static List<ListingFile> listfromserver;
	static String dirname;
	static JSplitPane splitPane2;
	static JSplitPane splitPane;
	static JSplitPane splitPane3;
	static JTextField tf=new JTextField();
	static JButton jb=new JButton("Create Folder");

     static class RTButton extends JButton
	{
	boolean upload;
	public RTButton(boolean isdirectory,boolean upload) 
	{
		super();
		this.upload=upload;
		if(isdirectory)
		{
		if(!upload)
		{
			menu = new JPopupMenu("Popup");
			addMouseListener(new PopupTriggerListener());
			item = new JMenuItem("Dowload");
			item.addActionListener(new ActionListener() 
			{
				public void actionPerformed(ActionEvent e) 
				{
					try
					{
						String folder=((JButton) menu.getInvoker()).getText();
						String log="Downloading "+folder;
						System.out.println(Client.LogActivity(username,log));
						DownloadFiles.DownloadDirectory(folder,folder.substring(folder.lastIndexOf('/')+1));
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
	   		});
	    		menu.add(item);

			item = new JMenuItem("Delete");
			item.addActionListener(new ActionListener() 
			{
				public void actionPerformed(ActionEvent e) 
				{
					try
					{
						String folder=((JButton) menu.getInvoker()).getText();				
						String log="Deleting "+folder;
						System.out.println(Client.LogActivity(username,log));
						DeleteFiles.DeleteDirectory(folder,folder.substring(folder.lastIndexOf('/')));
						listfromserver=ListFiles.ListFilesandDirectory(dirname);
						Filllist2();
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
			});
	    		menu.add(item);
		}
		else
		{
			upmenu = new JPopupMenu("Popup");
			addMouseListener(new PopupTriggerListener());
			item = new JMenuItem("Upload");
			item.addActionListener(new ActionListener() 
			{
				public void actionPerformed(ActionEvent e) 
				{
					try
					{
						Integer folder=Integer.parseInt(((JButton) upmenu.getInvoker()).getName());
						String log="Uploading "+listOfFiles[folder].getAbsolutePath()+"to "+dirname;
						System.out.println(Client.LogActivity(username,log));
						Upload.UploadDirectory(dirname,listOfFiles[folder].getAbsolutePath());
						System.out.println(listOfFiles[folder].getAbsolutePath());
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
	   		});
	    		upmenu.add(item);
		}		
	}
    }
    class PopupTriggerListener extends MouseAdapter 
    {
      public void mousePressed(MouseEvent ev) 
	 {
        if (ev.isPopupTrigger()) 
	   {
	   RTButton buttonclicked=(RTButton)ev.getComponent();
	   if(buttonclicked.upload==false)
          menu.show(ev.getComponent(), ev.getX(), ev.getY());
          else
          upmenu.show(ev.getComponent(), ev.getX(), ev.getY());
        }
      }
      public void mouseReleased(MouseEvent ev) 
	 {
        if (ev.isPopupTrigger()) 
	   {
          menu.show(ev.getComponent(), ev.getX(), ev.getY());
        }
      }

      public void mouseClicked(MouseEvent ev) 
	 {		 
      }
    }
  }
static ActionListener listener2=new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{			
			try
			{
				String buttonclicked=((JButton) e.getSource()).getText();	
				System.out.println("\n\n\n"+buttonclicked+"\n\n\n");			
				if(buttonclicked.equals(".."))
				{
					
					if(!dirname.equals(base))
					dirname=dirname.substring(0,dirname.lastIndexOf('/'));		
					listfromserver=ListFiles.ListFilesandDirectory(dirname);
					Filllist2();						
					return ;
				}	
			
			int key=Integer.parseInt(((JButton) e.getSource()).getName());
						if((listfromserver.get(key)).isdirectory)
						{
							listfromserver=ListFiles.ListFilesandDirectory(button3[key].getText());
							dirname=button3[key].getText();
							Filllist2();
							return ;				
						}
						
				
			}			
			catch(Exception ex){}
		}


	};


	static void Filllist2()
	{
		cloud.removeAll();
		cloud.revalidate();
		cloud.repaint();
		numfilesserver=listfromserver.size()-1;
		button3 = new JButton[numfilesserver];
		folderchooser1=new JButton();
		folderchooser1.setText("..");
		folderchooser1.setPreferredSize(new Dimension(200,30));
		folderchooser1.addActionListener(listener2);
		folderchooser1.setHorizontalAlignment(SwingConstants.CENTER);
		cloud.add(l2);		
		cloud.add(folderchooser1);		
		for (int j=0; j<numfilesserver; j++)
		{
			button3[j]=new RTButton((listfromserver.get(j)).isdirectory,false);
			button3[j].setName(new Integer(j).toString());
			button3[j].setText(((listfromserver.get(j)).name).substring(((listfromserver.get(numfilesserver)).name).length()));
			button3[j].setPreferredSize(new Dimension(200, 30));						
			button3[j].addActionListener(listener2);
			button3[j].setBorder(emptyBorder);
			cloud.add(button3[j]);
			button3[j].setOpaque(false);
			button3[j].setContentAreaFilled(false);
			button3[j].setBorder(BorderFactory.createCompoundBorder(
    			BorderFactory.createLineBorder(Color.CYAN, 5), 
			BorderFactory.createEmptyBorder(5, 5, 10, 10)));
			button3[j].setBorderPainted(false);
			button3[j].setHorizontalAlignment(SwingConstants.CENTER);
		}		
		System.out.println("Hi");
		jf.revalidate();
	}



	static ActionListener listener = new ActionListener() 
	{
	        @Override
        	public void actionPerformed(ActionEvent e) 
		{
			if(currentmenu==0)
			{
		    		if (e.getSource() instanceof JButton) {
					JButton buttonclicked=(JButton) e.getSource();
					if((buttonclicked.getText()).equals("Choose Folder"))
					{
						chooser = new JFileChooser(); 
	  					chooser.setCurrentDirectory(new File(choosertitle));
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
						int key=Integer.parseInt(buttonclicked.getName());
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
				catch(Exception ex)
				{
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
		right_side.add(l3);
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
		file_name.add(l1);	
		file_name.add(folderchooser);		
		for (int j=0; j<numfiles; j++)
		{
			button1[j]=new RTButton(listOfFiles[j].isDirectory(),true);
			button1[j].setName(new Integer(j).toString());
			button1[j].setText(button2[j].getText());
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
		right_side.add(l3);
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
		file_name.add(l2);
		file_name.add(folderchooser);		
		for (int j=0; j<numfiles; j++)
		{
			button1[j]=new JButton();
			button1[j].setName(new Integer(j).toString());
			button1[j].setText(button2[j].getText());
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
		username=args[2];	
		l1.setBorder(BorderFactory.createCompoundBorder(
    			BorderFactory.createLineBorder(Color.BLACK, 5), 
			BorderFactory.createEmptyBorder(5, 5, 10, 10)));
		l2.setBorder(BorderFactory.createCompoundBorder(
    			BorderFactory.createLineBorder(Color.BLACK, 5), 
			BorderFactory.createEmptyBorder(5, 5, 10, 10)));
		l3.setBorder(BorderFactory.createCompoundBorder(
    			BorderFactory.createLineBorder(Color.BLACK, 5), 
			BorderFactory.createEmptyBorder(5, 5, 10, 10)));
		JPanel upload=new JPanel(); 		
		up.setPreferredSize(new Dimension(150, 50));
		chooser = new JFileChooser(); 
		chooser.setCurrentDirectory(new java.io.File("."));
		//chooser.setDialogTitle(choosertitle);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		tf.setPreferredSize(new Dimension(150, 25));
		jb.setPreferredSize(new Dimension(150, 25));
		JPanel add_folder=new JPanel();
		add_folder.add(tf);
		add_folder.add(jb);
		JScrollPane scrollPane = new JScrollPane(file_name);
		JScrollPane scrollPane2 = new JScrollPane(right_side);
		//scrollPane.setPreferredSize(new Dimension(600, 600));	
		JScrollPane scrollPane3 = new JScrollPane(cloud);			
                splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                           scrollPane,scrollPane2);
		splitPane3=new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                           scrollPane3,add_folder);
		splitPane3.setDividerLocation(300);
                splitPane2=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                           splitPane,splitPane3);
                splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(300);
                splitPane2.setOneTouchExpandable(true);
		splitPane2.setDividerLocation(500);
		jf.add(splitPane2);
		upload.add(up);	



		class MenuActionListener implements ActionListener 
		{
			public void actionPerformed(ActionEvent e) 
			{
				try
				{
			     if(e.getActionCommand().equals("Logout"))
				{
					login log=new login();
					String args[]=new String[1];
					log.main(args);
					jf.dispose();
					prompt pm=new prompt();
					args[0]="Successfully Logged out";
					pm.main(args);
				}
				else if(e.getActionCommand().equals("Exit"))
				{
					jf.dispose();
					prompt pm=new prompt();
					String args[]=new String[1];
					args[0]="Successfully Logged out";
					pm.main(args);
				}
				else if(e.getActionCommand().equals("Upload"))
				{
					jf.getContentPane().remove(splitPane2);
					jf.add(splitPane);
					jf.getContentPane().invalidate();
					jf.getContentPane().validate();
					jf.getContentPane().remove(splitPane);
					splitPane2=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                           splitPane,splitPane3);
					splitPane2.setOneTouchExpandable(true);
		splitPane2.setDividerLocation(500);

					jf.add(splitPane2);
					jf.getContentPane().invalidate();
					jf.getContentPane().validate();
					currentmenu=0;
					up.setText("Upload");
					l1.setText("Files in System");
					Filllist(choosertitle);
					listfromserver=ListFiles.ListFilesandDirectory(base);
					Filllist2();
				}
				else if(e.getActionCommand().equals("Download"))
				{
					jf.getContentPane().remove(splitPane2);
					jf.add(splitPane);
					jf.getContentPane().invalidate();
					jf.getContentPane().validate();
					currentmenu=1;
					listfromserver=ListFiles.ListFilesandDirectory(base);
					up.setText("Download");
					Filllistfromserver();
					l1.setText("Files in Cloud");
				}
				else 
				{
					jf.getContentPane().remove(splitPane2);
					jf.add(splitPane);
					jf.getContentPane().invalidate();
					jf.getContentPane().validate();
					currentmenu=2;
					listfromserver=ListFiles.ListFilesandDirectory(base);
					up.setText("Delete");
					Filllistfromserver();
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
		JMenuItem logout = new JMenuItem("Logout",KeyEvent.VK_T);		
		JMenuItem exit = new JMenuItem("Exit",KeyEvent.VK_T);
		
		uploadmenu.addActionListener(new MenuActionListener());
		download.addActionListener(new MenuActionListener());
		delete.addActionListener(new MenuActionListener());
		logout.addActionListener(new MenuActionListener());
		exit.addActionListener(new MenuActionListener());	
				
		menu.add(uploadmenu);
		menu.add(download);				
		menu.add(delete);
		menu.add(logout);
		menu.add(exit);
		jmb.add(menu);			
		file_name.add(l1);
		right_side.add(l2);
		file_name.setLayout(new BoxLayout(file_name, BoxLayout.Y_AXIS));
		right_side.setLayout(new BoxLayout(right_side, BoxLayout.Y_AXIS));
		cloud.setLayout(new BoxLayout(cloud, BoxLayout.Y_AXIS));
		choosertitle=new java.io.File(".").toString();
		Filllist(choosertitle);
	      try{
              dirname=base;
               listfromserver=ListFiles.ListFilesandDirectory(dirname);
               }
               catch(Exception ex2){ ex2.printStackTrace();}   
              Filllist2();
		for (int i = 0; i < numfiles; i++) 
		{
      			if (listOfFiles[i].isFile()) 
        			System.out.println("File " + listOfFiles[i].getName());
    			else if (listOfFiles[i].isDirectory()) 
        			System.out.println("Directory " + listOfFiles[i].getName());
    		}
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
							String upfile=listOfFiles[i].toString();
							choosedfiles.add(upfile);
						}
					}
					String log="Uploading "+choosedfiles.size()+" files to "+dirname;
					System.out.println(Client.LogActivity(username,log));
					Upload.UploadFiles(dirname,choosedfiles);
					Filllist(choosertitle);
              			listfromserver=ListFiles.ListFilesandDirectory(dirname);
					Filllist2();					
				}
				else if(currentmenu==1)
				{
					for(int i=0;i<numfiles;i++)
					{
						if(button[i]==0)
						{				
							String fname=button1[i].getText();
							choosedfiles.add(fname.substring(fname.lastIndexOf('/')+1));						}
					}
					System.out.println("india"+dirname);
					String log="Downloading "+choosedfiles.size()+" files from "+dirname;
					System.out.println(Client.LogActivity(username,log));
					DownloadFiles.DownloadFiles(dirname,choosedfiles,((System.getProperty("user.home")).replace("\\","/"))+"/Downloads/");
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
					String log="Deleting "+choosedfiles.size()+" files from "+dirname;
					System.out.println(Client.LogActivity(username,log));					
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
		jb.addActionListener( new ActionListener()
		{
    		@Override
    		public void actionPerformed(ActionEvent e)
    		{
			try
			{
				prompt pm=new prompt();
				String arg[]=new String[1];
				if(!signup.validname(tf.getText()))
				{					
					arg[0]="Invalid foldername. Folder name should only contain alphabets and numbers";
					pm.main(arg);
					return ;
				}
				CreateFolder.CreateFolder(dirname+"/"+tf.getText());
				arg[0]="Successfully folder Created";
				pm.main(arg);
				listfromserver=ListFiles.ListFilesandDirectory(dirname);
				Filllist2();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
        	}
        	});
		jf.setJMenuBar(jmb);
		jf.add(upload,BorderLayout.PAGE_END);
		jf.setSize(800,500);
		jf.setVisible(true);
		jf.setLocationRelativeTo(null);
	}
}

