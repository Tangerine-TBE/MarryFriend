package com.hyphenate.easeui.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseIM;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.domain.EaseAvatarOptions;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.modules.chat.model.EaseChatItemStyleHelper;
import com.hyphenate.easeui.provider.EaseUserProfileProvider;
import com.hyphenate.easeui.widget.EaseImageView;

public class EaseUserUtils {

    static {
        // TODO: 2019/12/30 0030 how to provide userProfileProvider
//        userProvider = EaseUI.getInstance().getUserProfileProvider();
    }
    
    /**
     * get EaseUser according username
     * @param username
     * @return
     */
    public static EaseUser getUserInfo(String username){
        EaseUserProfileProvider provider = EaseIM.getInstance().getUserProvider();
        return provider == null ? null : provider.getUser(username);
    }

    /**
     * set user's avatar style
     * @param imageView
     */
    public static void setUserAvatarStyle(EaseImageView imageView) {
        EaseAvatarOptions avatarOptions = EaseIM.getInstance().getAvatarOptions();
        if(avatarOptions == null || imageView == null) {
            return;
        }
        if(avatarOptions.getAvatarShape() != 0)
            imageView.setShapeType(avatarOptions.getAvatarShape());
        if(avatarOptions.getAvatarBorderWidth() != 0)
            imageView.setBorderWidth(avatarOptions.getAvatarBorderWidth());
        if(avatarOptions.getAvatarBorderColor() != 0)
            imageView.setBorderColor(avatarOptions.getAvatarBorderColor());
        if(avatarOptions.getAvatarRadius() != 0)
            imageView.setRadius(avatarOptions.getAvatarRadius());
    }

    private static int manDefHead=R.drawable.ease_default_avatar;
    private static int womanDefHead=R.drawable.ease_default_avatar;
    private static int sex=1;

    public static int getSex() {
        return sex;
    }

    public static void setSex(int sex) {
        EaseUserUtils.sex = sex;
    }

    public static int getManDefHead() {
        return manDefHead;
    }

    public static void setManDefHead(int manDefHead) {
        EaseUserUtils.manDefHead = manDefHead;
    }

    public static int getWomanDefHead() {
        return womanDefHead;
    }

    public static void setWomanDefHead(int womanDefHead) {
        EaseUserUtils.womanDefHead = womanDefHead;
    }

    /**
     * set user avatar
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView){
    	EaseUser user = getUserInfo(username);
        int defHead;
        if (EMClient.getInstance().getCurrentUser().equals(username)){
            if (sex==1){
                defHead=manDefHead;
            }else {
                defHead=womanDefHead;
            }
        }else {
            if (sex!=1){
                defHead=manDefHead;
            }else {
                defHead=womanDefHead;
            }
        }
        if(user != null && user.getAvatar() != null){
            try {
                int avatarResId = Integer.parseInt(user.getAvatar());
                Glide.with(context).load(avatarResId)
                        .error(defHead)
                        .placeholder(defHead)
                        .into(imageView);
            } catch (Exception e) {
                //use default avatar
                Glide.with(context)
                        .load(user.getAvatar())
                        .apply(RequestOptions.placeholderOf(defHead)
                                .diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(imageView);
            }
        }else{
            Glide.with(context)
                    .load(defHead)
                    .error(defHead)
                    .placeholder(defHead)
                    .into(imageView);
        }
    }

    /**
     * show user avatar
     * @param context
     * @param avatar
     * @param imageView
     */
    public static void showUserAvatar(Context context, String avatar, ImageView imageView) {
        if(TextUtils.isEmpty(avatar)) {
            Glide.with(context).load(R.drawable.ease_default_avatar).into(imageView);
            return;
        }
        try {
            int avatarResId = Integer.parseInt(avatar);
            Glide.with(context).load(avatarResId).into(imageView);
        } catch (Exception e) {
            //use default avatar
            Glide.with(context).load(avatar)
                    .apply(RequestOptions.placeholderOf(R.drawable.ease_default_avatar)
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(imageView);
        }
    }
    
    /**
     * set user's nickname
     */
    public static void setUserNick(String username,TextView textView){
        if(textView != null){
        	EaseUser user = getUserInfo(username);
        	if(user != null && user.getNickname() != null){
        		textView.setText(user.getNickname());
        	}else{
        		textView.setText(username);
        	}
        }
    }
    
}
