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
    private DcMotor Leftfront, Leftback, Rightfront, Rightback, Lift;
    private Servo Leftclamp, Rightclamp;

    double Leftstick, Rightstick, Leftpower, Rightpower, Liftpower, Clamppower, Leftservopower, Rightservopower;

    final private static double JOYSTICK_DEADBAND = 0.1;

    @Override
    public void init() {

        //Assigning variables

        Leftfront = hardwareMap.dcMotor.get("Leftfront");
        Leftback = hardwareMap.dcMotor.get("Leftback");
        Rightfront = hardwareMap.dcMotor.get("Rightfront");
        Rightback = hardwareMap.dcMotor.get("Rightback");
        Lift = hardwareMap.dcMotor.get("Lift");
        Leftclamp = hardwareMap.servo.get("LeftClamp");
        Rightclamp = hardwareMap.servo.get("RightClamp");

        //Assigning directions of motors

        Leftfront.setDirection(DcMotor.Direction.REVERSE);
        Leftback.setDirection(DcMotor.Direction.REVERSE);
        Rightfront.setDirection(DcMotor.Direction.FORWARD);
        Rightback.setDirection(DcMotor.Direction.FORWARD);

        //Preassigning Clamppower

        Clamppower = -1.0;
    }

    //Code that resets the elapsed time once the driver hits play
    @Override
    public void start() {
        runtime.reset();
    }

    @Override
    public void loop() {

        //Assigning gamepad values

        Leftstick = -gamepad1.left_stick_y;
        Rightstick = gamepad1.left_stick_x;

        Clamppower += (gamepad2.right_trigger > 0.5 && gamepad2.left_trigger < 0.3) ? 0.05 :
                (gamepad2.right_trigger < 0.3 && gamepad2.left_trigger > 0.5) ? - 0.05 : 0;

        Liftpower = gamepad2.dpad_up && !gamepad2.dpad_down ? 0.2 :
                    !gamepad1.dpad_up && gamepad2.dpad_down ? -0.2 : 0.0;

        //Testing JOYSTICK_DEADBAND

        if (Math.abs(Leftstick) < JOYSTICK_DEADBAND) Leftstick = 0;
        if (Math.abs(Rightstick) < JOYSTICK_DEADBAND) Rightstick = 0;

        //Assiging POV drive values

        Leftpower = Range.clip(Leftstick + Rightstick, -1.0, 1.0);
        Rightpower = Range.clip(Leftstick - Rightstick, -1.0, 1.0);

        //Assigning power to each servo

        Leftservopower = -Clamppower;        //might need to reverse the negative sign
        Rightservopower = Clamppower;

        //Applying power to motors and servos

        Leftfront.setPower(Leftpower);
        Leftback.setPower(Leftpower);
        Rightfront.setPower(Rightpower);
        Rightback.setPower(Rightpower);
        Lift.setPower(Liftpower);

        Leftclamp.setPosition(Range.clip(Leftservopower, -1.0, 1.0));
        Rightclamp.setPosition(Range.clip(Rightservopower, -1.0, 1.0));

        // Show the elapsed game time and wheel power.
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Motors", "left (%.2f), right (%.2f)", Leftpower, Rightpower);
        telemetry.addData("Servo:", Clamppower);
    }
}