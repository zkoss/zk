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
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WebApp;

/**
 * This class is used to create and hold open EntityManagerFactory objects
 * within a Java EE environment.
 * <p>Applicable to EJB version 3.2.ga or later</p>
 * @author Jeff
 * @since 3.0.2
 */
public class JpaUtil {

	private static final Log log = Log.lookup(JpaUtil.class);

	public static final String CONFIG = "JpaUtil.PersistenceUnitName";

	public static final String JPA_EMF_MAP = "org.zkoss.zkplus.jpa.EmfMap";

	public static final String JPA_EM_MAP = "org.zkoss.zkplus.jpa.EmMap";

	/*
	 * Get the EntityManagerFactories Map from WebApp scope
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, EntityManagerFactory> getEmfMap() {
		Map map = (Map) getWebApp().getAttribute(JPA_EMF_MAP);
		if (map == null) {
			map = new HashMap();
			getWebApp().setAttribute(JPA_EMF_MAP, map);
		}
		return map;
	}

	/*
	 * Get the EntityManagers Map from Execution scope
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, EntityManager> getEmMap() {
		Execution current = Executions.getCurrent();
		Map map = (Map) current.getAttribute(JPA_EM_MAP);
		if (map == null) {
			map = new HashMap();
			Executions.getCurrent().setAttribute(JPA_EM_MAP, map);
		}
		return map;
	}

	/**
	 * Create or return the default EntityManagerFactory as defined in zk.xml.
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
	 * @return EntityManagerFactory
	 */
	public static EntityManagerFactory getEntityManagerFactory() {
		return initEntityManagerFactory(null, null);
	}

	/**
	 * Create or return the EntityManagerFactory for the specified persistence
	 * unit name. </br>*Notice:If the EntityManagerFactory with specified
	 * persistence unit is not created yet, a new one will be created.
	 * 
	 * @param puName Persistence unit name
	 * @return EntityManagerFactory
	 */
	public static EntityManagerFactory getEntityManagerFactory(String puName) {
		return initEntityManagerFactory(puName, null);
	}

	/**
	 * Create the EntityManagerFactory for the specified persistence unit and
	 * defined properties. </br>*Notice: It always creates a new
	 * EntityManagerFactory unless properties is null.
	 * 
	 * @param puName Persistence unit name
	 * @param properties Defined priorities
	 * @return EntityManagerFactory
	 */
	public static EntityManagerFactory getEntityManagerFactory(String puName,
	Map properties) {
		return initEntityManagerFactory(puName, properties);
	}

	/**
	 * Returns an EntityManager of the default EntityManagerFactory as defined in zk.xml in an Execution scope.
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
	 * @return EntityManager
	 */
	public static EntityManager getEntityManager() {
		return initEntityManger(null, null);
	}

	/**
	 * Returns or create an EntityManager for the specified persistence unit name. </br>*Notice:If
	 * the EntityManagerFactory with specified presistence unit is not created
	 * before, a new one will be created automatically.
	 * <p>The EntityManager get by this method is guaranteed to be the same within one Execution for the
	 * specified persistence unit name.</p>
	 * 
	 * @param puName -
	 *            Persistence unit name
	 * @return EntityManager
	 */
	public static EntityManager getEntityManager(String puName) {
		return initEntityManger(puName, null);
	}

	/**
	 * Closes the EntityManager of the default EntityManagerFactory as 
	 * defined in zk.xml in an Execution scope. It is equals to 
	 * closeEntityManager(null).
	 * <p>
	 * In WEB-INF/zk.xml, add following lines:
	 * 
	 * <pre><code>
	 *&lt;preference&gt;
	 *	&lt;name&gt;JPA.PersistenceUnitName&lt;/name&gt;
	 *	&lt;value&gt;PERSISTENCE_UNIT_NAME&lt;/value&gt;
     *&lt;/preference&gt;
	 * </code></pre>
	 * @since 3.0.7
	 */
	public static void closeEntityManager() {
		closeEntityManager(null);
	}

	/**
	 * Closes the EntityManager of the EntityManagerFactory of the specified puName; 
	 * @since 3.0.7
	 */
	public static void closeEntityManager(String puName) {
		EntityManager em = getEmMap().remove(getPersistenceUnitName(puName));
		if (em != null && em.isOpen()) em.close();
	}
	
	/**
	 * Returns an EntityManager for the specified persistence unit name and
	 * defined properties. </br>*Notice: It always creates a new EntityManagerFactory
	 * and thus a new EntityManager unless properties is null.
	 * 
	 * @param puName -
	 *            Persistence unit name
	 * @param properties -
	 *            Defined priorities
	 * @return EntityManager
	 */
	public static EntityManager getEntiyManager(String puName, Map properties) {
		return initEntityManger(puName, properties);
	}

	/*
	 * If EntityManagerFactory with persistence name puName is not found in Map,
	 * created a new one and return; or return a new created EntityManagerFactory
	 * directly.
	 */
	private static EntityManagerFactory initEntityManagerFactory(String puName, Map properties) {
		EntityManagerFactory emf;
		if (properties == null) {
			emf = getEmfMap().get(puName);
			if (emf == null) {
				emf = createEntityManagerFactory(puName, null);
				getEmfMap().put(puName, emf);
			}
		} else {
			emf = createEntityManagerFactory(puName, properties);
			getEmfMap().put(puName, emf);
		}
		return emf;
	}

	/*
	 * If EntityManager with persistence name puName is not found in Map,
	 * created a new one; or return the existing EntityManager of this execution directly.
	 */
	private static EntityManager initEntityManger(String puName,Map properties) {
		EntityManager em;
		if (properties == null) {
			em = getEmMap().get(puName);
			if (em == null) {
				em = createEntityManager(puName, null);
				getEmMap().put(puName, em);
			}
		} else {
			em = createEntityManager(puName, properties);
			getEmMap().put(puName, em);
		}
		return em;
	}

	/*
	 * Create the EntityManagerFactory by persistence unit name. If persistence
	 * unit name not given, using the one which is defined in zk.xml
	 */
	private static EntityManagerFactory createEntityManagerFactory(
	String puName, Map properties) {
		puName = getPersistenceUnitName(puName);
		EntityManagerFactory emf;
		try {
			if (properties == null) {
				emf = Persistence.createEntityManagerFactory(puName);
				log.info("EntityManagerFactory for: " + puName + " is created ");
			} else {
				emf = Persistence.createEntityManagerFactory(puName, properties);
				log.info("EntityManagerFactory for: " + puName + " with properties "+properties + " is created ");
			}
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
	private static EntityManager createEntityManager(String puName, Map properties) {
		puName = getPersistenceUnitName(puName);
		EntityManager em = initEntityManagerFactory(puName, properties).createEntityManager();
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
		if (pu == null) {
			throw new UiException("Forget to specify the preference of "+CONFIG+" in WEB-INF/zk.xml?");
		}
		return pu;
	}
}
