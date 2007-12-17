/* JpaUtil.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Mon Dec 17 2007, Created by jeffliu
 }}IS_NOTE

 Copyright (C) 2006 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 }}IS_RIGHT
 */

package org.zkoss.zkplus.jpa;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.Sessions;

/**
 * This class is used to create and hold open EntityManagerFactory objects within a Java EE environment.
 * @author Jeff
 * 
 */
public class JpaUtil {

	private static final Log log = Log.lookup(JpaUtil.class);
	private static final String JPA_SESSION_MAP = "org.zkoss.zkplus.jpa.SessionMap";
	private static Map _emfMap;
	
	/*
	 * Put the EntityManagerFactories Map into Session scope
	 */
	static Map getSessionMap(){
		//exec.setAttribute(HIBERNATE_SESSION_MAP, map); // store in Execution attribute
		_emfMap = (Map)Sessions.getCurrent().getAttribute(JPA_SESSION_MAP);
		if(_emfMap ==null){
			_emfMap = new HashMap();
			Sessions.getCurrent().setAttribute(JPA_SESSION_MAP, _emfMap);
		}
		return _emfMap;
	}
	
	/*
	 * Clean the EntityManagerFactories Map
	 */
	static void cleanSessionMap(){
		_emfMap = null;
		Sessions.getCurrent().removeAttribute(JPA_SESSION_MAP);
	}
	
	/**
	 * Create or return the EntityManagerFactory for the specified persistence unit name.
	 * </br>*Notice:If the EntityManagerFactory with specified presistence unit is not created before, a new one will be created. 
	 * @param puName - Persistence unit name
	 * @return EntityManagerFactory
	 */
	public static EntityManagerFactory getEntityManagerFactory(String puName) {
		return initEntityManagerFactory(puName, null);
	}
	
	/**
	 * Create the EntityManagerFactory for the specified persistence unit and defined priorities.
	 * </br>*Notice: It always creates a new EntityManagerFactory.
	 * @param puName - Persistence unit name
	 * @param priority - Defined priorities
	 * @return EntityManagerFactory
	 */
	public static EntityManagerFactory getEntityManagerFactory(String puName, Map priority) {
		return initEntityManagerFactory(puName, priority);
	}
	
	/**
	 * Create the EntityManager for the specified persistence unit name.
	 * </br>*Notice: It always creates a new EntityManger
	 * @param puName - Persistence unit name
	 * @return EntityManager
	 */
	public static EntityManager getEntityManager(String puName) {
		return getEntityManagerFactory(puName).createEntityManager();
	}
	
	/**
	 Create the EntityManager for the specified persistence unit name and defined priorities.
	 * </br>*Notice: It always creates a new EntityManger
	 * @param puName - Persistence unit name
	 * @param priority - Defined priorities
	 * @return EntityManager
	 */
	public static EntityManager getEntiyManager(String puName, Map priority) {
		return getEntityManagerFactory(puName, priority).createEntityManager();
	}
	
	/*
	 * If EntityManagerFactory with persistence name puName is not found in Map, created a new one and return
	 * Else, return the EntityManagerFactory directly.
	 */
	/*package*/static EntityManagerFactory initEntityManagerFactory(String puName,Map priority) {
		EntityManagerFactory emf;
		if (priority == null) {
			emf = (EntityManagerFactory) getSessionMap().get(puName);
			if (emf == null) {
				emf = createEntityManagerFactory(puName, null);
				getSessionMap().put(puName, emf);
			}
		} else {
			emf = createEntityManagerFactory(puName, priority);
			getSessionMap().put(puName, emf);
		}
		return emf;
	}
	
	/*
	 * Clear up all EntityManagerFactory in map
	 */
	/*package*/static void cleanupAllEmf() {
		if (!getSessionMap().isEmpty()) {

			for (Iterator itr = getSessionMap().values().iterator(); itr.hasNext();) {
				((EntityManagerFactory) itr.next()).close();
			}
			cleanSessionMap();
		}
	}
	
	/*
	 * Util
	 */
	private static EntityManagerFactory createEntityManagerFactory(String puName, Map priority){
		EntityManagerFactory emf;
		try{
			if(priority ==null)
				emf = Persistence.createEntityManagerFactory(puName);
			else
				emf = Persistence.createEntityManagerFactory(puName, priority);
			log.info("EntityManagerFactory for: "+puName+" is created ");
		}catch(Exception ex){
			log.error("Initial EntityManagerFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
		getSessionMap().put(puName, emf);
		return emf;
	}

}
