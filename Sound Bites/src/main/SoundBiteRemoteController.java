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
import shaper.ColourMapperEnum;
import shaper.ShaperEnum;

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
        java.awt.GridBagConstraints gridBagConstraints;

        javax.swing.JPanel pnlSlaves = new javax.swing.JPanel();
        javax.swing.JScrollPane scrlSlaves = new javax.swing.JScrollPane();
        lstSlaves = new javax.swing.JList();
        pnlSlaveButtons = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        javax.swing.JPanel pnlButtons = new javax.swing.JPanel();
        javax.swing.JPanel pnlGUI = new javax.swing.JPanel();
        javax.swing.JLabel lblGuiVisible = new javax.swing.JLabel();
        btnGuiVisible = new javax.swing.JToggleButton();
        javax.swing.JPanel pnlRendering = new javax.swing.JPanel();
        javax.swing.JLabel lblShaper = new javax.swing.JLabel();
        cbxShaper = new javax.swing.JComboBox();
        javax.swing.JLabel lblMapper = new javax.swing.JLabel();
        cbxMapper = new javax.swing.JComboBox();
        javax.swing.JLabel lblRenderMode = new javax.swing.JLabel();
        cbxRenderMode = new javax.swing.JComboBox();
        javax.swing.JLabel lblShapelblSkyboxr3 = new javax.swing.JLabel();
        cbxSkybox = new javax.swing.JComboBox();
        javax.swing.JPanel pnlCamera = new javax.swing.JPanel();
        javax.swing.JLabel lblCameraAutoRotate = new javax.swing.JLabel();
        btnCameraAutoRotate = new javax.swing.JToggleButton();
        javax.swing.JPanel pnlRecording = new javax.swing.JPanel();
        javax.swing.JLabel lblRecordingPause = new javax.swing.JLabel();
        btnAudioRecording = new javax.swing.JToggleButton();
        javax.swing.JPanel pnlFill = new javax.swing.JPanel();

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

        pnlButtons.setAlignmentY(0.0F);
        pnlButtons.setLayout(new java.awt.GridBagLayout());

        pnlGUI.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder("GUI"), javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        pnlGUI.setLayout(new java.awt.GridBagLayout());

        lblGuiVisible.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblGuiVisible.setLabelFor(btnGuiVisible);
        lblGuiVisible.setText("GUI visibility:");
        lblGuiVisible.setMinimumSize(new java.awt.Dimension(100, 14));
        lblGuiVisible.setPreferredSize(new java.awt.Dimension(100, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        pnlGUI.add(lblGuiVisible, gridBagConstraints);

        btnGuiVisible.setSelected(true);
        btnGuiVisible.setText("Visible");
        btnGuiVisible.setMinimumSize(new java.awt.Dimension(50, 20));
        btnGuiVisible.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnGuiVisibleActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 10.0;
        pnlGUI.add(btnGuiVisible, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 1.0;
        pnlButtons.add(pnlGUI, gridBagConstraints);

        pnlRendering.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder("Rendering"), javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        pnlRendering.setLayout(new java.awt.GridBagLayout());

        lblShaper.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblShaper.setLabelFor(cbxShaper);
        lblShaper.setText("Shaper:");
        lblShaper.setMinimumSize(new java.awt.Dimension(100, 14));
        lblShaper.setPreferredSize(new java.awt.Dimension(100, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        pnlRendering.add(lblShaper, gridBagConstraints);

        cbxShaper.setModel(new DefaultComboBoxModel(ShaperEnum.values()));
        cbxShaper.setMinimumSize(new java.awt.Dimension(50, 20));
        cbxShaper.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cbxShaperActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 10.0;
        pnlRendering.add(cbxShaper, gridBagConstraints);

        lblMapper.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblMapper.setLabelFor(cbxMapper);
        lblMapper.setText("Colour Mapper:");
        lblMapper.setMinimumSize(new java.awt.Dimension(100, 14));
        lblMapper.setPreferredSize(new java.awt.Dimension(100, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 10);
        pnlRendering.add(lblMapper, gridBagConstraints);

        cbxMapper.setModel(new DefaultComboBoxModel(ColourMapperEnum.values()));
        cbxMapper.setMinimumSize(new java.awt.Dimension(50, 20));
        cbxMapper.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cbxMapperActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 10.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        pnlRendering.add(cbxMapper, gridBagConstraints);

        lblRenderMode.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblRenderMode.setLabelFor(cbxRenderMode);
        lblRenderMode.setText("Render mode:");
        lblRenderMode.setMinimumSize(new java.awt.Dimension(100, 14));
        lblRenderMode.setPreferredSize(new java.awt.Dimension(100, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 10);
        pnlRendering.add(lblRenderMode, gridBagConstraints);

        cbxRenderMode.setModel(new DefaultComboBoxModel(RenderMode.values()));
        cbxRenderMode.setMinimumSize(new java.awt.Dimension(50, 20));
        cbxRenderMode.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cbxRenderModeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 10.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        pnlRendering.add(cbxRenderMode, gridBagConstraints);

        lblShapelblSkyboxr3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblShapelblSkyboxr3.setLabelFor(cbxSkybox);
        lblShapelblSkyboxr3.setText("Skybox:");
        lblShapelblSkyboxr3.setMinimumSize(new java.awt.Dimension(100, 14));
        lblShapelblSkyboxr3.setPreferredSize(new java.awt.Dimension(100, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 10);
        pnlRendering.add(lblShapelblSkyboxr3, gridBagConstraints);

        cbxSkybox.setModel(new DefaultComboBoxModel(SkyboxEnum.values()));
        cbxSkybox.setMinimumSize(new java.awt.Dimension(50, 20));
        cbxSkybox.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cbxSkyboxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 10.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        pnlRendering.add(cbxSkybox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        pnlButtons.add(pnlRendering, gridBagConstraints);

        pnlCamera.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder("Camera"), javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        pnlCamera.setLayout(new java.awt.GridBagLayout());

        lblCameraAutoRotate.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblCameraAutoRotate.setLabelFor(btnCameraAutoRotate);
        lblCameraAutoRotate.setText("Auto Rotation:");
        lblCameraAutoRotate.setMinimumSize(new java.awt.Dimension(100, 14));
        lblCameraAutoRotate.setPreferredSize(new java.awt.Dimension(100, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        pnlCamera.add(lblCameraAutoRotate, gridBagConstraints);

        btnCameraAutoRotate.setText("Off");
        btnCameraAutoRotate.setMinimumSize(new java.awt.Dimension(50, 20));
        btnCameraAutoRotate.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnCameraAutoRotateActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 10.0;
        pnlCamera.add(btnCameraAutoRotate, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        pnlButtons.add(pnlCamera, gridBagConstraints);

        pnlRecording.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder("Audio"), javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        pnlRecording.setLayout(new java.awt.GridBagLayout());

        lblRecordingPause.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblRecordingPause.setLabelFor(btnAudioRecording);
        lblRecordingPause.setText("Realtime Signal:");
        lblRecordingPause.setMinimumSize(new java.awt.Dimension(100, 14));
        lblRecordingPause.setPreferredSize(new java.awt.Dimension(100, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        pnlRecording.add(lblRecordingPause, gridBagConstraints);

        btnAudioRecording.setSelected(true);
        btnAudioRecording.setText("Recording");
        btnAudioRecording.setMinimumSize(new java.awt.Dimension(50, 20));
        btnAudioRecording.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnAudioRecordingActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 10.0;
        pnlRecording.add(btnAudioRecording, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        pnlButtons.add(pnlRecording, gridBagConstraints);

        pnlFill.setLayout(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weighty = 1.0;
        pnlButtons.add(pnlFill, gridBagConstraints);

        getContentPane().add(pnlButtons);

        pack();
    }// </editor-fold>//GEN-END:initComponents

        
       
    
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

    
    private void cbxShaperActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cbxShaperActionPerformed
    {//GEN-HEADEREND:event_cbxShaperActionPerformed
        vars.shaper.set((ShaperEnum) cbxShaper.getSelectedItem());
        sendUpdate();
    }//GEN-LAST:event_cbxShaperActionPerformed

    
    private void cbxMapperActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cbxMapperActionPerformed
    {//GEN-HEADEREND:event_cbxMapperActionPerformed
        vars.mapper.set((ColourMapperEnum) cbxMapper.getSelectedItem());
        sendUpdate();
    }//GEN-LAST:event_cbxMapperActionPerformed

    
    private void btnGuiVisibleActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnGuiVisibleActionPerformed
    {//GEN-HEADEREND:event_btnGuiVisibleActionPerformed
        boolean visible = btnGuiVisible.isSelected();
        btnGuiVisible.setText(visible ? "Visible" : "Hidden");
        vars.guiEnabled.set(visible);
        sendUpdate();
    }//GEN-LAST:event_btnGuiVisibleActionPerformed

    
    private void btnCameraAutoRotateActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnCameraAutoRotateActionPerformed
    {//GEN-HEADEREND:event_btnCameraAutoRotateActionPerformed
        btnCameraAutoRotate.setText(btnCameraAutoRotate.isSelected() ? "On" : "Off");
    }//GEN-LAST:event_btnCameraAutoRotateActionPerformed

    
    private void btnAudioRecordingActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnAudioRecordingActionPerformed
    {//GEN-HEADEREND:event_btnAudioRecordingActionPerformed
        boolean paused = btnAudioRecording.isSelected();
        vars.audioRecording.set(paused);
        btnAudioRecording.setText(paused ? "Recording" : "Paused");
        sendUpdate();
    }//GEN-LAST:event_btnAudioRecordingActionPerformed

    
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
            if ( !btnCameraAutoRotate.isSelected() ) return;
            
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
    private javax.swing.JToggleButton btnAudioRecording;
    private javax.swing.JToggleButton btnCameraAutoRotate;
    private javax.swing.JToggleButton btnGuiVisible;
    private javax.swing.JButton btnRemove;
    private javax.swing.JComboBox cbxMapper;
    private javax.swing.JComboBox cbxRenderMode;
    private javax.swing.JComboBox cbxShaper;
    private javax.swing.JComboBox cbxSkybox;
    private javax.swing.JList lstSlaves;
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
