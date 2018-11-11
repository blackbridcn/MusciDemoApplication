package com.music.javabean;

import java.io.Serializable;

public class MusicData implements Serializable {

    private static final long serialVersionUID = -8659667166655945241L;
    // music id
    private int musicID;
    // file name 全名
    private String fileName;
    // music name
    private String musicName;
    // music total duration
    private int musicDuration;
    // music artist
    private String musicArtist;
    // music album
    private String musicAlbum;
    // [本地]专辑ID
    private long albumId;
    // [在线]专辑封面路径
    private String coverPath;

    // music year
    private String musicYear;
    // file type mp3/avi
    private String fileType;
    // file size
    private String fileSize;
    // file path 歌曲资源存放路径
    private String dataFilePath;

    //歌曲类型：本地 1;网络 2; 未知:0;
    private int sourceType;
    //lrc file path
    private String lrcPath;
    //0:表示没有找到歌词； 1：表示有歌词
    private int hasLrc;

    public interface sourceType {
        int LOCAL = 1;
        int ONLINE = 2;
    }

    public int getMusicID() {
        return musicID;
    }

    public void setMusicID(int musicID) {
        this.musicID = musicID;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public int getMusicDuration() {
        return musicDuration;
    }

    public void setMusicDuration(int musicDuration) {
        this.musicDuration = musicDuration;
    }

    public String getMusicArtist() {
        return musicArtist;
    }

    public void setMusicArtist(String musicArtist) {
        this.musicArtist = musicArtist;
    }

    public String getMusicAlbum() {
        return musicAlbum;
    }

    public void setMusicAlbum(String musicAlbum) {
        this.musicAlbum = musicAlbum;
    }

    public String getMusicYear() {
        return musicYear;
    }

    public void setMusicYear(String musicYear) {
        this.musicYear = musicYear;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getDataFilePath() {
        return dataFilePath;
    }

    public void setDataFilePath(String filePath) {
        this.dataFilePath = filePath;
    }

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    public String getLrcPath() {
        return lrcPath;
    }

    public void setLrcPath(String lrcPath) {
        this.lrcPath = lrcPath;
    }

    public boolean hasLrc() {
        return hasLrc > 0 ? true : false;
    }

    public void setHasLrc(boolean hasLrc) {
        this.hasLrc = hasLrc ? 1 : 0;
    }


    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    @Override
    public String toString() {
        return "MusicData{" +
                ", fileName='" + fileName + '\'' +
                ", musicName='" + musicName + '\'' +
                ", musicAlbum='" + musicAlbum + '\'' +
                ", albumId=" + albumId +
                ", lrcPath='" + lrcPath + '\'' +
                ", hasLrc=" + hasLrc +
                '}';
    }
}
