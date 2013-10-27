package com.inca.np.demo.dnd;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-27
 * Time: 11:31:16
 * To change this template use File | Settings | File Templates.
 */
public class DndTest1 extends JFrame{
    private MyButton button1;
    private MyButton button2;

    public DndTest1() throws HeadlessException {
        super("Test dnd");
        Container cp = this.getContentPane();
        BoxLayout boxLayout = new BoxLayout(cp,BoxLayout.X_AXIS);
        cp.setLayout(boxLayout);

        button1 = new MyButton("button1");
        cp.add(button1);

        button2 = new MyButton("button2");
        cp.add(button2);
    }

    class MyButton extends JButton implements DropTargetListener, DragSourceListener, DragGestureListener,
            Transferable{
        DropTarget dropTarget = new DropTarget(this, this);
        DragSource dragSource = DragSource.getDefaultDragSource();
        public MyButton(String text) {
            super(text);
            dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
        }

        public void dragEnter(DropTargetDragEvent dtde) {
            System.out.println("dragEnter");
            dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
        }

        public void dragOver(DropTargetDragEvent dtde) {
            //System.out.println("dragOver");
        }

        public void dropActionChanged(DropTargetDragEvent dtde) {
            System.out.println("dropActionChanged");
        }

        public void dragExit(DropTargetEvent dte) {
            //System.out.println("dragExit");
        }

        public synchronized void drop(DropTargetDropEvent dtde) {
            System.out.println("drop");
            Transferable transferable = dtde.getTransferable();
        }

        public void dragEnter(DragSourceDragEvent dsde) {
            //System.out.println("DragSourceDragEvent dragEnter");
        }

        public void dragOver(DragSourceDragEvent dsde) {
            //System.out.println("DragSourceDragEvent dragOver");
        }

        public void dropActionChanged(DragSourceDragEvent dsde) {
            System.out.println("DragSourceDragEvent dropActionChanged");
        }

        public void dragExit(DragSourceEvent dse) {
            //System.out.println("DragSourceDragEvent dragExit");
        }

        public void dragDropEnd(DragSourceDropEvent dsde) {
            System.out.println("DragSourceDragEvent dragDropEnd");
        }

        public void dragGestureRecognized(DragGestureEvent dge) {
            System.out.println("DragSourceDragEvent dragGestureRecognized");
            dge.startDrag(DragSource.DefaultCopyDrop,this,this);

        }

        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[0];  //To change body of implemented methods use File | Settings | File Templates.
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public static void main(String[] argv){
        DndTest1 frm=new DndTest1();
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.pack();
        frm.setVisible(true);
    }
}
