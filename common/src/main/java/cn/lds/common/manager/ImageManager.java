package cn.lds.common.manager;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.util.List;

import cn.lds.common.constants.Constants;
import okhttp3.OkHttpClient;


/**
 * 图片管理中心
 * Created by leadingsoft on 2017/11/30.
 */

public class ImageManager {
    private static ImageManager mInstance;//单利引用
    private Context mContext;
    private int duration = 300;
    private final String cacheName = "image_cache";

    public static ImageManager getInstance() {
        ImageManager inst = mInstance;
        if (inst == null) {
            synchronized (ImageManager.class) {
                inst = mInstance;
                if (inst == null) {
                    inst = new ImageManager();
                    mInstance = inst;
                }
            }
        }
        return inst;
    }

    /**
     * 初始化 fresco，application 调用一次，不用多次调用
     */
    public void initFresco(Context mContext) {
        this.mContext = mContext;
        Fresco.initialize(mContext);//初始化 fresco
    }

    /**
     * 初始化 fresco，application 调用一次，不用多次调用
     */
    public void initFresco(Context mContext, OkHttpClient okHttpClient) {
        this.mContext = mContext;
        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(mContext)
                .setBaseDirectoryName(cacheName)
                .setBaseDirectoryPath(new File(Constants.SYS_CONFIG_FILE_PATH))
                .build();
        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                .newBuilder(mContext, okHttpClient)
                .setMainDiskCacheConfig(diskCacheConfig)
                .build();
        Fresco.initialize(mContext, config);//初始化 fresco
    }


    /**
     * 设置图片  start
     * <p>
     * 远程图片	http://, https://	HttpURLConnection 或者参考 使用其他网络加载方案
     * 本地文件	file://	FileInputStream
     * Content provider	content://	ContentResolver
     * asset目录下的资源	asset://	AssetManager
     * res目录下的资源	res://	Resources.openRawResource
     * Uri中指定图片数据	data:mime/type;base64,	数据类型必须符合 rfc2397规定 (仅支持 UTF-8)
     */
    public void loadImage(String url, SimpleDraweeView draweeView) {
        Uri uri = Uri.parse(url);
        draweeView.setImageURI(uri);
    }

    /**
     * 加载图片，渐进式显示
     *
     * @param url
     *         图片地址
     * @param draweeView
     *         view容器
     * @param renderingEnabled
     *         渐进显示开关
     */
    public void loadImage(String url, SimpleDraweeView draweeView, boolean renderingEnabled) {
        Uri uri = Uri.parse(url);
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setProgressiveRenderingEnabled(renderingEnabled)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(draweeView.getController())
                .build();
        draweeView.setController(controller);
        draweeView.setImageURI(uri);
    }

    /**
     * 加载图片，渐进式显示
     *
     * @param url
     *         图片地址
     * @param draweeView
     *         view容器
     * @param hierarchy
     *         基本参数设定
     */
    public void loadImage(String url, SimpleDraweeView draweeView, GenericDraweeHierarchy hierarchy) {
        draweeView.setHierarchy(hierarchy);
        loadImage(url, draweeView);
    }

    /**
     * 加载图片，渐进式显示
     *
     * @param url
     *         图片地址
     * @param draweeView
     *         view容器
     * @param placeholderId
     *         占位图资源id
     */
    public void loadImage(String url, SimpleDraweeView draweeView, int placeholderId) {
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(mContext.getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(duration)
                .setPlaceholderImage(placeholderId)//修改占位图为资源id:
                .build();
        loadImage(url, draweeView, hierarchy);
    }

    /**
     * 加载图片，渐进式显示
     *
     * @param url
     *         图片地址
     * @param draweeView
     *         view容器
     * @param placeholderId
     *         占位图资源id
     * @param overlaysList
     *         覆盖图片列表
     */
    public void loadImage(String url, SimpleDraweeView draweeView, int placeholderId, List<Drawable> overlaysList) {
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(mContext.getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(duration)
                .setPlaceholderImage(placeholderId)//修改占位图为资源id:
                .setOverlays(overlaysList)
                .build();
        loadImage(url, draweeView, hierarchy);
    }

    /**
     * 加载圆形状图片
     *
     * @param url
     *         图片地址
     * @param draweeView
     *         view容器
     * @param radius
     *         半径
     * @param borderWidth
     *         描边宽度
     */
    public void loadCircleImage(String url, SimpleDraweeView draweeView, float radius, int borderColor, float borderWidth) {

        RoundingParams roundingParams = RoundingParams.fromCornersRadius(radius);
// alternatively use fromCornersRadii or asCircle
        roundingParams.setBorder(borderColor, borderWidth);
        roundingParams.setCornersRadius(radius);
        roundingParams.setRoundAsCircle(true);
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(mContext.getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(duration)
                .setRoundingParams(roundingParams)
                .build();
        loadImage(url, draweeView, hierarchy);
    }

    /**
     * 加载圆形状图片
     *
     * @param url
     *         图片地址
     * @param draweeView
     *         view容器
     * @param radius
     *         半径
     */
    public void loadCircleImage(String url, SimpleDraweeView draweeView, float radius) {

        RoundingParams roundingParams = RoundingParams.fromCornersRadius(radius);
// alternatively use fromCornersRadii or asCircle
        roundingParams.setCornersRadius(radius);
        roundingParams.setRoundAsCircle(true);
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(mContext.getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(duration)
                .setRoundingParams(roundingParams)
                .build();
        loadImage(url, draweeView, hierarchy);
    }


    /**
     * 读取本地gif
     *
     * @param drawbleId
     *         文件id
     * @param draweeView
     *         view 容器
     */
    public void loadLocalGif(int drawbleId, SimpleDraweeView draweeView) {
        DraweeController mDraweeController = Fresco.newDraweeControllerBuilder()
                .setAutoPlayAnimations(true)
                //加载drawable里的一张gif图
                .setUri(Uri.parse("res://" + mContext.getPackageName() + "/" + drawbleId))//设置uri
                .build();
        //设置Controller
        draweeView.setController(mDraweeController);
    }
    /*
    *设置图片 end
    *
     */


    /**
     * 清除其中一个url的缓存
     */
    public void clearImage(String url) {
        Uri uri = Uri.parse(url);
        if (!isCacheImage(uri)) {
            return;
        }
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.evictFromMemoryCache(uri);
        imagePipeline.evictFromDiskCache(uri);

        // combines above two lines
        imagePipeline.evictFromCache(uri);

    }

    /**
     * 是否 存在url的缓存
     */
    public boolean isCacheImage(Uri uri) {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        return imagePipeline.isInBitmapMemoryCache(uri);
    }


}

