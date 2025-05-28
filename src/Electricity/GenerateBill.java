package Electricity;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class GenerateBill extends JFrame implements ActionListener{
    JLabel l1, l2;
    JTextArea t1;
    JButton b1;
    Choice c2;
    JPanel p1;
    String meter;
    GenerateBill(String meter){
        this.meter = meter;
        setSize(500,900);
        setLayout(new BorderLayout());
        
        p1 = new JPanel();
        
        l1 = new JLabel("Generate Bill");
        
        l2 = new JLabel(meter);
        c2 = new Choice();
        
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

        
        t1 = new JTextArea(50,15);
        t1.setText("\n\n\t------- Click on the -------\n\t Generate Bill Button to get\n\tthe bill of the Selected Month\n\n");
        JScrollPane jsp = new JScrollPane(t1);
        t1.setFont(new Font("Senserif",Font.ITALIC,18));
        
        b1 = new JButton("Generate Bill");
        
        p1.add(l1);
        p1.add(l2);
        p1.add(c2);
        add(p1,"North");
        
        add(jsp,"Center");
        add(b1,"South");
        
        b1.addActionListener(this);
        
        setLocation(750,100);
    }
    public void actionPerformed(ActionEvent ae){
        String selectedMonth = c2.getSelectedItem();
        t1.setText("\tReliance Power Limited\nELECTRICITY BILL FOR THE MONTH OF " + selectedMonth + " ,2021\n\n\n");

        // Fetch Customer Details
        String customerQuery = "select name, meter, address, state, city, email, phone from customer where meter = ?";
        try (Conn conn = new Conn(); PreparedStatement psCustomer = conn.c.prepareStatement(customerQuery)) {
            psCustomer.setString(1, this.meter);
            try (ResultSet rs = psCustomer.executeQuery()) {
                if (rs.next()) {
                    t1.append("\n    Customer Name: " + rs.getString("name"));
                    t1.append("\n    Meter Number:  " + rs.getString("meter"));
                    t1.append("\n    Address:       " + rs.getString("address"));
                    t1.append("\n    State:         " + rs.getString("state"));
                    t1.append("\n    City:          " + rs.getString("city"));
                    t1.append("\n    Email:         " + rs.getString("email"));
                    t1.append("\n    Phone Number:  " + rs.getString("phone"));
                    t1.append("\n-------------------------------------------------------------");
                    t1.append("\n");
                } else {
                    t1.append("\n    Customer details not found for meter: " + this.meter);
                    t1.append("\n-------------------------------------------------------------");
                    t1.append("\n");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            t1.append("\n    Error fetching customer details: " + e.getMessage());
        }

        // Fetch Meter Info
        String meterInfoQuery = "select meter_location, meter_type, phase_code, bill_type, days from meter_info where meter_number = ?";
        try (Conn conn = new Conn(); PreparedStatement psMeterInfo = conn.c.prepareStatement(meterInfoQuery)) {
            psMeterInfo.setString(1, this.meter);
            try (ResultSet rs = psMeterInfo.executeQuery()) {
                if (rs.next()) {
                    t1.append("\n    Meter Location: " + rs.getString("meter_location"));
                    t1.append("\n    Meter Type:     " + rs.getString("meter_type"));
                    t1.append("\n    Phase Code:     " + rs.getString("phase_code"));
                    t1.append("\n    Bill Type:      " + rs.getString("bill_type"));
                    t1.append("\n    Days:           " + rs.getString("days"));
                    t1.append("\n");
                } else {
                    t1.append("\n    Meter information not found for meter: " + this.meter);
                    t1.append("\n");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            t1.append("\n    Error fetching meter information: " + e.getMessage());
        }

        // Fetch Tax Details
        String taxQuery = "select cost_per_unit, meter_rent, service_charge, service_tax, swacch_bharat_cess, fixed_tax from tax";
        try (Conn conn = new Conn();
             Statement stmtTax = conn.c.createStatement(); // Tax query has no parameters
             ResultSet rs = stmtTax.executeQuery(taxQuery)) {
            if (rs.next()) {
                t1.append("---------------------------------------------------------------");
                t1.append("\n\n");
                t1.append("\n Cost per Unit:        Rs " + rs.getString("cost_per_unit"));
                t1.append("\n Meter Rent:           Rs " + rs.getString("meter_rent"));
                t1.append("\n Service Charge:       Rs " + rs.getString("service_charge"));
                t1.append("\n Service Tax:          Rs " + rs.getString("service_tax"));
                t1.append("\n Swacch Bharat Cess:   Rs " + rs.getString("swacch_bharat_cess"));
                t1.append("\n Fixed Tax:            Rs " + rs.getString("fixed_tax"));
                t1.append("\n");
            } else {
                t1.append("\n    Tax details not found.");
                t1.append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            t1.append("\n    Error fetching tax details: " + e.getMessage());
        }
        
        // Fetch Bill Details for the selected month
        String billQuery = "select month, units, total_bill from bill where meter_no = ? AND month = ?";
        try (Conn conn = new Conn(); PreparedStatement psBill = conn.c.prepareStatement(billQuery)) {
            psBill.setString(1, this.meter);
            psBill.setString(2, selectedMonth);
            try (ResultSet rs = psBill.executeQuery()) {
                if (rs.next()) {
                    t1.append("\n    Current Month :    " + rs.getString("month"));
                    t1.append("\n    Units Consumed:    " + rs.getString("units"));
                    t1.append("\n    Total Charges :    " + rs.getString("total_bill"));
                    t1.append("\n---------------------------------------------------------------");
                    t1.append("\n    TOTAL PAYABLE :    " + rs.getString("total_bill"));
                } else {
                    t1.append("\n    Bill details not found for meter " + this.meter + " for month " + selectedMonth + ".");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            t1.append("\n    Error fetching bill details: " + e.getMessage());
        } catch (Exception e) { // Catch any other unexpected errors
            e.printStackTrace();
            t1.append("\n    An unexpected error occurred: " + e.getMessage());
        }
    }
    
    public static void main(String[] args){
        new GenerateBill("").setVisible(true);
    }
}

