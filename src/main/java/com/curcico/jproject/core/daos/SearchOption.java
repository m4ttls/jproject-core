package com.curcico.jproject.core.daos;

public enum SearchOption {
	
	EQUAL("eq"), 
	NOT_EQUAL("ne"), 
	BEGIN("bw"),
	NOT_BEGIN("bn"),
	END("ew"), 
	NOT_END("en"), 
	CONTAIN("cn"), 
	NOT_CONTAIN("nc"), 
	NULL("nu"), 
	NOT_NULL("nn"),
	IN("in"), 
	NOT_IN("ni"),
	LESS("lt"),
	LESS_EQUAL("le"),
	GREATER("gt"),
	GREATER_EQUAL("ge"),
	REGEX("re");
	
	private final String id;

	SearchOption(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
	
	public static SearchOption getSearchOption(String id){
		switch(id.toLowerCase()){
		case "eq":
			return EQUAL;
		case "ne":
			return NOT_EQUAL;
		case "bw":
			return BEGIN;
		case "bn":
			return NOT_BEGIN;
		case "ew":
			return END;
		case "en":
			return NOT_END;
		case "cn":
			return CONTAIN;
		case "nc":
			return NOT_CONTAIN;
		case "nu":
			return NULL;
		case "nn":
			return NOT_NULL;
		case "in":
			return IN;
		case "ni":
			return NOT_IN;
		case "lt":
			return LESS;
		case "le":
			return LESS_EQUAL;
		case "gt":
			return GREATER;	
		case "ge":
			return GREATER_EQUAL;
		case "re":
			return REGEX;
		default:
			return null;
		}
	}

	public static boolean isRequiredFieldValue(SearchOption searchOp) {
		if(searchOp.equals(SearchOption.NULL) || searchOp.equals(SearchOption.NOT_NULL)){
			return false;
		}
		return true;
	}
	
	public static boolean isRequiredArrayValue(SearchOption searchOp) {
		return (searchOp.equals(IN) || searchOp.equals(NOT_IN));
		
	}
}
