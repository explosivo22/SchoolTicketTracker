package com.ocboe.tech.schooltickettracker;

import android.util.Log;

public class Properties {
	
	private static String ipAddress;
	private static int port;
	private static String page;
	private static String username;
	private static String name;
	private static String email;
	private static String techLevel;
	private static String maintLevel;
	private static String reqLevel;
	private static String school;
	private static String room;

	/**
	 * @param ipAddress The IP Address to the server
	 */
	public static void setIPAddress(String ipAddress){
		Properties.ipAddress = ipAddress;
	}
	
	/**
	 * @return IPAddress
	 */
	public static String getIPAddress(){
		return Properties.ipAddress;
	}
	
	/**
	 * @param port The port that the server listens on.  Defaults to port 80
	 */
	public static void setPort(int port){
		Properties.port = port;
	}
	
	/**
	 * @return port
	 */
	public static int getPort(){
		return Properties.port;
	}
	
	/**
	 * @param page The page to make the request to.
	 */
	public static void setPage(String page){
		Properties.page = page;
	}
	
	/**
	 * @return page
	 */
	public static String getPage(){
		return Properties.page;
	}
	
	/**
	 * @return URL
	 */
	public static String getURL() {
		return "http://" + Properties.ipAddress + ":" + Properties.port + "/" + Properties.page;
	}
	
	/**
	 * @param username The username of the user that logged in
	 */
	public static void setUsername(String username){
		Properties.username = username;
	}
	
	/**
	 * @return username
	 */
	public static String getUsername(){
		return Properties.username;
	}
	
	/**
	 * @param email The name of the user that logged in
	 */
	public static void setEmail(String email){
		Properties.email = email;
	}
	
	/**
	 * @return email
	 */
	public static String getEmail(){
		return Properties.email;
	}

	/**
	 * @param name The name of the user that logged in
	 */
	public static void setName(String name){
		Properties.name = name;
	}

	/**
	 * @return name
	 */
	public static String getName(){
		return Properties.name;
	}
	
	/**
	 * @param techLevel The tech level of the user that logged in
	 */
	public static void setTechLevel(String techLevel){
		Properties.techLevel = techLevel;
	}
	
	/**
	 * @return techLevel
	 */
	public static String getTechLevel(){
		return Properties.techLevel;
	}
	
	/**
	 * @param maintLevel The maintenance level of the user that logged in
	 */
	public static void setMaintLevel(String maintLevel){
		Properties.maintLevel = maintLevel;
	}
	
	/**
	 * @return
	 */
	public static String getMaintLevel(){
		return Properties.maintLevel;
	}
	
	/**
	 * @param reqLevel requisition level of user
	 */
	public static void setReqLevel(String reqLevel){
		Properties.reqLevel = reqLevel;
	}
	
	/**
	 * @return reqLevel returns the requisition level of the user
	 */
	public static String getReqLevel(){
		return Properties.reqLevel;
	}
	
	/**
	 * @param school The school of the user that logged in
	 */
	public static void setSchool(String school){
		Properties.school = school;
	}
	
	/**
	 * @return school
	 */
	public static String getSchool(){
		return Properties.school;
	}
	
	/**
	 * @param room The room of the user that logged in
	 */
	public static void setRoom(String room){
		Properties.room = room;
	}
	
	/**
	 * @return room
	 */
	public static String getRoom(){
		return Properties.room;
	}

}
