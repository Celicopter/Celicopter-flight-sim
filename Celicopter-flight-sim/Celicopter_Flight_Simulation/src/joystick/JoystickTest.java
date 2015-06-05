package joystick;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JToggleButton;

import net.java.games.input.Component;
import net.java.games.input.Controller;

/**
 *
 * Joystick Test with JInput
 * 
 * 
 * @author TheUzo007
 *         http://theuzo007.wordpress.com
 * 
 * 
 * Test for joysticks of stick or gamepad type (JInput type), 
 * like Logitech Dual Action which is a stick type or
 * Xbox MadCatz which is a gamepad type.
 * 
 */

public class JoystickTest {
    
    public static void main(String args[]) {
        final JFrameWindow window = new JFrameWindow();
        
        /*
        JInputJoystickTest jinputJoystickTest = new JInputJoystickTest();
        // Writes (into console) informations of all controllers that are found.
        jinputJoystickTest.getAllControllersInfo();
        // In loop writes (into console) all joystick components and its current values.
        jinputJoystickTest.pollControllerAndItsComponents(Controller.Type.STICK);
        //jinputJoystickTest.pollControllerAndItsComponents(Controller.Type.GAMEPAD);
        */
        
        
        // Test for joystick of stick or gamepad type.
        //stickOrGamepadTypeJoystick_Test(window);
        stickOrGamepadTypeJoystick_Test_Better(window);
        
        // Test for joystick of stick type.
        //stickTypeJoystick_Test(window);
    }
    
    
    
    
    /*
     * Test for stick and gamepad type of joystick.
     * The same as stickOrGamepadTypeJoystick_Test but better/easier/cleaner for right controller joystick.
     */
    private static void stickOrGamepadTypeJoystick_Test_Better(JFrameWindow window)
    {
        // Creates controller
        JInputJoystick joystick = new JInputJoystick(Controller.Type.STICK, Controller.Type.GAMEPAD);
        
        // Checks if the controller was found.
        if( !joystick.isControllerConnected() ){
            window.setControllerName("No controller found!");
            return;
        }
        
        // Sets controller name.
        window.setControllerName(joystick.getControllerName());
        
        // If gamepad, change name for axis.
        if(joystick.getControllerType() == Controller.Type.GAMEPAD)
        {
            window.setProgressBar1Name("X Rotation");
            window.setProgressBar2Name("Y Rotation");
        }
        // Stick type
        else
        {
            window.setProgressBar3Name("");
            window.hideProgresBar3();
        }
        
        while(true)
        {
            // Gets current state of joystick! And checks, if joystick is disconnected, break while loop.
            if( !joystick.pollController() ) {
                window.setControllerName("Controller disconnected!");
                break;
            }
            
            // Left controller joystick
            int xValuePercentageLeftJoystick = joystick.getX_LeftJoystick_Percentage();
            int yValuePercentageLeftJoystick = joystick.getY_LeftJoystick_Percentage();
            window.setXYAxis(xValuePercentageLeftJoystick, yValuePercentageLeftJoystick);
            
            // Right controller joystick
            int xValuePercentageRightJoystick = joystick.getX_RightJoystick_Percentage();
            int yValuePercentageRightJoystick = joystick.getY_RightJoystick_Percentage();
            window.setZAxis(xValuePercentageRightJoystick);
            window.setZRotation(yValuePercentageRightJoystick);
            
            // If controller is a gamepad type. 
            if(joystick.getControllerType() == Controller.Type.GAMEPAD)
            { // Must check if controller is a gamepad, because stick type controller also have Z axis but it's for right controller joystick.
                // If Z Axis exists.
                if(joystick.componentExists(Component.Identifier.Axis.Z)){
                    int zAxisValuePercentage = joystick.getZAxisPercentage();
                    window.setZAxisGamepad(zAxisValuePercentage);
                }
            }
            
            // Sets controller buttons
            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));
            buttonsPanel.setBounds(6, 19, 246, 110);
            ArrayList buttonsValues = joystick.getButtonsValues();
            for(int i=0; i < buttonsValues.size(); i++) {
                JToggleButton aToggleButton = new JToggleButton(""+(i+1), (Boolean) buttonsValues.get(i));
                aToggleButton.setPreferredSize(new Dimension(48, 25));
                aToggleButton.setEnabled(false);
                buttonsPanel.add(aToggleButton);
            }
            window.setControllerButtons(buttonsPanel);
            
            // Hat Switch
            float hatSwitchPosition = joystick.getHatSwitchPosition();
            window.setHatSwitch(hatSwitchPosition);
            
            try {
                Thread.sleep(20);
            } catch (InterruptedException ex) {
                Logger.getLogger(JoystickTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    
    
    /*
     * Test for stick and gamepad type of joystick.
     */
    private static void stickOrGamepadTypeJoystick_Test(JFrameWindow window)
    {
        // Creates controller
        JInputJoystick joystick = new JInputJoystick(Controller.Type.STICK, Controller.Type.GAMEPAD);
        
        // Checks if the controller was found.
        if( !joystick.isControllerConnected() ){
            window.setControllerName("No controller found!");
            return;
        }
        
        // Sets controller name.
        window.setControllerName(joystick.getControllerName());
        
        // Set axis names and progress bar visibility on the frame/window of the program.
        if(joystick.getControllerType() == Controller.Type.GAMEPAD)
        {
            // Gamepad
            window.setProgressBar1Name("X Rotation");
            window.setProgressBar2Name("Y Rotation");
        } else {
            // Stick
            window.setProgressBar3Name("");
            window.hideProgresBar3();
        }
        
        while(true)
        {
            // Gets current state of joystick! And checks, if joystick is disconnected, break while loop.
            if( !joystick.pollController() ) {
                window.setControllerName("Controller disconnected!");
                break;
            }
            
            // x axis & y axis (left joystick on controller)
            int xAxisValuePercentage = joystick.getXAxisPercentage();
            int yAxisValuePercentage = joystick.getYAxisPercentage();
            window.setXYAxis(xAxisValuePercentage, yAxisValuePercentage);
            
            // stick type controller
            if(joystick.getControllerType() == Controller.Type.STICK)
            {
                // z axis & z rotation (right joystick on controller)
                int zAxisValuePercentage = joystick.getZAxisPercentage();
                window.setZAxis(zAxisValuePercentage);
                int zRotationValuePercentage = joystick.getZRotationPercentage();
                window.setZRotation(zRotationValuePercentage);
            }
            // gamepad type controller
            else
            {
                // x rotation & y rotation (right joystick on controller)
                int xRotationValuePercentage = joystick.getXRotationPercentage();
                window.setZAxis(xRotationValuePercentage);
                int yRotationValuePercentage = joystick.getYRotationPercentage();
                window.setZRotation(yRotationValuePercentage);
                
                // Checks if z axis exists.
                if(joystick.componentExists(Component.Identifier.Axis.Z)){
                    int zAxisValuePercentage = joystick.getZAxisPercentage();
                    window.setZAxisGamepad(zAxisValuePercentage);
                }
            }
            
            // Sets controller buttons
            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));
            buttonsPanel.setBounds(6, 19, 246, 110);
            ArrayList<Boolean> buttonsValues = joystick.getButtonsValues();
            for(int i=0; i < buttonsValues.size(); i++) {
                JToggleButton aToggleButton = new JToggleButton(""+(i+1), buttonsValues.get(i));
                aToggleButton.setPreferredSize(new Dimension(48, 25));
                aToggleButton.setEnabled(false);
                buttonsPanel.add(aToggleButton);
            }
            window.setControllerButtons(buttonsPanel);
            
            // Hat Switch
            float hatSwitchPosition = joystick.getHatSwitchPosition();
            window.setHatSwitch(hatSwitchPosition);
            
            try {
                Thread.sleep(20);
            } catch (InterruptedException ex) {
                Logger.getLogger(JoystickTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    
    
    /*
     * Test only for joystick of stick type.
     */
    private static void stickTypeJoystick_Test(JFrameWindow window)
    {
        // Creates controller
        JInputJoystick joystick = new JInputJoystick(Controller.Type.STICK);
        
        // Checks if the controller was found.
        if( !joystick.isControllerConnected() ){
            window.setControllerName("No controller found!");
            return;
        }
        
        // Sets controller name.
        window.setControllerName(joystick.getControllerName());
        
        while(true)
        {
            // Gets current state of joystick! And checks if joystick is disconnected, break while loop.
            if( !joystick.pollController() ) {
                window.setControllerName("Controller disconnected!");
                break;
            }
            
            // x axis & y axis (left joystick on controller)
            int xAxisValuePercentage = joystick.getXAxisPercentage();
            int yAxisValuePercentage = joystick.getYAxisPercentage();
            window.setXYAxis(xAxisValuePercentage, yAxisValuePercentage);
            
            // z axis & z rotation (right joystick on controller)
            int zAxisValuePercentage = joystick.getZAxisPercentage();
            window.setZAxis(zAxisValuePercentage);
            int zRotationValuePercentage = joystick.getZRotationPercentage();
            window.setZRotation(zRotationValuePercentage);
            
            // Sets controller buttons
            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));
            buttonsPanel.setBounds(6, 19, 246, 110);
            ArrayList<Boolean> buttonsValues = joystick.getButtonsValues();
            for(int i=0; i < buttonsValues.size(); i++) {
                JToggleButton aToggleButton = new JToggleButton(""+(i+1), buttonsValues.get(i));
                aToggleButton.setPreferredSize(new Dimension(48, 25));
                aToggleButton.setEnabled(false);
                buttonsPanel.add(aToggleButton);
            }
            window.setControllerButtons(buttonsPanel);
            
            // Hat Switch
            float hatSwitchPosition = joystick.getHatSwitchPosition();
            window.setHatSwitch(hatSwitchPosition);
            
            try {
                Thread.sleep(20);
            } catch (InterruptedException ex) {
                Logger.getLogger(JoystickTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
