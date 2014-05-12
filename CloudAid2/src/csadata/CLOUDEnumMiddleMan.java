package csadata;

import java.util.HashMap;

import usdl.constants.enums.CLOUDEnum;

public class CLOUDEnumMiddleMan {
	
	HashMap<String, String> QuantMiddleMan;
	HashMap<String,String> QualMiddleMan;
	
	public HashMap<String, String> getQuantMiddleMan() {
		return QuantMiddleMan;
	}

	public HashMap<String, String> getQualMiddleMan() {
		return QualMiddleMan;
	}
	
	public CLOUDEnumMiddleMan()
	{
		QuantMiddleMan = new HashMap<String,String>();
		QualMiddleMan = new HashMap<String,String>();
		populate();
	}
	
	
	//let's populate our MiddleMan - KEY (user string concept) - VALUE (cloud concept)
	private void populate() {
		
		//Qualitative Features
		QualMiddleMan.put("api", CLOUDEnum.API.getConceptString());
		QualMiddleMan.put("commandline", CLOUDEnum.COMMAND_LINE.getConceptString());
		QualMiddleMan.put("console", CLOUDEnum.CONSOLE.getConceptString());
		QualMiddleMan.put("gui", CLOUDEnum.GUI.getConceptString());
		QualMiddleMan.put("web", CLOUDEnum.WEB.getConceptString());
		QualMiddleMan.put("interface", CLOUDEnum.OTHER_INTERFACE.getConceptString());
		QualMiddleMan.put("consistency", CLOUDEnum.CONSISTENCY.getConceptString());
		QualMiddleMan.put("durability", CLOUDEnum.DURABILITY.getConceptString());
		QualMiddleMan.put("performance", CLOUDEnum.PERFORMANCE.getConceptString());
		QualMiddleMan.put("reliability", CLOUDEnum.RELIABILITY.getConceptString());
		QualMiddleMan.put("scalability", CLOUDEnum.SCALABILITY.getConceptString());
		QualMiddleMan.put("security", CLOUDEnum.SECURITY.getConceptString());
		QualMiddleMan.put("ssl", CLOUDEnum.SSL.getConceptString());
		QualMiddleMan.put("encryption", CLOUDEnum.ENCRYPTION.getConceptString());
		QualMiddleMan.put("location", CLOUDEnum.LOCATION.getConceptString());
		QualMiddleMan.put("monitoring", CLOUDEnum.MONITORING.getConceptString());
		QualMiddleMan.put("platform", CLOUDEnum.PLATFORM.getConceptString());
		QualMiddleMan.put("replication", CLOUDEnum.REPLICATION.getConceptString());
		QualMiddleMan.put("computinginstance", CLOUDEnum.COMPUTINGINTANCE.getConceptString());
		QualMiddleMan.put("cputype", CLOUDEnum.CPUTYPE.getConceptString());
		QualMiddleMan.put("graphicalcard", CLOUDEnum.GRAPHICALCARD.getConceptString());
		QualMiddleMan.put("loadbalancing", CLOUDEnum.LOADBALANCING.getConceptString());
		QualMiddleMan.put("memoryallocation", CLOUDEnum.MEMORYALLOCATION.getConceptString());
		QualMiddleMan.put("storagetype", CLOUDEnum.STORAGETYPE.getConceptString());
		QualMiddleMan.put("failover", CLOUDEnum.FAILOVER.getConceptString());
		QualMiddleMan.put("developercenter", CLOUDEnum.DEVELOPERCENTER.getConceptString());
		QualMiddleMan.put("forum", CLOUDEnum.FORUM.getConceptString());
		QualMiddleMan.put("livechat", CLOUDEnum.LIVECHAT.getConceptString());
		QualMiddleMan.put("manual", CLOUDEnum.MANUAL.getConceptString());
		QualMiddleMan.put("support24_7", CLOUDEnum.SUPPORT_24_7.getConceptString());
		QualMiddleMan.put("supportteam", CLOUDEnum.SUPPORTTEAM.getConceptString());
		QualMiddleMan.put("videos", CLOUDEnum.VIDEOS.getConceptString());
		QualMiddleMan.put("other_support", CLOUDEnum.OTHER_SUPPORT.getConceptString());
		QualMiddleMan.put("opensource", CLOUDEnum.OPENSOURCE.getConceptString());
		QualMiddleMan.put("proprietary", CLOUDEnum.PROPRIETARY.getConceptString());
		QualMiddleMan.put("arch32bit", CLOUDEnum.BIT32.getConceptString());
		QualMiddleMan.put("arch64bit", CLOUDEnum.BIT64.getConceptString());
		QualMiddleMan.put("embedded", CLOUDEnum.EMBEDDED.getConceptString());
		QualMiddleMan.put("mobile", CLOUDEnum.MOBILE.getConceptString());
		QualMiddleMan.put("windows", CLOUDEnum.WINDOWS.getConceptString());
		QualMiddleMan.put("unix", CLOUDEnum.UNIX.getConceptString());
		QualMiddleMan.put("realtime", CLOUDEnum.REALTIME.getConceptString());
		QualMiddleMan.put("backup_recovery", CLOUDEnum.BACKUP_RECOVERY.getConceptString());
		QualMiddleMan.put("redundancy", CLOUDEnum.REDUNDANCY.getConceptString());
		QualMiddleMan.put("publicip", CLOUDEnum.PUBLICIP.getConceptString());
		QualMiddleMan.put("elasticip", CLOUDEnum.ELASTICIP.getConceptString());
		QualMiddleMan.put("ipv4", CLOUDEnum.IPV4.getConceptString());
		QualMiddleMan.put("ipv6", CLOUDEnum.IPV6.getConceptString());
		QualMiddleMan.put("protocol", CLOUDEnum.PROTOCOL.getConceptString());
		QualMiddleMan.put("language", CLOUDEnum.LANGUAGE.getConceptString());
		QualMiddleMan.put("messageprotocol", CLOUDEnum.MESSAGEPROTOCOL.getConceptString());
		QualMiddleMan.put("messagetype", CLOUDEnum.MESSAGETYPE.getConceptString());
		QualMiddleMan.put("feature", CLOUDEnum.FEATURE.getConceptString());
		
		
		//Quantitative Features
		QuantMiddleMan.put("availability", CLOUDEnum.AVAILABILITY.getConceptString());
		QuantMiddleMan.put("cachesize", CLOUDEnum.CACHESIZE.getConceptString());
		QuantMiddleMan.put("cpuspeed", CLOUDEnum.CPUSPEED.getConceptString());
		QuantMiddleMan.put("datainexternal", CLOUDEnum.DATAINEXTERNAL.getConceptString());
		QuantMiddleMan.put("dataininternal", CLOUDEnum.DATAININTERNAL.getConceptString());
		QuantMiddleMan.put("dataoutexternal", CLOUDEnum.DATAOUTEXTERNAL.getConceptString());
		QuantMiddleMan.put("dataoutinternal", CLOUDEnum.DATAOUTINTERNAL.getConceptString());
		QuantMiddleMan.put("dataprocessed", CLOUDEnum.DATAPROCESSED.getConceptString());
		QuantMiddleMan.put("disksize", CLOUDEnum.DISKSIZE.getConceptString());
		QuantMiddleMan.put("filesize", CLOUDEnum.FILESIZE.getConceptString());
		QuantMiddleMan.put("memorysize", CLOUDEnum.MEMORYSIZE.getConceptString());
		QuantMiddleMan.put("networkdelay", CLOUDEnum.NETWORKDELAY.getConceptString());
		QuantMiddleMan.put("networkinternalbandwidth", CLOUDEnum.NETWORKINTERNALBANDWIDTH.getConceptString());
		QuantMiddleMan.put("networklatency", CLOUDEnum.NETWORKLATENCY.getConceptString());
		QuantMiddleMan.put("networkpublicbandwidth", CLOUDEnum.NETWORKPUBLICBANDWIDTH.getConceptString());
		QuantMiddleMan.put("storagecapacity", CLOUDEnum.STORAGECAPACITY.getConceptString());
		QuantMiddleMan.put("traffic", CLOUDEnum.TRAFFIC.getConceptString());
		QuantMiddleMan.put("transferrate", CLOUDEnum.TRANSFERRATE.getConceptString());
		QuantMiddleMan.put("apicalls", CLOUDEnum.APICALLS.getConceptString());
		QuantMiddleMan.put("applications", CLOUDEnum.APPLICATIONS.getConceptString());
		QuantMiddleMan.put("copyrequests", CLOUDEnum.COPYREQUESTS.getConceptString());
		QuantMiddleMan.put("cpucores", CLOUDEnum.CPUCORES.getConceptString());
		QuantMiddleMan.put("cpuflop", CLOUDEnum.CPUFLOP.getConceptString());
		QuantMiddleMan.put("deleterequests", CLOUDEnum.DELETEREQUESTS.getConceptString());
		QuantMiddleMan.put("getrequests", CLOUDEnum.GETREQUESTS.getConceptString());
		QuantMiddleMan.put("httprequests", CLOUDEnum.HTTPREQUEST.getConceptString());
		QuantMiddleMan.put("httpsrequests", CLOUDEnum.HTTPSREQUEST.getConceptString());
		QuantMiddleMan.put("iooperations", CLOUDEnum.IOOPERATIONS.getConceptString());
		QuantMiddleMan.put("listrequests", CLOUDEnum.LISTREQUESTS.getConceptString());
		QuantMiddleMan.put("postrequests", CLOUDEnum.POSTREQUESTS.getConceptString());
		QuantMiddleMan.put("putrequests", CLOUDEnum.PUTREQUESTS.getConceptString());
		QuantMiddleMan.put("queries", CLOUDEnum.QUERIES.getConceptString());
		QuantMiddleMan.put("readsrequests", CLOUDEnum.READSREQUESTS.getConceptString());
		QuantMiddleMan.put("records", CLOUDEnum.RECORDS.getConceptString());
		QuantMiddleMan.put("transactions", CLOUDEnum.TRANSACTIONS.getConceptString());
		QuantMiddleMan.put("users", CLOUDEnum.USERS.getConceptString());
		QuantMiddleMan.put("websites", CLOUDEnum.WEBSITES.getConceptString());
		QuantMiddleMan.put("writesrequests", CLOUDEnum.WRITESREQUESTS.getConceptString());
		QuantMiddleMan.put("numberofips", CLOUDEnum.NUMBEROFIPS.getConceptString());
		QuantMiddleMan.put("messagenumber", CLOUDEnum.MESSAGENUMBER.getConceptString());
		QuantMiddleMan.put("dedicatedip", CLOUDEnum.DEDICATEDIP.getConceptString());
	}
}
