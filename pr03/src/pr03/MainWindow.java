package pr03;

import com.jogamp.opengl.util.FPSAnimator;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JFrame;

/**
 *
 * @author Adam Jurcik <xjurc@fi.muni.cz>
 */
public class MainWindow extends JFrame {

    private Cursor blankCursor;
    private Robot robot;
    private GLJPanel panel;
    private Scene scene;
    private FPSAnimator animator;
    private boolean fullscreen = false;
    
    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        initComponents();
        
        setSize(800, 600);
        setTitle("Project 3");
        
        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);
        
        // Set depth buffer to 24-bits
        capabilities.setDepthBits(24);
        
        panel = new GLJPanel(capabilities);
        
        add(panel, BorderLayout.CENTER);
        
        animator = new FPSAnimator(panel, 60, true);
        scene = new Scene(animator);
        
        panel.addGLEventListener(scene);
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                MainWindow.this.keyPressed(e);
            }
        });
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                MainWindow.this.mouseReleased(e);
            }
            @Override
            public void mousePressed(MouseEvent e) {
                MainWindow.this.mousePressed(e);
            }
        });
        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                MainWindow.this.mouseDragged(e);
            }
        });
        
        // Transparent 16 x 16 pixel cursor image.
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

        // Create a new blank cursor.
        blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");
        
        try {
            robot = new Robot();
        } catch (AWTException e) {
            System.exit(1);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
            
            case KeyEvent.VK_A:
                toggleAnimation();
                break;
                
            case KeyEvent.VK_F:
                toggleFullScreen();
                break;
                
            case KeyEvent.VK_M:
                scene.togglePolygonMode();
                break;
        }
        panel.display();
    }

    public void mousePressed(MouseEvent e) {
        scene.setUserRotates(true);
        panel.setCursor(blankCursor);
        robot.mouseMove(panel.getWidth() / 2, panel.getWidth() / 2);
    }
    
    public void mouseReleased(MouseEvent e) {
        scene.setUserRotates(false);
        panel.setCursor(Cursor.getDefaultCursor());
    }
    
    public void mouseDragged(MouseEvent e) {
        int width = panel.getWidth();
        int height = panel.getHeight();
        
        float diffX = (e.getX() - width / 2.0f) / 5.0f;
        float diffY = (e.getY() - height / 2.0f) / 5.0f;
        
        scene.addYaw(diffX);
        scene.addPitch(diffY);
        
        if (diffX != 0 || diffY != 0) {
            Point panelLoc = panel.getLocationOnScreen();
            Point frameLoc = getLocationOnScreen();
            int offsetX = panelLoc.x - frameLoc.x;
            int offsetY = panelLoc.y - frameLoc.y;
            robot.mouseMove(offsetX + width / 2, offsetY + height / 2);
        }
        
        panel.display();
    }
    
    private void toggleAnimation() {
        if (animator.isAnimating()) {
            animator.stop();
        } else {
            animator.start();
        }
    }
    
    private void toggleFullScreen() {
        fullscreen = !fullscreen;
        
        if (animator.isAnimating()) {
            animator.stop();
        }
        
        dispose();
        setUndecorated(fullscreen);
        pack();
        
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = environment.getDefaultScreenDevice();
        
        if (fullscreen) {
            device.setFullScreenWindow(this);
        } else {
            device.setFullScreenWindow(null);
        }
        setVisible(true);
        animator.start();
    }
    
}
