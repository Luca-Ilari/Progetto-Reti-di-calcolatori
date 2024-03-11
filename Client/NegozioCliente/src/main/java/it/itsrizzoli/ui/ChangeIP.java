package it.itsrizzoli.ui;

import it.itsrizzoli.tcpip.ClientConnessione;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChangeIP extends JFrame {
    private JPanel panel;
    private JTextField textIp;
    private JButton btnAggiroanIp;
    private JPanel panelForm;
    private JPanel panelTitle;
    private JPanel panelInput;
    private JTextField textPorta;


    public ChangeIP(ClientConnessione clientConnessione) {
        setContentPane(panelForm);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
        pack();
        setLocationRelativeTo(null);
        btnAggiroanIp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newIP = textIp.getText();
                String newPorta = textPorta.getText();

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
                dispose();

            }
        });

    }
}
