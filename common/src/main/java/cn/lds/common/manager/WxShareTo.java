package cn.lds.common.manager;

import android.support.annotation.IntDef;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static cn.lds.common.manager.WxShareTo.share_favorite;
import static cn.lds.common.manager.WxShareTo.share_session;
import static cn.lds.common.manager.WxShareTo.share_timeline;

@IntDef({ share_session, share_timeline, share_favorite })// 枚举数据
@Retention(RetentionPolicy.SOURCE) //告诉编译器在生成.class文件时不保留枚举注解数据
public @interface WxShareTo {
    // 发送到聊天界面——WXSceneSession
    int share_session = SendMessageToWX.Req.WXSceneSession;
    // 发送到朋友圈——WXSceneTimeline
    int share_favorite = SendMessageToWX.Req.WXSceneFavorite;
    // 添加到微信收藏——WXSceneFavorite
    int share_timeline = SendMessageToWX.Req.WXSceneTimeline;
}
