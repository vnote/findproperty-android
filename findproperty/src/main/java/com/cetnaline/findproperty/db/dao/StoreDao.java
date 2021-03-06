package com.cetnaline.findproperty.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.cetnaline.findproperty.db.entity.Store;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "house_store".
*/
public class StoreDao extends AbstractDao<Store, Long> {

    public static final String TABLENAME = "house_store";

    /**
     * Properties of entity Store.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property StoreId = new Property(0, long.class, "StoreId", true, "_id");
        public final static Property StoreName = new Property(1, String.class, "StoreName", false, "STORE_NAME");
        public final static Property RegionId = new Property(2, int.class, "RegionId", false, "REGION_ID");
        public final static Property GscopeId = new Property(3, int.class, "GscopeId", false, "GSCOPE_ID");
        public final static Property StoreAddr = new Property(4, String.class, "StoreAddr", false, "STORE_ADDR");
        public final static Property StoreTel = new Property(5, String.class, "StoreTel", false, "STORE_TEL");
        public final static Property Store400Tel = new Property(6, String.class, "Store400Tel", false, "STORE400_TEL");
        public final static Property StoreHonor = new Property(7, String.class, "StoreHonor", false, "STORE_HONOR");
        public final static Property Lng = new Property(8, double.class, "Lng", false, "LNG");
        public final static Property Lat = new Property(9, double.class, "Lat", false, "LAT");
        public final static Property PaNo = new Property(10, String.class, "PaNo", false, "PA_NO");
        public final static Property StaffCount = new Property(11, int.class, "StaffCount", false, "STAFF_COUNT");
        public final static Property TencentVistaUrl = new Property(12, String.class, "TencentVistaUrl", false, "TENCENT_VISTA_URL");
    }


    public StoreDao(DaoConfig config) {
        super(config);
    }
    
    public StoreDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"house_store\" (" + //
                "\"_id\" INTEGER PRIMARY KEY NOT NULL ," + // 0: StoreId
                "\"STORE_NAME\" TEXT," + // 1: StoreName
                "\"REGION_ID\" INTEGER NOT NULL ," + // 2: RegionId
                "\"GSCOPE_ID\" INTEGER NOT NULL ," + // 3: GscopeId
                "\"STORE_ADDR\" TEXT," + // 4: StoreAddr
                "\"STORE_TEL\" TEXT," + // 5: StoreTel
                "\"STORE400_TEL\" TEXT," + // 6: Store400Tel
                "\"STORE_HONOR\" TEXT," + // 7: StoreHonor
                "\"LNG\" REAL NOT NULL ," + // 8: Lng
                "\"LAT\" REAL NOT NULL ," + // 9: Lat
                "\"PA_NO\" TEXT," + // 10: PaNo
                "\"STAFF_COUNT\" INTEGER NOT NULL ," + // 11: StaffCount
                "\"TENCENT_VISTA_URL\" TEXT);"); // 12: TencentVistaUrl
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"house_store\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Store entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getStoreId());
 
        String StoreName = entity.getStoreName();
        if (StoreName != null) {
            stmt.bindString(2, StoreName);
        }
        stmt.bindLong(3, entity.getRegionId());
        stmt.bindLong(4, entity.getGscopeId());
 
        String StoreAddr = entity.getStoreAddr();
        if (StoreAddr != null) {
            stmt.bindString(5, StoreAddr);
        }
 
        String StoreTel = entity.getStoreTel();
        if (StoreTel != null) {
            stmt.bindString(6, StoreTel);
        }
 
        String Store400Tel = entity.getStore400Tel();
        if (Store400Tel != null) {
            stmt.bindString(7, Store400Tel);
        }
 
        String StoreHonor = entity.getStoreHonor();
        if (StoreHonor != null) {
            stmt.bindString(8, StoreHonor);
        }
        stmt.bindDouble(9, entity.getLng());
        stmt.bindDouble(10, entity.getLat());
 
        String PaNo = entity.getPaNo();
        if (PaNo != null) {
            stmt.bindString(11, PaNo);
        }
        stmt.bindLong(12, entity.getStaffCount());
 
        String TencentVistaUrl = entity.getTencentVistaUrl();
        if (TencentVistaUrl != null) {
            stmt.bindString(13, TencentVistaUrl);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Store entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getStoreId());
 
        String StoreName = entity.getStoreName();
        if (StoreName != null) {
            stmt.bindString(2, StoreName);
        }
        stmt.bindLong(3, entity.getRegionId());
        stmt.bindLong(4, entity.getGscopeId());
 
        String StoreAddr = entity.getStoreAddr();
        if (StoreAddr != null) {
            stmt.bindString(5, StoreAddr);
        }
 
        String StoreTel = entity.getStoreTel();
        if (StoreTel != null) {
            stmt.bindString(6, StoreTel);
        }
 
        String Store400Tel = entity.getStore400Tel();
        if (Store400Tel != null) {
            stmt.bindString(7, Store400Tel);
        }
 
        String StoreHonor = entity.getStoreHonor();
        if (StoreHonor != null) {
            stmt.bindString(8, StoreHonor);
        }
        stmt.bindDouble(9, entity.getLng());
        stmt.bindDouble(10, entity.getLat());
 
        String PaNo = entity.getPaNo();
        if (PaNo != null) {
            stmt.bindString(11, PaNo);
        }
        stmt.bindLong(12, entity.getStaffCount());
 
        String TencentVistaUrl = entity.getTencentVistaUrl();
        if (TencentVistaUrl != null) {
            stmt.bindString(13, TencentVistaUrl);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    @Override
    public Store readEntity(Cursor cursor, int offset) {
        Store entity = new Store( //
            cursor.getLong(offset + 0), // StoreId
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // StoreName
            cursor.getInt(offset + 2), // RegionId
            cursor.getInt(offset + 3), // GscopeId
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // StoreAddr
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // StoreTel
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // Store400Tel
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // StoreHonor
            cursor.getDouble(offset + 8), // Lng
            cursor.getDouble(offset + 9), // Lat
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // PaNo
            cursor.getInt(offset + 11), // StaffCount
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12) // TencentVistaUrl
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Store entity, int offset) {
        entity.setStoreId(cursor.getLong(offset + 0));
        entity.setStoreName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setRegionId(cursor.getInt(offset + 2));
        entity.setGscopeId(cursor.getInt(offset + 3));
        entity.setStoreAddr(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setStoreTel(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setStore400Tel(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setStoreHonor(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setLng(cursor.getDouble(offset + 8));
        entity.setLat(cursor.getDouble(offset + 9));
        entity.setPaNo(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setStaffCount(cursor.getInt(offset + 11));
        entity.setTencentVistaUrl(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Store entity, long rowId) {
        entity.setStoreId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Store entity) {
        if(entity != null) {
            return entity.getStoreId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Store entity) {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
