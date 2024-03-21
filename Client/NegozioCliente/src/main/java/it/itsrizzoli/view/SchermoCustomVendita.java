package it.itsrizzoli.view;

import it.itsrizzoli.controller.ControllerClientNegozio;
import it.itsrizzoli.model.Prodotto;
import it.itsrizzoli.model.Transazione;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SchermoCustomVendita extends JFrame {
    private JPanel panel1;
    private JTextField inputIdProdotto;
    private JButton btnInvia;
    private JProgressBar progressBarTransazioni;
    private JTable table;
    private JScrollPane scrollPanel;
    private JTextField inputQuantita;
    private JTextField inputViaggi;
    private JTextField inputNome;
    private JLabel labelStatoServer;
    private JButton btnChangeIP;
    private JRadioButton comprareRadioButton;
    private JRadioButton vendereRadioButton;

    private final List<Prodotto> listaProdotti = new ArrayList<>();
    private final ControllerClientNegozio controllerClientNegozio;
    private boolean statoNegozioOnline;

    public SchermoCustomVendita(ControllerClientNegozio controllerClientNegozio) {
        int idRandom = new Random().nextInt(0, 10000);

        this.controllerClientNegozio = controllerClientNegozio;
        SwingUtilities.invokeLater(() -> {
            setTitle("Negozio Online - " + "Interfaccia Client %d".formatted(idRandom));
            setContentPane(panel1);
            setMinimumSize(new Dimension(500, 700));
            setMaximumSize(new Dimension(500, 700));
            setPreferredSize(new Dimension(500, 700));

            setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            creazioneProdotti();

            creazioneTabella();

            aggiungiListenerRigaTabella();

            progressBarTransazioni.setStringPainted(true);
            progressBarTransazioni.setStringPainted(true); // Mostra il valore percentuale sulla progress bar
            progressBarTransazioni.setForeground(new Color(0, 102, 204)); // Colore della barra di avanzamento
            progressBarTransazioni.setBackground(Color.LIGHT_GRAY); // Colore dello sfondo della progress bar
            progressBarTransazioni.setBorderPainted(false); // Rimuove il bordo

            setPropertyButton(Color.GRAY, Color.WHITE, btnChangeIP);
            setPropertyButton(Color.green, Color.WHITE, btnInvia);

            setPropertyTextField(inputIdProdotto);
            setPropertyTextField(inputNome);
            setPropertyTextField(inputQuantita);
            setPropertyTextField(inputViaggi);
            setLocationRelativeTo(null);
            setVisible(true);

        });


        btnInvia.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (!controllerClientNegozio.getClientConnessione().onConnessione) {
                    JOptionPane.showMessageDialog(null, "Non sei connesso a un server!", "Errore",
                            JOptionPane.ERROR_MESSAGE);
                    return;

                }


                btnInvia.setEnabled(false);
                progressBarTransazioni.setValue(0);
                progressBarTransazioni.setString("pronto!");

                String idProdottoStr = inputIdProdotto.getText().trim();
                String nome = inputNome.getText().trim();
                String quantitaStr = inputQuantita.getText().trim();
                String viaggiStr = inputViaggi.getText().trim();

                if (idProdottoStr.isEmpty() || nome.isEmpty() || quantitaStr.isEmpty() || viaggiStr.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Per favore, inserisci tutti i campi.", "Avviso",
                            JOptionPane.WARNING_MESSAGE);
                    btnInvia.setEnabled(true);
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
                        btnInvia.setEnabled(true);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    System.out.println(ex.getMessage());
                    JOptionPane.showMessageDialog(null, "Inserisci solo numeri validi nei campi.", "Errore",
                            JOptionPane.ERROR_MESSAGE);
                    btnInvia.setEnabled(true);
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
                    btnInvia.setEnabled(true);
                    return;
                }

                creaThreadInvioTransazione(viaggi, idProdotto, quantita);
            }
        });

        btnChangeIP.addActionListener(e -> {
            setEnabled(false);
            new ChangeIP(controllerClientNegozio.getClientConnessione(), this);
            System.out.println(" CLICK: CHANGE btn IP");
        });


    }

    private void setPropertyButton(Color backgroundColor, Color textColor, JButton button) {
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

    private void setPropertyTextField(JTextField textField) {
        textField.setPreferredSize(new Dimension(200, 30)); // Dimensioni del JTextField
        textField.setFont(new Font("Arial", Font.PLAIN, 14)); // Impostazione del font e dello stile del testo
        textField.setForeground(Color.BLACK); // Colore del testo
        textField.setBackground(new Color(240, 240, 240)); // Colore di sfondo
        textField.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // Bordo del JTextField
        textField.setCaretColor(Color.BLUE); // Colore del cursore

        // Aggiungiamo un effetto di ombreggiatura al JTextField
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textField.setBorder(BorderFactory.createLineBorder(new Color(51, 153, 255))); // Cambia il colore del
                // bordo quando il JTextField ottiene il focus
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                textField.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // Ripristina il colore del bordo
                // quando il JTextField perde il focus
            }
        });

    }

    private void creaThreadInvioTransazione(int viaggi, int idProdotto, int quantita) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                progressBarTransazioni.setValue(0);
                progressBarTransazioni.setString("Pronto");
                int progressStep = 100 / viaggi;
                int restoProgresso = 100 % viaggi;

                for (int i = 0; i < viaggi; i++) {
                    Transazione transazione = new Transazione(idProdotto, quantita);
                   // controllerClientNegozio.getClientConnessione().inviaSingolaTransazione(transazione,);
                    int progressoAttuale = progressBarTransazioni.getValue() + progressStep;
                    if (i < restoProgresso) {
                        progressoAttuale++;
                    }
                    progressBarTransazioni.setValue(progressoAttuale);
                    progressBarTransazioni.setString(i + "/" + viaggi + " Transazioni");

                    if (progressBarTransazioni.getValue() == 100) {
                        progressBarTransazioni.setString("Finito!");
                        btnInvia.setEnabled(true);

                    }
                    try {
                        Thread.sleep(new Random().nextInt(200, 1000));
                    } catch (InterruptedException e) {
                        System.err.println(e.getMessage());
                    }
                }


            }
        });
        thread.start();
    }

    public void setStatoNegozioOnline(boolean statoNegozioOnline) {
        this.statoNegozioOnline = statoNegozioOnline;
    }

    private void creazioneTabella() {

        String[] columnNames = {"Id", "Nome", "Quantità Disponibile"};
        Object[][] data = new Object[listaProdotti.size()][columnNames.length];
        for (int i = 0; i < listaProdotti.size(); i++) {
            Prodotto product = listaProdotti.get(i);
            data[i][0] = product.getIdProdotto();
            data[i][1] = product.getNome();
            data[i][2] = product.getQuantitaDisponibile();

        }


        DefaultTableModel model = new DefaultTableModel(data, columnNames);

        aggiungiListenerRigaTabella();
        scrollPanel.setViewportView(table);
    }

    public void aggiungiListenerRigaTabella() {
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
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
            }
        });
    }


    private void creazioneProdotti() {
        listaProdotti.add(new Prodotto(100, "prod 01", 19.99, 20));
        if (!controllerClientNegozio.getProdottiNegozio().isEmpty()) {
            listaProdotti.clear();
            listaProdotti.addAll(controllerClientNegozio.getProdottiNegozio());
        }
    }

    public void changeTitle() {
        labelStatoServer.setText(statoNegozioOnline ? "Online" : "Offline");
        labelStatoServer.setForeground(statoNegozioOnline ? Color.GREEN : Color.RED);
    }

    public void aggiornaTabellaProdottiNegozio(List<Prodotto> newProdottiNegozio) {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel tableProdottiNegozio = (DefaultTableModel) table.getModel();
            tableProdottiNegozio.setRowCount(0);
            // Aggiungi righe per ciascun prodotto nella lista
            for (Prodotto product : newProdottiNegozio) {
                Object[] rowData = {product.getIdProdotto(), product.getNome(), product.getQuantitaDisponibile()};
                tableProdottiNegozio.addRow(rowData);
            }
            tableProdottiNegozio.fireTableDataChanged();

            System.out.println(" --> UI: Tabella dei prodotti nel negozio aggiornata!!");
        });
    }
}
