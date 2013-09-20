package main;

import com.illposed.osc.OSCPacket;
import com.illposed.osc.OSCPortOut;
import geom.RenderMode;
import geom.SkyboxEnum;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import processing.core.PVector;

/**
 * Dialog for remote control of SoundBite slaves.
 * 
 * @author  Stefan Marks
 * @version 1.0 - 13.09.2013: Created
 */
public class SoundBiteRemoteController extends javax.swing.JFrame
{
    /**
     * Creates a new SoundBite controller.
     */
    public SoundBiteRemoteController()
    {
        slaves = new DefaultListModel<Slave>();
        vars   = new SoundBiteVariables();
        
        slaves.addElement(new Slave("localhost"));
        
        initComponents();
        cbxRenderMode.setSelectedItem(vars.renderMode.get());
        
        animationTimer  = new Timer();
        cameraAnimation = new CameraRotationTask();
        animationTimer.scheduleAtFixedRate(cameraAnimation, 0, 1000 / 60);
    }

    /**
     * Sets up the controls
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        javax.swing.JPanel pnlSlaves = new javax.swing.JPanel();
        javax.swing.JScrollPane scrlSlaves = new javax.swing.JScrollPane();
        lstSlaves = new javax.swing.JList();
        pnlSlaveButtons = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        pnlButtons = new javax.swing.JPanel();
        chkGuiVisible = new javax.swing.JCheckBox();
        chkPaused = new javax.swing.JCheckBox();
        chkCameraRotation = new javax.swing.JCheckBox();
        cbxRenderMode = new javax.swing.JComboBox();
        cbxSkybox = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SoundBite Remote Controller");
        getContentPane().setLayout(new java.awt.GridLayout(0, 2, 10, 0));

        pnlSlaves.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder("Slaves"), javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        pnlSlaves.setLayout(new java.awt.BorderLayout(0, 5));

        lstSlaves.setModel(slaves);
        scrlSlaves.setViewportView(lstSlaves);

        pnlSlaves.add(scrlSlaves, java.awt.BorderLayout.CENTER);

        pnlSlaveButtons.setLayout(new java.awt.GridLayout(1, 0, 5, 0));

        btnAdd.setText("Add");
        btnAdd.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnAddActionPerformed(evt);
            }
        });
        pnlSlaveButtons.add(btnAdd);

        btnRemove.setText("Remove");
        btnRemove.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnRemoveActionPerformed(evt);
            }
        });
        pnlSlaveButtons.add(btnRemove);

        pnlSlaves.add(pnlSlaveButtons, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(pnlSlaves);

        pnlButtons.setLayout(new java.awt.GridLayout(5, 0));

        chkGuiVisible.setSelected(true);
        chkGuiVisible.setText("GUI Visible:");
        chkGuiVisible.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        chkGuiVisible.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        chkGuiVisible.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                chkGuiVisibleActionPerformed(evt);
            }
        });
        pnlButtons.add(chkGuiVisible);

        chkPaused.setText("Paused:");
        chkPaused.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        chkPaused.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        chkPaused.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                chkPausedActionPerformed(evt);
            }
        });
        pnlButtons.add(chkPaused);

        chkCameraRotation.setText("Rotate Camera:");
        chkCameraRotation.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        chkCameraRotation.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        pnlButtons.add(chkCameraRotation);

        cbxRenderMode.setModel(new DefaultComboBoxModel(RenderMode.values()));
        cbxRenderMode.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cbxRenderModeActionPerformed(evt);
            }
        });
        pnlButtons.add(cbxRenderMode);

        cbxSkybox.setModel(new DefaultComboBoxModel(SkyboxEnum.values()));
        cbxSkybox.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cbxSkyboxActionPerformed(evt);
            }
        });
        pnlButtons.add(cbxSkybox);

        getContentPane().add(pnlButtons);

        pack();
    }// </editor-fold>//GEN-END:initComponents

        
    /**
     * Sends a command to enable/disable the GUI of the slaves.
     * 
     * @param evt the event that triggered this action
     */
    private void chkGuiVisibleActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_chkGuiVisibleActionPerformed
    {//GEN-HEADEREND:event_chkGuiVisibleActionPerformed
        vars.guiEnabled.set(!vars.guiEnabled.get());
        sendUpdate();
    }//GEN-LAST:event_chkGuiVisibleActionPerformed

       
    /**
     * Sends a command that pauses/unpauses the slaves.
     * 
     * @param evt the event that triggered this action
     */ 
    private void chkPausedActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_chkPausedActionPerformed
    {//GEN-HEADEREND:event_chkPausedActionPerformed
        vars.recordingPaused.set(!vars.recordingPaused.get());
        sendUpdate();
    }//GEN-LAST:event_chkPausedActionPerformed

    
    /**
     * Adds a new slave to the list.
     * 
     * @param evt the event that triggered this action
     */
    private void btnAddActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnAddActionPerformed
    {//GEN-HEADEREND:event_btnAddActionPerformed
        String hostname = JOptionPane.showInputDialog(this,
            "Enter the IP address of hostname of the slave to add:", 
            "Add new slave",
            JOptionPane.PLAIN_MESSAGE);
        if ( (hostname != null) && !hostname.isEmpty() )
        {
            Slave slave = new Slave(hostname);
            if ( slave.isActive() )
            {
                slaves.addElement(slave);
            }
            else
            {
                JOptionPane.showMessageDialog(this, 
                    "Slave could not be added!",
                    "Error while adding slave",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnAddActionPerformed

    
    /***
     * Removes selected slaves from the list.
     * 
     * @param evt event that triggered this action
     */
    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnRemoveActionPerformed
    {//GEN-HEADEREND:event_btnRemoveActionPerformed
        List selected = lstSlaves.getSelectedValuesList();
        if ( selected.isEmpty() ) return;
        
        // create message
        String msg = "Do you really want to remove the following slaves:\n";
        for ( Object slave : selected )
        {
            msg += ((Slave) slave).toString() + "\n";
        }
        // ask user
        int choice = JOptionPane.showConfirmDialog(this,
            msg,
            "Remove slaves",
            JOptionPane.YES_NO_OPTION);
        // remove entries
        if ( choice == JOptionPane.YES_OPTION )
        {
            for ( Object object : selected )
            {
                slaves.removeElement(object);
            }
        }
    }//GEN-LAST:event_btnRemoveActionPerformed

    
    private void cbxRenderModeActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cbxRenderModeActionPerformed
    {//GEN-HEADEREND:event_cbxRenderModeActionPerformed
        vars.renderMode.set((RenderMode) cbxRenderMode.getSelectedItem());
        sendUpdate();
    }//GEN-LAST:event_cbxRenderModeActionPerformed

    
    private void cbxSkyboxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cbxSkyboxActionPerformed
    {//GEN-HEADEREND:event_cbxSkyboxActionPerformed
        vars.skybox.set((SkyboxEnum) cbxSkybox.getSelectedItem());
        sendUpdate();
    }//GEN-LAST:event_cbxSkyboxActionPerformed

    
    /**
     * Sends a message only with updated values.
     */
    private void sendUpdate()
    {
        OSCPacket packet  = vars.getUpdatePacket();
        Object[]  targets = lstSlaves.getSelectedValuesList().toArray();
        if ( targets.length == 0 )
        {
            targets = slaves.toArray();
        }
        for ( Object slave : targets )
        {
            ((Slave) slave).sendPacket(packet);
        }
    }
    
    
    private class CameraRotationTask extends TimerTask
    {
        @Override
        public void run()
        {
            if ( !chkCameraRotation.isSelected() ) return;
            
            PVector rot = vars.camRot.get();
            rot.y += 0.125;
            if ( rot.y > 360 ) { rot.y -= 360; }
            rot.x = 30 * (float) Math.sin(Math.toRadians(rot.y));
            vars.camRot.set(rot);
            sendUpdate();
        }
        
        float camRotX, camRotY;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                JFrame app = new SoundBiteRemoteController();
                app.setLocationRelativeTo(null);
                app.setVisible(true);
            }
        });
    }

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnRemove;
    private javax.swing.JComboBox cbxRenderMode;
    private javax.swing.JComboBox cbxSkybox;
    private javax.swing.JCheckBox chkCameraRotation;
    private javax.swing.JCheckBox chkGuiVisible;
    private javax.swing.JCheckBox chkPaused;
    private javax.swing.JList lstSlaves;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlSlaveButtons;
    // End of variables declaration//GEN-END:variables

    private DefaultListModel<Slave>  slaves;
    private SoundBiteVariables       vars;
    private Timer                    animationTimer;
    private CameraRotationTask       cameraAnimation;
    
    /**
     * Class for encapsulating slaves, their state, and the output for the list model.
     */
    private class Slave
    {
        public Slave(String hostname)
        {
            this.address = null; 
            this.port    = null;
            try
            {
                this.address = InetAddress.getByName(hostname);
                port = new OSCPortOut(address);
            }
            catch ( UnknownHostException e )
            {
                System.err.println("Could not add slaves");
            }
            catch ( SocketException e )
            {
                System.err.println("Could not open OSC port");
            }
        }
        
        public boolean isActive()
        {
            return (port != null);
        }
        
        public void sendPacket(OSCPacket packet)
        {
            try
            {
                port.send(packet);
            }
            catch (IOException e)
            {
                System.err.println("Could not send packet to slave '" + address + "'");
            }
        }
        
        @Override
        public String toString()
        {
            return address.getHostAddress();
        }
        
        private InetAddress address;
        private OSCPortOut  port;
    }
}
