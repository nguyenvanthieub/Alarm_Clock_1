package com.example.framgia.alarmclock.data.controller;

import com.example.framgia.alarmclock.data.Constants;
import com.example.framgia.alarmclock.data.model.Alarm;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by framgia on 12/07/2016.
 */
public class AlarmRepository {
    private static Realm mRealm = Realm.getDefaultInstance();

    public static RealmResults<Alarm> getAllAlarms() {
        return mRealm.where(Alarm.class).findAll();
    }

    public static Alarm getAlarmById(int id) {
        return mRealm.where(Alarm.class).equalTo(Constants.ID_FIELD, id).findFirst();
    }

    public static void updateAlarm(final Alarm alarm) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mRealm.copyToRealmOrUpdate(alarm);
            }
        });
    }

    public static void deleteAlarm(final Alarm alarm) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                alarm.deleteFromRealm();
            }
        });
    }

    public static void deleteAlarmById(final int id) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmObject result = realm.where(Alarm.class).equalTo(Constants.ID_FIELD, id)
                        .findFirst();
                result.deleteFromRealm();
            }
        });
    }

    public static void deleteAll() {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mRealm.delete(Alarm.class);
            }
        });
    }

    public static int getNextId() {
        return getSize() == 0 ? 1 :
                mRealm.where(Alarm.class).findAll().max(Constants.ID_FIELD).intValue() + 1;
    }

    public static int getSize() {
        return mRealm.where(Alarm.class).findAll().size();
    }
}
