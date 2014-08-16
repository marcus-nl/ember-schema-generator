package nl.marcus.embermg;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.base.Optional;

public class EmberModelWriter implements Closeable {
	private final Writer writer;
	
	public EmberModelWriter() {
		this.writer = new BufferedWriter(new OutputStreamWriter(System.out)); 
	}
	
	@Override
	public void close() {
		try {
			writer.close();
		}
		catch (IOException e) {
			throw new RuntimeException("", e);
		}
	}

	private void append(String s) {
		try {
			writer.append(s);
		}
		catch (IOException e) {
			throw new RuntimeException("", e);
		}
	}

	public void startModel(EmberClass emberClass) {
		Optional<EmberClass> superType = emberClass.getSuperType();
		String superName = superType.isPresent() ? superType.get().getName() : "";
		
		startModel(emberClass.getName(), superName, "(alias)");
	}

	public void startModel(String modelName, String superName, String alias) {
		append("App.");
		append(modelName);
		append(" = ");
		append("DS.defineModel(");
		
		Map<String, String> options = new LinkedHashMap<>();
		options.put("extends", superName);
		options.put("alias", alias);
		writeOptions(options);

		append("{\n");
	}
	
	private void writeOptions(Map<String,String> options) {
		append("{\n");
		for (Map.Entry<String,String> option : options.entrySet()) {
			append("\t");
			append(quote(option.getKey()));
			append(": ");
			append(quote(option.getValue()));
			append(",\n");
		}
		append("}, ");
	}

	public void endModel() {
		append("});\n\n");
	}

	public void addProperty(String propertyName, String type) {
		append("\t");
		append(quote(propertyName));
		append(": ");
		append(type);
		append(",\n");
	}

	protected String quote(String s) {
		return s == null ? "null" : "'" + s + "'";
	}
}