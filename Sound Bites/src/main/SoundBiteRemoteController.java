package main;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

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
        params = new LinkedList<Object>();
        
        slaves.addElement(new Slave("localhost"));
        
        initComponents();
        
    }

    /**
     * Sets up the controls
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        chkGuiVisible = new javax.swing.JCheckBox();
        chkPaused = new javax.swing.JCheckBox();
        javax.swing.JPanel pnlSlaves = new javax.swing.JPanel();
        javax.swing.JScrollPane scrlSlaves = new javax.swing.JScrollPane();
        lstSlaves = new javax.swing.JList();
        pnlSlaveButtons = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SoundBite Remote Controller");

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlSlaves, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 82, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkGuiVisible, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPaused, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlSlaves, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chkGuiVisible)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkPaused)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

        
    /**
     * Sends a command to enable/disable the GUI of the slaves.
     * 
     * @param evt the event that triggered this action
     */
    private void chkGuiVisibleActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_chkGuiVisibleActionPerformed
    {//GEN-HEADEREND:event_chkGuiVisibleActionPerformed
        sendMessage("/enableGUI", chkGuiVisible.isSelected());
    }//GEN-LAST:event_chkGuiVisibleActionPerformed

       
    /**
     * Sends a command that pauses/unpauses the slaves.
     * 
     * @param evt the event that triggered this action
     */ 
    private void chkPausedActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_chkPausedActionPerformed
    {//GEN-HEADEREND:event_chkPausedActionPerformed
        sendMessage("/paused", chkPaused.isSelected());
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

    /**
     * Sends a message to the slaves.
     * 
     * @param address the OSC address
     * @param param1  the first parameter
     * @param param2  the second parameter
     */
    private void sendMessage(String address, Object param1, Object param2)
    {
        params.clear();
        params.add(param1);
        params.add(param2);
        sendMessage(address, params);
    }
    
    
    /**
     * Sends a message to the slaves.
     * 
     * @param address the OSC address
     * @param param1  the first parameter
     */
    private void sendMessage(String address, Object param1)
    {
        params.clear();
        params.add(param1);
        sendMessage(address, params);
    }

    
    /**
     * Sends a message to the slaves.
     * 
     * @param address     the OSC address
     * @param parameters  the list of message parameters
     */
    private void sendMessage(String address, Collection<Object> parameters)
    {
        OSCMessage msg = new OSCMessage(address, parameters);
        Object[] targets = lstSlaves.getSelectedValuesList().toArray();
        if ( targets.length == 0 )
        {
            targets = slaves.toArray();
        }
        for ( Object slave : targets )
        {
            ((Slave) slave).sendMessage(msg);
        }
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
    private javax.swing.JCheckBox chkGuiVisible;
    private javax.swing.JCheckBox chkPaused;
    private javax.swing.JList lstSlaves;
    private javax.swing.JPanel pnlSlaveButtons;
    // End of variables declaration//GEN-END:variables

    private DefaultListModel<Slave>  slaves;
    private LinkedList<Object>       params;
    
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
        
        public void sendMessage(OSCMessage message)
        {
            try
            {
                port.send(message);
            }
            catch (IOException e)
            {
                System.err.println("Could not send message '" + message.getAddress() +
                                   "' to slave '" + address + "'");
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
