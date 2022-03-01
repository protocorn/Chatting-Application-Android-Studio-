package com.example.mainactivity.models;

public class GroupModel {
    String groupId,groupTitle,groupDesc,groupIcon,timestamp,createdBy,room_type;

       public GroupModel() {
       }

      public GroupModel(String groupId, String groupTitle, String groupDesc, String groupIcon, String timestamp, String createdBy,String room_type)
      {
        this.groupId = groupId;
        this.groupTitle = groupTitle;
        this.groupDesc = groupDesc;
        this.groupIcon = groupIcon;
        this.timestamp = timestamp;
        this.createdBy = createdBy;
        this.room_type=room_type;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getRoom_type() {
        return room_type;
    }

    public void setRoom_type(String room_type) {
        this.room_type = room_type;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public String getGroupDesc() {
        return groupDesc;
    }

    public void setGroupDesc(String groupDesc) {
        this.groupDesc = groupDesc;
    }

    public String getGroupIcon() {
        return groupIcon;
    }

    public void setGroupIcon(String groupIcon) {
        this.groupIcon = groupIcon;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCreatedBy(String uid) {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
