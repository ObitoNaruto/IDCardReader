package com.mobile.android.idcard.sdk.idcard;


import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class IdCardInfo implements Parcelable{

    /**
     * 身份证id
     */
    @SerializedName("id")
    private String mId;

    /**
     * 姓名
     */
    @SerializedName("name")
    private String mName;

    /**
     * 性别
     */
    @SerializedName("sex")
    private String mSex;

    /**

     * 民族
     */
    @SerializedName("nation")
    private String mNation;

    /**
     * 出生日期
     */
    @SerializedName("birthday")
    private String  mBirthDay;

    /**
     * 住址
     */
    @SerializedName("address")
    private String mAddress;

    /**
     * 签发单位
     */
    @SerializedName("issueUnit")
    private String mIssueingUnit;

    /**
     * 起始日期
     */
    @SerializedName("startDate")
    private String mStartDate;

    /**
     * 失效日期
     */
    @SerializedName("endDate")
    private String mEndDate;

    @SerializedName("pic")
    private Bitmap mPicBitmap;


    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getSex() {
        return mSex;
    }

    public void setSex(String mSex) {
        this.mSex = mSex;
    }

    public String getNation() {
        return mNation;
    }

    public void setNation(String mNation) {
        this.mNation = mNation;
    }

    public String getBirthDay() {
        return mBirthDay;
    }

    public void setBirthDay(String mBirthDay) {
        this.mBirthDay = mBirthDay;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public String getIssueingUnit() {
        return mIssueingUnit;
    }

    public void setIssueingUnit(String mIssueingUnit) {
        this.mIssueingUnit = mIssueingUnit;
    }

    public String getStartDate() {
        return mStartDate;
    }

    public void setStartDate(String mStartDate) {
        this.mStartDate = mStartDate;
    }

    public String getEndDate() {
        return mEndDate;
    }

    public void setEndDate(String mEndDate) {
        this.mEndDate = mEndDate;
    }

    public Bitmap getPicBitmap() {
        return mPicBitmap;
    }

    public void setPicBitmap(Bitmap mPicBitmap) {
        this.mPicBitmap = mPicBitmap;
    }

    public IdCardInfo() {
    }


    public IdCardInfo(Parcel in) {
        readFromParcel(in);
    }

    public static final Creator<IdCardInfo> CREATOR = new Creator<IdCardInfo>() {
        @Override
        public IdCardInfo createFromParcel(Parcel in) {
            return new IdCardInfo(in);
        }

        @Override
        public IdCardInfo[] newArray(int size) {
            return new IdCardInfo[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mName);
        dest.writeString(mSex);
        dest.writeString(mNation);
        dest.writeString(mBirthDay);
        dest.writeString(mAddress);
        dest.writeString(mIssueingUnit);
        dest.writeString(mStartDate);
        dest.writeString(mEndDate);
        dest.writeParcelable(mPicBitmap, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private void readFromParcel(Parcel in) {
        mId = in.readString();
        mName = in.readString();
        mSex = in.readString();
        mNation = in.readString();
        mBirthDay = in.readString();
        mAddress = in.readString();
        mIssueingUnit = in.readString();
        mStartDate = in.readString();
        mEndDate = in.readString();
        mPicBitmap = in.readParcelable(Bitmap.class.getClassLoader());
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
