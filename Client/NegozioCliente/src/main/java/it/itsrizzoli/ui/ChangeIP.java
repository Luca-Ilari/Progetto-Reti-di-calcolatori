package it.itsrizzoli.ui;

import it.itsrizzoli.tcpip.ClientConnessione;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChangeIP extends JFrame{
    private JPanel panel;
    private JTextField textField1;
    private JButton btnAggiroanIp;
    private JPanel Form;
    private JPanel panelTitle;
    private JPanel panelInput;

    public ClientConnessione clientConnessione;

    public ChangeIP(ClientConnessione clientConnessione) {
        setContentPane(Form);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setVisible(true);
        pack();
        setLocationRelativeTo(null);
        btnAggiroanIp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newIP = textField1.getText();
                if (newIP.isBlank()) {
                    JOptionPane.showMessageDialog(null, "Attenzione: il campo è vuoto");
                    return;
                }
                // Regex for digit from 0 to 255.
                String zeroTo255
                        = "(\\d{1,2}|(0|1)\\"
                        + "d{2}|2[0-4]\\d|25[0-5])";
                String regex
                        = zeroTo255 + "\\."
                        + zeroTo255 + "\\."
                        + zeroTo255 + "\\."
                        + zeroTo255;
                if (!newIP.matches(regex) && newIP.equals("localhost")){
                    JOptionPane.showMessageDialog(null, "Attenzione: formato Ip errato");
                    return;
                }
                if (ChangeIP.this.clientConnessione == null){
                    JOptionPane.showMessageDialog(null, "Attenzione: clientConnessione non presente");
                    return;
                }
                ChangeIP.this.clientConnessione.aggiornaIP(newIP);
                setVisible(false);
            }
        });

    }
}
