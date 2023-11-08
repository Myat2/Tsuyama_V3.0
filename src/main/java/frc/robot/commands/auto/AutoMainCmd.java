package frc.robot.commands.auto;

import java.util.List;

// import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile.Constraints;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.Arm;
import frc.robot.RobotContainer;

import com.studica.frc.Servo;
// import the commands

/**
 * DriveMotor class
 * <p>
 * This class creates the inline auto command to drive the motor
 */
public class AutoMainCmd extends SequentialCommandGroup
{   
    //private final Servo servo;
    
 
	public AutoMainCmd()
    {
        // servo = new Servo(0);
        // servo.setAngle(180);
        // new MoveServo(shoulderServo, 180.0, 1.0);
        // super(/
        //     new MoveRobot(2, -Math.PI/4, 0, 0, Math.PI),  
        //     new MoveRobot(2, Math.PI/4, 0, 0, Math.PI),
        //     new LoopCmd(new RotateTest()),
        //     new MoveRobot(2, Math.PI/4, 0, 0, Math.PI)
        //     );
    }
}
