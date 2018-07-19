package com.hysm.service;

import java.util.List;

import org.bson.Document;

public interface adService {
	
	int save(Document doc);
	
	public List<Document> getALL(String db_name,Document doc);
	
	public Document select_ById(String db_name,String _id);
	
	public void update(String db_name,Document doc,String _id);
	
	public List<Document> select_ByTitle(String db_name,String title);
	
	public void  change(Document doc);

	Document query(Document doc, int pag, int limi);

}
