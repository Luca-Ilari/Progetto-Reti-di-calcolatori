package it.itsrizzoli.view;

import it.itsrizzoli.tcpip.ClientConnessione;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ChangeIP extends JFrame {
    private JTextField textIp;
    private JButton btnAggiroanIp;
    private JPanel panelMainForm;
    private JTextField textPorta;
    private JFrame mainPanel;
    private ClientConnessione clientConnessione;

    public ChangeIP(ClientConnessione clientConnessione, JFrame jFrame) {

        this.mainPanel = jFrame;
        this.clientConnessione = clientConnessione;

        if (panelMainForm == null) {
            Panel panelError = new Panel();
            JLabel labelError =
                    new JLabel("ERRORE:caricamento main panel, chiudere la finestra - " + this.getClass().getName());


            panelError.add(labelError);
            setContentPane(panelError);

            pack();
            setLocationRelativeTo(null);

            setVisible(true);
            return;
        }
        setContentPane(panelMainForm);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mainPanel.setEnabled(true); // Nascondi la finestra
            }
        });
        SwingUtilities.invokeLater(() -> {
            setConnectionDetails();
            implementaListenerBtn();
            pack();
            setLocationRelativeTo(null);
            setVisible(true);
        });

    }

    public void setConnectionDetails() {
        textPorta.setText(String.valueOf(clientConnessione.getServerPort()));
        textIp.setText(clientConnessione.getServerAddress());
    }

    public void implementaListenerBtn() {
        btnAggiroanIp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeIpAddress();
            }
        });
    }

    private void changeIpAddress() {
        String newIP = textIp.getText().trim();
        String newPorta = textPorta.getText().trim();

        if (newIP.isBlank()) {
            JOptionPane.showMessageDialog(null, "Attenzione: il campo IP è vuoto");
            return;
        }

        // Regex per gli indirizzi IP
        String zeroTo255 = "(\\d{1,2}|(0|1)\\d{2}|2[0-4]\\d|25[0-5])";
        String regexIP = zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255;

        // Validazione dell'indirizzo IP
        if (!newIP.matches(regexIP) && !newIP.equals("localhost")) {
            JOptionPane.showMessageDialog(null, "Attenzione: formato IP errato");
            return;
        }

        if (newPorta.isBlank()) {
            JOptionPane.showMessageDialog(null, "Attenzione: il campo Porta è vuoto");
            return;
        }

        // Regex per la porta
        String regexPorta = "^[0-9]{1,5}$";

        // Validazione della porta
        if (!newPorta.matches(regexPorta)) {
            JOptionPane.showMessageDialog(null, "Attenzione: formato PORTA errato");
            return;
        }

        if (clientConnessione == null) {
            JOptionPane.showMessageDialog(null, "Errore: il socket client non è presente");
            return;
        }

        // Aggiornamento dell'indirizzo IP nel client di connessione
        clientConnessione.aggiornaIP(newIP, Integer.parseInt(newPorta));


        // Elimina la finestra
        this.mainPanel.setEnabled(true);
        dispose();
    }

}
