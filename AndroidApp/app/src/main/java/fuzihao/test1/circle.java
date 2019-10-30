package fuzihao.test1;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class circle {
    private Context context;
    private FloatBuffer vertexData;
    // 定义圆心坐标
    private float x;
    private float y;
    // 半径
    private float r;
    // 三角形分割的数量
    private int count = 40;
    // 每个顶点包含的数据个数 （ x 和 y ）
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int BYTES_PER_FLOAT = 4;

    private static final String U_COLOR = "u_Color";
    private static final String A_POSITION = "a_Position";
    private int program;
    private int uColorLocation;
    private int aPositionLocation;
    public circle() {
        x = 0f;
        y = 0f;
        r = 0.1f;
        initVertexData();
    }

    private void initVertexData() {
        // 顶点的个数，我们分割count个三角形，有count+1个点，再加上圆心共有count+2个点
        final int nodeCount = count + 2;
        float circleCoords[] = new float[nodeCount * POSITION_COMPONENT_COUNT];
        // x y
        int offset = 0;
        circleCoords[offset++] = x;// 中心点
        circleCoords[offset++] = y;
        for (int i = 0; i < count + 1; i++) {
            float angleInRadians = ((float) i / (float) count) * ((float) Math.PI * 2f);
//            float angleInRadians = ((float) i / (float) count)
//                    * ((float) Math.PI);
            circleCoords[offset++] = x + r * (float)Math.sin(angleInRadians);
            circleCoords[offset++] = y + r * (float)Math.cos(angleInRadians);
        }

        // 为存放形状的坐标，初始化顶点字节缓冲
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (坐标数 * 4)float占四字节
                circleCoords.length * BYTES_PER_FLOAT);
        // 设用设备的本点字节序
        bb.order(ByteOrder.nativeOrder());
        // 从ByteBuffer创建一个浮点缓冲
        vertexData = bb.asFloatBuffer();
        // 把坐标们加入FloatBuffer中
        vertexData.put(circleCoords);
        // 设置buffer，从第一个坐标开始读
        vertexData.position(0);
    }

    public void draw(GL10 gl) {

        // Counter-clockwise winding.
        gl.glFrontFace(GL10.GL_CCW);
        // Enable face culling.
        gl.glEnable(GL10.GL_CULL_FACE);
        // What faces to remove with the face culling.
        gl.glCullFace(GL10.GL_BACK);
        // Enabled the vertices buffer for writing
        //and to be used during
        // rendering.
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        // Specifies the location and data format of
        //an array of vertex
        // coordinates to use when rendering.
        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexData);

//        gl.glDrawArrays(GL10.GL_LINE_LOOP, 0,5);
        gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0,42);
        // Disable the vertices buffer.
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        // Disable face culling.
        gl.glDisable(GL10.GL_CULL_FACE);
    }

}
