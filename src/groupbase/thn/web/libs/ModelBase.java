package groupbase.thn.web.libs;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

public abstract class ModelBase {
	private RequestDispatcher mDispatcher;
	private String mPathView = "/";
	protected HttpServletRequest mRequest;
	protected HttpServletResponse mResponse;
	protected PrintWriter mPrintWriter;
	protected String mContentType = "text/plain; charset=utf-8";
	private ServletContext mServletContext;

	protected ModelBase(ServletContext servletContext, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		mServletContext = servletContext;
		mRequest = request;
		mResponse = response;
		mResponse.setContentType(mContentType);
		mPrintWriter = mResponse.getWriter();

	}

	protected void viewResult(String viewName) {
		mDispatcher = mServletContext.getRequestDispatcher(
				getPath() + viewName + ".jsp");
		try {
			mDispatcher.forward(mRequest, mResponse);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void Redirect(String url) {
		try {
			this.mResponse.sendRedirect(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	protected void outString(String text) {
		mPrintWriter.println(text);
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



	protected <T> T getQueryStringObject(Class<T> object) {
		return null;
	}

	protected String getPathRoot() {
		return mRequest.getServletContext().getRealPath("");
	}

	protected String outTextFromFile(String pathFile) {
		try {
			FileInputStream fis = new FileInputStream(getPathRoot() + pathFile);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader reader = new BufferedReader(isr);
			String line = null;
			String result = "";
			while ((line = reader.readLine()) != null) {
				result = result + line;
			}
			reader.close();
			fis.close();
			isr.close();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

	}

	protected void putData(String key, Object data) {
		mRequest.setAttribute(key, data);
	}

	private String getPath() {		
		if (mServletContext.getInitParameter("View") == null) {
			return mPathView;
		}
		if (mPathView.length() == 1) {
			return mServletContext.getInitParameter("View");
		} else {
			return mPathView;
		}
	}

	protected void setPathView(String pathView) {
		mPathView = pathView;
	}

	public abstract void defaultView();
}
