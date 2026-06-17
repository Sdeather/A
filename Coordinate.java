/**
 * Coordinate class representing a location in Minecraft
 */
public class Coordinate {
    private double x;
    private double y;
    private double z;
    private String dimension; // "Overworld", "Nether", "End"
    private String name;

    public Coordinate(double x, double y, double z, String dimension, String name) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
        this.name = name;
    }

    // Getters and Setters
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public double getZ() { return z; }
    public void setZ(double z) { this.z = z; }

    public String getDimension() { return dimension; }
    public void setDimension(String dimension) { this.dimension = dimension; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    /**
     * Convert Overworld coordinates to Nether coordinates
     */
    public Coordinate toNether() {
        if ("Overworld".equals(dimension)) {
            return new Coordinate(x / 8, y, z / 8, "Nether", name + " (Nether)");
        }
        return this;
    }

    /**
     * Convert Nether coordinates to Overworld coordinates
     */
    public Coordinate toOverworld() {
        if ("Nether".equals(dimension)) {
            return new Coordinate(x * 8, y, z * 8, "Overworld", name + " (Overworld)");
        }
        return this;
    }

    /**
     * Calculate distance to another coordinate
     */
    public double distanceTo(Coordinate other) {
        if (!this.dimension.equals(other.dimension)) {
            return -1; // Different dimensions
        }
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double dz = this.z - other.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Calculate horizontal distance (ignoring Y)
     */
    public double horizontalDistanceTo(Coordinate other) {
        if (!this.dimension.equals(other.dimension)) {
            return -1;
        }
        double dx = this.x - other.x;
        double dz = this.z - other.z;
        return Math.sqrt(dx * dx + dz * dz);
    }

    @Override
    public String toString() {
        return String.format("%s [%.1f, %.1f, %.1f] (%s)", name, x, y, z, dimension);
    }
}
