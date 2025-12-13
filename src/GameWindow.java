import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class GameWindow extends JFrame {
    private JTextArea logArea;

    // Tabs
    private JTabbedPane rightTabPane;
    private JPanel mapPanel;
    private JLabel[][] mapGridLabels;
    private JTable bestiaryTable;
    private DefaultTableModel bestiaryModel;

    // Stats Components
    private JProgressBar playerHealthBar;
    private JLabel playerHpText;
    private JLabel playerStatsText;
    private JLabel lblWeaponSlot;
    private JLabel lblArmorSlot;

    private JPanel enemyPanel;
    private JProgressBar enemyHealthBar;
    private JLabel enemyHpText;
    private JLabel enemyStatsText;

    // Inventory Components
    private JList<String> inventoryList;
    private DefaultListModel<String> inventoryModel;
    private List<Item> currentInventoryItems; // Local reference for tooltips
    private JButton btnUseItem;

    // Controls
    private JButton btnExplore;
    private JButton btnAttack;
    private JButton btnRun;

    public GameWindow() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        setTitle("Java RPG - Equipment Update");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(1, 2));

        // LEFT COLUMN
        JPanel leftPanel = new JPanel(new BorderLayout());
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        logArea.setMargin(new Insets(10, 10, 10, 10));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        ((DefaultCaret)logArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        leftPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);

        JPanel controls = new JPanel();
        btnExplore = new JButton("Explore Area");
        btnAttack = new JButton("Attack");
        btnRun = new JButton("Run");
        btnAttack.setEnabled(false);
        btnRun.setEnabled(false);
        controls.add(btnExplore);
        controls.add(btnAttack);
        controls.add(btnRun);
        leftPanel.add(controls, BorderLayout.SOUTH);
        add(leftPanel);

        // RIGHT COLUMN (TABS)
        rightTabPane = new JTabbedPane();

        // 1. STATS TAB
        rightTabPane.addTab("Status", createStatsTab());

        // 2. INVENTORY TAB
        rightTabPane.addTab("Inventory", createInventoryTab());

        // 3. MAP TAB
        mapPanel = new JPanel();
        rightTabPane.addTab("Map", mapPanel);

        // 4. BESTIARY TAB
        JPanel bestiaryPanel = new JPanel(new BorderLayout());
        String[] columns = {"Monster", "Lvl", "Stats", "Drops", "Chance"};
        bestiaryModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        bestiaryTable = new JTable(bestiaryModel);
        bestiaryTable.setFillsViewportHeight(true);
        bestiaryPanel.add(new JScrollPane(bestiaryTable), BorderLayout.CENTER);
        bestiaryPanel.add(new JLabel(" ??? indicates undiscovered info.", SwingConstants.CENTER), BorderLayout.SOUTH);
        rightTabPane.addTab("Bestiary", bestiaryPanel);
        rightTabPane.setEnabledAt(3, false);

        add(rightTabPane);
        Logger.setOutput(msg -> logArea.append(msg + "\n"));
    }

    private JPanel createStatsTab() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Player Section
        JPanel pPanel = new JPanel();
        pPanel.setLayout(new BoxLayout(pPanel, BoxLayout.Y_AXIS));
        TitledBorder tb = BorderFactory.createTitledBorder("Hero");
        tb.setTitleFont(new Font("SansSerif", Font.BOLD, 14));
        pPanel.setBorder(tb);

        playerHpText = new JLabel("HP: 0/0");
        playerHpText.setAlignmentX(Component.CENTER_ALIGNMENT);
        playerHealthBar = new JProgressBar(0, 100);
        playerHealthBar.setValue(100);
        playerHealthBar.setForeground(new Color(50, 205, 50));
        playerHealthBar.setStringPainted(false);
        playerStatsText = new JLabel("Stats...");
        playerStatsText.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Equipment Slots
        JPanel equipGrid = new JPanel(new GridLayout(2, 1));
        equipGrid.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));
        lblWeaponSlot = new JLabel("Main Hand: Empty");
        lblArmorSlot = new JLabel("Body: Empty");

        // Add borders/styling to slots
        lblWeaponSlot.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        lblArmorSlot.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        // Enable tooltips on slots
        ToolTipManager.sharedInstance().registerComponent(lblWeaponSlot);
        ToolTipManager.sharedInstance().registerComponent(lblArmorSlot);

        equipGrid.add(lblWeaponSlot);
        equipGrid.add(lblArmorSlot);

        pPanel.add(playerHpText);
        pPanel.add(playerHealthBar);
        pPanel.add(Box.createVerticalStrut(10));
        pPanel.add(playerStatsText);
        pPanel.add(equipGrid);

        p.add(pPanel);
        p.add(Box.createVerticalStrut(20));

        // Enemy Section
        enemyPanel = new JPanel();
        enemyPanel.setLayout(new BoxLayout(enemyPanel, BoxLayout.Y_AXIS));
        TitledBorder tb2 = BorderFactory.createTitledBorder("Enemy");
        tb2.setTitleFont(new Font("SansSerif", Font.BOLD, 14));
        enemyPanel.setBorder(tb2);

        enemyHpText = new JLabel("HP: 0/0");
        enemyHpText.setAlignmentX(Component.CENTER_ALIGNMENT);
        enemyHealthBar = new JProgressBar(0, 100);
        enemyHealthBar.setForeground(new Color(220, 20, 60));
        enemyHealthBar.setStringPainted(false);
        enemyStatsText = new JLabel("Stats...");
        enemyStatsText.setAlignmentX(Component.CENTER_ALIGNMENT);

        enemyPanel.add(enemyHpText);
        enemyPanel.add(enemyHealthBar);
        enemyPanel.add(Box.createVerticalStrut(10));
        enemyPanel.add(enemyStatsText);
        enemyPanel.setVisible(false);
        p.add(enemyPanel);

        return p;
    }

    private JPanel createInventoryTab() {
        JPanel p = new JPanel(new BorderLayout());
        inventoryModel = new DefaultListModel<>();
        currentInventoryItems = new ArrayList<>();

        // Custom JList for Tooltips
        inventoryList = new JList<>(inventoryModel) {
            @Override
            public String getToolTipText(MouseEvent evt) {
                int index = locationToIndex(evt.getPoint());
                if (index > -1 && index < currentInventoryItems.size()) {
                    return currentInventoryItems.get(index).getStatsInfo();
                }
                return null;
            }
        };
        ToolTipManager.sharedInstance().registerComponent(inventoryList);

        p.add(new JScrollPane(inventoryList), BorderLayout.CENTER);
        btnUseItem = new JButton("Use / Equip / Unequip");
        p.add(btnUseItem, BorderLayout.SOUTH);
        return p;
    }

    public void initMapGrid(int w, int h) {
        mapPanel.setLayout(new GridLayout(h, w, 2, 2));
        mapPanel.setBackground(Color.DARK_GRAY);
        mapGridLabels = new JLabel[w][h];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                JLabel lbl = new JLabel("?", SwingConstants.CENTER);
                lbl.setOpaque(true);
                lbl.setBackground(Color.GRAY);
                lbl.setForeground(Color.LIGHT_GRAY);
                lbl.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                mapGridLabels[x][y] = lbl;
                mapPanel.add(lbl);
            }
        }
    }

    public void updateMapDisplay(DungeonMap map) {
        Point pPos = map.getPlayerPos();
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                JLabel cell = mapGridLabels[x][y];
                if (x == pPos.x && y == pPos.y) {
                    cell.setBackground(Color.CYAN);
                    cell.setText("HERO");
                } else if (map.isExplored(x, y)) {
                    cell.setBackground(Color.WHITE);
                    cell.setText("");
                } else {
                    cell.setBackground(Color.GRAY);
                    cell.setText("?");
                }
            }
        }
    }

    public void setEngine(GameEngine engine) {
        btnExplore.addActionListener(e -> engine.actionExplore());
        btnAttack.addActionListener(e -> engine.actionAttack());
        btnRun.addActionListener(e -> engine.actionRun());
        btnUseItem.addActionListener(e -> {
            int idx = inventoryList.getSelectedIndex();
            if(idx != -1) engine.handleInventoryUse(idx);
        });
    }

    // --- GUI Updates ---
    public void updatePlayerStats(Player p) {
        JComponent parent = (JComponent) playerHealthBar.getParent();
        ((TitledBorder) parent.getBorder()).setTitle(p.getName() + " (Lvl " + p.getLevel() + ")");
        parent.repaint();
        playerHealthBar.setMaximum(p.getMaxHp());
        playerHealthBar.setValue(p.getCurrentHp());
        playerHpText.setText(p.getCurrentHp() + " / " + p.getMaxHp());
        playerStatsText.setText("STR: " + p.getEffectiveStrength() + " | DEF: " + p.calculateDefense());

        // Update Equipment Slots & Tooltips
        Weapon w = p.getEquippedWeapon();
        Armor a = p.getEquippedArmor();

        lblWeaponSlot.setText("Main Hand: " + (w != null ? w.getName() : "Fists"));
        lblWeaponSlot.setToolTipText(w != null ? w.getStatsInfo() : null);

        lblArmorSlot.setText("Body: " + (a != null ? a.getName() : "Clothes"));
        lblArmorSlot.setToolTipText(a != null ? a.getStatsInfo() : null);
    }

    public void updateEnemyStats(boolean active, Monster m) {
        enemyPanel.setVisible(active);
        if(active) {
            JComponent parent = (JComponent) enemyHealthBar.getParent();
            ((TitledBorder) parent.getBorder()).setTitle(m.getName() + " (Lvl " + m.getLevel() + ")");
            parent.repaint();
            enemyHealthBar.setMaximum(m.getMaxHp());
            enemyHealthBar.setValue(m.getCurrentHp());
            enemyHpText.setText(m.getCurrentHp() + " / " + m.getMaxHp());
            enemyStatsText.setText("STR: " + m.getEffectiveStrength() + " | DEF: " + m.getEffectiveDefense());
        }
    }

    public void updateInventoryList(List<Item> items, Player p) {
        inventoryModel.clear();
        currentInventoryItems.clear(); // Copy list for tooltips
        currentInventoryItems.addAll(items);

        for(Item i : items) {
            String name = i.toString();
            if (p.isEquipped(i)) {
                name = "[E] " + name;
            }
            inventoryModel.addElement(name);
        }
    }

    public void unlockBestiary() { rightTabPane.setEnabledAt(3, true); }

    public void updateBestiary(List<Monster> catalog, Player player) {
        bestiaryModel.setRowCount(0);
        for(Monster m : catalog) {
            if (player.hasKilled(m.getName())) {
                String lootInfo = "None";
                String chance = "0%";
                Item i = m.getPossibleLoot();
                if (i != null) {
                    lootInfo = i.getName();
                    if (i instanceof Weapon) lootInfo += " (+" + ((Weapon)i).getDamageBonus() + " Dmg)";
                    else if (i instanceof Armor) lootInfo += " (+" + ((Armor)i).getDefenseBonus() + " Def)";
                    chance = (int)(m.getDropChance() * 100) + "%";
                }
                bestiaryModel.addRow(new Object[]{ m.getName(), m.getLevel(), "Str:" + m.getEffectiveStrength() + " Def:" + m.getEffectiveDefense(), lootInfo, chance });
            } else {
                bestiaryModel.addRow(new Object[]{"???", "?", "?", "?", "?"});
            }
        }
    }

    public void setCombatMode(boolean isCombat) {
        btnExplore.setEnabled(!isCombat);
        btnAttack.setEnabled(isCombat);
        btnRun.setEnabled(isCombat);
        if(isCombat) rightTabPane.setSelectedIndex(0);
    }
}