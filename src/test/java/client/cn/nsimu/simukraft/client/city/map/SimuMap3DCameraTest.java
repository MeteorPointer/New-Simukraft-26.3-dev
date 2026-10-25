package client.cn.nsimu.simukraft.client.city.map;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SimuMap3DCameraTest {
    @Test
    void modelMatrix_liftsHigherBlocksTowardTheTopOfTheScreen() {
        Matrix4f matrix = SimuMap3DCamera.modelMatrix(400.0F, 300.0F, 0.6F, 2.0F, 64.0F);
        Vector4f ground = matrix.transform(new Vector4f(0.0F, 64.0F, 0.0F, 1.0F));
        Vector4f roof = matrix.transform(new Vector4f(0.0F, 80.0F, 0.0F, 1.0F));

        assertTrue(roof.y < ground.y, "higher blocks should move up the screen, roof=" + roof.y + " ground=" + ground.y);
        assertTrue(Math.abs(roof.x - ground.x) < 0.01F);
    }

    @Test
    void modelMatrix_keepsBlockDepthSeparateFromHeight() {
        Matrix4f matrix = SimuMap3DCamera.modelMatrix(400.0F, 300.0F, 0.0F, 2.0F, 64.0F);
        Vector4f ground = matrix.transform(new Vector4f(0.0F, 64.0F, 0.0F, 1.0F));
        Vector4f north = matrix.transform(new Vector4f(0.0F, 64.0F, -8.0F, 1.0F));
        Vector4f south = matrix.transform(new Vector4f(0.0F, 64.0F, 8.0F, 1.0F));
        Vector4f roof = matrix.transform(new Vector4f(0.0F, 80.0F, 0.0F, 1.0F));
        Vector4f east = matrix.transform(new Vector4f(8.0F, 64.0F, 0.0F, 1.0F));

        assertTrue(south.y > north.y, "south (+Z) should sit lower on the screen than north");
        assertTrue(east.x > ground.x, "east (+X) should sit to the right");
        assertTrue(south.z > north.z, "south should sit closer than north when looking from above the south edge");
        assertTrue(roof.z > ground.z, "roofs must sit closer to the camera than the ground below them, roof.z="
                + roof.z + " ground.z=" + ground.z);
        assertTrue(roof.z > south.z, "a roof must sit closer than the near ground, roof.z="
                + roof.z + " south.z=" + south.z);
    }

    @Test
    void pan_movesAWorldPointWithTheCursor() {
        float yaw = 0.8F;
        float scale = 2.0F;
        float mouseX = 20.0F;
        float mouseY = 16.0F;
        float[] delta = new float[2];
        SimuMap3DCamera.pan(mouseX, mouseY, yaw, scale, delta);

        Matrix4f matrix = SimuMap3DCamera.modelMatrix(0.0F, 0.0F, yaw, scale, 64.0F);
        Vector4f origin = matrix.transform(new Vector4f(0.0F, 64.0F, 0.0F, 1.0F));
        Vector4f moved = matrix.transform(new Vector4f(delta[0], 64.0F, delta[1], 1.0F));

        assertTrue(Math.abs((moved.x - origin.x) - mouseX) < 1.5F,
                "pan x should follow the cursor, got " + (moved.x - origin.x));
        assertTrue(Math.abs((moved.y - origin.y) - mouseY) < 1.5F,
                "pan y should follow the cursor, got " + (moved.y - origin.y));
    }

    @Test
    void pan_movesTheMapWithTheCursor() {
        float[] delta = new float[2];
        SimuMap3DCamera.pan(20.0F, 0.0F, 0.0F, 2.0F, delta);
        assertTrue(delta[0] > 0.0F);
        SimuMap3DCamera.pan(0.0F, 20.0F, 0.0F, 2.0F, delta);
        assertTrue(delta[1] > 0.0F);
    }
}
