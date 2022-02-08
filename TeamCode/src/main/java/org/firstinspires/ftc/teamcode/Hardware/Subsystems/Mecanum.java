package org.firstinspires.ftc.teamcode.Hardware.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import static org.firstinspires.ftc.teamcode.Utilities.MathUtils.angleMode.DEGREES;
import static org.firstinspires.ftc.teamcode.Utilities.MathUtils.closestAngle;
import static org.firstinspires.ftc.teamcode.Utilities.MathUtils.cos;
import static org.firstinspires.ftc.teamcode.Utilities.MathUtils.sin;
import static org.firstinspires.ftc.teamcode.Utilities.OpModeUtils.hardwareMap;
import static org.firstinspires.ftc.teamcode.Utilities.OpModeUtils.multTelemetry;

import org.firstinspires.ftc.teamcode.Hardware.Sensors.Color_Sensor;
import org.firstinspires.ftc.teamcode.Hardware.Sensors.Distance_Sensor;
import org.firstinspires.ftc.teamcode.Hardware.Sensors.Gyro;
import org.firstinspires.ftc.teamcode.Utilities.MathUtils;
import org.firstinspires.ftc.teamcode.Utilities.PID;
import org.firstinspires.ftc.teamcode.Z.Side;
import org.firstinspires.ftc.teamcode.Z.Vision.Camera;
import org.firstinspires.ftc.teamcode.Z.Vision.DetectionPipeline;
import org.opencv.core.Point;

public class Mecanum {

    public DcMotor fr,fl,br,bl;
    public Color_Sensor flColor, frColor, blColor, brColor;
    public PID pid;
    public Gyro gyro;
    public static ElapsedTime time = new ElapsedTime();
    private final ElapsedTime timeOut = new ElapsedTime();
    private ElapsedTime loopTimer1 = new ElapsedTime();
    public Camera cam;
    private DetectionPipeline pipeline = new DetectionPipeline();
    public double dist = 0;
    public double cycleDist = 0;



    public Mecanum(){
        pid = new PID(0.05, 0, 0.002);
        gyro = new Gyro();
        cam = new Camera("Camera",pipeline);
        initChassis();
    }




    public void initChassis() {


        fr = hardwareMap.get(DcMotor.class, "fr");
        fl = hardwareMap.get(DcMotor.class, "fl");
        br = hardwareMap.get(DcMotor.class, "br");
        bl = hardwareMap.get(DcMotor.class, "bl");
        flColor = new Color_Sensor();
        frColor = new Color_Sensor();
        blColor = new Color_Sensor();
        brColor = new Color_Sensor();
        flColor.init("flColor");
        frColor.init("frColor");
        blColor.init("blColor");
        brColor.init("brColor");
        resetMotors();

    }
    public Point getPosition(){
        double yDist = (fr.getCurrentPosition() + fl.getCurrentPosition() + br.getCurrentPosition() + bl.getCurrentPosition()) / 4.0;
        double xDist = (fl.getCurrentPosition() - fr.getCurrentPosition() + br.getCurrentPosition() - bl.getCurrentPosition()) / 4.0;
        return new Point(xDist, yDist);
    }

    public void setAllPower(double power){
        fl.setPower(power);
        fr.setPower(power);
        bl.setPower(power);
        br.setPower(power);
    }

    public void countDistReset(){
        dist = (fr.getCurrentPosition() + fl.getCurrentPosition() + br.getCurrentPosition() + bl.getCurrentPosition()) / 4.0;
    }

    public double countDist(){
        return((fr.getCurrentPosition() + fl.getCurrentPosition() + br.getCurrentPosition() + bl.getCurrentPosition()) / 4.0 - dist);
    }
    public void resetMotors(){
        fr.setDirection(DcMotorSimple.Direction.FORWARD);
        fl.setDirection(DcMotorSimple.Direction.REVERSE);
        br.setDirection(DcMotorSimple.Direction.FORWARD);
        bl.setDirection(DcMotorSimple.Direction.REVERSE);

        fr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        br.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);


        fr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        fl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        br.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void setDrivePower(double power, double strafe, double turn, double drive){

        double frPower = (drive - strafe - turn) * power;
        double flPower = (drive + strafe + turn) * power;
        double brPower = (drive + strafe - turn) * power;
        double blPower = (drive - strafe + turn) * power;

        fr.setPower(frPower);
        fl.setPower(flPower);
        br.setPower(brPower);
        bl.setPower(blPower);


    }


    public void strafe(double power, double ticks, double targetAngle, double strafeAngle, double marginOfError){

        // Reset our encoders to 0
        resetMotors();
        timeOut.reset();



        strafeAngle = strafeAngle - 90;
        targetAngle= targetAngle - 180;


        //Blue Switch

        targetAngle = closestAngle(targetAngle, gyro.rawAngle());

        // Calculate our x and y powers
        double xPower = cos(strafeAngle, DEGREES);
        double yPower = sin(strafeAngle, DEGREES);

        // Calculate the distances we need to travel
        double xDist = xPower * ticks;
        double yDist = yPower * ticks;



        // Initialize our current position variables
        Point curPos;
        double curHDist = 0;

        while ((curHDist < ticks || gyro.absAngularDist(targetAngle) > marginOfError) && timeOut.seconds() < ticks/500){
            gyro.update();
            curPos = getPosition();


            curHDist = Math.hypot(curPos.x, curPos.y);
            Point shiftedPowers = MathUtils.shift(new Point(xPower, yPower), -gyro.rawAngle());


            if(curHDist < ticks){

                setDrivePower(power, shiftedPowers.x, pid.update(targetAngle - gyro.rawAngle()), shiftedPowers.y);
            }else{
                setDrivePower(power, 0, pid.update(targetAngle - gyro.rawAngle()), 0);
            }




        }
        setAllPower(0);
    }

    public void strafe(double power, double ticks, double targetAngle, double strafeAngle){
        strafe(power, ticks, targetAngle, strafeAngle, 8);
    }

    public void turn(double targetAngle){
        targetAngle = closestAngle(targetAngle, gyro.rawAngle());
        while(gyro.rawAngle() != targetAngle) {
            gyro.update();
            pid.update(gyro.rawAngle() - targetAngle);
        }
    }

    public void sleep(double time, ElapsedTime timer){
        timer.reset();
        while(timer.seconds() < time){
        }
    }

    public void cycle(Intake intake, ScoringMechanism scorer, Distance_Sensor distance,int cycleNo){
        cycleDist = 8;
        double backingUp = 0;
        if(Side.red){

            strafe(.6,1000,270,270);

            if (cycleNo != 1){
                strafe(.4,200,270,0);
            }

            intake.autoSpin();
            scorer.updateBucketSensor();
            while (!scorer.isLoaded()) {

                distance.distanceUpdate();
                scorer.updateBucketSensor();
                intake.updateEncoders();

                if(!distance.isChanging){
                    intake.autoBackSpin();
                    strafe(.6,100,270,90);
                    multTelemetry.addData("Stage", "Retreating");
                    cycleDist = cycleDist - 1;
                }
                if (!intake.jammed()) {
                    intake.autoSpin();
                    strafe(.2, 100, 270, 270);
                    multTelemetry.addData("Stage", "Not Jammed, going slowly forwards");
                    cycleDist = cycleDist + 1;
                }else{
                    intake.autoBackSpin();
                    strafe(.6, 100, 270, 90);
                    multTelemetry.addData("Stage", "Retreating");
                    cycleDist = cycleDist - 1;
                }

                multTelemetry.addData("isChanging", distance.isChanging);
                multTelemetry.update();
            }

            intake.stop();
            intake.autoBackSpin();
            multTelemetry.addData("Stage", "Going Into Wall");
            multTelemetry.update();
            strafe(.2,100,270,180);

            multTelemetry.addData("Stage", "Leaving Warehouse");
            multTelemetry.update();
            strafe(.6,2200,270,95);

            intake.stop();
            multTelemetry.addData("Stage", "Out of warehouse");
            multTelemetry.update();

            strafe(.5, 600, 270, 10);
            strafe(.4, 300, 190, 5);
            scorer.autoTop();
            scorer.autoDeposit();
            strafe(.6, 600, 210, 185);
            strafe(.5, 600, 270, 190);
            strafe(.4,200,270,180);

        }else if(Side.blue){

        }
    }

}