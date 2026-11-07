package domain;

import java.io.Serializable;

/**
 * The field in one of the three resolutions, with a goal at each side.
 * The goals are scaled to the size of the field.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class Stadium implements Serializable {

    /** Width and height of the small field, in pixels. */
    public static final int[] SMALL = {960, 540};
    /** Width and height of the medium field, in pixels. */
    public static final int[] MEDIUM = {1280, 720};
    /** Width and height of the large field, in pixels. */
    public static final int[] LARGE = {1600, 900};
    /** Height of the ground as a share of the field height (the base of the posts in the background). */
    public static final double GROUND_SHARE = 0.90;
    /** Depth of each goal, from its side to the front post, as a share of the field width. */
    public static final double GOAL_WIDTH_SHARE = 0.12;
    /** Height of the goals, from the ground to the crossbar, as a share of the field height. */
    public static final double GOAL_HEIGHT_SHARE = 0.235;

    /** Resolution of the field. */
    private final String resolution;
    /** Width of the field, in pixels. */
    private final double width;
    /** Height of the field, in pixels. */
    private final double height;
    /** Height of the ground, in pixels. */
    private final double groundY;
    /** Goal of the left side. */
    private final Goal leftGoal;
    /** Goal of the right side. */
    private final Goal rightGoal;

    /**
     * Creates the field and its two goals.
     *
     * @param resolution "SMALL", "MEDIUM" or "LARGE"
     * @throws DOPOSoccerException UNKNOWN_RESOLUTION if the resolution is not one of the three
     */
    public Stadium(String resolution) throws DOPOSoccerException {
        int[] size = sizeOf(resolution);
        this.resolution = resolution;
        this.width = size[0];
        this.height = size[1];
        this.groundY = height * GROUND_SHARE;
        double goalWidth = width * GOAL_WIDTH_SHARE;
        double goalHeight = height * GOAL_HEIGHT_SHARE;
        this.leftGoal = new Goal("LEFT", 0, groundY - goalHeight, goalWidth, goalHeight);
        this.rightGoal = new Goal("RIGHT", width - goalWidth, groundY - goalHeight, goalWidth, goalHeight);
    }

    /**
     * Returns the size of a resolution.
     *
     * @param resolution "SMALL", "MEDIUM" or "LARGE"
     * @return width and height, in pixels
     * @throws DOPOSoccerException UNKNOWN_RESOLUTION if it is not one of the three
     */
    private static int[] sizeOf(String resolution) throws DOPOSoccerException {
        if (resolution == null) 
        {
            resolution = "";
        }
        switch (resolution) {
            case "SMALL":
                return SMALL;
            case "MEDIUM":
                return MEDIUM;
            case "LARGE":
                return LARGE;
            default:
                throw new DOPOSoccerException(DOPOSoccerException.UNKNOWN_RESOLUTION);
        }
    }

    /**
     * Returns the goal that a side defends.
     *
     * @param side "LEFT" or "RIGHT"
     * @return its goal, or null for an unknown side
     */
    public Goal getGoal(String side) {
        if ("LEFT".equals(side)) {
            return leftGoal;
        }
        return rightGoal;
    }

    /**
     * Returns the width of the field.
     *
     * @return width, in pixels
     */
    public double getWidth() {
        return width;
    }

    /**
     * Returns the height of the field.
     *
     * @return height, in pixels
     */
    public double getHeight() {
        return height;
    }

    /**
     * Returns the height of the ground: players stand on it.
     *
     * @return y of the ground, in pixels
     */
    public double getGroundY() {
        return groundY;
    }

    /**
     * Returns the resolution of the field.
     *
     * @return "SMALL", "MEDIUM" or "LARGE"
     */
    public String getResolution() {
        return resolution;
    }
}
