package spring.context.annotation.editor;

import org.springframework.stereotype.Component;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatePropertyEditor extends PropertyEditorSupport {
	private String format = "yyyy-MM-dd";

	public void setFormat(String format) {
		this.format = format;
	}

	public void setAsText(String arg)  {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		Date parse = null;
		try {
			parse = simpleDateFormat.parse(arg);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.setValue(parse);
	}
}
