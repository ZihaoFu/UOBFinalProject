package fuzihao.test1.Model;

public class RayPickingHelper {
    public static class Vector {
        public float x, y, z;

        public Vector(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public float length() {
            return (float) Math.sqrt(x * x + y * y + z * z);
        }

        // http://en.wikipedia.org/wiki/Cross_product
        public Vector crossProduct(Vector other) {
            return new Vector((y * other.z) - (z * other.y), (z * other.x) - (x * other.z),
                    (x * other.y) - (y * other.x));
        }

        public float dotProduct(Vector other) {
            return x * other.x + y * other.y + z * other.z;
        }

        public Vector scale(float f) {
            return new Vector(x * f, y * f, z * f);
        }
    }
}
