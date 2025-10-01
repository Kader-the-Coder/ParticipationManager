package main.java.app.gui;

import javax.swing.JFrame;

public class MainFrame extends JFrame {

  public MainFrame() {
    setTitle("My Application");           // window title
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(800, 600);                    // width x height
    setLocationRelativeTo(null);          // center on screen
  }
}
