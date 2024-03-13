package it.itsrizzoli.view;

import it.itsrizzoli.controller.ControllerClientNegozio;
import it.itsrizzoli.model.Prodotto;
import it.itsrizzoli.model.Transazione;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ClientNegozioInterfaccia extends JFrame {
    private JTable tblCarrello;
    private JTable tblNegozio;
    private JTable tblTransazioni;
    private JButton btnChangeIP;
    private JButton bntInvioTransazioni;
    private JLabel labelTitle;
    private JLabel labelCarrello;
    private JLabel labelNegozio;
    private JLabel labelStatoServer;
    private JLabel labelTransazioni;
    private JScrollPane scrolPanelNegozio;
    private JScrollPane scrollPanelCarrello;
    private JScrollPane scrollPanelTransazioni;
    private JPanel mainPanel;

    private boolean statoNegozioOnline = false;
    private ControllerClientNegozio controllerClientNegozio;
    private final String[] articoliNegozioColonne = {"Prodotto", "Prezzo", "Disponibile"};
    private final String[] carrelloColonne = {"Prodotto", "Quantità"};
    private final String[] transazioniColonne = {"Number", "Prodotto", "Prezzo", "Quantità", "Stato"};

    public ClientNegozioInterfaccia(String titolo) {
        inizializza(titolo);
    }

    public void setControllerClientNegozio(ControllerClientNegozio controllerClientNegozio) {
        this.controllerClientNegozio = controllerClientNegozio;
        attivaListenerBtn();
    }

    public void inizializza(String titolo) {
        setTitle(titolo);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(300, 400));

        setContentPane(mainPanel);
        SwingUtilities.invokeLater(() -> {

            // Creazione dei pannelli delle tabelle
            creaTabellaPanello(tblCarrello, carrelloColonne, scrollPanelCarrello);
            creaTabellaPanello(tblNegozio, articoliNegozioColonne, scrolPanelNegozio);
            creaTabellaPanello(tblTransazioni, transazioniColonne, scrollPanelTransazioni);


            Dimension labelSize = new Dimension(200, 30);
            Font largeFont = new Font("Arial", Font.BOLD, 30);
            Font smallFont = new Font("Arial", Font.BOLD, 14);
            setLabelProperties(labelTitle, labelSize, largeFont);
            setLabelProperties(labelCarrello, labelSize, smallFont);
            setLabelProperties(labelNegozio, labelSize, smallFont);
            setLabelProperties(labelStatoServer, labelSize, smallFont);
            setLabelProperties(labelTransazioni, labelSize, smallFont);


            pack();
            setLocationRelativeTo(null);

            setVisible(true);
            System.out.println(" --- FINE THREAD_SWING_EDT ---");
        });
    }

    public void aggiornaStatoNegozio(boolean statoNegozioOnline) {
        this.statoNegozioOnline = statoNegozioOnline;
        changeTitle();
    }

    private void attivaListenerBtn() {
        bntInvioTransazioni.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (statoNegozioOnline) {
                    // Avvia un thread per l'invio di json al server
                    controllerClientNegozio.startThreadTransazioni();
                } else {
                    JOptionPane.showMessageDialog(null, "Attenzione: nessuna connessione al server!");
                }
            }
        });
        btnChangeIP.addActionListener(e -> {
            // Azione da eseguire quando il bottone viene premuto
            new ChangeIP(controllerClientNegozio.getClientConnessione());
            System.out.println(" CLICK: CHANGE btn IP");
        });
    }

    private void creaTabellaPanello(JTable table, String[] colonneTabella, JScrollPane jScrollPane) {
        DefaultTableModel defaultTableModel = new DefaultTableModel(colonneTabella, 0);
        table.setModel(defaultTableModel);
        table.setEnabled(false);        // Imposta l'allineamento al centro per tutte le colonne
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        jScrollPane.setViewportView(table);
    }

    public void addSingleTransazioneAwait(Transazione transazione, List<Prodotto> prodottiNegozio) {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel model = (DefaultTableModel) tblTransazioni.getModel();

            for (Prodotto prodotto : prodottiNegozio) {
                if (prodotto.getIdProdotto() == transazione.getIdProdotto()) {
                    Object[] rowData = {transazione.getIdTransazione(), prodotto.getNome(), prodotto.getPrezzo() +
                            "€", transazione.getQuantita(), "Attesa risposta"};
                    model.addRow(rowData);
                }
            }
            model.fireTableDataChanged();

            System.out.println(" --> UI: Lista transazione aggiunta!!");

        });

    }

    public void aggiornaStatoTransazioneInTabella(Transazione transazione, List<Prodotto> prodottiNegozio,
                                                  List<Prodotto> prodottiCarrello) {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel transazioniTableModel = (DefaultTableModel) tblTransazioni.getModel();
            // Aggiorna il valore nella colonna "Stato" alla riga
            for (int riga = 0; riga < transazioniTableModel.getRowCount(); riga++) {
                int idTransazioneRow = (int) transazioniTableModel.getValueAt(riga, 0); // Converte l'oggetto in Integer
                String statoTransazione = (String) transazioniTableModel.getValueAt(riga, 4);
                if (idTransazioneRow == transazione.getIdTransazione()) {
                    if (statoTransazione.equals("Attesa risposta")) {
                        transazioniTableModel.setValueAt("Richiesta accettata", riga, 4); // Imposta il nuovo stato
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


            DefaultTableModel transazioniTableModel = (DefaultTableModel) tblTransazioni.getModel();
            // Aggiorna il valore nella colonna "Stato" alla riga
            for (int riga = 0; riga < transazioniTableModel.getRowCount(); riga++) {
                int idTransazioneRow = (int) transazioniTableModel.getValueAt(riga, 0); // Converte l'oggetto in Integer
                String statoTransazione = (String) transazioniTableModel.getValueAt(riga, 4);
                if (idTransazioneRow == transazione.getIdTransazione()) {
                    if (statoTransazione.equals("Attesa risposta")) {
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
        DefaultTableModel modelloCarrello = (DefaultTableModel) tblCarrello.getModel();
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
                String nomeProdotto = (String) tblNegozio.getValueAt(riga, 0);
                if (nomeProdotto.equals(prodotto.getNome())) {
                    int quantita = Integer.parseInt((String) tblCarrello.getValueAt(riga, 1));
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
            DefaultTableModel tableProdottiNegozio = (DefaultTableModel) tblNegozio.getModel();
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
        labelStatoServer.setText("server: " + (statoNegozioOnline ? "Online" : "Offline"));
    }

    public void allSetResponsiveTable() {
        tblNegozio.setPreferredScrollableViewportSize(tblNegozio.getPreferredSize());
        tblCarrello.setPreferredScrollableViewportSize(tblCarrello.getPreferredSize());
        tblTransazioni.setPreferredScrollableViewportSize(tblTransazioni.getPreferredSize());
    }

    private static void azzeraElementiTable(DefaultTableModel defaultTableModel) {
        defaultTableModel.setRowCount(0);
        defaultTableModel.fireTableDataChanged();
    }

    private Prodotto trovaProdottoLista(int idProdotto, List<Prodotto> prodotti) {
        for (Prodotto prodotto : prodotti) {
            if (prodotto.getIdProdotto() == idProdotto) {
                return prodotto;
            }
        }
        return null;
    }

    private void setLabelProperties(JLabel label, Dimension size, Font font) {
        label.setPreferredSize(size);
        label.setFont(font);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}

