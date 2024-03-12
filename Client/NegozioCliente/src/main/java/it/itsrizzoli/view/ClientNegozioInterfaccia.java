package it.itsrizzoli.view;

import it.itsrizzoli.controller.ControllerClientNegozio;
import it.itsrizzoli.model.Prodotto;
import it.itsrizzoli.model.Transazione;
import it.itsrizzoli.tcpip.ThreadClient;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import static it.itsrizzoli.tools.TypeThread.THREAD_WRITE_TRANSAZIONI;

public class ClientNegozioInterfaccia extends JFrame {
    private boolean statoNegozioOnline = false;
    private JButton actionButton;
    private JLabel titleLabel = new JLabel();
    private JTable tblProdottiCarrello = new JTable();
    private JTable tblProdottiNegozio = new JTable();
    private JTable tblTransazione = new JTable();

    private final String[] articoliNegozioColonne = {"Prodotto", "Prezzo", "Disponibile"};
    private final String[] carrelloColonne = {"Prodotto", "Quantità"};
    private final String[] transazioniColonne = {"Number", "Prodotto", "Prezzo", "Quantità", "Stato"};


    private ControllerClientNegozio controllerClientNegozio;

    public ClientNegozioInterfaccia(String titolo) {
        setTitle(titolo);
    }

    public void aggiornaStatoNegozio(boolean statoNegozioOnline) {
        this.statoNegozioOnline = statoNegozioOnline;
        changeTitle();
    }

    public void setControllerClientNegozio(ControllerClientNegozio controllerClientNegozio) {
        this.controllerClientNegozio = controllerClientNegozio;
    }

    public void inizzalizza() {
        SwingUtilities.invokeLater(() -> {

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setMinimumSize(new Dimension(300, 400));

            // Creazione del pannello principale
            JPanel contentPane = new JPanel();
            contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
            contentPane.add(createTitleLabel());


            contentPane.add(createLabelTableCarrelloNegozio());

            JPanel tablesPanel = new JPanel();
            tablesPanel.setLayout(new BoxLayout(tablesPanel, BoxLayout.X_AXIS));

            // Aggiungi la prima tabella
            String[][] carrelloData = {{"Prodotto 1", "1"}, {"Prodotto 2", "2"}};
            JScrollPane jScrollPane1 = creaTabellaPanello(tblProdottiCarrello, carrelloColonne, carrelloData);
            tablesPanel.add(jScrollPane1);

            // Aggiungi uno spazio tra le tabelle
            tablesPanel.add(Box.createRigidArea(new Dimension(10, 0)));

            // Aggiungi la seconda tabella
            String[][] articoliNegozioData = {{"Prodotto A", "10", "50.00"}, {"Prodotto B", "5", "120.00"}};
            JScrollPane jScrollPane2 = creaTabellaPanello(tblProdottiNegozio, articoliNegozioColonne,
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
            JScrollPane jScrollPane3 = creaTabellaPanello(tblTransazione, transazioniColonne, transazioniData);
            // DefaultTableModel defaultTableModel = (DefaultTableModel) transazioniTable.getModel();
            transazioniPanel.add(jScrollPane3);
            contentPane.add(transazioniPanel);

            JButton button = new JButton("Invia richieste random");
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (statoNegozioOnline) {
                        // Avvia un thread per l'invio di json al server
                        controllerClientNegozio.startThreadTransazioni();
                        JOptionPane.showMessageDialog(contentPane, "Richiesta inviata!");
                    } else {
                        JOptionPane.showMessageDialog(contentPane, "Attenzione: nessun connessione al server!");
                    }
                }
            });

            contentPane.add(button);
            setContentPane(contentPane);

            allSetResponsiveTable();
            pack();
            setLocationRelativeTo(null);
            System.out.println(" --- FINE THREAD_SWING_EDT ---");
        });
    }


    private Prodotto trovaProdottoLista(int idProdotto, List<Prodotto> prodotti) {
        for (Prodotto prodotto : prodotti) {
            if (prodotto.getIdProdotto() == idProdotto) {
                return prodotto;
            }
        }
        return null;
    }


    public void addSingleTransazioneAwait(Transazione transazione, List<Prodotto> prodottiNegozio) {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel model = (DefaultTableModel) tblTransazione.getModel();

            for (Prodotto prodotto : prodottiNegozio) {
                if (prodotto.getIdProdotto() == transazione.getIdProdotto()) {
                    Object[] rowData = {transazione.getIdTransazione(), prodotto.getNome(), prodotto.getPrezzo() +
                            "€", transazione.getQuantita(), "await"};
                    model.addRow(rowData);
                }
            }
            model.fireTableDataChanged();

            System.out.println(" --> UI: Lista transazione aggiunta!!");

        });

    }

    private JLabel createTitleLabel() {
        titleLabel = new JLabel("Gestione Ordini - Negozio Offline");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 25));
        return titleLabel;
    }


    private JPanel createLabelTableCarrelloNegozio() {

        JPanel labelsPanel = new JPanel();
        labelsPanel.setLayout(new BoxLayout(labelsPanel, BoxLayout.LINE_AXIS));


        JLabel carrelloLabel = new JLabel("Mio Carrello ");
        carrelloLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel articoliNegozioLabel = new JLabel("Articoli negozio");
        articoliNegozioLabel.setFont(new Font("Arial", Font.BOLD, 14));

        labelsPanel.add(carrelloLabel);
        labelsPanel.add(Box.createHorizontalGlue());
        labelsPanel.add(articoliNegozioLabel);
        labelsPanel.add(Box.createHorizontalGlue());


        actionButton = new JButton("Change Ip");
        actionButton.addActionListener(e -> {
            // Azione da eseguire quando il bottone viene premuto
            new ChangeIP(controllerClientNegozio.getClientConnessione());
            System.out.println(" CLICK: CHANGE btn IP");
        });
        labelsPanel.add(actionButton);
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
        return new JScrollPane(table);
    }

    public void aggiornaStatoTransazioneInTabella(Transazione transazione, List<Prodotto> prodottiNegozio,
                                                  List<Prodotto> prodottiCarrello) {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel transazioniTableModel = (DefaultTableModel) tblTransazione.getModel();
            // Aggiorna il valore nella colonna "Stato" alla riga
            for (int riga = 0; riga < transazioniTableModel.getRowCount(); riga++) {
                int idTransazioneRow = (int) transazioniTableModel.getValueAt(riga, 0); // Converte l'oggetto in Integer
                String statoTransazione = (String) transazioniTableModel.getValueAt(riga, 4);
                if (idTransazioneRow == transazione.getIdTransazione()) {
                    if (statoTransazione.equals("await")) {
                        transazioniTableModel.setValueAt("ok", riga, 4); // Imposta il nuovo stato
                        aggiornaQuantitaCarrello(transazione.getIdProdotto(), transazione.getQuantita(),
                                prodottiNegozio, prodottiCarrello);
                    }

                    System.out.println("Elemento trovato alla riga " + riga);

                    break;
                }
            }

            transazioniTableModel.fireTableDataChanged();


            System.out.println(" --> UI: Lista transazione aggiornato!!");

            // allSetResponsiveTable();
        });

    }


    public void aggiornaStateTransazioneFail(Transazione transazione) {
        SwingUtilities.invokeLater(() -> {


            DefaultTableModel transazioniTableModel = (DefaultTableModel) tblTransazione.getModel();
            // Aggiorna il valore nella colonna "Stato" alla riga
            for (int riga = 0; riga < transazioniTableModel.getRowCount(); riga++) {
                int idTransazioneRow = (int) transazioniTableModel.getValueAt(riga, 0); // Converte l'oggetto in Integer
                String statoTransazione = (String) transazioniTableModel.getValueAt(riga, 4);
                if (idTransazioneRow == transazione.getIdTransazione()) {
                    if (statoTransazione.equals("await")) {
                        transazioniTableModel.setValueAt("Prodotto finito!!", riga, 4); // Imposta il nuovo stato
                        System.out.println("Elemento trovato alla riga " + riga);

                        break;
                    }
                }
            }

            transazioniTableModel.fireTableDataChanged();


            System.out.println(" --> UI: Lista transazione aggiornato!!");

            // Aggiorna o inserisci prodotto nel carrello
        });


    }


    public synchronized void aggiornaQuantitaCarrello(int idProdotto, int quantitaAggiunta,
                                                      List<Prodotto> prodottiNegozio, List<Prodotto> prodottiCarrello) {
        DefaultTableModel modelloCarrello = (DefaultTableModel) tblProdottiCarrello.getModel();
        Prodotto prodotto = trovaProdottoLista(idProdotto, prodottiCarrello);

        if (prodotto == null) {
            // Inserimento del nuovo prodotto nel carrello
            Prodotto nuovoProdotto = trovaProdottoLista(idProdotto, prodottiNegozio);
            if (nuovoProdotto == null) {
                System.out.println("Errore: Prodotto non presente nel negozio.");
                return;
            }
            prodottiCarrello.add(nuovoProdotto);

            modelloCarrello.addRow(new String[]{nuovoProdotto.getNome(), String.valueOf(quantitaAggiunta)});
        } else {
            // Aggiornamento della quantità del prodotto nel carrello
            for (int riga = 0; riga < modelloCarrello.getRowCount(); riga++) {
                String nomeProdotto = (String) tblProdottiCarrello.getValueAt(riga, 0);
                if (nomeProdotto.equals(prodotto.getNome())) {
                    int quantita = Integer.parseInt((String) tblProdottiCarrello.getValueAt(riga, 1));
                    System.out.println("Elemento trovato alla riga " + riga);
                    int nuovaQuantita = quantita + quantitaAggiunta;
                    modelloCarrello.setValueAt(String.valueOf(nuovaQuantita), riga, 1); // Aggiorna la quantità
                    break;
                }
            }
        }

        modelloCarrello.fireTableDataChanged();
    }

    public void aggiornaTabellaProdottiNegozio(List<Prodotto> newProdottiNegozio) {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel tableProdottiNegozio = (DefaultTableModel) tblProdottiNegozio.getModel();
            tableProdottiNegozio.setRowCount(0);
            // Aggiungi righe per ciascun prodotto nella lista
            for (Prodotto product : newProdottiNegozio) {
                Object[] rowData = {product.getNome(), product.getPrezzo() + "€", product.getQuantitaDisponibile()};
                tableProdottiNegozio.addRow(rowData);
            }
            tableProdottiNegozio.fireTableDataChanged();

            System.out.println(" --> UI: Tabella dei prodotti nel negozio aggiornata!!");
        });
    }

    public void changeTitle() {
        String titolo = "Gestione Ordini - Negozio " + (statoNegozioOnline ? "Online" : "Offline");
        titleLabel.setText(titolo);
    }

    public void allSetResponsiveTable() {
        tblProdottiNegozio.setPreferredScrollableViewportSize(tblProdottiNegozio.getPreferredSize());
        tblProdottiCarrello.setPreferredScrollableViewportSize(tblProdottiCarrello.getPreferredSize());
        tblTransazione.setPreferredScrollableViewportSize(tblTransazione.getPreferredSize());
    }

    private static void azzeraElementiTable(DefaultTableModel defaultTableModel) {
        defaultTableModel.setRowCount(0);
        defaultTableModel.fireTableDataChanged();
    }


}
