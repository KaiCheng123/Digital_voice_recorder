package com.example.a31372.digital_voice_recorder.util;

public class RecordingItem {
    String mName; // file name
    String mFilePath; //file path
    int mId; //id in database
    String mLength; // length of recording in seconds
    String mTime; // date/time of the recording

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        mFilePath = filePath;
    }

    public String getLength() {
        return mLength;
    }

    public void setLength(int length) {
        int hh = length / 3600;
        int mm = length / 60;
        int ss = length % 60;
        if (hh < 10){
            if (mm < 10){
                if (ss < 10){
                    mLength = "0" + hh + ":" + "0" + mm + ":"+"0" + ss;
                }else {
                    mLength = "0" + hh + ":" + "0" + mm + ":"+ ss;
                }
            }else {
                if (ss < 10){
                    mLength = "0" + hh + ":" + mm + ":"+"0" + ss;
                }else {
                    mLength = "0" + hh + ":" + mm + ":"+ ss;
                }
            }
        }else {
            if (mm < 10) {
                if (ss < 10) {
                    mLength = hh + ":" + "0" + mm + ":" + "0" + ss;
                } else {
                    mLength = hh + ":" + "0" + mm + ":" + ss;
                }
            } else {
                if (ss < 10) {
                    mLength = hh + ":" + mm + ":" + "0" + ss;
                } else {
                    mLength = hh + ":" + mm + ":" + ss;
                }
            }
        }
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }
}
