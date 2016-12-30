package com.curcico.jproject.core.daos;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.curcico.jproject.core.daos.ConditionComplex.Operator;
import com.curcico.jproject.core.entities.OneBaseEntity;
import com.curcico.jproject.core.entities.OneBaseTimeRangeEntity;
import com.curcico.jproject.core.exception.BaseException;
import com.curcico.jproject.core.services.OneBaseEntityService;
import com.curcico.jproject.core.services.OneBaseTimeRangeEntityService;
import com.curcico.jproject.core.wrapper.GridWrapper;

@ContextConfiguration(locations = {"classpath:spring/application-config.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@Rollback
public class SearchOptionTest {

	Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	OneBaseTimeRangeEntityService service;
	
	@Autowired
	OneBaseEntityService oneBaseEntityService;
	
	
	/*Para crear la entidad para test, creo una entidad bajo un nombre particular 
	 * y creo la condicion equals para que la evaluacion se realice solo sobre eso
	 * */
	private synchronized List<ConditionEntry> createEntityAndCondition(String nombre) throws BaseException{
		OneBaseTimeRangeEntity vtre = service.saveOrUpdate(createOneVersionedTimeRangeEntity(nombre));
		List<ConditionEntry> filters = new ArrayList<ConditionEntry>();
		filters.add(new ConditionSimple("id", SearchOption.EQUAL, vtre.getId()));
		return filters;
	}
	
	private synchronized OneBaseTimeRangeEntity createOneVersionedTimeRangeEntity(String nombre) throws BaseException{
		OneBaseTimeRangeEntity vtre = new OneBaseTimeRangeEntity();
		vtre.setNombre(nombre);
		vtre.setDescripcion(nombre);
		return service.saveOrUpdate(vtre, 1);
	}
	
	private synchronized OneBaseEntity createOneVersionedEntity(String nombre) throws BaseException{
		OneBaseEntity o = new OneBaseEntity();
		o.setDescripcion(nombre);
		o.setVtr(createOneVersionedTimeRangeEntity("vtr_" + nombre));
		return oneBaseEntityService.saveOrUpdate(o);
	}
	
	/* ***********************   TESTS   *********************** */
	
	/* ***  SearchOption.EQUAL  *** */
	@Test
	public void searchOptionEquals_String_ok1() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("TEST");
		filters.add(new ConditionSimple("nombre", SearchOption.EQUAL, "TEST"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
				//.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}
	
	@Test
	public void searchOptionEquals_String_ok2() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("TEST");
		filters.add(new ConditionSimple("nombre", SearchOption.EQUAL, "test"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertTrue(resultado.getRows().isEmpty());
	}
	
	@Test
	public void searchOptionEquals_String_ok3() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("searchOptionEquals");
		filters.add(new ConditionSimple("nombre", SearchOption.EQUAL, "searchoptionequals1"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertTrue(resultado.getRows().isEmpty());
	}
	
	@Test
	public void searchOptionEquals_String_ok4() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("searchOptionEquals");
		filters.add(new ConditionSimple("nombre", SearchOption.EQUAL, "search_ptionequals"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertTrue(resultado.getRows().isEmpty());
	}
	
	@Test
	public void searchOptionEquals_String_ok5() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("search_Option%Equals");
		filters.add(new ConditionSimple("nombre", SearchOption.EQUAL, "search_Option%Equals"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}
	
	@Test
	public void searchOptionEquals_Number_ok1() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("searchOptionEquals");
		/*La condicion igual la pone el createEntityAndCondition */
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}
	
	@Test
	public void searchOptionEquals_Number_ok2() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("searchOptionEquals");
		filters.add(new ConditionSimple("id", SearchOption.EQUAL, -1000));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertTrue(resultado.getRows().isEmpty());
	}
	
	@Test
	public void searchOptionEquals_Entity_active_ok_01() throws BaseException{
		List<ConditionEntry> filters = createEntityAndCondition("searchOptionEquals");
		filters.add(new ConditionSimple("active", SearchOption.EQUAL, true));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}
	
	@Test
	public void searchOptionEquals_Entity_active_ok_02() throws BaseException, InterruptedException{
		OneBaseTimeRangeEntity vtre = createOneVersionedTimeRangeEntity("test");
		Thread.sleep(10L);
		vtre = service.delete(vtre);
		/*NOTA, cuando la condicion active se */
		Thread.sleep(1000L);
		List<ConditionEntry> filters = new ArrayList<ConditionEntry>();
		filters.add(new ConditionSimple("id", SearchOption.EQUAL, vtre.getId()));
		filters.add(new ConditionSimple("active", SearchOption.EQUAL, false));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}

	@Test
	public void searchOptionEquals_Entity_active_ok_03() throws BaseException{
		OneBaseEntity ove = createOneVersionedEntity("TEST");
		List<ConditionEntry> filters = new ArrayList<ConditionEntry>();
		ConditionComplex ce = new ConditionComplex(Operator.AND);
		ce.addCondition(new ConditionSimple("id", SearchOption.GREATER_EQUAL, ove.getId()));
		ce.addCondition(new ConditionSimple("vtr.active", SearchOption.EQUAL, true));
		filters.add(ce);
		GridWrapper<OneBaseEntity> resultado = 
				oneBaseEntityService.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}
	
	
	/* ***  SearchOption.NOT_EQUAL  *** */
	@Test
	public void searchOptionNotEquals_String_ok1() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("searchOptionEquals");
		filters.add(new ConditionSimple("nombre", SearchOption.NOT_EQUAL, "search_ptionEquals"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}
	
	@Test
	public void searchOptionNotEquals_String_ok2() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("searchOptionEquals");
		filters.add(new ConditionSimple("nombre", SearchOption.NOT_EQUAL, "searchOptionEquals"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertTrue(resultado.getRows().isEmpty());
	}	
	
	@Test
	public void searchOptionNotEquals_Number_ok1() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("searchOptionEquals");
		filters.add(new ConditionSimple("id", SearchOption.NOT_EQUAL, -1000));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}
	
	/* ***  SearchOption.BEGIN  *** */
	
	@Test
	public void searchOptionBegin_ok1() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("searchOptionEquals");
		filters.add(new ConditionSimple("nombre", SearchOption.BEGIN, "search"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}
	
	@Test
	public void searchOptionBegin_ok2() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("search_OptionEquals");
		filters.add(new ConditionSimple("nombre", SearchOption.BEGIN, "search_"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}
	
	@Test
	public void searchOptionBegin_ok3() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("search_OptionEquals");
		filters.add(new ConditionSimple("nombre", SearchOption.BEGIN, "Xsearch"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertTrue(resultado.getRows().isEmpty());
	}
	
	/* ***  SearchOption.NOT_BEGIN  *** */
	
	@Test
	public void searchOptionNotBegin_ok1() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("search_OptionEquals");
		filters.add(new ConditionSimple("nombre", SearchOption.NOT_BEGIN, "search"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertTrue(resultado.getRows().isEmpty());
	}
	
	@Test
	public void searchOptionNotBegin_ok2() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("search_OptionEquals");
		filters.add(new ConditionSimple("nombre", SearchOption.NOT_BEGIN, "%search_OptionEquals%"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}
	
	/* ***  SearchOption.END  *** */
	
	@Test
	public void searchOptionEnd_ok1() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("search_OptionEquals");
		filters.add(new ConditionSimple("nombre", SearchOption.END, "quAls"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}
	
	@Test
	public void searchOptionEnd_ok2() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("search_OptionEquals");
		filters.add(new ConditionSimple("nombre", SearchOption.END, "_quals"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertTrue(resultado.getRows().isEmpty());
	}
	
	/* ***  SearchOption.NOT_END  *** */
	
	@Test
	public void searchOptionNotEnd_ok1() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("search_OptionEquals");
		filters.add(new ConditionSimple("nombre", SearchOption.NOT_END, "quAls"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertTrue(resultado.getRows().isEmpty());
	}
	
	@Test
	public void searchOptionNotEnd_ok2() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("search_OptionEquals");
		filters.add(new ConditionSimple("nombre", SearchOption.NOT_END, "_quals"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}
	
	/* ***  SearchOption.CONTAIN  *** */
	
	@Test
	public void searchOptionContains_ok1() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("search_OptionEquals");
		filters.add(new ConditionSimple("nombre", SearchOption.CONTAIN, "XXch_optione"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertTrue(resultado.getRows().isEmpty());
	}
	
	@Test
	public void searchOptionContains_ok2() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("search_OptionEquals");
		filters.add(new ConditionSimple("nombre", SearchOption.CONTAIN, "ch_optione"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}
	
	/* ***  SearchOption.NOT_CONTAIN  *** */
	
	@Test
	public void searchOptionNotContains_ok1() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("search_OptionEquals");
		filters.add(new ConditionSimple("nombre", SearchOption.NOT_CONTAIN, "_option"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertTrue(resultado.getRows().isEmpty());
	}
	
	@Test
	public void searchOptionNotContains_ok2() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("search_OptionEquals");
		filters.add(new ConditionSimple("nombre", SearchOption.NOT_CONTAIN, "XXX"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}

	/* ***  SearchOption.LIKE  *** */
	
	@Test
	public void searchOptionLike_01() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("searchOptionLike_01");
		filters.add(new ConditionSimple("nombre", SearchOption.LIKE, "%"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}
	
	@Test
	public void searchOptionLike_02() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("TEST&TEST");
		filters.add(new ConditionSimple("nombre", SearchOption.LIKE, "TEST_test"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}
	
	@Test
	public void searchOptionLike_03() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("TEST_TEST");
		filters.add(new ConditionSimple("nombre", SearchOption.LIKE, "TEST!_test"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}
	
	@Test
	public void searchOptionLike_04() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("TEST&TEST");
		filters.add(new ConditionSimple("nombre", SearchOption.LIKE, "TEST!_test"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertTrue(resultado.getRows().isEmpty());
	}	
	
	
	/* ***  SearchOption.NOT_LIKE  	*** */
	@Test
	public void searchOptionNotLike_01() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("TEST&TEST");
		filters.add(new ConditionSimple("nombre", SearchOption.NOT_LIKE, "TEST_test"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertTrue(resultado.getRows().isEmpty());
	}	

	@Test
	public void searchOptionNotLike_02() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("TEST&TEST");
		filters.add(new ConditionSimple("nombre", SearchOption.NOT_LIKE, "TEST%"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertTrue(resultado.getRows().isEmpty());
	}		
	
	
	/* ***  SearchOption.NULL  		*** */
	@Test
	public void searchOptionNull_01() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("TEST&TEST");
		filters.add(new ConditionSimple("nombre", SearchOption.NULL, null));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertTrue(resultado.getRows().isEmpty());
	}		
	
	@Test
	public void searchOptionNull_02() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("TEST&TEST");
		filters.add(new ConditionSimple("validTo", SearchOption.NULL, null));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}		
	
	
	/* ***  SearchOption.NOT_NULL  	*** */
	@Test
	public void searchOptionNotNull_01() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("TEST&TEST");
		filters.add(new ConditionSimple("nombre", SearchOption.NOT_NULL, null));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}		
	
	@Test
	public void searchOptionNotNull_02() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("TEST&TEST");
		filters.add(new ConditionSimple("validTo", SearchOption.NOT_NULL, null));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertTrue(resultado.getRows().isEmpty());
	}		
	

	/* ***  SearchOption.IN  		*** */
	@Test
	public void searchOptionIn_01() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("TEST");
		Collection<String> lista = new ArrayList<>();
		lista.add("OTRO");
		lista.add("TEST");
		filters.add(new ConditionSimple("nombre", SearchOption.IN, lista));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}		
	
	@Test
	public void searchOptionIn_02() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("TEST");
		Collection<String> lista = new ArrayList<>();
		lista.add("UNO");
		lista.add("DOS");
		filters.add(new ConditionSimple("nombre", SearchOption.IN, lista));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertTrue(resultado.getRows().isEmpty());
	}	

	@Test
	public void searchOptionIn_03() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("TEST");
		filters.add(new ConditionSimple("nombre", SearchOption.IN, new String[]{"TEST", "DOS"}));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}
	
	@Test
	public void searchOptionIn_04() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("TEST");
		/*testea el case sensitive*/
		filters.add(new ConditionSimple("nombre", SearchOption.IN, new String[]{"test", "dos"}));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertTrue(resultado.getRows().isEmpty());
	}
	
	/* ***  SearchOption.NOT_IN  	*** */
	@Test
	public void searchOptionNotIn_01() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("TEST");
		Collection<String> lista = new ArrayList<>();
		lista.add("OTRO");
		lista.add("TEST");
		filters.add(new ConditionSimple("nombre", SearchOption.NOT_IN, lista));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertTrue(resultado.getRows().isEmpty());
	}		
	
	@Test
	public void searchOptionNotIn_02() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("TEST");
		Collection<String> lista = new ArrayList<>();
		lista.add("UNO");
		lista.add("DOS");
		filters.add(new ConditionSimple("nombre", SearchOption.NOT_IN, lista));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}	
	
	@Test
	public void searchOptionNotIn_03() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("TEST");
		filters.add(new ConditionSimple("nombre", SearchOption.NOT_IN, new String[]{"TEST", "DOS"}));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertTrue(resultado.getRows().isEmpty());
	}
	
	@Test
	public void searchOptionNotIn_04() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("TEST");
		/*testea el case sensitive*/
		filters.add(new ConditionSimple("nombre", SearchOption.NOT_IN, new String[]{"test", "dos"}));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}
	
	/* ***  SearchOption.LESS  				*** */
	@Test
	public void searchOptionLess_Number_01() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("TEST&TEST");
		filters.add(new ConditionSimple("version", SearchOption.LESS, 2));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}		
	
	@Test
	public void searchOptionLess_Number_02() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("TEST&TEST");
		filters.add(new ConditionSimple("version", SearchOption.LESS, 1));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertTrue(resultado.getRows().isEmpty());
	}
	
	@Test
	public void searchOptionLess_String_01() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("ABC");
		filters.add(new ConditionSimple("nombre", SearchOption.LESS, "BBB"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}		
	
	@Test
	public void searchOptionLess_String_02() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("ABC");
		filters.add(new ConditionSimple("nombre", SearchOption.LESS, "ABC"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertTrue(resultado.getRows().isEmpty());
	}

	
	@Test
	public void searchOptionLess_Date_01() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("ABC");
		filters.add(new ConditionSimple("validFrom", SearchOption.LESS, new Date()));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}		
	
	@Test
	public void searchOptionLess_Date_02() throws BaseException {
		OneBaseTimeRangeEntity vtre = createOneVersionedTimeRangeEntity("ABC");
		List<ConditionEntry> filters = new ArrayList<ConditionEntry>();
		filters.add(new ConditionSimple("id", SearchOption.EQUAL, vtre.getId()));
		filters.add(new ConditionSimple("validFrom", SearchOption.LESS, vtre.getValidFrom()));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertTrue(resultado.getRows().isEmpty());
	}
	
	
	/* ***  SearchOption.LESS_EQUAL  				*** */
	@Test
	public void searchOptionLessEquals_Number_01() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("TEST&TEST");
		filters.add(new ConditionSimple("version", SearchOption.LESS_EQUAL, 2));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}		
	
	@Test
	public void searchOptionLessEquals_Number_02() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("TEST&TEST");
		filters.add(new ConditionSimple("version", SearchOption.LESS_EQUAL, 1));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}
	
	@Test
	public void searchOptionLessEquals_Number_03() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("TEST&TEST");
		filters.add(new ConditionSimple("version", SearchOption.LESS_EQUAL, 0));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertTrue(resultado.getRows().isEmpty());
	}
	
	@Test
	public void searchOptionLessEquals_String_01() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("ABC");
		filters.add(new ConditionSimple("nombre", SearchOption.LESS_EQUAL, "BBB"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}		
	
	@Test
	public void searchOptionLessEquals_String_02() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("ABC");
		filters.add(new ConditionSimple("nombre", SearchOption.LESS_EQUAL, "ABC"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}
	
	@Test
	public void searchOptionLessEquals_String_03() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("ABC");
		filters.add(new ConditionSimple("nombre", SearchOption.LESS_EQUAL, "AAA"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertTrue(resultado.getRows().isEmpty());
	}

	
	@Test
	public void searchOptionLessEquals_Date_01() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("ABC");
		filters.add(new ConditionSimple("validFrom", SearchOption.LESS_EQUAL, new Date()));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}		
	
	@Test
	public void searchOptionLessEquals_Date_02() throws BaseException {
		OneBaseTimeRangeEntity vtre = createOneVersionedTimeRangeEntity("ABC");
		List<ConditionEntry> filters = new ArrayList<ConditionEntry>();
		filters.add(new ConditionSimple("id", SearchOption.EQUAL, vtre.getId()));
		filters.add(new ConditionSimple("validFrom", SearchOption.LESS_EQUAL, vtre.getValidFrom()));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}	
	
	@Test
	public void searchOptionLessEquals_Date_03() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("ABC");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, -1);
		filters.add(new ConditionSimple("validFrom", SearchOption.LESS_EQUAL, new Date(c.getTimeInMillis())));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertTrue(resultado.getRows().isEmpty());
	}
	
	
	/* ***  SearchOption.GREATER  				*** */
	@Test
	public void searchOptionGreater_Number_01() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("TEST&TEST");
		filters.add(new ConditionSimple("version", SearchOption.GREATER, 0));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}		
	
	@Test
	public void searchOptionGreater_Number_02() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("TEST&TEST");
		filters.add(new ConditionSimple("version", SearchOption.GREATER, 1));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertTrue(resultado.getRows().isEmpty());
	}
	
	@Test
	public void searchOptionGreater_String_01() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("ABC");
		filters.add(new ConditionSimple("nombre", SearchOption.GREATER, "AAA"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}		
	
	@Test
	public void searchOptionGreater_String_02() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("ABC");
		filters.add(new ConditionSimple("nombre", SearchOption.GREATER, "ABC"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertTrue(resultado.getRows().isEmpty());
	}

	
	@Test
	public void searchOptionGreater_Date_01() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("ABC");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, -1);
		filters.add(new ConditionSimple("validFrom", SearchOption.GREATER, new Date(c.getTimeInMillis())));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}		
	
	@Test
	public void searchOptionGreater_Date_02() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("ABC");
		filters.add(new ConditionSimple("validFrom", SearchOption.GREATER, new Date()));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertTrue(resultado.getRows().isEmpty());
	}
	
	
	/* ***  SearchOption.GREATER_EQUAL  				*** */
	@Test
	public void searchOptionGreaterEquals_Number_01() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("TEST&TEST");
		filters.add(new ConditionSimple("version", SearchOption.GREATER_EQUAL, 0));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}		
	
	@Test
	public void searchOptionGreaterEquals_Number_02() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("TEST&TEST");
		filters.add(new ConditionSimple("version", SearchOption.GREATER_EQUAL, 1));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}
	
	@Test
	public void searchOptionGreaterEquals_Number_03() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("TEST&TEST");
		filters.add(new ConditionSimple("version", SearchOption.GREATER_EQUAL, 2));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertTrue(resultado.getRows().isEmpty());
	}
	
	@Test
	public void searchOptionGreaterEquals_String_01() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("ABC");
		filters.add(new ConditionSimple("nombre", SearchOption.GREATER_EQUAL, "AAA"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}		
	
	@Test
	public void searchOptionGreaterEquals_String_02() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("ABC");
		filters.add(new ConditionSimple("nombre", SearchOption.GREATER_EQUAL, "ABC"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}
	
	@Test
	public void searchOptionGreaterEquals_String_03() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("ABC");
		filters.add(new ConditionSimple("nombre", SearchOption.GREATER_EQUAL, "BBB"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertTrue(resultado.getRows().isEmpty());
	}

	
	@Test
	public void searchOptionGreaterEquals_Date_01() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("ABC");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, -1);
		filters.add(new ConditionSimple("validFrom", SearchOption.GREATER_EQUAL, new Date(c.getTimeInMillis())));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}		
	
	@Test
	public void searchOptionGreaterEquals_Date_02() throws BaseException {
		OneBaseTimeRangeEntity vtre = createOneVersionedTimeRangeEntity("ABC");
		List<ConditionEntry> filters = new ArrayList<ConditionEntry>();
		filters.add(new ConditionSimple("id", SearchOption.EQUAL, vtre.getId()));
		filters.add(new ConditionSimple("validFrom", SearchOption.GREATER_EQUAL, vtre.getValidFrom()));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}	
	
	@Test
	public void searchOptionGreaterEquals_Date_03() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("ABC");
		filters.add(new ConditionSimple("validFrom", SearchOption.GREATER_EQUAL, new Date()));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertTrue(resultado.getRows().isEmpty());
	}

	
	/* ***  SearchOption.REGEX  				*** */
	@Test
	public void searchOptionRegex_01() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("1234");
		filters.add(new ConditionSimple("nombre", SearchOption.REGEX, ".*[1-9]$"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}
	
	@Test
	public void searchOptionRegex_02() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("ABC");
		filters.add(new ConditionSimple("nombre", SearchOption.REGEX, ".*[1-9]$"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertTrue(resultado.getRows().isEmpty());
	}
	
	@Test
	public void searchOptionRegex_03() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("ABC");
		filters.add(new ConditionSimple("nombre", SearchOption.REGEX, "(abc)+"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}
	
	@Test
	public void searchOptionRegex_04() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("abc");
		filters.add(new ConditionSimple("nombre", SearchOption.REGEX, "[abc]+"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertFalse(resultado.getRows().isEmpty());
	}
	
	@Test
	public void searchOptionRegex_05() throws BaseException {
		List<ConditionEntry> filters = createEntityAndCondition("abc");
		filters.add(new ConditionSimple("nombre", SearchOption.REGEX, "(xyz)+"));
		GridWrapper<?> resultado = 
				service.findByFiltersGridWrapper(filters, 1, 5, null, null, null);
		Assert.assertNotNull(resultado);
		Assert.assertTrue(resultado.getRows().isEmpty());
	}
	
}
