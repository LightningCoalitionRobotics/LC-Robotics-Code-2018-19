package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * The class for the LCR 2019-20 robot.
 *
 * If more components are added, please add them to this class!
 *
 * @author Noah Simon
 */
public class HardwareLilPanini extends Robot {

    // These are constants that we have experimentally determined, relating counts (the way an encoder measures movement) to inches or degrees (the way we understand movement)
    private static final int COUNTS_PER_REVOLUTION = 1400;                         // One full revolution of a wheel is 1400 counts
    private static final int COUNTS_PER_FORWARD_INCH = COUNTS_PER_REVOLUTION / 12; // 1 revolution FORWARDS is very close to 1 foot, so to get counts per inch, take counts per revolution and divide it by 12

    private static final int COUNTS_PER_360 = 10000;                               // One full turn 360 degrees is 10000 counts
    private static final int COUNTS_PER_DEGREE = COUNTS_PER_360 / 360;

    private static final int COUNTS_PER_SIDE_FOOT = 2000;                          // The amount of counts per the robot moving to the SIDE 1 foot is 2000, NOTICE this is different than the amount of counts going forward or backwards
    private static final int COUNTS_PER_SIDE_INCH = COUNTS_PER_SIDE_FOOT/12;

    // All of the components we will need (e.g. motors, servos, sensors...) that are attached to the robot are declared here

    public DcMotor motorFrontLeft;

    public DcMotor motorFrontRight;

    public DcMotor motorBackLeft;

    public DcMotor motorBackRight;

    public HardwareLilPanini(OpMode opMode) {
        super(opMode);
    }

    @Override  // Since this class extends the class Robot, these @Overrides let the code know that this will supercede any conflicting properties of init present in class Robot
    public void init(HardwareMap hardwareMap) { //This section registers the motors to the encoders and sets their default direction
        motorFrontLeft = registerMotor("motorFrontLeft", DcMotorSimple.Direction.FORWARD, DcMotor.RunMode.RUN_USING_ENCODER);
        motorFrontRight = registerMotor("motorFrontRight", DcMotorSimple.Direction.REVERSE, DcMotor.RunMode.RUN_USING_ENCODER); //this direction is reverse because the motor is backward, so to make it go forwards you (if you had this forwards) would have to set a negative speed
        motorBackLeft = registerMotor("motorRearLeft", DcMotorSimple.Direction.FORWARD, DcMotor.RunMode.RUN_USING_ENCODER);
        motorBackRight = registerMotor("motorRearRight", DcMotorSimple.Direction.REVERSE, DcMotor.RunMode.RUN_USING_ENCODER); // Same problem as above with this motor
    }

    @Override
    /**
     * Drive the robot forward or backwards.
     * @param speed An integer between -1 and 1, greater distance from origin is greater speed, negative is backwards and positive is forwards
     * @param dist Distance, in inches, that you want the robot to go (always positive)
     * @param timeout How many seconds before stopping wherever it is
     */
    public void drive(double speed, double dist, double timeout) {
        int distInCounts = (int) (dist * COUNTS_PER_FORWARD_INCH); //convert distance in inches provided to distance in counts for motor to understand

        // Target count value for each motor given dist, calculated from current position in counts plus (or minus if going backwards) distance in counts
        int topRightTargetForward = motorFrontRight.getCurrentPosition() + distInCounts;
        int topLeftTargetForward = motorFrontLeft.getCurrentPosition() + distInCounts;
        int bottomRightTargetForward = motorBackRight.getCurrentPosition() + distInCounts;
        int bottomLeftTargetForward = motorBackLeft.getCurrentPosition() + distInCounts;
        int topRightTargetBackward = motorFrontRight.getCurrentPosition() - distInCounts;
        int topLeftTargetBackward = motorFrontLeft.getCurrentPosition() - distInCounts;
        int bottomRightTargetBackward = motorBackRight.getCurrentPosition() - distInCounts;
        int bottomLeftTargetBackward = motorBackLeft.getCurrentPosition() - distInCounts;

        if (speed < 0) { // if we are trying to go forwards

            while (((LinearOpMode) opMode).opModeIsActive() && elapsedTime.seconds() < timeout) { //while opmode active and timeout not reached
                if (motorFrontRight.getCurrentPosition() <= topRightTargetForward || motorFrontLeft.getCurrentPosition() <= topLeftTargetForward || motorBackRight.getCurrentPosition() <= bottomRightTargetForward || motorBackLeft.getCurrentPosition() <= bottomLeftTargetForward) { // Very complicated way of saying 'if each motor hasn't yet reached target counts'
                    motorFrontRight.setPower(speed); //set motors to speed
                    motorFrontLeft.setPower(speed);
                    motorBackRight.setPower(speed);
                    motorBackLeft.setPower(speed);
                } else { //if opmode is not active or timeout reached
                    stop(); // This function is declared at the bottom, it sets all motor power to 0
                }
            }
        }
        if (speed > 0) { //if backwards
            while (((LinearOpMode) opMode).opModeIsActive() && elapsedTime.seconds() < timeout) { //while opmode active and timeout not reached
                if (motorFrontRight.getCurrentPosition() >= topRightTargetBackward || motorFrontLeft.getCurrentPosition() >= topLeftTargetBackward || motorBackRight.getCurrentPosition() >= bottomRightTargetBackward || motorBackLeft.getCurrentPosition() >= bottomLeftTargetBackward) { // Very complicated way of saying 'if each motor hasn't yet reached target counts'
                    motorFrontRight.setPower(speed); //set motors to speed while opmode active
                    motorFrontLeft.setPower(speed);
                    motorBackRight.setPower(speed);
                    motorBackLeft.setPower(speed);
                } else { //if opmode is not active or timeout reached
                    stop();
                }
            }
        }
    }

    /**
     * Drive the robot at a particular angle.
     * @param degrees The angle at which to move the robot. Measured in degrees above the positive X axis.
     * @param speed How fast the robot should move. Number should be in range (0, 1].
     * @param dist How far, in inches, to move the robot.
     * @param timeout If dist is never reached, how many seconds to wait before stopping.
     */
    public void driveAngle(double degrees, double speed, double dist, double timeout) {

    }

    /**
     * Strafe the robot left or right.
     * @param direction The direction in which to move the robot.
     * @param speed How fast the robot should move. Number should be in range (0, 1].
     * @param dist How far, in inches, to move the robot.
     * @param timeout If dist is never reached, how many seconds to wait before stopping.
     */
    public void strafe(HorizontalDirection direction, double speed, double dist, double timeout) {
        int distInCounts = (int)(dist * COUNTS_PER_SIDE_INCH);  // Once again, converting from things we understand to the language the motor understands

        int correctDirection;
        if (direction == HorizontalDirection.LEFT) {
            correctDirection = 1;
        } else {
            correctDirection = -1;
        }

        int topRightTarget = motorFrontRight.getCurrentPosition() + correctDirection * distInCounts;
        int topLeftTarget = motorFrontLeft.getCurrentPosition() - correctDirection * distInCounts;
        int bottomLeftTarget = motorBackLeft.getCurrentPosition() + correctDirection * distInCounts;
        int bottomRightTarget = motorBackRight.getCurrentPosition() - correctDirection * distInCounts;

        motorFrontRight.setPower(speed * correctDirection);
        motorFrontLeft.setPower(-speed * correctDirection);
        motorBackLeft.setPower(speed * correctDirection);
        motorBackRight.setPower(-speed * correctDirection);

        while (((LinearOpMode) opMode).opModeIsActive() && elapsedTime.seconds() < timeout) {
            if (direction == HorizontalDirection.LEFT) {
                if (motorFrontRight.getCurrentPosition() >= topRightTarget || motorFrontLeft.getCurrentPosition() <= topLeftTarget || motorBackLeft.getCurrentPosition() >= bottomLeftTarget || motorBackRight.getCurrentPosition() <= bottomRightTarget) {
                    break;
                }
            } else {
                if (motorFrontRight.getCurrentPosition() <= topRightTarget || motorFrontLeft.getCurrentPosition() >= topLeftTarget || motorBackLeft.getCurrentPosition() <= bottomLeftTarget || motorBackRight.getCurrentPosition() >= bottomRightTarget) {
                    break;
                }
            }
            ((LinearOpMode) opMode).idle();
        }

        stop();
    }

    @Override
    public void turn(double speed, double angle, double timeout) {
    int angleInCounts = (int)(angle * COUNTS_PER_DEGREE);
    //changes the angle variable from degrees to counts

    int topRightTarget = motorFrontRight.getCurrentPosition() + angleInCounts;
    int topLeftTarget = motorFrontLeft.getCurrentPosition() - angleInCounts;
    int bottomLeftTarget = motorBackLeft.getCurrentPosition() - angleInCounts;
    int bottomRightTarget = motorBackRight.getCurrentPosition() + angleInCounts;
    //finds target number of counts for each motor

    if (angle > 0) {
        motorFrontRight.setPower(speed);
        motorFrontLeft.setPower(-speed);
        motorBackLeft.setPower(-speed);
        motorBackRight.setPower(speed);
    //sets rights motors to positive and left to negative for counterclockwise turn
    } else {
        motorFrontRight.setPower(-speed);
        motorFrontLeft.setPower(speed);
        motorBackLeft.setPower(speed);
        motorBackRight.setPower(-speed);
    //sets left motors to positive and right to negative for clockwise turn
    }


        while (((LinearOpMode) opMode).opModeIsActive() && elapsedTime.seconds() < timeout){ //while opmode active and timenout not reached
            if (angle > 0){
                if (motorFrontRight.getCurrentPosition() >= topRightTarget || motorFrontLeft.getCurrentPosition() <= topLeftTarget || motorBackLeft.getCurrentPosition() <= bottomLeftTarget || motorBackRight.getCurrentPosition() >= bottomRightTarget) {
                    break;
                }
            } else {
                if (motorFrontRight.getCurrentPosition() <= topRightTarget || motorFrontLeft.getCurrentPosition() >= topLeftTarget || motorBackLeft.getCurrentPosition() >= bottomLeftTarget || motorBackRight.getCurrentPosition() <= bottomRightTarget) {
                    break;
                }
            }
            ((LinearOpMode) opMode).idle();
        }
        stop();
        //tells motors to stop if they've reached target number of counts
    }

    public void stop() {
        motorFrontRight.setPower(0);
        motorFrontLeft.setPower(0);
        motorBackLeft.setPower(0);
        motorBackRight.setPower(0);
    }

    public enum HorizontalDirection { //Enumerator declared for strafe function
        RIGHT,
        LEFT
    }
}
