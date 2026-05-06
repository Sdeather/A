// File: CounterApp.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CounterApp extends JFrame {
    private int count = 0;
    private JLabel label;

    public CounterApp() {
        super("Simple Counter");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(300, 150);
        setLocationRelativeTo(null);
        label = new JLabel("Count: 0", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));

        JButton addBtn = new JButton("+");
        JButton subBtn = new JButton("-");
        JButton resetBtn = new JButton("Reset");

        addBtn.setFont(new Font("Arial", Font.BOLD, 20));
        subBtn.setFont(new Font("Arial", Font.BOLD, 20));
        resetBtn.setFont(new Font("Arial", Font.PLAIN, 16));

        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                count++;
                label.setText("Count: " + count);
            }
        });

        subBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                count--;
                label.setText("Count: " + count);
            }
        });

        resetBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                count = 0;
                label.setText("Count: " + count);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(subBtn);
        buttonPanel.add(addBtn);
        buttonPanel.add(resetBtn);

        add(label, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CounterApp().setVisible(true);
        });
    }
}
