package com.inca.np.gui.control;

import javax.swing.*;
import javax.swing.event.ListDataListener;

import org.apache.log4j.Category;

import com.inca.np.communicate.RecordTrunk;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-19
 * Time: 9:56:21
 * To change this template use File | Settings | File Templates.
 */
public class CComboBoxModel implements MutableComboBoxModel {
    //DefaultComboBoxModel cbmodel=null;
    DBTableModel tablemodel=null;
    String keycolumn;
    String valuecolumn;


    /**
     * ����һ��ComboBoxModel.
     * @param tablemodel
     * @param keycolumn ���ݸ���
     * @param displaycolumn ��ʾ����
     */
    public CComboBoxModel(DBTableModel tablemodel,String keycolumn,String displaycolumn) {
        this.tablemodel=tablemodel;
        this.keycolumn=keycolumn;
        this.valuecolumn=displaycolumn;
        
        //20070928 ��0��Ӧ���Ǹ�""��ʾnull,�����л������
        //20071011 ɾ���������
        //20071012 �޸�
        if(tablemodel.getRowCount()>0){
        	String v=tablemodel.getItemValue(0,keycolumn);
        	if(v.length()>0){
        		addEmptyRecord();
        	}
        }else{
    		addEmptyRecord();
        }
    }
    
    /**
     * �����0��,����ѡ�յ�ѡ�� 
     */
    void addEmptyRecord(){
		RecordTrunk emptyrec=new RecordTrunk(tablemodel.getColumnCount());
		tablemodel.getDataVector().insertElementAt(emptyrec, 0);
		tablemodel.setItemValue(0,keycolumn,"");
		tablemodel.setItemValue(0,valuecolumn,"");
    }
        
    

    public int getKeyIndex(String value){
        for(int i=0;i<tablemodel.getRowCount();i++){
            if(tablemodel.getItemValue(i,keycolumn).equals(value)){
                return i;
            }
        }
        return -1;
    }


    Category logger=Category.getInstance(CComboBoxModel.class);
    public void addElement(Object obj) {
        logger.error("CComboBoxModel not support addElement(Object obj)");
    }

    public void removeElement(Object obj) {
        logger.error("CComboBoxModel not support removeElement(Object obj)");
    }

    public void insertElementAt(Object obj, int index) {
    	while(tablemodel.getRowCount()<=index)tablemodel.appendRow();
    	tablemodel.setItemValue(index, keycolumn,(String)obj);
    	tablemodel.setItemValue(index, valuecolumn,(String)obj);
        //logger.error("CComboBoxModel not support insertElementAt(Object obj, int index)");
    }

    public void removeElementAt(int index) {
        logger.error("CComboBoxModel not support removeElementAt(int index) ");
    }

    Object selecteditem=null;
    public void setSelectedItem(Object anItem) {
    	selecteditem=anItem;
    }

    public Object getSelectedItem() {
        return selecteditem;
    }

    public int getSize() {
        return tablemodel.getRowCount();
    }

    public Object getElementAt(int index) {
    	String v=tablemodel.getItemValue(index,valuecolumn);
    	//System.out.println("ccomboboxmodel getelementat ,index="+index+",v="+v);
        return  v;
    }

    public void addListDataListener(ListDataListener l) {
        
    }

    public void removeListDataListener(ListDataListener l) {
        
    }

    public String getKeyvalue(int index) {
        return tablemodel.getItemValue(index,keycolumn);
    }

    public String getValue(int index) {
        return tablemodel.getItemValue(index,valuecolumn);
    }

    public String getKey(String value){
        for(int i=0;i<tablemodel.getRowCount();i++){
            if(tablemodel.getItemValue(i,valuecolumn).equals(value)){
                return tablemodel.getItemValue(i,keycolumn);
            }
        }
        return "";
    }
    
    public DBTableModel getDbmodel(){
    	return tablemodel;
    }
}
