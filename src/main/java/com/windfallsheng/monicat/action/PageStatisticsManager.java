package com.windfallsheng.monicat.action;

import com.windfallsheng.monicat.common.MonicatConstants;
import com.windfallsheng.monicat.model.BatchInfo;
import com.windfallsheng.monicat.model.PageInfoEntity;
import com.windfallsheng.monicat.net.BaseCallBack;
import com.windfallsheng.monicat.net.BaseOkHttpClient;
import com.windfallsheng.monicat.util.LogUtils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;


/**
 * CreateDate: 2018/4/9.
 * <p>
 * Author: lzsheng
 * <p>
 * Description: 页面统计:标记页面访问的开始或者结束；
 * 本地数据的缓存；
 * <p>
 * 数据的上传；
 * <p>
 * 作为观察者，监听{@link SwitchEventManager} 每个activity生命周期变化；
 * 作为观察者，监听{@link MonicatManager#notifyUploadData()} 发出的通知并上报数据，或者根据上传数据的即时上报策略上传数据；
 * <p>
 * Version:
 */
class PageStatisticsManager extends BaseStatisticsManager {

    private Map<String, String> mCachePages;  // 存放注册了记录页面打开状态的activity的全路径名称

    /**
     * 添加到记录页面打开状态的activity的全路径名称的集合中
     *
     * @param className
     * @param pageName
     */
    public void registerPage(String className, String pageName) {
        if (mCachePages == null) {
            mCachePages = new ConcurrentHashMap<>();
        }
        if (!mCachePages.containsKey(className)) {
            mCachePages.put(className, pageName);
        }
        LogUtils.d(MonicatConstants.SDK_NAME, "PageStatisticsManager-->addPage()_mPageMaps==" + mCachePages.toString());
    }

    /**
     * 从记录页面打开状态的activity的全路径名称的集合中移除
     *
     * @param className
     */
    public void unregisterPage(String className) {
        if (mCachePages != null && mCachePages.containsKey(className)) {
            mCachePages.remove(className);
        }
    }

    /**
     * 获取记录页面打开状态的activity的全路径名称的集合
     *
     * @return
     */
    public Map<String, String> getCachePages() {
        return mCachePages;
    }

    /**
     * 清除记录页面打开状态的activity的全路径名称的集合
     */
    public void clearPage() {
        if (mCachePages != null && mCachePages.size() > 0) {
            mCachePages.clear();
        }
    }

    /**
     * 保存数据到数据库，并且根据上传策略完成必要的逻辑处理
     *
     * @param className
     * @param pageName
     * @param openOrClose
     */
    public void savePageInfo(String className, String pageName, int openOrClose) {
        synchronized (this) {
            PageInfoEntity pageInfoEntity = new PageInfoEntity(className, pageName,
                    TimecalibrationManager.getInstance().getCurrentServerTime(), openOrClose, MonicatConstants.UPLOADABLE);
            LogUtils.d(MonicatConstants.SDK_NAME, "PageStatisticsManager-->savePageInfos()_pageInfoEntity==" + pageInfoEntity);

//      TODO: 2018/5/9 保存到本地数据库中
            // …………
        }

        handleStatisticsByStrategy();

    }

    @Override
    public void uploadData() {
        LogUtils.d(MonicatConstants.SDK_NAME, "EventStatisticsManager-->uploadData()");
        uploadCacheData();
    }


    @Override
    int queryCacheTotalCount() {
        return 0;
    }

    @Override
    BatchInfo newBatchInfo(int count) {
        return new BatchInfo(PageStatisticsManager.class.getName(), count);
    }

    /**
     * 上传启动数据到服务器
     */
    @Override
    void uploadCacheData() {
        // TODO: 2020/2/6 查询对应表中的数据
        String url = MonicatConstants.SERVER_HOST;
        LogUtils.d(MonicatConstants.SDK_NAME, "EventStatisticsManager-->uploadEventInfos()_url=" + url);
        BaseOkHttpClient.newBuilder()
                .addParam("key", "value")
                .isJsonParam(false)
                .post()
                .url(url)
                .build()
                .enqueue(new BaseCallBack() {
                             @Override
                             public void onSuccess(Object o) {
                                 LogUtils.d(MonicatConstants.SDK_NAME, "EventStatisticsManager-->uploadEventInfos()_onSuccess()=" /*+ o.toString()*/);
                                 //{"rt":0,"rtInfo":"正确","data":null}
//                                 Gson gson = new Gson();
//                                 ResponseEntity responseEntity = gson.fromJson(o.toString(), ResponseEntity.class);
//                                 if (responseEntity.getRt() == 0) {
//                                     LogUtils.d(Constants.SDK_NAME, "EventStatisticsManager-->uploadEventInfos()_Upload account infos success! \nSessionStatisticsManager-->responseEntity.msg=" + responseEntity.getMsg());
//                                     // TODO: 2018/5/7 修改本地数据库中缓存数据的上传状态，改为已上传 Constants.HAS_UPLOADED
//
//                                 } else {
//
//                                 }
                             }

                             @Override
                             public void onError(int code) {
                                 LogUtils.d(MonicatConstants.SDK_NAME, "EventStatisticsManager-->uploadEventInfos()_onError()=" + code);
                             }

                             @Override
                             public void onFailure(Call call, IOException e) {
                                 LogUtils.d(MonicatConstants.SDK_NAME, "EventStatisticsManager-->uploadEventInfos()_onFailure()=" + e.toString());
                             }
                         }
                );
    }


}
