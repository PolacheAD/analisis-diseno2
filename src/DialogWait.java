
import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.ActionEvent;
import javax.swing.AbstractButton;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Arnold
 */
public class DialogWait {
    private JDialog dialog;

    public void makeWait(String msg, ActionEvent evt) {

    Window win = SwingUtilities.getWindowAncestor((AbstractButton) evt.getSource());
    dialog = new JDialog(win, msg, Dialog.ModalityType.APPLICATION_MODAL);
    dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    /*JProgressBar progressBar = new JProgressBar();
    progressBar.setIndeterminate(true);*/
    //JPanel panel = new JPanel(new BorderLayout());
    //panel.add(progressBar, BorderLayout.CENTER);
    //panel.add(new JLabel("Por favor espere..."), BorderLayout.PAGE_START);
    panel_espera panel = new panel_espera();
    dialog.add(panel);
    //dialog.setSize(200, 200);
    dialog.pack();
    dialog.setLocationRelativeTo(win);
       dialog.setVisible(true);
   }

   public void close() {
       dialog.dispose();
   }
}
