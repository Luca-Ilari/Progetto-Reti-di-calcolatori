package it.itsrizzoli.view;

import it.itsrizzoli.controller.ControllerClientNegozio;
import it.itsrizzoli.model.Prodotto;
import it.itsrizzoli.model.Transazione;
import it.itsrizzoli.tcpip.ClientConnessione;
import it.itsrizzoli.tools.CodiciStatoServer;
import it.itsrizzoli.tools.EStato;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Random;

public class ClientNegozioInterfaccia extends JFrame {
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
    private JTextField inputNumeroTransazioni;
    private JTextField inputNome;
    private JButton btnInviaTransazione;
    private JRadioButton radioBtnCompra;
    private JRadioButton radioBtnVendi;
    private JPanel panelCreaTransazione;
    private JProgressBar progressBarTransazioni;
    private JProgressBar progressBarQuantita;
    private JButton btnStopTransazioni;
    private JPanel panelMaxQuantita;
    private JLabel labelQuantitaTot;
    private JLabel labelIDProdotto;

    private boolean isConnessoAlServer = false;
    private ControllerClientNegozio controllerClientNegozio;
    private final String[] articoliNegozioColonne = {"Id", "Prodotto", "Prezzo", "Disponibile"};
    private final String[] carrelloColonne = {"Prodotto", "Quantità"};
    private final String[] transazioniColonne = {"Number", "Prodotto", "Prezzo", "Quantità", "Stato"};

    private boolean isFermoThreadTransazioni;
    public static final int MAX_QUANTITA = 100_000;


    private static final int COLONNA_STATO = 4;


    public ClientNegozioInterfaccia(String titolo) {

        SwingUtilities.invokeLater(() -> inizializza(titolo));

    }


    private void aggiornaQuantitaRichiestaLabel() {

        boolean isVendita = radioBtnVendi.isSelected();
        String numeroMassimoFormat = creaNumberFormatter(MAX_QUANTITA);
        String quantitaStringa = inputQuantita.getText().trim();
        String numeroTransazioniStringa = inputNumeroTransazioni.getText().trim();

        if (quantitaStringa.isEmpty() || numeroTransazioniStringa.isEmpty() || !quantitaStringa.matches("\\d+") || !numeroTransazioniStringa.matches("\\d+")) {
            labelQuantitaTot.setText("0 / " + numeroMassimoFormat + " prodotti");
            return;
        }

        int quantita = Integer.parseInt(quantitaStringa);
        int numeroTransazioni = Integer.parseInt(numeroTransazioniStringa);

        int variazioneQuantita = (isVendita ? -quantita : quantita);
        int quantitaNuova = numeroTransazioni * variazioneQuantita;
        int quantitaTotale = getQuantita() + quantitaNuova;

        SwingUtilities.invokeLater(() -> {
            String operatore = isVendita ? "-" : "+";

            String output = String.format("(%s%s) %s / %s prodotti", operatore,
                    creaNumberFormatter(Math.abs(quantitaNuova)), creaNumberFormatter(getQuantita()),
                    numeroMassimoFormat);

            labelQuantitaTot.setText(output);

            labelQuantitaTot.setForeground(quantitaTotale < 0 || quantitaTotale > MAX_QUANTITA ? Color.RED :
                    Color.GRAY);
        });
    }


    public void setControllerClientNegozio(ControllerClientNegozio controllerClientNegozio) {
        this.controllerClientNegozio = controllerClientNegozio;
        attivaListenerBtn();
    }

    public void inizializza(String titolo) {
        if (isNullPanelMain()) return;

        setTitle(titolo);
        setContentPane(mainPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1400, 800));


        panelCreaTransazione.setVisible(false);

        setProperietaProgressBar(progressBarQuantita, MAX_QUANTITA, new Color(34, 120, 34)); // Giallo scuro
        setProperietaProgressBar(progressBarTransazioni, 100, new Color(34, 139, 34)); // Verde scuro

        TitledBorder border = (TitledBorder) panelMaxQuantita.getBorder();
        String MAX_QUANTITA_FORMATTER = creaNumberFormatter(MAX_QUANTITA);
        border.setTitle(border.getTitle() + MAX_QUANTITA_FORMATTER);
        panelMaxQuantita.setBorder(border);

        // Creazione dei pannelli delle tabelle
        creaTabellaPanello(tblCarrello, carrelloColonne, scrollPanelCarrello);
        creaTabellaPanello(tblNegozio, articoliNegozioColonne, scrolPanelNegozio);
        creaTabellaPanello(tblTransazioniAcquisto, transazioniColonne, scrollPanelTransazioniAcquisto);
        creaTabellaPanello(tblTransazioniVendita, transazioniColonne, scrollPanelTransazioniVendita);

        impostaGestoreSelezioneRigaNegozio();


        disattivaRigaTabelle(tblCarrello);
        disattivaRigaTabelle(tblTransazioniAcquisto);
        disattivaRigaTabelle(tblTransazioniVendita);

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

        impostaListerTextField(inputNumeroTransazioni);
        impostaListerTextField(inputQuantita);
        labelQuantitaTot.setText(0 + " / " + MAX_QUANTITA_FORMATTER + " prodotti");

        pack();
        setLocationRelativeTo(null);

        setVisible(true);
        System.out.println(" --- FINE THREAD_SWING_EDT ---");
    }

    private void attivaListenerRadioButton() {
        radioBtnCompra.addActionListener(e -> {
            if (radioBtnCompra.isSelected()) {
                radioBtnVendi.setSelected(false); // Disattiva il radio button per la vendita
            }
            aggiornaQuantitaRichiestaLabel();
            inputIdProdotto.setVisible(true);
            labelIDProdotto.setVisible(true);
        });

// Aggiungi un ActionListener al radio button per la vendita
        radioBtnVendi.addActionListener(e -> {
            if (radioBtnVendi.isSelected()) {
                radioBtnCompra.setSelected(false); // Disattiva il radio button per l'acquisto
            }
            aggiornaQuantitaRichiestaLabel();
            inputIdProdotto.setVisible(false);
            labelIDProdotto.setVisible(false);

        });
    }

    private void impostaGestoreSelezioneRigaNegozio() {
        this.tblNegozio.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                int selectedRow = tblNegozio.getSelectedRow();
                if (selectedRow != -1) {
                    System.out.println("Hai cliccato sulla riga: " + selectedRow);
                    int idProdotto = Integer.parseInt(tblNegozio.getValueAt(selectedRow, 0).toString());
                    String nome = (String) tblNegozio.getValueAt(selectedRow, 1);
                    inputIdProdotto.setText(String.valueOf(idProdotto));
                    inputNome.setText(nome);
                }
            }
        });
        this.tblNegozio.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void impostaListerTextField(JTextField jTextField) {
        jTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                // Metodo chiamato quando viene inserito del testo nel JTextField
                aggiornaQuantitaRichiestaLabel();

            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                // Metodo chiamato quando viene rimosso del testo dal JTextField
                aggiornaQuantitaRichiestaLabel();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                aggiornaQuantitaRichiestaLabel();

            }
        });
    }

    private void disattivaRigaTabelle(JTable table) {
        table.setRowSelectionAllowed(false);
        table.setColumnSelectionAllowed(false);

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

    public synchronized int aggiornaProgressBar(int quantita) {
        int currentValue = progressBarQuantita.getValue();
        int newValue = currentValue + quantita;

        newValue = Math.max(newValue, 0);

        newValue = Math.min(newValue, MAX_QUANTITA);
        String MAX_QUANTITA_FORMATTER = creaNumberFormatter(MAX_QUANTITA);
        progressBarQuantita.setString(newValue + " / " + MAX_QUANTITA_FORMATTER + " Prodotti");

        progressBarQuantita.setValue(newValue);

        if (newValue == 0) {
            return -1;
        } else if (newValue == MAX_QUANTITA) {
            return +1;
        }
        return 0;

    }


    public synchronized int getQuantita() {
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
        this.isConnessoAlServer = statoNegozioOnline;
        changeTitle();
        if (!statoNegozioOnline) {
            stopThread();
        }
    }

    public void stopThread() {
        isFermoThreadTransazioni = true;
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


        btnInviaTransazione.addActionListener(e -> {
            progressBarTransazioni.setForeground(new Color(34, 139, 34)); // Verde scuro


            if (!controllerClientNegozio.getClientConnessione().onConnessione) {
                JOptionPane.showMessageDialog(null, "Non sei connesso a un server!", "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;

            }


            btnInviaTransazione.setEnabled(false);
            progressBarTransazioni.setValue(0);
            progressBarTransazioni.setString("pronti!");
            String idProdottoStr = inputIdProdotto.getText().trim();
            String nomeProdotto = inputNome.getText().trim();
            String quantitaStr = inputQuantita.getText().trim();
            String viaggiStr = inputNumeroTransazioni.getText().trim();

            if (idProdottoStr.isEmpty() || nomeProdotto.isEmpty() || quantitaStr.isEmpty() || viaggiStr.isEmpty()) {
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

            boolean isVendita = radioBtnVendi.isSelected();

            boolean trovatoProdotto = false;

            List<Prodotto> prodotti = isVendita ? controllerClientNegozio.getProdottiCarrello() :
                    controllerClientNegozio.getProdottiNegozio();
            for (Prodotto prodotto : prodotti) {
                if ((isVendita && prodotto.getNome().equals(nomeProdotto)) || (prodotto.getIdProdotto() == idProdotto && prodotto.getNome().equals(nomeProdotto))) {
                    trovatoProdotto = true;
                    idProdotto = prodotto.getIdProdotto();
                    break;
                }
            }

            if (!trovatoProdotto) {
                String errorMessage;
                if (isVendita) {
                    errorMessage = "Il prodotto selezionato non è presente nel carrello oppure \n assicurati di aver "
                            + "inserito il nome corretto.";
                } else {
                    errorMessage =
                            "Il prodotto selezionato non è presente nel negozio oppure \n assicurati di aver " +
                                    "inserito il nome e l'ID corretti.";
                }
                JOptionPane.showMessageDialog(null, errorMessage, "Prodotto non trovato", JOptionPane.WARNING_MESSAGE);
                btnInviaTransazione.setEnabled(true);
                return;
            }

            String message = isVendita ? "Quantità totale richiesta non disponibile." : "Stai chiedendo una " +
                    "quantità oltre il limite massimo.";
            String messageTitle = isVendita ? "Attenzione: Quantità Minima" : "Attenzione: Quantità Massima";
            String MAX_QUANTITA_FORMATTER = creaNumberFormatter(MAX_QUANTITA);
            for (int i = 1; i <= viaggi; i++) {
                int quantitaTotale = getQuantita() + (i * (isVendita ? -quantita : quantita));

                if ((isVendita && quantitaTotale < 0) || (!isVendita && quantitaTotale > MAX_QUANTITA)) {
                    int finalQuantita = getQuantita() + viaggi * (isVendita ? -quantita : quantita);
                    JOptionPane.showMessageDialog(null,
                            message + "\n" + finalQuantita + "/" + MAX_QUANTITA_FORMATTER + " " + "prodotti",
                            messageTitle, JOptionPane.WARNING_MESSAGE);
                    btnInviaTransazione.setEnabled(true);
                    return;
                }
            }


            btnStopTransazioni.setVisible(true);
            btnInviaTransazione.setVisible(false);
            isFermoThreadTransazioni = false;
            creaThreadInvioTransazione(viaggi, idProdotto, quantita, isVendita);

        });

        btnStopTransazioni.addActionListener(e -> {
            isFermoThreadTransazioni = true;
            progressBarTransazioni.setString(progressBarTransazioni.getString() + " concluse ");
            progressBarTransazioni.setForeground(Color.red);
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

    }


    private String creaNumberFormatter(int n) {
        return String.format("%,d", n).replace(",", ".");
    }

    private void creaThreadInvioTransazione(int nTransazioni, int idProdotto, int quantita, boolean isVendita) {
        String nTransazioniFormater = creaNumberFormatter(nTransazioni);
        Thread thread = new Thread(() -> {
            progressBarTransazioni.setValue(0);
            progressBarTransazioni.setString(null);
            progressBarTransazioni.setMaximum(nTransazioni);

            for (int i = 1; i <= nTransazioni; i++) {

                if (isFermoThreadTransazioni) {
                    System.out.println("Thread fermato:");
                    btnInviaTransazione.setVisible(true);
                    btnInviaTransazione.setEnabled(true);
                    isFermoThreadTransazioni = true;
                    break;
                }
                Transazione transazione = new Transazione(idProdotto, quantita);
                //invia transazione
                int codiceStato = isVendita ? CodiciStatoServer.AGGIUNGI_PRODOTTO : CodiciStatoServer.RIMUOVI_PRODOTTO;

                ClientConnessione clientConnessione = controllerClientNegozio.getClientConnessione();
                clientConnessione.inviaTransazioneSingola(transazione, codiceStato);

                // Aggiorna la barra di avanzamento all'interno dell'EDT
                if (isVendita) {
                    controllerClientNegozio.addSingleTransazioneVendiAwait(transazione);
                } else {
                    controllerClientNegozio.addSingleTransazioneCompraAwait(transazione);
                }
                int progressoAttuale = progressBarTransazioni.getValue() + 1;
                progressBarTransazioni.setValue(progressoAttuale);
                progressBarTransazioni.setString(progressoAttuale + "/" + (nTransazioniFormater) + " Transazioni");

                // Abilita il pulsante solo quando tutte le transazioni sono state inviate
                if (progressoAttuale == nTransazioni) {
                    progressBarTransazioni.setString("Finito!");
                    btnInviaTransazione.setEnabled(true);
                }

                try {
                    Thread.sleep(new Random().nextInt(100, 300)); // Aggiungi un piccolo ritardo tra le transazioni
                    // per evitare sovraccarichi
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
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


        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Imposta la tabella come non editabile
        table.setDefaultEditor(Object.class, null);

        jScrollPane.setViewportView(table);
    }

    public void addSingleTransazioneCompraAwait(Transazione transazione, List<Prodotto> prodottiNegozio) {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel model = (DefaultTableModel) tblTransazioniAcquisto.getModel();

            for (Prodotto prodotto : prodottiNegozio) {
                if (prodotto.getIdProdotto() == transazione.getIdProdotto()) {
                    Object[] rowData = {transazione.getIdTransazione(), prodotto.getNome(), prodotto.getPrezzo() +
                            "€", transazione.getQuantita(), EStato.IN_ATTESA_DI_CONFERMA.getValue()};
                    model.addRow(rowData);
                }
            }
            model.fireTableDataChanged();


        });

    }

    public void addSingleTransazioneVenditaAwait(Transazione transazione, List<Prodotto> prodottiCarrello) {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel model = (DefaultTableModel) tblTransazioniVendita.getModel();

            for (Prodotto prodotto : prodottiCarrello) {
                if (prodotto.getIdProdotto() == transazione.getIdProdotto()) {
                    Object[] rowData = {transazione.getIdTransazione(), prodotto.getNome(), prodotto.getPrezzo() +
                            "€", transazione.getQuantita(), EStato.IN_ATTESA_DI_CONFERMA.getValue()};
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
                if (idTransazioneRow != transazione.getIdTransazione()) {
                    continue;
                }
                String statoTransazione = (String) transazioniTableModel.getValueAt(riga, COLONNA_STATO);
                if (statoTransazione.equals(EStato.IN_ATTESA_DI_CONFERMA.getValue())) {
                    boolean isFull = incrementaQuantitaCarrello(transazione.getIdProdotto(),
                            transazione.getQuantita(), prodottiNegozio, prodottiCarrello);

                    aggiornaQuantitaRichiestaLabel();

                    if (isFull) {
                        transazioniTableModel.setValueAt(EStato.QUANTITA_MASSIMA_RAGGIUNTA.getValue(), riga,
                                COLONNA_STATO); // Imposta il
                        // nuovo stato
                        break;
                    }
                    transazioniTableModel.setValueAt(EStato.COMPLETATO.getValue(), riga, COLONNA_STATO); // Imposta
                    // il nuovo stato
                }

                System.out.println("Elemento trovato alla riga " + riga);

                break;
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
                if (idTransazioneRow != transazione.getIdTransazione()) {
                    continue;
                }
                String statoTransazione = (String) transazioniTableModel.getValueAt(riga, COLONNA_STATO);
                if (statoTransazione.equals(EStato.IN_ATTESA_DI_CONFERMA.getValue())) {
                    boolean isFull = decrementaQuantitaCarrello(transazione.getIdProdotto(),
                            transazione.getQuantita(), prodottiCarrello);
                    aggiornaQuantitaRichiestaLabel();

                    if (isFull) {
                        transazioniTableModel.setValueAt(EStato.QUANTITA_MINIMA_RAGGIUNTA.getValue(), riga,
                                COLONNA_STATO); // Imposta il
                        // nuovo stato
                        break;
                    }
                    transazioniTableModel.setValueAt(EStato.COMPLETATO.getValue(), riga, COLONNA_STATO); // Imposta
                    // il nuovo stato
                }
                System.out.println("Elemento trovato alla riga " + riga);

                break;
            }

            transazioniTableModel.fireTableDataChanged();


            System.out.println(" --> UI: Lista transazione aggiornato!!");

            // allSetResponsiveTable();
        });

    }

    public void aggiornaStatoTransazioneServerError() {
        SwingUtilities.invokeLater(() -> {

            DefaultTableModel venditaTableModel = (DefaultTableModel) tblTransazioniVendita.getModel();
            aggiornaStatoTransazioni(venditaTableModel);

            DefaultTableModel acquistoTableModel = (DefaultTableModel) tblTransazioniAcquisto.getModel();
            aggiornaStatoTransazioni(acquistoTableModel);

            System.out.println(" --> UI: Lista transazioni aggiornata!!");
        });
    }

    private void aggiornaStatoTransazioni(DefaultTableModel transazioniTableModel) {

        for (int riga = 0; riga < transazioniTableModel.getRowCount(); riga++) {
            String statoTransazione = (String) transazioniTableModel.getValueAt(riga, COLONNA_STATO);
            if (EStato.IN_ATTESA_DI_CONFERMA.getValue().equals(statoTransazione)) {
                transazioniTableModel.setValueAt(EStato.ERRORE_DEL_SERVER.getValue(), riga, COLONNA_STATO);
                System.out.println("Transazione trovata alla riga " + riga);
            }
        }
        transazioniTableModel.fireTableDataChanged();
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
                    if (statoTransazione.equals(EStato.IN_ATTESA_DI_CONFERMA.getValue())) {

                        if (isVendita) {
                            transazioniTableModel.setValueAt(EStato.PRODOTTO_ESAURITO_CLIENTE.getValue(), riga, 4);
                            // Imposta il nuovo
                            // stato
                        } else {
                            transazioniTableModel.setValueAt(EStato.PRODOTTO_ESAURITO_NEGOZIO.getValue(), riga, 4);
                            // Imposta il nuovo
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
            aggiornaProgressBar(quantitaAggiunta);
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
                aggiornaProgressBar(quantitaAggiunta);

                prodotto.setQuantitaDisponibile(nuovaQuantita);
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
            if (!nomeProdotto.equals(prodotto.getNome())) {
                continue;
            }
            System.out.println("Elemento trovato alla riga " + riga);

            int quantitaDisponibile = Integer.parseInt((String) tblCarrello.getValueAt(riga, 1));
            int nuovaQuantita = quantitaDisponibile - quantitaTogliere;
            if (getQuantita() - quantitaTogliere < 0) {
                System.out.println(" Attenzione: la quantità richieste non è disponibile ");
                return true;
            }
            modelloCarrello.setValueAt(String.valueOf(nuovaQuantita), riga, 1); // Aggiorna la quantità

            prodotto.setQuantitaDisponibile(nuovaQuantita);

            aggiornaProgressBar(-quantitaTogliere);

            if (prodotto.getQuantitaDisponibile() == 0) {
                System.out.println(" Attenzione: Prodotto esaurito nel carrello");
                modelloCarrello.removeRow(riga);
                controllerClientNegozio.removeProdottoCarrello(nomeProdotto);
            }
            modelloCarrello.fireTableDataChanged();
            return false;
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
        labelStatoServer.setText(isConnessoAlServer ? "Online" : "Offline");
        labelStatoServer.setForeground(isConnessoAlServer ? Color.GREEN : Color.RED);
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


    public void svuotaTabelleDati() {
        svuotaTabella(tblCarrello);
        svuotaTabella(tblNegozio);
        svuotaTabella(tblTransazioniAcquisto);
        svuotaTabella(tblTransazioniVendita);

        progressBarQuantita.setValue(0);
        progressBarQuantita.setString(null);

        progressBarTransazioni.setValue(0);
        progressBarTransazioni.setString(null);


        labelQuantitaTot.setText(0 + "/" + creaNumberFormatter(MAX_QUANTITA) + " prodotti");

    }

    public void svuotaTabelleDatiNegozio() {
        svuotaTabella(tblNegozio);
        progressBarTransazioni.setValue(0);
        progressBarTransazioni.setString(null);
        labelQuantitaTot.setText(0 + "/" + creaNumberFormatter(MAX_QUANTITA) + " prodotti");
    }

    private void svuotaTabella(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
    }


}

