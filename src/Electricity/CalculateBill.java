package Electricity;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class CalculateBill extends JFrame implements ActionListener{
    JLabel l1,l2,l3,l4,l5;
    JTextField t1;
    Choice c1,c2;
    JButton b1,b2;
    JPanel p;
    CalculateBill(){
        
        p = new JPanel();
        p.setLayout(null);
        p.setBackground(new Color(173, 216, 230));
        
        l1 = new JLabel("Calculate Electricity Bill");
        l1.setBounds(30, 10, 400, 30);
        
        l2 = new JLabel("Meter No");
        l2.setBounds(60, 70, 100, 30);
        
        JLabel l6 = new JLabel("Name");
        l6.setBounds(60, 120, 100, 30);
        
        JLabel l7 = new JLabel("Address");
        l7.setBounds(60, 170, 100, 30);
        
        l3 = new JLabel("Units Cosumed");
        l3.setBounds(60, 220, 100, 30);
        
        l5 = new JLabel("Month");
        l5.setBounds(60, 270, 100, 30);
        
        c1 = new Choice();
        c1.setBounds(200, 70, 180, 20);
        // Load meter numbers into choice c1
        try (Conn conn = new Conn(); Statement stmt = conn.c.createStatement(); ResultSet rs = stmt.executeQuery("select meter from customer")) {
            while(rs.next()){
                c1.add(rs.getString("meter"));
            }
        } catch(SQLException e){
            e.printStackTrace();
            // Handle error loading meter numbers, e.g., show a message
        }
        
        JLabel l11 = new JLabel();
        l11.setBounds(200, 120, 180, 20);
        p.add(l11);
        
        JLabel l12 = new JLabel();
        l12.setBounds(200, 170, 180, 20);
        p.add(l12);
        
        // Initial load of customer details
        loadCustomerDetails(c1.getSelectedItem(), l11, l12);
        
        c1.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent ae){
                loadCustomerDetails(c1.getSelectedItem(), l11, l12);
            }
        });
        
        t1 = new JTextField();
        t1.setBounds(200, 220, 180, 20);
        
        
        c2 = new Choice();
        c2.setBounds(200, 270, 180, 20);
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
        
        b1 = new JButton("Submit");
        b1.setBounds(100, 350, 100, 25);
        b2 = new JButton("Cancel");
        b2.setBounds(230, 350, 100, 25);
        
        b1.setBackground(Color.BLACK);
        b1.setForeground(Color.WHITE);

        b2.setBackground(Color.BLACK);
        b2.setForeground(Color.WHITE);
        
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/hicon2.jpg"));
        Image i2 = i1.getImage().getScaledInstance(180, 270,Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        l4 = new JLabel(i3);
        
        
        
        l1.setFont(new Font("Senserif",Font.PLAIN,26));
        //Move the label to center
        l1.setHorizontalAlignment(JLabel.CENTER);
        
        
        p.add(l1);
        p.add(l2);
        p.add(l6);
        p.add(l7);
        p.add(c1);
        p.add(l5);
        p.add(c2);
        p.add(l3);
        p.add(t1);
        p.add(b1);
        p.add(b2);
        
        setLayout(new BorderLayout(30,30));
        
        
        add(p,"Center");
        add(l4,"West");
        
        
        b1.addActionListener(this);
        b2.addActionListener(this);
        
        getContentPane().setBackground(Color.WHITE);        
        setSize(750,500);
        setLocation(550,220);
    }
    
    // Helper method to load customer details
    private void loadCustomerDetails(String meterNumber, JLabel nameLabel, JLabel addressLabel) {
        String query = "select name, address from customer where meter = ?";
        try (Conn conn = new Conn(); PreparedStatement pstmt = conn.c.prepareStatement(query)) {
            pstmt.setString(1, meterNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    nameLabel.setText(rs.getString("name"));
                    addressLabel.setText(rs.getString("address"));
                } else {
                    nameLabel.setText("");
                    addressLabel.setText("");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle error loading customer details
            nameLabel.setText("Error");
            addressLabel.setText("Error");
        }
    }

    public void actionPerformed(ActionEvent ae){
        if(ae.getSource() == b1){
            String meter_no = c1.getSelectedItem();
            String unitsStr = t1.getText();
            String month = c2.getSelectedItem();

            if (unitsStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter units consumed.");
                return;
            }

            int units_consumed;
            try {
                units_consumed = Integer.parseInt(unitsStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid number for units consumed.");
                return;
            }

            int total_bill = 0;
            String queryTax = "select cost_per_unit, meter_rent, service_charge, service_tax, swacch_bharat_cess, fixed_tax from tax";
            // In a real app, tax might be specific to meter_no or customer type, but current schema implies one tax rule.

            try (Conn conn = new Conn();
                 Statement stmtTax = conn.c.createStatement();
                 ResultSet rsTax = stmtTax.executeQuery(queryTax)) {

                if (rsTax.next()) {
                    total_bill = units_consumed * rsTax.getInt("cost_per_unit");
                    total_bill += rsTax.getInt("meter_rent");
                    total_bill += rsTax.getInt("service_charge");
                    total_bill += rsTax.getInt("service_tax");
                    total_bill += rsTax.getInt("swacch_bharat_cess");
                    total_bill += rsTax.getInt("fixed_tax");
                } else {
                     JOptionPane.showMessageDialog(null,"Tax details not found. Please configure tax data.");
                     return;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,"Database Error while fetching tax details: " + e.getMessage());
                return;
            }

            String queryInsertBill = "insert into bill (meter_no, month, units, total_bill, status) values(?, ?, ?, ?, ?)";
            try (Conn conn = new Conn(); PreparedStatement psBill = conn.c.prepareStatement(queryInsertBill)) {
                psBill.setString(1, meter_no);
                psBill.setString(2, month);
                psBill.setInt(3, units_consumed);
                psBill.setInt(4, total_bill);
                psBill.setString(5, "Not Paid");
                psBill.executeUpdate();
                
                JOptionPane.showMessageDialog(null,"Customer Bill Updated Successfully");
                this.setVisible(false);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,"Database Error while saving bill: " + e.getMessage());
            }

        } else if(ae.getSource()== b2){
            this.setVisible(false);
        }        
    }
    
    public static void main(String[] args){
        new CalculateBill().setVisible(true);
    }
}
