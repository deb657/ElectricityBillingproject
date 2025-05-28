package Electricity;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import net.proteanit.sql.DbUtils;

public class DepositDetails extends JFrame implements ActionListener{
 
    JTable t1;
    JButton b1, b2;
    JLabel l1, l2;
    Choice c1, c2;
    String x[] = {"Meter Number","Month","Units","Total Bill","Status"};
    String y[][] = new String[40][8];
    int i=0, j=0;
    DepositDetails(){
        super("Deposit Details");
        setSize(700,750);
        setLocation(600,150);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);
        
        l1 = new JLabel("Sort by Meter Number");
        l1.setBounds(20, 20, 150, 20);
        add(l1);
        
        c1 = new Choice();
        
        l2 = new JLabel("Sort By Month");
        l2.setBounds(400, 20, 100, 20);
        add(l2);
        
        c2 = new Choice();
        
        // t1 = new JTable(y,x); // Not ideal with DbUtils
        t1 = new JTable(); // Initialize JTable, DbUtils will provide the model
        
        // Load initial bill data
        loadBillData("select meter_no, month, units, total_bill, status from bill");
        
        // Load meter numbers into choice c1
        try (Conn conn = new Conn();
             Statement stmt = conn.c.createStatement();
             ResultSet rs = stmt.executeQuery("select distinct meter from customer")) { // Assuming 'meter' is the column name
            while(rs.next()){
                c1.add(rs.getString("meter"));
            }
        } catch(SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading meter numbers: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        
        c1.setBounds(180,20, 150, 20);
        add(c1);
        
        
        c2.setBounds(520, 20, 150, 20);
        c2.add("January");
        c2.add("February");
        c2.add("March");
        c2.add("April");
        c2.add("May");
        c2.add("June");
        c2.add("July");
        c2.add("August");
        c2.add("September");
        c2.add("October");
        c2.add("November");
        c2.add("December");
        add(c2);
        
        
        b1 = new JButton("Search");
        b1.setBounds(20, 70, 80, 20);
        b1.addActionListener(this);
        add(b1);
        
        b2 = new JButton("Print");
        b2.setBounds(120, 70, 80, 20);
        b2.addActionListener(this);
        add(b2);
        
        JScrollPane sp = new JScrollPane(t1);
        sp.setBounds(0, 100, 700, 650);
        add(sp);
        
    }
    
    private void loadBillData(String query, String... params) {
        try (Conn conn = new Conn();
             PreparedStatement pstmt = conn.c.prepareStatement(query)) {
            for (int k = 0; k < params.length; k++) {
                pstmt.setString(k + 1, params[k]);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                t1.setModel(DbUtils.resultSetToTableModel(rs));
            } // rs is auto-closed
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading bill data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void actionPerformed(ActionEvent ae){
        if(ae.getSource() == b1){ // Search button
            String meterNumber = c1.getSelectedItem();
            String month = c2.getSelectedItem();
            
            if (meterNumber == null || meterNumber.isEmpty() || month == null || month.isEmpty()) {
                 // Load all data if no specific filter is selected, or show a message
                loadBillData("select meter_no, month, units, total_bill, status from bill"); // Or specific columns as needed
                // JOptionPane.showMessageDialog(this, "Please select both meter number and month to search.");
                return;
            }
            
            String query = "select meter_no, month, units, total_bill, status from bill where meter_no = ? AND month = ?";
            loadBillData(query, meterNumber, month);

        }else if(ae.getSource() == b2){ // Print button
            try{
                t1.print();
            } catch (java.awt.print.PrinterException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error printing details: " + e.getMessage(), "Printing Error", JOptionPane.ERROR_MESSAGE);
            } catch(Exception e){
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "An unexpected error occurred during printing: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public static void main(String[] args){
        new DepositDetails().setVisible(true);
    }
    
}
