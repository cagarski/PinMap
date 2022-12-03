/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package map;

import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.*;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.MapClickListener;
import org.jxmapviewer.input.*;
import org.jxmapviewer.*;
import java.awt.event.*;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.jxmapviewer.viewer.WaypointPainter;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.DefaultWaypoint;
import java.awt.Dimension;
import javax.swing.UIManager;
import java.awt.Font;
import javax.swing.plaf.FontUIResource;
import java.io.*;
import javax.swing.JOptionPane;

/**
 * @author Jordan, Clare, Wesley
 * This is the main function. It handles the map display, map clicks, database query and return display. 
 * Customizable aspects include: home state bounds, how the returned data is displayed. 
 */

public class Main extends javax.swing.JFrame {

    /**
     * Creates new form Main
     * @param bounds
     */
    public Main(int bounds) {
        // initalize everything
        initComponents();
        defineCoords(bounds);
        startMap();
        this.setTitle("Colorado School of Mines Visitor Pin Map");
        this.setExtendedState(Main.MAXIMIZED_BOTH);
    }
   
    int selection = 0;
    public void defineCoords(int choice) {
        // define which map the user should start with (1 = state, 2 = us, 3 = world
        selection = choice;
    }
    
    private void startMap() {

        // start the map display 
        System.out.println("Current Choice " + selection);
       
        // define variables for map
        double lon = 0.0;
        double lat = 0.0;
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        jxMaps.setTileFactory(tileFactory);
        
        // set inital map load bounds
        if (selection == 1) {       
            // this is where you can cusomize the home state map bounds
            GeoPosition geo = new GeoPosition(39.287, -105.710);
            jxMaps.setAddressLocation(geo);
            jxMaps.setZoom(11);
        } else if (selection == 2) {
            GeoPosition geo = new GeoPosition(39, -96);
            jxMaps.setAddressLocation(geo);
            jxMaps.setZoom(14);
        } else if (selection == 3) {
            GeoPosition geo = new GeoPosition(13, 18);
            jxMaps.setAddressLocation(geo);
            jxMaps.setZoom(16);
        } else {
            System.out.println("Something went wrong you shouldn't have gotten here.");
            System.exit(-1);
        }
        
        //mouse listener variables
        PanMouseInputListener move = new PanMouseInputListener(jxMaps);
        jxMaps.addMouseListener(move);
        jxMaps.addMouseMotionListener(move);
        jxMaps.addMouseWheelListener(new ZoomMouseWheelListenerCenter(jxMaps));
        
        // set the zoom bounds on map, this means you can;t zoom out infinatly 
        jxMaps.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e){
                if(jxMaps.getZoom() > 16) {
                    jxMaps.setZoom(16);
                }
            }
        });
        
        // data structure for pin graphic location 
        Set<Waypoint> waypoints = new HashSet<Waypoint>();
        
        //Mouse Listener to get latitude and longitude from where the user clicks
        jxMaps.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON3){
                    java.awt.Point p = e.getPoint();
                    GeoPosition newLoc = jxMaps.convertPointToGeoPosition(p);
                    System.out.println("X:"+newLoc.getLatitude()+",Y:"+newLoc.getLongitude());
                    addWaypoint(newLoc);
                    Location click = new Location(newLoc.getLatitude(), newLoc.getLongitude());
                    click.printLocation();
                    String s = null;
                    
                    try {
                        // dummy variables are needed for state and county is the click was outside the us
                        if (!click.country.equals("us")) {
                            click.state = "na";
                            click.county = "na";
                        }
                        
                        // process builder to run the python database script
                        ProcessBuilder builder = new ProcessBuilder("python", "C:/PinMap/PinMap/build/classes/generate_shelf_locs.py", click.country, click.state, click.county);
                        Process process = builder.start();
                        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        
                        // read the output from the python script, print to terminal as check 
                        System.out.println("Here is the standard output of the command:\n");
                        String answer = "";
                        while ((s = stdInput.readLine()) != null) {
                            answer = s;
                            System.out.println(s);
                        }
                        
                        // create pop up window with python script output 
                        // these are cosmetic settings 
                        UIManager.put("OptionPane.minimumSize",new Dimension(500,200)); 
                        UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("Arial", Font.BOLD, 40))); 
                        UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.PLAIN, 30));
                        // these are your button options
                        String []options = {"Add Another Location", "Start Over"};
                        // display the pop up and save whicgh button the user clicked
                        selection = JOptionPane.showOptionDialog(rootPane, answer, "FIND A MAP", WIDTH, JOptionPane.PLAIN_MESSAGE, null, options, EXIT_ON_CLOSE);
                        System.out.println(selection);
                        
                        // determine what to do based on button click (0 = back to map, 1 = home screen)
                        switch(selection) {
                            case 1:     // go to home
                                // make homescreen and destroy map screen
                                new Home_Screen().setVisible(true);
                                dispose();
                                break;
                            default:    // close pop up
                                addWaypoint(null);      // this erases waypoints
                                break;
                        }
                    }
                    catch (IOException except) {        // error check 
                        System.out.println("exception happened - here's what I know: ");
                        except.printStackTrace();
                        System.exit(-1);
                    } 
                }
            }
        });
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jxMaps = new org.jxmapviewer.JXMapViewer();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout jxMapsLayout = new javax.swing.GroupLayout(jxMaps);
        jxMaps.setLayout(jxMapsLayout);
        jxMapsLayout.setHorizontalGroup(
            jxMapsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 764, Short.MAX_VALUE)
        );
        jxMapsLayout.setVerticalGroup(
            jxMapsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 595, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jxMaps, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jxMaps, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param location
     */
    // this function displays the waypoints
    public void addWaypoint(GeoPosition location) {
        //create a set of waypoints
        Set<Waypoint> waypoints = new HashSet<Waypoint>();
        // if returning to the map after pop up, null is passed to this function and waypoints will be erased 
        if (location != null) {
            waypoints.add(new DefaultWaypoint(location));
        }
        //create a WaypointPainter to draw the points
        WaypointPainter painter = new WaypointPainter();
        painter.setWaypoints(waypoints);
        jxMaps.setOverlayPainter(painter);
    }
    
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
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main(0).setVisible(true);
            }
        });
    }
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jxmapviewer.JXMapViewer jxMaps;
    // End of variables declaration//GEN-END:variables
}
