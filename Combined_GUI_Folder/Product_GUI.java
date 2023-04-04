import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.BorderLayout.*;
import javax.swing.BorderFactory.*;
import javax.swing.border.*;

import java.util.*;

/**
 * The Product_GUI class creates a GUI for the manager that gives
 * information for the perishable and non-perishable items. It also
 * allows the manager to add items to the database.
 * @author Nathan Coles
 */

public class Product_GUI extends JFrame {
    static JFrame f;
    public static String selected_product; //make global string variable
    public static String current_table;
    public static Vector<String> product_list = new Vector<>();
    public static Vector<String> column_names = new Vector<>();
    public static Vector<String> results = new Vector<>();
    public static JList<String> product_jlist = new JList<>(product_list);
    public static JList<String> column_jlist = new JList<>(column_names);
    public static JList<String> results_jlist = new JList<>(results);
    public static Vector<JLabel> Jlabels = new Vector<>();
    public static int TRANSACTION_ID = 200000;

    /**
    *Gives constraint dimensions to a layout constraint varaible.
    *<p>
    *This method is constantly used in ordering and placing different labels and buttons
    *on a grid with the passed on parameters.
    *
    * @param gridx The x-axis of where we want our item to be placed
    * @param gridy The y-axis of where we want our item to be placed
    * @param gridwidth The width of how many grid spaced you want the item to take up
    * @return c The constraint variable that is configured
    */
    public static GridBagConstraints constraints(int gridx, int gridy, int gridwidth) {
      GridBagConstraints c = new GridBagConstraints();
      c.gridx = gridx;
      c.gridy = gridy;
      c.gridwidth = gridwidth;
      c.fill = GridBagConstraints.HORIZONTAL;
      c.anchor = GridBagConstraints.NORTH;
      return c;
    }

    /**
    *Manipulates the physical feature of a Java panal.
    *<p>
    *This method is constantly used in assigning panel their look on the front end
    *and it allows different size manipulation of the different panels.
    *
    * @param my_panel The panel that will be manipulated
    * @param color Desired color of the panel
    * @param border The border style of the panel
    * @param bound1x The x dimension of where the panel will be placed on the frame
    * @param bound1y The y dimension of where the panel will be placed on the frame
    * @param bound2x The x dimension of the panel size
    * @param bound2y The y dimension of the panel size
    */
    public static void adjust_panel(JPanel my_panel, Color color, Border border, int bound1x, int bound1y, int bound2x, int bound2y) {
      my_panel.setBackground(color);
      my_panel.setBorder(border);
      my_panel.setBounds(bound1x,bound1y,bound2x,bound2y);
    }

    public static int get_latest_transaction(String table) {
      //Building the connection
      Connection conn = null;
      try {
        Class.forName("org.postgresql.Driver");
        conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_theta",
            "csce315331_theta_master", "3NHS");
      } catch (Exception e) {
        e.printStackTrace();
        System.err.println(e.getClass().getName()+": "+e.getMessage());
        System.exit(0);
      }
  
      // QUERY DATABASE FOR LASTEST TRANSACTION ID
      int output = TRANSACTION_ID + 1;
      try{
        Statement stmt = conn.createStatement(); // create a statement object
        String query = "SELECT id FROM " + table + " ORDER BY id DESC LIMIT 1";  // create an SQL statement
        ResultSet result = stmt.executeQuery(query); // send statement to DBMS
        while (result.next()) { // Get responses from database
          output = Integer.parseInt(result.getString(1));
        }
      } catch (Exception e){
        JOptionPane.showMessageDialog(null,"Error accessing Database:\n" + e);
      }
  
      //closing the connection
      try {
        conn.close();
        //JOptionPane.showMessageDialog(null,"Connection Closed.");
      } catch(Exception e) {
        JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
      }
  
      return output + 1;
    }

    public Product_GUI() {
      Border loweredbevel = BorderFactory.createLoweredBevelBorder();
      f = new JFrame("Product Info GUI");

      //Building the connection
      Connection conn = null;
      //TODO STEP 1
      try {
        Class.forName("org.postgresql.Driver");
        conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_theta",
            "csce315331_theta_master", "3NHS");
      } catch (Exception e) {
        e.printStackTrace();
        System.err.println(e.getClass().getName()+": "+e.getMessage());
        System.exit(0);
      } //JOptionPane.showMessageDialog(null,"Opened database successfully");

      JButton close_button = new JButton("Close");
      for (int i = 0; i < 1; i++) {
        Connection temp_conn = conn;
        close_button.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            try { //closing the connection
              temp_conn.close();
              //JOptionPane.showMessageDialog(null,"Connection Closed.");
            } catch(Exception a) {
              JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
            }
            f.dispose();
          }
        });
      }
      // add actionlistener to close button
      // b.addActionListener(s);
      // add button to panel

        // set the size of frame

        //Create title panel
        JPanel title_panel = new JPanel(new GridBagLayout());
        adjust_panel(title_panel, Color.gray, BorderFactory.createLoweredBevelBorder(), 20, 20, 1340, 60);
        JLabel title_label = new JLabel("Products");
        title_panel.add(title_label);

      //Create search panel

        JPanel search_panel = new JPanel(new GridBagLayout());
        adjust_panel(search_panel, Color.gray, BorderFactory.createLoweredBevelBorder(), 20, 100, 400, 650);
        //search_panel.setSize(100, 700);

        JLabel product_label = new JLabel("Products");
        search_panel.add(product_label, constraints(0, 0, 2));

        //Create a lists and combo box of all menu items
        String products[] = {"Choose List","Menu_Meals","Menu_Drinks","Menu_Entrees","Menu_Sides","Perishable", "Non-perishable"};
        JComboBox<String> comb_box = new JComboBox<>(products);
        search_panel.add(comb_box, constraints(0, 1, 2));

        //Actionlistener with combo box, change list given in search_panel
        //Create show button for actionlistener
        //JButton show_button = new JButton("Show");
        //search_panel.add(show_button, constraints(0, 2, 2));

        //Add JList object to add to search panel
        search_panel.add(product_jlist, constraints(0, 3, 2));

        //Create Apply Button for list of products
        JButton delete_button = new JButton("Delete");

        //Create an Add button to add items to inventory
        JButton add_button = new JButton("Add");

        //Create an Update button to update items in inventory
        JButton update_button = new JButton("Update");

        //search_panel.setLayout(new BoxLayout(search_panel, BoxLayout.Y_AXIS));

        GridBagConstraints c = new GridBagConstraints();
        //Add buttons
        search_panel.add(delete_button, constraints(0,4,2));
        search_panel.add(update_button, constraints(0,5,2));
        search_panel.add(add_button, constraints(0,6,2));
        search_panel.add(close_button, constraints(0,7,2));


        //Create info panel
        JPanel info_panel = new JPanel(new GridBagLayout());
        //info_panel.setSize(300, 400);
        adjust_panel(info_panel, Color.white, BorderFactory.createLoweredBevelBorder(), 440,100,920,650);

        JLabel info_label = new JLabel("Product Info:");
        info_label.setHorizontalAlignment(JLabel.CENTER);
        info_label.setBorder(loweredbevel);
        column_jlist.setBorder(loweredbevel);
        results_jlist.setBorder(loweredbevel);

        Dimension infoDimension = new Dimension(850,100);
        Dimension infoHalfDimension = new Dimension(420,440);

        c.anchor = GridBagConstraints.NORTH;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        info_label.setBackground(Color.lightGray);
        info_label.setPreferredSize(infoDimension);
        info_label.setFont(new Font("Verdana",Font.BOLD,32));
        info_panel.add(info_label, c);

        c.insets =  new Insets(5, 5, 5, 5);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        column_jlist.setBackground(Color.lightGray);
        column_jlist.setPreferredSize(infoHalfDimension);
        column_jlist.setFont(new Font("Verdana",Font.BOLD,26));
        info_panel.add(column_jlist, c);

        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 1;
        results_jlist.setBackground(Color.lightGray);
        results_jlist.setPreferredSize(infoHalfDimension);
        results_jlist.setFont(new Font("Verdana",Font.BOLD,26));
        info_panel.add(results_jlist, c);

        c.insets =  new Insets(0, 0, 0, 0);


        //info_panel.setLayout(new BoxLayout(info_panel, BoxLayout.Y_AXIS));

        //Create background panel to keep sizing consistent
        JPanel temp_panel = new JPanel(new GridBagLayout());

        //Functionality of combo box (show button)
        comb_box.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent ae) {
          current_table = (comb_box.getSelectedItem()).toString();
            product_list.clear();
            column_names.clear();
            //Building the connection
            Connection conn = null;
            try {
              Class.forName("org.postgresql.Driver");
              conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_theta",
                "csce315331_theta_master", "3NHS");
            } catch (Exception e) {
              e.printStackTrace();
              System.err.println(e.getClass().getName()+": "+e.getMessage());
              System.exit(0);
            }

            if(comb_box.getSelectedItem() == "Menu_Meals"){
              //Add elements to product list from database
              try{
                //create a statement object
                Statement stmt = conn.createStatement();
                //create an SQL statement
                String sqlStatement = "SELECT * FROM menu_meals";
                //send statement to DBMS
                ResultSet result = stmt.executeQuery(sqlStatement);
                while(result.next()){
                  product_list.add(result.getString("name"));
                }
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }
              product_jlist.setListData(product_list);

              //Create string list of column names for table
              column_names.add("Name");
              column_names.add("Perishable_1");
              column_names.add("Perishable_2");
              column_names.add("Non_Perish_1");
              column_names.add("Non_Perish_2");
              column_names.add("Non_Perish_3");
              column_names.add("Price");
            }

            if(comb_box.getSelectedItem() == "Menu_Drinks"){
              //Add elements to product list from database
              try{
                //create a statement object
                Statement stmt = conn.createStatement();
                //create an SQL statement
                String sqlStatement = "SELECT * FROM menu_drinks";
                //send statement to DBMS
                ResultSet result = stmt.executeQuery(sqlStatement);
                while(result.next()){
                  product_list.add(result.getString("name"));
                }
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }
              product_jlist.setListData(product_list);

              //Create string list of column names for table
              column_names.add("Name");
              column_names.add("Perishable_1");
              column_names.add("Perishable_2");
              column_names.add("Non_Perish_1");
              column_names.add("Non_Perish_2");
              column_names.add("Non_Perish_3");
              column_names.add("Price");
            }

            if(comb_box.getSelectedItem() == "Menu_Entrees"){
              //Add elements to product list from database
              try{
                //create a statement object
                Statement stmt = conn.createStatement();
                //create an SQL statement
                String sqlStatement = "SELECT * FROM menu_entrees";
                //send statement to DBMS
                ResultSet result = stmt.executeQuery(sqlStatement);
                while(result.next()){
                  product_list.add(result.getString("name"));
                }
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }
              product_jlist.setListData(product_list);

              //Create string list of column names for table
              column_names.add("Name");
              column_names.add("Perishable_1");
              column_names.add("Perishable_2");
              column_names.add("Non_Perish_1");
              column_names.add("Non_Perish_2");
              column_names.add("Non_Perish_3");
              column_names.add("Price");
            }

            if(comb_box.getSelectedItem() == "Menu_Sides"){
              //Add elements to product list from database
              try{
                //create a statement object
                Statement stmt = conn.createStatement();
                //create an SQL statement
                String sqlStatement = "SELECT * FROM menu_sides";
                //send statement to DBMS
                ResultSet result = stmt.executeQuery(sqlStatement);
                while(result.next()){
                  product_list.add(result.getString("name"));
                }
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }
              product_jlist.setListData(product_list);

              //Create string list of column names for table
              column_names.add("Name");
              column_names.add("Perishable_1");
              column_names.add("Perishable_2");
              column_names.add("Non_Perish_1");
              column_names.add("Non_Perish_2");
              column_names.add("Non_Perish_3");
              column_names.add("Price");
            }

            if(comb_box.getSelectedItem() == "Perishable"){
              //Add elements to product list from database
              try{
                //create a statement object
                Statement stmt = conn.createStatement();
                //create an SQL statement
                String sqlStatement = "SELECT * FROM perishable";
                //send statement to DBMS
                ResultSet result = stmt.executeQuery(sqlStatement);
                while(result.next()){
                  product_list.add(result.getString("name"));
                }
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }
              product_jlist.setListData(product_list);

              //Create string list of column names for table
              column_names.add("ID");
              column_names.add("Name");
              column_names.add("Stock");
              column_names.add("Reorder");
              column_names.add("Reorder_Date");
              column_names.add("Serving_Size");
              column_names.add("Price");
            }

            if(comb_box.getSelectedItem() == "Non-perishable"){
              //Add elements to list from database
              try{
                //create a statement object
                Statement stmt = conn.createStatement();
                //create an SQL statement
                String sqlStatement = "SELECT * FROM non_perishable";
                //send statement to DBMS
                ResultSet result = stmt.executeQuery(sqlStatement);
                while(result.next()){
                  product_list.add(result.getString("name"));
                }
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }
              product_jlist.setListData(product_list);

              //Create string list of column names for table
              column_names.add("ID");
              column_names.add("Name");
              column_names.add("Stock");
              column_names.add("Reorder_Date");
              column_names.add("Buy_Price");
            }

            /*for (int i = 0; i < column_names.size(); i++){
              Jlabels.add(new JLabel(column_names.get(i)+ ": "));
            }*/
            column_jlist.setListData(column_names);

            /*for (int i = 0; i< Jlabels.size(); i++){
              info_panel.add(Jlabels.get(i), constraints(0, i+1, 2));
            }*/

            //closing the connection
            try {
              conn.close();
            } catch(Exception e) {
              JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
            }
          }
        });

        //Apply Button Functionality
        product_jlist.addListSelectionListener(new ListSelectionListener() {
          public void valueChanged(ListSelectionEvent listSelectionEvent){
            if (product_jlist.getSelectedIndex() != -1){
              //Use global string variable
              selected_product = product_jlist.getSelectedValue();
            }
            results.clear();
            //Building the connection
            Connection conn = null;
            try {
              Class.forName("org.postgresql.Driver");
              conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_theta",
                "csce315331_theta_master", "3NHS");
            } catch (Exception e) {
              e.printStackTrace();
              System.err.println(e.getClass().getName()+": "+e.getMessage());
              System.exit(0);
            }

            if (current_table == "Menu_Meals"){

              try{
                //Gather info for menu_meals item
                //create a statement object
                Statement stmt1 = conn.createStatement();
                //create an SQL statement
                String sqlStatement1 = "SELECT * FROM menu_meals WHERE name = " + "'" + selected_product + "'";
                //send statement to DBMS
                ResultSet result1 = stmt1.executeQuery(sqlStatement1);
                while (result1.next()) {
                  results.add(result1.getString("name"));
                  results.add(result1.getString("perishable_1"));
                  results.add(result1.getString("perishable_2"));
                  results.add(result1.getString("non_perish_1"));
                  results.add(result1.getString("non_perish_2"));
                  results.add(result1.getString("non_perish_3"));
                  results.add(result1.getString("price"));
                }
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }

            //closing the connection
            try {
              conn.close();
            } catch(Exception e) {
              JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
            }
            }

            if (current_table == "Menu_Drinks"){

              try{
                //Gather info for menu_meals item
                //create a statement object
                Statement stmt1 = conn.createStatement();
                //create an SQL statement
                String sqlStatement1 = "SELECT * FROM menu_drinks WHERE name = " + "'" + selected_product + "'";
                //send statement to DBMS
                ResultSet result1 = stmt1.executeQuery(sqlStatement1);
                while (result1.next()) {
                  results.add(result1.getString("name"));
                  results.add(result1.getString("perishable_1"));
                  results.add(result1.getString("perishable_2"));
                  results.add(result1.getString("non_perish_1"));
                  results.add(result1.getString("non_perish_2"));
                  results.add(result1.getString("non_perish_3"));
                  results.add(result1.getString("price"));
                }
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }

            //closing the connection
            try {
              conn.close();
            } catch(Exception e) {
              JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
            }
            }

            if (current_table == "Menu_Entrees"){

              try{
                //Gather info for menu_meals item
                //create a statement object
                Statement stmt1 = conn.createStatement();
                //create an SQL statement
                String sqlStatement1 = "SELECT * FROM menu_entrees WHERE name = " + "'" + selected_product + "'";
                //send statement to DBMS
                ResultSet result1 = stmt1.executeQuery(sqlStatement1);
                while (result1.next()) {
                  results.add(result1.getString("name"));
                  results.add(result1.getString("perishable_1"));
                  results.add(result1.getString("perishable_2"));
                  results.add(result1.getString("non_perish_1"));
                  results.add(result1.getString("non_perish_2"));
                  results.add(result1.getString("non_perish_3"));
                  results.add(result1.getString("price"));
                }
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }

            //closing the connection
            try {
              conn.close();
            } catch(Exception e) {
              JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
            }
            }

            if (current_table == "Menu_Sides"){

              try{
                //Gather info for menu_meals item
                //create a statement object
                Statement stmt1 = conn.createStatement();
                //create an SQL statement
                String sqlStatement1 = "SELECT * FROM menu_sides WHERE name = " + "'" + selected_product + "'";
                //send statement to DBMS
                ResultSet result1 = stmt1.executeQuery(sqlStatement1);
                while (result1.next()) {
                  results.add(result1.getString("name"));
                  results.add(result1.getString("perishable_1"));
                  results.add(result1.getString("perishable_2"));
                  results.add(result1.getString("non_perish_1"));
                  results.add(result1.getString("non_perish_2"));
                  results.add(result1.getString("non_perish_3"));
                  results.add(result1.getString("price"));
                }
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }

            //closing the connection
            try {
              conn.close();
            } catch(Exception e) {
              JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
            }
            }

            if (current_table == "Perishable"){
              /*String product_id = "null";
              String product_name = "null";
              String stock_amount = "null";
              String reorder_level = "null";
              String reorder_date = "null";
              String serving_size = "null";
              String buy_price = "null";*/

              try{
                //Gather info for perishable item
                //create a statement object
                Statement stmt1 = conn.createStatement();
                //create an SQL statement
                String sqlStatement1 = "SELECT * FROM perishable WHERE name = " + "'" + selected_product + "'";
                //send statement to DBMS
                ResultSet result1 = stmt1.executeQuery(sqlStatement1);
                while (result1.next()) {
                  results.add(result1.getString("id"));
                  results.add(result1.getString("name"));
                  results.add(result1.getString("stock"));
                  results.add(result1.getString("reorder"));
                  results.add(result1.getString("reorder_date"));
                  results.add(result1.getString("serving_size"));
                  results.add(result1.getString("price"));
                }
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }

            //closing the connection
            try {
              conn.close();
            } catch(Exception e) {
              JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
            }
            }
            if (current_table == "Non-perishable"){
              /*String product_id = "null";
              String product_name = "null";
              String stock_amount = "null";
              String reorder_level = "null";
              String reorder_date = "null";
              String serving_size = "null";
              String buy_price = "null";*/
              try{
                //Create a statement for nonperishable items
                //create a statement object
                Statement stmt2 = conn.createStatement();
                //create an SQL statement
                String sqlStatement2 = "SELECT * FROM non_perishable WHERE name = " + "'" + selected_product + "'";
                //send statement to DBMS
                ResultSet result2 = stmt2.executeQuery(sqlStatement2);
                while (result2.next()) {
                  results.add(result2.getString("id"));
                  results.add(result2.getString("name"));
                  results.add(result2.getString("stock"));
                  results.add(result2.getString("reorder_date"));
                  results.add(result2.getString("buy_price"));
                }
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }

            //closing the connection
            try {
              conn.close();
            } catch(Exception e) {
              JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
            }
            }

            results_jlist.setListData(results);
        }
      });

      //Functionality of add button
      add_button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae){
            results.clear();
            product_list.clear();
            //Building the connection
            Connection conn = null;
            try {
              Class.forName("org.postgresql.Driver");
              conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_theta",
                "csce315331_theta_master", "3NHS");
            } catch (Exception e) {
              e.printStackTrace();
              System.err.println(e.getClass().getName()+": "+e.getMessage());
              System.exit(0);
            }
            if(current_table == "Menu_Meals"){
              String name = JOptionPane.showInputDialog(f, "Item name: ");
              String perish1 = JOptionPane.showInputDialog(f, "Item perishable 1: ");
              String perish2 = JOptionPane.showInputDialog(f, "Item perishable 2: ");
              String nonperish1 = JOptionPane.showInputDialog(f, "Item non_perishable 1: ");
              String nonperish2 = JOptionPane.showInputDialog(f, "Item non_perishable 2: ");
              String nonperish3 = JOptionPane.showInputDialog(f, "Item non_perishable 3: ");
              String price = JOptionPane.showInputDialog(f, "Item price: ");
              //Add to database
              try{
                //create a statement object
                Statement stmt = conn.createStatement();
                //create an SQL statement
                //Note: ID inputed manually
                String sqlStatement = "INSERT INTO menu_meals(name,perishable_1,perishable_2,non_perish_1,non_perish_2,non_perish_3,price) VALUES ("
                + "'" + name + "'" + "," + "'" + perish1 + "'" + "," +"'"+perish2+"'"+ "," + "'"+nonperish1+"'" + ","
                + "'"+nonperish2+"'"+ "," + "'"+nonperish3+"'" + "," + "'"+price+"'" + ");";
                //send statement to DBMS
                int rows = stmt.executeUpdate(sqlStatement);
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }

              //Add elements to product list from database
              try{
                //create a statement object
                Statement stmt = conn.createStatement();
                //create an SQL statement
                String sqlStatement = "SELECT * FROM menu_meals";
                //send statement to DBMS
                ResultSet result = stmt.executeQuery(sqlStatement);
                while(result.next()){
                  product_list.add(result.getString("name"));
                }
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }
              product_jlist.setListData(product_list);

            }
            if(current_table == "Menu_Drinks"){
              String name = JOptionPane.showInputDialog(f, "Item name: ");
              String perish1 = JOptionPane.showInputDialog(f, "Item perishable 1: ");
              String perish2 = JOptionPane.showInputDialog(f, "Item perishable 2: ");
              String nonperish1 = JOptionPane.showInputDialog(f, "Item non_perishable 1: ");
              String nonperish2 = JOptionPane.showInputDialog(f, "Item non_perishable 2: ");
              String nonperish3 = JOptionPane.showInputDialog(f, "Item non_perishable 3: ");
              String price = JOptionPane.showInputDialog(f, "Item price: ");
              //Add to database
              try{
                //create a statement object
                Statement stmt = conn.createStatement();
                //create an SQL statement
                //Note: ID inputed manually
                String sqlStatement = "INSERT INTO menu_drinks(name,perishable_1,perishable_2,non_perish_1,non_perish_2,non_perish_3,price) VALUES ("
                + "'" + name + "'" + "," + "'" + perish1 + "'" + "," +"'"+perish2+"'"+ "," + "'"+nonperish1+"'" + ","
                + "'"+nonperish2+"'"+ "," + "'"+nonperish3+"'" + "," + "'"+price+"'" + ");";
                //send statement to DBMS
                int rows = stmt.executeUpdate(sqlStatement);
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }

              //Add elements to product list from database
              try{
                //create a statement object
                Statement stmt = conn.createStatement();
                //create an SQL statement
                String sqlStatement = "SELECT * FROM menu_drinks";
                //send statement to DBMS
                ResultSet result = stmt.executeQuery(sqlStatement);
                while(result.next()){
                  product_list.add(result.getString("name"));
                }
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }
              product_jlist.setListData(product_list);

            }
            if(current_table == "Menu_Entrees"){
              String name = JOptionPane.showInputDialog(f, "Item name: ");
              String perish1 = JOptionPane.showInputDialog(f, "Item perishable 1: ");
              String perish2 = JOptionPane.showInputDialog(f, "Item perishable 2: ");
              String nonperish1 = JOptionPane.showInputDialog(f, "Item non_perishable 1: ");
              String nonperish2 = JOptionPane.showInputDialog(f, "Item non_perishable 2: ");
              String nonperish3 = JOptionPane.showInputDialog(f, "Item non_perishable 3: ");
              String price = JOptionPane.showInputDialog(f, "Item price: ");
              //Add to database
              try{
                //create a statement object
                Statement stmt = conn.createStatement();
                //create an SQL statement
                //Note: ID inputed manually
                String sqlStatement = "INSERT INTO menu_entrees(name,perishable_1,perishable_2,non_perish_1,non_perish_2,non_perish_3,price) VALUES ("
                + "'" + name + "'" + "," + "'" + perish1 + "'" + "," +"'"+perish2+"'"+ "," + "'"+nonperish1+"'" + ","
                + "'"+nonperish2+"'"+ "," + "'"+nonperish3+"'" + "," + "'"+price+"'" + ");";
                //send statement to DBMS
                int rows = stmt.executeUpdate(sqlStatement);
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }

              //Add elements to product list from database
              try{
                //create a statement object
                Statement stmt = conn.createStatement();
                //create an SQL statement
                String sqlStatement = "SELECT * FROM menu_entrees";
                //send statement to DBMS
                ResultSet result = stmt.executeQuery(sqlStatement);
                while(result.next()){
                  product_list.add(result.getString("name"));
                }
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }
              product_jlist.setListData(product_list);
            }
            if(current_table == "Menu_Sides"){
              String name = JOptionPane.showInputDialog(f, "Item name: ");
              String perish1 = JOptionPane.showInputDialog(f, "Item perishable 1: ");
              String perish2 = JOptionPane.showInputDialog(f, "Item perishable 2: ");
              String nonperish1 = JOptionPane.showInputDialog(f, "Item non_perishable 1: ");
              String nonperish2 = JOptionPane.showInputDialog(f, "Item non_perishable 2: ");
              String nonperish3 = JOptionPane.showInputDialog(f, "Item non_perishable 3: ");
              String price = JOptionPane.showInputDialog(f, "Item price: ");
              //Add to database
              try{
                //create a statement object
                Statement stmt = conn.createStatement();
                //create an SQL statement
                //Note: ID inputed manually
                String sqlStatement = "INSERT INTO menu_sides(name,perishable_1,perishable_2,non_perish_1,non_perish_2,non_perish_3,price) VALUES ("
                + "'" + name + "'" + "," + "'" + perish1 + "'" + "," +"'"+perish2+"'"+ "," + "'"+nonperish1+"'" + ","
                + "'"+nonperish2+"'"+ "," + "'"+nonperish3+"'" + "," + "'"+price+"'" + ");";
                //send statement to DBMS
                int rows = stmt.executeUpdate(sqlStatement);
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }

              //Add elements to product list from database
              try{
                //create a statement object
                Statement stmt = conn.createStatement();
                //create an SQL statement
                String sqlStatement = "SELECT * FROM menu_sides";
                //send statement to DBMS
                ResultSet result = stmt.executeQuery(sqlStatement);
                while(result.next()){
                  product_list.add(result.getString("name"));
                }
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }
              product_jlist.setListData(product_list);

            }
          if(current_table == "Perishable"){
            int id_num = get_latest_transaction("perishable");
            String id = Integer.toString(id_num);
            String name = JOptionPane.showInputDialog(f, "Item name: ");
            String stock = JOptionPane.showInputDialog(f, "Item stock amount: ");
            String reorder = JOptionPane.showInputDialog(f, "Item reorder level: ");
            String reorder_date = JOptionPane.showInputDialog(f, "Item reorder_date: ");
            String serving = JOptionPane.showInputDialog(f, "Item serving size: ");
            String price = JOptionPane.showInputDialog(f, "Item price: ");
            //Add to database
            try{
              //create a statement object
              Statement stmt = conn.createStatement();
              //create an SQL statement
              //Note: ID inputed manually
              String sqlStatement = "INSERT INTO perishable(id,name,stock,reorder,reorder_date,serving_size,price) VALUES ('"
              + id + "'" + "," + "'" + name + "'" + "," + "'" + stock + "'" + "," + "'"+ reorder + "'" + "," + "'" + reorder_date + 
              "'" + "," + "'" + serving+ "'" + "," + "'" + price + "'" + ");";
              //send statement to DBMS
              int rows = stmt.executeUpdate(sqlStatement);
            } catch (Exception e){
              JOptionPane.showMessageDialog(null,"Error accessing Database.");
            }

              //Add elements to product list from database
              try{
                //create a statement object
                Statement stmt = conn.createStatement();
                //create an SQL statement
                String sqlStatement = "SELECT * FROM perishable";
                //send statement to DBMS
                ResultSet result = stmt.executeQuery(sqlStatement);
                while(result.next()){
                  product_list.add(result.getString("name"));
                }
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }
              product_jlist.setListData(product_list);

          }
          if(current_table == "Non-perishable"){
            //Collect info
            int id_num = get_latest_transaction("non_perishable");
            String id = Integer.toString(id_num);
            String name = JOptionPane.showInputDialog(f, "Item name: ");
            String stock = JOptionPane.showInputDialog(f, "Item stock amount: ");
            String reorder_date = JOptionPane.showInputDialog(f, "Item reorder_date: ");
            String price = JOptionPane.showInputDialog(f, "Item price: ");

            //Add to database
            try{
              //create a statement object
              Statement stmt = conn.createStatement();
              //create an SQL statement
              //TODO Step 2
              String sqlStatement = "INSERT INTO non_perishable(id,name,stock,reorder_date,buy_price) VALUES ('"
              + id + "'" + "," + "'" + name + "'" + "," + "'" + stock + "'" + "," + "'" + reorder_date + "'" + "," + 
              "'" + price + "'" + ");";
              //send statement to DBMS
              int rows = stmt.executeUpdate(sqlStatement);
            } catch (Exception e){
              JOptionPane.showMessageDialog(null,"Error accessing Database.");
            }

              //Add elements to list from database
              try{
                //create a statement object
                Statement stmt = conn.createStatement();
                //create an SQL statement
                String sqlStatement = "SELECT * FROM non_perishable";
                //send statement to DBMS
                ResultSet result = stmt.executeQuery(sqlStatement);
                while(result.next()){
                  product_list.add(result.getString("name"));
                }
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }
              product_jlist.setListData(product_list);

          }

            //closing the connection
            try {
              conn.close();
            } catch(Exception e) {
              JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
            }

            results_jlist.setListData(results);
        }
      });

      //Update button functionality
      update_button.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent ae){
            //Check if item is selected
            if (selected_product != ""){
              results.clear();
            //Building the connection
            Connection conn = null;
            try {
              Class.forName("org.postgresql.Driver");
              conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_theta",
                "csce315331_theta_master", "3NHS");
            } catch (Exception e) {
              e.printStackTrace();
              System.err.println(e.getClass().getName()+": "+e.getMessage());
              System.exit(0);
            }
            if(current_table == "Menu_Meals"){
              String column = JOptionPane.showInputDialog(f, "Updated Column: ");
              String value = JOptionPane.showInputDialog(f, "Updated Value: ");
              //Add to database
              try{
                //create a statement object
                Statement stmt = conn.createStatement();
                //create an SQL statement
                //Note: ID inputed manually
                String sqlStatement = "UPDATE menu_meals SET " + column + " = '" + value + "' WHERE name = '" + selected_product + "';";
                //send statement to DBMS
                int rows = stmt.executeUpdate(sqlStatement);
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }

              //Rewrite to GUI
              try{
                //Gather info for menu_meals item
                //create a statement object
                Statement stmt1 = conn.createStatement();
                //create an SQL statement
                String sqlStatement1 = "SELECT * FROM menu_meals WHERE name = " + "'" + selected_product + "'";
                //send statement to DBMS
                ResultSet result1 = stmt1.executeQuery(sqlStatement1);
                while (result1.next()) {
                  results.add(result1.getString("name"));
                  results.add(result1.getString("perishable_1"));
                  results.add(result1.getString("perishable_2"));
                  results.add(result1.getString("non_perish_1"));
                  results.add(result1.getString("non_perish_2"));
                  results.add(result1.getString("non_perish_3"));
                  results.add(result1.getString("price"));
                }
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }
            }
            if(current_table == "Menu_Drinks"){
              String column = JOptionPane.showInputDialog(f, "Updated Column: ");
              String value = JOptionPane.showInputDialog(f, "Updated Value: ");
              //Add to database
              try{
                //create a statement object
                Statement stmt = conn.createStatement();
                //create an SQL statement
                //Note: ID inputed manually
                String sqlStatement = "UPDATE menu_drinks SET " + column + " = '" + value + "' WHERE name = '" + selected_product + "';";
                //send statement to DBMS
                int rows = stmt.executeUpdate(sqlStatement);
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }

              //Rewrite to GUI
              try{
                //Gather info for menu_meals item
                //create a statement object
                Statement stmt1 = conn.createStatement();
                //create an SQL statement
                String sqlStatement1 = "SELECT * FROM menu_drinks WHERE name = " + "'" + selected_product + "'";
                //send statement to DBMS
                ResultSet result1 = stmt1.executeQuery(sqlStatement1);
                while (result1.next()) {
                  results.add(result1.getString("name"));
                  results.add(result1.getString("perishable_1"));
                  results.add(result1.getString("perishable_2"));
                  results.add(result1.getString("non_perish_1"));
                  results.add(result1.getString("non_perish_2"));
                  results.add(result1.getString("non_perish_3"));
                  results.add(result1.getString("price"));
                }
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }
            }
            if(current_table == "Menu_Entrees"){
              String column = JOptionPane.showInputDialog(f, "Updated Column: ");
              String value = JOptionPane.showInputDialog(f, "Updated Value: ");
              //Add to database
              try{
                //create a statement object
                Statement stmt = conn.createStatement();
                //create an SQL statement
                //Note: ID inputed manually
                String sqlStatement = "UPDATE menu_entrees SET " + column + " = '" + value + "' WHERE name = '" + selected_product + "';";
                //send statement to DBMS
                int rows = stmt.executeUpdate(sqlStatement);
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }

              //Rewrite to GUI
              try{
                //Gather info for menu_meals item
                //create a statement object
                Statement stmt1 = conn.createStatement();
                //create an SQL statement
                String sqlStatement1 = "SELECT * FROM menu_entrees WHERE name = " + "'" + selected_product + "'";
                //send statement to DBMS
                ResultSet result1 = stmt1.executeQuery(sqlStatement1);
                while (result1.next()) {
                  results.add(result1.getString("name"));
                  results.add(result1.getString("perishable_1"));
                  results.add(result1.getString("perishable_2"));
                  results.add(result1.getString("non_perish_1"));
                  results.add(result1.getString("non_perish_2"));
                  results.add(result1.getString("non_perish_3"));
                  results.add(result1.getString("price"));
                }
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }
            }
            if(current_table == "Menu_Sides"){
              String column = JOptionPane.showInputDialog(f, "Updated Column: ");
              String value = JOptionPane.showInputDialog(f, "Updated Value: ");
              //Add to database
              try{
                //create a statement object
                Statement stmt = conn.createStatement();
                //create an SQL statement
                //Note: ID inputed manually
                String sqlStatement = "UPDATE menu_sides SET " + column + " = '" + value + "' WHERE name = '" + selected_product + "';";
                //send statement to DBMS
                int rows = stmt.executeUpdate(sqlStatement);
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }

              //Rewrite to GUI
              try{
                //Gather info for menu_meals item
                //create a statement object
                Statement stmt1 = conn.createStatement();
                //create an SQL statement
                String sqlStatement1 = "SELECT * FROM menu_sides WHERE name = " + "'" + selected_product + "'";
                //send statement to DBMS
                ResultSet result1 = stmt1.executeQuery(sqlStatement1);
                while (result1.next()) {
                  results.add(result1.getString("name"));
                  results.add(result1.getString("perishable_1"));
                  results.add(result1.getString("perishable_2"));
                  results.add(result1.getString("non_perish_1"));
                  results.add(result1.getString("non_perish_2"));
                  results.add(result1.getString("non_perish_3"));
                  results.add(result1.getString("price"));
                }
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }
            }
          if(current_table == "Perishable"){
            String column = JOptionPane.showInputDialog(f, "Updated Column: ");
            String value = JOptionPane.showInputDialog(f, "Updated Value: ");
            //Add to database
            try{
              //create a statement object
              Statement stmt = conn.createStatement();
              //create an SQL statement
              //Note: ID inputed manually
              String sqlStatement = "UPDATE perishable SET " + column + " = '" + value + "' WHERE name = '" + selected_product + "';";
              //send statement to DBMS
              int rows = stmt.executeUpdate(sqlStatement);
            } catch (Exception e){
              JOptionPane.showMessageDialog(null,"Error accessing Database.");
            }

            //Rewrite to GUI
            try{
              //Gather info for perishable item
              //create a statement object
              Statement stmt1 = conn.createStatement();
              //create an SQL statement
              String sqlStatement1 = "SELECT * FROM perishable WHERE name = " + "'" + selected_product + "'";
              //send statement to DBMS
              ResultSet result1 = stmt1.executeQuery(sqlStatement1);
              while (result1.next()) {
                results.add(result1.getString("id"));
                results.add(result1.getString("name"));
                results.add(result1.getString("stock"));
                results.add(result1.getString("reorder"));
                results.add(result1.getString("reorder_date"));
                results.add(result1.getString("serving_size"));
                results.add(result1.getString("price"));
              }
            } catch (Exception e){
              JOptionPane.showMessageDialog(null,"Error accessing Database.");
            }
          }
            if(current_table == "Non-perishable"){
              //Collect info
              String column = JOptionPane.showInputDialog(f, "Updated Column: ");
              String value = JOptionPane.showInputDialog(f, "Updated Value: ");

              //Add to database
              try{
                //create a statement object
                Statement stmt = conn.createStatement();
                //create an SQL statement
                String sqlStatement = "UPDATE non_perishable SET " + column + " = '" + value + "' WHERE name = '" + selected_product + "';";
                //send statement to DBMS
                int rows = stmt.executeUpdate(sqlStatement);
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }

              //Rewrite to GUI
              try{
                //Create a statement for nonperishable items
                //create a statement object
                Statement stmt2 = conn.createStatement();
                //create an SQL statement
                String sqlStatement2 = "SELECT * FROM non_perishable WHERE name = " + "'" + selected_product + "'";
                //send statement to DBMS
                ResultSet result2 = stmt2.executeQuery(sqlStatement2);
                while (result2.next()) {
                  results.add(result2.getString("id"));
                  results.add(result2.getString("name"));
                  results.add(result2.getString("stock"));
                  results.add(result2.getString("reorder_date"));
                  results.add(result2.getString("buy_price"));
                }
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }
            }

            //closing the connection
            try {
              conn.close();
            } catch(Exception e) {
              JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
            }
            }
            else {
              JOptionPane.showMessageDialog(f, "Make sure item is selected before updating.", "Alert", JOptionPane.WARNING_MESSAGE);
            }

            results_jlist.setListData(results); 

        }
      });

      delete_button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae){
          product_list.clear();
          //Building the connection
          Connection conn = null;
          try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_theta",
              "csce315331_theta_master", "3NHS");
          } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
          }

          if(current_table == "Menu_Meals"){
              //Delete from database
              try{
                //create a statement object
                Statement stmt = conn.createStatement();
                //create an SQL statement
                String sqlStatement = "DELETE FROM menu_meals WHERE name = '" + selected_product + "';";
                //send statement to DBMS
                int rows = stmt.executeUpdate(sqlStatement);
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }

            //Add elements to product list from database
            try{
              //create a statement object
              Statement stmt = conn.createStatement();
              //create an SQL statement
              String sqlStatement = "SELECT * FROM menu_meals";
              //send statement to DBMS
              ResultSet result = stmt.executeQuery(sqlStatement);
              while(result.next()){
                product_list.add(result.getString("name"));
              }
            } catch (Exception e){
              JOptionPane.showMessageDialog(null,"Error accessing Database.");
            }
            product_jlist.setListData(product_list);
          }

          if(current_table == "Menu_Drinks"){
              //Delete from database
              try{
                //create a statement object
                Statement stmt = conn.createStatement();
                //create an SQL statement
                String sqlStatement = "DELETE FROM menu_drinks WHERE name = '" + selected_product + "';";
                //send statement to DBMS
                int rows = stmt.executeUpdate(sqlStatement);
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }
            //Add elements to product list from database
            try{
              //create a statement object
              Statement stmt = conn.createStatement();
              //create an SQL statement
              String sqlStatement = "SELECT * FROM menu_drinks";
              //send statement to DBMS
              ResultSet result = stmt.executeQuery(sqlStatement);
              while(result.next()){
                product_list.add(result.getString("name"));
              }
            } catch (Exception e){
              JOptionPane.showMessageDialog(null,"Error accessing Database.");
            }
            product_jlist.setListData(product_list);
          }

          if(current_table == "Menu_Entrees"){
              //Delete from database
              try{
                //create a statement object
                Statement stmt = conn.createStatement();
                //create an SQL statement
                String sqlStatement = "DELETE FROM menu_entrees WHERE name = '" + selected_product + "';";
                //send statement to DBMS
                int rows = stmt.executeUpdate(sqlStatement);
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }
            //Add elements to product list from database
            try{
              //create a statement object
              Statement stmt = conn.createStatement();
              //create an SQL statement
              String sqlStatement = "SELECT * FROM menu_entrees";
              //send statement to DBMS
              ResultSet result = stmt.executeQuery(sqlStatement);
              while(result.next()){
                product_list.add(result.getString("name"));
              }
            } catch (Exception e){
              JOptionPane.showMessageDialog(null,"Error accessing Database.");
            }
            product_jlist.setListData(product_list);
          }

          if(current_table == "Menu_Sides"){
              //Delete from database
              try{
                //create a statement object
                Statement stmt = conn.createStatement();
                //create an SQL statement
                String sqlStatement = "DELETE FROM menu_sides WHERE name = '" + selected_product + "';";
                //send statement to DBMS
                int rows = stmt.executeUpdate(sqlStatement);
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }
            //Add elements to product list from database
            try{
              //create a statement object
              Statement stmt = conn.createStatement();
              //create an SQL statement
              String sqlStatement = "SELECT * FROM menu_sides";
              //send statement to DBMS
              ResultSet result = stmt.executeQuery(sqlStatement);
              while(result.next()){
                product_list.add(result.getString("name"));
              }
            } catch (Exception e){
              JOptionPane.showMessageDialog(null,"Error accessing Database.");
            }
            product_jlist.setListData(product_list);
          }

          if(current_table == "Perishable"){
              //Delete from database
              try{
                //create a statement object
                Statement stmt = conn.createStatement();
                //create an SQL statement
                String sqlStatement = "DELETE FROM perishable WHERE name = '" + selected_product + "';";
                //send statement to DBMS
                int rows = stmt.executeUpdate(sqlStatement);
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }
            //Add elements to product list from database
            try{
              //create a statement object
              Statement stmt = conn.createStatement();
              //create an SQL statement
              String sqlStatement = "SELECT * FROM perishable";
              //send statement to DBMS
              ResultSet result = stmt.executeQuery(sqlStatement);
              while(result.next()){
                product_list.add(result.getString("name"));
              }
            } catch (Exception e){
              JOptionPane.showMessageDialog(null,"Error accessing Database.");
            }
            product_jlist.setListData(product_list);
          }

          if(current_table == "Non-perishable"){
              //Delete from database
              try{
                //create a statement object
                Statement stmt = conn.createStatement();
                //create an SQL statement
                String sqlStatement = "DELETE FROM non_perishable WHERE name = '" + selected_product + "';";
                //send statement to DBMS
                int rows = stmt.executeUpdate(sqlStatement);
              } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Error accessing Database.");
              }
            //Add elements to list from database
            try{
              //create a statement object
              Statement stmt = conn.createStatement();
              //create an SQL statement
              String sqlStatement = "SELECT * FROM non_perishable";
              //send statement to DBMS
              ResultSet result = stmt.executeQuery(sqlStatement);
              while(result.next()){
                product_list.add(result.getString("name"));
              }
            } catch (Exception e){
              JOptionPane.showMessageDialog(null,"Error accessing Database.");
            }
            product_jlist.setListData(product_list);
          }

          //closing the connection
          try {
            conn.close();
          } catch(Exception e) {
            JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
          }
        }
      });

       //Add panels
       f.add(title_panel);
       f.add(search_panel);
       f.add(info_panel);
       f.add(temp_panel);

       f.setSize(1400, 900);

       f.setVisible(true);

    }

  }