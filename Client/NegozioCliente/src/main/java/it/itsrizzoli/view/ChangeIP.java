package it.itsrizzoli.view;

import it.itsrizzoli.tcpip.ClientConnessione;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ChangeIP extends JFrame {
    private JTextField inputIp;
    private JButton btnAggiroanIp;
    private JPanel panelMainForm;
    private JTextField inputPorta;
    private final JFrame mainPanel;
    private ClientConnessione clientConnessione;

    public ChangeIP(ClientConnessione clientConnessione, JFrame jFrame) {

        this.mainPanel = jFrame;
        this.clientConnessione = clientConnessione;
        SwingUtilities.invokeLater(() -> {
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
                    mainPanel.setEnabled(true); // Attiva la finestra main quando chiudi questa finestra
                }
            });

            setConnectionDetails();
            setProperietaButton(Color.GRAY, Color.WHITE, btnAggiroanIp);
            setProprietaTextField(inputIp);
            setProprietaTextField(inputPorta);
            implementaListenerBtn();
            pack();
            setLocationRelativeTo(null);
            setVisible(true);
        });

    }

    private void setProprietaTextField(JTextField textField) {
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

    public void setConnectionDetails() {
        inputPorta.setText(String.valueOf(clientConnessione.getServerPort()));
        inputIp.setText(clientConnessione.getServerAddress());
    }

    public void implementaListenerBtn() {
        btnAggiroanIp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeIpAddress();
            }
        });
    }

    private void setProperietaButton(Color backgroundColor, Color textColor, JButton button) {
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(180, 40));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
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

    }

    private void changeIpAddress() {
        String newIP = inputIp.getText().trim();
        String newPorta = inputPorta.getText().trim();

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
        clientConnessione.chiusuraConnessione();
        clientConnessione.startConnessione();

        // Elimina la finestra
        this.mainPanel.setEnabled(true);
        dispose();
    }

}
