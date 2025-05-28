package Electricity;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class CustomerDetails extends JFrame implements ActionListener{
 
import net.proteanit.sql.DbUtils; // Import DbUtils

public class CustomerDetails extends JFrame implements ActionListener{
 
    JTable t1;
    JButton b1;
    // String x[] = {"Customer Name","Meter Number","Address","City","State","Email","Phone"}; // Not needed with DbUtils
    // String y[][] = new String[40][8]; // Not needed with DbUtils
    // int i=0, j=0; // Not needed with DbUtils

    CustomerDetails(){
        super("Customer Details");
        setSize(1200,650);
        setLocation(400,150);
        setLayout(new BorderLayout()); // Set a layout manager for the JFrame

        t1 = new JTable(); // Initialize JTable

        String query = "select name as `Customer Name`, meter as `Meter Number`, address as `Address`, city as `City`, state as `State`, email as `Email`, phone as `Phone` from customer";
        
        try (Conn conn = new Conn();
             Statement stmt = conn.c.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            t1.setModel(DbUtils.resultSetToTableModel(rs));
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching customer details: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) { // Catch other potential exceptions
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        b1 = new JButton("Print");
        b1.addActionListener(this);
        
        JScrollPane sp = new JScrollPane(t1);
        
        add(sp, BorderLayout.CENTER); // Add scroll pane to center
        add(b1, BorderLayout.SOUTH); // Add button to south
    }

    public void actionPerformed(ActionEvent ae){
        if (ae.getSource() == b1) {
            try {
                t1.print();
            } catch (java.awt.print.PrinterException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error printing customer details: " + e.getMessage(), "Printing Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "An unexpected error occurred during printing: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public static void main(String[] args){
        new CustomerDetails().setVisible(true);
    }
    
}
