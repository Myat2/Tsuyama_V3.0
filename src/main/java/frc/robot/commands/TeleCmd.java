package frc.robot.commands;

import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Globals;
import frc.robot.RobotContainer;
import frc.robot.commands.gamepad.OI;
import frc.robot.subsystems.Arm;
import frc.robot.subsystems.OmniDrive;


// This command will be run during teleop mode
public class TeleCmd extends CommandBase
{
    /**
     * Bring in Subsystem and Gamepad code
     */
    private final OmniDrive m_omnidrive;
    private final Arm m_arm;
    private final OI m_oi;

    double Dpad = -1.0;

    Double joyXpos = 0.885;
    Double joyYpos = 0.9; //to have an intermediate variable for joystick control
    boolean gripperflag=false;

    private long lastRightBumperPressTime = 0;
    private static final long DEBOUNCE_DELAY = 500; // Set the debounce delay in milliseconds


    Translation2d pos;

    /**
     * Constructor
     */
    public TeleCmd(OmniDrive omnidrive, OI oi, Arm arm)
    {
        m_omnidrive = RobotContainer.m_omnidrive;
        
        m_oi = RobotContainer.m_oi;
        m_arm = RobotContainer.m_arm;
        addRequirements(m_omnidrive);
    }

    /**
     * Code here will run once when the command is called for the first time
     */
    @Override
    public void initialize()
    {
        Dpad = -1.0;        
    }

    /**
     * Code here will run continuously every robot loop until the command is stopped
     */
    @Override
    public void execute()
    {
        /**
         * Get Joystick data
         */
        // Right stick for X-Y control
        // Left stick for W (rotational) control

        boolean btnflag = false;

        Dpad  = m_oi.getDpad();  //Dpad angle
     
        double x = m_oi.getLeftDriveX();
        double y = -m_oi.getLeftDriveY(); // Down is positive. Need to negate
        double w = -m_oi.getRightDriveX(); // X-positive is CW. Need to negate

        if (Dpad == 90.0) {
            x = 0.5; // Move left
        } else if (Dpad == 270.0) {
            x = -0.5; // Move right
        }

        m_omnidrive.setRobotSpeedXYW_Open(x, y, w);

        //arm up and down (y axis)
        double RightTrig = m_oi.getDriveRightTrigger();
        double LeftTrig = m_oi.getDriveLeftTrigger();
        

        //arm preset positions
        boolean btnY = m_oi.getDriveYButton();
        boolean btnX = m_oi.getDriveXButton();
        boolean btnA = m_oi.getDriveAButton();
        boolean btnB = m_oi.getDriveBButton();

        boolean rightBumper = m_oi.getDriveRightBumper();


        if (rightBumper && System.currentTimeMillis() - lastRightBumperPressTime >= DEBOUNCE_DELAY) {
            // Update the last press time
            lastRightBumperPressTime = System.currentTimeMillis();
    
            if (!gripperflag) {
                m_arm.setGripper(210); // open
            } else {
                m_arm.setGripper(150); // close
            }
    
            // Toggle the gripperflag
            gripperflag = !gripperflag;
        }

    

        if (btnY) {

            joyXpos = 1.27;
            joyYpos = 0.91;

            btnflag = true;
        }
        else if (btnX){

            joyXpos = 0.79;
            joyYpos = 0.66;

            btnflag = true;
        }
        else if (btnA){

            joyXpos = 0.415;
            joyYpos = 0.560;

            btnflag = true;
        }
        else if (btnB){

            joyXpos = 0.515;
            joyYpos = 0.505;

            btnflag = true;
        }
        else if (RightTrig > 0.1){
            joyYpos = joyYpos + 0.005;
        }
        else if (LeftTrig > 0.1){
            joyYpos = joyYpos - 0.005;
        }
        else if (Dpad == 0.0) {
            joyXpos = joyXpos + 0.005;
        }
        else if (Dpad == 180.0) {
            joyXpos = joyXpos - 0.005;
        }


        pos = new Translation2d(joyXpos,joyYpos);

        System.out.println("Dpad" + Dpad);
        System.out.println("JoyXpos:" + joyXpos);
        System.out.println("JoyYpos:" + joyYpos);

        //use tail -f /var/local/kauailabs/log/FRC_UserProgram.log 
        //command in vnc CMD to view the printouts
        //new MoveArm(pos, 12.0).schedule();
        // m_arm.setArmPos(pos);

       
        if (btnflag == true)
        {
            new MoveArm(pos,1.0).schedule(); // change the voltage/speed values to see what works best
            new WaitCommand(10.0);   //delay so that movearm can finish, see if needed or if replace w flag
        }
        else
         {
            m_arm.setArmPos(pos);   
        }

        // m_arm.setGripper(m_arm.getSliderGripper());


    }

    /**
     * When the command is stopped or interrupted, this code is run
     */
    @Override
    public void end(boolean interrupted)
    {
        // Stop the drivetrain motors
        m_omnidrive.setMotorSpeedAll(0);
    }

    /**
     * Check to see if the command is finished
     */
    @Override
    public boolean isFinished()
    {
        return false;
    }
}
