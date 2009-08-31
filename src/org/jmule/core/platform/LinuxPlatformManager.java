/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2009 JMule Team ( jmule@jmule.org / http://jmule.org )
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
package org.jmule.core.platform;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.jmule.core.JMuleManager;

/**
 * Created on Aug 30, 2009
 * @author javajox
 * @version $Revision: 1.1 $
 * Last changed by $Author: javajox $ on $Date: 2009/08/31 17:26:28 $
 */
public class LinuxPlatformManager extends UnixPlatformManager {

	LinuxPlatformManager() {
		
	}

	public List<CPUCapabilities> getCPUCapabilities() throws PlatformManagerException {
		List<CPUCapabilities> cpus_capabilities = new ArrayList<CPUCapabilities>();
		try {
		   CPUCapabilities cpu_capabilities = null;
		   ProcessBuilder cat_proc_cpuinfo_cmd = new ProcessBuilder(new String[] {"cat", "/proc/cpuinfo"});
		   Process cat_proc_cpuinfo_cmd_running = cat_proc_cpuinfo_cmd.start();
		   int exit_status = cat_proc_cpuinfo_cmd_running.waitFor();
		   if( exit_status != 0 ) 
				   throw new PlatformManagerException("The OS process terminated abnormally, exit status : " + exit_status);
		   BufferedReader cat_proc_cpuinfo_cmd_result = new BufferedReader( new InputStreamReader( cat_proc_cpuinfo_cmd_running.getInputStream() ) );
		   String line;
		   while( ( line = cat_proc_cpuinfo_cmd_result.readLine() ) != null ) {
			     //System.out.println(line);
			     String[] splitted_line = line.split(":");
			     if( splitted_line.length == 2 ) {
				     String key    =  splitted_line[0].trim();
				     String value  =  splitted_line[1].trim();
			    	 	  if( key.equals("processor") ) {       
			    	 	        cpu_capabilities = new CPUCapabilities();
			    	 	        cpus_capabilities.add( cpu_capabilities );
			    	 	                                        cpu_capabilities.setNumber( value );
			    	 	  }
			    	 else if( key.equals("vendor_id") )         cpu_capabilities.setVendorId( value );
			    	 else if( key.equals("cpu family") )        cpu_capabilities.setFamily( value );
			    	 else if( key.equals("model") )             cpu_capabilities.setModel( value );
			    	 else if( key.equals("model name") )        cpu_capabilities.setModelName( value );
			    	 else if( key.equals("stepping") )          cpu_capabilities.setStepping( value );
			    	 else if( key.equals("cpu MHz") )           cpu_capabilities.setMHz( value );
			    	 else if( key.equals("cache size") )        cpu_capabilities.setCacheSize( value );
			    	 else if( key.equals("physical id") )       cpu_capabilities.setPhysicalId( value );
			    	 else if( key.equals("siblings") )          cpu_capabilities.setSiblings( value );
			    	 else if( key.equals("core id") )           cpu_capabilities.setCoreId( value );
			    	 else if( key.equals("cpu cores") )         cpu_capabilities.setNrOfCores( value );
			    	 else if( key.equals("apicid") )            cpu_capabilities.setApicid( value );
			    	 else if( key.equals("initial apicid") )    cpu_capabilities.setInitialApicid( value );
			    	 else if( key.equals("fdiv_bug") )          cpu_capabilities.setFdivBug( value.equals("no")?false:true );
			    	 else if( key.equals("hlt_bug") )           cpu_capabilities.setHltBug( value.equals("no")?false:true );
			    	 else if( key.equals("f00f_bug") )          cpu_capabilities.setF00fBug( value.equals("no")?false:true );
			    	 else if( key.equals("coma_bug") )          cpu_capabilities.setComaBug( value.equals("no")?false:true );
			    	 else if( key.equals("fpu") )               cpu_capabilities.setFpu( value.equals("no")?false:true );
			    	 else if( key.equals("fpu_exception") )     cpu_capabilities.setFpuException( value.equals("no")?false:true );
			    	 else if( key.equals("cpuid level") )       cpu_capabilities.setCpuidLevel( value );
			    	 else if( key.equals("wp") )                cpu_capabilities.setWp( value.equals("no")?false:true );
			    	 else if( key.equals("flags") )             cpu_capabilities.setFlags( value.split(" ") );
			    	 else if( key.equals("bogomips") )          cpu_capabilities.setBogomips( value );
			    	 else if( key.equals("clflush size") )      cpu_capabilities.setClflushSize( value );
			    	 else if( key.equals("power management") )  cpu_capabilities.setPowerManagerment( value );
			     } 
		   }
		}catch(Throwable cause) {
			throw new PlatformManagerException( cause );
		}
		return cpus_capabilities;
	}
	
	public void addToIPFilter(Object ip) throws PlatformManagerException {
		
	}

	public boolean isNativeAvailable(JMuleManager manager, String methodName) throws PlatformManagerException {

		return false;
	}


	public void removeFromIPFilter(Object ip) throws PlatformManagerException {
		
	}

}
