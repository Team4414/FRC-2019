package frc.util.kinematics.pos;

/**
 * RobotPos Class.
 * 
 * <p>A simple object to hold the position of a robot that can only be set, not modified.</p>
 */
public class RobotPos{

    /**
     * The X position of the Robot
     */
    private double x;

    /**
     * The Y position of the Robot
     */
    private double y;

    /**
     * The heading of the Robot
     */
    private double heading;

    /**
     * RobotPos Constructor.
     * 
     * @param x The X position of the robot.
     * @param y The Y position of the robot.
     * @param heading The heading of the robot.
     */
    public RobotPos(double x, double y, double heading){
        this.x = x;
        this.y = y;
        this.heading = heading;
    }

    /**
     * RobotPos Constructor
     * 
     * @param clone The RobotPos object to be cloned
     */
    public RobotPos(RobotPos clone){
        this(clone.x, clone.y, clone.heading);
    }

    // /**
    //  * RobotPos Constructor
    //  * 
    //  * @param clone The RobotPos object to be cloned
    //  */
    // public RobotPos(RobotPos clone, boolean invert){
    //     // if(invert){
    //     //     this.x = -clone.x;
    //     //     this.y = -clone.y;
    //     //     this.heading = heading;
    //     //     System.out.println("robotoisdidconstruct");
    //     // }else{
    //         this.x = clone.x;
    //         this.y = clone.y;
    //         this.heading = clone.heading;
    //     // }
    // }

    /**
     * Get X Method.
     * 
     * @return The X Position of the Robot

     */
    public double getX(){
        return x;
    }

    /**
     * Get Y Method.
     * 
     * 
     * @return The Y Position of the Robot
     */
    public double getY(){
        return y;
    }

    /**
     * Get Heading Method.
     * 
     * @return The Heading of the Robot
     */
    public double getHeading(){
        return heading;
    }

    /**
     * Add Method
     * 
     * <p>Accepts a {@link Deltas} and modifies the position accordingly
     * 
     * @param delta the delta used to modify the position.
     */
    public void add(Delta delta){
        x += delta.getX();
        y += delta.getY();
        heading = delta.getHeading();
    }

    /**
     * Deltas Constructor
     * 
     * @param dX the change in X Position
     * @param dY the change in Y Position
     * @param absHeading the absolute Heading. (NOT a Delta!)
     */
    public static class Delta extends RobotPos{
        public Delta(double dX, double dY, double absHeading){
            super(dX, dY, absHeading);
        }

        public Delta(Delta clone){
            super(clone);
        }
    }
}