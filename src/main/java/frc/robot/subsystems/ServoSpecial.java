package frc.robot.subsystems;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.hal.FRCNetComm.tResourceType;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SendableRegistry;

public class ServoSpecial extends PWM {
    private static final double kMaxServoAngle = 360.0;
    private static final double kMinServoAngle = 0.0;

    protected static final double kDefaultMaxServoPWM = 2.5;
    protected static final double kDefaultMinServoPWM = 0.5;


    public ServoSpecial(final int channel) {
        super(channel);
        setBounds(kDefaultMaxServoPWM, 0, 0, 0, kDefaultMinServoPWM);
        setPeriodMultiplier(PeriodMultiplier.k4X);

        HAL.report(tResourceType.kResourceType_Servo, getChannel() + 1);
        SendableRegistry.setName(this, "Servo", getChannel());
    }

    public void set(double value) {
        setPosition(value);
    }


    public double get() {
        return getPosition();
    }

    public void setAngle(double degrees) {
        if (degrees < kMinServoAngle) {
        degrees = kMinServoAngle;
        } else if (degrees > kMaxServoAngle) {
        degrees = kMaxServoAngle;
        }

        setPosition(((degrees - kMinServoAngle)) / getServoAngleRange());
    }

    public double getAngle() {
        return getPosition() * getServoAngleRange() + kMinServoAngle;
    }

    private double getServoAngleRange() {
        return kMaxServoAngle - kMinServoAngle;
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("Servo");
        builder.addDoubleProperty("Value", this::get, this::set);
    }
}