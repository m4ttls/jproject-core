package com.curcico.jproject.core.daos;

/** Condicion de evaluacion de las ConditionsSimple
 * */
public enum SearchOption {
	
	/** Igual estricto (en el caso de los textos ES case sensitive).
	 * 
	 * CASO PARTICULAR: Las entidades que extienden de VersionedTimeRangeEntity
	 * admiten una condicion especia sobre el campo calculado 'active'.
	 * Si se solicita la igualacion a true o false, la logica del 
	 * BaseEntityDaoImpl generara las querys necesarias para ello.
	 * COUDADO: si la igualdad de la propiedad ´active´ se realiza sobre la entidad
	 * raiz, la logica interna la reemplazara por las condiciones correspondientes utilizando
	 * la hora de la base de datos como hora actual.
	 * Pero para el caso que la condicion sea sobre una composicion (por ejemplo: foo.active, eq, true)
	 * las condiciones generadas utilizaran la hora del servidor como hora actual. 
	 */
	EQUAL("eq"),
	
	/** Distinto estricto (en el caso de los textos ES case sensitive)
	 * 
	 * CASO PARTICULAR: Las entidades que extienden de VersionedTimeRangeEntity
	 * admiten una condicion especia sobre el campo calculado 'active'.
	 * Si se solicita la igualacion a true o false, la logica del 
	 * BaseEntityDaoImpl generara las querys necesarias para ello.
	 * COUDADO: si la igualdad de la propiedad ´active´ se realiza sobre la entidad
	 * raiz, la logica interna la reemplazara por las condiciones correspondientes utilizando
	 * la hora de la base de datos como hora actual.
	 * Pero para el caso que la condicion sea sobre una composicion (por ejemplo: foo.active, eq, true)
	 * las condiciones generadas utilizaran la hora del servidor como hora actual. 
	 */
	NOT_EQUAL("ne"),
	
	/** Comience con(NO es case sensitive)  
	 *  Se corresponde con la expresion SQL:  LIKE '?%' ESCAPE '!'  
	 *  Le adiciona automaticamente el caracter de escape ! a los wildcards ( _ y & )
	 */	
	BEGIN("bw"),
	
	/** No comience con (NO es case sensitive)  
	 *  Se corresponde con la expresion SQL:  NOT LIKE '?%' ESCAPE '!'  
	 *  Le adiciona automaticamente el caracter de escape ! a los wildcards ( _ y & )
	 */	
	NOT_BEGIN("bn"),
	
	/** Termine con (NO es case sensitive)  
	 *  Se corresponde con la expresion SQL:  LIKE '%?' ESCAPE '!'  
	 *  Le adiciona automaticamente el caracter de escape ! a los wildcards ( _ y & )
	 */	
	END("ew"), 
	
	/** No termine con (NO es case sensitive)  
	 *  Se corresponde con la expresion SQL: NOT LIKE '%?' ESCAPE '!'  
	 *  Le adiciona automaticamente el caracter de escape ! a los wildcards ( _ y & )
	 */	
	NOT_END("en"), 
	
	/** Contenga (NO es case sensitive)  
	 *  Se corresponde con la expresion SQL: LIKE '%?%' ESCAPE '!'  
	 *  Le adiciona automaticamente el caracter de escape ! a los wildcards ( _ y & )
	 */	
	CONTAIN("cn"), 
	
	/** No contenga (NO es case sensitive)  
	 *  Se corresponde con la expresion SQL: NOT LIKE '%?%' ESCAPE '!'  
	 *  Le adiciona automaticamente el caracter de escape ! a los wildcards ( _ y & )
	 */	
	NOT_CONTAIN("nc"),
	
	/** Similar al iLike de postgres (NO es case sensitive)  
	 *  Se corresponde con la expresion SQL: LIKE '?' ESCAPE '!'  
	 *  CUIDADO: Los caracteres _ y & son interpretados como wildcards, por lo cual
	 *  si se desea que la condicion lo interprete como caracter en lugar de wildcard, deben
	 *  ser antecedidos del caracter de escape !
	 */		
	LIKE("lk"),
	
	/** Similar al NOT iLike de postgres (NO es case sensitive)  
	 *  Se corresponde con la expresion SQL: NOT LIKE '?' ESCAPE '!'  
	 *  CUIDADO: Los caracteres _ y & son interpretados como wildcards, por lo cual
	 *  si se desea que la condicion lo interprete como caracter en lugar de wildcard, deben
	 *  ser antecedidos del caracter de escape !
	 */		
	NOT_LIKE("nk"),
	
	/** Es null  
	 *  Se corresponde con la expresion SQL: IS NULL  
	 */		
	NULL("nu"), 
	
	/** Es not null 
	 *  Se corresponde con la expresion SQL: IS NULL  
	 */		
	NOT_NULL("nn"),
	
	/** Se encuentra entre los elementos de la lista ... 
	 *  Se corresponde con la expresion SQL: IN   
	 */		
	IN("in"), 
	
	/** NO se encuentra entre los elementos de la lista ... 
	 *  Se corresponde con la expresion SQL: NOT IN   
	 */		
	NOT_IN("ni"),
	
	/** MENOR QUE 
	 *  Se corresponde con la expresion SQL: <   
	 *  Se puede utilizar con cualquier tipo de objeto, pero es indispensable que el valor
	 *  que se le pasa como parametro sea del mismo tipo que el atributo a comparar
	 */	
	LESS("lt"),
	
	/** MENOR O IGUAL QUE 
	 *  Se corresponde con la expresion SQL: <=   
	 *  Se puede utilizar con cualquier tipo de objeto, pero es indispensable que el valor
	 *  que se le pasa como parametro sea del mismo tipo que el atributo a comparar
	 */	
	LESS_EQUAL("le"),
	
	/** MAYOR QUE 
	 *  Se corresponde con la expresion SQL: >   
	 *  Se puede utilizar con cualquier tipo de objeto, pero es indispensable que el valor
	 *  que se le pasa como parametro sea del mismo tipo que el atributo a comparar
	 */	
	GREATER("gt"),
	
	/** MAYOR O IGUAL QUE 
	 *  Se corresponde con la expresion SQL: >=   
	 *  Se puede utilizar con cualquier tipo de objeto, pero es indispensable que el valor
	 *  que se le pasa como parametro sea del mismo tipo que el atributo a comparar
	 */	
	GREATER_EQUAL("ge"),
	
	
	/** CUMPLE EXPRESION REGULAR 
	 *  Se corresponde con la expresion SQL: regexp_like(columnName, 'value' , 'i') 
	 *  CUIDADO: NO ES CASE SENSITIVE
	 */		
	REGEX("re") 	
	; 
	
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
		case "lk":
			return LIKE;
		case "nk":
			return NOT_LIKE;
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
		return !(searchOp.equals(NOT_NULL) || searchOp.equals(NULL));
	}

	public static boolean isRequiredArrayValue(SearchOption searchOp) {
		return (searchOp.equals(IN) || searchOp.equals(NOT_IN));
		
	}
}
