package it.itsrizzoli;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Start Client...");
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Dynamic ScrollPane Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(700, 500);
            frame.setLayout(new GridLayout(3, 1));
            JPanel panel = new JPanel(new GridLayout(1, 2)); // Una colonna che si adatta al contenuto

            JLabel label1 = new JLabel("La Mia Borsa");
            JLabel label2 = new JLabel("Negozio Online");

            panel.add(label1);
            panel.add(label2);

            frame.add(panel);
            String[][] data1 = {{"🥄", "0"}, {"⚙️", "0"}, {"🔦", "5"}, {"🎲", "2"}, {"📚", "1"}, {"🎧", "1"}, {"👕",
                    "2"}, {"👟", "1"}, {"🕶️", "8"}, {"💼", "1"}};
            String[] columnsBorsa = {"Prodotto", "Quantità"};
            JTable table1 = new JTable(data1, columnsBorsa);
            table1.setPreferredScrollableViewportSize(table1.getPreferredSize());

            String[][] data2 = {{"🪑", "10", "50.00"}, {"🛋️", "5", "120.00"}, {"🛏️", "2", "300.00"}, {"🚪", "1",
                    "150.00"}, {"🪞", "3", "80.00"}};
            String[] columnsNegozio = {"Prodotto", "Quantità", "Prezzo"};
            JTable table2 = new JTable(data2, columnsNegozio);
            table2.setPreferredScrollableViewportSize(table2.getPreferredSize());

            JPanel tablesPanel = new JPanel(new GridLayout(1, 2));
            tablesPanel.add(new JScrollPane(table1));
            tablesPanel.add(new JScrollPane(table2));

            JScrollPane dynamicScrollPane = new JScrollPane(tablesPanel);
            frame.add(dynamicScrollPane);

            JPanel transazioniPanel = new JPanel(new GridLayout(0, 1));
            transazioniPanel.setBackground(Color.LIGHT_GRAY);
            JLabel transazioniLabel = new JLabel("Le mie transazioni");
            transazioniLabel.setFont(new Font("Arial", Font.BOLD, 16));
            transazioniPanel.add(transazioniLabel);

            frame.add(transazioniPanel);
            Object[][] data = {{"Dato 1A", "Dato 1B", "Dato 1C"}, {"Dato 2A", "Dato 2B", "Dato 2C"}, {"Dato 3A",
                    "Dato 3B", "Dato 3C"}};
            DefaultTableModel model = new DefaultTableModel(data, columnsNegozio);
            JTable table = new JTable(model);
            table.setPreferredScrollableViewportSize(table.getPreferredSize());
            JScrollPane scrollPane = new JScrollPane(table);

            transazioniPanel.add(scrollPane);
            frame.add(transazioniPanel);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

    }
}
