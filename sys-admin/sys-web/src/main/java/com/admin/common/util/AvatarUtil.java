package com.admin.common.util;

import java.util.Random;

/**
 * @Author: 王琦 <QQ.Eamil>1124602935@qq.com</QQ.Eamil>
 * @Date: 2021-7-13 0013 23:42
 * @Description: 无描述信息
 */

public class AvatarUtil {


    private static final String [] avatarArray = new String[] {
           "https://ai-public.mastergo.com/ai/img_res/2f2907aedd5ac7478402c99d6c20eb4f.jpg",
            "https://ai-public.mastergo.com/ai/img_res/ee0cb35308d9e0d1631c2db894ade825.jpg",
            "https://ai-public.mastergo.com/ai/img_res/48c77eccc37e05eadb436521c9e76982.jpg",
            "https://images.unsplash.com/photo-1551288049-bebda4e38f71",
            "https://images.pexels.com/photos/669610/pexels-photo-669610.jpeg",
            "https://images.unsplash.com/photo-1590283603385-17ffb3a7f29f",
            "https://images.pexels.com/photos/730547/pexels-photo-730547.jpeg",
            "https://images.unsplash.com/photo-1450101499163-c8848c66ca85",
            "https://images.pexels.com/photos/844124/pexels-photo-844124.jpeg",
            "https://images.unsplash.com/photo-1639762681057-408e52192e55",
            "https://images.pexels.com/photos/6772078/pexels-photo-6772078.jpeg"

    };

    /***
     * 随机获取一个头像
     * @return
     */
    public static String getRandomAvatar() {
        int index = new Random().nextInt(11);
        return avatarArray[index];
    }
}
