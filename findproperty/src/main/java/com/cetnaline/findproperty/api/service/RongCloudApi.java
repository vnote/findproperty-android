package com.cetnaline.findproperty.api.service;


import com.cetnaline.findproperty.entity.bean.RcBaseBean;
import com.cetnaline.findproperty.entity.result.RcBaseResult;
import com.cetnaline.findproperty.entity.result.RcBlackUserListResult;
import com.cetnaline.findproperty.entity.result.RcBlockUserListResult;
import com.cetnaline.findproperty.entity.result.RcStatusResult;
import com.cetnaline.findproperty.entity.result.RcTokenResult;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by diaoqf on 2016/7/18.
 */
public interface RongCloudApi {

    /**
     * 获取 token
     * @param userId 用户Id
     * @param name 用户名
     * @param portraitUri 用户头像路径
     * @return
     */
    @FormUrlEncoded
    @POST("/user/getToken.json")
    Observable<RcTokenResult> getToken(@Field("userId") String userId, @Field("name") String name, @Field("portraitUri") String portraitUri);

    /**
     * 消息发送
     * @param fromUserId
     * @param toUserId
     * @param objectName
     * @param content
     * @param pushData
     * @param count
     * @param verifyBlacklist
     * @param isPersisted
     * @param isCounted
     * @return
     */
    @FormUrlEncoded
    @POST("/message/private/publish.json")
    Observable<RcBaseBean> sendMessage(@Field("fromUserId") String fromUserId,
                                       @Field("toUserId") String toUserId,
                                       @Field("objectName") String objectName,
                                       @Field("content") String content,
                                       @Field("pushData") String pushData,
                                       @Field("count") String count,
                                       @Field("verifyBlacklist") int verifyBlacklist,
                                       @Field("isPersisted") int isPersisted,
                                       @Field("isCounted") int isCounted);



    /**
     * 发送系统消息
     * @param fromUserId
     * @param toUserId
     * @param objectName
     * @param content
     * @param pushContent
     * @param isPersisted
     * @param isCounted
     * @return
     */
    @FormUrlEncoded
    @POST("/message/system/publish.json")
    Observable<RcBaseBean> sendSystemMessage(@Field("fromUserId") String fromUserId,
                                             @Field("toUserId") String toUserId,
                                             @Field("objectName") String objectName,
                                             @Field("content") String content,
                                             @Field("pushContent") String pushContent,
                                             @Field("isPersisted") int isPersisted,
                                             @Field("isCounted") int isCounted);

    /**
     * 更新用户信息
     * @param userId
     * @param name
     * @param portraitUri
     * @return
     */
    @FormUrlEncoded
    @POST("/user/refresh.json")
    Observable<RcBaseResult> updateUserInfo(@Field("userId") String userId, @Field("name") String name, @Field("portraitUri") String portraitUri);

    /**
     * 获取用户在线状态
     * @param userId
     * @return
     */
    @FormUrlEncoded
    @POST("/user/checkOnline.json")
    Observable<RcStatusResult> getUserStatus(@Field("userId") String userId);

    /**
     * 用户禁言
     * @param userId 用户ID
     * @param minute 封禁分钟数
     * @return
     */
    @FormUrlEncoded
    @POST("/user/block.json")
    Observable<RcBaseResult> blockUser(@Field("userId") String userId, @Field("minute") int minute);

    /**
     * 解禁用户
     * @param userId
     * @return
     */
    @FormUrlEncoded
    @POST("/user/unblock.json")
    Observable<RcBaseResult> unblockUser(@Field("userId") String userId);

    /**
     * 获取封禁用户列表
     * @return
     */
    @POST("/user/block/query.json")
    Observable<RcBlockUserListResult> getBlockUsers();

    /**
     * 添加用户到黑名单
     * @param userId  当前用Id
     * @param blackUserId 添加到黑名单用户Id
     * @return
     */
    @FormUrlEncoded
    @POST("/user/blacklist/add.json")
    Observable<RcBaseResult> addUserToBlackList(@Field("userId") String userId, @Field("blackUserId") String blackUserId);

    /**
     * 将用户移除黑名单
     * @param userId
     * @param blackUserId
     * @return
     */
    @FormUrlEncoded
    @POST("/user/blacklist/remove.json")
    Observable<RcBaseResult> removeUserFromBlackList(@Field("userId") String userId, @Field("blackUserId") String blackUserId);

    /**
     * 获取黑名单用户列表
     * @param userId 当前用户Id
     * @return
     */
    @FormUrlEncoded
    @POST("/user/blacklist/query.json")
    Observable<RcBlackUserListResult> getBlackUsers(@Field("userId") String userId);

}




























