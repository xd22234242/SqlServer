package com.example.sqlserver.Form;

import javax.swing.*;

public class ProcessForm extends JFrame {
    private JProgressBar progressBar = new JProgressBar();
    public JLabel text = new JLabel();
    private JPanel panel = new JPanel();

    public ProcessForm() {
        this.setBounds(300, 200, 450, 200);
        this.setResizable(false);

        progressBar.setBounds(50, 50, 350, 35);
        progressBar.setStringPainted(true);

        text.setBounds(50, 100, 200, 35);

        panel.add(text);
        panel.add(progressBar);

        panel.setLayout(null);

        this.setContentPane(panel);
        this.setVisible(true);

    }

    public void setText(String text) {
        this.text.setText(text);
    }

    //设置区间
    public void setInterval(int min, int max) {
        progressBar.setMinimum(min);
        progressBar.setMaximum(max);
    }

    //设置进度
    public void setProcess(int i) {
        progressBar.setValue(i);
        progressBar.updateUI();
    }
}
