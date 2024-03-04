package it.itsrizzoli.ui;

import it.itsrizzoli.modelli.Prodotto;
import it.itsrizzoli.modelli.Transazione;

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

    private List<Prodotto> prodottiCarrello = new ArrayList<>();

    private List<Prodotto> prodottiNegozio = new ArrayList<>();
    private List<Transazione> listaTransazione = new ArrayList<>();

    private String[] articoliNegozioColonne = {"Prodotto", "Prezzo", "Disponibile"};
    private String[] carrelloColonne = {"Prodotto", "Quantità"};
    private String[] transazioniColonne = {"Number", "Prodotto", "Prezzo", "Quantità", "Stato"};


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
        JScrollPane jScrollPane2 = creaTabellaPanello(articoliNegozioTable, articoliNegozioColonne,
                articoliNegozioData);
        tablesPanel.add(jScrollPane2);

        // Aggiungi il pannello delle tabelle al contenuto principale
        contentPane.add(tablesPanel);

        JLabel transazioniLabel = new JLabel("Le mie transazioni");
        transazioniLabel.setFont(new Font("Arial", Font.BOLD, 16));
        contentPane.add(transazioniLabel);

        JPanel transazioniPanel = new JPanel();
        transazioniPanel.setLayout(new BoxLayout(transazioniPanel, BoxLayout.PAGE_AXIS));
        transazioniPanel.setBackground(Color.LIGHT_GRAY);

        String[][] transazioniData = {{"", "", "", "", ""}};
        JScrollPane jScrollPane3 = creaTabellaPanello(transazioniTable, transazioniColonne, transazioniData);
        DefaultTableModel defaultTableModel = (DefaultTableModel) transazioniTable.getModel();
        transazioniPanel.add(jScrollPane3);
        contentPane.add(transazioniPanel);

        setContentPane(contentPane);

        allSetResponsiveTable();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private static void azzeraElementiTable(DefaultTableModel defaultTableModel) {
        defaultTableModel.setRowCount(0);
        defaultTableModel.fireTableDataChanged();
    }


    public List<Prodotto> getProdottiNegozio() {
        return prodottiNegozio;
    }

    public void setProdottiNegozio(List<Prodotto> prodottiNegozio) {
        this.prodottiNegozio = prodottiNegozio;
    }

    public List<Transazione> getListaTransazione() {
        return listaTransazione;
    }

    public void setListaTransazione(List<Transazione> listaTransazione) {
        this.listaTransazione = listaTransazione;
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

    public void aggiornaProdottiNegozio(List<Prodotto> newProdottiNegozio) {
        // Rimuovi tutte le righe esistenti dal modello
        this.prodottiNegozio = newProdottiNegozio;

        SwingUtilities.invokeLater(() -> {
            DefaultTableModel model = (DefaultTableModel) articoliNegozioTable.getModel();
            // Aggiungi righe per ciascun prodotto nella lista
            model.setRowCount(0);
            for (Prodotto product : newProdottiNegozio) {
                Object[] rowData = {product.getNome(), product.getPrezzo()+"€", product.getQuantitaDisponibile()};
                model.addRow(rowData);
            }
            model.fireTableDataChanged();

            System.out.println(" --> UI: Carrello aggiornato!!");

        });

    }

    public void aggiornaStateTransazione(int idTransazione) {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel transazioniTableModel = (DefaultTableModel) transazioniTable.getModel();
            Transazione transazione = null;

            transazione = trovaTransazione(idTransazione);
            if (transazione == null) {
                System.err.println(" - Errore: Transazione non trovata");
                return;
            }

            // Aggiorna il valore nella colonna "Stato" alla riga
            for (int riga = 0; riga < transazioniTableModel.getRowCount(); riga++) {
                int idTransazioneRow = (int) transazioniTableModel.getValueAt(riga, 0); // Converte l'oggetto in Integer
                String statoTransazione = (String) transazioniTableModel.getValueAt(riga, 4);
                if (idTransazioneRow == transazione.getIdTransazione()) {
                    if (statoTransazione.equals("await")) {
                        transazioniTableModel.setValueAt("complate", riga, 4); // Imposta il nuovo stato
                        aggiornaQuantitaCarrello(transazione.getIdProdotto(), transazione.getQuantita());
                    }

                    System.out.println("Elemento trovato alla riga " + riga);

                    break;
                }
            }

            transazioniTableModel.fireTableDataChanged();


            System.out.println(" --> UI: Lista transazione aggiornato!!");

            // Aggiorna o inserisci prodotto nel carrello
        });


    }

    private Transazione trovaTransazione(int idTransazione) {
        for (Transazione transazione : listaTransazione) {
            if (transazione.getIdTransazione() == idTransazione) {
                return transazione;
            }
        }
        return null;
    }


    public void aggiornaQuantitaCarrello(int idProdotto, int quantitaAggiunta) {
        DefaultTableModel modelloCarrello = (DefaultTableModel) carrelloTable.getModel();
        Prodotto prodotto = trovaProdottoLista(idProdotto, prodottiCarrello);

        if (prodotto == null) {
            // Inserimento del nuovo prodotto nel carrello
            Prodotto nuovoProdotto = trovaProdottoLista(idProdotto, prodottiNegozio);
            if (nuovoProdotto == null) {
                System.out.println("Errore: Prodotto non presente nel negozio.");
                return;
            }
            this.prodottiCarrello.add(nuovoProdotto);

            modelloCarrello.addRow(new String[]{nuovoProdotto.getNome(), String.valueOf(quantitaAggiunta)});
        } else {
            // Aggiornamento della quantità del prodotto nel carrello
            for (int riga = 0; riga < modelloCarrello.getRowCount(); riga++) {
                String nomeProdotto = (String) carrelloTable.getValueAt(riga, 0);
                if (nomeProdotto.equals(prodotto.getNome())) {
                    int quantita = Integer.parseInt((String) carrelloTable.getValueAt(riga, 1));
                    System.out.println("Elemento trovato alla riga " + riga);
                    int nuovaQuantita = quantita + quantitaAggiunta;
                    modelloCarrello.setValueAt(String.valueOf(nuovaQuantita), riga, 1); // Aggiorna la quantità
                    break;
                }
            }
        }

        modelloCarrello.fireTableDataChanged();
    }


    private Prodotto trovaProdottoLista(int idProdotto, List<Prodotto> prodotti) {
        for (Prodotto prodotto : prodotti) {
            if (prodotto.getIdProdotto() == idProdotto) {
                return prodotto;
            }
        }
        return null;
    }

    public void addTransazioneAwait() {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel model = (DefaultTableModel) transazioniTable.getModel();

            for (Transazione transazione : listaTransazione) {

                for (Prodotto prodotto : prodottiNegozio) {
                    if (prodotto.getIdProdotto() == transazione.getIdProdotto()) {
                        Object[] rowData = {transazione.getIdTransazione(), prodotto.getNome(), prodotto.getPrezzo()+"€",
                                transazione.getQuantita(), "await"};
                        model.addRow(rowData);
                    }
                }

            }
            model.fireTableDataChanged();

            System.out.println(" --> UI: Lista transazione aggiunta!!");

        });

    }

    public void addSingleTransazioneAwait(Transazione transazione) {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel model = (DefaultTableModel) transazioniTable.getModel();

            for (Prodotto prodotto : prodottiNegozio) {
                if (prodotto.getIdProdotto() == transazione.getIdProdotto()) {
                    Object[] rowData = {transazione.getIdTransazione(), prodotto.getNome(), prodotto.getPrezzo()+"€",
                            transazione.getQuantita(), "await"};
                    model.addRow(rowData);
                }
            }
            model.fireTableDataChanged();

            System.out.println(" --> UI: Lista transazione aggiunta!!");

        });

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
        DefaultTableModel defaultTableModel = new DefaultTableModel(elementiTabella, colonneTabella);
        azzeraElementiTable(defaultTableModel);
        table.setModel(defaultTableModel);

        // Imposta l'allineamento al centro per tutte le colonne
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();

        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        JScrollPane tableScrollPane = new JScrollPane(table);
        return tableScrollPane;
    }


    public void allSetResponsiveTable() {
        articoliNegozioTable.setPreferredScrollableViewportSize(articoliNegozioTable.getPreferredSize());
        carrelloTable.setPreferredScrollableViewportSize(carrelloTable.getPreferredSize());
        transazioniTable.setPreferredScrollableViewportSize(transazioniTable.getPreferredSize());
    }


}
