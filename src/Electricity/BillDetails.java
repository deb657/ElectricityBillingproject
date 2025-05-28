package Electricity;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import net.proteanit.sql.DbUtils;

public class BillDetails extends JFrame{
 
    JTable t1;
    String x[] = {"Meter Number","Month","Units","Total Bill","Status"};
    String y[][] = new String[40][8];
    int i=0, j=0;
    BillDetails(String meter){
        super("Bill Details");
        setSize(700,650);
        setLocation(600,150);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);
        
        // t1 = new JTable(y,x); // This initialization is not ideal if using DbUtils
        t1 = new JTable(); // Initialize JTable, DbUtils will provide the model
        
        String query = "select meter_no, month, units, total_bill, status from bill where meter_no = ?";
        
        try (Conn conn = new Conn(); PreparedStatement pstmt = conn.c.prepareStatement(query)) {
            pstmt.setString(1, meter);
            try (ResultSet rs = pstmt.executeQuery()) {
                t1.setModel(DbUtils.resultSetToTableModel(rs));
            } // rs is auto-closed here
        } catch (SQLException e) {
            e.printStackTrace();
            // Consider showing an error message to the user in the UI
            JOptionPane.showMessageDialog(this, "Error fetching bill details: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) { // Catch other potential exceptions
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        JScrollPane sp = new JScrollPane(t1);
        sp.setBounds(0, 0, 700, 650);
        add(sp);
        
    }
    
    public static void main(String[] args){
        new BillDetails("").setVisible(true);
    }
    
}
