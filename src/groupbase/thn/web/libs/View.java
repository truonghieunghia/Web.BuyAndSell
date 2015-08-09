package groupbase.thn.web.libs;

public class View {

	private String mViewName;
	private ViewAction mViewAction;

	public View(String viewName,ViewAction viewAction) {
		mViewName = viewName;
		mViewAction = viewAction;
	}


	public String getViewName() {
		return mViewName;
	}

	public ViewAction getViewAction (){
		return mViewAction;
	}
}
