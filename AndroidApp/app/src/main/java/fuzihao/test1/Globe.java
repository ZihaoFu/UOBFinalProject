package fuzihao.test1;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

public class Globe {
    private int divide = 40;
    private int radius = 3;

    private ArrayList<FloatBuffer> mVertices = new ArrayList<FloatBuffer>();
    private ArrayList<FloatBuffer> mTextureCoords = new ArrayList<FloatBuffer>();

    // 计算球面顶点坐标
    public Globe(){
        float altitude;
        float altitudeDelta;
        float azimuth;
        float ex;
        float ey;
        float ez;
        for(int i = 0; i <= divide; i++) {
            altitude      = (float) (Math.PI/2.0 -    i    * (Math.PI) / divide); //海拔
            altitudeDelta = (float) (Math.PI/2.0 - (i + 1) * (Math.PI) / divide);

            float[] vertices = new float[divide*6+6]; //顶点
            float[] texCoords = new float[divide*4+4];

            for(int j = 0; j <= divide; j++) {
                azimuth = (float)(j * (Math.PI*2) / divide);

                ex = (float) (Math.cos(altitude) * Math.cos(azimuth));
                ey = (float)  Math.sin(altitude);
                ez = (float) - (Math.cos(altitude) * Math.sin(azimuth));

                vertices[6*j+0] = radius * ex;
                vertices[6*j+1] = radius * ey;
                vertices[6*j+2] = radius * ez;

                texCoords[4*j+0] = j/(float)divide;
                texCoords[4*j+1] = i/(float)divide;

                ex = (float) (Math.cos(altitudeDelta) * Math.cos(azimuth));
                ey = (float) Math.sin(altitudeDelta);
                ez = (float) -(Math.cos(altitudeDelta) * Math.sin(azimuth));

                vertices[6*j+3] = radius * ex;
                vertices[6*j+4] = radius * ey;
                vertices[6*j+5] = radius * ez;

                texCoords[4*j+2] = j/(float)divide;
                texCoords[4*j+3] = (i + 1) / (float)divide;
            }

            mVertices.add(FileUtil.getFloatBuffer(vertices));
            mTextureCoords.add(FileUtil.getFloatBuffer(texCoords));
        }
    }

    public void drawGlobe(GL10 gl) {
        //启用纹理
        gl.glEnable(GL10.GL_TEXTURE_2D);
        //打开材质开关
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        //打开顶点开关
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        for(int i= 0;i<=divide;i++){
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertices.get(i));
            //声明纹理点坐标
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureCoords.get(i));
            //GL_LINE_STRIP只绘制线条，GL_TRIANGLE_STRIP才是画三角形的面
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, divide*2+2);
//            gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, divide*2+2);
        }
        //关闭顶点开关
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        //关闭材质开关
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisable(GL10.GL_TEXTURE_2D);
    }
}
