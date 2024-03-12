package com.dma.web;

import org.apache.commons.lang3.StringUtils;

public class Test30 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String FQUERY = ("FKQuery=SELECT 'FK_' || TARGETOBJ || '_' || OBJECTNAME || '_' || LOGICALRELATIONSHIP.LOGICALRELATIONSHIPID as FK_NAME, 'PK_' || OBJECTNAME || '_' || TARGETOBJ || '_' || LOGICALRELATIONSHIP.LOGICALRELATIONSHIPID as PK_NAME, TARGETOBJ as FKTABLE_NAME, OBJECTNAME as PKTABLE_NAME, KEYNUM as KEY_SEQ, TARGETKEY as FKCOLUMN_NAME, PARENTKEY as PKCOLUMN_NAME FROM LOGICALRELATIONSHIP join LRKEYS on LOGICALRELATIONSHIP.LOGICALRELATIONSHIPID = LRKEYS.LOGICALRELATIONSHIPID WHERE TARGETOBJ = $TABLE");
		String table = "POLINE";
		FQUERY = StringUtils.replace(FQUERY, " $TABLE", " '" + table + "' ");
		System.out.println(FQUERY);
		
	}

}
