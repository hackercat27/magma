package ca.hackercat.magma.io;

import ca.hackercat.logging.Logger;
import ca.hackercat.magma.core.Mesh;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileUtils {
    private static final Logger LOGGER = Logger.get(FileUtils.class);

    private static boolean fileInject = true;

    public static void setFileInject(boolean inject) {
        FileUtils.fileInject = inject;
    }

    public static String getContents(String path) {
        InputStream is = getInputStream(path);
        if (is == null) {
            LOGGER.warn("Attempted to read contents of '" + path + "', but file doesn't exist!");
            return "";
        }
        Scanner scan = new Scanner(is);
        StringBuilder b = new StringBuilder();
        while (scan.hasNextLine()) {
            b.append(scan.nextLine()).append("\n");
        }
        return b.toString();
    }

    private static boolean isResource(String path) {
        return path.charAt(0) == '/';
    }

    public static InputStream getInputStream(String path) {
        InputStream is = null;
        if (isResource(path)) {
            if (fileInject) {
                try {
                    is = new FileInputStream(path);
                    LOGGER.log("Returning InputStream '" + path + "' from system resources instead of program resources");
                } catch (FileNotFoundException ignored) {
                    LOGGER.log("Returning InputStream '" + path + "' from program resources");
                    is = FileUtils.class.getResourceAsStream(path);
                }
            }
            else {
                LOGGER.log("Returning InputStream '" + path + "' from program resources");
                is = FileUtils.class.getResourceAsStream(path);
            }
        }
        else {
            try {
                is = new FileInputStream(path);
                LOGGER.log("Returning InputStream '" + path + "' from system resources");
                return is;
            } catch (FileNotFoundException e) {
                LOGGER.error(e);
            }
        }
        if (is == null) {
            LOGGER.warn("InputStream '" + path + "' == null!");
        }
        return is;
    }

    private static class OBJVertex {

        private int position;
        private int uv;
        private int normal;

        public OBJVertex() {}

        public OBJVertex(String encodedNumbers) {

            String[] nums = encodedNumbers.split("/");
            position = Integer.parseInt(nums[0]) - 1;
            uv       = Integer.parseInt(nums[1]) - 1;
            normal   = Integer.parseInt(nums[2]) - 1;

        }

        public OBJVertex(int position, int uv, int normal) {
            this.position = position;
            this.uv = uv;
            this.normal = normal;
        }
    }

    private static class OBJTri {

        OBJVertex p1, p2, p3;

        public OBJTri(OBJVertex p1, OBJVertex p2, OBJVertex p3) {
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
        }

        public OBJVertex[] getVertices() {
            return new OBJVertex[] {p1, p2, p3};
        }

        public OBJVertex getP1() {
            return p1;
        }

        public OBJVertex getP2() {
            return p2;
        }

        public OBJVertex getP3() {
            return p3;
        }
    }

    public static Mesh loadWavefrontOBJ(String path, String texturePath) {
        String contents = getContents(path);

        List<Vector3f> unsortedPositionList = new ArrayList<>();
        List<Vector2f> unsortedUVList = new ArrayList<>();
        List<Vector3f> unsortedNormalList = new ArrayList<>();
        List<OBJTri> faceList = new ArrayList<>();

        Vector3f[] positions;
        Vector2f[] uvs;
        Vector3f[] normals;
        int[] indices;

        final String position_prefix = "v ";
        final String uv_prefix = "vt ";
        final String normal_prefix = "vn ";
        final String face_prefix = "f ";

        for (String line : contents.split("\n")) {

            if (line.startsWith(position_prefix)) {
                String[] sublines = line.split(" ");

                float x = Float.parseFloat(sublines[1]);
                float y = Float.parseFloat(sublines[2]);
                float z = Float.parseFloat(sublines[3]);

                unsortedPositionList.add(new Vector3f(x, y, z));
            }
            else if (line.startsWith(uv_prefix)) {
                String[] sublines = line.split(" ");

                float x = Float.parseFloat(sublines[1]);
                float y = 1 - Float.parseFloat(sublines[2]);
                // blender has 0, 0 at top left but opengl expects 0, 0 to be bottom left

                unsortedUVList.add(new Vector2f(x, y));
            }
            else if (line.startsWith(normal_prefix)) {
                String[] sublines = line.split(" ");

                float x = Float.parseFloat(sublines[1]);
                float y = Float.parseFloat(sublines[2]);
                float z = Float.parseFloat(sublines[3]);

                unsortedNormalList.add(new Vector3f(x, y, z));
            }
            else if (line.startsWith(face_prefix)) {
                String[] sublines = line.split(" ");
                faceList.add(new OBJTri(
                        new OBJVertex(sublines[1]),
                        new OBJVertex(sublines[2]),
                        new OBJVertex(sublines[3])
                ));
            }

        }

        int length = faceList.size() * 3;

        LOGGER.log(length);

        positions = new Vector3f[length];
        uvs = new Vector2f[length];
        normals = new Vector3f[length];
        indices = new int[length];

//        LOGGER.log(unsortedPositionList.size() + " " + unsortedUVList.size() + " " + unsortedNormalList.size() + " " + (faceList.size() * 3));

        int currentIndex = 0;
        for (OBJTri face : faceList) {

            for (OBJVertex vertex : face.getVertices()) {
                positions[currentIndex] = unsortedPositionList.get(vertex.position);
                uvs[currentIndex] = unsortedUVList.get(vertex.uv);
                normals[currentIndex] = unsortedNormalList.get(vertex.normal);
                indices[currentIndex] = currentIndex;

                currentIndex++;
            }

        }

        return new Mesh(positions, uvs, normals, indices, new Texture(texturePath));
    }
}
