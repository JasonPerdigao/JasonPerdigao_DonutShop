package program;

import core.DB;
import entity.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/** Oak Donuts point-of-sale and customer/order manager. */
public class Main extends JFrame {
    private static final Color BROWN=new Color(91,55,39), PINK=new Color(247,211,216), CREAM=new Color(255,249,239);
    private static final DateTimeFormatter FMT=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Map<String,Integer> MENU=new LinkedHashMap<>();
    static {
        MENU.put("Classic Glazed",199); MENU.put("Chocolate Frosted",229); MENU.put("Strawberry Sprinkle",249);
        MENU.put("Boston Cream",279); MENU.put("Maple Glazed",249); MENU.put("Cinnamon Sugar",199);
        MENU.put("Jelly Filled",259); MENU.put("Cookies & Cream",299); MENU.put("Old-Fashioned",219);
        MENU.put("Blueberry Cake",249); MENU.put("Apple Fritter",329); MENU.put("Dozen Assorted",2499);
    }
    private final CustomerDAO customers=new CustomerDAO(); private final OrderDAO orders=new OrderDAO();
    private final JTextField cId=new JTextField(8),cFirst=new JTextField(12),cLast=new JTextField(12);
    private final JComboBox<String> cFavorite=new JComboBox<>(MENU.keySet().toArray(String[]::new));
    private final JTable customerTable=new JTable();
    private final JTextField oId=new JTextField(7),oCustomer=new JTextField(7),oDate=new JTextField(17);
    private final JComboBox<String> donut=new JComboBox<>(MENU.keySet().toArray(String[]::new));
    private final JSpinner quantity=new JSpinner(new SpinnerNumberModel(1,1,99,1));
    private final JLabel priceLabel=new JLabel("$1.99 each  |  Total: $1.99");
    private final JTable orderTable=new JTable();

    public Main(){
        super("Jason Donuts | Shop Management");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); setMinimumSize(new Dimension(960,650)); setSize(1120,760); setLocationRelativeTo(null);
        JPanel shell=new JPanel(new BorderLayout(0,16));shell.setBackground(CREAM);shell.setBorder(new EmptyBorder(18,22,20,22));setContentPane(shell);
        JPanel banner=new JPanel(new BorderLayout());banner.setBackground(BROWN);banner.setBorder(new EmptyBorder(18,22,18,22));
        JLabel brand=new JLabel("Jason DONUTS");brand.setForeground(Color.WHITE);brand.setFont(new Font("SansSerif",Font.BOLD,27));
        JLabel tagline=new JLabel("FRESHLY MADE • ALWAYS DELICIOUS");tagline.setForeground(PINK);tagline.setFont(new Font("SansSerif",Font.BOLD,12));
        JPanel brandBox=new JPanel(new GridLayout(2,1));brandBox.setOpaque(false);brandBox.add(brand);brandBox.add(tagline);banner.add(brandBox,BorderLayout.WEST);
        JLabel badge=new JLabel("  DONUTS  •  COFFEE  •  GOOD DAYS  ");badge.setForeground(BROWN);badge.setBackground(PINK);badge.setOpaque(true);badge.setBorder(new EmptyBorder(10,12,10,12));banner.add(badge,BorderLayout.EAST);shell.add(banner,BorderLayout.NORTH);
        JTabbedPane tabs=new JTabbedPane();tabs.setFont(new Font("SansSerif",Font.BOLD,14));tabs.addTab("New Order & Order History",buildOrderPanel());tabs.addTab("Customers",buildCustomerPanel());shell.add(tabs,BorderLayout.CENTER);
        refreshCustomers();refreshOrders();
    }
    private JPanel panel(){JPanel p=new JPanel();p.setBackground(CREAM);p.setBorder(new EmptyBorder(16,12,12,12));return p;}
    private JPanel buildOrderPanel(){
        JPanel root=panel();root.setLayout(new BorderLayout(12,14));
        JPanel form=new JPanel(new GridBagLayout());form.setBackground(Color.WHITE);form.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(230,218,207)),new EmptyBorder(16,18,16,18)));
        GridBagConstraints g=new GridBagConstraints();g.insets=new Insets(7,8,7,8);g.anchor=GridBagConstraints.WEST;g.fill=GridBagConstraints.HORIZONTAL;
        int row=0;addField(form,g,row++,"Order ID",oId);addField(form,g,row++,"Customer ID",oCustomer);addField(form,g,row++,"Donut selection",donut);addField(form,g,row++,"Quantity",quantity);
        oDate.setText(LocalDateTime.now().format(FMT));addField(form,g,row++,"Order date/time",oDate);
        donut.addActionListener(e->updatePrice());quantity.addChangeListener(e->updatePrice());
        priceLabel.setFont(new Font("SansSerif",Font.BOLD,16));priceLabel.setForeground(BROWN);g.gridx=0;g.gridy=row;g.gridwidth=2;form.add(priceLabel,g);
        JPanel actions=new JPanel(new FlowLayout(FlowLayout.LEFT,8,4));actions.setOpaque(false);
        JButton add=button("Place Order"),update=button("Update Selected"),delete=button("Delete Selected"),clear=button("Clear Fields");
        actions.add(add);actions.add(update);actions.add(delete);actions.add(clear);g.gridy=++row;form.add(actions,g);
        add.addActionListener(e->safe("Place order",this::insertOrder));update.addActionListener(e->safe("Update order",this::updateOrder));delete.addActionListener(e->safe("Delete order",this::deleteOrder));clear.addActionListener(e->clearOrder());
        root.add(form,BorderLayout.NORTH);
        orderTable.setModel(model("Order ID","Customer ID","Customer","Donut","Qty","Total","Date / Time"));styleTable(orderTable);
        orderTable.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting()&&orderTable.getSelectedRow()>=0)loadOrder();});
        JPanel history=new JPanel(new BorderLayout(0,8));history.setOpaque(false);JLabel h=new JLabel("ORDER HISTORY");h.setForeground(BROWN);h.setFont(new Font("SansSerif",Font.BOLD,15));history.add(h,BorderLayout.NORTH);history.add(new JScrollPane(orderTable),BorderLayout.CENTER);root.add(history,BorderLayout.CENTER);
        updatePrice();return root;
    }
    private JPanel buildCustomerPanel(){
        JPanel root=panel();root.setLayout(new BorderLayout(12,14));
        JPanel form=new JPanel(new GridBagLayout());form.setBackground(Color.WHITE);form.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(230,218,207)),new EmptyBorder(16,18,16,18)));
        GridBagConstraints g=new GridBagConstraints();g.insets=new Insets(7,8,7,8);g.anchor=GridBagConstraints.WEST;g.fill=GridBagConstraints.HORIZONTAL;
        int r=0;addField(form,g,r++,"Customer ID",cId);addField(form,g,r++,"First name",cFirst);addField(form,g,r++,"Last name",cLast);addField(form,g,r++,"Favorite donut",cFavorite);
        JPanel actions=new JPanel(new FlowLayout(FlowLayout.LEFT,8,4));actions.setOpaque(false);JButton add=button("Add Customer"),update=button("Update Selected"),delete=button("Delete Selected"),clear=button("Clear Fields");
        actions.add(add);actions.add(update);actions.add(delete);actions.add(clear);g.gridx=0;g.gridy=r;g.gridwidth=2;form.add(actions,g);
        add.addActionListener(e->safe("Add customer",this::insertCustomer));update.addActionListener(e->safe("Update customer",this::updateCustomer));delete.addActionListener(e->safe("Delete customer",this::deleteCustomer));clear.addActionListener(e->clearCustomer());root.add(form,BorderLayout.NORTH);
        customerTable.setModel(model("Customer ID","First Name","Last Name","Favorite Donut"));styleTable(customerTable);
        customerTable.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting()&&customerTable.getSelectedRow()>=0)loadCustomer();});
        JPanel list=new JPanel(new BorderLayout(0,8));list.setOpaque(false);JLabel title=new JLabel("CUSTOMER DIRECTORY");title.setForeground(BROWN);title.setFont(new Font("SansSerif",Font.BOLD,15));list.add(title,BorderLayout.NORTH);list.add(new JScrollPane(customerTable),BorderLayout.CENTER);root.add(list,BorderLayout.CENTER);return root;
    }
    private void addField(JPanel p,GridBagConstraints g,int row,String label,JComponent field){g.gridx=0;g.gridy=row;g.gridwidth=1;g.weightx=0;p.add(new JLabel(label),g);g.gridx=1;g.weightx=1;p.add(field,g);}
    private JButton button(String text){JButton b=new JButton(text);b.setFocusPainted(false);b.setBackground(BROWN);b.setForeground(Color.WHITE);b.setFont(new Font("SansSerif",Font.BOLD,12));b.setBorder(new EmptyBorder(9,14,9,14));return b;}
    private DefaultTableModel model(String... cols){return new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};}
    private void styleTable(JTable t){t.setRowHeight(27);t.setFont(new Font("SansSerif",Font.PLAIN,13));t.getTableHeader().setFont(new Font("SansSerif",Font.BOLD,12));t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);}
    private void updatePrice(){String name=(String)donut.getSelectedItem();int unit=MENU.getOrDefault(name,199),qty=(Integer)quantity.getValue();priceLabel.setText(String.format("$%.2f each  |  Total: $%.2f",unit/100.0,unit*qty/100.0));}
    private void safe(String action,Runnable task){try{task.run();}catch(Exception ex){JOptionPane.showMessageDialog(this,action+" failed: "+ex.getMessage(),"Oak Donuts",JOptionPane.ERROR_MESSAGE);}}
    private int intValue(JTextField f,String label){try{return Integer.parseInt(f.getText().trim());}catch(Exception e){throw new IllegalArgumentException(label+" must be a whole number.");}}
    private String required(JTextField f,String label){String s=f.getText().trim();if(s.isEmpty())throw new IllegalArgumentException(label+" is required.");return s;}
    private void insertCustomer(){Customer c=customerFromFields();if(customers.get(c.getID()).isPresent())throw new IllegalArgumentException("That customer ID is already in use.");customers.insert(c);refreshCustomers();clearCustomer();}
    private void updateCustomer(){Customer c=customerFromFields();if(customers.get(c.getID()).isEmpty())throw new IllegalArgumentException("Customer ID was not found.");customers.update(c);refreshCustomers();}
    private void deleteCustomer(){int id=intValue(cId,"Customer ID");Customer c=customers.get(id).orElseThrow(()->new IllegalArgumentException("Customer was not found."));if(JOptionPane.showConfirmDialog(this,"Delete this customer? Orders linked to this customer must be deleted first.","Confirm",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){customers.delete(c);refreshCustomers();clearCustomer();}}
    private Customer customerFromFields(){return new Customer(intValue(cId,"Customer ID"),required(cFirst,"First name"),required(cLast,"Last name"),(String)cFavorite.getSelectedItem());}
    private void insertOrder(){Order o=orderFromFields();if(orders.get(o.getID()).isPresent())throw new IllegalArgumentException("That order ID is already in use.");orders.insert(o);refreshOrders();clearOrder();}
    private void updateOrder(){Order o=orderFromFields();if(orders.get(o.getID()).isEmpty())throw new IllegalArgumentException("Order ID was not found.");orders.update(o);refreshOrders();}
    private void deleteOrder(){int id=intValue(oId,"Order ID");Order o=orders.get(id).orElseThrow(()->new IllegalArgumentException("Order was not found."));if(JOptionPane.showConfirmDialog(this,"Delete this order?","Confirm",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){orders.delete(o);refreshOrders();clearOrder();}}
    private Order orderFromFields(){int id=intValue(oId,"Order ID"),cid=intValue(oCustomer,"Customer ID"),qty=(Integer)quantity.getValue();if(customers.get(cid).isEmpty())throw new IllegalArgumentException("Add the customer before placing the order.");String dt;try{dt=LocalDateTime.parse(required(oDate,"Order date/time"),FMT).format(FMT);}catch(Exception ex){throw new IllegalArgumentException("Use date/time format yyyy-MM-dd HH:mm:ss.");}String item=(String)donut.getSelectedItem();return new Order(id,MENU.get(item)*qty,dt,item,cid,qty);}
    private void refreshCustomers(){DefaultTableModel m=(DefaultTableModel)customerTable.getModel();m.setRowCount(0);for(Customer c:customers.getAll())m.addRow(new Object[]{c.getID(),c.getFirstName(),c.getLastName(),c.getFavoriteDonut()});}
    private void refreshOrders(){DefaultTableModel m=(DefaultTableModel)orderTable.getModel();m.setRowCount(0);for(Order o:orders.getAll()){String name=customers.get(o.getCustomerID()).map(c->c.getFirstName()+" "+c.getLastName()).orElse("Unknown");m.addRow(new Object[]{o.getID(),o.getCustomerID(),name,o.getItemName(),o.getQuantity(),String.format("$%.2f",o.getPrice()/100.0),o.getDateTime()});}}
    private void loadCustomer(){int r=customerTable.getSelectedRow();cId.setText(customerTable.getValueAt(r,0).toString());cFirst.setText(customerTable.getValueAt(r,1).toString());cLast.setText(customerTable.getValueAt(r,2).toString());cFavorite.setSelectedItem(customerTable.getValueAt(r,3).toString());}
    private void loadOrder(){int r=orderTable.getSelectedRow();oId.setText(orderTable.getValueAt(r,0).toString());oCustomer.setText(orderTable.getValueAt(r,1).toString());donut.setSelectedItem(orderTable.getValueAt(r,3).toString());quantity.setValue(Integer.parseInt(orderTable.getValueAt(r,4).toString()));oDate.setText(orderTable.getValueAt(r,6).toString());updatePrice();}
    private void clearCustomer(){cId.setText("");cFirst.setText("");cLast.setText("");cFavorite.setSelectedIndex(0);customerTable.clearSelection();}
    private void clearOrder(){oId.setText("");oCustomer.setText("");oDate.setText(LocalDateTime.now().format(FMT));donut.setSelectedIndex(0);quantity.setValue(1);orderTable.clearSelection();updatePrice();}
    public static void main(String[] args){SwingUtilities.invokeLater(()->{try{DB.getInstance();new Main().setVisible(true);}catch(Exception e){e.printStackTrace();JOptionPane.showMessageDialog(null,"Oak Donuts could not start: "+e.getMessage(),"Startup Error",JOptionPane.ERROR_MESSAGE);}});}
}
