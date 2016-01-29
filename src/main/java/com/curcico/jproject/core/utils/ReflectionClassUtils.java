package com.curcico.jproject.core.utils;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.vfs.Vfs;

public class ReflectionClassUtils extends Reflections {

	// Logger
	private static Logger logger = Logger.getLogger(ReflectionClassUtils.class);
	private final int TAIL_LENGTH = ".class".length();

	public ReflectionClassUtils(final Object... params) {
		super(ConfigurationBuilder.build(params));
	}

	// Metodos
	public Set<String> getClassesFromPackage() {
		Set<String> classes = new HashSet<String>();
		
		for (final URL url : configuration.getUrls()) {
			logger.info("scan: " + url);
			for (final Vfs.File file : Vfs.fromURL(url).getFiles()) {
	            String input = file.getRelativePath().replace('/', '.');
	            if (configuration.getInputsFilter() == null || configuration.getInputsFilter().apply(input)) {
	            	classes.add(input.substring(0, input.length()-TAIL_LENGTH));
	            }
	        }
		}
		return classes;
	}
}
