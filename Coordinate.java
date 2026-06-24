/**
 * Coordinate class representing a location in Minecraft.
 *
 * Improvements:
 * - Uses an enum for dimension to avoid string typos.
 * - Immutable (final fields). "withX/withY/withZ/withName/withDimension" return new instances.
 * - Implements equals() and hashCode().
 * - toNether()/toOverworld() return new instances and avoid appending duplicate suffixes.
 * - distanceTo()/horizontalDistanceTo() throw IllegalArgumentException for different dimensions.
 */
public final class Coordinate {
    public enum Dimension {
        OVERWORLD, NETHER, END;

        public static Dimension fromString(String s) {
            if (s == null) throw new IllegalArgumentException("dimension cannot be null");
            switch (s.trim().toLowerCase()) {
                case "overworld": return OVERWORLD;
                case "nether": return NETHER;
                case "end": return END;
                default: throw new IllegalArgumentException("Unknown dimension: " + s);
            }
        }

        @Override
        public String toString() {
            switch (this) {
                case OVERWORLD: return "Overworld";
                case NETHER: return "Nether";
                case END: return "End";
                default: return super.toString();
            }
        }
    }

    private final double x;
    private final double y;
    private final double z;
    private final Dimension dimension;
    private final String name;

    public Coordinate(double x, double y, double z, Dimension dimension, String name) {
        if (dimension == null) throw new IllegalArgumentException("dimension cannot be null");
        if (name == null) throw new IllegalArgumentException("name cannot be null");
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
        this.name = name;
    }

    // Backwards-friendly factory if you prefer passing a String dimension
    public static Coordinate of(double x, double y, double z, String dimension, String name) {
        return new Coordinate(x, y, z, Dimension.fromString(dimension), name);
    }

    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public Dimension getDimension() { return dimension; }
    public String getName() { return name; }

    // 'With' methods return new instances (immutability)
    public Coordinate withX(double x) { return new Coordinate(x, this.y, this.z, this.dimension, this.name); }
    public Coordinate withY(double y) { return new Coordinate(this.x, y, this.z, this.dimension, this.name); }
    public Coordinate withZ(double z) { return new Coordinate(this.x, this.y, z, this.dimension, this.name); }
    public Coordinate withDimension(Dimension dimension) { return new Coordinate(this.x, this.y, this.z, dimension, this.name); }
    public Coordinate withName(String name) { return new Coordinate(this.x, this.y, this.z, this.dimension, name); }

    /**
     * Convert Overworld coordinates to Nether coordinates (scale X/Z by 1/8).
     * If already in Nether, returns this (same instance).
     * For End or other dimensions: throws IllegalStateException (explicit).
     */
    public Coordinate toNether() {
        if (this.dimension == Dimension.NETHER) return this;
        if (this.dimension == Dimension.OVERWORLD) {
            String newName = name.endsWith(" (Nether)") ? name : name + " (Nether)";
            return new Coordinate(x / 8.0, y, z / 8.0, Dimension.NETHER, newName);
        }
        throw new IllegalStateException("Cannot convert from " + this.dimension + " to Nether");
    }

    /**
     * Convert Nether coordinates to Overworld coordinates (scale X/Z by 8).
     * If already in Overworld, returns this (same instance).
     */
    public Coordinate toOverworld() {
        if (this.dimension == Dimension.OVERWORLD) return this;
        if (this.dimension == Dimension.NETHER) {
            String newName = name.endsWith(" (Overworld)") ? name : name + " (Overworld)";
            return new Coordinate(x * 8.0, y, z * 8.0, Dimension.OVERWORLD, newName);
        }
        throw new IllegalStateException("Cannot convert from " + this.dimension + " to Overworld");
    }

    /**
     * Calculate 3D distance to another coordinate.
     * Throws IllegalArgumentException if coordinates are in different dimensions.
     */
    public double distanceTo(Coordinate other) {
        if (other == null) throw new IllegalArgumentException("other cannot be null");
        if (this.dimension != other.dimension) {
            throw new IllegalArgumentException("Cannot compute distance across dimensions: " + this.dimension + " vs " + other.dimension);
        }
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double dz = this.z - other.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Calculate horizontal distance (ignoring Y).
     * Throws IllegalArgumentException if coordinates are in different dimensions.
     */
    public double horizontalDistanceTo(Coordinate other) {
        if (other == null) throw new IllegalArgumentException("other cannot be null");
        if (this.dimension != other.dimension) {
            throw new IllegalArgumentException("Cannot compute distance across dimensions: " + this.dimension + " vs " + other.dimension);
        }
        double dx = this.x - other.x;
        double dz = this.z - other.z;
        return Math.sqrt(dx * dx + dz * dz);
    }

    @Override
    public String toString() {
        return String.format("%s [%.1f, %.1f, %.1f] (%s)", name, x, y, z, dimension.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordinate)) return false;
        Coordinate other = (Coordinate) o;
        return Double.doubleToLongBits(x) == Double.doubleToLongBits(other.x)
            && Double.doubleToLongBits(y) == Double.doubleToLongBits(other.y)
            && Double.doubleToLongBits(z) == Double.doubleToLongBits(other.z)
            && dimension == other.dimension
            && name.equals(other.name);
    }

    @Override
    public int hashCode() {
        int result = 17;
        long temp = Double.doubleToLongBits(x);
        result = 31 * result + (int)(temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int)(temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(z);
        result = 31 * result + (int)(temp ^ (temp >>> 32));
        result = 31 * result + dimension.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
