package org.firstinspires.ftc.teamcode;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuMarkInstanceId;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

/*
 *
 *This program is for Blue Alliance Square 1
 *This is the left sided balancing stone from the drivers square perspective on the blue side
 *
 */
@Autonomous(name = "PentacoreRedPlate2", group = "Blue Side")
//@Disabled
public class PentacoreRedPlate2 extends LinearOpMode
{

    ColorSensor colorSensorLeft;    // Hardware Device Object
    ColorSensor colorSensorRight;
    Servo colorServoLeft;
    Servo colorServoRight;
    DcMotor rightMotorOutside;
    DcMotor leftMotorOutside;
    DcMotor rightMotorInside;
    DcMotor leftMotorInside;
    DcMotor linearSlideRight;
    DcMotor linearSlideLeft;
    Servo rightServoClaw;
    Servo leftServoClaw;
    VuforiaLocalizer vuforia;
    String _placement=null;

    @Override
    public void runOpMode()
    {
        float hsvValues[] = {0F,0F,0F};
        final float values[] = hsvValues;

        int relativeLayoutId = hardwareMap.appContext.getResources().getIdentifier("RelativeLayout", "id", hardwareMap.appContext.getPackageName());
        final View relativeLayout = ((Activity) hardwareMap.appContext).findViewById(relativeLayoutId);

        // get a reference to our ColorSensor object.
        colorSensorRight = hardwareMap.colorSensor.get("colorSensorRight");
        colorServoRight = hardwareMap.servo.get("colorServoRight");
        rightMotorOutside = hardwareMap.dcMotor.get("rightMotorOutside");
        leftMotorOutside = hardwareMap.dcMotor.get("leftMotorOutside");
        rightMotorInside = hardwareMap.dcMotor.get("rightMotorInside");
        leftMotorInside = hardwareMap.dcMotor.get("leftMotorInside");
        linearSlideRight = hardwareMap.dcMotor.get("linearSlideRight");
        linearSlideLeft = hardwareMap.dcMotor.get("linearSlideLeft");
        rightServoClaw = hardwareMap.servo.get("rightServoClaw");
        leftServoClaw = hardwareMap.servo.get("leftServoClaw");
        linearSlideLeft.setDirection(DcMotor.Direction.REVERSE);
        rightMotorInside.setDirection(DcMotor.Direction.REVERSE);
        rightMotorOutside.setDirection(DcMotor.Direction.REVERSE);


        // wait for the start button to be pressed.
        waitForStart();

        //set variable for speed of robot moving when doing jewels mission
        double _move = 0.15;
        long _sleeptime = 350;

        //move sensor arm down
        colorServoLeft.setPosition(0.7);
        sleep(1000);
        colorServoLeft.setPosition(0.9);
        sleep(1000);
        leftServoClaw.setPosition(0.65);
        rightServoClaw.setPosition(0.05);
        LinearSlide(0.15);
        sleep(250);
        LinearSlideStop();
        sleep(1);
        StopDriving();
        sleep(1000);


        //begin to identify the color of jewel
        // convert the RGB values to HSV values.
        Color.RGBToHSV(colorSensorLeft.red() * 8, colorSensorLeft.green() * 8, colorSensorLeft.blue() * 8, hsvValues);

        // send the info back to driver station using telemetry function.
        telemetry.addData("Clear", colorSensorLeft.alpha());
        telemetry.addData("Red  ", colorSensorLeft.red());
        telemetry.addData("Green", colorSensorLeft.green());
        telemetry.addData("Blue ", colorSensorLeft.blue());
        telemetry.addData("Hue", hsvValues[0]);

        //move robot to knock off opposite alliance color ball
        //if senses red drive backward
        telemetry.log().add("$$$$$$$$$ $$$$$ color sensed red", colorSensorLeft.red());
        telemetry.log().add("$$$$$$$$$ $$$$$ color sensed blue", colorSensorLeft.blue());
        Log.d("$$$$$$ RED",""+colorSensorLeft.red());
        Log.d("$$$$$$BLUE",""+colorSensorLeft.blue());

        if (colorSensorLeft.red() > colorSensorLeft.blue())
        {
            StopDriving();
            sleep(1000);
            colorServoLeft.setPosition(0.85);
            DriveBackward(_move);
            sleep(_sleeptime);
            colorServoLeft.setPosition(0.1);
            DriveBackward(-_move);
            sleep(_sleeptime+300);
        }
        //if senses blue drive forward
        else if (colorSensorLeft.blue() > colorSensorLeft.red())
        {
            StopDriving();
            sleep(1000);
            colorServoLeft.setPosition(0.85);
            DriveBackward(-_move);
            sleep(_sleeptime);
            colorServoLeft.setPosition(0.1);
            DriveBackward(_move);
            sleep(_sleeptime+300);
        }
        else
        {
            //raise sensor arm up if no color is sensed
            colorServoLeft.setPosition(0.1);
            sleep(100);
        }
        //turn right and move towards the crypto box
        StopDriving();
        sleep(1000);
        RightTurn(0.23);
        sleep(125);
        StopDriving();
        sleep(1000);
        DriveForward(0.5);
        sleep(1400);
        StopDriving();
        sleep(1000);
        LeftTurn(0.25);
        sleep(800);
        //Drop linear slide down to place cube on ground
        leftServoClaw.setPosition(0.0);
        rightServoClaw.setPosition(0.6);
        DriveForward(0.5);
        sleep(1200);

        StopDriving();


    }
    public void DriveForward(double power)
    {
        leftMotorInside.setPower(power);
        leftMotorOutside.setPower(power);
        rightMotorInside.setPower(power);
        rightMotorOutside.setPower(power);
    }
    public void DriveBackward(double power)
    {
        leftMotorInside.setPower(-power);
        leftMotorOutside.setPower(-power);
        rightMotorInside.setPower(-power);
        rightMotorOutside.setPower(-power);
    }
    public void RightTurn(double power)
    {
        leftMotorInside.setPower(power);
        leftMotorOutside.setPower(power);
        rightMotorInside.setPower(-power);
        rightMotorOutside.setPower(-power);
    }
    public void LeftTurn(double power)
    {
        leftMotorInside.setPower(-power);
        leftMotorOutside.setPower(-power);
        rightMotorInside.setPower(power);
        rightMotorOutside.setPower(power);
    }
    public void StopDriving()
    {
        leftMotorInside.setPower(0);
        leftMotorOutside.setPower(0);
        rightMotorInside.setPower(0);
        rightMotorOutside.setPower(0);
    }
    public void LinearSlide(double power)
    {
        linearSlideRight.setPower(power);
        linearSlideLeft.setPower(power);
    }
    public void LinearSlideStop()
    {
        linearSlideLeft.setPower(0);
        linearSlideRight.setPower(0);
    }
    String format(OpenGLMatrix transformationMatrix) {
        return (transformationMatrix != null) ? transformationMatrix.formatAsTransform() : "null";
    }
}