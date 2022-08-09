package hn.com.tigo.josm.persistence.cache;

import hn.com.tigo.josm.common.configuration.dto.ConstraintDetailsErrors;

import java.util.HashMap;
import java.util.Map;

public class DataBaseErrorsDetail {
	

	/** Attribute that determine databaseErrorMap. */
	private Map<String, Map<String, ConstraintDetailsErrors>> databaseErrorMap;
	
	
	public DataBaseErrorsDetail(){
		this.databaseErrorMap =  new HashMap<String, Map<String,ConstraintDetailsErrors>>();
	}

	public Map<String, Map<String, ConstraintDetailsErrors>> getDatabaseErrorMap() {
		return databaseErrorMap;
	}

	public void setDatabaseErrorMap(
			Map<String, Map<String, ConstraintDetailsErrors>> databaseErrorMap) {
		this.databaseErrorMap = databaseErrorMap;
	}

	
	
}
