package com.example.answer;

import android.app.Application;

import org.xutils.DbManager;
import org.xutils.x;

/**
 * Created by Administrator on 2016/9/23.
 */
public class MyApplication extends Application {
    // 在application的onCreate中初始化
    /**
     * 一个App中操作数据库的地方有很多，就像是否登录一样，有些地方必须登录后才能操作，那么肯定是全局变量，所以，必须将数据库的初始化放在Application
     * ，且必须提供获取数据库的方法，使得在应用程序的任何地方都可以直接获取数据库，并操作数据库，不然重复的获取与释放只能增加内存无谓的消耗。
     */
    private DbManager.DaoConfig daoConfig;

    public DbManager.DaoConfig getDaoConfig() {
        return daoConfig;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);//Xutils初始化
        x.Ext.setDebug(true);
        //String strPath = getFilesDir().getAbsolutePath() + "/"; // 正式数据库存储地址
        //String strPath = Environment.getExternalStorageDirectory() + "/";     // 测试数据库存储地址
        daoConfig = new DbManager.DaoConfig()
                .setDbName("db1")//创建数据库的名称
                .setDbVersion(1)//数据库版本号
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                        // TODO: ...
                        // db.addColumn(...);
                        // db.dropTable(...);
                        // ...
                    }
                });//数据库更新操作

        /*DbManager db = x.getDb(daoConfig);
        Question question=new Question();
        question.setCorrectAnswer("A");
        question.setCollect(true);
        question.setOptionA("aaa");
        question.setTopic("qvnale?");
        question.setTopicId("00211");
        try {
            db.save(question);
        } catch (DbException e) {
            e.printStackTrace();
        }*/
    }
}
