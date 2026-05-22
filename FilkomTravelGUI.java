import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

public class FilkomTravelGUI extends JFrame {
    
    // Core Application State
    private List<Customer> customers;
    private List<Vehicle> vehicles;
    private List<Promotion> promotions;
    private List<Order> allOrders;
    
    private Customer currentCustomer = null; // Logged-in Customer
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    
    // Color Palette (CyberSlate Dark Mode)
    private static final Color BG_DARK = new Color(17, 24, 39);      // Slate Black
    private static final Color BG_CARD = new Color(30, 41, 59);      // Slate Gray
    private static final Color COLOR_PRIMARY = new Color(99, 102, 241); // Indigo Violet
    private static final Color COLOR_SECONDARY = new Color(6, 182, 212); // Cyan Blue
    private static final Color COLOR_TEXT_PRIMARY = new Color(243, 244, 246);
    private static final Color COLOR_TEXT_SECONDARY = new Color(156, 163, 175);
    private static final Color COLOR_BORDER = new Color(55, 65, 81);
    private static final Color COLOR_SUCCESS = new Color(16, 185, 129);  // Emerald Green
    private static final Color COLOR_DANGER = new Color(239, 68, 68);    // Crimson Red
    
    // Card Controllers
    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    
    // Frontend Header Profile Badges
    private JLabel lblUserBadge;
    private JLabel lblUserBalance;
    private JLabel lblUserPromo;
    
    // Auth & Registration Controls
    private JTextField txtRegId;
    private JTextField txtRegName;
    private JTextField txtRegDate;
    private JTextField txtRegBalance;
    private JRadioButton rbMember;
    private JRadioButton rbGuest;
    private JCheckBox chkTerms;
    
    // Front-End Browsing, Cart, and History Controls
    private JTable tblFrontendVehicles;
    private JTable tblCart;
    private JTable tblHistory;
    private DefaultTableModel modelFrontendVehicles;
    private DefaultTableModel modelCart;
    private DefaultTableModel modelHistory;
    
    private JSpinner spnRentalDays;
    private JTextField txtStartDate;
    private JTextField txtPromoInput;
    private JTextField txtTopUp;
    private JList<String> lstAvailablePromos;
    private DefaultListModel<String> modelAvailablePromos;

    // Back-End Admin Controls
    private JTable tblAdminCustomers;
    private JTable tblAdminOrders;
    private DefaultTableModel modelAdminCustomers;
    private DefaultTableModel modelAdminOrders;

    public FilkomTravelGUI() {
        setTitle("FilkomTravel - Premium Swing Vehicle Rental Dashboard");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        loadAllData();
        
        if (vehicles.isEmpty() && promotions.isEmpty() && customers.isEmpty()) {
            seedSampleData();
        }
        
        UIManager.put("Label.foreground", COLOR_TEXT_PRIMARY);
        UIManager.put("Panel.background", BG_DARK);
        
        setJMenuBar(createMenuBar());
        
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(BG_DARK);
        
        // Assemble visual hierarchy cards
        mainContentPanel.add(createWelcomePanel(), "WELCOME");
        mainContentPanel.add(createFrontendPanel(), "FRONTEND");
        mainContentPanel.add(createBackendPanel(), "BACKEND");
        
        add(mainContentPanel);
        cardLayout.show(mainContentPanel, "WELCOME");
    }
    
    private void loadAllData() {
        vehicles = FileStorage.loadVehicles("vehicles.txt");
        promotions = FileStorage.loadPromotions("promos.txt");
        customers = FileStorage.loadCustomers("customers.txt", promotions);
        allOrders = FileStorage.loadOrders("orders.txt", customers, vehicles, promotions);
    }
    
    private void saveAllData() {
        FileStorage.saveVehicles("vehicles.txt", vehicles);
        FileStorage.savePromotions("promos.txt", promotions);
        FileStorage.saveCustomers("customers.txt", customers);
        FileStorage.saveOrders("orders.txt", allOrders);
    }
    
    private void seedSampleData() {
        LocalDate now = LocalDate.now();
        // Seed Vehicles
        vehicles.add(new Motorcycle("V001", "Honda Beat", "N 4321 XY", 80000));
        vehicles.add(new Motorcycle("V002", "Yamaha NMax", "DK 7777 Z", 150000));
        vehicles.add(new Car("V003", "Toyota Innova Zenix", "N 1234 AB", 450000, "MPV"));
        vehicles.add(new Car("V004", "Honda Civic RS", "B 9999 CC", 650000, "Sedan"));
        
        // Seed Promotions
        promotions.add(new PercentOffPromo("LIBURAN20", now.minusDays(5), now.plusDays(25), 0.20, 100000, 200000));
        promotions.add(new CashbackPromo("CASHBACK10", now.minusDays(2), now.plusDays(30), 0.10, 50000, 150000));
        
        // Seed Customers
        customers.add(new Member("M001", "Budi Santoso", now.minusDays(45), 1500000));
        customers.add(new Guest("G001", 500000));
        
        saveAllData();
    }
    
    private static String formatNumber(double amount) {
        java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        java.text.DecimalFormat formatter = new java.text.DecimalFormat("###,###.##", symbols);
        return formatter.format(amount);
    }

    // ==========================================
    // NAVIGATION MENU BAR
    // ==========================================
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(BG_CARD);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));
        
        JMenu menuFile = new JMenu("File");
        menuFile.setForeground(COLOR_TEXT_PRIMARY);
        
        JMenuItem itemSave = new JMenuItem("Save Current Data");
        itemSave.addActionListener(e -> {
            saveAllData();
            JOptionPane.showMessageDialog(this, "Data successfully serialized to text files!", "System Info", JOptionPane.INFORMATION_MESSAGE);
        });
        
        JMenuItem itemExit = new JMenuItem("Exit");
        itemExit.addActionListener(e -> {
            saveAllData();
            System.exit(0);
        });
        
        menuFile.add(itemSave);
        menuFile.addSeparator();
        menuFile.add(itemExit);
        
        JMenu menuNav = new JMenu("Navigation");
        menuNav.setForeground(COLOR_TEXT_PRIMARY);
        
        JMenuItem itemShowWelcome = new JMenuItem("Show Welcome Screen");
        itemShowWelcome.addActionListener(e -> cardLayout.show(mainContentPanel, "WELCOME"));
        
        menuNav.add(itemShowWelcome);
        menuBar.add(menuFile);
        menuBar.add(menuNav);
        
        return menuBar;
    }
    
    // ==========================================
    // WELCOME PANEL
    // ==========================================
    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_DARK);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(10, 20, 30, 20);
        gbc.anchor = GridBagConstraints.CENTER;
        
        JLabel lblTitle = new JLabel("FILKOMTRAVEL");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 46));
        lblTitle.setForeground(COLOR_PRIMARY);
        panel.add(lblTitle, gbc);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 20, 45, 20);
        JLabel lblSubtitle = new JLabel("Graphical Swing-Based Vehicle Rental Application");
        lblSubtitle.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        lblSubtitle.setForeground(COLOR_TEXT_SECONDARY);
        panel.add(lblSubtitle, gbc);
        
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        JPanel buttonRow = new JPanel(new GridLayout(1, 2, 35, 0));
        buttonRow.setOpaque(false);
        
        JButton btnFrontend = createStyledWelcomeButton("FRONT-END PORTAL\n(Customer Panel)", COLOR_PRIMARY);
        btnFrontend.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "FRONTEND");
            refreshAllUI();
        });
        
        JButton btnBackend = createStyledWelcomeButton("BACK-END SYSTEM\n(Administrator Console)", COLOR_SECONDARY);
        btnBackend.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "BACKEND");
            refreshAllUI();
        });
        
        buttonRow.add(btnFrontend);
        buttonRow.add(btnBackend);
        panel.add(buttonRow, gbc);
        
        return panel;
    }

    // ==========================================
    // CUSTOMER FRONT-END DASHBOARD
    // ==========================================
    private JPanel createFrontendPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_DARK);
        
        // 1. Header Navigation Bar
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_CARD);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitle = new JLabel("Customer Portal Workspace");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(COLOR_TEXT_PRIMARY);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        
        JButton btnBack = createStyledButton("Switch Role", COLOR_DANGER);
        btnBack.addActionListener(e -> cardLayout.show(mainContentPanel, "WELCOME"));
        headerPanel.add(btnBack, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        JPanel workspace = new JPanel(new BorderLayout(15, 0));
        workspace.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        workspace.setOpaque(false);
        
        JPanel sidebar = new JPanel(new BorderLayout(0, 15));
        sidebar.setPreferredSize(new Dimension(320, 0));
        sidebar.setOpaque(false);
        
        JPanel authCard = new JPanel(new CardLayout());
        authCard.setBackground(BG_CARD);
        authCard.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        
        JPanel loggedOutPanel = new JPanel(new GridBagLayout());
        loggedOutPanel.setBackground(BG_CARD);
        loggedOutPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lblAuthHeader = new JLabel("Customer Log-In / Sign-Up");
        lblAuthHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblAuthHeader.setForeground(COLOR_PRIMARY);
        loggedOutPanel.add(lblAuthHeader, gbc);
        
        gbc.gridy = 1; gbc.gridwidth = 1;
        loggedOutPanel.add(new JLabel("Customer ID:"), gbc);
        gbc.gridx = 1;
        txtRegId = createStyledTextField("M003");
        loggedOutPanel.add(txtRegId, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        loggedOutPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        txtRegName = createStyledTextField("Ahmad Dani");
        loggedOutPanel.add(txtRegName, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        loggedOutPanel.add(new JLabel("Reg Date:"), gbc);
        gbc.gridx = 1;
        txtRegDate = createStyledTextField(LocalDate.now().format(DATE_FORMATTER));
        loggedOutPanel.add(txtRegDate, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        loggedOutPanel.add(new JLabel("Init Balance:"), gbc);
        gbc.gridx = 1;
        txtRegBalance = createStyledTextField("500000");
        loggedOutPanel.add(txtRegBalance, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        radioPanel.setOpaque(false);
        rbMember = new JRadioButton("Member", true);
        rbGuest = new JRadioButton("Guest");
        rbMember.setForeground(COLOR_TEXT_PRIMARY);
        rbGuest.setForeground(COLOR_TEXT_PRIMARY);
        ButtonGroup grpCustType = new ButtonGroup();
        grpCustType.add(rbMember);
        grpCustType.add(rbGuest);
        radioPanel.add(rbMember);
        radioPanel.add(rbGuest);
        
        rbGuest.addActionListener(e -> {
            txtRegName.setEnabled(false);
            txtRegDate.setEnabled(false);
        });
        rbMember.addActionListener(e -> {
            txtRegName.setEnabled(true);
            txtRegDate.setEnabled(true);
        });
        loggedOutPanel.add(radioPanel, gbc);
        
        gbc.gridy = 6;
        chkTerms = new JCheckBox("Agree to Terms & Conditions");
        chkTerms.setForeground(COLOR_TEXT_SECONDARY);
        chkTerms.setOpaque(false);
        loggedOutPanel.add(chkTerms, gbc);
        
        gbc.gridy = 7; gbc.gridwidth = 1;
        JButton btnLogin = createStyledButton("LOGIN", COLOR_SECONDARY);
        loggedOutPanel.add(btnLogin, gbc);
        
        gbc.gridx = 1;
        JButton btnRegister = createStyledButton("REGISTER", COLOR_PRIMARY);
        loggedOutPanel.add(btnRegister, gbc);
        
        authCard.add(loggedOutPanel, "LOGGED_OUT");
        
        // 2b. Logged In Panel (Profile Badge Card)
        JPanel loggedInPanel = new JPanel(new GridBagLayout());
        loggedInPanel.setBackground(BG_CARD);
        loggedInPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        GridBagConstraints gbcIn = new GridBagConstraints();
        gbcIn.fill = GridBagConstraints.HORIZONTAL;
        gbcIn.insets = new Insets(6, 4, 6, 4);
        gbcIn.weightx = 1.0;
        
        gbcIn.gridx = 0; gbcIn.gridy = 0; gbcIn.gridwidth = 2;
        lblUserBadge = new JLabel("Welcome Customer");
        lblUserBadge.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblUserBadge.setForeground(COLOR_PRIMARY);
        loggedInPanel.add(lblUserBadge, gbcIn);
        
        gbcIn.gridy = 1; gbcIn.gridwidth = 1;
        loggedInPanel.add(new JLabel("Available Wallet:"), gbcIn);
        gbcIn.gridx = 1;
        lblUserBalance = new JLabel("Rp 0,00");
        lblUserBalance.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUserBalance.setForeground(COLOR_SUCCESS);
        loggedInPanel.add(lblUserBalance, gbcIn);
        
        gbcIn.gridx = 0; gbcIn.gridy = 2;
        loggedInPanel.add(new JLabel("Promo applied:"), gbcIn);
        gbcIn.gridx = 1;
        lblUserPromo = new JLabel("None");
        lblUserPromo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUserPromo.setForeground(COLOR_SECONDARY);
        loggedInPanel.add(lblUserPromo, gbcIn);
        
        gbcIn.gridx = 0; gbcIn.gridy = 3; gbcIn.gridwidth = 2;
        JSeparator sep = new JSeparator();
        sep.setBackground(COLOR_BORDER);
        loggedInPanel.add(sep, gbcIn);
        
        gbcIn.gridy = 4; gbcIn.gridwidth = 1;
        loggedInPanel.add(new JLabel("Topup Balance:"), gbcIn);
        gbcIn.gridx = 1;
        txtTopUp = createStyledTextField("250000");
        loggedInPanel.add(txtTopUp, gbcIn);
        
        gbcIn.gridx = 0; gbcIn.gridy = 5; gbcIn.gridwidth = 2;
        JButton btnTopUp = createStyledButton("TOP UP WALLET", COLOR_PRIMARY);
        loggedInPanel.add(btnTopUp, gbcIn);
        
        gbcIn.gridy = 6;
        JButton btnLogout = createStyledButton("LOG OUT CUSTOMER", COLOR_DANGER);
        loggedInPanel.add(btnLogout, gbcIn);
        
        authCard.add(loggedInPanel, "LOGGED_IN");
        
        sidebar.add(authCard, BorderLayout.NORTH);
        
        // Active Promotions JList
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBackground(BG_CARD);
        listPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_BORDER), 
                "Active Promo Codes List", 
                TitledBorder.LEFT, TitledBorder.TOP, 
                new Font("Segoe UI", Font.BOLD, 12), COLOR_TEXT_PRIMARY));
        
        modelAvailablePromos = new DefaultListModel<>();
        lstAvailablePromos = new JList<>(modelAvailablePromos);
        lstAvailablePromos.setBackground(BG_DARK);
        lstAvailablePromos.setForeground(COLOR_TEXT_PRIMARY);
        lstAvailablePromos.setSelectionBackground(COLOR_PRIMARY);
        lstAvailablePromos.setSelectionForeground(Color.WHITE);
        lstAvailablePromos.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        JScrollPane scrollPromos = new JScrollPane(lstAvailablePromos);
        scrollPromos.setBorder(BorderFactory.createEmptyBorder());
        listPanel.add(scrollPromos, BorderLayout.CENTER);
        
        sidebar.add(listPanel, BorderLayout.CENTER);
        workspace.add(sidebar, BorderLayout.WEST);
        
        // Tabbed Panel
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(BG_CARD);
        tabbedPane.setForeground(COLOR_TEXT_PRIMARY);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        tabbedPane.addTab("1. Browse & Rent Vehicles", createFrontendRentPanel());
        tabbedPane.addTab("2. Review Cart & Checkout", createFrontendCartPanel());
        tabbedPane.addTab("3. My Order History", createFrontendHistoryPanel());
        
        workspace.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(workspace, BorderLayout.CENTER);
        
        // Wire Auth triggers
        CardLayout authLayout = (CardLayout) authCard.getLayout();
        
        btnLogin.addActionListener(e -> {
            String id = txtRegId.getText().trim();
            Customer found = findCustomer(id);
            if (found != null) {
                currentCustomer = found;
                authLayout.show(authCard, "LOGGED_IN");
                refreshAllUI();
                JOptionPane.showMessageDialog(this, "Logged in as " + found.getName() + "!", "Login", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Customer ID '" + id + "' not found! Please register.", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnRegister.addActionListener(e -> {
            if (!chkTerms.isSelected()) {
                JOptionPane.showMessageDialog(this, "Please agree to the terms to proceed!", "Terms Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String id = txtRegId.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Customer ID is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (findCustomer(id) != null) {
                JOptionPane.showMessageDialog(this, "Customer ID already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                double initialBalance = Double.parseDouble(txtRegBalance.getText().trim());
                if (rbGuest.isSelected()) {
                    Guest g = new Guest(id, initialBalance);
                    customers.add(g);
                    currentCustomer = g;
                } else {
                    String name = txtRegName.getText().trim();
                    if (name.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Member Name is required!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    LocalDate regDate = LocalDate.parse(txtRegDate.getText().trim(), DATE_FORMATTER);
                    Member m = new Member(id, name, regDate, initialBalance);
                    customers.add(m);
                    currentCustomer = m;
                }
                
                authLayout.show(authCard, "LOGGED_IN");
                saveAllData();
                refreshAllUI();
                JOptionPane.showMessageDialog(this, "Registration Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Please enter a valid balance!", "Number Format Error", JOptionPane.ERROR_MESSAGE);
            } catch (DateTimeParseException dtpe) {
                JOptionPane.showMessageDialog(this, "Reg Date must match yyyy/MM/dd!", "Date Format Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnLogout.addActionListener(e -> {
            currentCustomer = null;
            authLayout.show(authCard, "LOGGED_OUT");
            refreshAllUI();
        });
        
        btnTopUp.addActionListener(e -> {
            if (currentCustomer == null) return;
            try {
                double amt = Double.parseDouble(txtTopUp.getText().trim());
                if (amt <= 0) {
                    JOptionPane.showMessageDialog(this, "Amount must be positive!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                currentCustomer.addBalance(amt);
                JOptionPane.showMessageDialog(this, "Top-up completed!", "Topup", JOptionPane.INFORMATION_MESSAGE);
                
                saveAllData();
                refreshAllUI();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Enter a valid number for top up!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        return mainPanel;
    }
    
    // Sub-tab 1: Rent vehicles
    private JPanel createFrontendRentPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        modelFrontendVehicles = new DefaultTableModel(new String[]{"ID", "Type", "Vehicle Name", "Plate No", "Price / Day", "Details", "Availability"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblFrontendVehicles = new JTable(modelFrontendVehicles);
        styleTable(tblFrontendVehicles);
        
        panel.add(new JScrollPane(tblFrontendVehicles), BorderLayout.CENTER);
        
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(BG_CARD);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;
        
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Rental Duration (Days):"), gbc);
        
        gbc.gridx = 1;
        spnRentalDays = new JSpinner(new SpinnerNumberModel(1, 1, 365, 1));
        spnRentalDays.setPreferredSize(new Dimension(80, 26));
        form.add(spnRentalDays, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Start Date (yyyy/MM/dd):"), gbc);
        
        gbc.gridx = 1;
        txtStartDate = createStyledTextField(LocalDate.now().format(DATE_FORMATTER));
        form.add(txtStartDate, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0; gbc.gridheight = 2;
        JButton btnAddCart = createStyledButton("ADD TO CART", COLOR_PRIMARY);
        btnAddCart.setFont(new Font("Segoe UI", Font.BOLD, 14));
        form.add(btnAddCart, gbc);
        
        panel.add(form, BorderLayout.SOUTH);
        
        btnAddCart.addActionListener(e -> {
            if (currentCustomer == null) {
                JOptionPane.showMessageDialog(this, "Login first to rent!", "Auth Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int row = tblFrontendVehicles.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a vehicle above!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String id = (String) tblFrontendVehicles.getValueAt(row, 0);
            Vehicle vehicle = findVehicle(id);
            if (vehicle == null) return;
            
            if (!vehicle.isAvailable()) {
                JOptionPane.showMessageDialog(this, "Vehicle is currently rented out!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                int days = (Integer) spnRentalDays.getValue();
                LocalDate start = LocalDate.parse(txtStartDate.getText().trim(), DATE_FORMATTER);
                
                CartItem existingItem = currentCustomer.getCurrentCart().stream()
                        .filter(item -> item.getVehicle().getId().equals(id))
                        .findFirst().orElse(null);
                
                if (existingItem != null) {
                    existingItem.setQuantity(existingItem.getQuantity() + days);
                } else {
                    currentCustomer.getCurrentCart().add(new CartItem(vehicle, days, start));
                }
                
                JOptionPane.showMessageDialog(this, "Added to cart!", "Cart Info", JOptionPane.INFORMATION_MESSAGE);
                refreshAllUI();
                
            } catch (DateTimeParseException dtpe) {
                JOptionPane.showMessageDialog(this, "Use format yyyy/MM/dd!", "Date Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        return panel;
    }
    
    // Sub-tab 2: Review Cart
    private JPanel createFrontendCartPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        modelCart = new DefaultTableModel(new String[]{"No", "Vehicle Name", "Plate No", "Days", "Start Date", "End Date", "Subtotal"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblCart = new JTable(modelCart);
        styleTable(tblCart);
        
        panel.add(new JScrollPane(tblCart), BorderLayout.CENTER);
        
        JPanel bottomArea = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomArea.setOpaque(false);
        
        JPanel promoPanel = new JPanel(new GridBagLayout());
        promoPanel.setBackground(BG_CARD);
        promoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        
        GridBagConstraints gbcP = new GridBagConstraints();
        gbcP.fill = GridBagConstraints.HORIZONTAL;
        gbcP.insets = new Insets(6, 6, 6, 6);
        gbcP.weightx = 1.0;
        
        gbcP.gridx = 0; gbcP.gridy = 0; gbcP.gridwidth = 2;
        JLabel lblPromoTitle = new JLabel("Apply Coupon code");
        lblPromoTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblPromoTitle.setForeground(COLOR_SECONDARY);
        promoPanel.add(lblPromoTitle, gbcP);
        
        gbcP.gridy = 1; gbcP.gridwidth = 1;
        promoPanel.add(new JLabel("Promo Code:"), gbcP);
        gbcP.gridx = 1;
        txtPromoInput = createStyledTextField("LIBURAN20");
        promoPanel.add(txtPromoInput, gbcP);
        
        gbcP.gridx = 0; gbcP.gridy = 2; gbcP.gridwidth = 2;
        JButton btnApplyPromo = createStyledButton("APPLY PROMO", COLOR_SECONDARY);
        promoPanel.add(btnApplyPromo, gbcP);
        
        gbcP.gridy = 3;
        JButton btnRemoveCart = createStyledButton("REMOVE SELECTED", COLOR_DANGER);
        promoPanel.add(btnRemoveCart, gbcP);
        
        bottomArea.add(promoPanel);
        
        JPanel calcPanel = new JPanel(new GridBagLayout());
        calcPanel.setBackground(BG_CARD);
        calcPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        
        GridBagConstraints gbcC = new GridBagConstraints();
        gbcC.fill = GridBagConstraints.HORIZONTAL;
        gbcC.insets = new Insets(4, 8, 4, 8);
        
        gbcC.gridx = 0; gbcC.gridy = 0; gbcC.weightx = 0.5;
        calcPanel.add(new JLabel("Sub Total:"), gbcC);
        gbcC.gridx = 1; gbcC.weightx = 0.5;
        JLabel lblSubtotalVal = new JLabel("Rp 0,00");
        lblSubtotalVal.setHorizontalAlignment(SwingConstants.RIGHT);
        calcPanel.add(lblSubtotalVal, gbcC);
        
        gbcC.gridx = 0; gbcC.gridy = 1;
        calcPanel.add(new JLabel("DiscountApplied:"), gbcC);
        gbcC.gridx = 1;
        JLabel lblDiscountVal = new JLabel("-Rp 0,00");
        lblDiscountVal.setForeground(COLOR_DANGER);
        lblDiscountVal.setHorizontalAlignment(SwingConstants.RIGHT);
        calcPanel.add(lblDiscountVal, gbcC);
        
        gbcC.gridx = 0; gbcC.gridy = 2;
        calcPanel.add(new JLabel("Final Total:"), gbcC);
        gbcC.gridx = 1;
        JLabel lblTotalVal = new JLabel("Rp 0,00");
        lblTotalVal.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTotalVal.setForeground(COLOR_SUCCESS);
        lblTotalVal.setHorizontalAlignment(SwingConstants.RIGHT);
        calcPanel.add(lblTotalVal, gbcC);
        
        gbcC.gridx = 0; gbcC.gridy = 3;
        calcPanel.add(new JLabel("Cashback Earned:"), gbcC);
        gbcC.gridx = 1;
        JLabel lblCashbackVal = new JLabel("Rp 0,00");
        lblCashbackVal.setForeground(COLOR_SECONDARY);
        lblCashbackVal.setHorizontalAlignment(SwingConstants.RIGHT);
        calcPanel.add(lblCashbackVal, gbcC);
        
        gbcC.gridx = 0; gbcC.gridy = 4; gbcC.gridwidth = 2;
        gbcC.insets = new Insets(10, 8, 0, 8);
        JButton btnCheckout = createStyledButton("CHECK OUT & PAY", COLOR_PRIMARY);
        btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 15));
        calcPanel.add(btnCheckout, gbcC);
        
        bottomArea.add(calcPanel);
        panel.add(bottomArea, BorderLayout.SOUTH);
        
        ActionListener calcRefresher = e -> {
            if (currentCustomer == null) return;
            double sub = currentCustomer.getCurrentCart().stream().mapToDouble(CartItem::getSubtotal).sum();
            Promotion promo = currentCustomer.getAppliedPromo();
            
            double disc = 0;
            if (promo instanceof PercentOffPromo) {
                disc = Math.min(sub * promo.getPercentageDiscount(), promo.getMaxDiscount());
            }
            
            double cash = 0;
            if (promo instanceof CashbackPromo) {
                cash = Math.min(sub * promo.getPercentageDiscount(), promo.getMaxDiscount());
            }
            
            double total = sub - disc;
            
            lblSubtotalVal.setText("Rp " + formatNumber(sub));
            lblDiscountVal.setText("-Rp " + formatNumber(disc));
            lblTotalVal.setText("Rp " + formatNumber(total));
            lblCashbackVal.setText("Rp " + formatNumber(cash));
        };
        
        btnApplyPromo.addActionListener(e -> {
            if (currentCustomer == null) return;
            String code = txtPromoInput.getText().trim();
            try {
                applyPromoCode(currentCustomer, code);
                JOptionPane.showMessageDialog(this, "Promo applied successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                saveAllData();
                refreshAllUI();
                calcRefresher.actionPerformed(null);
            } catch (InvalidPromoException ipe) {
                JOptionPane.showMessageDialog(this, ipe.getMessage(), "Promo Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnRemoveCart.addActionListener(e -> {
            if (currentCustomer == null) return;
            int sel = tblCart.getSelectedRow();
            if (sel == -1) return;
            
            currentCustomer.getCurrentCart().remove(sel);
            if (currentCustomer.getCurrentCart().isEmpty()) {
                currentCustomer.setAppliedPromo(null);
            }
            saveAllData();
            refreshAllUI();
            calcRefresher.actionPerformed(null);
        });
        
        btnCheckout.addActionListener(e -> {
            if (currentCustomer == null) return;
            try {
                checkoutCustomer(currentCustomer);
                JOptionPane.showMessageDialog(this, "Checkout complete!", "Payment Complete", JOptionPane.INFORMATION_MESSAGE);
                saveAllData();
                refreshAllUI();
                calcRefresher.actionPerformed(null);
            } catch (InsufficientBalanceException ibe) {
                JOptionPane.showMessageDialog(this, ibe.getMessage(), "Balance Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        this.addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
                calcRefresher.actionPerformed(null);
            }
        });
        
        return panel;
    }
    
    // Sub-tab 3: History
    private JPanel createFrontendHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        modelHistory = new DefaultTableModel(new String[]{"No", "Order Number", "Date", "Motors", "Cars", "Promo", "Total Paid"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblHistory = new JTable(modelHistory);
        styleTable(tblHistory);
        
        panel.add(new JScrollPane(tblHistory), BorderLayout.CENTER);
        
        JPanel detailCard = new JPanel(new BorderLayout());
        detailCard.setBackground(BG_CARD);
        detailCard.setPreferredSize(new Dimension(0, 180));
        detailCard.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_BORDER), 
                "Order Invoice Receipt Details", 
                TitledBorder.LEFT, TitledBorder.TOP, 
                new Font("Segoe UI", Font.BOLD, 12), COLOR_TEXT_PRIMARY));
        
        JTextArea txtReceipt = new JTextArea();
        txtReceipt.setEditable(false);
        txtReceipt.setBackground(new Color(15, 23, 42)); // dark text area
        txtReceipt.setForeground(COLOR_SUCCESS);
        txtReceipt.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtReceipt.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        
        detailCard.add(new JScrollPane(txtReceipt), BorderLayout.CENTER);
        panel.add(detailCard, BorderLayout.SOUTH);
        
        tblHistory.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || currentCustomer == null) return;
            int sel = tblHistory.getSelectedRow();
            if (sel == -1) {
                txtReceipt.setText("Select an order...");
                return;
            }
            
            Order order = currentCustomer.getOrderHistory().get(sel);
            StringBuilder sb = new StringBuilder();
            sb.append("==================================================\n");
            sb.append(String.format("CUSTOMER ID   : %s\n", order.getCustomer().getId()));
            sb.append(String.format("NAME          : %s\n", order.getCustomer().getName()));
            sb.append(String.format("ORDER NO      : %d\n", order.getOrderNumber()));
            sb.append(String.format("ORDER DATE    : %s\n", order.getOrderDate().format(DATE_FORMATTER)));
            sb.append("==================================================\n");
            sb.append(String.format("%-25s | %4s | %13s\n", "Vehicle (Plate)", "Days", "Subtotal"));
            sb.append("--------------------------------------------------\n");
            
            for (CartItem item : order.getItems()) {
                String d = item.getVehicle().getName() + " (" + item.getVehicle().getPlateNumber() + ")";
                if (d.length() > 25) d = d.substring(0, 22) + "...";
                sb.append(String.format("%-25s | %4d | Rp %10s\n", 
                        d, 
                        item.getQuantity(), 
                        formatNumber(item.getSubtotal())));
            }
            sb.append("--------------------------------------------------\n");
            double sub = order.getSubtotalPrice();
            sb.append(String.format("%-32s: Rp %13s\n", "Sub Total", formatNumber(sub)));
            
            Promotion promo = order.getPromo();
            if (promo != null) {
                if (promo instanceof PercentOffPromo) {
                    double pctOff = Math.min(sub * promo.getPercentageDiscount(), promo.getMaxDiscount());
                    sb.append(String.format("PROMO: %-25s: -Rp %12s\n", promo.getPromoCode(), formatNumber(pctOff)));
                } else if (promo instanceof CashbackPromo) {
                    double cashback = Math.min(sub * promo.getPercentageDiscount(), promo.getMaxDiscount());
                    sb.append(String.format("PROMO: %-25s: Rp %13s (CASHBACK)\n", promo.getPromoCode(), formatNumber(cashback)));
                }
            }
            sb.append("==================================================\n");
            sb.append(String.format("%-32s: Rp %13s\n", "TOTAL PRICE PAID", formatNumber(order.getTotalPrice())));
            sb.append("==================================================\n");
            
            txtReceipt.setText(sb.toString());
        });
        
        return panel;
    }
    
    // Core logic validation methods
    private void checkoutCustomer(Customer customer) throws InsufficientBalanceException {
        List<CartItem> cart = customer.getCurrentCart();
        if (cart.isEmpty()) throw new InsufficientBalanceException("Cart is empty!");
        
        Promotion promo = customer.getAppliedPromo();
        double subtotal = cart.stream().mapToDouble(CartItem::getSubtotal).sum();
        double totalPrice = subtotal;
        
        if (promo instanceof PercentOffPromo) {
            totalPrice = subtotal - Math.min(subtotal * promo.getPercentageDiscount(), promo.getMaxDiscount());
        }
        
        if (customer.getBalance() < totalPrice) {
            throw new InsufficientBalanceException("Insufficient Balance!\n" +
                    "Required: Rp " + formatNumber(totalPrice) + "\n" +
                    "Wallet  : Rp " + formatNumber(customer.getBalance()));
        }
        
        customer.deductBalance(totalPrice);
        
        if (promo instanceof CashbackPromo) {
            double cashback = Math.min(subtotal * promo.getPercentageDiscount(), promo.getMaxDiscount());
            customer.addBalance(cashback);
        }
        
        int orderNo = customer.getOrderHistory().size() + 1;
        List<CartItem> itemsCopy = new ArrayList<>(cart);
        Order order = new Order(orderNo, customer, itemsCopy, LocalDate.now(), promo);
        
        customer.getOrderHistory().add(order);
        allOrders.add(order);
        
        for (CartItem item : itemsCopy) {
            item.getVehicle().setAvailable(false);
        }
        
        customer.getCurrentCart().clear();
        customer.setAppliedPromo(null);
    }
    
    private void applyPromoCode(Customer customer, String promoCode) throws InvalidPromoException {
        Promotion promo = promotions.stream()
                .filter(p -> p.getPromoCode().equalsIgnoreCase(promoCode))
                .findFirst().orElse(null);
                
        if (promo == null) throw new InvalidPromoException("Promo code '" + promoCode + "' is invalid!");
        if (promo.isExpired(LocalDate.now())) throw new InvalidPromoException("Promo is expired!");
        if (!(customer instanceof Member)) throw new InvalidPromoException("Guests cannot apply promo codes!");
        
        Member member = (Member) customer;
        if (member.getMembershipDays() <= 30) {
            throw new InvalidPromoException("Restricted to loyalty members with >30 days of membership.\n" +
                    "Current: " + member.getMembershipDays() + " days.");
        }
        
        double cartSubtotal = customer.getCurrentCart().stream().mapToDouble(CartItem::getSubtotal).sum();
        if (cartSubtotal < promo.getMinPurchase()) {
            throw new InvalidPromoException("Min Purchase not met!\n" +
                    "Requires: Rp " + formatNumber(promo.getMinPurchase()));
        }
        
        customer.setAppliedPromo(promo);
    }
    
    // ==========================================
    // UI REFRESHERS
    // ==========================================
    private void refreshAllUI() {
        refreshFrontendVehicles();
        refreshCartTable();
        refreshHistoryTable();
        refreshPromoList();
        refreshAdminCustomers();
        refreshAdminOrders();
        
        if (currentCustomer != null) {
            String t = (currentCustomer instanceof Member) ? "MEMBER" : "GUEST";
            lblUserBadge.setText(String.format("Welcome, %s (%s)", currentCustomer.getName(), t));
            lblUserBalance.setText("Rp " + formatNumber(currentCustomer.getBalance()));
            Promotion promo = currentCustomer.getAppliedPromo();
            lblUserPromo.setText(promo != null ? promo.getPromoCode() : "None");
        } else {
            lblUserBadge.setText("Welcome Guest");
            lblUserBalance.setText("Rp 0,00");
            lblUserPromo.setText("None");
        }
    }
    
    private void refreshFrontendVehicles() {
        modelFrontendVehicles.setRowCount(0);
        for (Vehicle v : vehicles) {
            String details = (v instanceof Car) ? ((Car) v).getCustomType() : "Motorbike";
            modelFrontendVehicles.addRow(new Object[]{
                v.getId(),
                v.getVehicleType(),
                v.getName(),
                v.getPlateNumber(),
                "Rp " + formatNumber(v.getPricePerDay()),
                details,
                v.isAvailable() ? "AVAILABLE" : "RENTED / UNVALIABLE"
            });
        }
    }
    
    private void refreshCartTable() {
        modelCart.setRowCount(0);
        if (currentCustomer == null) return;
        
        int no = 1;
        for (CartItem item : currentCustomer.getCurrentCart()) {
            modelCart.addRow(new Object[]{
                no++,
                item.getVehicle().getName(),
                item.getVehicle().getPlateNumber(),
                item.getQuantity() + " Days",
                item.getStartDate().format(DATE_FORMATTER),
                item.getEndDate().format(DATE_FORMATTER),
                "Rp " + formatNumber(item.getSubtotal())
            });
        }
    }
    
    private void refreshHistoryTable() {
        modelHistory.setRowCount(0);
        if (currentCustomer == null) return;
        
        int no = 1;
        for (Order order : currentCustomer.getOrderHistory()) {
            long motors = order.getItems().stream().filter(item -> item.getVehicle() instanceof Motorcycle).count();
            long cars = order.getItems().stream().filter(item -> item.getVehicle() instanceof Car).count();
            String promoStr = order.getPromo() != null ? order.getPromo().getPromoCode() : "None";
            
            modelHistory.addRow(new Object[]{
                no++,
                order.getOrderNumber(),
                order.getOrderDate().format(DATE_FORMATTER),
                motors,
                cars,
                promoStr,
                "Rp " + formatNumber(order.getTotalPrice())
            });
        }
    }
    
    private void refreshPromoList() {
        modelAvailablePromos.clear();
        for (Promotion p : promotions) {
            int pct = (int)(p.getPercentageDiscount() * 100);
            modelAvailablePromos.addElement(String.format("[%s] Code: %s (Get %d%% Off)", p.getPromoType(), p.getPromoCode(), pct));
        }
    }

    // ==========================================
    // ADMIN BACK-END PORTAL
    // ==========================================
    private JPanel createBackendPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_DARK);
        
        // 1. Header Navigation Bar
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_CARD);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitle = new JLabel("Administrator Management Console");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(COLOR_TEXT_PRIMARY);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        
        JButton btnBack = createStyledButton("Switch Role", COLOR_DANGER);
        btnBack.addActionListener(e -> cardLayout.show(mainContentPanel, "WELCOME"));
        headerPanel.add(btnBack, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // 2. Tabbed Panel
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(BG_CARD);
        tabbedPane.setForeground(COLOR_TEXT_PRIMARY);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        tabbedPane.addTab("1. View Customers Base", createAdminCustomersPanel());
        tabbedPane.addTab("2. View System Orders", createAdminOrdersPanel());
        tabbedPane.addTab("3. Add New Vehicle", createAdminAddVehiclePanel());
        tabbedPane.addTab("4. Setup New Promotion", createAdminAddPromoPanel());
        
        JPanel workspace = new JPanel(new BorderLayout());
        workspace.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        workspace.setOpaque(false);
        workspace.add(tabbedPane, BorderLayout.CENTER);
        
        mainPanel.add(workspace, BorderLayout.CENTER);
        
        return mainPanel;
    }

    private JPanel createAdminCustomersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        modelAdminCustomers = new DefaultTableModel(new String[]{"Customer ID", "Full Name", "Type", "Registration Date", "Wallet Balance", "Applied Promo"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblAdminCustomers = new JTable(modelAdminCustomers);
        styleTable(tblAdminCustomers);
        
        panel.add(new JScrollPane(tblAdminCustomers), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAdminOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        modelAdminOrders = new DefaultTableModel(new String[]{"Order #", "Customer ID", "Customer Name", "Order Date", "Motorcycles", "Cars", "Applied Promo", "Total Paid"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblAdminOrders = new JTable(modelAdminOrders);
        styleTable(tblAdminOrders);
        
        panel.add(new JScrollPane(tblAdminOrders), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAdminAddVehiclePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.weightx = 1.0;
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lblHeader = new JLabel("Register New Fleet Vehicle");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeader.setForeground(COLOR_SECONDARY);
        card.add(lblHeader, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        card.add(new JLabel("Vehicle ID:"), gbc);
        gbc.gridx = 1;
        JTextField txtVehId = createStyledTextField("V005");
        card.add(txtVehId, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        card.add(new JLabel("Name / Model:"), gbc);
        gbc.gridx = 1;
        JTextField txtVehName = createStyledTextField("Toyota Avanza");
        card.add(txtVehName, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        card.add(new JLabel("Plate Number:"), gbc);
        gbc.gridx = 1;
        JTextField txtVehPlate = createStyledTextField("N 8888 PL");
        card.add(txtVehPlate, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        card.add(new JLabel("Price Per Day (Rp):"), gbc);
        gbc.gridx = 1;
        JTextField txtVehPrice = createStyledTextField("300000");
        card.add(txtVehPrice, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        card.add(new JLabel("Vehicle Category:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> cbVehCategory = new JComboBox<>(new String[]{"Car", "Motorcycle"});
        cbVehCategory.setBackground(BG_CARD);
        cbVehCategory.setForeground(COLOR_TEXT_PRIMARY);
        cbVehCategory.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        card.add(cbVehCategory, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        card.add(new JLabel("Car Custom Type (e.g. SUV) / Details:"), gbc);
        gbc.gridx = 1;
        JTextField txtVehDetails = createStyledTextField("SUV");
        card.add(txtVehDetails, gbc);
        
        cbVehCategory.addActionListener(e -> {
            if ("Motorcycle".equals(cbVehCategory.getSelectedItem())) {
                txtVehDetails.setText("Motorbike");
                txtVehDetails.setEnabled(false);
            } else {
                txtVehDetails.setText("SUV");
                txtVehDetails.setEnabled(true);
            }
        });
        
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        JCheckBox chkVehAvailable = new JCheckBox("Mark as Available for Rent instantly", true);
        chkVehAvailable.setOpaque(false);
        chkVehAvailable.setForeground(COLOR_TEXT_PRIMARY);
        card.add(chkVehAvailable, gbc);
        
        gbc.gridy = 8;
        JButton btnAddVeh = createStyledButton("ADD VEHICLE TO FLEET", COLOR_PRIMARY);
        btnAddVeh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        card.add(btnAddVeh, gbc);
        
        btnAddVeh.addActionListener(e -> {
            String id = txtVehId.getText().trim();
            String name = txtVehName.getText().trim();
            String plate = txtVehPlate.getText().trim();
            String priceStr = txtVehPrice.getText().trim();
            String category = (String) cbVehCategory.getSelectedItem();
            String details = txtVehDetails.getText().trim();
            boolean isAvail = chkVehAvailable.isSelected();

            if (id.isEmpty() || name.isEmpty() || plate.isEmpty() || priceStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All vehicle fields are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (findVehicle(id) != null) {
                JOptionPane.showMessageDialog(this, "Vehicle ID already exists!", "Duplicate Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                double price = Double.parseDouble(priceStr);
                if (price <= 0) {
                    JOptionPane.showMessageDialog(this, "Price must be positive!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Vehicle v;
                if ("Motorcycle".equalsIgnoreCase(category)) {
                    v = new Motorcycle(id, name, plate, price);
                } else {
                    v = new Car(id, name, plate, price, details.isEmpty() ? "Standard" : details);
                }
                v.setAvailable(isAvail);
                vehicles.add(v);
                saveAllData();
                refreshAllUI();
                JOptionPane.showMessageDialog(this, "Vehicle added successfully to fleet!", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                txtVehId.setText("V" + String.format("%03d", vehicles.size() + 1));
                txtVehName.setText("");
                txtVehPlate.setText("");
                txtVehPrice.setText("");
                txtVehDetails.setText(cbVehCategory.getSelectedItem().equals("Motorcycle") ? "Motorbike" : "SUV");
                chkVehAvailable.setSelected(true);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Price must be a valid number!", "Format Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        GridBagConstraints outerGbc = new GridBagConstraints();
        outerGbc.gridx = 0; outerGbc.gridy = 0;
        outerGbc.weightx = 1.0; outerGbc.weighty = 1.0;
        outerGbc.anchor = GridBagConstraints.CENTER;
        panel.add(card, outerGbc);
        
        return panel;
    }

    private JPanel createAdminAddPromoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.weightx = 1.0;
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lblHeader = new JLabel("Setup New System Promotion Coupon");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeader.setForeground(COLOR_SECONDARY);
        card.add(lblHeader, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        card.add(new JLabel("Promo Code:"), gbc);
        gbc.gridx = 1;
        JTextField txtPromoCode = createStyledTextField("WEEKEND30");
        card.add(txtPromoCode, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        card.add(new JLabel("Promo Type:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> cbPromoType = new JComboBox<>(new String[]{"Percent Off", "Cashback"});
        cbPromoType.setBackground(BG_CARD);
        cbPromoType.setForeground(COLOR_TEXT_PRIMARY);
        cbPromoType.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        card.add(cbPromoType, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        card.add(new JLabel("Start Date (yyyy/MM/dd):"), gbc);
        gbc.gridx = 1;
        JTextField txtPromoStart = createStyledTextField(LocalDate.now().format(DATE_FORMATTER));
        card.add(txtPromoStart, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        card.add(new JLabel("End Date (yyyy/MM/dd):"), gbc);
        gbc.gridx = 1;
        JTextField txtPromoEnd = createStyledTextField(LocalDate.now().plusDays(30).format(DATE_FORMATTER));
        card.add(txtPromoEnd, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        card.add(new JLabel("Discount Rate (0.0 to 1.0):"), gbc);
        gbc.gridx = 1;
        JTextField txtPromoRate = createStyledTextField("0.20");
        card.add(txtPromoRate, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        card.add(new JLabel("Max Cap Discount (Rp):"), gbc);
        gbc.gridx = 1;
        JTextField txtPromoMax = createStyledTextField("100000");
        card.add(txtPromoMax, gbc);
        
        gbc.gridx = 0; gbc.gridy = 7;
        card.add(new JLabel("Min Purchase Required (Rp):"), gbc);
        gbc.gridx = 1;
        JTextField txtPromoMin = createStyledTextField("150000");
        card.add(txtPromoMin, gbc);
        
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 12, 5, 12);
        JButton btnAddPromo = createStyledButton("CREATE PROMOTION CODE", COLOR_PRIMARY);
        btnAddPromo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        card.add(btnAddPromo, gbc);
        
        btnAddPromo.addActionListener(e -> {
            String code = txtPromoCode.getText().trim();
            String type = (String) cbPromoType.getSelectedItem();
            String startStr = txtPromoStart.getText().trim();
            String endStr = txtPromoEnd.getText().trim();
            String rateStr = txtPromoRate.getText().trim();
            String maxStr = txtPromoMax.getText().trim();
            String minStr = txtPromoMin.getText().trim();

            if (code.isEmpty() || startStr.isEmpty() || endStr.isEmpty() || rateStr.isEmpty() || maxStr.isEmpty() || minStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All promo fields are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (promotions.stream().anyMatch(p -> p.getPromoCode().equalsIgnoreCase(code))) {
                JOptionPane.showMessageDialog(this, "Promo code already exists!", "Duplicate Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                LocalDate start = LocalDate.parse(startStr, DATE_FORMATTER);
                LocalDate end = LocalDate.parse(endStr, DATE_FORMATTER);
                double rate = Double.parseDouble(rateStr);
                double maxD = Double.parseDouble(maxStr);
                double minP = Double.parseDouble(minStr);

                if (rate < 0 || rate > 1.0) {
                    JOptionPane.showMessageDialog(this, "Discount rate must be between 0.0 and 1.0 (e.g. 0.20 for 20% off)!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (maxD < 0 || minP < 0) {
                    JOptionPane.showMessageDialog(this, "Values must be non-negative!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (start.isAfter(end)) {
                    JOptionPane.showMessageDialog(this, "Start date cannot be after end date!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Promotion p;
                if ("Percent Off".equalsIgnoreCase(type)) {
                    p = new PercentOffPromo(code, start, end, rate, maxD, minP);
                } else {
                    p = new CashbackPromo(code, start, end, rate, maxD, minP);
                }
                
                promotions.add(p);
                saveAllData();
                refreshAllUI();
                JOptionPane.showMessageDialog(this, "Promotion added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                txtPromoCode.setText("");
                txtPromoStart.setText(LocalDate.now().format(DATE_FORMATTER));
                txtPromoEnd.setText(LocalDate.now().plusDays(30).format(DATE_FORMATTER));
                txtPromoRate.setText("0.20");
                txtPromoMax.setText("100000");
                txtPromoMin.setText("150000");
            } catch (DateTimeParseException dtpe) {
                JOptionPane.showMessageDialog(this, "Date format must be yyyy/MM/dd!", "Date Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Numeric fields must contain valid numbers!", "Format Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        GridBagConstraints outerGbc = new GridBagConstraints();
        outerGbc.gridx = 0; outerGbc.gridy = 0;
        outerGbc.weightx = 1.0; outerGbc.weighty = 1.0;
        outerGbc.anchor = GridBagConstraints.CENTER;
        panel.add(card, outerGbc);
        
        return panel;
    }

    private void refreshAdminCustomers() {
        if (modelAdminCustomers == null) return;
        modelAdminCustomers.setRowCount(0);
        for (Customer c : customers) {
            String type = (c instanceof Member) ? "MEMBER" : "GUEST";
            String regDate = (c instanceof Member) ? ((Member) c).getRegistrationDate().format(DATE_FORMATTER) : "N/A";
            String promo = (c instanceof Member && ((Member) c).getAppliedPromo() != null) ? ((Member) c).getAppliedPromo().getPromoCode() : "None";
            modelAdminCustomers.addRow(new Object[]{
                c.getId(),
                c.getName(),
                type,
                regDate,
                "Rp " + formatNumber(c.getBalance()),
                promo
            });
        }
    }

    private void refreshAdminOrders() {
        if (modelAdminOrders == null) return;
        modelAdminOrders.setRowCount(0);
        for (Order o : allOrders) {
            long motors = o.getItems().stream().filter(item -> item.getVehicle() instanceof Motorcycle).count();
            long cars = o.getItems().stream().filter(item -> item.getVehicle() instanceof Car).count();
            String promoCode = o.getPromo() != null ? o.getPromo().getPromoCode() : "None";
            modelAdminOrders.addRow(new Object[]{
                o.getOrderNumber(),
                o.getCustomer().getId(),
                o.getCustomer().getName(),
                o.getOrderDate().format(DATE_FORMATTER),
                motors,
                cars,
                promoCode,
                "Rp " + formatNumber(o.getTotalPrice())
            });
        }
    }

    // ==========================================
    // STYLING UTILITIES
    // ==========================================
    private JPanel createPlaceholderPanel(String text) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_DARK);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 20, 20);
        
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        label.setForeground(COLOR_TEXT_SECONDARY);
        panel.add(label, gbc);
        
        gbc.gridy = 1;
        JButton btnBack = createStyledButton("Back to Welcome Screen", COLOR_DANGER);
        btnBack.addActionListener(e -> cardLayout.show(mainContentPanel, "WELCOME"));
        panel.add(btnBack, gbc);
        
        return panel;
    }
    
    private JTextField createStyledTextField(String defaultText) {
        JTextField txt = new JTextField(defaultText);
        txt.setBackground(new Color(30, 41, 59));
        txt.setForeground(COLOR_TEXT_PRIMARY);
        txt.setCaretColor(Color.WHITE);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        return txt;
    }
    
    private JButton createStyledButton(String label, Color baseColor) {
        JButton btn = new JButton(label);
        btn.setBackground(baseColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(baseColor.darker(), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)));
                
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(baseColor.brighter());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(baseColor);
            }
        });
        return btn;
    }
    
    private JButton createStyledWelcomeButton(String text, Color baseColor) {
        JButton btn = new JButton("<html><center>" + text.replaceAll("\n", "<br>") + "</center></html>");
        btn.setPreferredSize(new Dimension(280, 180));
        btn.setBackground(baseColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(baseColor.darker(), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
                
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(baseColor.brighter());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(baseColor);
            }
        });
        return btn;
    }
    
    private void styleTable(JTable table) {
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(30, 41, 59));
        table.getTableHeader().setForeground(new Color(243, 244, 246));
        table.getTableHeader().setOpaque(true);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 1, COLOR_BORDER));
        
        table.setBackground(BG_CARD);
        table.setForeground(COLOR_TEXT_PRIMARY);
        table.setRowHeight(32);
        table.setGridColor(COLOR_BORDER);
        table.setSelectionBackground(COLOR_PRIMARY);
        table.setSelectionForeground(Color.WHITE);
        
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isSel, boolean hasFoc, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, isSel, hasFoc, row, col);
                if (isSel) {
                    setBackground(COLOR_PRIMARY);
                    setForeground(Color.WHITE);
                } else {
                    if (row % 2 == 0) {
                        setBackground(new Color(15, 23, 42));
                    } else {
                        setBackground(new Color(30, 41, 59));
                    }
                    setForeground(COLOR_TEXT_PRIMARY);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        });
    }
    
    private Customer findCustomer(String id) {
        return customers.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
    }

    private Vehicle findVehicle(String id) {
        return vehicles.stream().filter(v -> v.getId().equals(id)).findFirst().orElse(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FilkomTravelGUI gui = new FilkomTravelGUI();
            gui.setVisible(true);
        });
    }
}
