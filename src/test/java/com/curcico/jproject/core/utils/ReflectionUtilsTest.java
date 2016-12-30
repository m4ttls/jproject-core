package com.curcico.jproject.core.utils;

import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.curcico.jproject.core.entities.OneBaseEntity;
import com.curcico.jproject.core.entities.OneBaseTimeRangeEntity;
import com.curcico.jproject.core.exception.ReflectionException;

@ContextConfiguration(locations = {"classpath:spring/application-config.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class ReflectionUtilsTest {
	
	private static String allowedDateFormats[] = new String[]{"yyyy-MM-dd'T'HH:mm:ss.S'Z'", "dd/MM/yyyy", "dd-MM-yyyy"};
	
	Logger log = Logger.getLogger(getClass());
	
	@Test
	public void parseDate() throws ReflectionException  {
		
		String parsableDate = "2013-09-29T18:46:19.5Z";
		Assert.assertNotNull(ReflectionUtils.castField(SimpleDateContainer.class, "testDate", parsableDate));
		Assert.assertNotNull(ReflectionUtils.castField(SimpleDateContainer.class, "testSqlDate", parsableDate));
		Assert.assertNotNull(ReflectionUtils.castField(SimpleDateContainer.class, "testTimestamp", parsableDate));
		
		Assert.assertNotNull(ReflectionUtils.parseDateWithMultiFormat(parsableDate, allowedDateFormats));
		
		parsableDate = "29-09-2013";
		ReflectionUtils.castField(SimpleDateContainer.class, "testDate", parsableDate);
		Assert.assertNotNull(ReflectionUtils.parseDateWithMultiFormat(parsableDate, allowedDateFormats));
		
		parsableDate = "29/09/2013"; 
		Assert.assertNotNull(ReflectionUtils.parseDateWithMultiFormat(parsableDate, allowedDateFormats));
		
		
		String unparsableDate = "2014/11/12";
		Assert.assertNull(ReflectionUtils.parseDateWithMultiFormat(unparsableDate, allowedDateFormats));
		
		unparsableDate = "29-00-2013";
		Assert.assertNull(ReflectionUtils.parseDateWithMultiFormat(unparsableDate, allowedDateFormats));
		
		unparsableDate = "50-01-2013";
		Assert.assertNull(ReflectionUtils.parseDateWithMultiFormat(unparsableDate, allowedDateFormats));
		
		unparsableDate = "2013-09-2918:46:19.5";
		Assert.assertNull(ReflectionUtils.parseDateWithMultiFormat(unparsableDate, allowedDateFormats));
	}
	
	@Test
	public void getCast_ok(){
		Class<?> c = ReflectionUtils.getCast(OneBaseEntity.class, "vtr");
		Assert.assertTrue(c.isAssignableFrom(OneBaseTimeRangeEntity.class));
	}
	
	
	
	
	class SimpleDateContainer{
		
		java.util.Date testDate;
		java.sql.Date testSqlDate;
		Timestamp testTimestamp;
		
		public SimpleDateContainer() {
			super();
		}

		public java.util.Date getTestDate() {
			return testDate;
		}

		public void setTestDate(java.util.Date testDate) {
			this.testDate = testDate;
		}

		public java.sql.Date getTestSqlDate() {
			return testSqlDate;
		}

		public void setTestSqlDate(java.sql.Date testSqlDate) {
			this.testSqlDate = testSqlDate;
		}

		public Timestamp getTestTimestamp() {
			return testTimestamp;
		}

		public void setTestTimestamp(Timestamp testTimestamp) {
			this.testTimestamp = testTimestamp;
		}

	}
}

