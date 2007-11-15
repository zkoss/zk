public class Mail{
	String _sender, _email, _subject, _path;
	public Mail(String sender, String email, String subject, String path){
		_sender = sender;
		_email = email;
		_subject = subject;
		_path = path;
	}
	public void setSender(String sender){
		_sender = sender;
	}
	public String getSender(){
		return _sender;
	}
	public String getEmail(){
		return _email;
	}
	public void setEmail(String email){
		_email = email;
	}
	public String getSubject(){
		return _subject;
	}
	public void setSubject(String subject){
		_subject = subject;
	}
	public String getPath(){
		return _path;
	}
	public void setPath(String path){
		_path = path;
	}
}
