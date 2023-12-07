package frc.robot.subsystems;

import java.util.Map;
import frc.robot.Globals;

import com.studica.frc.Servo;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.PWM.PeriodMultiplier;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.robot.subsystems.ServoSpecial;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

//testing commit

public class Arm extends SubsystemBase {
    private final Servo gripperServo, cameraServo, trolleyServo;
    private final ServoSpecial shoulderServo, elbowServo;

    private Translation2d m_pos; // current arm tip target position
    private Translation2d m_posreal;// actual current arm tip position

    private final double a2 = 0.885;  //upper arm (x)
    private final double a1 = 0.9 ;  //lower arm (y)
    
    private double offset0 = 0; // For making software adjustment to servo
    private double offset1 = 0;

    private double m_x, m_y;     //intermediate variable to check if arm is tryign to reach an impossible coordinate

    private double shoulderRatio = 4.0;
    private double elbowRatio = 2.0;

    // Shuffleboard
    private final ShuffleboardTab tab = Shuffleboard.getTab("Arm");

    private final NetworkTableEntry D_shoulderServo = tab.add("shoulderServo", 0).withPosition(2,0).getEntry();
    private final NetworkTableEntry D_elbowServo = tab.add("elbowServo", 0).withPosition(3,0).getEntry();
    private final NetworkTableEntry D_gripperServo = tab.add("gripperServo", 0).withPosition(4,0).getEntry();

    private final NetworkTableEntry D_offset0 = tab.addPersistent("shoulder offset", 0).withPosition(6,1).withWidget(BuiltInWidgets.kNumberSlider)
            .withProperties(Map.of("min", -200, "max", 200)).getEntry();
    private final NetworkTableEntry D_offset1 = tab.addPersistent("elbow offset", 0).withPosition(8,1).withWidget(BuiltInWidgets.kNumberSlider)
            .withProperties(Map.of("min", -200, "max", 200)).getEntry();

    private final NetworkTableEntry D_posX = tab.add("Target posX", 0).withPosition(0,0).getEntry();
    private final NetworkTableEntry D_posY = tab.add("Target posY", 0).withPosition(1,0).getEntry();

    private final NetworkTableEntry D_posXreal = tab.add("real posX", 0).withPosition(0,1).getEntry();
    private final NetworkTableEntry D_posYreal = tab.add("real posY", 0).withPosition(1,1).getEntry();

    private final NetworkTableEntry D_angleA = tab.add("A wrt horizon", 0).withPosition(2,1).getEntry();
    private final NetworkTableEntry D_angleB = tab.add("B wrt horizon", 0).withPosition(3,1).getEntry();

    private final NetworkTableEntry D_sliderX = tab.add("setX", 0.16).withPosition(5,0).withWidget(BuiltInWidgets.kNumberSlider)
            .withProperties(Map.of("min", 0.05, "max", 2.0)).getEntry();
    private final NetworkTableEntry D_sliderY = tab.add("setY", 0.38).withPosition(7,0).withWidget(BuiltInWidgets.kNumberSlider)
            .withProperties(Map.of("min", -1.5, "max", 1.5)).getEntry();

    private final NetworkTableEntry D_sliderGripper = tab.add("GripperAngle", 75).withPosition(4,1).withWidget(BuiltInWidgets.kNumberSlider)
            .withProperties(Map.of("min", 0, "max", 150)).getEntry();
       

    public Arm() {
        shoulderServo = new ServoSpecial(0); // shoulder
        elbowServo = new ServoSpecial(1); // elbow

        gripperServo = new Servo(2); // gripper 


        
        cameraServo = new Servo(3); // camera
        trolleyServo = new Servo(4); // trolley holder

        m_pos = new Translation2d(a2,a1);
        
    }

    public void initialize() {
        m_pos = new Translation2d(a2,a1);
        setArmPos(m_pos);
    }

    /**
     * Sets the shoulderServo angle
     * <p>
     * 
     * @param degrees degree to set the servo to, range 0° - 300°
     */
    public void setShoulderAngle(final double degrees) {
        // System.out.println("shoulder angle being set:" + degrees );
        shoulderServo.setAngle(degrees);
    }

    /**
     * Sets the elbowServo angle
     * <p>
     * 
     * @param degrees degree to set the servo to, range 0° - 300°
     */
    public void setElbowAngle(final double degrees) {
        // System.out.println("elbow angle being set:" + degrees );
        elbowServo.setAngle(degrees);
    }

    /**
     * Sets the gripperServo angle (Gripper)
     * <p>
     * 
     * @param degrees degree to set the servo to, range 0° - 300°
     */
    public void setGripper(final double degrees) {
        gripperServo.setAngle(degrees);
    }

    /**
     * Sets the gripperServo angle (Camera)
     * <p>
     * 
     * @param degrees degree to set the servo to, range 0° - 300°
     */
    public void setCameraAngle(final double degrees) {
        cameraServo.setAngle(degrees);
    }
    /**
     * Sets the trolleyServo angle (Trollley Holder)
     * <p>
     * 
     * @param degrees degree to set the servo to, range 0° - 300°
     */
    public void setTrolleyAngle(final double degrees){
        trolleyServo.setAngle(degrees);
      }
    /**
     * Get Trolley Servo Angle
     * <p>
     * 
     * @return return slider value
     */
    public double getTrolleyAngle(){
        return trolleyServo.getAngle();
      }
    public double getCameraAngle(){
        return cameraServo.getAngle();
    }
    /**
     * Get slider-x value
     * <p>
     * 
     * @return return slider value
     */
    public double getSliderX() {
        return D_sliderX.getDouble(a2);
    }

    /*
     * Get slider-y value
     * <p>
     * 
     * @return return slider value
     */
    public double getSliderY() {
        return D_sliderY.getDouble(a1);
    }
    /*
     * Get slider-Gripper value
     * <p>
     * 
     * @return return slider value
     */
    public double getSliderGripper() {
        return D_sliderGripper.getDouble(0);
    }
    // public double getSliderCamera() {
    //     return D_camera.getDouble(300);
    // }

    /**
     * <p>
     * Returns the shoulderServo angle
     * 
     */

    public double getServoAngle0() {
        return shoulderServo.getAngle();
    }

    /**
     * Returns the elbowServo angle
     * <p>
     */
    public double getServoAngle1() {
        return elbowServo.getAngle();
    }

    /**
     * Returns the gripperServo angle (Gripper)
     * <p>
     */
    public double getGripper() {
        return gripperServo.getAngle();
    }

    /**
     * Returns the cameraServo angle (Camera)
     * <p>
     */
    public double getServoAngle3() {
        return cameraServo.getAngle();
    }

    public void LimitArmXY() {
        double a = a2;
        double c = a1;
        double dist = Math.sqrt(m_x*m_x + m_y*m_y);
        double maxDist = a+c;
        System.out.println("Distance between target point and 0.0: "+ dist);
        if (dist>=(maxDist-0.05)) {
            dist = maxDist-0.05;
            double angle = Math.atan2(m_y, m_x);
            m_x = Math.cos(angle) * dist;
            m_y = Math.sin(angle) * dist;
        }

    }

    /**
     * Sets the arm tip (x,y) position
     * <p>
     * 
     * @param pos (x,y) position of arm tip
     */
    public void setArmPos(Translation2d pos) {

        // Refer to https://www.alanzucconi.com/2018/05/02/ik-2d-1/
        m_pos = pos;
        double x = pos.getX();
        double y = pos.getY();

        // arm tip cannot be physically in the area around origin
        if ((x < 0.05) && (y < 0.1)) {
            x = 0.05;
            m_pos = new Translation2d(x, y);
        }
        m_x = x;
        m_y = y;

        LimitArmXY();

        x = m_x;
        y = m_y;

        
        System.out.println("");
        System.out.println("Actual X:" + m_x);
        System.out.println("Actual Y:" + m_y);

        D_posXreal.setDouble(m_x);
        D_posYreal.setDouble(m_y);

        double a = a2; //a2=x or 0.885m
        double c = a1; //a1=y or 0.9m
        double b = Math.sqrt(x * x + y * y);

        System.out.println("Actual distance between arm tip and 0.0: " + b);

        double alpha = Math.acos((b * b + c * c - a * a) / (2 * b * c));
        double beta = Math.acos((a * a + c * c - b * b) / (2 * a * c));

        // A is shoulderServo angle wrt horizon
        // When A is zero, arm-c is horizontal.
        // beta is elbowServo angle wrt arm-c (BA)
        // When beta is zero, arm-c is closed to arm-c
        double B = Math.PI - beta; // Use B to designate beta. Different from diagram.
        double A = alpha + Math.atan2(y, x);

        // shoulderServo and elbowServo might be mounted clockwise or anti clockwise.
        // offset0 and offset1 are used to adjust the zero the arm position.
        // This makes it easier to mount and tune the arm.
        A = Math.toDegrees(A) * shoulderRatio;
        B = Math.toDegrees(B) * elbowRatio;

        System.out.println("A:" + A);
        System.out.println("B:" + B);
        
        D_angleA.setDouble(A/shoulderRatio);
        D_angleB.setDouble(B/elbowRatio);

        System.out.println("A wrt horizon:" + A/shoulderRatio);
        System.out.println("B wrt horizon:" + B/elbowRatio);

        A = A - 180; // to account for the offset, e.g. limited angle range due to 1:4

        // Uncomment if servo direction needs to be flip.
        // A = 360 - A;

        //Servo B is flipped
        B = 300 - B;

        System.out.println("B after minus:" + B);

        System.out.println("offset1:" + offset1);

        shoulderServo.setAngle(360  - (A + offset0)); 
        elbowServo.setAngle(360 - (B + offset1)); 

        System.out.println("A angle being set:" + (360-(A+offset0)) );
        System.out.println("B angle being set:" + (360-(B+offset1)) );
        System.out.println("--------");
        System.out.println("--------");

     
    }

    public void setArmPosInc(double dx, double dy ) {
        double m_x = dx;
        double m_y = dy;
        setArmPos(new Translation2d(m_x, m_y));
    }

    
    public Translation2d getArmPos(){
        return m_pos;
      } 

    
    /**
     * Code that runs once every robot loop
     */
    @Override
    public void periodic() {
        offset0 =  D_offset0.getDouble(120);
        offset1 =  D_offset1.getDouble(-60);



        D_shoulderServo.setDouble(shoulderServo.getAngle());
        D_elbowServo.setDouble(elbowServo.getAngle());
        D_gripperServo.setDouble(gripperServo.getAngle());


        D_posX.setDouble(m_pos.getX());
        D_posY.setDouble(m_pos.getY());
        
    }
}
