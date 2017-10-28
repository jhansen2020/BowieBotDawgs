import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import static android.os.SystemClock.sleep;

@TeleOp(name = "TestTeleOpMode", group = "TeleOp")

public class TestTeleOpMode extends OpMode{

    //Creating variables

    private ElapsedTime runtime = new ElapsedTime();

    private DcMotor leftFrontMotor, leftBackMotor, rightFrontMotor, rightBackMotor,liftMotor;
    private Servo leftClampServo, rightClampServo;

    double leftJoyStick, rightJoyStick, leftMotorPower, rightMotorPower, liftMotorPower, clampMotorPower, leftServoPower, rightServoPower;

    final private static double JOYSTICK_DEADBAND = 0.1;

    @Override
    public void init() {

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

        //Preassigning clampMotorPower



        //Moves turns on lift motor for 3 seconds to make sure the lift is at the bottom
//        long t= System.currentTimeMillis();
//        long end = t+3000;
//        while(System.currentTimeMillis() < end) {
//            liftMotorPower = 1;
//            sleep(3);

        liftMotorPower = 1;
        sleep(3);

//
    }

    //Code that resets the elapsed time once the driver hits play
    @Override
    public void start() {
        runtime.reset();

        rightServoPower = 0.75;
        leftServoPower = -0.75;

    }

    @Override
    public void loop() {

        //Assigning gamepad values

        leftJoyStick = -gamepad1.left_stick_y;
        rightJoyStick = gamepad1.right_stick_x;

        //clampMotorPower = (gamepad2.right_trigger > 0.5 && gamepad2.left_trigger < 0.3) ? .75 :
         //       (gamepad2.right_trigger < 0.3 && gamepad2.left_trigger > 0.5) ? - 1 : clampMotorPower;

        if (gamepad2.right_trigger > 0.5 && gamepad2.left_trigger < 0.3){
            rightServoPower = 0.0;
            leftServoPower = 0.75;
        } else if (gamepad2.right_trigger < 0.3 && gamepad2.left_trigger > 0.5){
            rightServoPower = 1.0;
            leftServoPower = -0.75;
        }


        liftMotorPower = gamepad2.dpad_up && !gamepad2.dpad_down ? -1 :
                    !gamepad1.dpad_up && gamepad2.dpad_down ? 1 : 0.0;

        //Testing JOYSTICK_DEADBAND

        if (Math.abs(leftJoyStick) < JOYSTICK_DEADBAND) leftJoyStick = 0;
        if (Math.abs(rightJoyStick) < JOYSTICK_DEADBAND) rightJoyStick = 0;

        //Assiging POV drive values

        leftMotorPower = Range.clip(leftJoyStick + rightJoyStick, -1.0, 1.0);
        rightMotorPower = Range.clip(leftJoyStick - rightJoyStick, -1.0, 1.0);

        //Assigning power to each servo and clipping clampMotorPower

        clampMotorPower = Range.clip(clampMotorPower, -1.0, 1.0);

//        leftServoPower = -clampMotorPower;
//        rightServoPower = -clampMotorPower;

        //Applying power to motors and servos

        leftFrontMotor.setPower(leftMotorPower);
        leftBackMotor.setPower(leftMotorPower);
        rightFrontMotor.setPower(rightMotorPower);
        rightBackMotor.setPower(rightMotorPower);
        liftMotor.setPower(liftMotorPower);

        leftClampServo.setPosition(leftServoPower);
        rightClampServo.setPosition(rightServoPower);

        // Show the elapsed game time and wheel power.
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Motors", "left (%.2f), right (%.2f)", leftMotorPower, rightMotorPower);
        telemetry.addData("LeftServoPower", "power: (%.2f)", leftServoPower);
        telemetry.addData("RightServoPower", "power: (%.2f)", rightServoPower);

    }
}