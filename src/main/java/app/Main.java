package main.java.app;

import main.java.app.dao.DB;
import main.java.app.gui.MainFrame;

import javax.swing.SwingUtilities;

public class Main {
  public static void main(String[] args) {
    DB.init();

    SwingUtilities.invokeLater(() -> {
      MainFrame frame = new MainFrame();
      frame.setVisible(true);
    });
  }
}
