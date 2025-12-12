import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.util.List;

public class GameWindow extends JFrame {
    private JTextArea logArea;
    private GameEngine engine;

    // Stats Components
    private JProgressBar playerHealthBar;
    private JLabel playerHpText;
    private JLabel playerStatsText;

    private JProgressBar enemyHealthBar;
    private JLabel enemyHpText;
    private JLabel enemyStatsText;
    private JPanel enemyPanel; // To hide/show when no enemy

    // Inventory Components
    private JList<String> inventoryList;
    private DefaultListModel<String> inventoryModel;
    private JButton btnUseItem;
    private JTabbedPane rightTabPane;

    // Action Buttons
    private JButton btnExplore;
    private JButton btnAttack;
    private JButton btnRun;
    private JButton btnBestiary;

    public GameWindow() {
        // 1. Look and Feel
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception e) {}

        setTitle("Java RPG - GUI Edition");
        setSize(1000, 600); // Wider window for columns
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(1, 2)); // Two main columns

        // === LEFT COLUMN: LOG & CONTROLS ===
        JPanel leftPanel = new JPanel(new BorderLayout());

        // Log Area
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        logArea.setMargin(new Insets(10, 10, 10, 10));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        DefaultCaret caret = (DefaultCaret) logArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        leftPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);

        // Control Buttons
        JPanel controlsPanel = new JPanel(new FlowLayout());
        btnExplore = new JButton("Explore");
        btnAttack = new JButton("Attack");
        btnRun = new JButton("Run");
        btnBestiary = new JButton("Bestiary");

        btnAttack.setEnabled(false);
        btnRun.setEnabled(false);

        controlsPanel.add(btnExplore);
        controlsPanel.add(btnAttack);
        controlsPanel.add(btnRun);
        controlsPanel.add(btnBestiary);
        leftPanel.add(controlsPanel, BorderLayout.SOUTH);

        add(leftPanel);

        // === RIGHT COLUMN: TABS (STATS & INVENTORY) ===
        rightTabPane = new JTabbedPane();

        // -- Tab 1: Status --
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Player Stats Section
        JPanel playerPanel = createStatsSection("Hero Status");
        playerHpText = new JLabel("HP: 0/0", SwingConstants.CENTER);
        playerHpText.setAlignmentX(Component.CENTER_ALIGNMENT);
        playerHealthBar = new JProgressBar(0, 100);
        playerHealthBar.setValue(100);
        playerHealthBar.setForeground(new Color(50, 205, 50)); // Lime Green
        playerHealthBar.setStringPainted(false);
        playerStatsText = new JLabel("STR: 0 | DEF: 0");
        playerStatsText.setAlignmentX(Component.CENTER_ALIGNMENT);

        playerPanel.add(Box.createVerticalStrut(5));
        playerPanel.add(playerHpText);
        playerPanel.add(playerHealthBar);
        playerPanel.add(Box.createVerticalStrut(10));
        playerPanel.add(playerStatsText);
        statsPanel.add(playerPanel);

        statsPanel.add(Box.createVerticalStrut(20));

        // Enemy Stats Section
        enemyPanel = createStatsSection("Enemy Status");
        enemyHpText = new JLabel("HP: 0/0", SwingConstants.CENTER);
        enemyHpText.setAlignmentX(Component.CENTER_ALIGNMENT);
        enemyHealthBar = new JProgressBar(0, 100);
        enemyHealthBar.setForeground(new Color(220, 20, 60)); // Crimson
        enemyHealthBar.setStringPainted(false);
        enemyStatsText = new JLabel("STR: 0 | DEF: 0");
        enemyStatsText.setAlignmentX(Component.CENTER_ALIGNMENT);

        enemyPanel.add(Box.createVerticalStrut(5));
        enemyPanel.add(enemyHpText);
        enemyPanel.add(enemyHealthBar);
        enemyPanel.add(Box.createVerticalStrut(10));
        enemyPanel.add(enemyStatsText);
        statsPanel.add(enemyPanel);

        enemyPanel.setVisible(false); // Hide until combat

        rightTabPane.addTab("Status", statsPanel);

        // -- Tab 2: Inventory --
        JPanel invPanel = new JPanel(new BorderLayout());
        inventoryModel = new DefaultListModel<>();
        inventoryList = new JList<>(inventoryModel);
        inventoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        invPanel.add(new JScrollPane(inventoryList), BorderLayout.CENTER);

        JPanel invActionPanel = new JPanel();
        btnUseItem = new JButton("Use / Equip Selected");
        invActionPanel.add(btnUseItem);
        invPanel.add(invActionPanel, BorderLayout.SOUTH);

        rightTabPane.addTab("Inventory", invPanel);

        add(rightTabPane);

        // 6. Connect Logger
        Logger.setOutput(msg -> logArea.append(msg + "\n"));
    }

    private JPanel createStatsSection(String title) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        TitledBorder border = BorderFactory.createTitledBorder(title);
        border.setTitleFont(new Font("SansSerif", Font.BOLD, 14));
        p.setBorder(border);
        return p;
    }

    public void setEngine(GameEngine engine) {
        this.engine = engine;

        btnExplore.addActionListener(e -> engine.actionExplore());
        btnAttack.addActionListener(e -> engine.actionAttack());
        btnRun.addActionListener(e -> engine.actionRun());
        btnBestiary.addActionListener(e -> engine.actionBestiary());

        btnUseItem.addActionListener(e -> {
            int idx = inventoryList.getSelectedIndex();
            if (idx != -1) {
                engine.handleInventoryUse(idx);
            }
        });
    }

    // --- Update Methods ---

    public void updatePlayerStats(String name, int currentHp, int maxHp, int str, int def, String gearInfo) {
        // Fix: Cast parent to JComponent to access getBorder()
        JComponent parent = (JComponent) playerHealthBar.getParent();
        ((TitledBorder) parent.getBorder()).setTitle(name);
        parent.repaint(); // Force repaint of the border title

        playerHealthBar.setMaximum(maxHp);
        playerHealthBar.setValue(currentHp);
        playerHpText.setText(currentHp + " / " + maxHp);
        playerStatsText.setText("<html><center>STR: " + str + " | DEF: " + def + "<br/>" + gearInfo + "</center></html>");
        repaint();
    }

    public void updateEnemyStats(boolean hasEnemy, String name, int currentHp, int maxHp, int str, int def) {
        if (!hasEnemy) {
            enemyPanel.setVisible(false);
        } else {
            enemyPanel.setVisible(true);
            ((TitledBorder) enemyPanel.getBorder()).setTitle(name);
            enemyHealthBar.setMaximum(maxHp);
            enemyHealthBar.setValue(currentHp);
            enemyHpText.setText(currentHp + " / " + maxHp);
            enemyStatsText.setText("STR: " + str + " | DEF: " + def);
        }
    }

    public void updateInventoryList(List<Item> items) {
        inventoryModel.clear();
        for (Item i : items) {
            inventoryModel.addElement(i.toString());
        }
    }

    public void setCombatMode(boolean isCombat) {
        btnExplore.setEnabled(!isCombat);
        btnAttack.setEnabled(isCombat);
        btnRun.setEnabled(isCombat);

        // Auto-switch tabs based on context
        if (isCombat) {
            rightTabPane.setSelectedIndex(0); // Show stats in fight
        }
    }
}