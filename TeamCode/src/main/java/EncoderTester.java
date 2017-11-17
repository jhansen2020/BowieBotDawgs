import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;


@Autonomous(name="EncoderTester", group="Test")
//@Disabled
public class EncoderTester extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();

    private DcMotor leftFrontMotor, leftBackMotor, rightFrontMotor, rightBackMotor,liftMotor;
    private Servo leftClampServo, rightClampServo;




    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

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

        waitForStart();
        runtime.reset();

        int newLeftTarget;
        int newRightTarget;

        // run until the end of the match (driver presses STOP)
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLeftTarget = leftFrontMotor.getCurrentPosition() + (int)(12/*This is the inches it will move*/ * COUNTS_PER_INCH);
            newRightTarget = rightFrontMotor.getCurrentPosition() + (int)(12/*This is the inches it will move*/ * COUNTS_PER_INCH);
            leftFrontMotor.setTargetPosition(newLeftTarget);
            rightFrontMotor.setTargetPosition(newRightTarget);

            // Turn On RUN_TO_POSITION
            leftFrontMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightFrontMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            leftFrontMotor.setPower(Math.abs(.5));
            rightFrontMotor.setPower(Math.abs(.5));

            while (opModeIsActive() &&
                    (runtime.seconds() < 12.0/*The time it is allowed to take to do tthe movement*/) &&
                    (leftFrontMotor.isBusy() || rightFrontMotor.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Path1",  "Running to %7d :%7d", newLeftTarget,  newRightTarget);
                telemetry.addData("Path2",  "Running at %7d :%7d",
                        leftFrontMotor.getCurrentPosition(),
                        rightFrontMotor.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;
            leftFrontMotor.setPower(0);
            rightFrontMotor.setPower(0);

            // Turn off RUN_TO_POSITION
            leftFrontMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightFrontMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            //  sleep(250);   // optional pause after each move

        }

        telemetry.addData("Path", "Complete");
        telemetry.update();

    }

    /**
     * This file illustrates the concept of driving a path based on encoder counts.
     * It uses the common Pushbot hardware class to define the drive on the robot.
     * The code is structured as a LinearOpMode
     *
     * The code REQUIRES that you DO have encoders on the wheels,
     *   otherwise you would use: PushbotAutoDriveByTime;
     *
     *  This code ALSO requires that the drive Motors have been configured such that a positive
     *  power command moves them forwards, and causes the encoders to count UP.
     *
     *   The desired path in this example is:
     *   - Drive forward for 48 inches
     *   - Spin right for 12 Inches
     *   - Drive Backwards for 24 inches
     *   - Stop and close the claw.
     *
     *  The code is written using a method called: encoderDrive(speed, leftInches, rightInches, timeoutS)
     *  that performs the actual movement.
     *  This methods assumes that each movement is relative to the last stopping place.
     *  There are other ways to perform encoder based moves, but this method is probably the simplest.
     *  This code uses the RUN_TO_POSITION mode to enable the Motor controllers to generate the run profile
     *
     * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
     * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
     */

    @Autonomous(name="Pushbot: Auto Drive By Encoder", group="Autonomous")
    @Disabled
    public static class EncoderTest extends LinearOpMode {

        /* Declare OpMode members. */
        HardwarePushbot robot   = new HardwarePushbot();   // Use a Pushbot's hardware
        private ElapsedTime     runtime = new ElapsedTime();

        static final double     COUNTS_PER_MOTOR_REV    = 1440 ;    // eg: TETRIX Motor Encoder
        static final double     DRIVE_GEAR_REDUCTION    = 2.0 / 3 ;     // This is < 1.0 if geared UP
        static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
        static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
                (WHEEL_DIAMETER_INCHES * 3.1415);
        static final double     DRIVE_SPEED             = 0.6;
        static final double     TURN_SPEED              = 0.5;

        @Override
        public void runOpMode() {

            /*
             * Initialize the drive system variables.
             * The init() method of the hardware class does all the work here
             */
            robot.init(hardwareMap);





            // Send telemetry message to signify robot waiting;
            telemetry.addData("Status", "Resetting Encoders");    //
            telemetry.update();

            robot.leftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            robot.rightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

            robot.leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            // Send telemetry message to indicate successful Encoder reset
            telemetry.addData("Path0",  "Starting at %7d :%7d",
                    robot.leftDrive.getCurrentPosition(),
                    robot.rightDrive.getCurrentPosition());
            telemetry.update();

            robot.leftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            robot.rightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

            // Wait for the game to start (driver presses PLAY)
            waitForStart();

            int newLeftTarget;
            int newRightTarget;

            // Ensure that the opmode is still active
            if (opModeIsActive()) {


                // Determine new target position, and pass to motor controller
                newLeftTarget = robot.leftDrive.getCurrentPosition() + (int)(12/*This is the inches it will move*/ * COUNTS_PER_INCH);
                newRightTarget = robot.rightDrive.getCurrentPosition() + (int)(12/*This is the inches it will move*/ * COUNTS_PER_INCH);
                robot.leftDrive.setTargetPosition(newLeftTarget);
                robot.rightDrive.setTargetPosition(newRightTarget);

                // Turn On RUN_TO_POSITION
                robot.leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                robot.rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                // reset the timeout time and start motion.
                runtime.reset();
                robot.leftDrive.setPower(Math.abs(.5));
                robot.rightDrive.setPower(Math.abs(.5));


                while (opModeIsActive() &&
                        (runtime.seconds() < 12.0/*The time it is allowed to take to do tthe movement*/) &&
                        (robot.leftDrive.isBusy() || robot.rightDrive.isBusy())) {

                    // Display it for the driver.
                    telemetry.addData("Path1",  "Running to %7d :%7d", newLeftTarget,  newRightTarget);
                    telemetry.addData("Path2",  "Running at %7d :%7d",
                            robot.leftDrive.getCurrentPosition(),
                            robot.rightDrive.getCurrentPosition());
                    telemetry.update();
                }

                // Stop all motion;
                robot.leftDrive.setPower(0);
                robot.rightDrive.setPower(0);

                // Turn off RUN_TO_POSITION
                robot.leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                robot.rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

                //  sleep(250);   // optional pause after each move
            }


            telemetry.addData("Path", "Complete");
            telemetry.update();
        }


    }
}
