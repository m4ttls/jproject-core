package com.curcico.jproject.core.daos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.curcico.jproject.core.daos.ConditionComplex.Operator;
import com.curcico.jproject.core.exception.BaseException;
import com.curcico.jproject.core.wrapper.GridWrapper;
import com.google.gson.JsonArray;

@ContextConfiguration(locations = {"classpath:spring/application-config.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class NativeDaoImplTest {
	
	@Autowired
	private NativeDaoImpl dao;
	
	private static final String queryTest_01 = "SELECT * from ("
															+ "SELECT SYSDATE as fecha, "
															+ "10333 as entero, "
															+ "'ALGO' as texto, "
															+ "1 as booleano, "
															+ "10.25 as moneda "
															+ "FROM DUAL) "
											+  "WHERE 1=1 ";
	
	private static final String queryTest_02 = "SELECT * from ("
															+ "SELECT SYSDATE as fecha, "
															+ "10333 as entero, "
															+ "'ALGO' as texto, "
															+ "1 as booleano, "
															+ "10.25 as moneda "
															+ "FROM DUAL) "
												+  "WHERE 1=:arg_valor ";
	
	
	private static final String queryTest_03 = "SELECT SYSDATE as fecha, "
												+ "10333 as entero, "
												+ "'ALGO' as texto, "
												+ "1 as booleano, "
												+ "10.25 as moneda "
												+ "FROM DUAL "
												+ "WHERE 1=1 ";
	
	public NativeDaoImplTest() {
	}
	 
	@Test
	public void test_countByFilters_01() throws BaseException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("arg_valor", new Integer(1));
		Long filas = dao.countByFilters("SELECT SYSDATE, 1 as NUMERO, 'ALGO' as TEXTO FROM DUAL WHERE 1=:arg_valor",
				parameters, null);
		Assert.assertTrue(filas.equals(1L));
	}
	
	@Test
	public void test_countByFilters_02() throws BaseException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("arg_valor", new Integer(0));
		Long filas = dao.countByFilters("SELECT SYSDATE, 1 as NUMERO, 'ALGO' as TEXTO FROM DUAL WHERE 1=:arg_valor",
				parameters, null);
		Assert.assertTrue(filas.equals(0L));
	}
	
	@Test
	public void test_countByFilters_03() throws BaseException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("arg_valor", new Integer(1));
		List<ConditionEntry> conditions = new ArrayList<ConditionEntry>();
		conditions.add(new ConditionSimple("numero", SearchOption.EQUAL, new Integer(1)));
		Long filas = dao.countByFilters("select * from (SELECT SYSDATE, 1 as NUMERO, 'ALGO' as TEXTO FROM DUAL) WHERE 1=:arg_valor",
				parameters, conditions);
		Assert.assertTrue(filas.equals(1L));
	}
	
	@Test
	public void test_countByFilters_04() throws BaseException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("arg_valor", new Integer(1));
		List<ConditionEntry> conditions = new ArrayList<ConditionEntry>();
		conditions.add(new ConditionSimple("entero", 	SearchOption.GREATER_EQUAL, new Integer(10333)));
		conditions.add(new ConditionSimple("booleano", 	SearchOption.EQUAL, new Boolean(true)));
		conditions.add(new ConditionSimple("texto", 	SearchOption.CONTAIN, "LG"));
		conditions.add(new ConditionSimple("fecha", 	SearchOption.NOT_EQUAL, new Date()));
			ConditionComplex complex = new ConditionComplex(Operator.OR);
			complex.addCondition(new ConditionSimple("moneda", SearchOption.GREATER, new Float(9.50)));
			complex.addCondition(new ConditionSimple("moneda", SearchOption.LESS, new Float(11.50)));
		conditions.add(complex); 
		Long filas = dao.countByFilters(queryTest_01 + " AND 1=:arg_valor",
				parameters, conditions);
		Assert.assertTrue(filas.equals(1L));
	}
	
	@Test
	public void test_countByFilters_05() throws BaseException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("arg_valor", new Integer(1));
		Long filas = dao.countByFilters(queryTest_01 + " AND 1=:arg_valor", 
				parameters, null);
		Assert.assertTrue(filas!=0);
	}
		
	@Test
	public void test_findByFilters_01() throws BaseException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("arg_valor", new Integer(1));
		List<ConditionEntry> conditions = new ArrayList<ConditionEntry>();
		conditions.add(new ConditionSimple("entero", 	SearchOption.GREATER_EQUAL, new Integer(10333)));
		conditions.add(new ConditionSimple("booleano", 	SearchOption.EQUAL, new Boolean(true)));
		conditions.add(new ConditionSimple("texto", 	SearchOption.CONTAIN, "LG"));
		conditions.add(new ConditionSimple("fecha", 	SearchOption.NOT_EQUAL, new Date()));
			ConditionComplex complex = new ConditionComplex(Operator.OR);
			complex.addCondition(new ConditionSimple("moneda", SearchOption.GREATER, new Float(9.50)));
			complex.addCondition(new ConditionSimple("moneda", SearchOption.LESS, new Float(11.50)));
		conditions.add(complex); 
		JsonArray filas = (JsonArray) dao.findByFilters(queryTest_01 + " AND 1=:arg_valor", 
				parameters, conditions, 1, 10, "entero", "DESC", JsonArray.class);
		Assert.assertFalse(filas.isJsonNull());
	}
	
	@Test
	public void test_findByFilters_02() throws BaseException {
		JsonArray filas = (JsonArray) dao.findByFilters(queryTest_01, 
				null, null, 1, 10, "texto", "DESC",JsonArray.class);
		Assert.assertFalse(filas.isJsonNull());
		Assert.assertTrue(filas.size()==1);
	}

	@Test
	public void test_findByFilters_03() throws BaseException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("arg_valor", new Integer(1));
		JsonArray filas = (JsonArray) dao.findByFilters(queryTest_01 + " AND 1=:arg_valor", 
				parameters, null, 1, 10, "texto", "DESC", JsonArray.class);
		Assert.assertFalse(filas.isJsonNull());
	}
	
	@Test
	public void test_findByFilters_04() throws BaseException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("arg_valor", new Integer(1));
		@SuppressWarnings("unchecked")
		Collection<HashMap<String, Object>> filas = (Collection<HashMap<String, Object>>) dao.findByFilters(queryTest_01 + " AND 1=:arg_valor", 
				parameters, null, 1, 10, "texto", "DESC", Collection.class);
		Assert.assertFalse(filas.isEmpty());
	}
	
	@Test
	public void test_findByFilters_07() throws BaseException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("arg_valor", new Integer(1));
		@SuppressWarnings("unchecked")
		Collection<HashMap<String, Object>> filas = (Collection<HashMap<String, Object>>) dao.findByFilters(queryTest_01 + " AND 1=:arg_valor", 
				parameters, null, null, null, "texto", null, Collection.class);
		Assert.assertFalse(filas.isEmpty());
		Assert.assertTrue(filas.size()==1);
	}
	
	@Test
	public void test_findByFilters_09() throws BaseException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("arg_valor", new Integer(1));
		@SuppressWarnings("unchecked")
		GridWrapper<Object> filas = (GridWrapper<Object>) dao.findByFilters(queryTest_01 + " AND 1=:arg_valor", 
				parameters, null, 1, 10, "texto", null, GridWrapper.class);
		Assert.assertFalse(filas.getRows().isEmpty());
		Assert.assertTrue(filas.getRecords()>0);
	}
	
	@Test
	public void test_findByFilters_10() throws Exception {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("arg_valor", new Integer(1));
		String filter = "{'field':'fecha','op':'ge','data':'2006-12-11T14:16:56Z'}";
		List<ConditionEntry> filters = ConditionEntry.transformNativeFilters(filter);
		@SuppressWarnings("unchecked")
		GridWrapper<Object> filas = (GridWrapper<Object>) dao.findByFilters(queryTest_02, 
				parameters, filters, 1, 10, "texto", null, GridWrapper.class);
		// lo ejecuto nuevamente para comprobar que los parametros y filters no hayan sido corrompidos
		filas = (GridWrapper<Object>) dao.findByFilters(queryTest_02, 
				parameters, filters, 1, 10, "texto", null, GridWrapper.class);
		Assert.assertFalse(filas.getRows().isEmpty());
		Assert.assertTrue(filas.getRecords()>0);
	}
	
	@Test
	public void test_findByFilters_11() throws Exception {
		String filter = "{'field':'fecha','op':'ge','data':'2006-12-11'}";
		@SuppressWarnings("unchecked")
		GridWrapper<Object> filas = (GridWrapper<Object>) dao.findByFilters(queryTest_03, 
				null, ConditionEntry.transformNativeFilters(filter), 1, 10, "texto", null, GridWrapper.class);
		Assert.assertFalse(filas.getRows().isEmpty());
		Assert.assertTrue(filas.getRecords()>0);
	}
	
	@Test(expected=Exception.class)
	public void test_findByFilters_12() throws Exception {
		String filter = "{'field':'fecha','op':'ge','data':'2006-DIC-11'}";
		@SuppressWarnings("unchecked")
		GridWrapper<Object> filas = (GridWrapper<Object>) dao.findByFilters(queryTest_03, 
				null, ConditionEntry.transformNativeFilters(filter), 1, 10, "texto", null, GridWrapper.class);
		Assert.assertFalse(filas.getRows().isEmpty());
		Assert.assertTrue(filas.getRecords()>0);
	}	
	
	@Test
	public void test_findByFilters_13() throws Exception {
		String filter = "{'field':'texto','op':'ge','data':'2006-DIC-11'}";
		@SuppressWarnings("unchecked")
		GridWrapper<Object> filas = (GridWrapper<Object>) dao.findByFilters(queryTest_03, 
				null, ConditionEntry.transformNativeFilters(filter), 1, 10, "texto", null, GridWrapper.class);
		Assert.assertFalse(filas.getRows().isEmpty());
		Assert.assertTrue(filas.getRecords()>0);
	}
	
	@Test
	public void test_findByFilters_14() throws Exception {
		String filter = "{'field':'texto','op':'bw','data':'AL'}";
		@SuppressWarnings("unchecked")
		GridWrapper<Object> filas = (GridWrapper<Object>) dao.findByFilters(queryTest_03, 
				null, ConditionEntry.transformNativeFilters(filter), 1, 10, "texto", null, GridWrapper.class);
		Assert.assertFalse(filas.getRows().isEmpty());
		Assert.assertTrue(filas.getRecords()>0);
	}	
}
