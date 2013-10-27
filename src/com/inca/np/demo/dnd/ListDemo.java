package com.inca.np.demo.dnd;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import javax.swing.*;
import javax.swing.event.*;

public class ListDemo extends JFrame
        implements ListSelectionListener {
    private DroppableList list;
    private JTextField fileName;

    public ListDemo() {
        super("ListDemo");

//Create the list and put it in a scroll pane
        list = new DroppableList();
        DefaultListModel listModel = (DefaultListModel) list.getModel();
        list.setCellRenderer(new CustomCellRenderer());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(this);
        JScrollPane listScrollPane = new JScrollPane(list);

        String dirName = "c:/a";
        String filelist[] = new File(dirName).list();
        for (int i = 0; i < filelist.length; i++) {
            String thisFileSt =  filelist[i];
            File thisFile = new File(thisFileSt);
            if (thisFile.isDirectory())
                continue;
            try {
                listModel.addElement(makeNode(thisFile.getName(),
                        thisFile.toURL().toString(),
                        thisFile.getAbsolutePath()));
            } catch (java.net.MalformedURLException e) {
            }
        }

        fileName = new JTextField("xxx",50);
        int selindex = list.getSelectedIndex();
        if(selindex>=0){
            String name = listModel.getElementAt(selindex).toString();
            fileName.setText(name);
        }

        //file://Create a panel that uses FlowLayout (the default).
        JPanel buttonPane = new JPanel();
        buttonPane.add(fileName);

        Container contentPane = getContentPane();
        contentPane.add(listScrollPane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.NORTH);
    }

    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {
            fileName.setText("");
            if (list.getSelectedIndex() != -1) {
                String name = list.getSelectedValue().toString();
                fileName.setText(name);
            }
        }
    }

    private static Hashtable makeNode(String name,
                                      String url, String strPath) {
        Hashtable hashtable = new Hashtable();
        hashtable.put("name", name);
        hashtable.put("url", url);
        hashtable.put("path", strPath);
        return hashtable;
    }

    public class DroppableList extends JList
            implements DropTargetListener, DragSourceListener, DragGestureListener {
        DropTarget dropTarget = new DropTarget(this, this);
        DragSource dragSource = DragSource.getDefaultDragSource();

        public DroppableList() {
            dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
            setModel(new DefaultListModel());
        }

        public void dragDropEnd(DragSourceDropEvent DragSourceDropEvent) {
        }

        public void dragEnter(DragSourceDragEvent DragSourceDragEvent) {
        }

        public void dragExit(DragSourceEvent DragSourceEvent) {
        }

        public void dragOver(DragSourceDragEvent DragSourceDragEvent) {
        }

        public void dropActionChanged(DragSourceDragEvent DragSourceDragEvent) {
        }

        public void dragEnter(DropTargetDragEvent dropTargetDragEvent) {
            dropTargetDragEvent.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
        }

        public void dragExit(DropTargetEvent dropTargetEvent) {
        }

        public void dragOver(DropTargetDragEvent dropTargetDragEvent) {
        }

        public void dropActionChanged(DropTargetDragEvent dropTargetDragEvent) {
        }

        public synchronized void drop(DropTargetDropEvent dropTargetDropEvent) {
            try {
                Transferable tr = dropTargetDropEvent.getTransferable();
                if (tr.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    dropTargetDropEvent.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    java.util.List fileList = (java.util.List)
                            tr.getTransferData(DataFlavor.javaFileListFlavor);
                    Iterator iterator = fileList.iterator();
                    while (iterator.hasNext()) {
                        File file = (File) iterator.next();
                        Hashtable hashtable = new Hashtable();
                        hashtable.put("name", file.getName());
                        hashtable.put("url", file.toURL().toString());
                        hashtable.put("path", file.getAbsolutePath());
                        ((DefaultListModel) getModel()).addElement(hashtable);
                    }
                    dropTargetDropEvent.getDropTargetContext().dropComplete(true);
                } else {
                    System.err.println("Rejected");
                    dropTargetDropEvent.rejectDrop();
                }
            } catch (IOException io) {
                io.printStackTrace();
                dropTargetDropEvent.rejectDrop();
            } catch (UnsupportedFlavorException ufe) {
                ufe.printStackTrace();
                dropTargetDropEvent.rejectDrop();
            }
        }

        public void dragGestureRecognized(DragGestureEvent dragGestureEvent) {
            if (getSelectedIndex() == -1)
                return;
            Object obj = getSelectedValue();
            if (obj == null) {
// Nothing selected, nothing to drag
                System.out.println("Nothing selected - beep");
                getToolkit().beep();
            } else {
                Hashtable table = (Hashtable) obj;
                FileSelection transferable =
                        new FileSelection(new File((String) table.get("path")));
                dragGestureEvent.startDrag(DragSource.DefaultCopyDrop,
                        transferable,
                        this);
            }
        }
    }

    public class CustomCellRenderer implements ListCellRenderer {
        DefaultListCellRenderer listCellRenderer =
                new DefaultListCellRenderer();

        public Component getListCellRendererComponent(JList list, Object value, int index,
                                                      boolean selected, boolean hasFocus) {
            listCellRenderer.getListCellRendererComponent(list, value, index, selected, hasFocus);
            listCellRenderer.setText(getValueString(value));
            return listCellRenderer;
        }

        private String getValueString(Object value) {
            String returnString = "null";
            if (value != null) {
                if (value instanceof Hashtable) {
                    Hashtable h = (Hashtable) value;
                    String name = (String) h.get("name");
                    String url = (String) h.get("url");
                    returnString = name  +   " ==> "  +   url;
                } else {
                    returnString = "X: "  +    value.toString();
                }
            }
            return returnString;
        }
    }

    public class FileSelection extends Vector implements Transferable {
        final static int FILE = 0;
        final static int STRING = 1;
        final static int PLAIN = 2;
        DataFlavor flavors[] = {DataFlavor.javaFileListFlavor,
                                DataFlavor.stringFlavor,
                                DataFlavor.plainTextFlavor};

        public FileSelection(File file) {
            addElement(file);
        }
/* Returns the array of flavors in which it can provide the data. */
        public synchronized DataFlavor[] getTransferDataFlavors() {
            return flavors;
        }
/* Returns whether the requested flavor is supported by this object. */
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            boolean b = false;
            b |= flavor.equals(flavors[FILE]);
            b |= flavor.equals(flavors[STRING]);
            b |= flavor.equals(flavors[PLAIN]);
            return (b);
        }

        /**
         * If the data was requested in the "java.lang.String" flavor,
         * return the String representing the selection.
         */
        public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (flavor.equals(flavors[FILE])) {
                return this;
            } else if (flavor.equals(flavors[PLAIN])) {
                return new StringReader(((File) elementAt(0)).getAbsolutePath());
            } else if (flavor.equals(flavors[STRING])) {
                return ((File) elementAt(0)).getAbsolutePath();
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }
    }

    public static void main(String s[]) {
        JFrame frame = new ListDemo();
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        frame.pack();
        frame.setVisible(true);
    }
}