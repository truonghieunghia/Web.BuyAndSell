package groupbase.thn.web.libs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

public abstract class ModelBase {
	private HttpServletRequest mRequest;

	public ModelBase( HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		mRequest = request;

	}
	private String getPathRoot() {
		return mRequest.getServletContext().getRealPath("");
	}

	private <T> T paserStringToType(String str, Class<T> clazz) {
		Object value = null;
		if (clazz.equals(String.class)) {
			value = str;
		}
		if (clazz.equals(Integer.class)) {
			value = Integer.parseInt(str);
		}
		if (clazz.equals(Float.class)) {
			value = Float.parseFloat(str);
		}
		return clazz.cast(value);
	}
	protected <T> T getDataPost(Class<T> formData) {
		try {
			List<Field> fields = new ArrayList<Field>();
			fields.addAll(Arrays.asList(formData.getDeclaredFields()));
			Object result = formData.newInstance();

			for (Field field : fields) {
				field.setAccessible(true);
				FormAnnotation formAnnotation = field
						.getAnnotation(FormAnnotation.class);
				if (formAnnotation != null) {
					String[] value = mRequest.getParameterValues(formAnnotation
							.Name());

					if (value != null) {
						if (value.length == 1) {
							if (value[0].trim().length() == 0) {
								field.set(result, null);
							} else {
								field.set(
										result,
										paserStringToType(value[0],
												formAnnotation.FieldType()));
							}
						} else {
							field.set(result, value);
						}
					}
				}
			}
			return formData.cast(result);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	protected String getFileName(final Part part) {

		for (String content : part.getHeader("content-disposition").split(";")) {
			if (content.trim().startsWith("filename")) {
				return content.substring(content.indexOf('=') + 1).trim()
						.replace("\"", "");
			}
		}
		return null;
	}

	protected void upload(String pathFolder, String partName, String fileName) {
		try {
			final String path = getPathRoot() + pathFolder.trim();
			final Part filePart = mRequest.getPart(partName);
			final String tempfile = getFileName(filePart);
			if (fileName == null | fileName.trim().length() == 0) {
				fileName = tempfile;
			} else {

				fileName = fileName
						+ tempfile.substring(tempfile.lastIndexOf("."),
								tempfile.length());
			}
			OutputStream out = null;
			InputStream filecontent = null;
			out = new FileOutputStream(new File(path + File.separator
					+ fileName));
			filecontent = filePart.getInputStream();

			int read = 0;
			final byte[] bytes = new byte[1024];

			while ((read = filecontent.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			if (out != null) {
				out.close();
			}
			if (filecontent != null) {
				filecontent.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void upload(String pathFolder, Part part, String fileName) {
		try {
			final String path = getPathRoot() + pathFolder.trim();
			final Part filePart = part;
			final String tempfile = getFileName(filePart);
			if (fileName == null | fileName.trim().length() == 0) {
				fileName = tempfile;
			} else {

				fileName = fileName
						+ tempfile.substring(tempfile.lastIndexOf("."),
								tempfile.length());
			}
			OutputStream out = null;
			InputStream filecontent = null;
			out = new FileOutputStream(new File(path + File.separator
					+ fileName));
			filecontent = filePart.getInputStream();

			int read = 0;
			final byte[] bytes = new byte[1024];

			while ((read = filecontent.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			if (out != null) {
				out.close();
			}
			if (filecontent != null) {
				filecontent.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected ArrayList<Part> getPart(String partName) {
		try {
			return new ArrayList<Part>(mRequest.getParts());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	protected void putData(String key, Object data) {
		mRequest.setAttribute(key, data);
	}
}
