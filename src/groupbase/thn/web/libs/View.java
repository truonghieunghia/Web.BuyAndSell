package groupbase.thn.web.libs;

public class View {

	private String mViewName;
	private ViewAction mViewAction;
	private boolean mIsFile = false;

	public View(String viewName,ViewAction viewAction) {
		mViewName = viewName;
		mViewAction = viewAction;
	}

	public View(String viewName) {
		mViewName = viewName;
		mViewAction = ViewAction.FORWARD;
	}
	public String getViewName() {
		return mViewName;
	}

	public View setIsFile(boolean isfile){
		mIsFile = isfile;
		return this;
	}
	
	public boolean isFile(){
		return mIsFile;
	}
	public ViewAction getViewAction (){
		return mViewAction;
	}
}
