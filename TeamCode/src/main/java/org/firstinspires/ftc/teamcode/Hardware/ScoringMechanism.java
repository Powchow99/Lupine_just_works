package org.firstinspires.ftc.teamcode.Hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import static org.firstinspires.ftc.teamcode.Utilities.OpModeUtils.hardwareMap;
import static org.firstinspires.ftc.teamcode.Utilities.OpModeUtils.multTelemetry;

import org.firstinspires.ftc.teamcode.Utilities.Unfixed;
import org.opencv.android.Utils;

public class ScoringMechanism {
    public DcMotor spool;
    public Servo bucket;
    public static ElapsedTime time = new ElapsedTime();
    public boolean armUp = false;

    public ScoringMechanism() {
        spool = hardwareMap.get(DcMotor.class, "spool");
        bucket = hardwareMap.get(Servo.class, "bucket");
        bucket.setPosition(0.82);
        resetMotors();

    }

    public void resetMotors() {
        spool.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        spool.setTargetPosition(0);
        spool.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        spool.setPower(0.8);


    }


    public void top() {

        armUp = true;
        if(time.seconds() > 0 && time.seconds() < .1){
            bucket.setPosition(0.7);
        }
        if (time.seconds() > .1 && time.seconds() < .7) {
            spool.setTargetPosition(-1900);
            spool.setPower((spool.getTargetPosition() - spool.getCurrentPosition()) / (double) spool.getTargetPosition() + 0.2);
        }
    }


    public void middle() {

        armUp = true;
        if(time.seconds() > 0 && time.seconds() < .1){
            bucket.setPosition(0.7);
        }
        if (time.seconds() > .1 && time.seconds() < .7) {
            spool.setTargetPosition(-1000);
            spool.setPower((spool.getTargetPosition() - spool.getCurrentPosition()) / (double) spool.getTargetPosition() + 0.2);
        }
    }

    public void bottom() {

        armUp = true;
        if(time.seconds() > 0 && time.seconds() < .1){
            bucket.setPosition(0.7);
        }
        if (time.seconds() > .1 && time.seconds() < .7) {
            spool.setTargetPosition(-500);
            spool.setPower((spool.getTargetPosition() - spool.getCurrentPosition()) / (double) spool.getTargetPosition() + 0.2);
        }
    }

    public void deposit() {
        armUp = true;
        if (time.seconds() < .5) {
            bucket.setPosition(0);
        }
        if(time.seconds() > 1 && time.seconds() < 1.5){
            bucket.setPosition(0.7);
        }
        if (time.seconds() > 2 && time.seconds() < 2.2) {
            drivingFromUp();
        }
        armUp = false;
    }

    public void drivingFromIntake(){
        if(time.seconds() > 0 && time.seconds() < .1){
            bucket.setPosition(0.7);
        }
        if(time.seconds() > 0.2 && time.seconds() < .5){
            spool.setTargetPosition(-300);
        }
    }

    public void drivingFromUp(){
        if(time.seconds() > 0 && time.seconds() < .1){
            spool.setTargetPosition(-300);
        }
        if(time.seconds() > 0.2 && time.seconds() < .5){
            bucket.setPosition(0.7);
        }
    }

    public void intake(){
        if (time.seconds() > 2.5 && time.seconds() < 3) {
            spool.setTargetPosition(0);
        }
        if (time.seconds() > 3 && time.seconds() < 3.1) {
            bucket.setPosition(0.85);
            armUp = false;
        }
    }

    public void autoTop() {
        time.reset();

        armUp = true;

            bucket.setPosition(0.7);

wait(0.7);
            spool.setTargetPosition(-1900);
            spool.setPower((spool.getTargetPosition() - spool.getCurrentPosition()) / (double) spool.getTargetPosition() + 0.2);

            wait(1.0);
    }


    public void autoMiddle() {
        time.reset();
        armUp = true;
        if(time.seconds() > 0 && time.seconds() < .1){
            bucket.setPosition(0.7);
        }
        if (time.seconds() > .1 && time.seconds() < .7) {
            spool.setTargetPosition(-1000);
            spool.setPower((spool.getTargetPosition() - spool.getCurrentPosition()) / (double) spool.getTargetPosition() + 0.2);
        }
    }

    public void autoBottom() {
        time.reset();
        armUp = true;

            bucket.setPosition(0.7);
        if (time.seconds() > .1 && time.seconds() < .7) {
            spool.setTargetPosition(-500);
            spool.setPower((spool.getTargetPosition() - spool.getCurrentPosition()) / (double) spool.getTargetPosition() + 0.2);
        }
    }

    public void autoDeposit() {

        armUp = true;
        wait(0.5);
            bucket.setPosition(0);

        wait(1.5);

        bucket.setPosition(0.82);
        wait(2.0);

        drivingFromUp();

        armUp = false;
    }

    public void autoDrivingFromIntake(){
        time.reset();
        if(time.seconds() > 0 && time.seconds() < .1){
            bucket.setPosition(0.7);
        }
        if(time.seconds() > 0.2 && time.seconds() < .5){
            spool.setTargetPosition(-300);
        }
    }

    public void autoDrivingFromUp(){
        time.reset();
        if(time.seconds() > 0 && time.seconds() < .1){
            spool.setTargetPosition(-300);
        }
        if(time.seconds() > 0.2 && time.seconds() < .5){
            bucket.setPosition(0.7);
        }
    }

    public void autoIntake(){
        time.reset();
        if (time.seconds() > 2.5 && time.seconds() < 3) {
            spool.setTargetPosition(0);
        }
        if (time.seconds() > 3 && time.seconds() < 3.1) {
            bucket.setPosition(0.85);
            armUp = false;
        }
    }

    public void wait(double timeout){
        time.reset();
        while(time.seconds()<timeout){

        }
    }

}
