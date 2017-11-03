import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import static android.os.SystemClock.sleep;

@TeleOp(name = "DriverPreferenceTester", group = "TeleOp")

public class DriverPreferenceTester extends OpMode{

    //Creating variables

    private ElapsedTime runtime = new ElapsedTime();

    private DcMotor leftFrontMotor, leftBackMotor, rightFrontMotor, rightBackMotor,liftMotor;
    private Servo leftClampServo, rightClampServo;

    double leftJoyStick, rightJoyStick, leftMotorPower, rightMotorPower, liftMotorPower, clampMotorPower, leftServoPower, rightServoPower, motorMin, motorMax;

    final private static double JOYSTICK_DEADBAND = 0.1;



    @Override
    public void init() {

        motorMax = 1.0;
        motorMin = -1.0;

        //Assigning variables

        leftFrontMotor = hardwareMap.dcMotor.get("Leftfront");
        leftBackMotor = hardwareMap.dcMotor.get("Leftback");
        rightFrontMotor = hardwareMap.dcMotor.get("Rightfront");
        rightBackMotor = hardwareMap.dcMotor.get("Rightback");
        liftMotor = hardwareMap.dcMotor.get("Lift");
        leftClampServo = hardwareMap.servo.get("LeftClamp");
        rightClampServo = hardwareMap.servo.get("RightClamp");

        //Assigning directions of motors

        leftFrontMotor.setDirection(DcMotor.Direction.REVERSE);
        leftBackMotor.setDirection(DcMotor.Direction.REVERSE);
        rightFrontMotor.setDirection(DcMotor.Direction.FORWARD);
        rightBackMotor.setDirection(DcMotor.Direction.FORWARD);

    }

    //Code that resets the elapsed time once the driver hits play
    @Override
    public void start() {
        runtime.reset();

    }

    @Override
    public void loop() {

        if(gamepad1.dpad_up && !gamepad1.dpad_down){
            motorMax = 1.0;
            motorMin = -1.0;
        }else if(gamepad1.dpad_down && !gamepad1.dpad_up){
            motorMax = 0.35;
            motorMin = -0.35;
        }


        if (gamepad1.left_bumper && !gamepad1.right_bumper) {

            //Assigning gamepad values

            leftJoyStick = -gamepad1.left_stick_y;
            rightJoyStick = gamepad1.right_stick_x;


            //Testing JOYSTICK_DEADBAND

            DeabandTester(leftJoyStick);
            DeabandTester(rightJoyStick);

            leftMotorPower = Range.clip(leftJoyStick + rightJoyStick, motorMin, motorMax);
            rightMotorPower = Range.clip(leftJoyStick - rightJoyStick, motorMin, motorMax);
        }else if(gamepad1.right_bumper && !gamepad1.left_bumper){
            
            //Assigning gamepad values

            leftJoyStick = -gamepad1.left_stick_y;
            rightJoyStick = -gamepad1.right_stick_y;


            //Testing JOYSTICK_DEADBAND

            DeabandTester(leftJoyStick);
            DeabandTester(rightJoyStick);

            leftMotorPower = Range.clip(leftJoyStick, motorMin, motorMax);
            rightMotorPower = Range.clip(leftJoyStick, motorMin, motorMax);
        }


            //Applying power to motors and servos

            leftFrontMotor.setPower(leftMotorPower);
            leftBackMotor.setPower(leftMotorPower);
            rightFrontMotor.setPower(rightMotorPower);
            rightBackMotor.setPower(rightMotorPower);

        }


    private double DeabandTester(double joystick){
        if (Math.abs(joystick) < JOYSTICK_DEADBAND) joystick = 0;

        return joystick;


    }

}