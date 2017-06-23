package com.cetnaline.findproperty.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.cetnaline.findproperty.db.entity.HistoryHouseTag;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "house_search_history".
*/
public class HistoryHouseTagDao extends AbstractDao<HistoryHouseTag, Long> {

    public static final String TABLENAME = "house_search_history";

    /**
     * Properties of entity HistoryHouseTag.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserId = new Property(1, String.class, "UserId", false, "USER_ID");
        public final static Property HouseType = new Property(2, String.class, "HouseType", false, "HOUSE_TYPE");
        public final static Property TagCode = new Property(3, String.class, "TagCode", false, "TAG_CODE");
        public final static Property PN1 = new Property(4, String.class, "PN1", false, "PN1");
        public final static Property PN2 = new Property(5, String.class, "PN2", false, "PN2");
        public final static Property Tag = new Property(6, String.class, "Tag", false, "TAG");
        public final static Property TagPY = new Property(7, String.class, "TagPY", false, "TAG_PY");
        public final static Property TagCategory = new Property(8, String.class, "TagCategory", false, "TAG_CATEGORY");
        public final static Property SNum = new Property(9, Integer.class, "SNum", false, "SNUM");
        public final static Property RNum = new Property(10, Integer.class, "RNum", false, "RNUM");
        public final static Property EstateAvgPriceSale = new Property(11, Double.class, "EstateAvgPriceSale", false, "ESTATE_AVG_PRICE_SALE");
        public final static Property EstateAvgPriceRent = new Property(12, Double.class, "EstateAvgPriceRent", false, "ESTATE_AVG_PRICE_RENT");
    }


    public HistoryHouseTagDao(DaoConfig config) {
        super(config);
    }
    
    public HistoryHouseTagDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"house_search_history\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"USER_ID\" TEXT," + // 1: UserId
                "\"HOUSE_TYPE\" TEXT," + // 2: HouseType
                "\"TAG_CODE\" TEXT," + // 3: TagCode
                "\"PN1\" TEXT," + // 4: PN1
                "\"PN2\" TEXT," + // 5: PN2
                "\"TAG\" TEXT," + // 6: Tag
                "\"TAG_PY\" TEXT," + // 7: TagPY
                "\"TAG_CATEGORY\" TEXT," + // 8: TagCategory
                "\"SNUM\" INTEGER," + // 9: SNum
                "\"RNUM\" INTEGER," + // 10: RNum
                "\"ESTATE_AVG_PRICE_SALE\" REAL," + // 11: EstateAvgPriceSale
                "\"ESTATE_AVG_PRICE_RENT\" REAL);"); // 12: EstateAvgPriceRent
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"house_search_history\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, HistoryHouseTag entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String UserId = entity.getUserId();
        if (UserId != null) {
            stmt.bindString(2, UserId);
        }
 
        String HouseType = entity.getHouseType();
        if (HouseType != null) {
            stmt.bindString(3, HouseType);
        }
 
        String TagCode = entity.getTagCode();
        if (TagCode != null) {
            stmt.bindString(4, TagCode);
        }
 
        String PN1 = entity.getPN1();
        if (PN1 != null) {
            stmt.bindString(5, PN1);
        }
 
        String PN2 = entity.getPN2();
        if (PN2 != null) {
            stmt.bindString(6, PN2);
        }
 
        String Tag = entity.getTag();
        if (Tag != null) {
            stmt.bindString(7, Tag);
        }
 
        String TagPY = entity.getTagPY();
        if (TagPY != null) {
            stmt.bindString(8, TagPY);
        }
 
        String TagCategory = entity.getTagCategory();
        if (TagCategory != null) {
            stmt.bindString(9, TagCategory);
        }
 
        Integer SNum = entity.getSNum();
        if (SNum != null) {
            stmt.bindLong(10, SNum);
        }
 
        Integer RNum = entity.getRNum();
        if (RNum != null) {
            stmt.bindLong(11, RNum);
        }
 
        Double EstateAvgPriceSale = entity.getEstateAvgPriceSale();
        if (EstateAvgPriceSale != null) {
            stmt.bindDouble(12, EstateAvgPriceSale);
        }
 
        Double EstateAvgPriceRent = entity.getEstateAvgPriceRent();
        if (EstateAvgPriceRent != null) {
            stmt.bindDouble(13, EstateAvgPriceRent);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, HistoryHouseTag entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String UserId = entity.getUserId();
        if (UserId != null) {
            stmt.bindString(2, UserId);
        }
 
        String HouseType = entity.getHouseType();
        if (HouseType != null) {
            stmt.bindString(3, HouseType);
        }
 
        String TagCode = entity.getTagCode();
        if (TagCode != null) {
            stmt.bindString(4, TagCode);
        }
 
        String PN1 = entity.getPN1();
        if (PN1 != null) {
            stmt.bindString(5, PN1);
        }
 
        String PN2 = entity.getPN2();
        if (PN2 != null) {
            stmt.bindString(6, PN2);
        }
 
        String Tag = entity.getTag();
        if (Tag != null) {
            stmt.bindString(7, Tag);
        }
 
        String TagPY = entity.getTagPY();
        if (TagPY != null) {
            stmt.bindString(8, TagPY);
        }
 
        String TagCategory = entity.getTagCategory();
        if (TagCategory != null) {
            stmt.bindString(9, TagCategory);
        }
 
        Integer SNum = entity.getSNum();
        if (SNum != null) {
            stmt.bindLong(10, SNum);
        }
 
        Integer RNum = entity.getRNum();
        if (RNum != null) {
            stmt.bindLong(11, RNum);
        }
 
        Double EstateAvgPriceSale = entity.getEstateAvgPriceSale();
        if (EstateAvgPriceSale != null) {
            stmt.bindDouble(12, EstateAvgPriceSale);
        }
 
        Double EstateAvgPriceRent = entity.getEstateAvgPriceRent();
        if (EstateAvgPriceRent != null) {
            stmt.bindDouble(13, EstateAvgPriceRent);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public HistoryHouseTag readEntity(Cursor cursor, int offset) {
        HistoryHouseTag entity = new HistoryHouseTag( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // UserId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // HouseType
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // TagCode
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // PN1
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // PN2
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // Tag
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // TagPY
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // TagCategory
            cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9), // SNum
            cursor.isNull(offset + 10) ? null : cursor.getInt(offset + 10), // RNum
            cursor.isNull(offset + 11) ? null : cursor.getDouble(offset + 11), // EstateAvgPriceSale
            cursor.isNull(offset + 12) ? null : cursor.getDouble(offset + 12) // EstateAvgPriceRent
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, HistoryHouseTag entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setHouseType(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setTagCode(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setPN1(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setPN2(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setTag(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setTagPY(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setTagCategory(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setSNum(cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9));
        entity.setRNum(cursor.isNull(offset + 10) ? null : cursor.getInt(offset + 10));
        entity.setEstateAvgPriceSale(cursor.isNull(offset + 11) ? null : cursor.getDouble(offset + 11));
        entity.setEstateAvgPriceRent(cursor.isNull(offset + 12) ? null : cursor.getDouble(offset + 12));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(HistoryHouseTag entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(HistoryHouseTag entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(HistoryHouseTag entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
