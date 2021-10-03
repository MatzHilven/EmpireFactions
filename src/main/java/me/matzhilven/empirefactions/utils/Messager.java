package me.matzhilven.empirefactions.utils;

import me.matzhilven.empirefactions.EmpireFactions;

import java.util.List;

public class Messager {

    //  General
    public static final String INVALID_PERMISSION = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.invalid-permission");
    public static final String INVALID_SENDER = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.invalid-sender");
    public static final String INVALID_EMPIRE = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.invalid-empire");
    public static final String INVALID_FACTION = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.invalid-faction");
    public static final String INVALID_TARGET = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.invalid-target");
    public static final String INVALID_TARGET_SELF = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.invalid-target-self");
    public static final String INVALID_CORE_TYPE = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.invalid-coretype");
    public static final String INVALID_COLOR = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.invalid-color");
    public static final String INVALID_NUMBER = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.invalid-number");
    public static final String MAX_ADMINS = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.max-admins");
    public static final String PROMOTE_ADMIN = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.promote-admin");

    //  Usage
    public static final List<String> USAGE_EMPIRE_COMMAND = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getStringList("messages.usage-empire");
    public static final List<String> USAGE_FACTION_COMMAND = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getStringList("messages.usage-faction");
    public static final String USAGE_INFO = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.usage-info");
    public static final String USAGE_INFO_FACTION = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.usage-info-faction");
    public static final String USAGE_CREATE = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.usage-create");
    public static final String USAGE_CREATE_FACTION = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.usage-create-faction");
    public static final String USAGE_SET_DESCRIPTION = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.usage-set-description");
    public static final String USAGE_SET_DESCRIPTION_FACTION = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.usage-set-description-faction");
    public static final String USAGE_SET_COLOR = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.usage-set-color");
    public static final String USAGE_PROMOTE = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.usage-promote");
    public static final String USAGE_DEMOTE = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.usage-demote");
    public static final String USAGE_GET_CORE = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.usage-get-core");
    public static final String USAGE_INVITE = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.usage-invite");
    public static final String USAGE_ACCEPT = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.usage-accept");
    public static final String USAGE_DISBAND_FACTION = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.usage-disband-faction");

    // Empire
    public static final List<String> EMPIRE_INFO = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getStringList("messages.empire-info");
    public static final String CREATE_SUCCESS_EMPIRE = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.create-success-empire");
    public static final String NOT_IN_EMPIRE = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.not-in-empire");
    public static final String PROMOTE_SUCCESS = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.promote");
    public static final String PROMOTE_SUCCESS_TARGET = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.promote-target");
    public static final String DEMOTE_SUCCESS = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.demote");
    public static final String DEMOTE_SUCCESS_TARGET = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.demote-target");
    public static final String SET_DESCRIPTION = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.set-description");
    public static final String SET_COLOR = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.set-color");
    public static final String JOINED = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.joined");
    public static final String RECEIVED_CORE = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.received-core");
    public static final String PLACED_CORE = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.placed-core");
    public static final String BROKEN_CORE = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.broken-core");

    // Faction
    public static final List<String> FACTION_INFO = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getStringList("messages.faction-info");
    public static final String CREATE_SUCCESS_FACTION = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.create-success-faction");
    public static final String DISBAND_SUCCESS_FACTION = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.disband-success-faction");
    public static final String NOT_IN_FACTION = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.not-in-faction");
    public static final String ALREADY_IN_FACTION = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.already-in-faction");
    public static final String INVITED = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.invited");
    public static final String INVITED_TARGET = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.invited-target");
    public static final String JOINED_FACTION = EmpireFactions.getPlugin(EmpireFactions.class).getMessages().getString("messages.joined-faction");


}
