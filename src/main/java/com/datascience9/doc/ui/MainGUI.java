package com.datascience9.doc.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainGUI {
    private JTextField inputDirTextField;
    private JButton inputDirBtn;
    private JTextField outputDirTextField;
    private JButton outputDirBtn;

    public MainGUI() {
        outputDirBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fc = new JFileChooser();
                int returnVal = fc.showOpenDialog(null);
            }
        });
    }
}
