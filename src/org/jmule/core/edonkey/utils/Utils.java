/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2008 JMule team ( jmule@jmule.org / http://jmule.org )
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
package org.jmule.core.edonkey.utils;

import static org.jmule.core.edonkey.E2DKConstants.PeerFeatures.AICHVer;
import static org.jmule.core.edonkey.E2DKConstants.PeerFeatures.AcceptCommentVer;
import static org.jmule.core.edonkey.E2DKConstants.PeerFeatures.DataCompVer;
import static org.jmule.core.edonkey.E2DKConstants.PeerFeatures.ExtendedRequestsVer;
import static org.jmule.core.edonkey.E2DKConstants.PeerFeatures.MultiPacket;
import static org.jmule.core.edonkey.E2DKConstants.PeerFeatures.NoViewSharedFiles;
import static org.jmule.core.edonkey.E2DKConstants.PeerFeatures.PeerCache;
import static org.jmule.core.edonkey.E2DKConstants.PeerFeatures.SourceExchange1Ver;
import static org.jmule.core.edonkey.E2DKConstants.PeerFeatures.SupportPreview;
import static org.jmule.core.edonkey.E2DKConstants.PeerFeatures.SupportSecIdent;
import static org.jmule.core.edonkey.E2DKConstants.PeerFeatures.UDPVer;
import static org.jmule.core.edonkey.E2DKConstants.PeerFeatures.UnicodeSupport;

import java.util.Hashtable;
import java.util.Map;

import org.jmule.core.edonkey.E2DKConstants.PeerFeatures;

/**
 * Created on Dec 24, 2008
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2009/05/09 16:41:26 $
 */
public class Utils {

	public static int MiscOptions1ToInt( Map<PeerFeatures, Integer> clientFeatures) {
		int misc_optins1 = 0;
		
		misc_optins1 |= (byte)(clientFeatures.get(AICHVer) << 29);
		misc_optins1 |= (int)(clientFeatures.get(UnicodeSupport) << 28);
		misc_optins1 |= (int)(clientFeatures.get(UDPVer) << 24);
		misc_optins1 |= (int)(clientFeatures.get(DataCompVer) << 20);
		misc_optins1 |= (int)(clientFeatures.get(SupportSecIdent) << 16);
		misc_optins1 |= (int)(clientFeatures.get(SourceExchange1Ver) << 12);
		misc_optins1 |= (int)(clientFeatures.get(ExtendedRequestsVer) << 8);
		misc_optins1 |= (int)(clientFeatures.get(AcceptCommentVer) << 4);
		misc_optins1 |= (byte)(clientFeatures.get(PeerCache) << 3);
		misc_optins1 |= (byte)(clientFeatures.get(NoViewSharedFiles) << 2);
		misc_optins1 |= (byte)(clientFeatures.get(MultiPacket) << 1);
		misc_optins1 |= (byte)(clientFeatures.get(SupportPreview) << 0);
		
		return misc_optins1;
	}
	
	public static Map<PeerFeatures,Integer> IntToMiscOptions1(int rawData) {
		Map<PeerFeatures,Integer> result = new Hashtable<PeerFeatures,Integer>();
		
		result.put(AICHVer, (rawData >> 29) & 0x07);
		result.put(UnicodeSupport, (rawData >> 28) & 0x01);
		result.put(UDPVer, (rawData >> 24) & 0x0f);
		result.put(DataCompVer, (rawData >> 20) & 0x0f);
		result.put(SupportSecIdent, (rawData >> 16) & 0x0f);
		result.put(SourceExchange1Ver, (rawData >> 12) & 0x0f);
		result.put(ExtendedRequestsVer,(rawData >>  8) & 0x0f);
		result.put(AcceptCommentVer, (rawData >>  4) & 0x0f);
		result.put(PeerCache, (rawData >>  3) & 0x01);
		result.put(NoViewSharedFiles, (rawData >>  2) & 0x01);
		result.put(MultiPacket, (rawData >>  1) & 0x01);
		result.put(SupportPreview, (rawData >>  0) & 0x01);
		
		return result;
	}
	
}
