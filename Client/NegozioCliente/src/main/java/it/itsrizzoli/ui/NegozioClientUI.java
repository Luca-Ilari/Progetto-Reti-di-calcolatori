package it.itsrizzoli.ui;

import it.itsrizzoli.modelli.Prodotto;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NegozioClientUI extends JFrame {
    private JTable carrelloTable = new JTable();
    private JTable articoliNegozioTable = new JTable();
    private JTable transazioniTable = new JTable();


    private List<Prodotto> prodottiNegozio = new ArrayList<>();

    private String[] articoliNegozioColonne = {"Prodotto", "Quantità", "Prezzo"};
    private String[] carrelloColonne = {"Prodotto", "Quantità"};
    private String[] transazioniColonne = {"Number", "Prodotto", "Quantità", "Prezzo", "Stato"};


    public NegozioClientUI() {
        setTitle("Negozio Online - Interfaccia Cliente");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(200, 300));
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        contentPane.add(createTitleLabel());

        contentPane.add(createLabelTableCarrelloNegozio());

        JPanel tablesPanel = new JPanel();
        tablesPanel.setLayout(new BoxLayout(tablesPanel, BoxLayout.X_AXIS));

        // Aggiungi la prima tabella
        String[][] carrelloData = {{"Prodotto 1", "1"}, {"Prodotto 2", "2"}};
        JScrollPane jScrollPane1 = creaTabellaPanello(carrelloTable, carrelloColonne, carrelloData);
        tablesPanel.add(jScrollPane1);

        // Aggiungi uno spazio tra le tabelle
        tablesPanel.add(Box.createRigidArea(new Dimension(10, 0)));

        // Aggiungi la seconda tabella
        String[][] articoliNegozioData = {{"Prodotto A", "10", "50.00"}, {"Prodotto B", "5", "120.00"}};
        JScrollPane jScrollPane2 = creaTabellaPanello(articoliNegozioTable, articoliNegozioColonne, articoliNegozioData);
        tablesPanel.add(jScrollPane2);

        // Aggiungi il pannello delle tabelle al contenuto principale
        contentPane.add(tablesPanel);

        JLabel transazioniLabel = new JLabel("Le mie transazioni");
        transazioniLabel.setFont(new Font("Arial", Font.BOLD, 16));
        contentPane.add(transazioniLabel);

        JPanel transazioniPanel = new JPanel();
        transazioniPanel.setLayout(new BoxLayout(transazioniPanel, BoxLayout.PAGE_AXIS));
        transazioniPanel.setBackground(Color.LIGHT_GRAY);

        String[][] transazioniData = {{"1", "Prodotto X", "5", "10.00", "Consegnato"}};
        JScrollPane jScrollPane3 = creaTabellaPanello(transazioniTable, transazioniColonne, transazioniData);
        transazioniPanel.add(jScrollPane3);
        contentPane.add(transazioniPanel);

        setContentPane(contentPane);

        allSetResponsiveTable();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }


    public JTable getCarrelloTable() {
        return carrelloTable;
    }

    public void setCarrelloTable(JTable carrelloTable) {
        this.carrelloTable = carrelloTable;
    }

    public JTable getArticoliNegozioTable() {
        return articoliNegozioTable;
    }

    public void setArticoliNegozioTable(JTable articoliNegozioTable) {
        this.articoliNegozioTable = articoliNegozioTable;
    }

    public JTable getTransazioniTable() {
        return transazioniTable;
    }

    public void setTransazioniTable(JTable transazioniTable) {
        this.transazioniTable = transazioniTable;
    }

    public boolean aggiornaElementiTabella(List<Prodotto> prodottiNegozio, DefaultTableModel model) {
        // Rimuovi tutte le righe esistenti dal modello
        model.setRowCount(0);
        // Aggiungi righe per ciascun prodotto nella lista
        for (Prodotto product : prodottiNegozio) {
            Object[] rowData = {product.getNome(), product.getPrezzo(), product.getQuantitaDisponibile()};
            model.addRow(rowData);
        }
        return true;
    }

    private JLabel createTitleLabel() {
        JLabel titleLabel = new JLabel("Gestione Ordini - Negozio Online");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 25));
        return titleLabel;
    }

    private JPanel createLabelTableCarrelloNegozio() {

        JPanel labelsPanel = new JPanel();
        labelsPanel.setLayout(new BoxLayout(labelsPanel, BoxLayout.LINE_AXIS));


        JLabel carrelloLabel = new JLabel("Carrello ");
        carrelloLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel articoliNegozioLabel = new JLabel("Articoli negozio");
        articoliNegozioLabel.setFont(new Font("Arial", Font.BOLD, 14));

        labelsPanel.add(Box.createHorizontalGlue());
        labelsPanel.add(carrelloLabel);
        labelsPanel.add(Box.createHorizontalGlue());
        labelsPanel.add(articoliNegozioLabel);
        labelsPanel.add(Box.createHorizontalGlue());

        return labelsPanel;
    }

    private JScrollPane creaTabellaPanello(JTable table, String[] colonneTabella, String[][] elementiTabella) {
        table = new JTable(new DefaultTableModel(elementiTabella, colonneTabella));
        // Imposta l'allineamento al centro per tutte le colonne
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        JScrollPane tableScrollPane = new JScrollPane(table);
        return tableScrollPane;
    }


    private void allSetResponsiveTable() {
        articoliNegozioTable.setPreferredScrollableViewportSize(articoliNegozioTable.getPreferredSize());
        carrelloTable.setPreferredScrollableViewportSize(carrelloTable.getPreferredSize());
        transazioniTable.setPreferredScrollableViewportSize(transazioniTable.getPreferredSize());
    }


}
