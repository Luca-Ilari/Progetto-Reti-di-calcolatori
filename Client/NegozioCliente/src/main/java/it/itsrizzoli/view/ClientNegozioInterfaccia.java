package it.itsrizzoli.view;

import it.itsrizzoli.controller.ControllerClientNegozio;
import it.itsrizzoli.model.Prodotto;
import it.itsrizzoli.model.Transazione;
import it.itsrizzoli.tools.CodiciStatoServer;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Random;

public class ClientNegozioInterfaccia extends JFrame implements Runnable {
    private JTable tblCarrello;
    private JTable tblNegozio;
    private JTable tblTransazioniAcquisto;
    private JButton btnChangeIP;
    private JLabel labelStatoServer;
    private JScrollPane scrolPanelNegozio;
    private JScrollPane scrollPanelCarrello;
    private JScrollPane scrollPanelTransazioniAcquisto;
    private JPanel mainPanel;
    private JTable tblTransazioniVendita;
    private JScrollPane scrollPanelTransazioniVendita;
    private JButton btnCreaTransazioni;
    private JTextField inputIdProdotto;
    private JTextField inputQuantita;
    private JTextField inputViaggi;
    private JTextField inputNome;
    private JButton btnInviaTransazione;
    private JRadioButton radioBtnCompra;
    private JRadioButton radioBtnVendi;
    private JPanel panelCreaTransazione;
    private JProgressBar progressBarTransazioni;
    private JProgressBar progressBarQuantita;
    private JButton btnStopTransazioni;
    private JPanel panelMaxQuantita;


    private boolean statoNegozioOnline = false;
    private ControllerClientNegozio controllerClientNegozio;
    private final String[] articoliNegozioColonne = {"Id", "Prodotto", "Prezzo", "Disponibile"};
    private final String[] carrelloColonne = {"Prodotto", "Quantità"};
    private final String[] transazioniColonne = {"Number", "Prodotto", "Prezzo", "Quantità", "Stato"};


    public final static int MAX_QUANTITA = 100_000;
    public static final String WAITING_CONFIRMATION = "In attesa";
    public static final String SUCCESS_RESPONSE = "Completato";
    public static final String PRODUCT_FINISHED_NEGOZIO = "Esaurito (negoziante)";
    public static final String PRODUCT_FINISHED_CLIENTE = "Esaurito (cliente)";
    public static final String QUANTITA_MASSIMA_RAGGIUNTA = "Error: max quantità";
    public static final String QUANTITA_MINIMIA_RAGGIUNTA = "Error: min quantità";

    // esaurito per il cliente


    public ClientNegozioInterfaccia(String titolo) {
        inizializza(titolo);

    }

    public void setControllerClientNegozio(ControllerClientNegozio controllerClientNegozio) {
        this.controllerClientNegozio = controllerClientNegozio;
        attivaListenerBtn();
    }

    public void inizializza(String titolo) {

        if (isNullPanelMain()) return;


        SwingUtilities.invokeLater(() -> {
            setTitle(titolo);

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setPreferredSize(new Dimension(1400, 800));

            setContentPane(mainPanel);
            setProperietaProgressBar(progressBarQuantita, MAX_QUANTITA, Color.ORANGE);
            setProperietaProgressBar(progressBarTransazioni, 100, Color.BLUE);
            progressBarTransazioni.setString("pronto!");

            panelCreaTransazione.setVisible(false);

            // Creazione di un bordo con il titolo desiderato
            TitledBorder border = (TitledBorder) panelMaxQuantita.getBorder();

            String max = String.valueOf(MAX_QUANTITA);



            border.setTitle(border.getTitle() + max);
            panelMaxQuantita.setBorder(border);
            // Creazione dei pannelli delle tabelle
            creaTabellaPanello(tblCarrello, carrelloColonne, scrollPanelCarrello);
            creaTabellaPanello(tblNegozio, articoliNegozioColonne, scrolPanelNegozio);
            creaTabellaPanello(tblTransazioniAcquisto, transazioniColonne, scrollPanelTransazioniAcquisto);
            creaTabellaPanello(tblTransazioniVendita, transazioniColonne, scrollPanelTransazioniVendita);

            impostaGestoreSelezioneRiga(tblNegozio);
            impostaGestoreSelezioneRiga(tblCarrello);

            Dimension labelSize = new Dimension(200, 30);
            Font smallFont = new Font("Arial", Font.BOLD, 14);
            setLabelProperties(labelStatoServer, labelSize, smallFont);
            setProprietaButton(Color.GRAY, Color.WHITE, btnChangeIP);
            setProprietaButton(new Color(0, 102, 204), Color.WHITE, btnCreaTransazioni);
            setProprietaButton(new Color(0, 153, 0), Color.WHITE, btnInviaTransazione);
            setProprietaButton(Color.red, Color.WHITE, btnStopTransazioni);

            btnStopTransazioni.setVisible(false);

            setProprietaRadioButton(radioBtnCompra);
            setProprietaRadioButton(radioBtnVendi);
            attivaListenerRadioButton();


            pack();
            setLocationRelativeTo(null);

            setVisible(true);
            System.out.println(" --- FINE THREAD_SWING_EDT ---");
        });
    }

    private void attivaListenerRadioButton() {
        radioBtnCompra.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (radioBtnCompra.isSelected()) {
                    radioBtnVendi.setSelected(false); // Disattiva il radio button per la vendita
                }
            }
        });

// Aggiungi un ActionListener al radio button per la vendita
        radioBtnVendi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (radioBtnVendi.isSelected()) {
                    radioBtnCompra.setSelected(false); // Disattiva il radio button per l'acquisto
                }
            }
        });
    }

    private void impostaGestoreSelezioneRiga(JTable table) {
        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    System.out.println("Hai cliccato sulla riga: " + selectedRow);
                    int idProdotto = (int) table.getValueAt(selectedRow, 0);
                    String nome = (String) table.getValueAt(selectedRow, 1);
                    inputIdProdotto.setText(String.valueOf(idProdotto));
                    inputNome.setText(nome);
                }
            }
        });
    }

    private void setProperietaProgressBar(JProgressBar progressBar, int max, Color color) {
        progressBar.setStringPainted(true);
        progressBar.setForeground(color); // Colore della barra di avanzamento
        progressBar.setBackground(Color.LIGHT_GRAY); // Colore dello sfondo della progress bar
        progressBar.setBorderPainted(false);
        progressBar.setMaximum(max);
    }

    private void setProprietaRadioButton(JRadioButton radioButton) {
        radioButton.setFocusPainted(false);
        radioButton.setFont(new Font("Arial", Font.PLAIN, 14));
        radioButton.setForeground(Color.BLACK);
        radioButton.setBackground(Color.WHITE);
        radioButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        radioButton.setBorderPainted(true);
        radioButton.setOpaque(false);
    }

    public synchronized int updateProgressBar(int quantita) {
        int currentValue = progressBarQuantita.getValue();
        int newValue = currentValue + quantita;

        newValue = Math.max(newValue, 0);

        newValue = Math.min(newValue, MAX_QUANTITA);

        progressBarQuantita.setString(newValue + " / " + MAX_QUANTITA + " Prodotti");

        progressBarQuantita.setValue(newValue);

        if (newValue == 0) {
            return -1;
        } else if (newValue == MAX_QUANTITA) {
            return +1;
        }
        return 0;

    }


    public synchronized int getQuantita() {
        System.out.println("progressBar.getValue() = " + progressBarQuantita.getValue());
        return progressBarQuantita.getValue();
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
        btnChangeIP.addActionListener(e -> {
            setEnabled(false);
            new ChangeIP(controllerClientNegozio.getClientConnessione(), this);
            System.out.println(" CLICK: CHANGE btn IP");
        });
        btnCreaTransazioni.addActionListener(e -> {

            panelCreaTransazione.setVisible(!panelCreaTransazione.isVisible());     // Inverte la visibilità del

            btnCreaTransazioni.setText(panelCreaTransazione.isVisible() ? "Chiudi Finestra" : "Compra - Vendi");

            // pannello
        });


        btnInviaTransazione.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (!controllerClientNegozio.getClientConnessione().onConnessione) {
                    JOptionPane.showMessageDialog(null, "Non sei connesso a un server!", "Errore",
                            JOptionPane.ERROR_MESSAGE);
                    return;

                }


                btnInviaTransazione.setEnabled(false);
                progressBarTransazioni.setValue(0);
                progressBarTransazioni.setString("pronti!");
                String idProdottoStr = inputIdProdotto.getText().trim();
                String nome = inputNome.getText().trim();
                String quantitaStr = inputQuantita.getText().trim();
                String viaggiStr = inputViaggi.getText().trim();

                if (idProdottoStr.isEmpty() || nome.isEmpty() || quantitaStr.isEmpty() || viaggiStr.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Per favore, inserisci tutti i campi.", "Avviso",
                            JOptionPane.WARNING_MESSAGE);
                    btnInviaTransazione.setEnabled(true);
                    return;
                }

                int idProdotto;
                int quantita;
                int viaggi;

                try {
                    idProdotto = Integer.parseInt(idProdottoStr);
                    quantita = Integer.parseInt(quantitaStr);
                    viaggi = Integer.parseInt(viaggiStr);

                    if (idProdotto < 0 || quantita <= 0 || viaggi <= 0) {
                        JOptionPane.showMessageDialog(null, "I valori devono essere maggiori di zero.", "Errore",
                                JOptionPane.ERROR_MESSAGE);
                        btnInviaTransazione.setEnabled(true);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    System.out.println(ex.getMessage());
                    JOptionPane.showMessageDialog(null, "Inserisci solo numeri validi nei campi.", "Errore",
                            JOptionPane.ERROR_MESSAGE);
                    btnInviaTransazione.setEnabled(true);
                    return;
                }

                boolean trovatoProdotto = false;
                for (Prodotto prodotto : controllerClientNegozio.getProdottiNegozio()) {
                    if (prodotto.getIdProdotto() == idProdotto) {
                        trovatoProdotto = true;
                        break;

                    }
                }
                if (!trovatoProdotto) {
                    JOptionPane.showMessageDialog(null, "Il prodotto selezionato non è disponibile nel negozio.",
                            "Prodotto non trovato", JOptionPane.WARNING_MESSAGE);
                    btnInviaTransazione.setEnabled(true);
                    return;
                }

                boolean isVendita = radioBtnVendi.isSelected();

                for (int i = 1; i <= viaggi; i++) {
                    int quantitaTotale = isVendita ? getQuantita() - (i * quantita) : getQuantita() + (i * quantita);
                    String messaggio = isVendita ? "La disponibilità della quantità delle transazioni supera cio che "
                            + "hai comprato!" :
                            "La somma delle quantità delle transazione supera il limite di  " + MAX_QUANTITA;

                    if ((isVendita && quantitaTotale < 0) || (!isVendita && quantitaTotale > MAX_QUANTITA)) {
                        String tipoMessaggio = isVendita ? "Quantità Minima" : "Quantità Massima";
                        JOptionPane.showMessageDialog(null,
                                messaggio + (quantitaTotale + quantita * viaggi) + "/" + MAX_QUANTITA, tipoMessaggio,
                                JOptionPane.WARNING_MESSAGE);
                        btnInviaTransazione.setEnabled(true);
                        return;
                    }
                }
                btnStopTransazioni.setVisible(true);
                btnInviaTransazione.setVisible(false);
                isStopThread = false;
                creaThreadInvioTransazione(viaggi, idProdotto, quantita, isVendita);

            }
        });

        btnStopTransazioni.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isStopThread = true;
                progressBarTransazioni.setString(progressBarTransazioni.getString() + " conclusi ");
            }
        });
    }

    private void setProprietaButton(Color backgroundColor, Color textColor, JButton button) {
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(80, 20));
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setBackground(backgroundColor);

        // Aggiunta di un effetto di ombreggiatura al passaggio del mouse
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(0, 128, 255)); // Cambia il colore di sfondo al passaggio del mouse
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(backgroundColor); // Ripristina il colore di sfondo al mouse out
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


    private Thread thread;

    private void creaThreadInvioTransazione(int viaggi, int idProdotto, int quantita, boolean isVendita) {
        thread = new Thread(() -> {
            progressBarTransazioni.setValue(0);
            progressBarTransazioni.setString(null);
            progressBarTransazioni.setMaximum(viaggi);

            for (int i = 1; i <= viaggi; i++) {

                if (isStopThread) {
                    System.out.println("Thread fermato:");
                    btnInviaTransazione.setVisible(true);
                    btnInviaTransazione.setEnabled(true);
                    break;
                }
                Transazione transazione = new Transazione(idProdotto, quantita);
                //invia transazione
                int codiceStato = isVendita ? CodiciStatoServer.AGGIUNGI_PRODOTTO : CodiciStatoServer.RIMUOVI_PRODOTTO;
                controllerClientNegozio.getClientConnessione().inviaSingolaTransazione(transazione, codiceStato);

                // Aggiorna la barra di avanzamento all'interno dell'EDT
                if (isVendita) {
                    controllerClientNegozio.addSingleTransazioneVendiAwait(transazione);
                } else {
                    controllerClientNegozio.addSingleTransazioneCompraAwait(transazione);
                }
                int progressoAttuale = progressBarTransazioni.getValue() + 1;
                progressBarTransazioni.setValue(progressoAttuale);
                progressBarTransazioni.setString(progressoAttuale + "/" + (viaggi) + " Transazioni");

                // Abilita il pulsante solo quando tutte le transazioni sono state inviate
                if (progressoAttuale == viaggi) {
                    progressBarTransazioni.setString("Finito!");
                    btnInviaTransazione.setEnabled(true);
                }

                try {
                    Thread.sleep(new Random().nextInt(100, 500)); // Aggiungi un piccolo ritardo tra le transazioni
                    // per evitare sovraccarichi
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            btnInviaTransazione.setVisible(true);
            btnStopTransazioni.setVisible(false);
        });
        thread.start();
    }

    private void creaTabellaPanello(JTable table, String[] colonneTabella, JScrollPane jScrollPane) {
        DefaultTableModel defaultTableModel = new DefaultTableModel(colonneTabella, 0);
        table.setModel(defaultTableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 14));


        table.setCellSelectionEnabled(true);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Imposta la tabella come non editabile
        table.setDefaultEditor(Object.class, null);

        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    System.out.println("Hai cliccato sulla riga: " + selectedRow);
                    int idProdotto = (int) table.getValueAt(selectedRow, 0);
                    String nome = (String) table.getValueAt(selectedRow, 1);
                    inputIdProdotto.setText(String.valueOf(idProdotto));
                    inputNome.setText(nome);
                }
            }
        });
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

    public void addAllTransazioneCompraAwait(List<Transazione> transazioneList, List<Prodotto> prodottiNegozio) {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel model = (DefaultTableModel) tblTransazioniAcquisto.getModel();

            boolean trovatoNelCarrello = false;
            for (Transazione transazione : transazioneList) {
                for (Prodotto prodotto : prodottiNegozio) {
                    if (prodotto.getIdProdotto() == transazione.getIdProdotto()) {
                        Object[] rowData = {transazione.getIdTransazione(), prodotto.getNome(),
                                prodotto.getPrezzo() + "€", transazione.getQuantita(), WAITING_CONFIRMATION};
                        model.addRow(rowData);
                        trovatoNelCarrello = true;
                    }
                }

            }
            model.fireTableDataChanged();

            System.out.println(" --> UI: Lista transazione aggiunta!!");

            if (!trovatoNelCarrello) {
                JOptionPane.showMessageDialog(null, "Nessun Prodotto della transazione è presente nel Carrello",
                        "Attenzione", JOptionPane.WARNING_MESSAGE);
            }
        });

    }

    public void addAllTransazioneVenditaAwait(List<Transazione> transazioneList, List<Prodotto> prodottiCarrello) {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel model = (DefaultTableModel) tblTransazioniVendita.getModel();

            boolean trovatoNelCarrello = false;
            for (Transazione transazione : transazioneList) {
                for (Prodotto prodotto : prodottiCarrello) {
                    if (prodotto.getIdProdotto() == transazione.getIdProdotto()) {
                        Object[] rowData = {transazione.getIdTransazione(), prodotto.getNome(),
                                prodotto.getPrezzo() + "€", transazione.getQuantita(), WAITING_CONFIRMATION};
                        model.addRow(rowData);
                        trovatoNelCarrello = true;
                    }
                }
            }
            model.fireTableDataChanged();

            if (!trovatoNelCarrello) {
                JOptionPane.showMessageDialog(null, "Nessun Prodotto della transazione è presente nel Carrello",
                        "Attenzione", JOptionPane.WARNING_MESSAGE);
            }

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
                        boolean isFull = incrementaQuantitaCarrello(transazione.getIdProdotto(),
                                transazione.getQuantita(), prodottiNegozio, prodottiCarrello);
                        if (isFull) {
                            transazioniTableModel.setValueAt(QUANTITA_MASSIMA_RAGGIUNTA, riga, 4); // Imposta il
                            // nuovo stato
                            break;
                        }
                        transazioniTableModel.setValueAt(SUCCESS_RESPONSE, riga, 4); // Imposta il nuovo stato
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
                        boolean isFull = decrementaQuantitaCarrello(transazione.getIdProdotto(),
                                transazione.getQuantita(), prodottiCarrello);
                        if (isFull) {
                            transazioniTableModel.setValueAt(QUANTITA_MINIMIA_RAGGIUNTA, riga, 4); // Imposta il
                            // nuovo stato
                            break;
                        }
                        transazioniTableModel.setValueAt(SUCCESS_RESPONSE, riga, 4); // Imposta il nuovo stato
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

       /* if (controllaCarrello) {
            prodotto = trovaProdottoLista(idProdotto, prodottiCarrello);
        }
        if (prodotto == null) {
            // Inserimento del nuovo prodotto nel carrello
            Prodotto nuovoProdotto = trovaProdottoLista(idProdotto, prodottiNegozio);
            if (nuovoProdotto == null) {
                System.out.println("Errore: Prodotto non presente nel negozio.");
                return;
            }
            nuovoProdotto.setQuantitaDisponibile(quantitaAggiunta);
            prodottiCarrello.add(nuovoProdotto);

            modelloCarrello.addRow(new String[]{nuovoProdotto.getNome(), String.valueOf(quantitaAggiunta)});
        } else {
            // Aggiornamento della quantità del prodotto nel carrello
            for (int riga = 0; riga < modelloCarrello.getRowCount(); riga++) {
                String nomeProdotto = (String) tblNegozio.getValueAt(riga, 1);
                if (nomeProdotto.equals(prodotto.getNome())) {
                    int quantita = Integer.parseInt((String) tblCarrello.getValueAt(riga, 1));
                    System.out.println("Elemento trovato alla riga " + riga);
                    int nuovaQuantita = quantita + quantitaAggiunta;
                    nuovaQuantita = Math.min(nuovaQuantita, MAX_QUANTITA);
                    if (getQuantita() < MAX_QUANTITA) {
                        modelloCarrello.setValueAt(String.valueOf(nuovaQuantita), riga, 1); // Aggiorna la quantità
                    }

                    modelloCarrello.fireTableDataChanged();
                    break;
                }
            }
        }*/

    }

    public synchronized boolean incrementaQuantitaCarrello(int idProdotto, int quantitaAggiunta,
                                                           List<Prodotto> prodottiNegozio,
                                                           List<Prodotto> prodottiCarrello) {
        if (getQuantita() >= MAX_QUANTITA) {
            System.out.println(" Attenzione: QUANTITA MAX RAGGIUNTA");
            return true;
        }

        DefaultTableModel modelloCarrello = (DefaultTableModel) tblCarrello.getModel();
        Prodotto prodotto = trovaProdottoLista(idProdotto, prodottiCarrello);

        if (prodotto == null) {
            // Inserimento del nuovo prodotto nel carrello
            Prodotto nuovoProdotto = trovaProdottoLista(idProdotto, prodottiNegozio);
            if (nuovoProdotto == null) {
                System.out.println("Errore: Prodotto non presente nel negozio.");
                return false;
            }

            nuovoProdotto.setQuantitaDisponibile(quantitaAggiunta);
            prodottiCarrello.add(nuovoProdotto);

            modelloCarrello.addRow(new String[]{nuovoProdotto.getNome(), String.valueOf(quantitaAggiunta)});

            modelloCarrello.fireTableDataChanged();
            updateProgressBar(quantitaAggiunta);
            return false;
        }

        // Aggiornamento della quantità del prodotto nel carrello
        for (int riga = 0; riga < modelloCarrello.getRowCount(); riga++) {
            String nomeProdotto = (String) tblCarrello.getValueAt(riga, 0);
            if (nomeProdotto.equals(prodotto.getNome())) {
                int quantita = Integer.parseInt((String) tblCarrello.getValueAt(riga, 1));
                System.out.println("Elemento trovato alla riga " + riga);
                int nuovaQuantita = quantita + quantitaAggiunta;
                if (quantitaAggiunta + getQuantita() > MAX_QUANTITA) {
                    System.out.println(" Attenzione: la quantità richieste supera quella disponibile ");
                    return true;
                }
                modelloCarrello.setValueAt(String.valueOf(nuovaQuantita), riga, 1); // Aggiorna la quantità
                modelloCarrello.fireTableDataChanged();
                updateProgressBar(quantitaAggiunta);
                return false;
            }
        }


        return false;
    }

    public synchronized boolean decrementaQuantitaCarrello(int idProdotto, int quantitaTogliere,
                                                           List<Prodotto> prodottiCarrello) {

        if (getQuantita() <= 0) {
            return true;
        }

        DefaultTableModel modelloCarrello = (DefaultTableModel) tblCarrello.getModel();
        Prodotto prodotto = trovaProdottoLista(idProdotto, prodottiCarrello);

        if (prodotto == null) {
            System.out.println(" Attenzione: Prodotto non trovato nel carrello");
            return false;
        }
        // Aggiornamento della quantità del prodotto nel carrello
        for (int riga = 0; riga < modelloCarrello.getRowCount(); riga++) {
            String nomeProdotto = (String) tblCarrello.getValueAt(riga, 0);
            if (nomeProdotto.equals(prodotto.getNome())) {
                System.out.println("Elemento trovato alla riga " + riga);

                int quantitaDisponibile = Integer.parseInt((String) tblCarrello.getValueAt(riga, 1));
                int nuovaQuantita = quantitaDisponibile - quantitaTogliere;

                if (getQuantita() - quantitaTogliere < 0) {
                    System.out.println(" Attenzione: la quantità richieste non è disponibile ");
                    return true;
                }

                modelloCarrello.setValueAt(String.valueOf(nuovaQuantita), riga, 1); // Aggiorna la quantità

                int stato = updateProgressBar(-quantitaTogliere);
                if (stato == -1) {
                    modelloCarrello.removeRow(riga);
                }

                modelloCarrello.fireTableDataChanged();
                return false;
            }
        }


        return false;
    }

    public void aggiornaTabellaProdottiNegozio(List<Prodotto> newProdottiNegozio) {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel model = (DefaultTableModel) tblNegozio.getModel();
            model.setRowCount(0);
            // Aggiungi righe per ciascun prodotto nella lista
            for (Prodotto product : newProdottiNegozio) {
                Object[] rowData = {product.getIdProdotto(), product.getNome(), product.getPrezzo() + "€",
                        product.getQuantitaDisponibile()};
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

    private boolean isStopThread;

    @Override
    public void run() {

    }
}

