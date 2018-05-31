package io.yfam.yagily.dto;

import java.util.ArrayList;
import java.util.List;

public class Project {
    private int _id;
    private String _key;
    private String _name;
    private String _logoUrl;
    private Integer _createUserId;
    private List<Member> _memberList;

    public Project() {
        _memberList = new ArrayList<>();
    }

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        _id = id;
    }

    public String getKey() {
        return _key;
    }

    public void setKey(String key) {
        _key = key;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public String getLogoUrl() {
        return _logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        _logoUrl = logoUrl;
    }

    public Integer getCreateUserId() {
        return _createUserId;
    }

    public void setCreateUserId(Integer createUserId) {
        _createUserId = createUserId;
    }

    public List<Member> getMemberList() {
        return _memberList;
    }

    public void setMemberList(List<Member> memberList) {
        _memberList = memberList;
    }
}
