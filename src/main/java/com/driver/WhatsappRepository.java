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
    private int customGroupCount;
    private int messageId;

    HashMap<String, User> user=new HashMap<>();   // map having key-->mobile number ,, value-->user  --->create user

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
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
        this.messageId++;
        Message message = new Message(messageId, content, new Date());
        return this.messageId;
    }

    public int sendMessage(Message message, User sender, Group group)
    {
        if(!adminMap.containsKey(group))
        {
            return -1;
        }

        List<User> members=new ArrayList<>();
        members=groupUserMap.get(group);
        for(User eachMember:members)
        {
            String name= eachMember.getName();
            String mobile=eachMember.getMobile();
            if(!name.equals(sender.getName()) && !mobile.equals(sender.getMobile()))
            {
                return -2;
            }
        }

        senderMap.put(message, sender);
        return message.getId();
    }

    public String changeAdmin(User approver, User user, Group group)
    {
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
