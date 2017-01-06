package org.usfirst.frc.team4009.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import org.usfirst.frc.team4009.robot.commands.ExampleCommand;
import org.usfirst.frc.team4009.robot.subsystems.ExampleSubsystem;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	public static final ExampleSubsystem exampleSubsystem = new ExampleSubsystem();
	public static OI oi;

    Command autonomousCommand;
    SendableChooser chooser;
     CANTalon can0, can1,can2,can3,can4,can5,can6,can7;
     RobotDrive ac,eg,db,hf;
     Joystick joystick;
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
		oi = new OI();
        chooser = new SendableChooser();
        chooser.addDefault("Default Auto", new ExampleCommand());
//        chooser.addObject("My Auto", new MyAutoCommand());
        SmartDashboard.putData("Auto mode", chooser);
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        //::: Initialize objects! Each CANTalon # is the same channel number
        //::: ex.  can0 is on channel, can1 is on channel 1
        /**
         * private CANTalon can0, can1,can2,can3,can4,can5,can6,can7;
         * private RobotDrive ac,eg,db,hf;
         * private Joystick joystick;
         */
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        joystick = new Joystick(0);
        
        can0 = new CANTalon(0);
        can1 = new CANTalon(1);
        can2 = new CANTalon(2);
        can3 = new CANTalon(3);
        can4 = new CANTalon(4);
        can5 = new CANTalon(5);
        can6 = new CANTalon(6);
        can7 = new CANTalon(7);
        
        ac = new RobotDrive(can7,can4);
        eg = new RobotDrive(can6,can2);
        db = new RobotDrive(can1,can3);
        hf = new RobotDrive(can5,can0);

    }
	
	/**
     * This function is called once each time the robot enters Disabled mode.
     * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
     */
    public void disabledInit(){

    }
	
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
		//System.out.println(" Twist " + joystick.getTwist());
		//System.out.println(" X " + joystick.getX());
		//System.out.println(" Y " + joystick.getY());
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select between different autonomous modes
	 * using the dashboard. The sendable chooser code works with the Java SmartDashboard. If you prefer the LabVIEW
	 * Dashboard, remove all of the chooser code and uncomment the getString code to get the auto name from the text box
	 * below the Gyroa
	 *
	 * You can add additional auto modes by adding additional commands to the chooser code above (like the commented example)
	 * or additional comparisons to the switch structure below with additional strings & commands.
	 */
    public void autonomousInit() {
        autonomousCommand = (Command) chooser.getSelected();
        
		/* String autoSelected = SmartDashboard.getString("Auto Selector", "Default");
		switch(autoSelected) {
		case "My Auto":
			autonomousCommand = new MyAutoCommand();
			break;
		case "Default Auto":
		default:
			autonomousCommand = new ExampleCommand();
			break;
		} */
    	
    	// schedule the autonomous command (example)
        if (autonomousCommand != null) autonomousCommand.start();
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
    }

    public void teleopInit() {
		// This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to 
        // continue until interrupted by another command, remove
        // this line or comment it out.
        if (autonomousCommand != null) autonomousCommand.cancel();
    }

    /**
     * This function is called periodically during operator control
     */

        //////////////////////////////////////////////////////////////////

    public void teleopPeriodic() {
        Scheduler.getInstance().run();
    
    	double xPwr;
    	double yPwr;
    	double zPwr;
    	double highPwr;
    	double oddQuad;
    	double evenQuad;
    	double pScale;
    	
    	double DZ = 0.1;
    	double twistDZ = 0.2;
    	
    	double xCur;
    	double yCur;
    	double zCur;
    	double highCur;
    	double oddCur;
    	double evenCur;
    	boolean isPressed;
    	double pMag;
    	
    	while(isOperatorControl() && isEnabled()){

    		xPwr = joystick.getX();
    		yPwr = -joystick.getY();
    		zPwr = joystick.getTwist();
    		highPwr = Math.max(Math.abs(xPwr), Math.abs(yPwr));
    		oddQuad = yPwr - xPwr;
    		evenQuad = yPwr + xPwr;	
    		isPressed = joystick.getTrigger();
    		pMag = (joystick.getThrottle() + 1) / 2;
    		
    	// Precision Mode
    	   /**	the reason we add .5 to pMag then divide by 2 is to change the effective
    		*	range of pMag from the throttle range of 0-1 to 0.25-0.75  
    		*	A scale factor of zero would just be stopped, and a scale factor of 1 would 
    		*	be full speed and thus pointless for precision mode.  You can change the
    		*	function to create whatever precision range driver finds useful. 
    		*/
    		if (isPressed==true){
    			pScale=((pMag+0.5)/2);
    		}
    		else{
    			pScale=1;
    		}
    		
    		 		
    	// The Cur Variables 
    	   /**	Cur variables are meant to smooth the drive speed coming in and out of
    	 	*	the dead zone so that the robot drives less "choppy" at slower speeds
    		*/
    		
    	// xCur
    		if (Math.abs(xPwr)<DZ){
    			xCur=0;
    		}
    		else{
    			xCur= Math.signum(xPwr)*pScale*((Math.abs(xPwr)-DZ)*(1/(1-DZ)));
    		}
    		
    		/**  
    		 	//same code for xCur as above using "if" statements
    			if (xPwr<0) {xCur= pScale*((xPwr+DZ)*(1/(1-DZ)));}
    			if (xPwr>0) {xCur= pScale*((xPwr-DZ)*(1/(1-DZ)));}}
    		*/
    			
    	
    	// yCur
    		if (Math.abs(yPwr)<DZ){
    			yCur=0;
    		}
    		else{
    			yCur= Math.signum(yPwr)*pScale*((Math.abs(yPwr)-DZ)*(1/(1-DZ)));
    		}
    			
    		/**
    			//same code for yCur as above using "if" statements
    			if (yPwr<0) {yCur= pScale*((yPwr+DZ)*(1/(1-DZ)));}
    			if (yPwr>0) {yCur= pScale*((yPwr-DZ)*(1/(1-DZ)));}}
    		*/
    	
    	
    	// zCur
    		if (Math.abs(zPwr)<twistDZ){
    			zCur=0;
    		}
    		else{
    			zCur= Math.signum(zPwr)*pScale*((Math.abs(zPwr)-twistDZ)*(1/(1-twistDZ)));
    		}
    		
    	// highCur
    		if (Math.abs(highPwr)<DZ){
    			highCur=0;
    		}
    		else{
    			highCur= Math.signum(highPwr)*pScale*((Math.abs(highPwr)-DZ)*(1/(1-DZ)));
    		}
    		
    	// oddCur
    		if (Math.abs(oddQuad)<DZ){
    			oddCur=0;
    		}else{
    			oddCur= Math.signum(oddQuad)*pScale*((Math.abs(oddQuad)-DZ)*(1/(1-DZ)));
    		}
    		
    	// evenCur
    		if (Math.abs(evenQuad)<DZ){
    			evenCur=0;
    		}
    		else{
    			evenCur= Math.signum(evenQuad)*pScale*((Math.abs(evenQuad)-DZ)*(1/(1-DZ)));
    		}
    						
    				
    		
  //Code assumes motors c,g,b,f are reversed in direction
    		
			if(Math.abs(zPwr)>twistDZ){
				ac.setLeftRightMotorOutputs(zCur, -zCur);
				eg.setLeftRightMotorOutputs(zCur, -zCur);
				db.setLeftRightMotorOutputs(zCur, -zCur);
				hf.setLeftRightMotorOutputs(zCur, -zCur);
			}
			else{
				
			 // DeadZone is built into the Cur variables so separate DeadZone section not required
				
			 // X-Positive, Y-Positive
				if(xPwr >= 0 && yPwr >= 0){
				ac.drive(highCur,0);
				eg.drive(xCur,0);
				db.drive(oddCur,0);
				hf.drive(yCur,0);
				}
				
			// X-Negative, Y-Positive
			if(xPwr < 0 && yPwr >= 0){
				ac.drive(evenCur,0);
				eg.drive(xCur,0);
				db.drive(highCur,0);
				hf.drive(yCur,0);
				}
			
			// X-Negative, Y-Negative
			if(xPwr < 0 && yPwr < 0){
				ac.drive(-highCur,0);
				eg.drive(xCur,0);
				db.drive(oddCur,0);
				hf.drive(yCur,0); 
				}
			
			// X-Positive, Y-Negative
			if(xPwr >= 0 && yPwr < 0){
				ac.drive(evenCur,0);
				eg.drive(xCur,0);
				db.drive(-highCur,0);
				hf.drive(yCur,0);	
        		}
			
			}
    		
    			
    /**	 Commented out old code in favor of Cur code

    	//Code assumes motors c,g,b,f are reversed in direction
    		
    			if(zPwr <= -0.2 || zPwr >= 0.2){
    				ac.setLeftRightMotorOutputs(zPwr, -zPwr);
    				eg.setLeftRightMotorOutputs(zPwr, -zPwr);
    				db.setLeftRightMotorOutputs(zPwr, -zPwr);
    				hf.setLeftRightMotorOutputs(zPwr, -zPwr);
				}
    			else{
					
				  // DeadZone
    				if(Math.abs(xPwr) < DZ && Math.abs(yPwr) < DZ){
    				ac.drive(0,0);
    				eg.drive(0,0);
    				db.drive(0,0);
    				hf.drive(0,0);
    				
    			}// X-Positive, Y-Positive
    				if(xPwr >= 0 && yPwr >= 0){
    				ac.drive(highPwr,0);
    				eg.drive(xPwr,0);
    				db.drive(oddQuad,0);
    				hf.drive(yPwr,0);	

    			}// X-Negative, Y-Positive
    			if(xPwr < 0 && yPwr >= 0){
    				ac.drive(evenQuad,0);
    				eg.drive(xPwr,0);
    				db.drive(highPwr,0);
    				hf.drive(yPwr,0);

    			}// X-Negative, Y-Negative
    			if(xPwr < 0 && yPwr < 0){
    				ac.drive(-highPwr,0);
    				eg.drive(xPwr,0);
    				db.drive(oddQuad,0);
    				hf.drive(yPwr,0); 

    			}// X-Positive, Y-Negative
    			if(xPwr >= 0 && yPwr < 0){
    				ac.drive(evenQuad,0);
    				eg.drive(xPwr,0);
    				db.drive(-highPwr,0);
    				hf.drive(yPwr,0);	
    			
            		}
    			}		
    */
			
    			
    	}
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
        LiveWindow.run();
    }
    
   
}




