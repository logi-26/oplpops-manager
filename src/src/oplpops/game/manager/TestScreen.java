/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oplpops.game.manager;

import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxList;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 *
 * @author louis
 */
public class TestScreen extends javax.swing.JFrame {

    /**
     * Creates new form TestScreen
     */
    public TestScreen(java.awt.Frame parent) {
        initComponents();
        
        JFrame frame = new JFrame();    
        
        String column[]={"ID","NAME","SALARY"};  
        
        String data[][]={ {"101","Amit","670000"},    
                          {"102","Jai","780000"},    
                          {"101","Sachin","700000"}};    
        
          
        JTable table = new JTable(data,column);    
        table.setBounds(30,40,200,300);          
        JScrollPane sp=new JScrollPane(table);    
        frame.add(sp);          
        frame.setSize(300,400);    
        frame.setVisible(true);    
        
        
        /*
        JTable table = new JTable();
         
        
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.add(table);
        
        JFrame frame = new JFrame("Foo");
        frame.add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(parent);
        frame.setVisible(true);
        */
        
        
        
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}