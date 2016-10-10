package com.curcico.jproject.core.wrapper;

import java.util.Collection;

/**Clase para envolver resultados de una consulta paginada, contiene numero de pagina, 
 * total de paginas, total de registros y una colección de registros
 * @author acurci
 *
 * @param <T>
 */
public class GridWrapper<T> {
	
	Integer 		page 	 = 1;
	Integer 	  	total	 = 1;
	Long 		  	records  = 0L;
	Collection<T> 	rows;
	
	public GridWrapper(Integer page, Integer pageSize, Long records,
			Collection<T> rows) {
		super();
		if (page != null && pageSize!=null){
			if(records.intValue()>0){
				this.page = page;
				if(pageSize!=0){
					this.total = ((records.intValue()%pageSize)==0?(records.intValue()/pageSize):((records.intValue()/pageSize)+1));
				}
			}
		}
		this.records = records;
		this.rows = rows;
	}

	/**
	 * @return the page
	 */
	public Integer getPage() {
		return page;
	}
	/**
	 * @param page the page to set
	 */
	public void setPage(Integer page) {
		this.page = page;
	}
	/**
	 * @return the total
	 */
	public Integer getTotal() {
		return total;
	}
	/**
	 * @param total the total to set
	 */
	public void setTotal(Integer total) {
		this.total = total;
	}
	/**
	 * @return the records
	 */
	public Long getRecords() {
		return records;
	}
	/**
	 * @param records the records to set
	 */
	public void setRecords(Long records) {
		this.records = records;
	}
	/**
	 * @return the rows
	 */
	public Collection<T> getRows() {
		return rows;
	}
	/**
	 * @param rows the rows to set
	 */
	public void setRows(Collection<T> rows) {
		this.rows = rows;
	}
} 