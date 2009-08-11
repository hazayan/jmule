/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2009 JMule team ( jmule@jmule.org / http://jmule.org )
 *
 *  Any parts of this program derived from other projects, or contributed
 *  by third-party developers are copyrighted by their respective authors.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package org.jmule.core.configmanager.test;

import static org.jmule.core.utils.Convert.hexStringToByte;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jmule.core.configmanager.ConfigurationManagerException;
import org.jmule.core.configmanager.ConfigurationManagerFactory;
import org.jmule.core.configmanager.InternalConfigurationManager;
import org.jmule.core.edonkey.impl.UserHash;
import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.utils.Utils;
import org.junit.Before;
import org.junit.Test;

/**
 * Created on Aug 10, 2009
 * @author binary256
 * @author javajox
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2009/08/11 12:06:48 $
 */
public class ConfigurationManagerTest {

	private InternalConfigurationManager manager = (InternalConfigurationManager) ConfigurationManagerFactory.getInstance();
	
	@Before
	public void setUp() {
		manager.initialize();
		manager.start();
	}
	

	@Test
	public void testGetDownloadBandwidth() {
		try {
			manager.setDownloadBandwidth(100);
			assertEquals(100, manager.getDownloadBandwidth());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}	
		
	}

	@Test
	public void testGetDownloadLimit() {
		try {
			manager.setDownloadLimit(100);
			assertEquals(100, manager.getDownloadLimit());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		
		try {
			manager.setDownloadLimit(0);
			assertEquals(0, manager.getDownloadLimit());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		
		try {
			manager.setDownloadLimit("1234");
			assertEquals(1234, manager.getDownloadLimit());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		
		try {
			manager.setDownloadLimit(-1);
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
		try {
			manager.setDownloadLimit("-10");
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testGetNickName() {
		try {
			manager.setNickName("Bee");
			assertEquals("Bee", manager.getNickName());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		
		// the nickname's length can't be 0
		try {
			manager.setNickName("");
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		 
		try {
			byte[] array = new byte[65540];
			manager.setNickName(new String(array));
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
		// the nickname's length can't be more than 65535
		try {
			byte[] array = new byte[65535];
			manager.setNickName(new String(array));
			assertEquals(65535, manager.getNickName().length());
		} catch (ConfigurationManagerException e) {
			assertTrue(false);
		}
	}

	@Test
	public void testGetTCP() {
		try {
			manager.setTCP(0);
			assertEquals(0, manager.getTCP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		
		try {
			manager.setTCP(65535);
			assertEquals(65535, manager.getTCP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		
		try {
			manager.setTCP(-1);
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
		try {
			manager.setTCP(1000);
			assertEquals(1000, manager.getTCP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
	}

	@Test
	public void testGetUDP() {
		try {
			manager.setUDP(0);
			assertEquals(0, manager.getUDP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		
		try {
			manager.setUDP(65535);
			assertEquals(65535, manager.getUDP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		
		try {
			manager.setUDP(-1);
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
		try {
			manager.setUDP(2000);
			assertEquals(2000, manager.getUDP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
	}

	@Test
	public void testGetUploadBandwidth() {
		try {
			manager.setDownloadBandwidth(1000);
			assertEquals(1000, manager.getDownloadBandwidth());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		
		try {
			manager.setDownloadBandwidth(-1000);
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
		try {
			manager.setDownloadBandwidth(0);
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testGetUploadLimit() {
		try {
			manager.setDownloadLimit(256);		
			assertEquals(256, manager.getDownloadLimit());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		
		try {
			manager.setDownloadLimit(0);		
			assertEquals(0, manager.getDownloadLimit());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		
		try {
			manager.setDownloadLimit(-128);		
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testGetUserHash() {
		UserHash newHash = UserHash.genNewUserHash();
		try {
			((InternalConfigurationManager)(manager)).setUserHash(newHash.getAsString());
			assertEquals(newHash, manager.getUserHash());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		
		try {
			((InternalConfigurationManager)(manager)).setUserHash("d1xc");
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testLoad() {
		try {
			manager.load();
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
	}

	@Test
	public void testSave() {
		try {
			manager.save();
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
	}

	@Test
	public void testSetDownloadBandwidthLong() {
		try {
			manager.setDownloadBandwidth(-1000);
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
		try {
			manager.setDownloadBandwidth(0);
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
				
		try {
			manager.setDownloadBandwidth(500);
			assertEquals(500, manager.getDownloadBandwidth());
		} catch (ConfigurationManagerException e) {
			assertTrue(false);
		}
		
				
		
	}

	@Test
	public void testSetDownloadBandwidthString() {
		try {
			manager.setDownloadBandwidth("0_0");
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
		try {
			manager.setDownloadBandwidth("-1");
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
		try {
			manager.setDownloadBandwidth("2465");
			assertEquals(2465,manager.getDownloadBandwidth());
		} catch (ConfigurationManagerException e) {
			assertTrue(false);
		}
	}

	@Test
	public void testSetDownloadLimitLong() {
		try {
			manager.setDownloadLimit(0);
			assertEquals(0, manager.getDownloadLimit());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		
		try {
			manager.setDownloadLimit(500);
			assertEquals(500, manager.getDownloadLimit());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		
		try {
			manager.setDownloadLimit(-10);
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testSetDownloadLimitString() {
		try {
			manager.setDownloadLimit("0");
			assertEquals(0, manager.getDownloadLimit());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		
		try {
			manager.setDownloadLimit("4444");
			assertEquals(4444, manager.getDownloadLimit());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		
		try {
			manager.setDownloadLimit("-5550");
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testSetNickName() {
		try {
			manager.setNickName("test");
			assertEquals("test",manager.getNickName());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
	}

	@Test
	public void testSetTCPString() {
		try {
			manager.setTCP("111");
			assertEquals(111, manager.getTCP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		try {
			manager.setTCP("0");
			assertEquals(0, manager.getTCP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		try {
			manager.setTCP("65535");
			assertEquals(65535, manager.getTCP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		try {
			manager.setTCP("-1");
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		try {
			manager.setTCP("bad port ");
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
	}

	@Test
	public void testSetTCPInt() {
		try {
			manager.setTCP(666);
			assertEquals(666, manager.getTCP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		try {
			manager.setTCP(0);
			assertEquals(0, manager.getTCP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		try {
			manager.setTCP(65535);
			assertEquals(65535, manager.getTCP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		try {
			manager.setTCP(-1);
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testSetUDPString() {
		try {
			manager.setUDP("666");
			assertEquals(666, manager.getUDP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		try {
			manager.setUDP("0");
			assertEquals(0, manager.getUDP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		try {
			manager.setUDP("65535");
			assertEquals(65535, manager.getUDP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		try {
			manager.setUDP("-1");
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		try {
			manager.setUDP("4dd3zxcc ");
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testSetUDPInt() {
		try {
			manager.setUDP(364);
			assertEquals(364, manager.getUDP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		try {
			manager.setUDP(0);
			assertEquals(0, manager.getUDP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		try {
			manager.setUDP(65535);
			assertEquals(65535, manager.getUDP());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		try {
			manager.setUDP(-1);
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testSetUploadBandwidthLong() {
		try {
			manager.setUploadBandwidth(-2000);
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
		try {
			manager.setUploadBandwidth(0);
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
				
		try {
			manager.setUploadBandwidth(600);
			assertEquals(600, manager.getUploadBandwidth());
		} catch (ConfigurationManagerException e) {
			assertTrue(false);
		}
	}

	@Test
	public void testSetUploadBandwidthString() {
		try {
			manager.setUploadBandwidth("-20");
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
		try {
			manager.setUploadBandwidth("0");
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
		try {
			manager.setUploadBandwidth("11.223");
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
				
		try {
			manager.setUploadBandwidth("12");
			assertEquals(12, manager.getUploadBandwidth());
		} catch (ConfigurationManagerException e) {
			assertTrue(false);
		}
	}

	@Test
	public void testSetUploadLimitLong() {
		try {
			manager.setUploadLimit(0);
			assertEquals(0, manager.getUploadLimit());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		

		try {
			manager.setUploadLimit(-60);
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
		try {
			manager.setUploadLimit(1500);
			assertEquals(1500, manager.getUploadLimit());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		
	}

	@Test
	public void testSetUploadLimitString() {
		try {
			manager.setUploadLimit("0");
			assertEquals(0, manager.getUploadLimit());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		

		try {
			manager.setUploadLimit("-11");
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
		try {
			manager.setUploadLimit("a11");
			assertTrue(false);
		} catch (ConfigurationManagerException e) {
			assertTrue(true);
		}
		
		try {
			manager.setUploadLimit("2500");
			assertEquals(2500, manager.getUploadLimit());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
	}

	@Test
	public void testSetUDPEnabled() {
		try {
			manager.setUDPEnabled(false);
			assertFalse(manager.isUDPEnabled());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
		
	}

	@Test
	public void testIsUDPEnabled() {
		try {
			manager.setUDPEnabled(true);
			assertTrue(manager.isUDPEnabled());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
	}

	@Test
	public void testIsJKadAutoconnectEnabled() {
		try {
			manager.setAutoconnectJKad(true);
			assertTrue(manager.isJKadAutoconnectEnabled());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
	}

	@Test
	public void testSetAutoconnectJKad() {
		try {
			manager.setAutoconnectJKad(false);
			assertFalse(manager.isJKadAutoconnectEnabled());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
	}

	@Test
	public void testSetUpdateServerListAtConnect() {
		try {
			manager.setUpdateServerListAtConnect(true);
			assertTrue(manager.updateServerListAtConnect());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
	}

	@Test
	public void testUpdateServerListAtConnect() {
		try {
			manager.setUpdateServerListAtConnect(false);
			assertFalse(manager.updateServerListAtConnect());
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
	}

	@Test
	public void testGetJKadClientID() {
		try {
			String jkad_id = manager.getJKadClientID();
			if (jkad_id==null)
				fail("JKad id is null");
			assertTrue(true);
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
	}	
	@Test
	public void testSetJKadClientID() {
		Int128 new_id = new Int128(Utils.getRandomInt128());
		try {
			manager.setJKadClientID(new_id.toHexString());
			String jkad_id = manager.getJKadClientID();
			Int128 config_id = new Int128(hexStringToByte(jkad_id));
			assertEquals(new_id,config_id);
		} catch (ConfigurationManagerException e) {
			fail(e+"");
		}
	}

}
