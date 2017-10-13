import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

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

        clampMotorPower = -1.0;
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

        clampMotorPower += (gamepad2.right_trigger > 0.5 && gamepad2.left_trigger < 0.3) ? 0.05 :
                (gamepad2.right_trigger < 0.3 && gamepad2.left_trigger > 0.5) ? - 0.05 : 0;

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

        leftServoPower = -clampMotorPower;   //might need to reverse the negative sign
        rightServoPower = clampMotorPower;

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
        telemetry.addData("Servo:", clampMotorPower);
    }
}