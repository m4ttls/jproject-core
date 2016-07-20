package com.curcico.jproject.core.daos;

import java.security.InvalidParameterException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.curcico.jproject.core.daos.ConditionComplex.Operator;
import com.curcico.jproject.core.entities.TestEntity;

@ContextConfiguration(locations = {"classpath:spring/application-config.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class ConditionEntryTest {
	
//	@Autowired
//	OneVersionedTimeRangeEntityService vtrService;
//	
//	@Autowired
//	OneVersionedEntityService veService;
	
	/*Condicion sin campo data requerido (nn=NOT NULL)*/
	String filtroSimple01 		= "{'field':'id','op':'nn'}";
	
	/*Condicion con campo data requerido*/
	String filtroSimple02 		= "{'field':'id','op':'eq','data':'1'}";
	
	/*Arreglo de condiciones, asume que todas las condiciones deben cumplirse (AND)*/
	String condicionDEFAULT 	= "[{'field':'id','op':'eq','data':'1'},{'field':'fecha','op':'nn','data':''}]";

	/*Arreglo de condiciones con agrupador informado */
	String condicionAND 	 	= "{'groupOp':'AND','rules':[{'field':'id','op':'eq','data':'1'},{'field':'fecha','op':'nn','data':''}]}";

	/*Arreglo de condiciones con agrupador informado */
	String condicionOR 	 		= "{'groupOp':'OR','rules':[{'field':'id','op':'eq','data':'1'},{'field':'fecha','op':'nn','data':''}]}";
	
	/*Arreglo de condiciones con agrupador informado y grupo de condiciones*/
	String condicionCOMPLEJA 	= "{'groupOp':'AND','rules':[{'field':'id','op':'eq','data':'1'},{'field':'fecha','op':'nn','data':''}],'groups':[{'groupOp':'OR','rules':[{'field':'numero','op':'gt','data':'100'},{'field':'numero','op':'le','data':'1'}],'groups':[]}]}";

	/*Condicion con campo data requerido*/
	String filtroSimpleConLista01 = "{'field':'id','op':'in','data':'[1, 2, 3]'}";
	
	/*Condicion con campo data requerido*/
	String filtroSimpleConLista02 = "{'field':'nombre','op':'ni','data':'[\"marta\", \"luis\"]'}";	
	
	
	/* LLAMADAS ERRONEAS */
	String error_falta_campo_obligatorio 					=  "{'op':'eq','data':'1'}";
	String error_falta_valor_para_operacion_que_lo_requiere =  "{'field':'id','op':'eq'}";
	String error_valor_nulo_para_operacion_que_lo_requiere  =  "{'field':'id','op':'eq','data':''}";
	
	
	/* ************************************************* */
	/* TRANSFORMACION DE FILTROS A List<ConditionEntry>  */
	/* ************************************************* */

	
	@Test
	public void transformFilters_null_ok() throws Exception{
		List<ConditionEntry> c = ConditionEntry.transformFilters(TestEntity.class, null);
		Assert.assertNotNull(c);
		Assert.assertTrue(c.isEmpty());
	}
	
	@Test
	public void transformFilters_empty_ok() throws Exception{
		List<ConditionEntry> c = ConditionEntry.transformFilters(TestEntity.class, "");
		Assert.assertNotNull(c);
		Assert.assertTrue(c.isEmpty());
	}
	
	@Test
	public void transformFilters_simple_ok() throws Exception{
		List<ConditionEntry> c = ConditionEntry.transformFilters(TestEntity.class, condicionDEFAULT);
		Assert.assertNotNull(c);
		Assert.assertFalse(c.isEmpty());
		Assert.assertEquals(2, c.size());		
	}
	
	@Test
	public void transformFilters_and_ok()throws Exception{
		List<ConditionEntry> c = ConditionEntry.transformFilters(TestEntity.class, condicionAND);
		Assert.assertNotNull(c);
		Assert.assertFalse(c.isEmpty());
		Assert.assertEquals(1, c.size());
		ConditionEntry c2 = c.get(0);
		Assert.assertTrue(c2 instanceof ConditionComplex);
		ConditionComplex cc = (ConditionComplex) c2;
		Assert.assertFalse(cc.getConditions().isEmpty());
		Assert.assertEquals(2, cc.getConditions().size());
		Assert.assertTrue(cc.getOperator().equals(Operator.AND));
	}
	
	@Test
	public void transformFilters_or_ok() throws Exception{
		List<ConditionEntry> c = ConditionEntry.transformFilters(TestEntity.class, condicionOR);
		Assert.assertNotNull(c);
		Assert.assertFalse(c.isEmpty());
		Assert.assertEquals(1, c.size());
		ConditionEntry c2 = c.get(0);
		Assert.assertTrue(c2 instanceof ConditionComplex);
		ConditionComplex cc = (ConditionComplex) c2;
		Assert.assertFalse(cc.getConditions().isEmpty());
		Assert.assertEquals(2, cc.getConditions().size());
		Assert.assertTrue(cc.getOperator().equals(Operator.OR));
	}
	
	@Test
	public void transformFilters_complex_ok() throws Exception{
		List<ConditionEntry> c = ConditionEntry.transformFilters(TestEntity.class, condicionCOMPLEJA);
		Assert.assertNotNull(c);
		Assert.assertFalse(c.isEmpty());
		Assert.assertEquals(1, c.size());
		ConditionEntry c2 = c.get(0);
		Assert.assertTrue(c2 instanceof ConditionComplex);
		ConditionComplex cc = (ConditionComplex) c2;
		Assert.assertFalse(cc.getConditions().isEmpty());
		Assert.assertEquals(3, cc.getConditions().size());
		Assert.assertTrue(cc.getOperator().equals(Operator.AND));
		for (ConditionEntry ce: cc.getConditions()){
			if(ce instanceof ConditionComplex){
				ConditionComplex cc2 = (ConditionComplex) ce;
				Assert.assertFalse(cc2.getConditions().isEmpty());
				Assert.assertEquals(2, cc2.getConditions().size());
				Assert.assertTrue(cc2.getOperator().equals(Operator.OR));
			}
		}
	}	
	/*
	@Test
	public void transformFilters_actives_ok() throws Exception{
		//ACTIVAS
		List<ConditionEntry> c = ConditionEntry.transformFilters(OneVersionedTimeRangeEntity.class, "{'field':'active','op':'eq','data':'true'}");
		Collection<OneVersionedTimeRangeEntity> r = vtrService.findByFilters(c);
		Assert.assertNotNull(r);
		Assert.assertFalse(r.isEmpty());
		c = ConditionEntry.transformFilters(OneVersionedTimeRangeEntity.class, "{'field':'active','op':'ne','data':'false'}");
		r = vtrService.findByFilters(c);
		Assert.assertNotNull(r);
		Assert.assertFalse(r.isEmpty());
		c = ConditionEntry.transformFilters(OneVersionedTimeRangeEntity.class, "{'field':'this','op':'on'}");
		r = vtrService.findByFilters(c);
		Assert.assertNotNull(r);
		Assert.assertFalse(r.isEmpty());
		
		// NO ACTIVAS
		c = ConditionEntry.transformFilters(OneVersionedTimeRangeEntity.class, "{'field':'active','op':'eq','data':'false'}");
		r = vtrService.findByFilters(c);
		Assert.assertNotNull(r);
		Assert.assertTrue(r.isEmpty());
		c = ConditionEntry.transformFilters(OneVersionedTimeRangeEntity.class, "{'field':'active','op':'ne','data':'true'}");
		r = vtrService.findByFilters(c);
		Assert.assertNotNull(r);
		Assert.assertTrue(r.isEmpty());
		c = ConditionEntry.transformFilters(OneVersionedTimeRangeEntity.class, "{'field':'this','op':'off'}");
		r = vtrService.findByFilters(c);
		Assert.assertNotNull(r);
		Assert.assertTrue(r.isEmpty());
		
		// RELACIONES ACTIVAS
		c = ConditionEntry.transformFilters(OneVersionedEntity.class, "{'field':'vtr.active','op':'eq','data':'true'}");
		Collection<OneVersionedEntity> r2 = veService.findByFilters(c);
		Assert.assertNotNull(r2);
		Assert.assertFalse(r2.isEmpty());
		c = ConditionEntry.transformFilters(OneVersionedEntity.class, "{'field':'vtr.active','op':'ne','data':'false'}");
		r2 = veService.findByFilters(c);
		Assert.assertNotNull(r2);
		Assert.assertFalse(r2.isEmpty());
		c = ConditionEntry.transformFilters(OneVersionedEntity.class, "{'field':'vtr','op':'on'}");
		r2 = veService.findByFilters(c);
		Assert.assertNotNull(r2);
		Assert.assertFalse(r2.isEmpty());
		
		// RELACIONES NO ACTIVAS
		c = ConditionEntry.transformFilters(OneVersionedEntity.class, "{'field':'vtr.active','op':'eq','data':'false'}");
		r2 = veService.findByFilters(c);
		Assert.assertNotNull(r2);
		Assert.assertTrue(r2.isEmpty());
		c = ConditionEntry.transformFilters(OneVersionedEntity.class, "{'field':'vtr.active','op':'ne','data':'true'}");
		r2 = veService.findByFilters(c);
		Assert.assertNotNull(r2);
		Assert.assertTrue(r2.isEmpty());
		c = ConditionEntry.transformFilters(OneVersionedEntity.class, "{'field':'vtr','op':'off'}");
		r2 = veService.findByFilters(c);
		Assert.assertNotNull(r2);
		Assert.assertTrue(r2.isEmpty());
		
	}	
	 */
	
	
	
	@Test(expected=Exception.class)
	public void transformFilters_operator_need_value_expetion_01() throws Exception{
		ConditionEntry.transformFilters(TestEntity.class, error_valor_nulo_para_operacion_que_lo_requiere);
	}

	@Test(expected=Exception.class)
	public void transformFilters_operator_need_value_expetion_02() throws Exception{
		ConditionEntry.transformFilters(TestEntity.class, error_falta_valor_para_operacion_que_lo_requiere);
	}
	
	@Test(expected=Exception.class)
	public void transformFilters_field_required_exceptions() throws Exception{
		ConditionEntry.transformFilters(TestEntity.class, error_falta_campo_obligatorio);
	}
	
	@Test
	public void transformFilters_operator_need_value_ok() throws Exception{
		List<ConditionEntry> c = ConditionEntry.transformFilters(TestEntity.class, "[{'field':'id','op':'eq','data':'1'}]");
		Assert.assertNotNull(c);
		Assert.assertFalse(c.isEmpty());
		Assert.assertEquals(1, c.size());
	}

	@Test
	public void transformFilters_operator_not_need_value_ok_01() throws Exception{
		List<ConditionEntry> c = ConditionEntry.transformFilters(TestEntity.class, "[{'field':'id','op':'nn','data':''}]");
		Assert.assertNotNull(c);
		Assert.assertFalse(c.isEmpty());
		Assert.assertEquals(1, c.size());	
	}
	
	@Test
	public void transformFilters_operator_not_need_value_ok_02() throws Exception{
		List<ConditionEntry> c = ConditionEntry.transformFilters(TestEntity.class, "[{'field':'id','op':'nn'}]");
		Assert.assertNotNull(c);
		Assert.assertFalse(c.isEmpty());
		Assert.assertEquals(1, c.size());	
	}
	
	@Test
	public void transformFilters_simple_filter_ok_01() throws Exception{
		List<ConditionEntry> c = ConditionEntry.transformFilters(TestEntity.class, filtroSimple01);
		Assert.assertNotNull(c);
		Assert.assertFalse(c.isEmpty());
		Assert.assertEquals(1, c.size());	
	}
	
	@Test
	public void create_conditionSimple() throws Exception{
		ConditionSimple cs = ConditionEntry.getConditionSimple(TestEntity.class, "id", SearchOption.EQUAL, "1");
		Assert.assertNotNull(cs);
		Assert.assertEquals(1, cs.getValue());
		Assert.assertEquals("id", cs.getColumn());
		Assert.assertEquals( SearchOption.EQUAL, cs.getCondition());
	}
		
	@Test
	public void transformFilters_simpleConLista_ok() throws Exception{
		List<ConditionEntry> c = ConditionEntry.transformFilters(TestEntity.class, filtroSimpleConLista01);
		Assert.assertNotNull(c);
		Assert.assertFalse(c.isEmpty());
		Assert.assertEquals(1, c.size());		
	}
	
	@Test
	public void transformFilters_simpleConLista02_ok() throws Exception{
		List<ConditionEntry> c = ConditionEntry.transformFilters(TestEntity.class, filtroSimpleConLista02);
		Assert.assertNotNull(c);
		Assert.assertFalse(c.isEmpty());
		Assert.assertEquals(1, c.size());		
	}
	
	
	
	/* ************************************************* */
	/* ACUMULACION DE FILTROS EN UN UNICO JSON           */
	/* ************************************************* */

	
	
	@Test
	public void acummulateFiltersSimpleSimple_ok() throws Exception{
		String resultado = ConditionEntry.acumulateFilters(filtroSimple01, filtroSimple02);
		List<ConditionEntry> c = ConditionEntry.transformFilters(TestEntity.class, resultado);
		Assert.assertNotNull(c);
		Assert.assertFalse(c.isEmpty());
		Assert.assertEquals(1, c.size());
		ConditionEntry c2 = c.get(0);
		Assert.assertTrue(c2 instanceof ConditionComplex);
		ConditionComplex cc = (ConditionComplex) c2;
		Assert.assertFalse(cc.getConditions().isEmpty());
		Assert.assertEquals(2, cc.getConditions().size());
		Assert.assertTrue(cc.getOperator().equals(Operator.AND));
	}
	
	@Test
	public void acummulateFiltersNull_ok() throws Exception{
		String resultado = ConditionEntry.acumulateFilters(null, filtroSimple02);
		List<ConditionEntry> c = ConditionEntry.transformFilters(TestEntity.class, resultado);
		Assert.assertNotNull(c);
		Assert.assertFalse(c.isEmpty());
		Assert.assertEquals(1, c.size());
	}
	
	@Test
	public void acummulateFiltersEmpty_ok() throws Exception{
		String resultado = ConditionEntry.acumulateFilters("", filtroSimple02);
		List<ConditionEntry> c = ConditionEntry.transformFilters(TestEntity.class, resultado);
		Assert.assertNotNull(c);
		Assert.assertFalse(c.isEmpty());
		Assert.assertEquals(1, c.size());
	}
	
	@Test
	public void acummulateFiltersNull2_ok() throws Exception{
		String resultado = ConditionEntry.acumulateFilters(filtroSimple02, null);
		List<ConditionEntry> c = ConditionEntry.transformFilters(TestEntity.class, resultado);
		Assert.assertNotNull(c);
		Assert.assertFalse(c.isEmpty());
		Assert.assertEquals(1, c.size());
	}
	
	@Test
	public void acummulateFiltersEmptyNull_ok() throws Exception{
		String resultado = ConditionEntry.acumulateFilters("", null);
		List<ConditionEntry> c = ConditionEntry.transformFilters(TestEntity.class, resultado);
		Assert.assertNotNull(c);
		Assert.assertTrue(c.isEmpty());
	}
	
	@Test
	public void acummulateFiltersNullNull_ok() throws Exception{
		String resultado = ConditionEntry.acumulateFilters(null, null);
		List<ConditionEntry> c = ConditionEntry.transformFilters(TestEntity.class, resultado);
		Assert.assertNotNull(c);
		Assert.assertTrue(c.isEmpty());
	}
	
	@Test
	public void acummulateFiltersSimpleArray_ok() throws Exception{
		String resultado = ConditionEntry.acumulateFilters(filtroSimple01, condicionDEFAULT);
		List<ConditionEntry> c = ConditionEntry.transformFilters(TestEntity.class, resultado);
		Assert.assertNotNull(c);
		Assert.assertFalse(c.isEmpty());
		Assert.assertEquals(1, c.size());
		ConditionEntry c2 = c.get(0);
		Assert.assertTrue(c2 instanceof ConditionComplex);
		ConditionComplex cc = (ConditionComplex) c2;
		Assert.assertFalse(cc.getConditions().isEmpty());
		Assert.assertEquals(3, cc.getConditions().size());
		Assert.assertTrue(cc.getOperator().equals(Operator.AND));
	}
	
	@Test
	public void acummulateFiltersArraySimple_ok() throws Exception{
		String resultado = ConditionEntry.acumulateFilters(condicionDEFAULT, filtroSimple01);
		List<ConditionEntry> c = ConditionEntry.transformFilters(TestEntity.class, resultado);
		Assert.assertNotNull(c);
		Assert.assertFalse(c.isEmpty());
		Assert.assertEquals(1, c.size());
		ConditionEntry c2 = c.get(0);
		Assert.assertTrue(c2 instanceof ConditionComplex);
		ConditionComplex cc = (ConditionComplex) c2;
		Assert.assertFalse(cc.getConditions().isEmpty());
		Assert.assertEquals(3, cc.getConditions().size());
		Assert.assertTrue(cc.getOperator().equals(Operator.AND));
	}
	
	@Test
	public void acummulateFiltersArrayArray_ok() throws Exception{
		String resultado = ConditionEntry.acumulateFilters(condicionDEFAULT, condicionDEFAULT);
		List<ConditionEntry> c = ConditionEntry.transformFilters(TestEntity.class, resultado);
		Assert.assertNotNull(c);
		Assert.assertFalse(c.isEmpty());
		Assert.assertEquals(1, c.size());
		ConditionEntry c2 = c.get(0);
		Assert.assertTrue(c2 instanceof ConditionComplex);
		ConditionComplex cc = (ConditionComplex) c2;
		Assert.assertFalse(cc.getConditions().isEmpty());
		Assert.assertEquals(4, cc.getConditions().size());
		Assert.assertTrue(cc.getOperator().equals(Operator.AND));
	}
	
	@Test
	public void acummulateFiltersArrayComplex_ok() throws Exception{
		String resultado = ConditionEntry.acumulateFilters(condicionDEFAULT, condicionOR);
		List<ConditionEntry> c = ConditionEntry.transformFilters(TestEntity.class, resultado);
		Assert.assertNotNull(c);
		Assert.assertFalse(c.isEmpty());
		Assert.assertEquals(1, c.size());
		ConditionEntry c2 = c.get(0);
		Assert.assertTrue(c2 instanceof ConditionComplex);
		ConditionComplex cc = (ConditionComplex) c2;
		Assert.assertFalse(cc.getConditions().isEmpty());
		Assert.assertEquals(3, cc.getConditions().size());
		Assert.assertTrue(cc.getOperator().equals(Operator.AND));
		for (ConditionEntry conditionEntry : cc.getConditions()) {
			if(conditionEntry instanceof ConditionComplex){
				ConditionComplex complex = (ConditionComplex) conditionEntry;
				Assert.assertTrue(complex.getOperator().equals(Operator.OR));
				Assert.assertEquals(2, complex.getConditions().size());
			}
		}
	}
	
	@Test
	public void acummulateFiltersComplexComplex_ok() throws Exception{
		String resultado = ConditionEntry.acumulateFilters(condicionOR, condicionOR);
		List<ConditionEntry> c = ConditionEntry.transformFilters(TestEntity.class, resultado);
		Assert.assertNotNull(c);
		Assert.assertFalse(c.isEmpty());
		Assert.assertEquals(1, c.size());
		ConditionEntry c2 = c.get(0);
		Assert.assertTrue(c2 instanceof ConditionComplex);
		ConditionComplex cc = (ConditionComplex) c2;
		Assert.assertFalse(cc.getConditions().isEmpty());
		Assert.assertEquals(2, cc.getConditions().size());
		Assert.assertTrue(cc.getOperator().equals(Operator.AND));
		for (ConditionEntry conditionEntry : cc.getConditions()) {
			if(conditionEntry instanceof ConditionComplex){
				ConditionComplex complex = (ConditionComplex) conditionEntry;
				Assert.assertTrue(complex.getOperator().equals(Operator.OR));
				Assert.assertEquals(2, complex.getConditions().size());
			}
		}
	}

	@Test
	public void acummulateFiltersSimpleComplex_ok() throws Exception{
		String resultado = ConditionEntry.acumulateFilters(filtroSimple01, condicionOR);
		List<ConditionEntry> c = ConditionEntry.transformFilters(TestEntity.class, resultado);
		Assert.assertNotNull(c);
		Assert.assertFalse(c.isEmpty());
		Assert.assertEquals(1, c.size());
		ConditionEntry c2 = c.get(0);
		Assert.assertTrue(c2 instanceof ConditionComplex);
		ConditionComplex cc = (ConditionComplex) c2;
		Assert.assertFalse(cc.getConditions().isEmpty());
		Assert.assertEquals(2, cc.getConditions().size());
		Assert.assertTrue(cc.getOperator().equals(Operator.AND));
		for (ConditionEntry conditionEntry : cc.getConditions()) {
			if(conditionEntry instanceof ConditionComplex){
				ConditionComplex complex = (ConditionComplex) conditionEntry;
				Assert.assertTrue(complex.getOperator().equals(Operator.OR));
				Assert.assertEquals(2, complex.getConditions().size());
			}
		}
	}
	
	@Test
	public void acummulateFiltersComplexComplex_02_ok() throws Exception{
		String resultado = ConditionEntry.acumulateFilters(condicionCOMPLEJA, condicionCOMPLEJA);
		List<ConditionEntry> c = ConditionEntry.transformFilters(TestEntity.class, resultado);
		Assert.assertNotNull(c);
		Assert.assertFalse(c.isEmpty());
		Assert.assertEquals(1, c.size());
		ConditionEntry c2 = c.get(0);
		Assert.assertTrue(c2 instanceof ConditionComplex);
		ConditionComplex cc = (ConditionComplex) c2;
		Assert.assertFalse(cc.getConditions().isEmpty());
		Assert.assertEquals(2, cc.getConditions().size());
		Assert.assertTrue(cc.getOperator().equals(Operator.AND));
		for (ConditionEntry conditionEntry : cc.getConditions()) {
			if(conditionEntry instanceof ConditionComplex){
				ConditionComplex complex = (ConditionComplex) conditionEntry;
				Assert.assertTrue(complex.getOperator().equals(Operator.AND));
				Assert.assertEquals(3, complex.getConditions().size());
				for (ConditionEntry cce2 : complex.getConditions()) {
					if(cce2 instanceof ConditionComplex){
						ConditionComplex cce3 = (ConditionComplex) cce2;
						Assert.assertTrue(cce3.getOperator().equals(Operator.OR));
						Assert.assertEquals(2, cce3.getConditions().size());
					}
				}
			}
		}
	}


	@Test(expected=InvalidParameterException.class)
	public void acummulateFiltersInvalidSimple_ok() throws Exception{
		ConditionEntry.acumulateFilters(error_falta_campo_obligatorio, filtroSimple02);
		Assert.fail();
	}

	@Test
	public void isDate_ok(){
		Assert.assertTrue(ConditionEntry.isDate("2004-10-25T03:00:00Z"));
		Assert.assertTrue(ConditionEntry.isDate("2016-01-29T13:56:26Z"));
		Assert.assertTrue(ConditionEntry.isDate("2004-10-25"));
		Assert.assertTrue(ConditionEntry.isDate("25/10/2004"));
		Assert.assertFalse(ConditionEntry.isDate("texto"));
		Assert.assertFalse(ConditionEntry.isDate("25102004"));
	}
	
	
	
}
