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
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WebApp;

/**
 * This class is used to create and hold open EntityManagerFactory objects
 * within a Java EE environment.
 * 
 * @author Jeff
 * @since 3.1.0
 */
public class JpaUtil {

	private static final Log log = Log.lookup(JpaUtil.class);

	public static final String CONFIG = "JPA.PersistenceUnitName";

	public static final String JPA_EMF_MAP = "org.zkoss.zkplus.jpa.EmfMap";

	public static final String JPA_EM_MAP = "org.zkoss.zkplus.jpa.EmMap";

	/*
	 * Get the EntityManagerFactories Map from Execution scope
	 */
	static Map getEmfMap() {
		return getMapThreadLocal(JPA_EMF_MAP);
	}

	/*
	 * Clean the EntityManagerFactories Map
	 */
	static void cleanEmfMap() {
		Executions.getCurrent().removeAttribute(JPA_EMF_MAP);
	}

	/*
	 * Get the EntityManagers Map from Execution scope
	 */
	static Map getEmMap() {
		return getMapThreadLocal(JPA_EM_MAP);
	}

	/*
	 * Clean the EntityManagersMap
	 */
	static void cleanEmMap() {
		Executions.getCurrent().removeAttribute(JPA_EM_MAP);
	}

	private static Map getMapThreadLocal(String key) {
		Map map = (Map) Executions.getCurrent().getAttribute(key);
		if (map == null) {
			map = new HashMap();
			Executions.getCurrent().setAttribute(key, map);
		}
		return map;
	}

	/**
	 * Create or return the EntityManager by peference in zk.xml
	 * 
	 * <p>
	 * In WEB-INF/zk.xml, add following lines:
	 * 
	 * <pre><code>
	 *&lt;preference&gt;
	 *	&lt;name&gt;JPA.PersistenceUnitName&lt;/name&gt;
	 *	&lt;value&gt;PERSISTENCE_UNIT_NAME&lt;/value&gt;
     *&lt;/preference&gt;
	 * </code></pre>
	 * 
	 * </p>
	 * 
	 * @return EntityManager
	 */
	public static EntityManagerFactory getEntityManagerFactory() {
		return initEntityManagerFactory(null, null);
	}

	/**
	 * Create or return the EntityManagerFactory for the specified persistence
	 * unit name. </br>*Notice:If the EntityManagerFactory with specified
	 * presistence unit is not created before, a new one will be created.
	 * 
	 * @param puName -
	 *            Persistence unit name
	 * @return EntityManagerFactory
	 */
	public static EntityManagerFactory getEntityManagerFactory(String puName) {
		return initEntityManagerFactory(puName, null);
	}

	/**
	 * Create the EntityManagerFactory for the specified persistence unit and
	 * defined priorities. </br>*Notice: It always creates a new
	 * EntityManagerFactory.
	 * 
	 * @param puName -
	 *            Persistence unit name
	 * @param priority -
	 *            Defined priorities
	 * @return EntityManagerFactory
	 */
	public static EntityManagerFactory getEntityManagerFactory(String puName,
			Map priority) {
		return initEntityManagerFactory(puName, priority);
	}

	/**
	 * Create the EntityManager by peference in zk.xml
	 *  <p>
	 * In WEB-INF/zk.xml, add following lines:
	 * 
	 * <pre><code>
	 *&lt;preference&gt;
	 *	&lt;name&gt;JPA.PersistenceUnitName&lt;/name&gt;
	 *	&lt;value&gt;PERSISTENCE_UNIT_NAME&lt;/value&gt;
     *&lt;/preference&gt;
	 * </code></pre>
	 * 
	 * </p>
	 * @return EntityManager
	 */
	public static EntityManager getEntityManager() {
		return initEntityManger(null, null);
	}

	/**
	 * Create the EntityManager for the specified persistence unit name. </br>*Notice:If
	 * the EntityManagerFactory with specified presistence unit is not created
	 * before, a new one will be created.
	 * 
	 * @param puName -
	 *            Persistence unit name
	 * @return EntityManager
	 */
	public static EntityManager getEntityManager(String puName) {
		return initEntityManger(puName, null);
	}

	/**
	 * Create the EntityManager for the specified persistence unit name and
	 * defined priorities. </br>*Notice: It always creates a new EntityManger
	 * 
	 * @param puName -
	 *            Persistence unit name
	 * @param priority -
	 *            Defined priorities
	 * @return EntityManager
	 */
	public static EntityManager getEntiyManager(String puName, Map priority) {
		return initEntityManger(puName, priority);
	}

	/*
	 * If EntityManagerFactory with persistence name puName is not found in Map,
	 * created a new one and return Else, return the EntityManagerFactory
	 * directly.
	 */
	/* package */static EntityManagerFactory initEntityManagerFactory(String puName, Map priority) {
		EntityManagerFactory emf;
		if (priority == null) {
			emf = (EntityManagerFactory) getEmfMap().get(puName);
			if (emf == null) {
				emf = createEntityManagerFactory(puName, null);
				getEmfMap().put(puName, emf);
			}
		} else {
			emf = createEntityManagerFactory(puName, priority);
			getEmfMap().put(puName, emf);
		}
		return emf;
	}

	/*
	 * If EntityManager with persistence name puName is not found in Map,
	 * created a new one and return Else, return the EntityManager directly.
	 */
	/* package */static EntityManager initEntityManger(String puName,Map priority) {
		EntityManager em;
		if (priority == null) {
			em = (EntityManager) getEmMap().get(puName);
			if (em == null) {
				em = createEntityManager(puName, null);
				getEmMap().put(puName, em);
			}
		} else {
			em = createEntityManager(puName, priority);
			getEmMap().put(puName, em);
		}
		return em;
	}

	/*
	 * Clear up all EntityManagerFactory in map
	 */
	/* package */static void cleanupAllEmf() {
		if (!getEmfMap().isEmpty()) {

			for (Iterator itr = getEmfMap().values().iterator(); itr.hasNext();) {
				((EntityManagerFactory) itr.next()).close();
			}
			cleanEmfMap();
		}
	}

	/*
	 * Util
	 */
	/*
	 * Create the EntityManagerFactory by persistence unit name. If persistence
	 * unit name not given, using the one which is defined in zk.xml
	 */
	private static EntityManagerFactory createEntityManagerFactory(
			String puName, Map priority) {
		puName = getPersistenceUnitName(puName);
		EntityManagerFactory emf;
		try {
			if (priority == null) {
				emf = Persistence.createEntityManagerFactory(puName);
				log.info("EntityManagerFactory for: " + puName + " is created ");
			} else
				emf = Persistence.createEntityManagerFactory(puName, priority);
		} catch (Exception ex) {
			log.error("Initial EntityManagerFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
		getEmfMap().put(puName, emf);
		return emf;
	}

	/*
	 * Create the EntityManager by persistence unit name. If persistence unit
	 * name not given, using the one which is defined in zk.xml
	 */
	private static EntityManager createEntityManager(String puName, Map priority) {
		puName = getPersistenceUnitName(puName);
		EntityManager em = createEntityManagerFactory(puName, priority).createEntityManager();
		return em;
	}

	private static WebApp getWebApp() {
		WebApp app = null;
		final Execution exec = Executions.getCurrent();
		if (exec != null) {
			final Desktop desktop = exec.getDesktop();
			if (desktop != null) {
				app = desktop.getWebApp();
			}
		}
		return app;
	}

	private static String getPersistenceUnitName(String pu) {
		if (pu == null || pu.equals("")) {
			/*
			 * Create EntityManagerFactory by preference in zk.xml
			 */
			final org.zkoss.zk.ui.util.Configuration config = getWebApp().getConfiguration();
			pu = config.getPreference(CONFIG, null);
		}
		return pu;
	}

}
