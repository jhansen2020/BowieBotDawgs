import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import static android.os.SystemClock.sleep;

@TeleOp(name = "MainTeleOpMode", group = "TeleOp")

public class MainTeleOpMode extends OpMode{

    //Creating variables

    private ElapsedTime runtime = new ElapsedTime();

    double leftJoyStick, rightJoyStick, leftMotorPower, rightMotorPower, liftMotorPower, clampMotorPower, leftServoPower, rightServoPower;

    final private static double JOYSTICK_DEADBAND = 0.1;

    //Encoder Ticks Variables
    static final double     COUNTS_PER_MOTOR_REV    = 1440 ;    // eg: TETRIX Motor Encoder
    static final double     DRIVE_GEAR_REDUCTION    = 2.0 / 3 ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double     DRIVE_SPEED             = 0.6;
    static final double     TURN_SPEED              = 0.5;

    double motorLiftDownwardSpeed = 0.0;
    double motorLiftUpwardSpeed = 0.0;
    //Lift Limit varibles
    //double liftUpdatedTicks;
    double liftTotalTicks =  COUNTS_PER_INCH * 9;

    double motorMovementMin = 0.0;
    double motorMovementMax = 0.0;

    BotDawg robot;

    @Override
    public void init() {

        robot = new BotDawg();
        robot.init(hardwareMap);


        //I'VE SWITCH THE POWER ON THE SERVO SO THAT WHEN THE ROBOT STARTS IT CLOSES THE ARMS TO GRAB THE BLOCK
        rightServoPower = -0.75;
        leftServoPower = 0.75;



    }

    //Code that resets the elapsed time once the driver hits play
    @Override
    public void start() {
        runtime.reset();



    }

    @Override
    public void loop() {

        //Assigning gamepad values

        leftJoyStick = -gamepad1.left_stick_y;
        rightJoyStick = gamepad1.right_stick_x;



        //This is for limiting the speed of movement motors

        if (gamepad1.a && !gamepad1.y){
            motorMovementMin = -0.4;
            motorMovementMax = 0.6;

        }else if (gamepad1.y && !gamepad1.a){
            motorMovementMin = -0.15;
            motorMovementMax = 0.15;
        }



        //This is for limiting the speed of the Lift motor

        if (gamepad2.a && !gamepad2.y){
            motorLiftDownwardSpeed = -1;
            motorLiftUpwardSpeed = 1;

        }else if (gamepad2.y && !gamepad2.a){
            motorLiftDownwardSpeed = -0.50;
            motorLiftUpwardSpeed = 0.50;
        }


        if (gamepad2.dpad_up && !gamepad2.dpad_down) {
            liftMotorPower = -motorLiftUpwardSpeed;
        } else if (!gamepad2.dpad_up && gamepad2.dpad_down) {
            liftMotorPower = -motorLiftDownwardSpeed;
        } else{
        liftMotorPower = 0;
    }



        //Closing the claw and opening it
        if (gamepad2.right_trigger > 0.5 && gamepad2.left_trigger < 0.3){
            rightServoPower = -0.9;
            leftServoPower = .75;
        } else if (gamepad2.right_trigger < 0.3 && gamepad2.left_trigger > 0.5){
            rightServoPower = .75;
            leftServoPower = -.8;
        } else if (gamepad2.x){
            rightServoPower = -.25;
            leftServoPower = .15;
        }



        //Testing JOYSTICK_DEADBAND



        if (Math.abs(leftJoyStick) < JOYSTICK_DEADBAND) leftJoyStick = 0;
        if (Math.abs(rightJoyStick) < JOYSTICK_DEADBAND) rightJoyStick = 0;

        //Assiging POV drive values

        leftMotorPower = Range.clip(leftJoyStick + rightJoyStick, motorMovementMin, motorMovementMax);
        rightMotorPower = Range.clip(leftJoyStick - rightJoyStick, motorMovementMin, motorMovementMax);

        //Assigning power to each servo and clipping clampMotorPower

        clampMotorPower = Range.clip(clampMotorPower, -1.0, 1.0);

        //Applying power to motors and servos

        robot.leftFrontMotor.setPower(leftMotorPower);
        robot.leftBackMotor.setPower(leftMotorPower);
        robot.rightFrontMotor.setPower(rightMotorPower);
        robot.rightBackMotor.setPower(rightMotorPower);
        robot.liftMotor.setPower(liftMotorPower);

        robot.leftClampServo.setPosition(leftServoPower);
        robot.rightClampServo.setPosition(rightServoPower);



        // Show the elapsed game time and wheel power.
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Motors", "left (%.2f), right (%.2f)", leftMotorPower, rightMotorPower);
        telemetry.addData("LeftServoPower", "power: (%.2f)", leftServoPower);
        telemetry.addData("RightServoPower", "power: (%.2f)", rightServoPower);
        telemetry.addData("lift", "power: (%.2f)", liftMotorPower);
        telemetry.addData("Blue", robot.colorSensor.blue());
        telemetry.addData("Red", robot.colorSensor.red());
        //telemetry.addData("CurrentPostition", "currentPosition: (%.2f)", liftUpdatedTicks);

    }


    //use stop function to go back to bottom position
}