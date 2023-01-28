package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    int customGroupCount=1;
   int messageId=1;

    HashMap<String, User> user=new HashMap<>();   // map having key-->mobile number ,, value-->user  --->create user

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
//        this.customGroupCount = 0;
//        this.messageId = 0;
    }

    public String createUser(String name, String mobile)
    {
        //If the mobile number exists in database, throw "User already exists" exception
        //Otherwise, create the user and return "SUCCESS"
        if(user.containsKey(mobile))
        {
            return "User already exists";
        }

        userMobile.add(mobile);
        User newUser=new User(name, mobile);
        user.put(mobile, newUser);
        return "SUCCESS";
    }

    public Group createGroup(List<User> users)
    {
        User admin=users.get(0);
        String groupName="";
        if(users.size()==2)
        {
            groupName=users.get(1).getName();
        }
        else
        {
            groupName="Group "+customGroupCount;
            customGroupCount++;
        }

        Group newGroup=new Group(groupName, users.size());
        groupUserMap.put(newGroup, users);
        adminMap.put(newGroup, admin);
        return newGroup;
    }

    public int createMessage(String content)
    {
        Message message=new Message(messageId, content);
        messageId++;
        return message.getId();
    }

    public int sendMessage(Message message, User sender, Group group)
    {
        if(!adminMap.containsKey(group))
        {
            return -1;
        }

        List<User> members=new ArrayList<>();
        members=groupUserMap.get(group);
        if(!members.contains(sender))
        {
            return -2;
        }

        senderMap.put(message, sender);
        return message.getId();
    }

    public String changeAdmin(User approver, User user, Group group)
    {
        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "Approver does not have rights" if the approver is not the current admin of the group
        //Throw "User is not a participant" if the user is not a part of the group
        //Change the admin of the group to "user" and return "SUCCESS". Note that at one time there is only one admin and the admin rights are transferred from approver to user.

        //private HashMap<Group, User> adminMap;
        if(!adminMap.containsKey(group))
        {
            return "Group does not exist";
        }

        User admin=adminMap.get(group);
        String adminName=admin.getName();
        String adminMobile=admin.getMobile();

        if(!adminName.equals(approver.getName()) && !adminMobile.equals(approver.getMobile()))
        {
            return "Approver does not have rights";
        }

        //private HashMap<Group, List<User>> groupUserMap;
        List<User> participants=new ArrayList<>();
        participants=groupUserMap.get(group);
        if(!participants.contains(user))
        {
            return "User is not a participant";
        }

        // Since all edge cases have passed, so we can change admin to
        //private HashMap<Group, User> adminMap
        adminMap.get(group).setName(user.getName());
        adminMap.get(group).setMobile(user.getMobile());
        return "SUCCESS";
    }
}
