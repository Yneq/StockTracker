package com.controller;

import com.model.StockHistoryData;
import com.service.StockService;
import com.service.impl.StockServiceImpl;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class StockControllerFrame extends JFrame {

    private final StockService stockService = new StockServiceImpl();
    private JTable dataTable;
    private DefaultTableModel tableModel;
    private JButton syncButton;
    private JLabel statusLabel;

    public StockControllerFrame() {
        super(" Mac Stocks Controller Terminal");
        initUI();
        initEvents();
        startAutoRefreshTimer();
        refreshTableData();
    }

    private void initUI() {
        setSize(850, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.BLACK);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.BLACK);
        
        JLabel titleLabel = new JLabel(" 股市本機控制台監控端");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        
        syncButton = new JButton("⚡ 立即強制同步");
        syncButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        syncButton.setBackground(new Color(31, 41, 55));
        syncButton.setForeground(Color.WHITE);
        
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(syncButton, BorderLayout.EAST);

        String[] columns = {"代碼", "股票名稱", "即時價格", "開盤價", "ADR折合", "套利空間", "證交所時間"};
        tableModel = new DefaultTableModel(columns, 0);
        dataTable = new JTable(tableModel);
        dataTable.setBackground(new Color(17, 24, 39));
        dataTable.setForeground(Color.LIGHT_GRAY);
        dataTable.setGridColor(new Color(31, 41, 55));
        dataTable.setFont(new Font("Monospaced", Font.PLAIN, 13));
        dataTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(dataTable);
        scrollPane.getViewport().setBackground(Color.BLACK);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(31, 41, 55)));

        statusLabel = new JLabel("● 系統就緒：定時任務同步中...");
        statusLabel.setForeground(Color.GRAY);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(statusLabel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void initEvents() {
        syncButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText("⚡ 正在強制觸發網路 API 同步中...");
                new Thread(() -> {
                    try {
                        stockService.downloadAndStoreStockData();
                        refreshTableData();
                        statusLabel.setText("● 同步成功！最新資料已寫入資料庫。");
                    } catch (Exception ex) {
                        statusLabel.setText("❌ 同步失敗: " + ex.getMessage());
                    }
                }).start();
            }
        });
    }

    private void startAutoRefreshTimer() {
        Timer timer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshTableData();
            }
        });
        timer.start();
    }

    private void refreshTableData() {
        try {
            List<StockHistoryData> list = stockService.fetchDisplayData();
            tableModel.setRowCount(0);
            for (StockHistoryData d : list) {
                tableModel.addRow(new Object[]{
                        d.getStockCode(),
                        d.getStockName(),
                        d.getPrice(),
                        d.getOpenPrice(),
                        "2330".equals(d.getStockCode()) ? d.getAdrPrice() : "-",
                        "2330".equals(d.getStockCode()) ? d.getArbitrageSpace() + "%" : "-",
                        d.getTradeTime()
                });
            }
        } catch (Exception ex) {
            statusLabel.setText("⚠️ 自動刷新表格失敗: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StockControllerFrame().setVisible(true);
        });
    }
}