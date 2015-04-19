/*
 * Copyright (C) 2011,2012 Samuel Audet
 *
 * This file is part of JavaCV.
 *
 * JavaCV is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version (subject to the "Classpath" exception
 * as provided in the LICENSE.txt file that accompanied this code).
 *
 * JavaCV is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JavaCV.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.bytedeco.javacv;

import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.CLBuffer;
import com.jogamp.opencl.CLContext;
import com.jogamp.opencl.CLDevice;
import com.jogamp.opencl.CLEventList;
import com.jogamp.opencl.CLImage2d;
import com.jogamp.opencl.CLImageFormat;
import com.jogamp.opencl.CLImageFormat.ChannelOrder;
import com.jogamp.opencl.CLImageFormat.ChannelType;
import com.jogamp.opencl.CLKernel;
import com.jogamp.opencl.CLMemory;
import com.jogamp.opencl.CLObject;
import com.jogamp.opencl.CLPlatform;
import com.jogamp.opencl.CLProgram;
import com.jogamp.opencl.CLProgram.CompilerOptions;
import com.jogamp.opencl.gl.CLGLContext;
import com.jogamp.opencl.gl.CLGLImage2d;
import com.jogamp.opencl.gl.CLGLObject;
import java.io.InputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.nio.ByteBuffer;
import java.util.Vector;
import java.util.logging.Logger;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLCapabilitiesImmutable;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLException;
import javax.media.opengl.GLPbuffer;
import javax.media.opengl.GLProfile;
import javax.media.opengl.glu.GLU;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.BytePointer;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

/**
 *
 * @author Samuel Audet
 *
 * To make NVIDIA drivers happy, we may need to limit the maximum
 * heap memory size of Java to 1 GB, by defining something like
 *      export _JAVA_OPTIONS=-Xmx1G
 *
 */
public class JavaCVCL {
    public JavaCVCL(CLContext context) {
        this(context, context.getDevices()[0]);
    }
    public JavaCVCL(CLContext context, CLDevice device) {
        this.pbuffer = null;
        this.context = context;
        this.glu = context instanceof CLGLContext ? new GLU() : null;
        this.commandQueue = device.createCommandQueue(/*Mode.PROFILING_MODE*/);
        CLKernel[] kernels = buildKernels(fastCompilerOptions, "JavaCV.cl", "pyrDown", "remap", "remapBayer");
        this.pyrDownKernel    = kernels[0];
        this.remapKernel      = kernels[1];
        this.remapBayerKernel = kernels[2];
    }

    public static GLCapabilities getDefaultGLCapabilities(GLProfile profile) {
        GLCapabilities caps = new GLCapabilities(profile != null ? profile : GLProfile.getDefault());
        // Without line below, there is an error on Windows.
        caps.setDoubleBuffered(false);
        return caps;
    }

    public JavaCVCL() {
        this(false);
    }
    public JavaCVCL(boolean createPbuffer) {
        this(createPbuffer ? getDefaultGLCapabilities(null) : null, null, null);
    }
    public JavaCVCL(GLContext shareWith) {
        this(getDefaultGLCapabilities(shareWith == null ? null :
            shareWith.getGLDrawable().getGLProfile()), shareWith, null);
    }
    public JavaCVCL(GLCapabilitiesImmutable caps, GLContext shareWith, CLDevice device) {
        GLPbuffer pbuffer = null;
        if (caps != null) {
            GLDrawableFactory factory = GLDrawableFactory.getFactory(caps.getGLProfile());
            if (factory.canCreateGLPbuffer(null, caps.getGLProfile())) {
                try {
                    // makes a new buffer
                    pbuffer = factory.createGLPbuffer(null, caps, null, 32, 32, shareWith);
                    // required for drawing to the buffer
                    pbuffer.createContext(shareWith).makeCurrent();
                } catch (GLException e) {
                    logger.warning("Could not create PBuffer: " + e);
                }
            } else {
                logger.warning("OpenGL implementation does not support PBuffers.");
            }
        }
        this.pbuffer = pbuffer;

        GLContext glContext = GLContext.getCurrent();
        if (device == null && glContext != null) {
            // woohoo! we have a GLContext

            // find gl compatible device
            CLDevice[] devices = CLPlatform.getDefault().listCLDevices();
            for (CLDevice d : devices) {
                if(d.isGLMemorySharingSupported()) {
                    device = d;
                    break;
                }
            }
//            if(null==device) {
//                throw new RuntimeException("couldn't find any CL/GL memory sharing devices ..");
//            }
        }
        if (glContext != null && device != null) {
            // create OpenCL context before creating any OpenGL objects
            // you want to share with OpenCL (AMD driver requirement)
            context = CLGLContext.create(glContext, device);
            glu = GLU.createGLU();
        } else if (device != null) {
            context = CLContext.create(device);
            glu = null;
        } else {
            // find a CL implementation
            //CLPlatform platform = CLPlatform.getDefault(/*type(CPU)*/);
            context = CLContext.create(/*platform.getMaxFlopsDevice()*/);
            device = context.getDevices()[0];
            glu = null;
        }

        // creade a command queue with benchmarking flag set
        commandQueue = device.createCommandQueue(/*Mode.PROFILING_MODE*/);

        CLKernel[] kernels = buildKernels(fastCompilerOptions, "JavaCV.cl", "pyrDown", "remap", "remapBayer");
        this.pyrDownKernel    = kernels[0];
        this.remapKernel      = kernels[1];
        this.remapBayerKernel = kernels[2];
    }

    public void release() {
        if (!context.isReleased()) {
            context.release();
            if (pbuffer != null) {
                pbuffer.getContext().makeCurrent();
                pbuffer.getContext().release();
                pbuffer.getContext().destroy();
                pbuffer.destroy();
            }
        }
    }
    @Override protected void finalize() throws Throwable {
        super.finalize();
        release();
    }

    public static final String fastCompilerOptions = // "-cl-nv-verbose " +
            CompilerOptions.FAST_RELAXED_MATH + " " + CompilerOptions.ENABLE_MAD;

    private static final Logger logger = Logger.getLogger(JavaCVCL.class.getName());

    private final GLPbuffer pbuffer;
    private final CLContext context;
    private final CLCommandQueue commandQueue;
    private final GLU glu;
    private final CLKernel pyrDownKernel, remapKernel, remapBayerKernel;

    public CLContext getCLContext() {
        return context;
    }

    public CLCommandQueue getCLCommandQueue() {
        return commandQueue;
    }

    public CLGLContext getCLGLContext() {
        return context instanceof CLGLContext ? (CLGLContext)context : null;
    }

    public GLContext getGLContext() {
        return context instanceof CLGLContext ? ((CLGLContext)context).getGLContext() : null;
    }

    public GL getGL() {
        GLContext glContext = getGLContext();
        return glContext != null ? glContext.getGL() : null;
    }

    public GL2 getGL2() {
        GL gl = getGL();
        return gl != null ? gl.getGL2() : null;
    }

    public GLU getGLU() {
        return glu;
    }

    public CLKernel buildKernel(String resourceNames, String kernelName) {
        return buildKernels(fastCompilerOptions, Loader.getCallerClass(2), resourceNames, kernelName)[0];
    }
    public CLKernel buildKernel(String compilerOptions, String resourceNames, String kernelName) {
        return buildKernels(compilerOptions, Loader.getCallerClass(2), resourceNames, kernelName)[0];
    }

    public CLKernel[] buildKernels(String compilerOptions, String resourceNames, String ... kernelNames) {
        return buildKernels(compilerOptions, Loader.getCallerClass(2), resourceNames, kernelNames);
    }
    public CLKernel[] buildKernels(String compilerOptions, Class resourceClass, String resourceNames, String ... kernelNames) {
        try {
            //load and compile program for the chosen device
            InputStream s;
            String[] a = resourceNames.split(":");
            if (a.length == 1) {
                s = resourceClass.getResourceAsStream(a[0]);
            } else {
                Vector<InputStream> vs = new Vector<InputStream>(a.length);
                for (String name : a) {
                    vs.addElement(resourceClass.getResourceAsStream(name));
                }
                s = new SequenceInputStream(vs.elements());
            }
            CLProgram program = context.createProgram(s);
//System.out.println("Building " + resourceNames + "...");
            program.build(compilerOptions);
//System.out.println(program.getBuildLog());
            assert program.isExecutable();

            // create kernel and set function parameters
            CLKernel[] kernels = new CLKernel[kernelNames.length];
            for (int i = 0; i < kernelNames.length; i++) {
                kernels[i] = program.createCLKernel(kernelNames[i]);
            }
            return kernels;
        } catch(IOException ex) {
            throw (Error)new LinkageError(ex.toString()).initCause(ex);
        }
    }

    public CLImage2d createCLImageFrom(IplImage image, CLImage2d.Mem ... flags) {
        int width = image.width();
        int height = image.height();
        int pitch = image.widthStep();
        ByteBuffer buffer = image.getByteBuffer();
        ChannelOrder order = null;
        ChannelType type = null;
        int size = 0;
        switch (image.depth()) {
            case IPL_DEPTH_8S:  type = ChannelType.SNORM_INT8;   size = 1; break;
            case IPL_DEPTH_8U:  type = ChannelType.UNORM_INT8;   size = 1; break;
            case IPL_DEPTH_16S: type = ChannelType.SNORM_INT16;  size = 2; break;
            case IPL_DEPTH_16U: type = ChannelType.UNORM_INT16;  size = 2; break;
            case IPL_DEPTH_32S: type = ChannelType.SIGNED_INT32; size = 4; break;
            case IPL_DEPTH_32F: type = ChannelType.FLOAT;        size = 4; break;
            default: assert false;
        }
        switch (image.nChannels()) {
            case 1: order = ChannelOrder.LUMINANCE;       break;
            case 2: order = ChannelOrder.RG;   size *= 2; break;
            case 3: order = ChannelOrder.RGB;  size *= 3; break;
            case 4: order = ChannelOrder.RGBA; size *= 4; break;
            default: assert false;
        }
        // NVIDIA drivers do not like it when width != pitch/size
        if (width != pitch/size) {
            width = pitch/size;
        }
        CLImageFormat format = new CLImageFormat(order, type);
        return context.createImage2d(buffer, width, height, /*pitch,*/ format, flags);
    }

    public CLGLImage2d createCLGLImageFrom(IplImage image, CLImage2d.Mem ... flags) {
        GL2 gl = getGL2();
        if (gl == null) {
            return null;
        }

        int width = image.width();
        int height = image.height();
        int pitch = image.widthStep();
        //ByteBuffer buffer = image.getByteBuffer();
        int format = 0;
        int size = 0;
        switch (image.nChannels()) {
            case 1:
                switch (image.depth()) {
                    case IPL_DEPTH_8S:  format = GL2.GL_LUMINANCE8_SNORM;  size = 1; break;
                    case IPL_DEPTH_8U:  format = GL2.GL_LUMINANCE8;        size = 1; break;
                    case IPL_DEPTH_16S: format = GL2.GL_LUMINANCE16_SNORM; size = 2; break;
                    case IPL_DEPTH_16U: format = GL2.GL_LUMINANCE16;       size = 2; break;
                    case IPL_DEPTH_32S: format = GL2.GL_LUMINANCE32I;      size = 4; break;
                    case IPL_DEPTH_32F: format = GL2.GL_LUMINANCE32F;      size = 4; break;
                    default: assert false;
                }
                break;
            case 2:
                switch (image.depth()) {
                    case IPL_DEPTH_8S:  format = GL2.GL_RG8_SNORM;  size = 2; break;
                    case IPL_DEPTH_8U:  format = GL2.GL_RG8;        size = 2; break;
                    case IPL_DEPTH_16S: format = GL2.GL_RG16_SNORM; size = 4; break;
                    case IPL_DEPTH_16U: format = GL2.GL_RG16;       size = 4; break;
                    case IPL_DEPTH_32S: format = GL2.GL_RG32I;      size = 8; break;
                    case IPL_DEPTH_32F: format = GL2.GL_RG32F;      size = 8; break;
                    default: assert false;
                }
                break;
            case 3:
                switch (image.depth()) {
                    case IPL_DEPTH_8S:  format = GL2.GL_RGB8_SNORM;  size = 3; break;
                    case IPL_DEPTH_8U:  format = GL2.GL_RGB8;        size = 3; break;
                    case IPL_DEPTH_16S: format = GL2.GL_RGB16_SNORM; size = 6; break;
                    case IPL_DEPTH_16U: format = GL2.GL_RGB16;       size = 6; break;
                    case IPL_DEPTH_32S: format = GL2.GL_RGB32I;      size = 12; break;
                    case IPL_DEPTH_32F: format = GL2.GL_RGB32F;      size = 12; break;
                    default: assert false;
                }
                break;
            case 4:
                switch (image.depth()) {
                    case IPL_DEPTH_8S:  format = GL2.GL_RGBA8_SNORM;  size = 4; break;
                    case IPL_DEPTH_8U:  format = GL2.GL_RGBA8;        size = 4; break;
                    case IPL_DEPTH_16S: format = GL2.GL_RGBA16_SNORM; size = 8; break;
                    case IPL_DEPTH_16U: format = GL2.GL_RGBA16;       size = 8; break;
                    case IPL_DEPTH_32S: format = GL2.GL_RGBA32I;      size = 16; break;
                    case IPL_DEPTH_32F: format = GL2.GL_RGBA32F;      size = 16; break;
                    default: assert false;
                }
                break;
            default: assert false;
        }
        // NVIDIA drivers do not like it when width != pitch/size
        if (width != pitch/size) {
            width = pitch/size;
        }
        int[] renderBuffer = new int[1];
        gl.glGenRenderbuffers(1, renderBuffer, 0);
        gl.glBindRenderbuffer(GL2.GL_RENDERBUFFER, renderBuffer[0]);
        gl.glRenderbufferStorage(GL2.GL_RENDERBUFFER, format, width, height);
        return getCLGLContext().createFromGLRenderbuffer(renderBuffer[0], flags);
    }

    public void releaseCLGLImage(CLGLImage2d image) {
        image.release();
        getGL2().glDeleteRenderbuffers(1, new int[] { image.getGLObjectID() }, 0);
    }

    @SuppressWarnings("unchecked")
    public CLBuffer createPinnedBuffer(int size) {
        // as per NVIDIA's OpenCL Best Practices Guide
        CLBuffer pinnedBuffer = context.createBuffer(size, CLMemory.Mem.ALLOCATE_BUFFER);
        ByteBuffer byteBuffer = commandQueue.putMapBuffer(pinnedBuffer, CLMemory.Map.READ_WRITE, true);
        pinnedBuffer.use(byteBuffer);
        return pinnedBuffer;
    }

    class PinnedIplImage extends IplImage {
        PinnedIplImage(int width, int height, int depth, int channels) {
            super(cvCreateImageHeader(new CvSize().width(width).height(height), depth, channels));
            pinnedBuffer = createPinnedBuffer(imageSize());
            imageData(new BytePointer(getByteBuffer()));
        }

        final CLBuffer pinnedBuffer;

        public CLBuffer getCLBuffer() {
            return pinnedBuffer;
        }

        @Override public ByteBuffer getByteBuffer() {
            return (ByteBuffer)pinnedBuffer.getBuffer();
        }

        @Override public void release() {
            commandQueue.putUnmapMemory(pinnedBuffer, getByteBuffer());
            pinnedBuffer.release();
            cvReleaseImageHeader(this);
        }
    }

    public IplImage createPinnedIplImage(int width, int height, int depth, int channels) {
        return new PinnedIplImage(width, height, depth, channels);
    }

    public IplImage createIplImageFrom(CLImage2d image) {
        int width = image.width;
        int height = image.height;
        CLImageFormat format = image.getFormat();
        ChannelOrder order = format.getImageChannelOrder();
        ChannelType type = format.getImageChannelDataType();
        int depth = 0, channels = 0;
        switch (order) {
            case R:
            case A:
            case INTENSITY:
            case LUMINANCE:
                channels = 1;
                break;
            case Rx:
            case RG:
            case RA:
                channels = 2;
                break;
            case RGx:
            case RGB:
                channels = 3;
                break;
            case RGBx:
            case RGBA:
            case ARGB:
            case BGRA:
                channels = 4;
                break;
            default: assert false;
        }
        switch (type) {
            case SIGNED_INT8:
            case SNORM_INT8:    depth = IPL_DEPTH_8S;  break;
            case UNSIGNED_INT8:
            case UNORM_INT8:    depth = IPL_DEPTH_8U;  break;
            case SIGNED_INT16:
            case SNORM_INT16:   depth = IPL_DEPTH_16S; break;
            case UNSIGNED_INT16:
            case UNORM_INT16:   depth = IPL_DEPTH_16U; break;
            case UNSIGNED_INT32:
            case SIGNED_INT32:  depth = IPL_DEPTH_32S; break;
            case FLOAT:         depth = IPL_DEPTH_32F; break;
            case HALF_FLOAT:
            case UNORM_SHORT_565:
            case UNORM_SHORT_555:
            case UNORM_INT_101010:
            default: assert false;
        }
        return IplImage.create(width, height, depth, channels);
        //return createPinnedIplImage(width, height, depth, channels);
    }

    @SuppressWarnings("unchecked")
    public IplImage readImage(CLImage2d clImg, IplImage iplImage, boolean blocking) {
        if (iplImage == null) {
            iplImage = createIplImageFrom(clImg);
        }
        int x = 0, y = 0;
        int width = clImg.width;
        int height = clImg.height;
        int pitch = iplImage.widthStep();
        ByteBuffer buffer = iplImage.getByteBuffer();
        IplROI roi = iplImage.roi();
        if (roi != null) {
            x = roi.xOffset();
            y = roi.yOffset();
            width = roi.width();
            height = roi.height();
            int pixelSize = iplImage.nChannels()*((iplImage.depth()&~IPL_DEPTH_SIGN)/8);
            buffer = iplImage.getByteBuffer(y*pitch + x*pixelSize);
        }
        clImg.use(buffer);
        commandQueue.putReadImage(clImg, pitch, x, y, width, height, blocking);
        return iplImage;
    }

    @SuppressWarnings("unchecked")
    public CLImage2d writeImage(CLImage2d clImg, IplImage iplImage, boolean blocking) {
        if (clImg == null) {
            clImg = createCLImageFrom(iplImage);
        }
        int x = 0, y = 0;
        int width = iplImage.width();
        int height = iplImage.height();
        int pitch = iplImage.widthStep();
        ByteBuffer buffer = iplImage.getByteBuffer();
        IplROI roi = iplImage.roi();
        if (roi != null) {
            x = roi.xOffset();
            y = roi.yOffset();
            width = roi.width();
            height = roi.height();
            int pixelSize = iplImage.nChannels()*((iplImage.depth()&~IPL_DEPTH_SIGN)/8);
            buffer = iplImage.getByteBuffer(y*pitch + x*pixelSize);
        }
        clImg.use(buffer);
        commandQueue.putWriteImage(clImg, pitch, x, y, width, height, blocking);
        return clImg;
    }

    public void acquireGLObject(CLObject object) {
        if (object instanceof CLGLObject) {
            commandQueue.putAcquireGLObject((CLGLObject)object);
        }
    }
    public void releaseGLObject(CLObject object) {
        if (object instanceof CLGLObject) {
            commandQueue.putReleaseGLObject((CLGLObject)object);
        }
    }

    public void readBuffer(CLBuffer<?> buffer, boolean blocking) {
        commandQueue.putReadBuffer(buffer, blocking);
    }
    public void writeBuffer(CLBuffer<?> buffer, boolean blocking) {
        commandQueue.putWriteBuffer(buffer, blocking);
    }

    public void executeKernel(CLKernel kernel,
            long globalWorkOffsetX, long globalWorkSizeX, long localWorkSizeX) {
        commandQueue.put1DRangeKernel(kernel,
                globalWorkOffsetX, globalWorkSizeX, localWorkSizeX);
    }
    public void executeKernel(CLKernel kernel,
            long globalWorkOffsetX, long globalWorkSizeX, long localWorkSizeX, CLEventList events) {
        commandQueue.put1DRangeKernel(kernel,
                globalWorkOffsetX, globalWorkSizeX, localWorkSizeX, events);
    }
    public void executeKernel(CLKernel kernel,
            long globalWorkOffsetX, long globalWorkSizeX, long localWorkSizeX,
            CLEventList condition, CLEventList events) {
        commandQueue.put1DRangeKernel(kernel,
                globalWorkOffsetX, globalWorkSizeX, localWorkSizeX, condition, events);
    }

    public void executeKernel(CLKernel kernel,
            long globalWorkOffsetX, long globalWorkOffsetY,
            long globalWorkSizeX, long globalWorkSizeY,
            long localWorkSizeX, long localWorkSizeY) {
        commandQueue.put2DRangeKernel(kernel,
                globalWorkOffsetX, globalWorkOffsetY,
                globalWorkSizeX, globalWorkSizeY,
                localWorkSizeX, localWorkSizeY);
    }
    public void executeKernel(CLKernel kernel,
            long globalWorkOffsetX, long globalWorkOffsetY,
            long globalWorkSizeX, long globalWorkSizeY,
            long localWorkSizeX, long localWorkSizeY, CLEventList events) {
        commandQueue.put2DRangeKernel(kernel,
                globalWorkOffsetX, globalWorkOffsetY,
                globalWorkSizeX, globalWorkSizeY,
                localWorkSizeX, localWorkSizeY, events);
    }
    public void executeKernel(CLKernel kernel,
            long globalWorkOffsetX, long globalWorkOffsetY,
            long globalWorkSizeX, long globalWorkSizeY,
            long localWorkSizeX, long localWorkSizeY,
            CLEventList condition, CLEventList events) {
        commandQueue.put2DRangeKernel(kernel,
                globalWorkOffsetX, globalWorkOffsetY,
                globalWorkSizeX, globalWorkSizeY,
                localWorkSizeX, localWorkSizeY, condition, events);
    }

    public void executeKernel(CLKernel kernel,
            long globalWorkOffsetX, long globalWorkOffsetY, long globalWorkOffsetZ,
            long globalWorkSizeX, long globalWorkSizeY, long globalWorkSizeZ,
            long localWorkSizeX, long localWorkSizeY, long localWorkSizeZ) {
        commandQueue.put3DRangeKernel(kernel,
                globalWorkOffsetX, globalWorkOffsetY, globalWorkOffsetZ,
                globalWorkSizeX, globalWorkSizeY, globalWorkSizeZ,
                localWorkSizeX, localWorkSizeY, localWorkSizeZ);
    }
    public void executeKernel(CLKernel kernel,
            long globalWorkOffsetX, long globalWorkOffsetY, long globalWorkOffsetZ,
            long globalWorkSizeX, long globalWorkSizeY, long globalWorkSizeZ,
            long localWorkSizeX, long localWorkSizeY, long localWorkSizeZ, CLEventList events) {
        commandQueue.put3DRangeKernel(kernel,
                globalWorkOffsetX, globalWorkOffsetY, globalWorkOffsetZ,
                globalWorkSizeX, globalWorkSizeY, globalWorkSizeZ,
                localWorkSizeX, localWorkSizeY, localWorkSizeZ, events);
    }
    public void executeKernel(CLKernel kernel,
            long globalWorkOffsetX, long globalWorkOffsetY, long globalWorkOffsetZ,
            long globalWorkSizeX, long globalWorkSizeY, long globalWorkSizeZ,
            long localWorkSizeX, long localWorkSizeY, long localWorkSizeZ,
            CLEventList condition, CLEventList events) {
        commandQueue.put3DRangeKernel(kernel,
                globalWorkOffsetX, globalWorkOffsetY, globalWorkOffsetZ,
                globalWorkSizeX, globalWorkSizeY, globalWorkSizeZ,
                localWorkSizeX, localWorkSizeY, localWorkSizeZ, condition, events);
    }

    public void finish() {
        commandQueue.finish();
    }
    public void flush() {
        commandQueue.flush();
    }

    public static int alignCeil(int x, int n) {
        return (x + n-1)/n*n;
    }
    public static int alignFloor(int x, int n) {
        return x/n*n;
    }

    public void pyrDown(CLImage2d srcImg, CLImage2d dstImg) {
        CLEventList list = null;//new CLEventList(1);

        pyrDownKernel.putArg(srcImg).putArg(dstImg).rewind();
        executeKernel(pyrDownKernel, 0, 0, alignCeil(dstImg.width, 2), alignCeil(dstImg.height, 64), 2, 64, list); // execute program

//        finish();
//        CLEvent event = list.getEvent(0);
//        System.out.println("pyrDown: " + (event.getProfilingInfo(CLEvent.ProfilingCommand.END) -
//                                          event.getProfilingInfo(CLEvent.ProfilingCommand.START))/1000000.0);
    }

    public void remap(CLImage2d srcImg, CLImage2d dstImg, CLImage2d mapxImg, CLImage2d mapyImg) {
        remap(srcImg, dstImg, mapxImg, mapyImg, -1L);
    }
    public void remap(CLImage2d srcImg, CLImage2d dstImg, CLImage2d mapxImg, CLImage2d mapyImg, long sensorPattern) {
        CLEventList list = null;//new CLEventList(1);

        CLKernel kernel;
        if (sensorPattern != -1L) {
            kernel = remapBayerKernel.putArg(srcImg).putArg(dstImg).putArg(mapxImg).putArg(mapyImg).putArg(sensorPattern).rewind();
        } else {
            kernel = remapKernel.putArg(srcImg).putArg(dstImg).putArg(mapxImg).putArg(mapyImg).rewind();
        }
        executeKernel(kernel, 0, 0, alignCeil(dstImg.width, 2), alignCeil(dstImg.height, 64), 2, 64, list); // execute program

//        finish();
//        CLEvent event = list.getEvent(0);
//        System.out.println("remap: " + (event.getProfilingInfo(CLEvent.ProfilingCommand.END) -
//                                        event.getProfilingInfo(CLEvent.ProfilingCommand.START))/1000000.0);
    }

    public static void main(String[] args) {
        JavaCVCL context = new JavaCVCL();

        CLImageFormat[] formats = context.getCLContext().getSupportedImage2dFormats();
        for (CLImageFormat f : formats) {
            System.out.println(f);
        }

        CameraDevice camera = new CameraDevice("Camera");
        camera.imageWidth = 1280;
        camera.imageHeight = 960;
        camera.cameraMatrix = CvMat.create(3, 3);
        double f = camera.imageWidth*2.5;
        camera.cameraMatrix.put(f,   0.0, camera.imageWidth /2,
                                0.0, f,   camera.imageHeight/2,
                                0.0, 0.0,    1);
        camera.R = CvMat.create(3, 3);
        cvSetIdentity(camera.R);
        camera.T = CvMat.create(3, 1);
        cvSetZero(camera.T);
        camera.distortionCoeffs = CvMat.create(1, 4);
        cvSetZero(camera.distortionCoeffs);
        camera.distortionCoeffs.put(0.2);
        camera.colorMixingMatrix = CvMat.create(3, 3);
        cvSetIdentity(camera.colorMixingMatrix);

        IplImage srcImg = cvLoadImageRGBA(args[0]);
        //IplImage dstImg = srcImg.clone();
        IplImage downDst = IplImage.create(srcImg.width()/2, srcImg.height()/2, IPL_DEPTH_8U /*IPL_DEPTH_32F*/, 4);
        camera.setFixedPointMaps(false);
        camera.setMapsPyramidLevel(1);
        IplImage mapxImg = camera.getUndistortMap1();
        IplImage mapyImg = camera.getUndistortMap2();
        long start = System.nanoTime();
        cvRemap(srcImg, downDst, mapxImg, mapyImg, CV_INTER_LINEAR | CV_WARP_FILL_OUTLIERS, CvScalar.ZERO);
        System.out.println("cvRemap: " + (System.nanoTime()-start)/1000000.0);
        cvSaveImage("/tmp/opencv.png", downDst);

        CLImage2d src = context.createCLImageFrom(srcImg);
//        CLImage2d dst = context.createCLImageFrom(dstImg);
        CLImage2d dst = context.createCLImageFrom(downDst);

        CLImage2d mapx = context.createCLImageFrom(mapxImg);
        CLImage2d mapy = context.createCLImageFrom(mapyImg);
        context.writeImage(src, srcImg, false);
        context.writeImage(mapx, mapxImg, false);
        context.writeImage(mapy, mapyImg, false);

        //context.pyrDown(src, dst);
        context.remap(src, dst, mapx, mapy);
        context.readImage(dst, downDst, true);
        //cvConvertScale(downDst, downDst, 255, 0);
        cvSaveImage("/tmp/javacvcl.png", downDst);

        context.release();
        System.exit(0);
    }
}
