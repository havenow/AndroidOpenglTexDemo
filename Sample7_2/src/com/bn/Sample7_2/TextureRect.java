package com.bn.Sample7_2;
import static com.bn.Sample7_2.ShaderUtil.createProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES20;
import android.opengl.Matrix;

//�������
public class TextureRect 
{	
	int mProgram;//�Զ�����Ⱦ���߳���id
    int muMVPMatrixHandle;//�ܱ任��������id
    int maPositionHandle; //����λ����������id  
    int maTexCoorHandle; //��������������������id  
    String mVertexShader;//������ɫ��    	 
    String mFragmentShader;//ƬԪ��ɫ��
    static float[] mMMatrix = new float[16];//����������ƶ���ת����
	
	FloatBuffer   mVertexBuffer;//�����������ݻ���
	FloatBuffer   mTexCoorBuffer;//���������������ݻ���
    int vCount=0;   

    float mRatio = 1.0f;
    float mCordZ = 0.0f;
    
    public TextureRect(MySurfaceView mv)
    {    	
    	//��ʼ��������������ɫ����
    	initVertexData();
    	//��ʼ��shader        
    	initShader(mv);
    }
    
    //��ʼ��������������ɫ���ݵķ���
    public void initVertexData()
    {
    	//�����������ݵĳ�ʼ��================begin============================
        vCount=6;
        final float UNIT_SIZE=0.3f;
//        float vertices[]=new float[]
//        {
//        	-4*UNIT_SIZE,4*UNIT_SIZE,0,
//        	-4*UNIT_SIZE,-4*UNIT_SIZE,0,
//        	4*UNIT_SIZE,-4*UNIT_SIZE,0,
//        	
//        	4*UNIT_SIZE,-4*UNIT_SIZE,0,
//        	4*UNIT_SIZE,4*UNIT_SIZE,0,
//        	-4*UNIT_SIZE,4*UNIT_SIZE,0
//        };
        //float ratio = 0.5625f;
        //float z = 1.999999f;
        float vertices[]=new float[]
        {
        	-mRatio,1.0f,mCordZ,
         	-mRatio,-1.0f,mCordZ,
         	mRatio,-1.0f,mCordZ,
                	
         	mRatio,-1.0f,mCordZ,
        	mRatio,1.0f,mCordZ,
        	-mRatio,1.0f,mCordZ
        };
		
        //���������������ݻ���
        //vertices.length*4����Ϊһ�������ĸ��ֽ�
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mVertexBuffer = vbb.asFloatBuffer();//ת��ΪFloat�ͻ���
        mVertexBuffer.put(vertices);//�򻺳����з��붥����������
        mVertexBuffer.position(0);//���û�������ʼλ��
        //�ر���ʾ�����ڲ�ͬƽ̨�ֽ�˳��ͬ���ݵ�Ԫ�����ֽڵ�һ��Ҫ����ByteBuffer
        //ת�����ؼ���Ҫͨ��ByteOrder����nativeOrder()�������п��ܻ������
        //�����������ݵĳ�ʼ��================end============================
        
        //���������������ݵĳ�ʼ��================begin============================
//        float texCoor[]=new float[]//������ɫֵ���飬ÿ������4��ɫ��ֵRGBA
//  	    {
//  	      		0,0, 0,tRange, sRange,tRange,
//  	      		sRange,tRange, sRange,0, 0,0        		
//  	    };  
        float texCoor[]=new float[]//������ɫֵ���飬ÿ������4��ɫ��ֵRGBA
          	    {
          	      		0,0, 0,1, 1,1,
          	      		1,1, 1,0, 0,0        		
          	    };  
        //�������������������ݻ���
        ByteBuffer cbb = ByteBuffer.allocateDirect(texCoor.length*4);
        cbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mTexCoorBuffer = cbb.asFloatBuffer();//ת��ΪFloat�ͻ���
        mTexCoorBuffer.put(texCoor);//�򻺳����з��붥����ɫ����
        mTexCoorBuffer.position(0);//���û�������ʼλ��
        //�ر���ʾ�����ڲ�ͬƽ̨�ֽ�˳��ͬ���ݵ�Ԫ�����ֽڵ�һ��Ҫ����ByteBuffer
        //ת�����ؼ���Ҫͨ��ByteOrder����nativeOrder()�������п��ܻ������
        //���������������ݵĳ�ʼ��================end============================

    }

    //��ʼ��shader
    public void initShader(MySurfaceView mv)
    {
    	//���ض�����ɫ���Ľű�����
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        //����ƬԪ��ɫ���Ľű�����
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());  
        //���ڶ�����ɫ����ƬԪ��ɫ����������
        mProgram = createProgram(mVertexShader, mFragmentShader);
        //��ȡ�����ж���λ����������id  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //��ȡ�����ж�������������������id  
        maTexCoorHandle= GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        //��ȡ�������ܱ任��������id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");  
    }
    
    public void drawSelf(int texId)
    {        
    	 //�ƶ�ʹ��ĳ��shader����
    	 GLES20.glUseProgram(mProgram);        
    	 //��ʼ���任����
    	 Matrix.setIdentityM(mMMatrix, 0);
         //Matrix.setRotateM(mMMatrix,0,0,0,1,0);
         //������Z������λ��1
         //Matrix.translateM(mMMatrix,0,0,0,1);
//         //������y����ת
//         Matrix.rotateM(mMMatrix,0,yAngle,0,1,0);
//         //������z����ת
//         Matrix.rotateM(mMMatrix,0,zAngle,0,0,1);  
//         //������x����ת
//         Matrix.rotateM(mMMatrix,0,xAngle,1,0,0);
         //�����ձ任������shader����
         GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(mMMatrix), 0); 
         //Ϊ����ָ������λ������
         GLES20.glVertexAttribPointer  
         (
         		maPositionHandle,   
         		3, 
         		GLES20.GL_FLOAT, 
         		false,
                3*4,   
                mVertexBuffer
         );       
         //Ϊ����ָ������������������
         GLES20.glVertexAttribPointer  
         (
        		maTexCoorHandle, 
         		2, 
         		GLES20.GL_FLOAT, 
         		false,
                2*4,   
                mTexCoorBuffer
         );   
         //������λ����������
         GLES20.glEnableVertexAttribArray(maPositionHandle);  
         GLES20.glEnableVertexAttribArray(maTexCoorHandle);  
         
         //������
         GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
         GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
         
         //�����������
         GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 
    }
    
    public void setRatio(float ratio, float cordZ)
    {
    	mRatio = ratio;
    	mCordZ = cordZ;
    }
}
