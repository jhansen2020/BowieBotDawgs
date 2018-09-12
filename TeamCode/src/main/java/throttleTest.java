import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import static android.os.SystemClock.sleep;

@TeleOp(name = "throttleTest", group = "TeleOp")

public class throttleTest extends OpMode{

    //Creating variables
    private ElapsedTime runtime = new ElapsedTime();

    double leftJoyStick, rightJoyStick, leftMotorPower, rightMotorPower, liftMotorPower, clampMotorPower, leftServoPower, rightServoPower;

    final private static double JOYSTICK_DEADBAND = 0.1;

    //Encoder Ticks Variables
    private static final double     COUNTS_PER_MOTOR_REV    = 1440 ;    // eg: TETRIX Motor Encoder
    private static final double     DRIVE_GEAR_REDUCTION    = 2.0 / 3 ;     // This is < 1.0 if geared UP
    private static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    private static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);

    private BotDawg robot;

    @Override
    public void init() {

        robot = new BotDawg();
        robot.init(hardwareMap);

    }

    //Code that resets the elapsed time once the driver hits play
    @Override
    public void start() {
        runtime.reset();
    }


    public void loop() {

        // throttle is taken directly from the right trigger, the right trigger ranges in values from
        // 0 to 1
        double throttle = gamepad2.right_trigger;

        // if the left trigger is pressed, go in reverse
        if (gamepad2.left_trigger != 0.0) {
            throttle = -gamepad2.left_trigger;
        }

        // assign throttle to the left and right motors
        double rightThrottle = throttle;
        double leftThrottle = throttle;
        //I MIGHT NEED TO ADD JOYSTICK_DEADBAND
        // now we need to apply steering (direction). The left stick ranges from -1 to 1. If it is
        // negative we want to slow down the left motor. If it is positive we want to slow down the
        // right motor.
        if (gamepad2.left_stick_x < 0) {
            // negative value, stick is pulled to the left
            leftThrottle = leftThrottle * (1 + gamepad2.left_stick_x);
        }
        if (gamepad2.left_stick_x > 0) {
            // positive value, stick is pulled to the right
            rightThrottle = rightThrottle * (1 - gamepad2.left_stick_x);
        }
        // write the values to the motor. This will over write any values placed while processing gamepad1
        robot.leftBackMotor.setPower(leftThrottle);
        robot.leftFrontMotor.setPower(leftThrottle);
        robot.rightFrontMotor.setPower(rightThrottle);
        robot.rightBackMotor.setPower(rightThrottle);

        //Telemetry is not used to control the robot, it is purely to help debug by showing
        //Information on the phone
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Motors", "left (%.2f), right (%.2f)", leftThrottle, rightThrottle);
    }
}