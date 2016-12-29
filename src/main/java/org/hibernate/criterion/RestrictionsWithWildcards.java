package org.hibernate.criterion;

public class RestrictionsWithWildcards extends Restrictions {

	
	/**
	 * A case-insensitive "like", similar to Postgres <tt>ilike</tt>
	 * operator
	 *
	 * @param propertyName
	 * @param value
	 * @param matchMode
	 * @param escapeChar
	 * @return Criterion
	 */
	public static Criterion ilike(String propertyName, String value, MatchMode matchMode, Character escapeChar) {
		return new LikeExpression(propertyName, value, matchMode, escapeChar, true);
	}

}
