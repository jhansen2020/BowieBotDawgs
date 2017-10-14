import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;



@Autonomous(name="ColorSensorTest", group="Linear Opmode")
public class ColorSensorTest extends LinearOpMode {

    ColorSensor colorSensor;

    @Override
    public void runOpMode() {

        float hsvValues[] = {0f, 0f, 0f};

        final float values[] = hsvValues;

        // get a reference to the RelativeLayout so we can change the background
        // color of the Robot Controller app to match the hue detected by the RGB sensor.
        int relativeLayoutId = hardwareMap.appContext.getResources().getIdentifier("RelativeLayout", "id", hardwareMap.appContext.getPackageName());
        final View relativeLayout = ((Activity) hardwareMap.appContext).findViewById(relativeLayoutId);

        boolean bPrevState = false;
        boolean bCurrState = false;

        boolean bLedOn = true;

        colorSensor = hardwareMap.get(ColorSensor.class, "colorSensor");

        colorSensor.enableLed(bLedOn);

        waitForStart();

        while (opModeIsActive()) {

            bCurrState = gamepad1.x;

            if (bCurrState && (bCurrState != bPrevState)){
                bLedOn = !bLedOn;
                colorSensor.enableLed(bLedOn);
            }

            bPrevState = bCurrState;

            Color.RGBToHSV(colorSensor.red() * 8, colorSensor.green() * 8, colorSensor.blue() * 8, hsvValues);

            telemetry.addData("LED", bLedOn ? "On" : "Off");
            telemetry.addData("Clear", colorSensor.alpha());
            telemetry.addData("Red  ", colorSensor.red());
            telemetry.addData("Green", colorSensor.green());
            telemetry.addData("Blue ", colorSensor.blue());
            telemetry.addData("Hue", hsvValues[0]);

            relativeLayout.post(new Runnable() {
                public void run(){
                    relativeLayout.setBackgroundColor(Color.HSVToColor(0xff,values));
                }
            });

            telemetry.update();
        }

        relativeLayout.post(new Runnable() {
            public void run() {
                relativeLayout.setBackgroundColor(Color.WHITE);
            }
        });
    }
}
