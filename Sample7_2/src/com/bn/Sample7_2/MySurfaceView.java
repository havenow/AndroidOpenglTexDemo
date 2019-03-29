package com.bn.Sample7_2;
import java.io.IOException;
import java.io.InputStream;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.MotionEvent;
import android.opengl.GLES20;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

class MySurfaceView extends GLSurfaceView 
{
    private SceneRenderer mRenderer;//场景渲染器
    
    int textureCTId;//系统分配的拉伸纹理id
    int textureREId;//系统分配的重复纹理id
    int currTextureId;//当前纹理id  
    
    TextureRect[] texRect=new TextureRect[3];//纹理矩形数组
    int trIndex=0;//当前纹理矩形索引
	
	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(2); //设置使用OPENGL ES2.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
    }

	private class SceneRenderer implements GLSurfaceView.Renderer 
    {      	
        public void onDrawFrame(GL10 gl) 
        { 
        	//清除深度缓冲与颜色缓冲
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
			
			/*if (romLoaded) {
            	Bitmap localBitmap = Bitmap.createBitmap(240, 160, Bitmap.Config.RGB_565);
	            EmulatorGL20View.e.position(0);
	            localBitmap.copyPixelsFromBuffer(EmulatorGL20View.e); 
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, localBitmap, 0);
                //GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, localBitmap);
                
                localBitmap.recycle();
			}*/
			
            //绘制当前纹理矩形
            texRect[trIndex].drawSelf(currTextureId);             
        }  

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置 
        	GLES20.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            float near = 1.0f;
            float far = 10.0f;
            float cameraZ = 3.0f;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProject(-ratio, ratio, -1, 1, near, far);
            //调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(0, 0, cameraZ, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            
            texRect[trIndex].setRatio(ratio, cameraZ - near - 0.000001f);//不减0.000001f，画面就不再平截头体内？？？
            texRect[trIndex].initVertexData();
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES20.glClearColor(1.0f,1.0f,1.0f, 1.0f);  
            //创建三个纹理矩形对对象 
            texRect[0]=new TextureRect(MySurfaceView.this);  
            texRect[1]=new TextureRect(MySurfaceView.this);  
            texRect[2]=new TextureRect(MySurfaceView.this);        
            //打开深度检测
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);           
            //初始化系统分配的拉伸纹理id
            textureCTId=initTexture(false);
            //初始化系统分配的重复纹理id
            textureREId=initTexture(true);
            //初始化当前纹理id
            currTextureId=textureREId;
            //关闭背面剪裁   
            GLES20.glDisable(GLES20.GL_CULL_FACE);
        }
    }
	
	//初始化纹理的方法
	public int initTexture(boolean isRepeat)//textureId
	{
		//生成纹理ID
		int[] textures = new int[1];  
		GLES20.glGenTextures
		(
				1,          //产生的纹理id的数量
				textures,   //纹理id的数组
				0           //偏移量 
		);    
		int textureId=textures[0];    
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
	
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
				
		
        if(isRepeat)
        {
        	GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, 
        			GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_REPEAT);
    		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, 
    				GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_REPEAT);
        }
        else
        {
        	GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, 
        			GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
    		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, 
    				GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
        }
        
        //通过输入流加载图片===============begin===================
        InputStream is = this.getResources().openRawResource(R.drawable.robot);
        Bitmap bitmapTmp;
        try 
        {
        	bitmapTmp = BitmapFactory.decodeStream(is);
        } 
        finally 
        {
            try 
            {
                is.close();
            } 
            catch(IOException e) 
            {
                e.printStackTrace();
            }
        }
        //通过输入流加载图片===============end=====================  
        
        //实际加载纹理
        GLUtils.texImage2D
        (
        		GLES20.GL_TEXTURE_2D,   //纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
        		0, 					  //纹理的层次，0表示基本图像层，可以理解为直接贴图
        		bitmapTmp, 			  //纹理图像
        		0					  //纹理边框尺寸
        );
        bitmapTmp.recycle(); 		  //纹理加载成功后释放图片
        return textureId;
	}
}
