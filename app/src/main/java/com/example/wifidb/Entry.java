package com.example.wifidb;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "Entries")
public class Entry {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "sno")
    private int sno;
    private String bssid;
    private int dbm;
    private double X;
    private double Y;
    private double Z;

    public Entry () {

    }

    public Entry(int sno, String bssid, int dbm, double x, double y, double z) {
        this.sno = sno;
        this.bssid = bssid;
        this.dbm = dbm;
        X = x;
        Y = y;
        Z = z;
    }

    public String toString() {
        return bssid+"\t\t\t\tdBm:"+dbm+" X:"+X+" Y:"+Y+" Z:"+Z+" ";
    }

    public void setSno(int sno) {
        this.sno = sno;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public void setDbm(int dbm) {
        this.dbm = dbm;
    }

    public void setX(double x) {
        X = x;
    }

    public void setY(double y) {
        Y = y;
    }

    public void setZ(double z) {
        Z = z;
    }

    public int getSno() {
        return sno;
    }

    public String getBssid() {
        return bssid;
    }

    public int getDbm() {
        return dbm;
    }

    public double getX() {
        return X;
    }

    public double getY() {
        return Y;
    }

    public double getZ() {
        return Z;
    }
}
