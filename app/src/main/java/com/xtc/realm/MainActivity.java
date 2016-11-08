package com.xtc.realm;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xtc.log.LogUtil;
import com.xtc.realm.demo1.Person;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    @Bind(R.id.text)
    TextView text;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Create the Realm instance
        realm = Realm.getDefaultInstance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close(); // Remember to close Realm when done.
    }

    @OnClick({R.id.insert, R.id.insertOrUpdate, R.id.update, R.id.query, R.id.delete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.insert:
                insert();
                break;
            case R.id.insertOrUpdate:
                insertOrUpdate();
                break;
            case R.id.update:
                break;
            case R.id.query:
                query();
                break;
            case R.id.delete:
                delete();
                break;
        }
    }

    Person person;

    private void insert() {
        // All writes must be wrapped in a transaction to facilitate safe multi threading
        // 调用 realm.beginTransaction()、realm.commitTransaction() 和 realm.cancelTransaction() 管理事务

        realm.beginTransaction();
        person = realm.createObject(Person.class, "Young Person");
        //person由Realm管理
        person.setId(1);
        person.setAge(14);
        realm.commitTransaction();
    }

    private void insertOrUpdate() {
        //realm.executeTransaction() 方法，它会自动处理写入事物的开始和提交，并在错误发生时取消写入事物。
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Person person = new Person();
                person.setId(2);
                person.setName("Senior Person");
                person.setAge(99);

                Person dbPerson = realm.copyToRealm(person);
                //dbPerson由Realm管理

//                realm.insertOrUpdate(person);
            }
        });
    }

    private void delete() {
        // Delete all persons
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(Person.class);
            }
        });
    }

    private void query() {
        LogUtil.d(TAG, "Number of persons: " + realm.where(Person.class).count());

        RealmResults<Person> results = realm.where(Person.class).findAll();

        LogUtil.d(TAG, "Size of result set: " + results.size());

        for (Person person : results) {
            LogUtil.d(TAG, person.toString());
        }
    }

}
