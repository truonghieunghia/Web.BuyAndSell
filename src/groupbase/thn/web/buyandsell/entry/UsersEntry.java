package groupbase.thn.web.buyandsell.entry;

import java.io.Serializable;

import groupbase.thn.web.libs.ColumnNameAnnotation;
import groupbase.thn.web.libs.EntryAnnotation;

@EntryAnnotation(TableName = "Users")
public class UsersEntry implements Serializable {

	private static final long serialVersionUID = 1L;
	public static String UserID_Field = "UserID";
	public static String Email_Field = "Email";
	public static String UserName_Field = "UserName";
	public static String FullName_Field = "FullName";
	public static String Sex_Field = "Sex";
	public static String Birthday_Field = "Birthday";
	public static String Password_Field = "Password";

	@ColumnNameAnnotation(ColumnName = "UserID", FieldType = Long.class, isPrimary = true, MaxLength = 19)
	private long UserID;

	@ColumnNameAnnotation(ColumnName = "Email", FieldType = String.class, MaxLength = 100)
	private String Email;

	@ColumnNameAnnotation(ColumnName = "UserName", FieldType = String.class, MaxLength = 45)
	private String UserName;

	@ColumnNameAnnotation(ColumnName = "FullName", FieldType = String.class, MaxLength = 45)
	private String FullName;

	@ColumnNameAnnotation(ColumnName = "Sex", FieldType = Integer.class, MaxLength = 1)
	private int Sex;

	@ColumnNameAnnotation(ColumnName = "Birthday", FieldType = String.class, MaxLength = 8)
	private String Birthday;

	@ColumnNameAnnotation(ColumnName = "Password", FieldType = String.class)
	private String Password;

	public long getUserID() {
		return UserID;
	}

	public void setUserID(long userID) {
		UserID = userID;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getFullName() {
		return FullName;
	}

	public void setFullName(String fullName) {
		FullName = fullName;
	}

	public int getSex() {
		return Sex;
	}

	public void setSex(int sex) {
		Sex = sex;
	}

	public String getBirthday() {
		return Birthday;
	}

	public void setBirthday(String birthday) {
		Birthday = birthday;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}

}
