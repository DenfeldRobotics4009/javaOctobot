
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
        //::: Initialize ojbects! Each CANTalon # is the same channel number
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
        
        ac = new RobotDrive(can0,can4);
        eg = new RobotDrive(can1,can5);
        db = new RobotDrive(can6,can2);
        hf = new RobotDrive(can7,can3);

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
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select between different autonomous modes
	 * using the dashboard. The sendable chooser code works with the Java SmartDashboard. If you prefer the LabVIEW
	 * Dashboard, remove all of the chooser code and uncomment the getString code to get the auto name from the text box
	 * below the Gyro
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
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
        //::: PSEUDOCODE ::://
        /**
         * FORWARD:  		if(joystick x = 0 and joystick y > 0) ac, db, hf to go forward, eg to stop
         * BACKWARD: 		if(joystick x = 0 and joystick y < 0) ac, db, hf to go backward, eg to stop
         * STRAFE LEFT: 	if(joystick x<0 and joystick y = 0) eg, ac(backward), bd to go forward, hf to stop
         * STRAFE RIGHT:	if(joystick x>0 and joystick y = 0) eg(backwards), ac, bd(backward), hf to stop
         * STRAFE UP LEFT:	if(joystick x<0 and joystick y>0) eg, db, hf, to go forward, ac to stop  
         * STRAFE UP RIGHT 	if(joystick x>0 and joystick y>0) eg to go backward, ac, hf, to go forward, bd to stop
         * STRAFE DOWN LEFT if(joystick x<0 and joystick y<0) eg to go forward, ac to go backward, hf to go backward, db to stop
         * STRAFE DOWN RIGHT: if joystick x>0 and joystick y<0) eg to go backwards, hf to go backwards, db to go backwards, ac to stop
         * 
         */

        //:::::::::::::WRITE DEADZONE EVALUATION FUNCTION::::::::::::::::
        double xMag;
        double yMag;
        double avgMag; 
        int DZ = 10;
        while(isOperatorControl() && isEnabled()){
           
            xMag = Math.abs(Math.pow(joystick.getX()/100, 3)); // Value cubed for exponential speed increase
            yMag = Math.abs(Math.pow(joystick.getY()/100, 3)); // ^^
            avgMag = (xMag + yMag)/2;
        	//FORWARD
        	if((joystick.getX() < DZ && joystick.getX() > -DZ) && (joystick.getY() > DZ)){
        		ac.drive(yMag,0);
        		eg.drive(0,0);
        		db.drive(yMag,0);
        		hf.drive(yMag,0);
        		
        	}//BACKWARD
        	else if((joystick.getX() < DZ && joystick.getX() > -DZ) && joystick.getY() < -DZ){
        		ac.drive(-yMag,0);
        		eg.drive( 0,0);
        		db.drive(-yMag,0);
        		hf.drive(-yMag,0);
        	}//LEFT
        	else if(joystick.getX() < -DZ && (joystick.getY() < DZ && joystick.getY() > -DZ)){
        		ac.drive(-xMag,0);
        		eg.drive(xMag,0);
        		db.drive(xMag,0);
        		hf.drive(0,0);
        	}//RIGHT
        	else if(joystick.getX() > DZ && (joystick.getY() < DZ && joystick.getY() > -DZ)){
        		ac.drive(xMag,0);
        		eg.drive(-xMag,0);
        		db.drive(-xMag,0);
        		hf.drive(0,0);
        	}//UP LEFT
        	else if(joystick.getX() < -DZ && joystick.getY() > DZ){
        		ac.drive(0,0);
        		eg.drive(xMag,0);
        		db.drive(avgMag,0);
        		hf.drive(yMag,0);        		
        	}//UP RIGHT
        	else if(joystick.getX() > DZ && joystick.getY() > DZ){
        		ac.drive(avgMag,0);
        		eg.drive(-xMag,0);
        		db.drive(0,0);
        		hf.drive(yMag,0);
        	}//DOWN LEFT
        	else if(joystick.getX() < -DZ && joystick.getY() < -DZ){
        		ac.drive(-avgMag,0);
        		eg.drive(xMag,0);
        		db.drive(0,0);
        		hf.drive(-yMag,0);
        	}//DOWN RIGHT
        	else if(joystick.getX() > DZ && joystick.getY() < -DZ){
        		ac.drive(0, 0);
        		eg.drive(-xMag,0);
        		db.drive(-avgMag,0);
        		hf.drive(-yMag,0);
        	}
        }
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
        LiveWindow.run();
    }
    
   
}
