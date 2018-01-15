package com.sensetime.motionsdksamples.Common;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.widget.Toast;

import com.sensetime.motionsdksamples.R;

import java.util.ArrayList;

import static com.sensetime.motionsdksamples.Utils.UniqueId.getStrUid;

/**
 * Created by lyt on 2017/10/10.
 */

public class PersonDao {
    private static final String TAG = "PersonsDao";

    // 列定义
    private final String[] ORDER_COLUMNS = new String[]{"Id",
            "Uid", "Name", "Pinyin", "Age", "Gender", "Registered",
            "Feature"};
    private Context mContext;
    private PersonDbHelper mPersonDbHelper;

    public PersonDao(Context context) {
        mContext = context;
        mPersonDbHelper = new PersonDbHelper(context);
    }

    /**
     * 判断表中是否有数据
     */
    public boolean isDataExist() {
        int count = 0;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = mPersonDbHelper.getReadableDatabase();

            //debug
            //mPersonDbHelper.dropTable(db);

            // select count(Id) from Persons
            cursor = db.query(PersonDbHelper.TABLE_NAME, new String[]{"COUNT(Id)"}, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            if (count > 0) return true;
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return false;
    }

    /**
     * 初始化数据
     */
    public void initTable() {
        SQLiteDatabase db = null;

        try {
            db = mPersonDbHelper.getWritableDatabase();
            db.beginTransaction();

            /*
            db.execSQL("insert into " + PersonDbHelper.TABLE_NAME + " (Id, CustomName, PersonPrice, Country) values (1, '李佳', 36, '男')");
            db.execSQL("insert into " + PersonDbHelper.TABLE_NAME + " (Id, CustomName, PersonPrice, Country) values (2, '若依', 20, '女')");
            db.execSQL("insert into " + PersonDbHelper.TABLE_NAME + " (Id, CustomName, PersonPrice, Country) values (3, '杨幂', 30, '女')");
            db.execSQL("insert into " + PersonDbHelper.TABLE_NAME + " (Id, CustomName, PersonPrice, Country) values (4, 'Bor', 300, 'USA')");
            db.execSQL("insert into " + PersonDbHelper.TABLE_NAME + " (Id, CustomName, PersonPrice, Country) values (5, 'Arc', 600, 'China')");
            db.execSQL("insert into " + PersonDbHelper.TABLE_NAME + " (Id, CustomName, PersonPrice, Country) values (6, 'Doom', 200, 'China')");
            */

            ContentValues cv = new ContentValues();
            cv.put("Id", 1);
            cv.put("Uid", getStrUid());
            cv.put("Name", "王一");
            cv.put("Pinyin", "wangyi");
            cv.put("Age", 30);
            cv.put("Gender", "男");
            cv.put("Registered", 0);
            /*
            String filename = Environment.getExternalStorageDirectory().toString();
            filename = filename + File.separator + "lijia1";
            filename = filename + File.separator + "wangyi.jpg";
            Bitmap bitmap = BitmapFactory.decodeFile(filename);
            cv.put("Picture", PicUtils.bitmapToBytes(mContext, bitmap));//图片转为二进制
            */
            db.insert(PersonDbHelper.TABLE_NAME, null, cv);

            cv.clear();
            //cv.put("Id", 2);
            cv.put("Uid", "2");
            cv.put("Name", "赵二");
            cv.put("Pinyin", "zhaoer");
            cv.put("Age", 20);
            cv.put("Gender", "女");
            cv.put("Registered", 0);
            //cv.put("Picture", PicUtils.bitmabToBytes(mContext));//图片转为二进制
            /*
            filename = Environment.getExternalStorageDirectory().toString();
            filename = filename + File.separator + "lijia1";
            filename = filename + File.separator + "zhaoer.jpg";
            bitmap = BitmapFactory.decodeFile(filename);
            cv.put("Picture", PicUtils.bitmapToBytes(mContext, bitmap));//图片转为二进制
            */
            db.insert(PersonDbHelper.TABLE_NAME, null, cv);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    /**
     * 执行自定义SQL语句
     */
    public void execSQL(String sql) {
        SQLiteDatabase db = null;

        try {
            if (sql.contains("select")) {
                Toast.makeText(mContext, R.string.strUnableSql, Toast.LENGTH_SHORT).show();
            } else if (sql.contains("insert") || sql.contains("update") || sql.contains("delete")) {
                db = mPersonDbHelper.getWritableDatabase();
                db.beginTransaction();
                db.execSQL(sql);
                db.setTransactionSuccessful();
                Toast.makeText(mContext, R.string.strSuccessSql, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(mContext, R.string.strErrorSql, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    /**
     * 查询数据库中所有数据
     */
    public List<Person> getAllPersons() {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = mPersonDbHelper.getReadableDatabase();
            // select * from Persons
            cursor = db.query(PersonDbHelper.TABLE_NAME, ORDER_COLUMNS, null, null, null, null, null);

            if (cursor.getCount() > 0) {
                List<Person> personList = new ArrayList<Person>(cursor.getCount());
                while (cursor.moveToNext()) {
                    personList.add(parsePerson(cursor));
                }
                return personList;
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return null;
    }

    /**
     * 新增一条数据
     */
    public boolean insertPerson(ContentValues cv) {
        SQLiteDatabase db = null;

        try {
            db = mPersonDbHelper.getWritableDatabase();
            db.beginTransaction();

            // insert into Persons(Id, CustomName, PersonPrice, Country) values (7, "Jne", 700, "China");
            /*
            ContentValues cv = new ContentValues();
            cv.put("Id", 7);
            cv.put("CustomName", "Jne");
            cv.put("PersonPrice", 700);
            cv.put("Country", "China");
            */
            db.insertOrThrow(PersonDbHelper.TABLE_NAME, null, cv);
            db.setTransactionSuccessful();
            return true;
        } catch (SQLiteConstraintException e) {
            Toast.makeText(mContext, "主键重复", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
        return false;
    }

    /**
     * 删除一条数据  此处删除Id为7的数据
     */
    public boolean deletePersonByUid(long uid) {
        SQLiteDatabase db = null;

        try {
            db = mPersonDbHelper.getWritableDatabase();
            db.beginTransaction();

            // delete from Persons where Id = 7
            db.delete(PersonDbHelper.TABLE_NAME, "Uid = ?", new String[]{String.valueOf(uid)});
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
        return false;
    }

    /**
     * 修改一条数据  此处将Id为6的数据的PersonPrice修改了800
     */
    public boolean updatePersonByUid(ContentValues cv) {
        SQLiteDatabase db = null;
        String strUid = cv.getAsString("Uid");
        try {
            db = mPersonDbHelper.getWritableDatabase();
            db.beginTransaction();

            // update Persons set PersonPrice = 800 where Id = 6
            //ContentValues cv = new ContentValues();
            //cv.put("PersonPrice", 800);
            db.update(PersonDbHelper.TABLE_NAME,
                    cv,
                    "Uid = ?",
                    new String[]{strUid});
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }

        return false;
    }

    /**
     * 数据查询  此处将用户名为name的信息提取出来
     */
    public List<Person> getPersonByName(String name) {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = mPersonDbHelper.getReadableDatabase();

            // select * from Persons where CustomName = 'Bor'
            cursor = db.query(PersonDbHelper.TABLE_NAME,
                    ORDER_COLUMNS,
                    "Name = ?",
                    new String[]{name},
                    null, null, null);

            if (cursor.getCount() > 0) {
                List<Person> personList = new ArrayList<Person>(cursor.getCount());
                while (cursor.moveToNext()) {
                    Person person = parsePerson(cursor);
                    personList.add(person);
                }
                return personList;
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return null;
    }

    /**
     * 数据查询  此处将用户名uid的信息提取出来
     */
    public List<Person> getPersonByUid(String strUid) {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = mPersonDbHelper.getReadableDatabase();

            // select * from Persons where CustomName = 'Bor'
            cursor = db.query(PersonDbHelper.TABLE_NAME,
                    ORDER_COLUMNS,
                    "Uid = ?",
                    new String[]{strUid},
                    null, null, null);

            /*
            String table =  PersonDbHelper.TABLE_NAME ;
            String[] columns = ORDER_COLUMNS;
            String selection = null ;
            String[] selectionArgs = null;
            String groupBy = "CustomerName" ;
            String having = "SUM(OrderPrice)=500" ;
            String orderBy = "CustomerName" ;
            */


            if (cursor.getCount() > 0) {
                List<Person> personList = new ArrayList<Person>(cursor.getCount());
                while (cursor.moveToNext()) {
                    Person person = parsePerson(cursor);
                    personList.add(person);
                }
                return personList;
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return null;
    }

    /**
     * 数据查询  此处将用户名uid的信息提取出来
     */
    public List<Person> getPersonById(int id) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String strId = String.valueOf(id);

        try {
            db = mPersonDbHelper.getReadableDatabase();

            // select * from Persons where CustomName = 'Bor'
            cursor = db.query(PersonDbHelper.TABLE_NAME,
                    ORDER_COLUMNS,
                    "Id = ?",
                    new String[]{String.valueOf(strId)},
                    null, null, null);

            /*
            String table =  PersonDbHelper.TABLE_NAME ;
            String[] columns = ORDER_COLUMNS;
            String selection = null ;
            String[] selectionArgs = null;
            String groupBy = "CustomerName" ;
            String having = "SUM(OrderPrice)=500" ;
            String orderBy = "CustomerName" ;
            */


            if (cursor.getCount() > 0) {
                List<Person> personList = new ArrayList<Person>(cursor.getCount());
                while (cursor.moveToNext()) {
                    Person person = parsePerson(cursor);
                    personList.add(person);
                }
                return personList;
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return null;
    }

    /**
     * 统计查询  此处查询Country为China的用户总数
     */
    public int getChinaCount() {
        int count = 0;

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = mPersonDbHelper.getReadableDatabase();
            // select count(Id) from Persons where Country = 'China'
            cursor = db.query(PersonDbHelper.TABLE_NAME,
                    new String[]{"COUNT(Id)"},
                    "Country = ?",
                    new String[]{"China"},
                    null, null, null);

            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return count;
    }

    /**
     * 比较查询  此处查询单笔数据中Person Id最高的
     */
    public Person getMaxPersonId() {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = mPersonDbHelper.getReadableDatabase();
            // select Id, CustomName, Max(PersonPrice) as PersonPrice, Country from Persons
            cursor = db.query(PersonDbHelper.TABLE_NAME, new String[]{"Id", "Name", "Max(Id) as Id" }, null, null, null, null, null);

            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    return parsePerson(cursor);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return null;
    }

    /**
     * 将查找到的数据转换成Person类
     */
    private Person parsePerson(Cursor cursor) {
        Person person = new Person();
        person.mDid = (cursor.getInt(cursor.getColumnIndex("Id")));
        person.mStrUid = (cursor.getString(cursor.getColumnIndex("Uid")));
        person.mName = (cursor.getString(cursor.getColumnIndex("Name")));
        person.mPinyin = (cursor.getString(cursor.getColumnIndex("Pinyin")));
        person.mAge = (cursor.getInt(cursor.getColumnIndex("Age")));
        person.mGender = (cursor.getString(cursor.getColumnIndex("Gender")));
        person.mRegistered = (cursor.getInt(cursor.getColumnIndex("Registered")));
        person.mFeatureByte = (cursor.getBlob(cursor.getColumnIndex("Feature")));
        //person.mPictureByte = (cursor.getBlob(cursor.getColumnIndex("Picture")));
        //byte[] in=cur.getBlob(cur.getColumnIndex("express_img"));
        //String filename = Environment.getExternalStorageDirectory().toString();
        //filename = filename + File.separator + "lijia1";
        //filename = filename + File.separator + person.mPinyin + ".jpg";
        //person.mPictureBitmap = BitmapFactory.decodeFile(filename);
        return person;
    }

    public boolean deleteAll() {
        SQLiteDatabase db = null;

        try {
            db = mPersonDbHelper.getWritableDatabase();
            db.beginTransaction();

            // delete from Persons where Id = 7
            db.delete(PersonDbHelper.TABLE_NAME, "Uid > ?", new String[]{String.valueOf(0)});
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
        return false;
    }
}
