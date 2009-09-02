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
package org.jmule.core.edonkey.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.jmule.core.edonkey.ED2KFileLink;
import org.jmule.core.edonkey.ED2KLinkMalformedException;
import org.jmule.core.edonkey.FileHash;
import org.jmule.core.edonkey.PartHashSet;
import org.jmule.core.utils.Convert;
import org.junit.Test;

/**
 * Created on Sep 1, 2009
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2009/09/02 18:59:48 $
 */
public class ED2KFileLinkTest {

	@Test
	public void testGeneric() {
		String fileName = "jquery-1.3.2.min.js";
		long fileSize = 57254;
		FileHash hash = new FileHash("5F2DEDD3FA0E62B37D987D055FF16D1F");
		String str = "ed2k://|file|"+fileName+"|"+fileSize+"|"+hash+"|/";
		ED2KFileLink link = null;
		try {
			link = new ED2KFileLink(str);
		} catch (ED2KLinkMalformedException e) {
			fail(e+"");
		}
		
		assertEquals(fileName, link.getFileName());
		assertEquals(fileSize, link.getFileSize());
		assertEquals(hash, link.getFileHash());
	}
	
	@Test
	public void testPartHashSet() {
		String fileName = "linux-2.6.30.5.tar.bz2";
		long fileSize = 59427252;
		FileHash hash = new FileHash("5DAB0CDD691CECB21A23A8898A2513C7");
		
		PartHashSet hash_set = new PartHashSet(hash);
		hash_set.add(Convert.hexStringToByte("EB6F7A0CFEF22C907FF74658DF49CD31"));
		hash_set.add(Convert.hexStringToByte("C14D7F7AFCF68DD05CDEE7E35781E7B9"));
		hash_set.add(Convert.hexStringToByte("F9F70AAA59654CC30F8F55AFADBBDD40"));
		hash_set.add(Convert.hexStringToByte("46CE37AEBA15FA317DD0B0C5F5352793"));
		hash_set.add(Convert.hexStringToByte("7303D193268487C43BEED24ED38FFF96"));
		hash_set.add(Convert.hexStringToByte("6995A66BEF1E15EA146042D93B799A05"));
		hash_set.add(Convert.hexStringToByte("BCF58FF2037ED072C38F7FF394E422F5"));
		String root_hash = "JM3OTKN7KCPQDYU66JLTEVN7EVDV3627";
		
		String str = "ed2k://|file|linux-2.6.30.5.tar.bz2|59427252|5DAB0CDD691CECB21A23A8898A2513C7|p=EB6F7A0CFEF22C907FF74658DF49CD31:C14D7F7AFCF68DD05CDEE7E35781E7B9:F9F70AAA59654CC30F8F55AFADBBDD40:46CE37AEBA15FA317DD0B0C5F5352793:7303D193268487C43BEED24ED38FFF96:6995A66BEF1E15EA146042D93B799A05:BCF58FF2037ED072C38F7FF394E422F5|h=JM3OTKN7KCPQDYU66JLTEVN7EVDV3627|/";
		ED2KFileLink link = null;
		try {
			link = new ED2KFileLink(str);
		} catch (ED2KLinkMalformedException e) {
			fail(e+"");
		}
		
		assertEquals(fileName, link.getFileName());
		assertEquals(fileSize, link.getFileSize());
		assertEquals(hash, link.getFileHash());
		assertEquals(hash_set, link.getPartHashSet());
		assertEquals(root_hash, link.getRootHash());
	}
	
	@Test
	public void test2() {
		String str = "ed2k://|file|jquery-1.3.2.min.js|57254|5F2DeDD3FA0E62B37D987D055FF16D1F|h=HXE7PQTEF377ISBONDE5TX4HJP4Y6W6L|/";
		ED2KFileLink link = null;
		try {
			link = new ED2KFileLink(str);
		} catch (ED2KLinkMalformedException e) {
			fail(e+"");
		}
		System.out.println(link.toString());
	}
	
	@Test
	public void test3() {
		String str = "ed2k://|file|jquery-1.3.2.min.js|57254|5F2DEDD3FA0E62B37D987D055FF16D1F|h=HXE7PQTEF377ISBONDE5TX4HJP4Y6W6L|/|sources,111.111.111.111:6666|/";
		ED2KFileLink link = null;
		try {
			link = new ED2KFileLink(str);
		} catch (ED2KLinkMalformedException e) {
			fail(e+"");
		}
		System.out.println(link.toString());
	}

	@Test
	public void test4() {		
		String str = "ed2k://|file|jquery-1.3.2.min.js|57254|5F2DEDD3FA0E62B37D987D055FF16D1F|h=HXE7PQTEF377ISBONDE5TX4HJP4Y6W6L|/|sources,111.111.111.111:6666,222.222.222.222:111|/";
		ED2KFileLink link = null;
		try {
			link = new ED2KFileLink(str);
		} catch (ED2KLinkMalformedException e) {
			fail(e+"");
		}
		System.out.println(link);
	}

	@Test
	public void test5() {		
		String str = "ed2k://|file|jquery-1.3.2.min.js|57254|5F2DEDD3FA0E62B37D987D055FF16D1F|H=HXE7PQTEF377ISBONDE5TX4HJP4Y6W6L|s=http://www.test.com:111/jquery-1.3.2.min.js|/|sources,111.111.111.111:6666,222.222.222.522:1111|/";
		ED2KFileLink link = null;
		try {
			link = new ED2KFileLink(str);
		} catch (ED2KLinkMalformedException e) {
			fail(e+"");
		}
		System.out.println("Test5");
		System.out.println(link);
	}
	
}
