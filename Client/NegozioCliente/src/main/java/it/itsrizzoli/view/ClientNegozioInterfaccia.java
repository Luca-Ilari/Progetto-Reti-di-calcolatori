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
    private JTable tblTransazioniAcquisto;
    private JButton btnChangeIP;
    private JButton btnCompraProdotti;
    private JButton btnVendiProdotti;

    private JLabel labelTitle;
    private JLabel labelStatoServer;

    private JScrollPane scrolPanelNegozio;
    private JScrollPane scrollPanelCarrello;
    private JScrollPane scrollPanelTransazioniAcquisto;
    private JPanel mainPanel;
    private JTable tblTransazioniVendita;
    private JScrollPane scrollPanelTransazioniVendita;
    private JButton btnSwitchUtente;
    private JProgressBar progressBar;


    private boolean statoNegozioOnline = false;
    private ControllerClientNegozio controllerClientNegozio;
    private final String[] articoliNegozioColonne = {"Prodotto", "Prezzo", "Disponibile"};
    private final String[] carrelloColonne = {"Prodotto", "Quantità"};
    private final String[] transazioniColonne = {"Number", "Prodotto", "Prezzo", "Quantità", "Stato"};


    public final static int MAX_QUANTITA = 10_000;
    public static final String WAITING_CONFIRMATION = "In attesa";
    public static final String SUCCESS_RESPONSE = "Completato";
    public static final String PRODUCT_FINISHED_NEGOZIO = "Esaurito (negoziante)";
    public static final String PRODUCT_FINISHED_CLIENTE = "Esaurito (cliente)";

    // esaurito per il cliente


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
        setSize(new Dimension(300, 400));

        if (isNullPanelMain()) return;


        setContentPane(mainPanel);
        SwingUtilities.invokeLater(() -> {

            setProperietaProgressBar(progressBar, MAX_QUANTITA);

            // Creazione dei pannelli delle tabelle
            creaTabellaPanello(tblCarrello, carrelloColonne, scrollPanelCarrello);
            creaTabellaPanello(tblNegozio, articoliNegozioColonne, scrolPanelNegozio);
            creaTabellaPanello(tblTransazioniAcquisto, transazioniColonne, scrollPanelTransazioniAcquisto);
            creaTabellaPanello(tblTransazioniVendita, transazioniColonne, scrollPanelTransazioniVendita);

            Dimension labelSize = new Dimension(200, 30);
            Font largeFont = new Font("Arial", Font.BOLD, 30);
            Font smallFont = new Font("Arial", Font.BOLD, 14);
            setLabelProperties(labelTitle, labelSize, largeFont);
            setLabelProperties(labelStatoServer, labelSize, smallFont);
            setProprietaButton(Color.GRAY, Color.WHITE, btnChangeIP);
            setProprietaButton(Color.GRAY, Color.WHITE, btnSwitchUtente);
            setProprietaButton(Color.GREEN, Color.WHITE, btnCompraProdotti);
            setProprietaButton(Color.RED, Color.WHITE, btnVendiProdotti);

            pack();
            setLocationRelativeTo(null);

            setVisible(true);
            System.out.println(" --- FINE THREAD_SWING_EDT ---");
        });
    }

    private void setProperietaProgressBar(JProgressBar progressBar, int max) {
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(0, 102, 204)); // Colore della barra di avanzamento
        progressBar.setBackground(Color.LIGHT_GRAY); // Colore dello sfondo della progress bar
        progressBar.setBorderPainted(false);
        progressBar.setMaximum(max);
    }

    public synchronized void updateProgressBar(int quantita) {
        int currentValue = progressBar.getValue();
        int newValue = currentValue + quantita;

        newValue = Math.max(newValue, 0);

        newValue = Math.min(newValue, MAX_QUANTITA);

        progressBar.setString(newValue + " / " + MAX_QUANTITA + " Prodotti");
        if (MAX_QUANTITA == newValue) {
            progressBar.setString("Spazio finito!");
        }

        progressBar.setValue(newValue);
    }


    public int getQuantita() {
        return progressBar.getValue();
    }

    private boolean isNullPanelMain() {
        if (mainPanel == null) {
            SwingUtilities.invokeLater(() -> {

                Panel panelError = new Panel();
                JLabel labelError = new JLabel("ERRORE nel caricamento mainPanel - " + this.getClass().getName());


                panelError.add(labelError);
                setContentPane(panelError);

                pack();
                setLocationRelativeTo(null);

                setVisible(true);

                System.exit(0);
            });
            return true;
        }
        return false;
    }

    public void aggiornaStatoNegozio(boolean statoNegozioOnline) {
        this.statoNegozioOnline = statoNegozioOnline;
        changeTitle();
    }

    private void attivaListenerBtn() {
        btnCompraProdotti.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!statoNegozioOnline) {
                    JOptionPane.showMessageDialog(null, "Attenzione: nessuna connessione al server!");


                }
                // Avvia un thread per l'invio di json al server
                if (progressBar.getValue() >= MAX_QUANTITA) {
                    JOptionPane.showMessageDialog(null, "Quantità massima raggiunta", "Attenzione",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                controllerClientNegozio.startThreadCompraProdotti();

                try {
                    setEnabled(false);
                    Thread.sleep(1_000);
                    setEnabled(true);
                } catch (InterruptedException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });


        btnVendiProdotti.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!statoNegozioOnline) {
                    JOptionPane.showMessageDialog(null, "Attenzione: nessuna connessione al server!");
                    return;
                }
                // Avvia un thread per l'invio di json al server
                if (progressBar.getValue() <= 0) {
                    JOptionPane.showMessageDialog(null, "Quantità minima raggiunta", "Attenzione",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                controllerClientNegozio.startThreadVendiProdotti();

                try {
                    setEnabled(false);
                    Thread.sleep(1_000);
                    setEnabled(true);
                } catch (InterruptedException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });

        btnChangeIP.addActionListener(e -> {
            setEnabled(false);
            new ChangeIP(controllerClientNegozio.getClientConnessione(), this);
            System.out.println(" CLICK: CHANGE btn IP");
        });

        btnSwitchUtente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                controllerClientNegozio.getClientConnessione().chiusuraConnessione();

                SchermoCustomVendita schermoCustomVendita = new SchermoCustomVendita(controllerClientNegozio);
                schermoCustomVendita.setStatoNegozioOnline(statoNegozioOnline);
                schermoCustomVendita.changeTitle();


            }
        });


    }

    private void setProprietaButton(Color backgroundColor, Color textColor, JButton button) {
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(80, 40));
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setBackground(backgroundColor);

        // Aggiunta di un effetto di ombreggiatura al passaggio del mouse
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(new Color(0, 102, 204)); // Cambia il colore del testo al passaggio del mouse
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(Color.WHITE); // Ripristina il colore del testo al mouse out
            }
        });

        // Aggiunta dell'azione al pulsante
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Aggiungere l'azione desiderata qui
            }
        });

    }

    private void creaTabellaPanello(JTable table, String[] colonneTabella, JScrollPane jScrollPane) {
        DefaultTableModel defaultTableModel = new DefaultTableModel(colonneTabella, 0);
        table.setModel(defaultTableModel);
        table.setEnabled(false);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        jScrollPane.setViewportView(table);
    }

    public void addSingleTransazioneCompraAwait(Transazione transazione, List<Prodotto> prodottiNegozio) {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel model = (DefaultTableModel) tblTransazioniAcquisto.getModel();

            for (Prodotto prodotto : prodottiNegozio) {
                if (prodotto.getIdProdotto() == transazione.getIdProdotto()) {
                    Object[] rowData = {transazione.getIdTransazione(), prodotto.getNome(), prodotto.getPrezzo() +
                            "€", transazione.getQuantita(), WAITING_CONFIRMATION};
                    model.addRow(rowData);
                }
            }
            model.fireTableDataChanged();


        });

    }

    public synchronized void addAllTransazioneCompraAwait(List<Transazione> transazioneList,
                                                          List<Prodotto> prodottiNegozio) {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel model = (DefaultTableModel) tblTransazioniAcquisto.getModel();

            for (Transazione transazione : transazioneList) {
                for (Prodotto prodotto : prodottiNegozio) {
                    if (prodotto.getIdProdotto() == transazione.getIdProdotto()) {
                        Object[] rowData = {transazione.getIdTransazione(), prodotto.getNome(),
                                prodotto.getPrezzo() + "€", transazione.getQuantita(), WAITING_CONFIRMATION};
                        model.addRow(rowData);
                    }
                }

            }
            model.fireTableDataChanged();

            System.out.println(" --> UI: Lista transazione aggiunta!!");

        });

    }

    public void addAllTransazioneVenditaAwait(List<Transazione> transazioneList, List<Prodotto> prodottiCarrello) {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel model = (DefaultTableModel) tblTransazioniVendita.getModel();

            for (Transazione transazione : transazioneList) {
                for (Prodotto prodotto : prodottiCarrello) {
                    if (prodotto.getIdProdotto() == transazione.getIdProdotto()) {
                        Object[] rowData = {transazione.getIdTransazione(), prodotto.getNome(),
                                prodotto.getPrezzo() + "€", transazione.getQuantita(), WAITING_CONFIRMATION};
                        model.addRow(rowData);
                    }
                }
            }
            model.fireTableDataChanged();

            System.out.println(" --> UI: Lista transazione aggiunta!!");

        });

    }

    public void addSingleTransazioneVenditaAwait(Transazione transazione, List<Prodotto> prodottiCarrello) {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel model = (DefaultTableModel) tblTransazioniVendita.getModel();

            for (Prodotto prodotto : prodottiCarrello) {
                if (prodotto.getIdProdotto() == transazione.getIdProdotto()) {
                    Object[] rowData = {transazione.getIdTransazione(), prodotto.getNome(), prodotto.getPrezzo() +
                            "€", transazione.getQuantita(), WAITING_CONFIRMATION};
                    model.addRow(rowData);
                }
            }
            model.fireTableDataChanged();

            System.out.println(" --> UI: Lista transazione aggiunta!!");

        });

    }

    public void aggiornaStatoTransazioneInTabellaAcquisto(Transazione transazione, List<Prodotto> prodottiNegozio,
                                                          List<Prodotto> prodottiCarrello) {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel transazioniTableModel = (DefaultTableModel) tblTransazioniAcquisto.getModel();
            // Aggiorna il valore nella colonna "Stato" alla riga
            for (int riga = 0; riga < transazioniTableModel.getRowCount(); riga++) {
                int idTransazioneRow = (int) transazioniTableModel.getValueAt(riga, 0); // Converte l'oggetto in Integer
                String statoTransazione = (String) transazioniTableModel.getValueAt(riga, 4);
                if (idTransazioneRow == transazione.getIdTransazione()) {
                    if (statoTransazione.equals(WAITING_CONFIRMATION)) {
                        transazioniTableModel.setValueAt(SUCCESS_RESPONSE, riga, 4); // Imposta il nuovo stato
                        incrementaQuantitaCarrello(transazione.getIdProdotto(), transazione.getQuantita(),
                                prodottiNegozio, prodottiCarrello);
                        updateProgressBar(transazione.getQuantita());
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

    public void aggiornaStatoTransazioneInTabellaVendita(Transazione transazione, List<Prodotto> prodottiCarrello) {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel transazioniTableModel = (DefaultTableModel) tblTransazioniVendita.getModel();
            // Aggiorna il valore nella colonna "Stato" alla riga
            for (int riga = 0; riga < transazioniTableModel.getRowCount(); riga++) {
                int idTransazioneRow = (int) transazioniTableModel.getValueAt(riga, 0); // Converte l'oggetto in Integer
                String statoTransazione = (String) transazioniTableModel.getValueAt(riga, 4);
                if (idTransazioneRow == transazione.getIdTransazione()) {
                    if (statoTransazione.equals(WAITING_CONFIRMATION)) {
                        transazioniTableModel.setValueAt(SUCCESS_RESPONSE, riga, 4); // Imposta il nuovo stato
                        decrementaQuantitaCarrello(transazione.getIdProdotto(), transazione.getQuantita(),
                                prodottiCarrello);
                        updateProgressBar(-transazione.getQuantita());
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

    public void aggiornaStateTransazioneFail(Transazione transazione, boolean isVendita) {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel transazioniTableModel;

            if (!isVendita) {
                transazioniTableModel = (DefaultTableModel) tblTransazioniAcquisto.getModel();
            } else {
                transazioniTableModel = (DefaultTableModel) tblTransazioniVendita.getModel();
            }
            // Aggiorna il valore nella colonna "Stato" alla riga
            for (int riga = 0; riga < transazioniTableModel.getRowCount(); riga++) {
                int idTransazioneRow = (int) transazioniTableModel.getValueAt(riga, 0); // Converte l'oggetto in Integer
                String statoTransazione = (String) transazioniTableModel.getValueAt(riga, 4);
                if (idTransazioneRow == transazione.getIdTransazione()) {
                    if (statoTransazione.equals(WAITING_CONFIRMATION)) {

                        if (isVendita) {
                            transazioniTableModel.setValueAt(PRODUCT_FINISHED_CLIENTE, riga, 4); // Imposta il nuovo
                            // stato
                        } else {
                            transazioniTableModel.setValueAt(PRODUCT_FINISHED_NEGOZIO, riga, 4); // Imposta il nuovo
                            // stato
                        }
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
                                                      List<Prodotto> prodottiNegozio, List<Prodotto> prodottiCarrello
            , boolean controllaCarrello) {
        DefaultTableModel modelloCarrello = (DefaultTableModel) tblCarrello.getModel();
        Prodotto prodotto = null;

        if (controllaCarrello) {
            prodotto = trovaProdottoLista(idProdotto, prodottiCarrello);
        }
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

    public synchronized void incrementaQuantitaCarrello(int idProdotto, int quantitaAggiunta,
                                                        List<Prodotto> prodottiNegozio,
                                                        List<Prodotto> prodottiCarrello) {
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

    public synchronized void decrementaQuantitaCarrello(int idProdotto, int quantitaTogliere,
                                                        List<Prodotto> prodottiCarrello) {
        DefaultTableModel modelloCarrello = (DefaultTableModel) tblCarrello.getModel();
        Prodotto prodotto = trovaProdottoLista(idProdotto, prodottiCarrello);

        if (prodotto == null) {
            System.out.println(" Attenzione: Prodotto non trovato nel carrello");
            return;
        }
        // Aggiornamento della quantità del prodotto nel carrello
        for (int riga = 0; riga < modelloCarrello.getRowCount(); riga++) {
            String nomeProdotto = (String) tblNegozio.getValueAt(riga, 0);
            if (nomeProdotto.equals(prodotto.getNome())) {
                System.out.println("Elemento trovato alla riga " + riga);

                int quantitaDisponibile = Integer.parseInt((String) tblCarrello.getValueAt(riga, 1));
                int nuovaQuantita = quantitaDisponibile - quantitaTogliere;

                if (nuovaQuantita < 0) {
                    System.out.println(" Attenzione: la quantità richieste non è disponibile ");
                    return;
                }

                modelloCarrello.setValueAt(String.valueOf(nuovaQuantita), riga, 1); // Aggiorna la quantità
                break;
            }
        }


        modelloCarrello.fireTableDataChanged();
    }

    public void aggiornaTabellaProdottiNegozio(List<Prodotto> newProdottiNegozio) {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel model = (DefaultTableModel) tblNegozio.getModel();
            model.setRowCount(0);
            // Aggiungi righe per ciascun prodotto nella lista
            for (Prodotto product : newProdottiNegozio) {
                Object[] rowData = {product.getNome(), product.getPrezzo() + "€", product.getQuantitaDisponibile()};
                model.addRow(rowData);
            }
            model.fireTableDataChanged();

            System.out.println(" --> UI: Tabella dei prodotti nel negozio aggiornata!!");
        });
    }

    public void changeTitle() {
        labelStatoServer.setText(statoNegozioOnline ? "Online" : "Offline");
        labelStatoServer.setForeground(statoNegozioOnline ? Color.GREEN : Color.RED);
    }

    public void allLimitResponsiveTable(JTable table) {
        Dimension preferredSize = table.getPreferredSize();
        int maxWidth = 400; // Imposta la larghezza massima desiderata

        if (preferredSize.width > maxWidth) {
            preferredSize.width = maxWidth;
        }
        table.setPreferredScrollableViewportSize(preferredSize);
    }

    public static void azzeraElementiTable(DefaultTableModel defaultTableModel) {
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


}

