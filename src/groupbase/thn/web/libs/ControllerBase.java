package groupbase.thn.web.libs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.DispatcherType;
import javax.servlet.GenericServlet;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.Part;


public abstract class ControllerBase<M extends ModelBase> extends GenericServlet {

	private static final long serialVersionUID = 1L;

	private static final String METHOD_DELETE = "DELETE";
	private static final String METHOD_HEAD = "HEAD";
	private static final String METHOD_GET = "GET";
	private static final String METHOD_OPTIONS = "OPTIONS";
	private static final String METHOD_POST = "POST";
	private static final String METHOD_PUT = "PUT";
	private static final String METHOD_TRACE = "TRACE";

	private static final String HEADER_IFMODSINCE = "If-Modified-Since";
	private static final String HEADER_LASTMOD = "Last-Modified";

	private static final String LSTRING_FILE = "javax.servlet.http.LocalStrings";
	private static ResourceBundle lStrings = ResourceBundle.getBundle(LSTRING_FILE);
	private HttpServletRequest mRequest;
	private HttpServletResponse mResponse;
	public M Model;
	private PrintWriter mPrintWriter;
	private String mPathView = "/";
	protected String mContentType = "text/plain; charset=utf-8";

	/**
	 * Does nothing, because this is an abstract class.
	 */
	public ControllerBase() {
		// NOOP
	}

	/**
	 * Called by the server (via the <code>service</code> method) to allow a
	 * servlet to handle a GET request.
	 *
	 * <p>
	 * Overriding this method to support a GET request also automatically
	 * supports an HTTP HEAD request. A HEAD request is a GET request that
	 * returns no body in the response, only the request header fields.
	 *
	 * <p>
	 * When overriding this method, read the request data, write the response
	 * headers, get the response's writer or output stream object, and finally,
	 * write the response data. It's best to include content type and encoding.
	 * When using a <code>PrintWriter</code> object to return the response, set
	 * the content type before accessing the <code>PrintWriter</code> object.
	 *
	 * <p>
	 * The servlet container must write the headers before committing the
	 * response, because in HTTP the headers must be sent before the response
	 * body.
	 *
	 * <p>
	 * Where possible, set the Content-Length header (with the
	 * {@link javax.servlet.ServletResponse#setContentLength} method), to allow
	 * the servlet container to use a persistent connection to return its
	 * response to the client, improving performance. The content length is
	 * automatically set if the entire response fits inside the response buffer.
	 *
	 * <p>
	 * When using HTTP 1.1 chunked encoding (which means that the response has a
	 * Transfer-Encoding header), do not set the Content-Length header.
	 *
	 * <p>
	 * The GET method should be safe, that is, without any side effects for
	 * which users are held responsible. For example, most form queries have no
	 * side effects. If a client request is intended to change stored data, the
	 * request should use some other HTTP method.
	 *
	 * <p>
	 * The GET method should also be idempotent, meaning that it can be safely
	 * repeated. Sometimes making a method safe also makes it idempotent. For
	 * example, repeating queries is both safe and idempotent, but buying a
	 * product online or modifying data is neither safe nor idempotent.
	 *
	 * <p>
	 * If the request is incorrectly formatted, <code>doGet</code> returns an
	 * HTTP "Bad Request" message.
	 *
	 * @param req
	 *            an {@link HttpServletRequest} object that contains the request
	 *            the client has made of the servlet
	 *
	 * @param resp
	 *            an {@link HttpServletResponse} object that contains the
	 *            response the servlet sends to the client
	 *
	 * @exception IOException
	 *                if an input or output error is detected when the servlet
	 *                handles the GET request
	 *
	 * @exception ServletException
	 *                if the request for the GET could not be handled
	 *
	 * @see javax.servlet.ServletResponse#setContentType
	 */
	private void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		String protocol = req.getProtocol();
//		String msg = lStrings.getString("http.method_get_not_supported");
//		if (protocol.endsWith("1.1")) {
//			resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
//		} else {
//			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
//		}
		showView(doGet());
	}

	protected abstract View doGet();

	/**
	 * Returns the time the <code>HttpServletRequest</code> object was last
	 * modified, in milliseconds since midnight January 1, 1970 GMT. If the time
	 * is unknown, this method returns a negative number (the default).
	 *
	 * <p>
	 * Servlets that support HTTP GET requests and can quickly determine their
	 * last modification time should override this method. This makes browser
	 * and proxy caches work more effectively, reducing the load on server and
	 * network resources.
	 *
	 * @param req
	 *            the <code>HttpServletRequest</code> object that is sent to the
	 *            servlet
	 *
	 * @return a <code>long</code> integer specifying the time the
	 *         <code>HttpServletRequest</code> object was last modified, in
	 *         milliseconds since midnight, January 1, 1970 GMT, or -1 if the
	 *         time is not known
	 */
	protected long getLastModified(HttpServletRequest req) {
		return -1;
	}

	/**
	 * <p>
	 * Receives an HTTP HEAD request from the protected <code>service</code>
	 * method and handles the request. The client sends a HEAD request when it
	 * wants to see only the headers of a response, such as Content-Type or
	 * Content-Length. The HTTP HEAD method counts the output bytes in the
	 * response to set the Content-Length header accurately.
	 *
	 * <p>
	 * If you override this method, you can avoid computing the response body
	 * and just set the response headers directly to improve performance. Make
	 * sure that the <code>doHead</code> method you write is both safe and
	 * idempotent (that is, protects itself from being called multiple times for
	 * one HTTP HEAD request).
	 *
	 * <p>
	 * If the HTTP HEAD request is incorrectly formatted, <code>doHead</code>
	 * returns an HTTP "Bad Request" message.
	 *
	 * @param req
	 *            the request object that is passed to the servlet
	 *
	 * @param resp
	 *            the response object that the servlet uses to return the
	 *            headers to the client
	 *
	 * @exception IOException
	 *                if an input or output error occurs
	 *
	 * @exception ServletException
	 *                if the request for the HEAD could not be handled
	 */
	protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		if (DispatcherType.INCLUDE.equals(req.getDispatcherType())) {
			doGet(req, resp);
		} else {
			NoBodyResponse response = new NoBodyResponse(resp);
			doGet(req, response);
			response.setContentLength();
		}
	}

	/**
	 * Called by the server (via the <code>service</code> method) to allow a
	 * servlet to handle a POST request.
	 *
	 * The HTTP POST method allows the client to send data of unlimited length
	 * to the Web server a single time and is useful when posting information
	 * such as credit card numbers.
	 *
	 * <p>
	 * When overriding this method, read the request data, write the response
	 * headers, get the response's writer or output stream object, and finally,
	 * write the response data. It's best to include content type and encoding.
	 * When using a <code>PrintWriter</code> object to return the response, set
	 * the content type before accessing the <code>PrintWriter</code> object.
	 *
	 * <p>
	 * The servlet container must write the headers before committing the
	 * response, because in HTTP the headers must be sent before the response
	 * body.
	 *
	 * <p>
	 * Where possible, set the Content-Length header (with the
	 * {@link javax.servlet.ServletResponse#setContentLength} method), to allow
	 * the servlet container to use a persistent connection to return its
	 * response to the client, improving performance. The content length is
	 * automatically set if the entire response fits inside the response buffer.
	 *
	 * <p>
	 * When using HTTP 1.1 chunked encoding (which means that the response has a
	 * Transfer-Encoding header), do not set the Content-Length header.
	 *
	 * <p>
	 * This method does not need to be either safe or idempotent. Operations
	 * requested through POST can have side effects for which the user can be
	 * held accountable, for example, updating stored data or buying items
	 * online.
	 *
	 * <p>
	 * If the HTTP POST request is incorrectly formatted, <code>doPost</code>
	 * returns an HTTP "Bad Request" message.
	 *
	 *
	 * @param req
	 *            an {@link HttpServletRequest} object that contains the request
	 *            the client has made of the servlet
	 *
	 * @param resp
	 *            an {@link HttpServletResponse} object that contains the
	 *            response the servlet sends to the client
	 *
	 * @exception IOException
	 *                if an input or output error is detected when the servlet
	 *                handles the request
	 *
	 * @exception ServletException
	 *                if the request for the POST could not be handled
	 *
	 * @see javax.servlet.ServletOutputStream
	 * @see javax.servlet.ServletResponse#setContentType
	 */
	private void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

//		String protocol = req.getProtocol();
//		String msg = lStrings.getString("http.method_post_not_supported");
//		if (protocol.endsWith("1.1")) {
//			resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
//		} else {
//			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
//		}
		showView(doPost());

	}

	protected abstract View doPost();

	/**
	 * Called by the server (via the <code>service</code> method) to allow a
	 * servlet to handle a PUT request.
	 *
	 * The PUT operation allows a client to place a file on the server and is
	 * similar to sending a file by FTP.
	 *
	 * <p>
	 * When overriding this method, leave intact any content headers sent with
	 * the request (including Content-Length, Content-Type,
	 * Content-Transfer-Encoding, Content-Encoding, Content-Base,
	 * Content-Language, Content-Location, Content-MD5, and Content-Range). If
	 * your method cannot handle a content header, it must issue an error
	 * message (HTTP 501 - Not Implemented) and discard the request. For more
	 * information on HTTP 1.1, see RFC 2616
	 * <a href="http://www.ietf.org/rfc/rfc2616.txt"></a>.
	 *
	 * <p>
	 * This method does not need to be either safe or idempotent. Operations
	 * that <code>doPut</code> performs can have side effects for which the user
	 * can be held accountable. When using this method, it may be useful to save
	 * a copy of the affected URL in temporary storage.
	 *
	 * <p>
	 * If the HTTP PUT request is incorrectly formatted, <code>doPut</code>
	 * returns an HTTP "Bad Request" message.
	 *
	 * @param req
	 *            the {@link HttpServletRequest} object that contains the
	 *            request the client made of the servlet
	 *
	 * @param resp
	 *            the {@link HttpServletResponse} object that contains the
	 *            response the servlet returns to the client
	 *
	 * @exception IOException
	 *                if an input or output error occurs while the servlet is
	 *                handling the PUT request
	 *
	 * @exception ServletException
	 *                if the request for the PUT cannot be handled
	 */
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String protocol = req.getProtocol();
		String msg = lStrings.getString("http.method_put_not_supported");
		if (protocol.endsWith("1.1")) {
			resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
		} else {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
		}
	}

	/**
	 * Called by the server (via the <code>service</code> method) to allow a
	 * servlet to handle a DELETE request.
	 *
	 * The DELETE operation allows a client to remove a document or Web page
	 * from the server.
	 *
	 * <p>
	 * This method does not need to be either safe or idempotent. Operations
	 * requested through DELETE can have side effects for which users can be
	 * held accountable. When using this method, it may be useful to save a copy
	 * of the affected URL in temporary storage.
	 *
	 * <p>
	 * If the HTTP DELETE request is incorrectly formatted,
	 * <code>doDelete</code> returns an HTTP "Bad Request" message.
	 *
	 * @param req
	 *            the {@link HttpServletRequest} object that contains the
	 *            request the client made of the servlet
	 *
	 *
	 * @param resp
	 *            the {@link HttpServletResponse} object that contains the
	 *            response the servlet returns to the client
	 *
	 * @exception IOException
	 *                if an input or output error occurs while the servlet is
	 *                handling the DELETE request
	 *
	 * @exception ServletException
	 *                if the request for the DELETE cannot be handled
	 */
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String protocol = req.getProtocol();
		String msg = lStrings.getString("http.method_delete_not_supported");
		if (protocol.endsWith("1.1")) {
			resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
		} else {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
		}
	}

	private static Method[] getAllDeclaredMethods(Class<?> c) {

		if (c.equals(javax.servlet.http.HttpServlet.class)) {
			return null;
		}

		Method[] parentMethods = getAllDeclaredMethods(c.getSuperclass());
		Method[] thisMethods = c.getDeclaredMethods();

		if ((parentMethods != null) && (parentMethods.length > 0)) {
			Method[] allMethods = new Method[parentMethods.length + thisMethods.length];
			System.arraycopy(parentMethods, 0, allMethods, 0, parentMethods.length);
			System.arraycopy(thisMethods, 0, allMethods, parentMethods.length, thisMethods.length);

			thisMethods = allMethods;
		}

		return thisMethods;
	}

	/**
	 * Called by the server (via the <code>service</code> method) to allow a
	 * servlet to handle a OPTIONS request.
	 *
	 * The OPTIONS request determines which HTTP methods the server supports and
	 * returns an appropriate header. For example, if a servlet overrides
	 * <code>doGet</code>, this method returns the following header:
	 *
	 * <p>
	 * <code>Allow: GET, HEAD, TRACE, OPTIONS</code>
	 *
	 * <p>
	 * There's no need to override this method unless the servlet implements new
	 * HTTP methods, beyond those implemented by HTTP 1.1.
	 *
	 * @param req
	 *            the {@link HttpServletRequest} object that contains the
	 *            request the client made of the servlet
	 *
	 * @param resp
	 *            the {@link HttpServletResponse} object that contains the
	 *            response the servlet returns to the client
	 *
	 * @exception IOException
	 *                if an input or output error occurs while the servlet is
	 *                handling the OPTIONS request
	 *
	 * @exception ServletException
	 *                if the request for the OPTIONS cannot be handled
	 */
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		Method[] methods = getAllDeclaredMethods(this.getClass());

		boolean ALLOW_GET = false;
		boolean ALLOW_HEAD = false;
		boolean ALLOW_POST = false;
		boolean ALLOW_PUT = false;
		boolean ALLOW_DELETE = false;
		boolean ALLOW_TRACE = true;
		boolean ALLOW_OPTIONS = true;

		for (int i = 0; i < methods.length; i++) {
			Method m = methods[i];

			if (m.getName().equals("doGet")) {
				ALLOW_GET = true;
				ALLOW_HEAD = true;
			}
			if (m.getName().equals("doPost"))
				ALLOW_POST = true;
			if (m.getName().equals("doPut"))
				ALLOW_PUT = true;
			if (m.getName().equals("doDelete"))
				ALLOW_DELETE = true;
		}

		String allow = null;
		if (ALLOW_GET)
			allow = METHOD_GET;
		if (ALLOW_HEAD)
			if (allow == null)
				allow = METHOD_HEAD;
			else
				allow += ", " + METHOD_HEAD;
		if (ALLOW_POST)
			if (allow == null)
				allow = METHOD_POST;
			else
				allow += ", " + METHOD_POST;
		if (ALLOW_PUT)
			if (allow == null)
				allow = METHOD_PUT;
			else
				allow += ", " + METHOD_PUT;
		if (ALLOW_DELETE)
			if (allow == null)
				allow = METHOD_DELETE;
			else
				allow += ", " + METHOD_DELETE;
		if (ALLOW_TRACE)
			if (allow == null)
				allow = METHOD_TRACE;
			else
				allow += ", " + METHOD_TRACE;
		if (ALLOW_OPTIONS)
			if (allow == null)
				allow = METHOD_OPTIONS;
			else
				allow += ", " + METHOD_OPTIONS;

		resp.setHeader("Allow", allow);
	}

	/**
	 * Called by the server (via the <code>service</code> method) to allow a
	 * servlet to handle a TRACE request.
	 *
	 * A TRACE returns the headers sent with the TRACE request to the client, so
	 * that they can be used in debugging. There's no need to override this
	 * method.
	 *
	 * @param req
	 *            the {@link HttpServletRequest} object that contains the
	 *            request the client made of the servlet
	 *
	 * @param resp
	 *            the {@link HttpServletResponse} object that contains the
	 *            response the servlet returns to the client
	 *
	 * @exception IOException
	 *                if an input or output error occurs while the servlet is
	 *                handling the TRACE request
	 *
	 * @exception ServletException
	 *                if the request for the TRACE cannot be handled
	 */
	protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		int responseLength;

		String CRLF = "\r\n";
		StringBuilder buffer = new StringBuilder("TRACE ").append(req.getRequestURI()).append(" ")
				.append(req.getProtocol());

		Enumeration<String> reqHeaderEnum = req.getHeaderNames();

		while (reqHeaderEnum.hasMoreElements()) {
			String headerName = reqHeaderEnum.nextElement();
			buffer.append(CRLF).append(headerName).append(": ").append(req.getHeader(headerName));
		}

		buffer.append(CRLF);

		responseLength = buffer.length();

		resp.setContentType("message/http");
		resp.setContentLength(responseLength);
		ServletOutputStream out = resp.getOutputStream();
		out.print(buffer.toString());
		out.close();
		return;
	}

	/**
	 * Receives standard HTTP requests from the public <code>service</code>
	 * method and dispatches them to the <code>do</code><i>Method</i> methods
	 * defined in this class. This method is an HTTP-specific version of the
	 * {@link javax.servlet.Servlet#service} method. There's no need to override
	 * this method.
	 *
	 * @param req
	 *            the {@link HttpServletRequest} object that contains the
	 *            request the client made of the servlet
	 *
	 * @param resp
	 *            the {@link HttpServletResponse} object that contains the
	 *            response the servlet returns to the client
	 *
	 * @exception IOException
	 *                if an input or output error occurs while the servlet is
	 *                handling the HTTP request
	 *
	 * @exception ServletException
	 *                if the HTTP request cannot be handled
	 *
	 * @see javax.servlet.Servlet#service
	 */
	private void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String method = req.getMethod();

		if (method.equals(METHOD_GET)) {
			long lastModified = getLastModified(req);
			if (lastModified == -1) {
				// servlet doesn't support if-modified-since, no reason
				// to go through further expensive logic
				doGet(req, resp);
			} else {
				long ifModifiedSince;
				try {
					ifModifiedSince = req.getDateHeader(HEADER_IFMODSINCE);
				} catch (IllegalArgumentException iae) {
					// Invalid date header - proceed as if none was set
					ifModifiedSince = -1;
				}
				if (ifModifiedSince < (lastModified / 1000 * 1000)) {
					// If the servlet mod time is later, call doGet()
					// Round down to the nearest second for a proper compare
					// A ifModifiedSince of -1 will always be less
					maybeSetLastModified(resp, lastModified);
					doGet(req, resp);
				} else {
					resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
				}
			}

		} else if (method.equals(METHOD_HEAD)) {
			long lastModified = getLastModified(req);
			maybeSetLastModified(resp, lastModified);
			doHead(req, resp);

		} else if (method.equals(METHOD_POST)) {
			doPost(req, resp);

		} else if (method.equals(METHOD_PUT)) {
			doPut(req, resp);

		} else if (method.equals(METHOD_DELETE)) {
			doDelete(req, resp);

		} else if (method.equals(METHOD_OPTIONS)) {
			doOptions(req, resp);

		} else if (method.equals(METHOD_TRACE)) {
			doTrace(req, resp);

		} else {
			//
			// Note that this means NO servlet supports whatever
			// method was requested, anywhere on this server.
			//

			String errMsg = lStrings.getString("http.method_not_implemented");
			Object[] errArgs = new Object[1];
			errArgs[0] = method;
			errMsg = MessageFormat.format(errMsg, errArgs);

			resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, errMsg);
		}
	}

	/*
	 * Sets the Last-Modified entity header field, if it has not already been
	 * set and if the value is meaningful. Called before doGet, to ensure that
	 * headers are set before response data is written. A subclass might have
	 * set this header already, so we check.
	 */
	private void maybeSetLastModified(HttpServletResponse resp, long lastModified) {
		if (resp.containsHeader(HEADER_LASTMOD))
			return;
		if (lastModified >= 0)
			resp.setDateHeader(HEADER_LASTMOD, lastModified);
	}

	/**
	 * Dispatches client requests to the protected <code>service</code> method.
	 * There's no need to override this method.
	 *
	 * @param req
	 *            the {@link HttpServletRequest} object that contains the
	 *            request the client made of the servlet
	 *
	 * @param res
	 *            the {@link HttpServletResponse} object that contains the
	 *            response the servlet returns to the client
	 *
	 * @exception IOException
	 *                if an input or output error occurs while the servlet is
	 *                handling the HTTP request
	 *
	 * @exception ServletException
	 *                if the HTTP request cannot be handled
	 *
	 * @see javax.servlet.Servlet#service
	 */
	@Override
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {

		try {
			mRequest = (HttpServletRequest) req;
			mResponse = (HttpServletResponse) res;
			mResponse.setContentType(mContentType);
			mPrintWriter = mResponse.getWriter();
		} catch (ClassCastException e) {
			throw new ServletException("non-HTTP request or response");
		}
		createModel(mRequest);
		service(mRequest, mResponse);
		
	}

	private void createModel(HttpServletRequest request) {
		@SuppressWarnings("unchecked")
		Class<M> obj = (Class<M>) ((ParameterizedType) this.getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
		try {
			Model = obj.getConstructor( HttpServletRequest.class)
					.newInstance( request);
//			Model= obj.getDeclaredConstructor(HttpServletRequest.class, HttpServletResponse.class).newInstance(request, response);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String getPath() {
		ServletContext context = getServletContext();
		if (context.getInitParameter("View") == null) {
			return mPathView;
		}
		if (mPathView.length() == 1) {
			return context.getInitParameter("View");
		} else {
			return mPathView;
		}
	}

	protected void setPathView(String pathView) {
		mPathView = pathView;
	}

	private void showView(View view) {
		RequestDispatcher mDispatcher = getServletContext()
				.getRequestDispatcher(getPath() + view.getViewName() + ".jsp");
		switch (view.getViewAction()) {
		case FORWARD:
			try {
				mDispatcher.forward(mRequest, mResponse);
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case REDIRECT:
			try {
				this.mResponse.sendRedirect(view.getViewName());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case OUTTEXT:
			if (view.isFile()){
				outString(outTextFromFile( view.getViewName()));
			}else{
				outString(view.getViewName());
			}
			
			break;
		default:

			try {
				mDispatcher.forward(mRequest, mResponse);
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}

	}

	private void outString(String text) {
		mPrintWriter.println(text);
	}
	protected void putData(String key, Object data) {
		mRequest.setAttribute(key, data);
	}
	private String getPathRoot() {
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
	protected <T> T getQueryStringObject(Class<T> object) {
		return null;
	}
}

/*
 * A response wrapper for use in (dumb) "HEAD" support. This just swallows that
 * body, counting the bytes in order to set the content length appropriately.
 * All other methods delegate to the wrapped HTTP Servlet Response object.
 */
// file private
class NoBodyResponse extends HttpServletResponseWrapper {
	private NoBodyOutputStream noBody;
	private PrintWriter writer;
	private boolean didSetContentLength;

	// file private
	NoBodyResponse(HttpServletResponse r) {
		super(r);
		noBody = new NoBodyOutputStream();
	}

	// file private
	void setContentLength() {
		if (!didSetContentLength) {
			if (writer != null) {
				writer.flush();
			}
			super.setContentLength(noBody.getContentLength());
		}
	}

	// SERVLET RESPONSE interface methods

	@Override
	public void setContentLength(int len) {
		super.setContentLength(len);
		didSetContentLength = true;
	}

	@Override
	public void setHeader(String name, String value) {
		super.setHeader(name, value);
		checkHeader(name);
	}

	@Override
	public void addHeader(String name, String value) {
		super.addHeader(name, value);
		checkHeader(name);
	}

	@Override
	public void setIntHeader(String name, int value) {
		super.setIntHeader(name, value);
		checkHeader(name);
	}

	@Override
	public void addIntHeader(String name, int value) {
		super.addIntHeader(name, value);
		checkHeader(name);
	}

	private void checkHeader(String name) {
		if ("content-length".equalsIgnoreCase(name)) {
			didSetContentLength = true;
		}
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return noBody;
	}

	@Override
	public PrintWriter getWriter() throws UnsupportedEncodingException {

		if (writer == null) {
			OutputStreamWriter w;

			w = new OutputStreamWriter(noBody, getCharacterEncoding());
			writer = new PrintWriter(w);
		}
		return writer;
	}
}

/*
 * Servlet output stream that gobbles up all its data.
 */

// file private
class NoBodyOutputStream extends ServletOutputStream {

	private static final String LSTRING_FILE = "javax.servlet.http.LocalStrings";
	private static ResourceBundle lStrings = ResourceBundle.getBundle(LSTRING_FILE);

	private int contentLength = 0;

	// file private
	NoBodyOutputStream() {
		// NOOP
	}

	// file private
	int getContentLength() {
		return contentLength;
	}

	@Override
	public void write(int b) {
		contentLength++;
	}

	@Override
	public void write(byte buf[], int offset, int len) throws IOException {
		if (buf == null) {
			throw new NullPointerException(lStrings.getString("err.io.nullArray"));
		}

		if (offset < 0 || len < 0 || offset + len > buf.length) {
			String msg = lStrings.getString("err.io.indexOutOfBounds");
			Object[] msgArgs = new Object[3];
			msgArgs[0] = Integer.valueOf(offset);
			msgArgs[1] = Integer.valueOf(len);
			msgArgs[2] = Integer.valueOf(buf.length);
			msg = MessageFormat.format(msg, msgArgs);
			throw new IndexOutOfBoundsException(msg);
		}

		contentLength += len;
	}

}
