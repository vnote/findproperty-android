package com.cetnaline.findproperty.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PointF;

import com.baidu.mapapi.model.LatLng;
import com.cetnaline.findproperty.api.bean.SearchData;
import com.cetnaline.findproperty.db.dao.DaoMaster;
import com.cetnaline.findproperty.db.dao.DaoSession;
import com.cetnaline.findproperty.db.dao.DropBoDao;
import com.cetnaline.findproperty.db.dao.FileDownLoadDao;
import com.cetnaline.findproperty.db.dao.GScopeDao;
import com.cetnaline.findproperty.db.dao.HistoryHouseTagDao;
import com.cetnaline.findproperty.db.dao.RailLineDao;
import com.cetnaline.findproperty.db.dao.RailWayDao;
import com.cetnaline.findproperty.db.dao.RcTokenDao;
import com.cetnaline.findproperty.db.dao.StaffDao;
import com.cetnaline.findproperty.db.dao.StoreDao;
import com.cetnaline.findproperty.db.entity.DropBo;
import com.cetnaline.findproperty.db.entity.FileDownLoad;
import com.cetnaline.findproperty.db.entity.GScope;
import com.cetnaline.findproperty.db.entity.HistoryHouseTag;
import com.cetnaline.findproperty.db.entity.RailLine;
import com.cetnaline.findproperty.db.entity.RailWay;
import com.cetnaline.findproperty.db.entity.RcToken;
import com.cetnaline.findproperty.db.entity.Staff;
import com.cetnaline.findproperty.db.entity.Store;
import com.cetnaline.findproperty.entity.bean.StaffListBean;
import com.cetnaline.findproperty.ui.activity.SearchActivity;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by diaoqf on 2016/8/2.
 */
public class DbUtil {

    private DaoMaster.DevOpenHelper openHelper;

    private static DaoSession writeDaoSession;

    private static DaoSession readDaoSession;

    private static DbUtil dbUtil;

    private int dbVersion;

    public static final String DB_NAME = "findproperty-db";

    private DbUtil(Context mContext){
        openHelper = new DaoMaster.DevOpenHelper(mContext, DB_NAME, null);
        DaoMaster writeDaoMaster = new DaoMaster(getWritableDatabase());
        writeDaoSession = writeDaoMaster.newSession();

        DaoMaster readDaoMaster = new DaoMaster(getReadableDatabase());
        dbVersion = readDaoMaster.getSchemaVersion();
        readDaoSession = readDaoMaster.newSession();
    }

    public int getVersion(){
        return dbVersion;
    }

    public static DbUtil init(Context context) {
        if (dbUtil == null) {
            dbUtil = new DbUtil(context);
        }
        return dbUtil;
    }

    public static void setNull(){
        dbUtil = null;
    }

    /**
     * 获取可写数据库
     */
    private SQLiteDatabase getWritableDatabase() {
        if (openHelper == null) {
            throw new RuntimeException("DbManager not instance");
        }
        SQLiteDatabase db = openHelper.getWritableDatabase();
        return db;
    }

    public DaoSession getWriteDaoSession() {
        return writeDaoSession;
    }

    /**
     * 获取可读可写数据库
     */
    private SQLiteDatabase getReadableDatabase() {
        if (openHelper == null) {
            throw new RuntimeException("DbManager not instance");
        }
        SQLiteDatabase db = openHelper.getReadableDatabase();
        return db;
    }

    public DaoSession getReadDaoSession() {
        return readDaoSession;
    }

    public void execSql(String sql){
        writeDaoSession.getDatabase().execSQL(sql);
    }


    /**
     * 依据uid查询staff
     * @param uId
     * @return
     */
    public static Staff getStaffByUid(String uId) {
        if (uId != null) {
            List<Staff> list = readDaoSession.getStaffDao().queryBuilder()
                    .where(StaffDao.Properties.UId.eq(uId)).list();
            if (list.size() > 0) {
                return list.get(0);
            }
        }
        return null;
    }

    /**
     * 保存staff
     *
     * @param staff
     */
    public static void addStaff(Staff staff) {
        Staff savedStaff = getStaffByUid(staff.getUId());
        if (savedStaff == null) {
            if (staff.getMobile() == null) {
                staff.setMobile("");
            }
            writeDaoSession.getStaffDao().insert(staff);
        } else {
            savedStaff.setDepartmentName(staff.getDepartmentName());
            savedStaff.setImageUrl(staff.getImageUrl());
            savedStaff.setName(staff.getName());
            savedStaff.setMobile(staff.getMobile());
            savedStaff.setServiceEstates(staff.getServiceEstates());
            savedStaff.setStaff400Tel(staff.getStaff400Tel());
            savedStaff.setStaffRemark(staff.getStaffRemark());
            writeDaoSession.getStaffDao().update(savedStaff);
        }
    }

    public static Staff addStaff(StaffListBean bean) {
        Staff staff = new Staff();
        staff.setUId(bean.StaffNo);
        staff.setDepartmentName(bean.StoreName);
        staff.setImageUrl(bean.StaffImg);
        staff.setName(bean.CnName);
        staff.setMobile(bean.Mobile);
        staff.setServiceEstates(bean.ServiceEstates);
        String number = bean.MobileBy400 != null ? bean.MobileBy400:bean.Staff400Tel;
        staff.setStaff400Tel(number);
        staff.setStaffRemark(bean.StaffRemark);
        DbUtil.addStaff(staff);

        return staff;
    }

    /**
     * 保存staff列表
     *
     * @param staffs
     */
    public static void addStaffs(List<Staff> staffs) {
        for (Staff staff : staffs) {
            addStaff(staff);
        }
    }

    /**
     * 获取所有staff
     *
     * @return
     */
    public static List<Staff> getAllStaff() {
        return readDaoSession.getStaffDao().loadAll();
    }

    /**
     * 分页获取staff
     *
     * @return
     */
    public static List<Staff> getStaffByPage(int page, int pageSize) {
        return readDaoSession.getStaffDao().queryBuilder().offset((page - 1) * pageSize).limit(pageSize).list();
    }

    /**
     * 删除所有staff
     */
    public static void deleteAllStaff() {
        writeDaoSession.getStaffDao().deleteAll();
    }

    /**
     * 删除指定staff
     *
     * @param uId
     */
    public static void deleteStaff(String uId) {
        Staff staff = getStaffByUid(uId);
        deleteStaff(staff);
    }

    /**
     * 删除指定staff
     *
     * @param staff
     */
    public static void deleteStaff(Staff staff) {
        writeDaoSession.getStaffDao().delete(staff);
    }


    /**
     * 依据id获取Gscope
     *
     * @param id
     * @return
     */
    public static GScope getGScopeById(int id) {
        GScope gscope = readDaoSession.getGScopeDao().queryBuilder()
                .where(GScopeDao.Properties.GScopeId.eq(id))
                .orderDesc(GScopeDao.Properties.Id)
                .unique();
        return gscope;
    }

    public static Store getStoreById(int id) {
        Store store = readDaoSession.getStoreDao().queryBuilder()
                .where(StoreDao.Properties.StoreId.eq(id))
                .orderDesc(StoreDao.Properties.StoreId)
                .unique();
        return store;
    }

    /**
     * 按照区域查找店铺
     * @param scopeId
     * @return
     */
    public static List<Store> getStoreByGscope(int scopeId) {

        List<Store> list = readDaoSession.getStoreDao().queryBuilder()
                .where(StoreDao.Properties.RegionId.eq(scopeId))
                .list();

        return list;
    }

    /**
     * 获取周围门店
     * @param location
     * @param round
     * @return
     */
    public static List<Store> getStoreByLocation(LatLng location, int round) {
        PointF center = new PointF((float) location.latitude, (float) location.longitude);
        final double mult = 1; // mult = 1.1; is more reliable
        PointF p1 = calculateDerivedPosition(center, mult * round, 0);
        PointF p2 = calculateDerivedPosition(center, mult * round, 90);
        PointF p3 = calculateDerivedPosition(center, mult * round, 180);
        PointF p4 = calculateDerivedPosition(center, mult * round, 270);

        List<Store> list = readDaoSession.getStoreDao().queryBuilder()
                .where(StoreDao.Properties.Lat.gt(p3.x),
                        StoreDao.Properties.Lat.lt(p1.x),
                        StoreDao.Properties.Lng.gt(p4.y),
                        StoreDao.Properties.Lng.lt(p2.y))
                .list();
        return list;
    }

    /**
     * 获取所有门店
     * @return
     */
    public static List<Store> getAllStore() {
        List<Store> list = readDaoSession.getStoreDao().queryBuilder().list();
        return list;
    }

    /**
     * 计算当前位置查询店铺条件
     * @param point
     * @param range
     * @param bearing
     * @return
     */
    public static PointF calculateDerivedPosition(PointF point, double range, double bearing)
    {
        double EarthRadius = 6371000; // m
        double latA = Math.toRadians(point.x);
        double lonA = Math.toRadians(point.y);
        double angularDistance = range / EarthRadius;
        double trueCourse = Math.toRadians(bearing);

        double lat = Math.asin(
                Math.sin(latA) * Math.cos(angularDistance) +
                        Math.cos(latA) * Math.sin(angularDistance)
                                * Math.cos(trueCourse));

        double dlon = Math.atan2(
                Math.sin(trueCourse) * Math.sin(angularDistance)
                        * Math.cos(latA),
                Math.cos(angularDistance) - Math.sin(latA) * Math.sin(lat));

        double lon = ((lonA + dlon + Math.PI) % (Math.PI * 2)) - Math.PI;

        lat = Math.toDegrees(lat);
        lon = Math.toDegrees(lon);

        PointF newPoint = new PointF((float) lat, (float) lon);

        return newPoint;

    }

    /**
     * 获取下级节点
     *
     * @param parentId
     * @return
     */
    public static List<GScope> getGScopeChild(int parentId) {
        List<GScope> childs = readDaoSession.getGScopeDao().queryBuilder()
                .where(GScopeDao.Properties.ParentId.eq(parentId <= 0 ? 21 : parentId))
                .orderAsc(GScopeDao.Properties.OrderBy)
                .list();

        return childs;
    }

    /**
     * 依据位置获取当前区块列表
     * @param location
     * @return
     */
    public static List<GScope> getGScopeChildByLocation(LatLng location) {
        PointF center = new PointF((float) location.latitude, (float) location.longitude);
        final double mult = 1; // mult = 1.1; is more reliable
        PointF p1 = calculateDerivedPosition(center, mult * 3000, 0);
        PointF p2 = calculateDerivedPosition(center, mult * 3000, 90);
        PointF p3 = calculateDerivedPosition(center, mult * 3000, 180);
        PointF p4 = calculateDerivedPosition(center, mult * 3000, 270);

        List<GScope> childs = readDaoSession.getGScopeDao().queryBuilder()
                .where(GScopeDao.Properties.Lat.gt(p3.x),
                        GScopeDao.Properties.Lat.lt(p1.x),
                        GScopeDao.Properties.Lng.gt(p4.y),
                        GScopeDao.Properties.Lng.lt(p2.y),
                        GScopeDao.Properties.ParentId.notEq(21))
                .orderAsc(GScopeDao.Properties.OrderBy)
                .list();

        return childs;
    }

    public static long getGscpoeCount(){
        return readDaoSession.getGScopeDao().queryBuilder().count();
    }

    public static List<RailLine> getRailLines() {
        return readDaoSession.getRailLineDao().queryBuilder().list();
    }

    public static long getRailLineCount(){
        return readDaoSession.getRailLineDao().queryBuilder().count();
    }

    public static List<RailWay> getRailWayByRailLineId(String radilLineId) {
        return readDaoSession.getRailWayDao().queryBuilder()
                .where(RailWayDao.Properties.RailLineID.eq(radilLineId))
                .list();
    }

    public static RailWay getRailWayByNameAndLineId(String railLineId, String railWayName){
        RailWay railWay = readDaoSession.getRailWayDao().queryBuilder()
                .where(RailWayDao.Properties.RailLineID.eq(railLineId),RailWayDao.Properties.RailWayName.eq(railWayName))
                .unique();
        return railWay;
    }

    public static RailLine getRailLineById(String railLineId){
        RailLine railLine = readDaoSession.getRailLineDao().queryBuilder()
                .where(RailLineDao.Properties.RailLineID.eq(railLineId))
                .unique();
        return railLine;
    }

    public static RailWay getRailWayById(String railWayId){
        RailWay railWay = readDaoSession.getRailWayDao().queryBuilder()
                .where(RailWayDao.Properties.RailWayID.eq(railWayId))
                .unique();
        return railWay;
    }

    /**
     * 保存Gscope
     *
     * @param gScope
     */
    public static void addGScope(GScope gScope) {
        GScope savedGScope = getGScopeById(gScope.getGScopeId());
        if (savedGScope == null) {
            writeDaoSession.getGScopeDao().insert(gScope);
        } else {
            savedGScope.setEstateCount(gScope.getEstateCount());
            savedGScope.setFirstPY(gScope.getFirstPY());
            savedGScope.setFullPY(gScope.getFullPY());
            savedGScope.setGScopeAlias(gScope.getGScopeAlias());
            savedGScope.setGScopeCode(gScope.getGScopeCode());
            savedGScope.setGScopeId(gScope.getGScopeId());
            savedGScope.setGScopeLevel(gScope.getGScopeLevel());
            savedGScope.setGScopeName(gScope.getGScopeName());
            savedGScope.setLat(gScope.getLat());
            savedGScope.setLng(gScope.getLng());
            savedGScope.setSaleAvgPrice(gScope.getSaleAvgPrice());
            savedGScope.setSaleAvgPriceRise(gScope.getSaleAvgPriceRise());
            savedGScope.setOrderBy(gScope.getOrderBy());
            savedGScope.setParentId(gScope.getParentId());
            writeDaoSession.getGScopeDao().update(savedGScope);
        }
    }

    public static void addStore(Store store) {
        Store savedStore = getStoreById((int) store.getStoreId());
        if (savedStore == null) {
            writeDaoSession.getStoreDao().insert(store);
        } else {
            savedStore.setGscopeId(store.getGscopeId());
            savedStore.setLat(store.getLat());
            savedStore.setLng(store.getLng());
            savedStore.setPaNo(store.getPaNo());
            savedStore.setRegionId(store.getRegionId());
            savedStore.setStaffCount(store.getStaffCount());
            savedStore.setStore400Tel(store.getStore400Tel());
            savedStore.setStoreAddr(store.getStoreAddr());
            savedStore.setStoreHonor(store.getStoreHonor());
            savedStore.setStoreName(store.getStoreName());
            savedStore.setStoreTel(store.getStoreTel());
            savedStore.setTencentVistaUrl(store.getTencentVistaUrl());
            savedStore.setStoreId(store.getStoreId());
            writeDaoSession.getStoreDao().update(savedStore);
        }
    }


    public static void saveSearchData(SearchData searchData) {
        List<DropBo> dropBos = searchData.getSearchDataItemList();
        for (DropBo dropBo : dropBos) {
            dropBo.setName(searchData.getName());
        }
        writeDaoSession.getDropBoDao().insertInTx(dropBos);
    }

    /**
     * 依据id和name获取search data
     *
     * @param id
     * @return
     */
    public static DropBo getSearchDataByNameAndId(String name, Integer id) {
        QueryBuilder qb = readDaoSession.getDropBoDao().queryBuilder();
        DropBo dropBo = readDaoSession.getDropBoDao().queryBuilder()
                .where(DropBoDao.Properties.ID.eq(id), DropBoDao.Properties.Name.eq(name))
                .unique();
        return dropBo;
    }

    public static void saveRailWay(RailLine railLine) {
        List<RailWay> railWays = railLine.getRailWayList();
        if (railWays != null) {
            for (RailWay railWay : railWays) {
                railWay.setRailLineID(railLine.getRailLineID());
            }
            writeDaoSession.getRailWayDao().insertInTx(railWays);
        }
    }

    public static void saveRailLine(List<RailLine> railLines){
        writeDaoSession.getRailLineDao().insertInTx(railLines);
    }

    public static void clearSearchData() {
        writeDaoSession.getDropBoDao().deleteAll();
    }

    public static void clearRailLine() {
        writeDaoSession.getRailLineDao().deleteAll();
        writeDaoSession.getRailWayDao().deleteAll();
    }

    public static List<DropBo> getSearchDataByName(String name) {
        return readDaoSession.getDropBoDao().queryBuilder()
                .where(DropBoDao.Properties.Name.eq(name))
                .list();
    }

    public static long getSearchDataCount(){
        return readDaoSession.getDropBoDao().queryBuilder()
                .count();
    }


    /**
     * 删除所有区域信息
     */
    public static void deleteAllGScope() {
        writeDaoSession.getGScopeDao().deleteAll();
    }

    public static void deleteAllStore() {
        writeDaoSession.getStoreDao().deleteAll();
    }

    /**
     * 保存搜索记录
     *
     * @param tag
     */
    public static boolean saveHistoryHouseTag(HistoryHouseTag tag) {
        return writeDaoSession.getHistoryHouseTagDao().insert(tag)>0;
    }

    /**
     * 获取搜索历史记录
     *
     * @param id
     * @return
     */
    public static List<HistoryHouseTag> getHistoryHouseTag(String id) {
        return readDaoSession.getHistoryHouseTagDao().queryBuilder()
                .where(HistoryHouseTagDao.Properties.UserId.eq(id))
                .orderDesc(HistoryHouseTagDao.Properties.Id).list();
    }

    /**
     * 获取搜索历史记录 小区
     *
     * @param id
     * @return
     */
    public static List<HistoryHouseTag> getHistoryCommunityTag(String id) {
        return readDaoSession.getHistoryHouseTagDao().queryBuilder()
                .where(HistoryHouseTagDao.Properties.UserId.eq(id)
                        , HistoryHouseTagDao.Properties.HouseType.eq(SearchActivity.HOUSE_TYPE_COMMUNITY))
                .orderDesc(HistoryHouseTagDao.Properties.Id).list();
    }

    /**
     * 删除所有搜索历史记录
     */
    public static void deleteHistoryHouseTagAll() {
        writeDaoSession.getHistoryHouseTagDao().deleteAll();
    }

    /**
     * 删除单条搜索历史记录
     *
     * @param tag
     */
    public static void deleteHistoryHouseTag(HistoryHouseTag tag) {
        writeDaoSession.getHistoryHouseTagDao().delete(tag);
    }


    /**
     * 融云token缓存
     * @param userId
     * @return
     */
    public static String getRcTokenById(String userId) {
        RcToken token = getRcTokenBean(userId);
        if (token != null) {
            return token.getToken();
        }
        return null;
    }

    /**
     * 获取token string
     * @param userId
     * @return
     */
    public static RcToken getRcTokenBean(String userId) {
        RcToken rcToken = readDaoSession.getRcTokenDao().queryBuilder()
                .where(RcTokenDao.Properties.UserId.eq(userId))
                .unique();
        return rcToken;
    }

    /**
     * 保存token
     * @param token
     */
    public static void saveRcToken(String token) {
        String sss = "u_" + DataHolder.getInstance().getUserId().toLowerCase();

        RcToken rcToken = getRcTokenBean(sss);
        if (rcToken == null) {
            RcToken tmp = new RcToken();
            tmp.setToken(token);
            tmp.setUserId(sss);
            writeDaoSession.getRcTokenDao().insert(tmp);
        } else {
            rcToken.setToken(token);
            writeDaoSession.getRcTokenDao().update(rcToken);
        }
    }

    public static Map<Integer, Integer> getDownLoadByPath(String url){
        List<FileDownLoad> downLoads = readDaoSession.getFileDownLoadDao().queryBuilder()
                .where(FileDownLoadDao.Properties.DownPath.eq(url))
                .list();

        if (downLoads==null || downLoads.size()==0)return null;

        Map<Integer, Integer> data = new HashMap<>();
        for (FileDownLoad item : downLoads){
            data.put(item.getThreadId(), item.getDownloadLength());
        }
        return data;
    }

    public static String getDownLoadModifiedSince(String url){
        List<FileDownLoad> downLoads = readDaoSession.getFileDownLoadDao().queryBuilder()
                .where(FileDownLoadDao.Properties.DownPath.eq(url)).list();
        if (downLoads==null || downLoads.size()==0)return null;
        return downLoads.get(0).getModifiedSince();
    }

    public static void saveDownLoadData(String path, Map<Integer, Integer> map, String modifiedSince){

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            FileDownLoad file = new FileDownLoad();
            file.setDownPath(path);
            file.setThreadId(entry.getKey());
            file.setDownloadLength(entry.getValue());
            file.setModifiedSince(modifiedSince);
            writeDaoSession.getFileDownLoadDao().save(file);
        }
    }

    public static void deleteDownLoadData(String path){
        List<FileDownLoad> fileDownLoads = readDaoSession.getFileDownLoadDao().queryBuilder().where(FileDownLoadDao.Properties.DownPath.eq(path)).list();
        writeDaoSession.getFileDownLoadDao().deleteInTx(fileDownLoads);
    }

    public static void updateDownLoad(String path, int threadId, int pos){
        FileDownLoad file = readDaoSession.getFileDownLoadDao().queryBuilder()
                .where(FileDownLoadDao.Properties.DownPath.eq(path), FileDownLoadDao.Properties.ThreadId.eq(threadId))
                .unique();
        if (file!=null){
            file.setDownloadLength(pos);
            writeDaoSession.getFileDownLoadDao().update(file);
        }
    }
}
