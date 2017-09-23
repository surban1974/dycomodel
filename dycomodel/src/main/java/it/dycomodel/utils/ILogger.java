package it.dycomodel.utils;

import java.io.Serializable;
import java.util.Map;

public interface ILogger extends Serializable{
	void addThrowable(Throwable t);
	void addString(String s, String type);
	void addParametrized(String s, String type, Map<String, String> parameters);
	void addMessage(String id, String type, Map<String, String> parameters);
}
