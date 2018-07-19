package com.hysm.service;

import org.bson.Document;

import com.alibaba.fastjson.JSONArray;

public interface musicService {
	
	int save(Document doc);

	Document query(Document doc, int pag, int limi);

	Document select_ById(String tMusic, String _id);

	void update(String tMusic, Document doc, String _id);

	JSONArray findAllmusic();
}
