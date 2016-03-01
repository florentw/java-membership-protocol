/*
	jmmp - a java implementation of a multicast membership protocol

	Copyright (C) 2008 Philippe Esling, Julien Clement and Florent Weber

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package hypership;

import hyperbolize.graphe.GrapheHyperbolique;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.*;

import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.Dockable;
import com.vlsolutions.swing.docking.DockingConstants;

/**
* 
* This class is the representation of the file system widget.
* Used to find executable files on the hard drive for remote execution.
* 
* @version 						1.5
* @see 							GrapheHyperbolique
* 
*/
public class 					GUIFileExplorer extends JPanel implements Dockable
{
	private static final long 	serialVersionUID = -6602084100247395117L;
    private DockKey 			key;
	private JTree				tree;
	private JScrollPane 		jsp;
	
	/**
	 * 
	 * Main constructor for the GUIFileExplorer.
	 * Allocates all memory resources.
	 * 
	 */
    public 						GUIFileExplorer()
    {
    	initTreeSys(".");
    	key = new DockKey("File Explorer");
        setLayout(new BorderLayout());
        jsp = new JScrollPane(tree);
        jsp.setPreferredSize(new Dimension(200, 200));
        JButton changeRoot = new JButton("Change root ...");
		changeRoot.setToolTipText("Changes the root directory.");
		changeRoot.addActionListener(new ActionListener(){
	        public void actionPerformed(ActionEvent e)
	        {
	        	JFileChooser		fileChoose = new JFileChooser();
	        	
	        	fileChoose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    		int returnVal = fileChoose.showOpenDialog(fileChoose);
	    		if (returnVal == JFileChooser.APPROVE_OPTION)
	    		{
	    			File file = fileChoose.getSelectedFile();
	    			initTreeSys(file.getAbsolutePath());
	    			remove(jsp);
	    	        jsp = new JScrollPane(tree);
	    	        add(jsp, BorderLayout.CENTER);
	    	        revalidate();
	    			//repaint();
	    		}
	        }});
		add(changeRoot, BorderLayout.NORTH);
        add(jsp, BorderLayout.CENTER);
        key.setName(" Explorer");
        key.setTooltip("Explore your filesystem.");
        key.setIcon(new ImageIcon("icons/file_explorer.gif"));
        key.setAutoHideBorder(DockingConstants.HIDE_LEFT);
        key.setMaximizeEnabled(false);
        key.setCloseEnabled(false);
        
    }
    
    /**
     * 
     * Initializes the tree representation of the file system from a specified directory
     * 
     * @param rootPath : the starting point for exploration
     * 
     */
    public void					initTreeSys(String rootPath)
    {
    	tree = new JTree(createFileSys(rootPath));
    	TransferHandler handler = new GUITransferHandler();
    	tree.setTransferHandler(handler);
    	tree.setDragEnabled(true);
    	tree.addTreeExpansionListener(new TreeExpansionListener()
        {
            public void treeCollapsed( TreeExpansionEvent e )
            {
            }
            public void treeExpanded( TreeExpansionEvent e )
            {
                TreePath path = e.getPath();
                FileNode node = ( FileNode )
                    path.getLastPathComponent();
 
                if ( !node.isExplored() )
                {
                    DefaultTreeModel model = ( DefaultTreeModel )tree.getModel();
 
                    node.explore();
                    model.nodeStructureChanged( node );
                }
            }
        });
    }

    /**
     * 
     * Creates the file system representation from the current working directory
     * 
     * @return A tree model of the file system. 
     * 
     */
    public DefaultTreeModel		createFileSys()
    {
        File 					root = new File( "." );
        FileNode 				rootNode = new FileNode( root );
 
        rootNode.explore();
        return new DefaultTreeModel( rootNode );
    }

    /**
     * 
     * Creates the file system representation from a specified directory
     * 
     * @param rootPath : starting point for exploration
     * @return A tree model of the file system. 
     * 
     */    
    public DefaultTreeModel		createFileSys(String rootPath)
    {
        File 					root = new File(rootPath);
        FileNode 				rootNode = new FileNode( root );
 
        rootNode.explore();
        return new DefaultTreeModel( rootNode );
    }
    
    /**
     * 
     * Gets the currently selected file
     * 
     * @return The current selection or a specific error message
     * 
     */
    public String				getSelectedFile()
    {
    	if (tree.isSelectionEmpty())
    		return "Aucun fichier s�lectionn�";
    	Object[] path = tree.getSelectionPath().getPath();
    	String send = ((FileNode)path[path.length - 1]).getFile().getAbsolutePath();
		    	
    	return send;
    }
    
	public Component 			getComponent()
	{
		return this;
	}

	public DockKey 				getDockKey()
	{
		return this.key;
	}
}

/**
 * 
 * This internal class represents one node in the tree structure of the file system. 
 *
 */
class 							FileNode extends DefaultMutableTreeNode
{
	private static final long 	serialVersionUID = -8586342444846859623L;
	private boolean 			explored = false;
 
	/**
	 * 
	 * Main constructor for the FileNode class
	 * 
	 * @param file : the file represented by this node
	 * 
	 */
    public 						FileNode(File file)
    {
        setUserObject(file);
    }
 
    /**
     * 
     * Main function for file exploration.
     * The children of the node are parsed and only directories and
     * executable files are added in the file system representation.
     * 
     */
    public void 				explore()
    {
        if (!isDirectory())
            return;
        if (!isExplored())
        {
            File 				file = getFile();
            File[] 				children = file.listFiles();
            String				pathname, os;
            boolean				executable;
 
            os = System.getProperty("os.name");
            for (int i = 0; i < children.length; ++i)
            {
            	pathname = children[i].getAbsolutePath();
            	if (children[i].isDirectory())
            	{
            		add(new FileNode(children[i]));
            		continue;
            	}
            	
            	if (os.equals("Windows") || os.equals("Windows XP"))
            	{
            		if (pathname.lastIndexOf(".") == -1)
            			executable = false;
            		else
            			executable = pathname.substring(pathname.lastIndexOf(".")).equals(".exe");
            	}
            	else
            		executable = children[i].canExecute();
            	if (children[i].isFile() && executable)
            		add(new FileNode(children[i]));
            }
            explored = true;
        }
    }
    
    public boolean 				getAllowsChildren()
    {
        return isDirectory();
    }
 
    public boolean 				isLeaf()
    {
        return !isDirectory();
    }
 
    public File 				getFile()
    {
    	return (File)getUserObject();
    }
 
    public boolean 				isExplored()
    {
        return explored;
    }
 
    public boolean 				isDirectory()
    {
        return getFile().isDirectory();
    }
    
    public boolean				isExecutable()
    {
    	return getFile().canExecute();
    }
 
    public String 				toString()
    {
        File file = ( File )getUserObject();
        String filename = file.toString();
        int index = filename.lastIndexOf( File.separator );
 
        return ( index != -1 && index != filename.length() - 1 ) ?
            filename.substring( index + 1 ) : filename;
    }
}
